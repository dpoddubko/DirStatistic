package com.dmipoddubko.fileSystemStatistic;

import org.apache.log4j.PropertyConfigurator;

public class FillDBTableApp {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        FillDBTable fillDBTable = new FillDBTable();
        fillDBTable.connection();
        fillDBTable.create();
        fillDBTable.visitFolder("C:\\Users\\dpoddubko\\Desktop\\testRecursion");
        //fillDBTable.clean();
        fillDBTable.read();
        fillDBTable.close();
    }
}

