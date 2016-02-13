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
public void withConnection(){

}
    public Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException("The database connection isn't established.", e);
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

    public void closeRSet(ResultSet set) {
        try {
            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
