package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface FileSystemService {
    void insertPrepare(String defaultPath);

    void print(List<FolderData> data);

    void getStatistic(String path);
}
