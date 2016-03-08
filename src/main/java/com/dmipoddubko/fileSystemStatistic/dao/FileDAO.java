package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public interface FileDAO {

    void create();

    void insert(FolderData fd);

    void insert(List<FolderData> collection);

    List<FolderData> read();

    void clean();

    JdbcTemplate getJdbcTemplate();

    void setDataSource(DataSource dataSource);

}

