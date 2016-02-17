package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface FileSystemService {

    void print(List<FolderData> data);

    void getStatistic(String path);
}
