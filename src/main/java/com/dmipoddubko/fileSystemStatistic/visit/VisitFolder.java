package com.dmipoddubko.fileSystemStatistic.visit;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface VisitFolder {
    void visit(String defaultPath);
    List<FolderData> getData();
}
