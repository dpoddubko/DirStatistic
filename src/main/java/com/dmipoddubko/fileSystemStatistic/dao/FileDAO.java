package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface FileDAO {
    void create();

    void insert(String defaultPath);

    void insertPrepare(String name, String path, String type, long size);

    List<FolderData> read();

    void print(List<FolderData> data);

    void clean();

    void getStatistic(String path);
}
