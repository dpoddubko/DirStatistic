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
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
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
    public void insert(List<FolderData> list) {
        String SQL = "INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)";
            jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    FolderData l = list.get(i);
                    ps.setString(1, l.getName());
                    ps.setString(2, l.getPath());
                    ps.setString(3, l.getType());
                    ps.setLong(4, l.getSize());
                }
                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });

    }

    @Override
    public List<FolderData> read() {
        return jdbcTemplate.query("SELECT * FROM directory", new FolderDataMapper());
    }

    @Override
    public void clean() {
        jdbcTemplate.update("DELETE FROM directory");
    }

    public class FolderDataMapper implements RowMapper<FolderData> {
        public FolderData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FolderDataImpl(rs.getString("name"), rs.getString("path"), rs.getString("type"), rs.getLong("size"), rs.getInt("id"));
        }
    }
}
