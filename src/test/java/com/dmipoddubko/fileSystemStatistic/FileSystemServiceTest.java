package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileSystemServiceTest {

    private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

    @Test
    public void fileSystemServiceTest() {
        PropertyConfigurator.configure("log4j.properties");
        int num = DirTest.count();
        List<FolderData> list = new ArrayList<>();
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500001));
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500002));
        VisitFolder visitFolder = mock(VisitFolder.class);
        when(visitFolder.visit("path")).thenReturn(list);
        FileSystemServiceImpl service = (FileSystemServiceImpl) context.getBean("fileSystemService");
        service.setVisitFolderImpl(visitFolder);
        service.index("path");
        assertEquals(num + 2, DirTest.count());
    }

    @Test
    public void readTest() throws SQLException {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        jdbcTemplate.query("SELECT * FROM directory", new FileDAOImpl.FolderDataMapper());

        ResultSet rs = mock(ResultSet.class);
        Mockito.when(rs.next()).thenReturn(true, true, false);
        Mockito.when(rs.getString("name")).thenReturn("testFile1.txt").thenReturn("testFile2.txt");
        Mockito.when(rs.getString("path")).thenReturn("C:\\TestDirectory").thenReturn("C:\\TestDirectory");
        Mockito.when(rs.getString("type")).thenReturn("file").thenReturn("file");
        Mockito.when(rs.getLong("size")).thenReturn((long) 123).thenReturn((long) 456);
        Mockito.when(rs.getInt("id")).thenReturn(1, 2);

        assertTrue(rs.next());
        assertEquals(1, rs.getInt("id"));
        assertEquals("testFile1.txt", rs.getString("name"));
        assertEquals("C:\\TestDirectory", rs.getString("path"));
        assertEquals("file", rs.getString("type"));
        assertEquals(123, rs.getLong("size"));

        assertTrue(rs.next());
        assertEquals(2, rs.getInt("id"));
        assertEquals("testFile2.txt", rs.getString("name"));
        assertEquals("C:\\TestDirectory", rs.getString("path"));
        assertEquals("file", rs.getString("type"));
        assertEquals(456, rs.getLong("size"));
        assertFalse(rs.next());

        FileDAOImpl.FolderDataMapper mapper = new FileDAOImpl.FolderDataMapper();
        mapper.mapRow(rs, 0);
    }
}
