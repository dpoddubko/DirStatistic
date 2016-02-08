package com.dmipoddubko.fileSystemStatistic;

import java.sql.Connection;
import java.sql.Statement;

public interface IFillDBTable {
    Connection connection();
    void create();
    void insert(String name, String path, String type, long size);
    void read();
    void clean();
    void close(Statement stm);
    void visitFolder(String defaultPath);
}
