package com.dmipoddubko.fileSystemStatistic.dao;

import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import org.apache.log4j.PropertyConfigurator;

public class FileDAODemo {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        FileSystemServiceImpl systemService = new FileSystemServiceImpl();
        systemService.getStatistic("C:\\Users\\dpoddubko\\Desktop\\testRecursion");
    }
}

