package com.dmipoddubko.fileSystemStatistic;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.*;

public class FillDBTable {
    private Connection connection;
    private Statement stm;

    private final static Logger LOG = Logger.getLogger(FillDBTable.class);

    public void connection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        LOG.info("The database is connected.");
    }

    public void createTab() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        stm.execute("CREATE TABLE if not exists 'directory' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
        LOG.info("The table was created or already exists.");
        stm.close();
    }

    public void insertIntoTab(String name, String path, String type, long size) throws SQLException {
        stm = connection.createStatement();
        stm.execute("INSERT INTO 'directory' ('name', 'path', 'type', 'size') " +
                "VALUES ('" + name + "','" + path + "','" + type + "','" + size + "'); ");
        stm.close();
    }

    public void readTab() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        ResultSet set = stm.executeQuery("SELECT * FROM directory");
        while (set.next()) {
            int id = set.getInt("id");
            String name = set.getString("name");
            String path = set.getString("path");
            String type = set.getString("type");
            String size = set.getString("size");
            LOG.info("ID = " + id);
            LOG.info("name = " + name);
            LOG.info("path = " + path);
            LOG.info("type = " + type);
            LOG.info("size = " + size + "\n");
        }
        LOG.info("The table printed.");
        set.close();
        stm.close();
    }

    public void deleteTab() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        int deletedRows = stm.executeUpdate("DELETE FROM directory");
        if (deletedRows > 0) {
            LOG.info("The table is cleared.");
        } else {
            LOG.info("The table was empty.");
        }
        stm.close();
    }

    public void visitFolder(String defaultPath) throws SQLException {
        File folder = new File(defaultPath);
        File[] listOfFiles = folder.listFiles();
        if (folder.isDirectory())
            for (File file : listOfFiles) insertFolder(file);
        else LOG.info("Please enter a valid folder.");

    }

    public void insertFolder(File file) throws SQLException {
        String name = file.getName();
        String path = file.getPath();
        if (file.isFile()) insertIntoTab(name, path, "file", file.length());
        else if (file.isDirectory()) {
            insertIntoTab(name, path, "folder", folderSize(file));
            visitFolder(path);
        }
    }

    public long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void closeDB() throws ClassNotFoundException, SQLException {
        connection.close();
        LOG.info("Connection closed.");
    }
}
