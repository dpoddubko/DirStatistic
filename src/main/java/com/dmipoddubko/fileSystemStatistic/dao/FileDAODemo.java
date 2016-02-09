package com.dmipoddubko.fileSystemStatistic.dao;

import org.apache.log4j.PropertyConfigurator;

public class FileDAODemo {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        FileDAOImpl fileDAO = new FileDAOImpl();
        fileDAO.getStatistic("C:\\Users\\dpoddubko\\Desktop\\testRecursion");
    }
}

