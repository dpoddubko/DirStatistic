package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.SQLException;
import java.sql.Statement;

public interface ConnectionBD {
    void withConnection(OnConnectionListener onConnection);

    interface OnConnectionListener {
        void apply(Statement stm) throws SQLException;
    }
}
