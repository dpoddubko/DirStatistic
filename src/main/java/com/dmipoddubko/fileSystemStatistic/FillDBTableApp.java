package com.dmipoddubko.fileSystemStatistic;

import org.apache.log4j.PropertyConfigurator;

import java.sql.SQLException;

public class FillDBTableApp {
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
        FillDBTable fillDBTable = new FillDBTable();
        try {
            fillDBTable.conn();
            fillDBTable.createTable();
            fillDBTable.visitDir("C:\\Users\\dpoddubko\\Desktop\\testRecursion");
            fillDBTable.readTable();
            fillDBTable.CloseDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

