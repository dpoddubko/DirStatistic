package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;

import java.util.List;

public interface FileDAO {
    void create();
    void insert(String defaultPath);
    void insertPrepare(String name, String path, String type, long size);
    List<FolderDataImpl> read();
    void print(List<FolderDataImpl> data);
    void clean();
    void getStatistic(String path);
}
