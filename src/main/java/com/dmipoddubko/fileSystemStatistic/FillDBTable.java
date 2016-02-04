package com.dmipoddubko.fileSystemStatistic;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FillDBTable {
    public java.sql.Connection connection;
    public Statement stm;
    public ResultSet set;
    private final static Logger LOG = Logger.getLogger(FillDBTable.class);

    public void conn() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        LOG.info("База Подключена!");
    }

    public void createTable() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        stm.execute("CREATE TABLE if not exists 'directory' " +
                "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
        LOG.info("Таблица создана или уже существует.");
    }

    public void insertDir(String dir, String path, String type, long size) throws SQLException {
        stm = connection.createStatement();
        stm.execute("INSERT INTO 'directory' ('name', 'path', 'type', 'size') " +
                "VALUES ('" + dir + "','" + path + "','" + type + "','" + size + "'); ");
    }

    public void readTable() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        set = stm.executeQuery("SELECT * FROM directory");

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

        LOG.info("Данные таблицы распечатаны.");
    }

    public void deleteTable() throws ClassNotFoundException, SQLException {
        stm = connection.createStatement();
        stm = connection.createStatement();
        String query = "DELETE FROM directory";
        int deletedRows = stm.executeUpdate(query);
        if (deletedRows > 0) {
            LOG.info("Таблица очищена.");
        } else {
            LOG.info("Таблица была пустой.");
        }
    }

    public void visitDir(String defaultPath) throws SQLException {
        File folder = new File(defaultPath);
        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles)
                setFolder(file);
        } else if (folder.isFile()) {
            setFolder(folder);
        }
    }

    public void setFolder(File file) throws SQLException {
        String name = file.getName();
        String path = file.getPath();
        String type;
        long size;
        if (file.isFile()) {
            type = "file";
            size = file.length();
            insertDir(name, path, type, size);
        } else if (file.isDirectory()) {
            type = "folder";
            size = folderSize(file);
            insertDir(name, path, type, size);
            visitDir(path);
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

    public void CloseDB() throws ClassNotFoundException, SQLException {
        stm.close();
        set.close();
        connection.close();

        LOG.info("Соединение закрыто");
    }
}
