package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionBD {
    Connection connection();
    void withConnection(OnConnectionListener onConnection);

    interface OnConnectionListener {
        void apply(Connection connection) throws SQLException;


    }
}
