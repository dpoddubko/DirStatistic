package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public interface ConnectionBD {
    void withConnection(OnConnectionListener onConnection);

    interface OnConnectionListener {
        void apply(StatementFactory sf) throws SQLException;
    }

    interface StatementFactory {
        Statement statement() throws SQLException;

        PreparedStatement preparedStatement(String s) throws SQLException;

        void close() throws SQLException;
    }
}
