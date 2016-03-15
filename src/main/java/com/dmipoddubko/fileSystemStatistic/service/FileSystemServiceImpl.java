package com.dmipoddubko.fileSystemStatistic.service;

import com.dmipoddubko.fileSystemStatistic.dao.FileDAO;
import com.dmipoddubko.fileSystemStatistic.dao.FileDAOImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolderImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileSystemServiceImpl implements FileSystemService {
    private FileDAO fileDAO;
    private VisitFolder visitFolder;

    public void FileSystemServiceImpl() {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
        this.fileDAO = (FileDAOImpl) context.getBean("fileDAO");
        this.visitFolder = (VisitFolderImpl) context.getBean("visitFolder");
    }

    public void index(String path) {
        fileDAO.insert(visitFolder.visit(path));
    }
}

