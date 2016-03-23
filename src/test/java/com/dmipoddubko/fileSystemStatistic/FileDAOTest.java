package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class FileDAOTest {
    private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

    @Test
    public void readTest() {
        FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        fileDAO.setJdbcTemplate(jdbcTemplate);
        fileDAO.read();
        verify(jdbcTemplate, times(1)).query(eq("SELECT * FROM directory"), any(FileDAOImpl.FolderDataMapper.class));
    }

    @Test
    public void folderDataMapperTest() throws SQLException {
        FileDAOImpl.FolderDataMapper mapper = new FileDAOImpl.FolderDataMapper();
        ResultSet rs = mock(ResultSet.class);
        Mockito.when(rs.getInt("id")).thenReturn(1);
        Mockito.when(rs.getString("name")).thenReturn("testFile1.txt");
        Mockito.when(rs.getString("path")).thenReturn("C:\\TestDirectory");
        Mockito.when(rs.getString("type")).thenReturn("file");
        Mockito.when(rs.getLong("size")).thenReturn((long) 123);
        FolderData fd = mapper.mapRow(rs, 1);
        assertNotNull(fd);
        assertTrue(fd instanceof FolderData);
        assertEquals(fd.getId(), rs.getInt("id"));
        assertEquals(fd.getName(), rs.getString("name"));
        assertEquals(fd.getPath(), rs.getString("path"));
        assertEquals(fd.getType(), rs.getString("type"));
        assertEquals(fd.getSize(), rs.getLong("size"));
    }
}
