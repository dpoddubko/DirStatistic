package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.*;

public class ConnectionBDImpl implements ConnectionBD {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found.", e);
        }
    }

    public void withConnection(OnConnectionListener onConnectionListener) {
        Connection connection = connection();
        Statement stm = null;
        try {
            stm = connection.createStatement();
            onConnectionListener.apply(stm);
        } catch (SQLException e) {
            throw new RuntimeException("Some error with database connection.", e);
        } finally {
            try {
                stm.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Some error with database connection.", e);
            }
        }
    }

    public Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException("The database connection isn't established.", e);
        }
    }
}
