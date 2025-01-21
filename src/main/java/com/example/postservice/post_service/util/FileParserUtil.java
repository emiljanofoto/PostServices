package com.example.postservice.post_service.util;

import com.example.postservice.post_service.entity.Package;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileParserUtil {

    public static List<Package> parseExcelFile(InputStream inputStream) {
        List<Package> packages = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip header row
                }
                Package pkg = new Package();
                pkg.setTrackingNumber(row.getCell(0).getStringCellValue());
                pkg.setSender(row.getCell(1).getStringCellValue());
                pkg.setRecipient(row.getCell(2).getStringCellValue());
                pkg.setStatus(row.getCell(3).getStringCellValue());
                pkg.setPriority(row.getCell(4).getStringCellValue());
                packages.add(pkg);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
        return packages;
    }
}