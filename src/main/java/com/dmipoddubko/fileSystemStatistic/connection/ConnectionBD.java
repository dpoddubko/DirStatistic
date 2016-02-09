package com.dmipoddubko.fileSystemStatistic.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface ConnectionBD {
    Connection connection();

    void close(Statement stm, Connection connection);

    void closeRSet(ResultSet set);
}
