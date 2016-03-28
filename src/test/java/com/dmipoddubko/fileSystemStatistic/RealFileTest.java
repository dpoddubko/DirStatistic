package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.dmipoddubko.fileSystemStatistic.DirTest.getJdbcTemplate;
import static org.testng.AssertJUnit.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RealFileTest {
    private static String rootPath = "C:\\TestFolder";
    private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
    private FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");

    @BeforeClass
    public static void setUp() throws InterruptedException, ExecutionException {
        DirDataImpl dirData = new DirDataImpl();
        DirDataImpl.setNumber(2);
        dirData.createDir(dirData.buildPath(rootPath, 30));
        dirData.writeFiles(rootPath, 30);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException, ExecutionException, IOException {
        FileUtils.deleteDirectory(new File(rootPath));
    }

    @Test
    public void a_fileExistTest() throws IOException {
        long countDir = Files.find(
                Paths.get(rootPath), 35,
                (path, attributes) -> attributes.isDirectory()
        ).count();
        assertEquals(30, countDir);
        long countFiles = Files.find(
                Paths.get(rootPath), 35,
                (path, attributes) -> attributes.isRegularFile()
        ).count();
        assertEquals(150, countFiles);
    }

    @Test
    public void b_visitFolderImplTest() {
        VisitFolder visitFolder = (VisitFolderImpl) context.getBean("visitFolder");
        List<FolderData> data = visitFolder.visit(rootPath);
        assertEquals(180, data.size());
    }

    @Test
    public void c_createTest() {
        fileDAO.create();
        JdbcTemplate jdbcTemplate = getJdbcTemplate();
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='directory'";
        List<String> strLst = jdbcTemplate.query(sql, new DirTest.StrMapper());
        assertEquals("directory", strLst.get(0));
    }

    @Test
    public void d_insertTest() {
        VisitFolder visitFolder = (VisitFolderImpl) context.getBean("visitFolder");
        List<FolderData> data = visitFolder.visit(rootPath);
        fileDAO.insert(data);
        assertEquals(180, DirTest.count());
    }

    @Test
    public void e_readTest() {
        assertEquals(180, fileDAO.read().size());
    }

    @Test
    public void f_cleanTest() {
        fileDAO.clean();
        assertEquals(0, DirTest.count());
    }
}
