package com.cselcuk89.seleniumautomationframework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {

    /**
     * Reads data from an Excel sheet and returns it as a List of Maps.
     * Each Map represents a row, with column headers as keys.
     *
     * @param filePath   Path to the Excel file.
     * @param sheetName  Name of the sheet to read.
     * @return List of Maps, where each Map is a row of data.
     * @throws IOException If an error occurs while reading the file.
     */
    public static List<Map<String, String>> getData(String filePath, String sheetName) throws IOException {
        List<Map<String, String>> dataList = new ArrayList<>();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IOException("Excel file not found: " + filePath);
            }

            fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis); // For .xlsx files
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IOException("Sheet '" + sheetName + "' not found in the Excel file: " + filePath);
            }

            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IOException("Header row not found in sheet '" + sheetName + "'.");
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Get data rows
            DataFormatter dataFormatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow == null) continue; // Skip empty rows

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
        return dataList;
    }

    /**
     * Example main method to test the ExcelReader.
     * Create an Excel file e.g. "test_data/LoginData.xlsx" with a sheet "LoginDetails"
     * Headers: Username, Password, ExpectedResult
     * Row 1: user1, pass1, Success
     * Row 2: user2, pass2, Failure
     */
    public static void main(String[] args) {
        // This is an example. Adjust path and sheet name as per your test data file.
        String projectDir = System.getProperty("user.dir");
        String filePath = projectDir + "/src/test/resources/test_data/ExampleTestData.xlsx"; // Example path
        String sheetName = "Sheet1";

        // Create a dummy Excel file for testing if it doesn't exist
        File testFile = new File(filePath);
        if (!testFile.getParentFile().exists()) {
            testFile.getParentFile().mkdirs();
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
                System.out.println("Created dummy Excel file: " + filePath);
            } catch (IOException e) {
                System.err.println("Could not create dummy Excel file for testing: " + e.getMessage());
            }
        }


        try {
            List<Map<String, String>> data = getData(filePath, sheetName);
            for (Map<String, String> row : data) {
                System.out.println(row);
            }
        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
