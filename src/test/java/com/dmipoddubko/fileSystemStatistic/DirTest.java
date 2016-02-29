package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBD;
import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBDImpl;
import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DirTest {
    private static String rootPath = "C:\\TestFolder1";
    private final static int THREADS = 5;
    private final static Logger LOG = Logger.getLogger(DirTest.class);
    private static ExecutorService pool = Executors.newFixedThreadPool(THREADS);
    private static FileSystemServiceImpl systemService = new FileSystemServiceImpl();
    private ConnectionBD connectionBD = new ConnectionBDImpl();

    @BeforeClass
    public static void setUp() throws InterruptedException {
        DirDataImpl dirData = new DirDataImpl();
        String path = dirData.buildPath(rootPath, 30);
        dirData.createDir(path);
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);

        List<Callable<Object>> tasks = new ArrayList<>();
        for (String p : paths) {
            tasks.add(new Callable<Object>() {
                public Object call() throws Exception {
                    dirData.createFiles(p);
                    return null;
                }
            });
        }
        pool.invokeAll(tasks);
        systemService.getFileDAO().create();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        DirDataImpl dirData = new DirDataImpl();
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();
        try {
            for (String p : paths) {
                tasks.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        dirData.delDir(p);
                        return null;
                    }
                });
            }
            pool.invokeAll(tasks);
        } finally {
            pool.shutdown();
            while (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
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
    public void statementFactoryTest() throws SQLException {
        ConnectionBDImpl.StatementFactoryImpl sf = new ConnectionBDImpl.StatementFactoryImpl(ConnectionBDImpl.connection());
        assertTrue(sf.statement() instanceof Statement);
        assertTrue(sf.preparedStatement("INSERT INTO 'directory' ('name') VALUES (?)") instanceof PreparedStatement);
        sf.close();
    }

    @Test
    public void tableExistTest() {
        connectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                try (ResultSet set = sf.statement().executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='directory';")) {
                    assertEquals("directory", set.getString("name"));
                }
            }
        });
    }

    @Test
    public void fileSystemServiceTest() {
        systemService.index(rootPath);
        countTest(499980);
    }

    @Test
    public void fileDAOReadTest() {
        assertEquals(499980, systemService.getFileDAO().read().size());
    }

    @Test
    public void fileDAOCleanTest() {
        systemService.getFileDAO().clean();
        countTest(0);
    }

    public void countTest(int num) {
        connectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                try (ResultSet set = sf.statement().executeQuery("SELECT COUNT(*) FROM 'directory';")) {
                    assertEquals(num, set.getInt(1));
                }
            }
        });
    }
}
