package com.dmipoddubko.fileSystemStatistic.writeFile;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class WriteXLSImpl implements WriteFile {
    public void doFile(String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream(path);
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet worksheet = workbook.createSheet("POI Worksheet");
            HSSFRow row1 = worksheet.createRow((short) 0);
            HSSFCell cellA1 = row1.createCell((short) 0);
            cellA1.setCellValue("Some data");
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.GOLD.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellA1.setCellStyle(cellStyle);
            HSSFCell cellB1 = row1.createCell((short) 1);
            cellB1.setCellValue("Another data");
            cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellB1.setCellStyle(cellStyle);
            HSSFCell cellC1 = row1.createCell((short) 2);
            cellC1.setCellValue(true);
            HSSFCell cellD1 = row1.createCell((short) 3);
            cellD1.setCellValue(new Date());
            cellStyle = workbook.createCellStyle();
            cellStyle.setDataFormat(HSSFDataFormat
                    .getBuiltinFormat("m/d/yy h:mm"));
            cellD1.setCellStyle(cellStyle);
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File xls not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Some error with writing xls files in folder.", e);
        }
    }
}
