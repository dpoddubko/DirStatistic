package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAO;
import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.apache.log4j.Logger;

import java.util.List;

public class FileSystemServiceImpl implements FileSystemService {
    private final static Logger LOG = Logger.getLogger(FileSystemServiceImpl.class);
    private FileDAO fileDAO;

    public FileSystemServiceImpl() {
        this.fileDAO = new FileDAOImpl();
    }

    public void insertPrepare(String defaultPath) {
        VisitFolder visitFolder = new VisitFolderImpl();
        fileDAO.insert(visitFolder.visit(defaultPath));
    }

    public void print(List<FolderData> data) {
        for (FolderData d : data) {
            LOG.info("id = " + d.getId());
            LOG.info("name = " + d.getName());
            LOG.info("path = " + d.getPath());
            LOG.info("type = " + d.getType());
            LOG.info("size = " + d.getSize() + "\n");
        }
    }

    public void getStatistic(String path) {
        fileDAO.create();
        insertPrepare(path);
        print(fileDAO.read());
        fileDAO.clean();
    }
}
