package com.dmipoddubko.fileSystemStatistic;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;

import static java.nio.file.FileVisitResult.CONTINUE;

public class FillDBTable implements IFillDBTable {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found.", e);
        }
    }

    private final static Logger LOG = Logger.getLogger(FillDBTable.class);

    public Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException("The database connection isn't established.", e);
        }
    }

    public void create() {
        Statement stm = null;
        Connection connection = connection();
        try {
            stm = connection.createStatement();
            stm.execute("CREATE TABLE if not exists 'directory' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
        } catch (SQLException e) {
            throw new RuntimeException("The table hasn't been created.", e);
        } finally {
            close(stm, connection);
        }
        LOG.info("The table was created or already exists.");
    }

    public void insert(String name, String path, String type, long size) {
        PreparedStatement pst = null;
        Connection connection = connection();
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
            close(pst, connection);
        }
    }

    public void read() {
        Statement stm = null;
        Connection connection = connection();
        ResultSet set = null;
        try {
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
            LOG.info("The table printed.");
        } catch (SQLException e) {
            throw new RuntimeException("The table can't be printed", e);
        } finally {
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            close(stm, connection);
        }
    }

    public void clean() {
        Statement stm = null;
        Connection connection = connection();
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
            close(stm, connection);
        }
    }

    public void visitFolder(String defaultPath) {
        try {
            Files.walkFileTree(Paths.get(defaultPath), new InsertFileVisitor());
        } catch (IOException exc) {
            LOG.error("I/O Error");
        }
    }

    public void close(Statement stm, Connection connection) {
        try {
            stm.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
            LOG.error(exc);
            return CONTINUE;
        }
    }
}
