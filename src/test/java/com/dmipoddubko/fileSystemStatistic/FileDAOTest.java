package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.*;

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
}
