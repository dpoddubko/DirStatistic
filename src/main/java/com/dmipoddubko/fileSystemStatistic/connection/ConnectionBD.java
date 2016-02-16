package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface ConnectionBD {
    Connection connection();

    void withConnection(OnConnectionListener onConnection);

    interface OnConnectionListener {
        void apply(Statement stm) throws SQLException;
    }
}
