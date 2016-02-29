package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBD;
import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBDImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    private ConnectionBD baseConnectionBD;
    private final static int BATCH_SIZE = 50;

    public FileDAOImpl() {
        baseConnectionBD = new ConnectionBDImpl();
    }

    public void create() {
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                sf.statement().execute("CREATE TABLE if not exists 'directory' " +
                        "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
            }
        });
    }

    public void insert(final FolderData fd) {
        insert(Collections.singletonList(fd));
    }

    public void insert(final Collection<FolderData> collection) {
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                int count = 0;
                PreparedStatement pst = sf.preparedStatement("INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)");
                for (FolderData c : collection) {
                    pst.setString(1, c.getName());
                    pst.setString(2, c.getPath());
                    pst.setString(3, c.getType());
                    pst.setLong(4, c.getSize());
                    pst.addBatch();
                    if (++count % BATCH_SIZE == 0) {
                        pst.executeBatch();
                    }
                }
                pst.executeBatch();
            }
        });
    }

    public List<FolderData> read() {
        final List<FolderData> data = new ArrayList<>();
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                try (ResultSet set = sf.statement().executeQuery("SELECT * FROM directory")) {
                    while (set.next()) {
                        data.add(new FolderDataImpl(set.getString("name"), set.getString("path"), set.getString("type"), set.getLong("size"), set.getInt("id")));
                    }
                }
            }
        });
        return data;
    }

    public void clean() {
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                sf.statement().executeUpdate("DELETE FROM directory");
            }
        });
    }

    public ConnectionBD getBaseConnectionBD() {
        return baseConnectionBD;
    }
}
