package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBDImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileDAOImpl implements FileDAO {

    private final static Logger LOG = Logger.getLogger(FileDAOImpl.class);
    ConnectionBDImpl baseConnectionBD;
    VisitFolderImpl visitFolder;

    public FileDAOImpl() {
        baseConnectionBD = new ConnectionBDImpl();
        visitFolder = new VisitFolderImpl();
    }

    public void create() {
        Statement stm = null;
        Connection connection = baseConnectionBD.connection();
        try {
            stm = connection.createStatement();
            stm.execute("CREATE TABLE if not exists 'directory' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
        } catch (SQLException e) {
            throw new RuntimeException("The table hasn't been created.", e);
        } finally {
            baseConnectionBD.close(stm, connection);
        }
        LOG.info("The table was created or already exists.");
    }

    public void insert(String defaultPath) {
        visitFolder.visit(defaultPath);
        List<FolderDataImpl> data = visitFolder.getData();
        for (FolderDataImpl d : data) {
            insertPrepare(d.getName(), d.getPath(), d.getType(), d.getSize());
        }
    }

    public void insertPrepare(String name, String path, String type, long size) {
        Connection connection = baseConnectionBD.connection();
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setString(2, path);
            pst.setString(3, type);
            pst.setLong(4, size);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Data hasn't been inserted into the table", e);
        } finally {
            baseConnectionBD.close(pst, connection);
        }
    }

    public List<FolderData> read() {
        List<FolderData> data = new ArrayList<>();
        Connection connection = baseConnectionBD.connection();
        Statement stm = null;
        ResultSet set = null;
        try {
            stm = connection.createStatement();
            set = stm.executeQuery("SELECT * FROM directory");
            while (set.next()) {
                data.add(new FolderDataImpl(set.getString("name"), set.getString("path"), set.getString("type"), set.getLong("size"),set.getInt("id")));
            }
            LOG.info("The table read.");
        } catch (SQLException e) {
            throw new RuntimeException("The table can't be printed", e);
        } finally {
            baseConnectionBD.closeRSet(set);
            baseConnectionBD.close(stm, connection);
        }
        return data;
    }

    public void clean() {
        Statement stm = null;
        Connection connection = baseConnectionBD.connection();
        try {
            stm = connection.createStatement();
            int deletedRows = stm.executeUpdate("DELETE FROM directory");
            if (deletedRows > 0) {
                LOG.info("The table is cleaned.");
            } else {
                LOG.info("The table was empty.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("The table can't be cleaned", e);
        } finally {
            baseConnectionBD.close(stm, connection);
        }
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
    public void getStatistic(String path){
        create();
        insert(path);
        print(read());
        clean();
    }
}
