package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBD;
import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBDImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    private final static Logger LOG = Logger.getLogger(FileDAOImpl.class);
    private ConnectionBD baseConnectionBD;
    private VisitFolder visitFolder;

    public FileDAOImpl() {
        baseConnectionBD = new ConnectionBDImpl();
        visitFolder = new VisitFolderImpl();
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
        visitFolder.visit(defaultPath);
        List<FolderData> data = visitFolder.getData();
        for (FolderData d : data) {
            insertPrepare(d.getName(), d.getPath(), d.getType(), d.getSize());
        }
    }

    public void insertPrepare(final String name, final String path, final String type, final long size) {
        baseConnectionBD.withConnection(new ConnectionBDImpl.OnConnectionListener() {
            public void apply(ConnectionBD.StatementFactory sf) throws SQLException {
                PreparedStatement pst = sf.preparedStatement("INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)");
                pst.setString(1, name);
                pst.setString(2, path);
                pst.setString(3, type);
                pst.setLong(4, size);
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

    public void print(List<FolderData> data) {
        for (FolderData d : data) {
            LOG.info("id = " + d.getId());
            LOG.info("name = " + d.getName());
            LOG.info("path = " + d.getPath());
            LOG.info("type = " + d.getType());
            LOG.info("size = " + d.getSize() + "\n");
        }
    }

    public void getStatistic(String path) {
        create();
        insert(path);
        print(read());
        clean();
    }
}
