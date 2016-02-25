package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.connection.ConnectionBD;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.Collection;
import java.util.List;

public interface FileDAO {
    void create();

    void insert(FolderData fd);

    void insert(Collection<FolderData> collection);

    List<FolderData> read();

    void clean();

    int count();

    ConnectionBD getBaseConnectionBD();
}
