package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class DirTest {

    private static String rootPath = "C:\\TestDirectory";
    private final static int THREADS = 5;
    private final static Logger LOG = Logger.getLogger(DirTest.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(THREADS);
    private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    private FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");


    @BeforeClass
    public static void setUp() throws InterruptedException, ExecutionException {
        DirDataImpl dirData = new DirDataImpl();
        DirDataImpl.setNumber(3334);
        String path = dirData.buildPath(rootPath, 30);
        dirData.createDir(path);
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        List<Future<String>> list = new ArrayList<>();
        for (String p : paths) {
            Future<String> submit = executor.submit(() -> {
                dirData.createFiles(p, 6);
                return "It done!";
            });
            list.add(submit);
        }
        for (Future<String> future : list) {
            future.get();
        }
    }

    @AfterClass
    public static void tearDown() throws InterruptedException, ExecutionException, IOException {
        DirDataImpl dirData = new DirDataImpl();
        DirDataImpl.setNumber(3334);
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        List<Future<String>> list = new ArrayList<>();
        try {
            for (String p : paths) {
                Future<String> submit = executor.submit(() -> {
                    dirData.delDir(p, 6);
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
        FileUtils.deleteDirectory(new File(rootPath));
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
        VisitFolder visitFolder = (VisitFolderImpl) context.getBean("visitFolder");
        List<String> paths = DirDataImpl.dividePath(rootPath, 30, THREADS);
        List<FolderData> data = visitFolder.visit(paths.get(4));
        assertEquals(99996, data.size());
    }

    @Test
    public void createTest() {
        fileDAO.create();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='directory'";
        List<String> strLst = jdbcTemplate.query(sql, new StrMapper());
        assertEquals("directory", strLst.get(0));
    }

    @Test
    public void insertTest() {
        VisitFolder visitFolder = (VisitFolderImpl) context.getBean("visitFolder");
        List<String> paths = DirDataImpl.dividePath(rootPath, 30, THREADS);
        List<FolderData> data = visitFolder.visit(paths.get(4));
        System.out.println(paths.get(4));
        fileDAO.insert(data);
        assertEquals(99996, count());
    }

    @Test
    public void cleanTest() {
        fileDAO.clean();
        assertEquals(0, count());
    }

    public static class StrMapper implements RowMapper<String> {
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString(1);
        }
    }

    public static class IntMapper implements RowMapper<Integer> {
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt(1);
        }
    }

    public static int count() {
        PropertyConfigurator.configure("log4j.properties");
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String sql = "SELECT COUNT(*) FROM 'directory';";
        List<Integer> intLst = jdbcTemplate.query(sql, new IntMapper());
        return (int) intLst.get(0);
    }

    public static JdbcTemplate getJdbcTemplate() {
        SingleConnectionDataSource ds = new SingleConnectionDataSource();
        ds.setDriverClassName("org.sqlite.JDBC");
        ds.setUrl("jdbc:sqlite:database.sqlite");
        return new JdbcTemplate(ds);
    }
}
