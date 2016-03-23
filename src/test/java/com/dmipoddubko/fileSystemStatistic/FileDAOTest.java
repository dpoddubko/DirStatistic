package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
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
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("name")).thenReturn("testFile1.txt");
        when(rs.getString("path")).thenReturn("C:\\TestDirectory");
        when(rs.getString("type")).thenReturn("file");
        when(rs.getLong("size")).thenReturn((long) 123);
        FolderData fd = mapper.mapRow(rs, 1);
        assertNotNull(fd);
        assertEquals(fd.getId(), rs.getInt("id"));
        assertEquals(fd.getName(), rs.getString("name"));
        assertEquals(fd.getPath(), rs.getString("path"));
        assertEquals(fd.getType(), rs.getString("type"));
        assertEquals(fd.getSize(), rs.getLong("size"));
    }

    @Test
    public void createTest() {
        FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        fileDAO.setJdbcTemplate(jdbcTemplate);
        fileDAO.create();
        verify(jdbcTemplate, times(1)).execute(eq("CREATE TABLE if not exists 'directory' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);"));
    }

    @Test
    public void cleanTest() {
        FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        fileDAO.setJdbcTemplate(jdbcTemplate);
        fileDAO.clean();
        verify(jdbcTemplate, times(1)).update(eq("DELETE FROM directory"));
    }

    @Test
    public void insertTest() {
        FileDAOImpl fileDAO = (FileDAOImpl) context.getBean("fileDAO");
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        fileDAO.setJdbcTemplate(jdbcTemplate);
        List<FolderData> list = new ArrayList<>();
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500003));
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500004));
        FolderData fd = mock(FolderData.class);
        String SQL = "INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)";
        fileDAO.insert(fd);
        fileDAO.insert(list);
        verify(jdbcTemplate, times(2)).batchUpdate(eq(SQL), any(BatchPreparedStatementSetter.class));
    }
}

