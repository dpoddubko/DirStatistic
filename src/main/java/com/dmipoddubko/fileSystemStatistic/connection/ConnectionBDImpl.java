package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionBDImpl implements ConnectionBD {
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found.", e);
        }
    }

    public Connection connection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:folder.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException("The database connection isn't established.", e);
        }
    }

    public void withConnection(OnConnectionListener onConnectionListener) {
        StatementFactory sf = new StatementFactoryImpl(connection());
        try {
            onConnectionListener.apply(sf);
        } catch (SQLException e) {
            throw new RuntimeException("Some error with database connection.", e);
        } finally {
            try {
                sf.close();
            } catch (SQLException e) {
                throw new RuntimeException("Some error with database connection.", e);
            }
        }
    }

    public static class StatementFactoryImpl implements StatementFactory {
        private Connection connection;
        private List<Statement> list = new ArrayList<>();

        public StatementFactoryImpl(Connection connection) {
            this.connection = connection;
        }

        public Statement statement() throws SQLException {
            Statement stm = connection.createStatement();
            list.add(stm);
            return stm;
        }

        public PreparedStatement preparedStatement(String s) throws SQLException {
            PreparedStatement pst = connection.prepareStatement(s);
            list.add(pst);
            return pst;
        }

        public void close() {
            try {
                for (Statement s : list) {
                    s.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Some error with database connection.", e);
                }
            }
        }
    }
}
