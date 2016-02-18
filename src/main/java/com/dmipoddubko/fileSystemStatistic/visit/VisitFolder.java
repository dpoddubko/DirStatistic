package com.dmipoddubko.fileSystemStatistic.visit;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;

import java.util.List;

public interface VisitFolder {
    List<FolderData> visit(String defaultPath);

}
