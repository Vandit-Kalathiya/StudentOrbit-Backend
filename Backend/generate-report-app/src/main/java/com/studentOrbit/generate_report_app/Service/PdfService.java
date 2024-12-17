package com.studentOrbit.generate_report_app.Service;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfService {

    Logger logger = LoggerFactory.getLogger(PdfService.class);

    private static final Color HEADER_BACKGROUND_COLOR = new DeviceRgb(1, 41, 112);
    private static final Color HEADER_FONT_COLOR = new DeviceRgb(255, 255, 255);

    public ByteArrayInputStream createPdf(String username) {
        logger.info("Creating PDF" + username);

        String mentorName = "Mentor: Ronak R. Patel";
        String projectName = "Project Name: Project Management System";
        String headerLeft = "CE-363 : SGP-III";
        String headerRight = "Student ID : " + username;
        String title = "Welcome to StudentOrbit!";
        String content = "Encircles and keeps track of all aspects of student projects in one place.";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer), PageSize.A4);
            document.setMargins(20, 20, 20, 20);

            // Header
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(createHeaderCell(headerLeft, TextAlignment.LEFT));
            headerTable.addCell(createHeaderCell(headerRight, TextAlignment.RIGHT));
            document.add(headerTable);

            // Mentor and Project Name
            document.add(new Paragraph(mentorName).setFontSize(12).setMarginBottom(5));
            document.add(new Paragraph(projectName).setFontSize(12).setMarginBottom(20));

            // Title
            document.add(new Paragraph(title)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold()
                    .setMarginBottom(20));

            // Content
            document.add(new Paragraph(content)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setMarginBottom(20));

            // Table Columns
            float[] columnWidths = {1, 3, 2, 2, 1.5f};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100)); // Full width

            // Add table headers
            table.addHeaderCell(createTableHeaderCell("Week"));
            table.addHeaderCell(createTableHeaderCell("Task"));
            table.addHeaderCell(createTableHeaderCell("Deadline"));
            table.addHeaderCell(createTableHeaderCell("Submitted Date"));
            table.addHeaderCell(createTableHeaderCell("Status"));

            // Mock data for the table
            List<WeekTask> weekTasks = getMockWeekTasks(); // Replace with your actual data fetching
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

            // Add data rows
            for (WeekTask weekTask : weekTasks) {
                table.addCell(createTableDataCell("Week " + weekTask.getWeekNumber()));
                table.addCell(createTableDataCell(weekTask.getTaskName()));
                table.addCell(createTableDataCell(dateFormat.format(weekTask.getDeadline())));
                table.addCell(createTableDataCell(dateFormat.format(weekTask.getSubmittedDate())));

                String status = weekTask.getSubmittedDate().before(weekTask.getDeadline()) ? "On Time" : "Late";
                table.addCell(createTableDataCell(status));
            }

            // Add the table to the document
            document.add(table);

            document.close();
        } catch (Exception e) {
            logger.error("Error creating PDF: ", e);
        }

        logger.info("PDF created");
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private static Cell createHeaderCell(String content, TextAlignment alignment) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content).setFontColor(HEADER_FONT_COLOR).setBold());
        cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
        cell.setTextAlignment(alignment);
        cell.setBorder(null); // Remove cell borders for header
        return cell;
    }

    private static Cell createTableHeaderCell(String content) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content).setFontColor(HEADER_FONT_COLOR).setBold());
        cell.setBackgroundColor(HEADER_BACKGROUND_COLOR);
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }

    private static Cell createTableDataCell(String content) {
        Cell cell = new Cell();
        cell.add(new Paragraph(content));
        cell.setTextAlignment(TextAlignment.CENTER);
        return cell;
    }

    private List<WeekTask> getMockWeekTasks() {
        return List.of(
                new WeekTask(1, "Task 1", new Date(), new Date()),
                new WeekTask(2, "Task 2", new Date(), new Date())
        );
    }

    @Data
    @AllArgsConstructor
    private static class WeekTask {
        private final int weekNumber;
        private final String taskName;
        private final Date deadline;
        private final Date submittedDate;
    }
}
