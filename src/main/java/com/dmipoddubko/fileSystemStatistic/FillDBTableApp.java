package com.dmipoddubko.fileSystemStatistic;

import org.apache.log4j.PropertyConfigurator;

import java.sql.SQLException;

public class FillDBTableApp {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        FillDBTable fillDBTable = new FillDBTable();
        try {
            fillDBTable.connection();
            fillDBTable.createTab();
            fillDBTable.visitFolder("C:\\Users\\dpoddubko\\Desktop\\testRecursion");
            fillDBTable.readTab();
            fillDBTable.closeDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

