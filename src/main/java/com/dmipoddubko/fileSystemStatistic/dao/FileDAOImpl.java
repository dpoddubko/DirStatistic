package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class FileDAOImpl implements FileDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private final static int BATCH_SIZE = 50;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create() {
        jdbcTemplate.execute("CREATE TABLE if not exists 'directory' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
    }

    @Override
    public void insert(final FolderData fd) {
        insert(Collections.singletonList(fd));
    }

    @Override
    public void insert(List<FolderData> collection) {
        String SQL = "INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)";
        for (int j = 0; j < collection.size(); j += BATCH_SIZE) {
            final List<FolderData> batchList = collection.subList(j, j + BATCH_SIZE > collection.size() ? collection.size() : j + BATCH_SIZE);
            jdbcTemplate.batchUpdate(SQL,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i)
                                throws SQLException {
                            FolderData folderData = batchList.get(i);
                            ps.setString(1, folderData.getName());
                            ps.setString(2, folderData.getPath());
                            ps.setString(3, folderData.getType());
                            ps.setString(4, String.valueOf(folderData.getSize()));
                        }

                        @Override
                        public int getBatchSize() {
                            return batchList.size();
                        }
                    });
        }
    }

    @Override
    public List<FolderData> read() {
        List<FolderData> data = jdbcTemplate.query("SELECT * FROM directory",
                new FolderDataMapper());
        return data;
    }

    @Override
    public void clean() {
        jdbcTemplate.update("DELETE FROM directory");
    }

    public class FolderDataMapper implements RowMapper<FolderData> {
        public FolderData mapRow(ResultSet rs, int rowNum) throws SQLException {
            FolderData data = new FolderDataImpl(rs.getString("name"), rs.getString("path"), rs.getString("type"), rs.getLong("size"), rs.getInt("id"));
            return data;
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
