package com.example.postservice.post_service.service;

import com.example.postservice.post_service.entity.Package;
import com.example.postservice.post_service.repository.PackageRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private PackageRepository packageRepository;

    public byte[] exportPackages(String format) {
        List<Package> packages = packageRepository.findAll();

        switch (format.toLowerCase()) {
            case "csv":
                return exportToCSV(packages);
            case "xlsx":
                return exportToExcel(packages);
            case "pdf":
                return exportToPDF(packages);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }

    private byte[] exportToCSV(List<Package> packages) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.println("Tracking Number,Sender,Recipient,Status,Priority");
            for (Package pkg : packages) {
                writer.printf("%s,%s,%s,%s,%s\n",
                        pkg.getTrackingNumber(),
                        pkg.getSender(),
                        pkg.getRecipient(),
                        pkg.getStatus(),
                        pkg.getPriority());
            }
        }
        return out.toByteArray();
    }

    private byte[] exportToExcel(List<Package> packages) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Packages");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tracking Number");
            header.createCell(1).setCellValue("Sender");
            header.createCell(2).setCellValue("Recipient");
            header.createCell(3).setCellValue("Status");
            header.createCell(4).setCellValue("Priority");

            int rowIdx = 1;
            for (Package pkg : packages) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(pkg.getTrackingNumber());
                row.createCell(1).setCellValue(pkg.getSender());
                row.createCell(2).setCellValue(pkg.getRecipient());
                row.createCell(3).setCellValue(pkg.getStatus());
                row.createCell(4).setCellValue(pkg.getPriority());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to Excel", e);
        }
    }

    private byte[] exportToPDF(List<Package> packages) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Initialize the PDF document
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Package List")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
            );

            // Add a table with package details
            Table table = new Table(new float[]{4, 4, 4, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Tracking Number").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Sender").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Recipient").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Status").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Priority").setBold()));

            // Populate table rows
            for (Package pkg : packages) {
                table.addCell(pkg.getTrackingNumber() != null ? pkg.getTrackingNumber() : "N/A");
                table.addCell(pkg.getSender() != null ? pkg.getSender() : "N/A");
                table.addCell(pkg.getRecipient() != null ? pkg.getRecipient() : "N/A");
                table.addCell(pkg.getStatus() != null ? pkg.getStatus() : "N/A");
                table.addCell(pkg.getPriority() != null ? pkg.getPriority() : "N/A");
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to PDF", e);
        }

        return out.toByteArray();
    }
}