package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAO;
import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;

public class FileSystemServiceImpl implements FileSystemService {
    private FileDAO fileDAO;
    private VisitFolder visitFolder;

    public FileSystemServiceImpl() {
        this.fileDAO = new FileDAOImpl();
        this.visitFolder = new VisitFolderImpl();
    }

    public void index(String path) {
        fileDAO.insert(visitFolder.visit(path));
    }

    public FileDAO getFileDAO() {
        return fileDAO;
    }
}

