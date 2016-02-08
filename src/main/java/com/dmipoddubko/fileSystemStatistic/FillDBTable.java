package com.dmipoddubko.fileSystemStatistic;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FillDBTable implements IFillDBTable {

    private final static Logger LOG = Logger.getLogger(FillDBTable.class);

    public Connection connection() {
        Connection connection;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("The database connection isn't established.", e);
        }
        return connection;
    }

    public void create() {
        String msg = "The table was created or already exists.";
        try {
            Statement stm = connection().createStatement();
            stm.execute("CREATE TABLE if not exists 'directory' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            msg = "The table hasn't been created.";
        } finally {
            LOG.info(msg);
        }
    }

    public void insert(String name, String path, String type, long size) {
        String msg = "Data has been inserted into the table";
        try {
            Statement stm = connection().createStatement();
            stm.execute("INSERT INTO 'directory' ('name', 'path', 'type', 'size') " +
                    "VALUES ('" + name + "','" + path + "','" + type + "','" + size + "'); ");
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
            msg = "Data has not been inserted into the table";
        } finally {
            LOG.info(msg);
        }
    }

    public void read() {
        try {
            Statement stm = connection().createStatement();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clean() {
        String msg = "The table wasn't cleaned.";
        try {
            Statement stm = connection().createStatement();
            int deletedRows = stm.executeUpdate("DELETE FROM directory");
            if (deletedRows > 0) {
                msg = "The table is cleared.";
            } else {
                msg = "The table was empty.";
            }
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            LOG.info(msg);
        }
    }

    public void visitFolder(String defaultPath) {
        try {
            Files.walkFileTree(Paths.get(defaultPath), new InsertFileVisitor());
        } catch (IOException exc) {
            System.out.println("I/O Error");
        }
    }

    public void close() {
        try {
            connection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOG.info("Connection closed.");
    }

    public class InsertFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attr) {
            if (attr.isRegularFile()) {
                insert(file.toFile().getName(), file.toFile().getPath(), "file", file.toFile().length());
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir,
                                                  IOException exc) {
            insert(dir.toFile().getName(), dir.toFile().getPath(), "folder", FileUtils.sizeOfDirectory(dir.toFile()));
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            System.err.println(exc);
            return CONTINUE;
        }
    }
}
