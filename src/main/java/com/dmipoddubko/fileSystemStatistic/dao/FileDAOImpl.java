package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBD;
import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBDImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    private ConnectionBD baseConnectionBD;

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

    public void insert(String defaultPath) {
        VisitFolder visitFolder = new VisitFolderImpl();
        visitFolder.visit(defaultPath);
        List<FolderData> data = visitFolder.getData();
        for (FolderData d : data) {
            insertPrepare(d);
        }
    }

    public void insertPrepare(final FolderData fd) {
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                PreparedStatement pst = sf.preparedStatement("INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)");
                pst.setString(1, fd.getName());
                pst.setString(2, fd.getPath());
                pst.setString(3, fd.getType());
                pst.setLong(4, fd.getSize());
                pst.executeUpdate();
            }
        });
    }

    public List<FolderData> read() {
        final List<FolderData> data = new ArrayList<>();
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                ResultSet set = sf.statement().executeQuery("SELECT * FROM directory");
                while (set.next()) {
                    data.add(new FolderDataImpl(set.getString("name"), set.getString("path"), set.getString("type"), set.getLong("size"), set.getInt("id")));
                }
                set.close();
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
}
