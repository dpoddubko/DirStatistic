package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;


public class DirTest {
    private static String rootPath = "C:\\TestFolder";
    private final static int THREADS = 5;

    @BeforeClass
    public static void createDirTest() {
        DirDataImpl dirData = new DirDataImpl();
        String path = dirData.buildPath(rootPath, 30);
        dirData.createDir(path);
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        List<Callable<Object>> tasks = new ArrayList<>();
        try {
            for (String p : paths) {
                tasks.add(new Callable<Object>() {
                    public Object call() throws Exception {
                        dirData.createFiles(p);
                        return null;
                    }
                });
            }
            pool.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException("Some error with creating files", e);
        } finally {
            pool.shutdown();
        }
    }


    @AfterClass
    public static void deleteDirTest() {
        DirDataImpl dirData = new DirDataImpl();
        List<String> paths = dirData.dividePath(rootPath, 30, THREADS);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
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
        } catch (InterruptedException e) {
            throw new RuntimeException("Some error with creating files", e);
        } finally {
            pool.shutdown();
        }
    }

    @Test
    public void fileExistTest() {
        try {
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
        } catch (IOException e) {
            throw new RuntimeException("Some error with counting sub-folders.", e);
        }
    }

    @Test
    public void tableTest() {
        FileSystemServiceImpl systemService = new FileSystemServiceImpl();
        systemService.getFileDAO().create();
        systemService.index(rootPath);
        systemService.getFileDAO().count();
        assertEquals(499980, systemService.getFileDAO().count());
        systemService.getFileDAO().clean();
        assertEquals(0, systemService.getFileDAO().count());
    }
}
