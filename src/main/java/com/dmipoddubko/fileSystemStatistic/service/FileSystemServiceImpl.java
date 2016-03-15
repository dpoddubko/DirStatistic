package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAO;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;

public class FileSystemServiceImpl implements FileSystemService {
    private FileDAO fileDAOImpl;
    private VisitFolder visitFolderImpl;

    public void setFileDAOImpl(FileDAO fileDAO) {
        this.fileDAOImpl = fileDAO;
    }

    public void setVisitFolderImpl(VisitFolder visitFolder) {
        this.visitFolderImpl = visitFolder;
    }

    public void index(String path) {
        fileDAOImpl.insert(visitFolderImpl.visit(path));
    }
}

