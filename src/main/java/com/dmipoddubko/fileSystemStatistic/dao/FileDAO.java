package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface FileDAO {
    void create();

    void insert(FolderData fd);

    List<FolderData> read();

    void clean();
}
