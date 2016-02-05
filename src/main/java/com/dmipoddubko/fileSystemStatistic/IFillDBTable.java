package com.dmipoddubko.fileSystemStatistic;

import java.sql.Connection;

public interface IFillDBTable {
    Connection connection();
    void create();
    void insert(String name, String path, String type, long size);
    void read();
    void clean();
    void close();
    void visitFolder(String defaultPath);
}
