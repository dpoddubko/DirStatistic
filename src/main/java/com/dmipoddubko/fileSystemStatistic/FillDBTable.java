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
        Statement stm = null;
        try {
            stm = connection().createStatement();
            stm.execute("CREATE TABLE if not exists 'directory' " +
                    "('id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " 'name' varchar(50), 'path' text, 'type' varchar(10), 'size' bigint);");
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.info("The table hasn't been created.");
        }finally {
            try {
                stm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        LOG.info("The table was created or already exists.");
    }

    public void insert(String name, String path, String type, long size) {
        PreparedStatement pst = null;
        try {
            pst = connection().prepareStatement("INSERT INTO 'directory' ('name', 'path', 'type', 'size') VALUES (?, ?, ?, ?)");
            pst.setString(1, name);
            pst.setString(2, path);
            pst.setString(3, type);
            pst.setLong(4, size);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            LOG.info("Data hasn't been inserted into the table");
        } finally {
            try {
                pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
        try {
            Statement stm = connection().createStatement();
            int deletedRows = stm.executeUpdate("DELETE FROM directory");
            if (deletedRows > 0) {
                LOG.info("The table is cleared.");
            } else {
                LOG.info("The table was empty.");
            }
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
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
            LOG.error(exc);
            return CONTINUE;
        }
    }
}
