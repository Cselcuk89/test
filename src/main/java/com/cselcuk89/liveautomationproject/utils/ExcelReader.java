package com.cselcuk89.liveautomationproject.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager; // Added Log4j2 import
import org.apache.logging.log4j.Logger;    // Added Log4j2 import

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream; // Added for dummy file creation in main
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    private static final Logger log = LogManager.getLogger(ExcelReader.class); // Added logger

    public static List<Map<String, String>> getData(String filePath, String sheetName) throws IOException {
        log.info("Reading data from Excel file: '{}', sheet: '{}'", filePath, sheetName); // Added log
        List<Map<String, String>> dataList = new ArrayList<>();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IOException("Excel file not found: " + filePath);
            }

            fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis); 
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IOException("Sheet '" + sheetName + "' not found in the Excel file: " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Header row not found in sheet '" + sheetName + "'.");
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            DataFormatter dataFormatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) continue; 

                Map<String, String> rowMap = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = dataRow.getCell(j);
                    String cellValue = "";
                    if (cell != null) {
                        cellValue = dataFormatter.formatCellValue(cell);
                    }
                    rowMap.put(headers.get(j), cellValue);
                }
                dataList.add(rowMap);
            }
        } finally {
            if (workbook != null) {
                workbook.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        // It's tricky to get exact column count without assumptions or iterating all rows for max columns.
        // For now, logging row count. Column count could be inferred from headers.size() if rows are consistent.
        int columnCount = dataList.isEmpty() ? 0 : dataList.get(0).keySet().size();
        log.debug("Data read successfully: {} rows, approx. {} columns (based on first row).", dataList.size(), columnCount); // Added log
        return dataList;
    }

    public static void main(String[] args) {
        String projectDir = System.getProperty("user.dir");
        String filePath = projectDir + "/src/test/resources/test_data/ExampleTestData.xlsx";
        String sheetName = "Sheet1";

        File testFile = new File(filePath);
        if (!testFile.getParentFile().exists()) {
            if(testFile.getParentFile().mkdirs()){
                log.info("Created directory for dummy Excel file: {}", testFile.getParentFile().getAbsolutePath());
            } else {
                log.error("Failed to create directory for dummy Excel file: {}", testFile.getParentFile().getAbsolutePath());
            }
        }
        if (!testFile.exists()) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet(sheetName);
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Username");
                headerRow.createCell(1).setCellValue("Password");
                headerRow.createCell(2).setCellValue("Role");

                Row dataRow1 = sheet.createRow(1);
                dataRow1.createCell(0).setCellValue("testuser1");
                dataRow1.createCell(1).setCellValue("pass123");
                dataRow1.createCell(2).setCellValue("Admin");

                Row dataRow2 = sheet.createRow(2);
                dataRow2.createCell(0).setCellValue("testuser2");
                dataRow2.createCell(1).setCellValue("pass456");
                dataRow2.createCell(2).setCellValue("User");

                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    workbook.write(fos);
                }
                log.info("Created dummy Excel file: {}", filePath);
            } catch (IOException e) {
                log.error("Could not create dummy Excel file for testing: {}", e.getMessage(), e);
            }
        }

        try {
            List<Map<String, String>> data = getData(filePath, sheetName);
            for (Map<String, String> row : data) {
                log.info("Row data: {}", row); // Changed from System.out
            }
        } catch (IOException e) {
            // The getData method already throws IOException, so this catch block in main
            // is for handling it if main itself calls getData directly.
            // The error logging for reading issues is primarily handled within getData for general use.
            // However, if an exception occurs during getData, it will be thrown and can be caught here.
            // The specific instruction was "In case of errors: log.error("Error reading Excel file: {}", e.getMessage(), e);"
            // This is better placed within the getData method or if getData doesn't throw, but it does.
            // For the main method's direct call, this is fine.
            log.error("Error reading Excel file from main method: {}", e.getMessage(), e);
        }
    }
}
