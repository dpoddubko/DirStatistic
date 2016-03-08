package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;

public class DirTest {

    private static String rootPath = "C:\\TestDirectory";

    private final static int THREADS = 5;
    private final static Logger LOG = Logger.getLogger(DirTest.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    private static FileSystemServiceImpl systemService = new FileSystemServiceImpl();


    @BeforeTest
    public static void setUp() throws InterruptedException, ExecutionException {
        DirDataImpl dirData = new DirDataImpl();
        String path = dirData.buildPath(rootPath, 30);
        dirData.createDir(path);
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        List<Future<String>> list = new ArrayList<>();
        for (String p : paths) {
            Future<String> submit = executor.submit(() -> {
                dirData.createFiles(p);
                return "It done!";
            });
            list.add(submit);
        }
        for (Future<String> future : list) {
            future.get();
        }
        systemService.getFileDAO().create();
    }

    @AfterTest
    public static void tearDown() throws InterruptedException, ExecutionException {
        PropertyConfigurator.configure("log4j.properties");
        DirDataImpl dirData = new DirDataImpl();
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        List<Future<String>> list = new ArrayList<>();
        try {
            for (String p : paths) {
                Future<String> submit = executor.submit(() -> {
                    dirData.delDir(p);
                    return "It done!";
                });
                list.add(submit);
            }
            for (Future<String> future : list) {
                future.get();
            }
        } finally {
            executor.shutdown();
            while (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                LOG.info("Awaiting completion of threads.");
            }
        }
    }

    @Test
    public void fileExistTest() throws IOException {
        long countDir = Files.find(
                Paths.get(rootPath), 35,
                (path, attributes) -> attributes.isDirectory()
        ).count();
        assertEquals(30, countDir);
        long countFiles = Files.find(
                Paths.get(rootPath), 35,
                (path, attributes) -> attributes.isRegularFile()
        ).count();
        assertEquals(499950, countFiles);
    }

    @Test
    public void folderDataTest() {
        String name = "some_file.txt";
        String path = "C:\\SomeFolder";
        String type = "file";
        long size = 55;
        int id = 1;
        FolderData folderData = new FolderDataImpl(name, path, type, size, id);
        assertEquals(name, folderData.getName());
        assertEquals(path, folderData.getPath());
        assertEquals(type, folderData.getType());
        assertEquals(size, folderData.getSize());
        assertEquals(id, folderData.getId());
    }

    @Test
    public void visitFolderImplTest() {
        VisitFolder visitFolder = new VisitFolderImpl();
        List<String> paths = DirDataImpl.dividePath(rootPath, 30, THREADS);
        List<FolderData> data = visitFolder.visit(paths.get(4));
        assertEquals(99996, data.size());
    }


    @Test
    public void tableExistTest() {
        PropertyConfigurator.configure("log4j.properties");
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='directory'";
        List<String> strLst = jdbcTemplate.query(sql, new StrMapper());
        assertEquals("directory", strLst.get(0));
    }

    @Test
    public void countTest() {
        PropertyConfigurator.configure("log4j.properties");
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String sql = "SELECT COUNT(*) FROM 'directory';";
        List<Integer> intLst = jdbcTemplate.query(sql, new IntMapper());
        assertEquals(0, (int) intLst.get(0));
    }

    class StrMapper implements RowMapper<String> {
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
    }

    class IntMapper implements RowMapper<Integer> {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:folder.sqlite");
        return new JdbcTemplate(ds);
    }
}
