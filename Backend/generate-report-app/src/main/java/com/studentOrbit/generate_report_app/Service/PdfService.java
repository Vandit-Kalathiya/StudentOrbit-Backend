package com.studentOrbit.generate_report_app.Service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.AllArgsConstructor;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PdfService {
    // Theme constants
    private static class Theme {
        // Brand Colors
        static final DeviceRgb PRIMARY = new DeviceRgb(25, 91, 255);      // Vibrant blue
        static final DeviceRgb SECONDARY = new DeviceRgb(44, 62, 80);     // Dark slate
        static final DeviceRgb ACCENT = new DeviceRgb(52, 152, 219);      // Light blue

        // Neutral Colors
        static final DeviceRgb BACKGROUND = new DeviceRgb(247, 250, 252); // Light gray
        static final DeviceRgb TEXT_PRIMARY = new DeviceRgb(45, 55, 72);  // Dark gray
        static final DeviceRgb TEXT_SECONDARY = new DeviceRgb(113, 128, 150); // Medium gray
        static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);

        // Status Colors
        static final DeviceRgb[] COMPLETED = {
                new DeviceRgb(198, 246, 213), // Light green
                new DeviceRgb(39, 103, 73)    // Dark green
        };

        static final DeviceRgb[] IN_PROGRESS = {
                new DeviceRgb(190, 227, 248), // Light blue
                new DeviceRgb(44, 82, 130)    // Dark blue
        };

        static final DeviceRgb[] PENDING = {
                new DeviceRgb(254, 235, 200), // Light orange
                new DeviceRgb(146, 64, 14)    // Dark orange
        };
    }

    // Header component
    private static class Header {
        static void add(Document document, String username) {
            Table header = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(Theme.WHITE)
                    .setPadding(20)
                    .setMarginTop(20.0f);

            // Logo and Title
            Cell titleCell = new Cell()
//                    .add(new Paragraph("StudentOrbit")
//                            .setFontSize(12)
//                            .setFontColor(Theme.TEXT_PRIMARY)
//                            .setMarginBottom(5))
                    .add(new Paragraph("Project Progress Report")
                            .setFontSize(20)
                            .setBold()
                            .setFontColor(Theme.TEXT_PRIMARY))
                    .setBorder(null);

            // Student Info
            Cell infoCell = new Cell()
//                    .add(new Paragraph("Student ID")
//                            .setFontSize(12)
//                            .setFontColor(Theme.PRIMARY) // Approximated opacity
//                            .setMarginBottom(5))
                    .add(new Paragraph(username)
                            .setFontSize(16)
                            .setBold()
                            .setFontColor(Theme.TEXT_PRIMARY))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null)
                    .setPaddingTop(9.0f);

            header.addCell(titleCell);
            header.addCell(infoCell);

            document.add(header);
            document.add(new Paragraph().setMarginBottom(30));
        }
    }

    // Task Card component
    private static class TaskCard {
        static void add(Document document, TaskData task) {
            Table taskCard = new Table(UnitValue.createPercentArray(new float[]{7, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(15)
                    .setBackgroundColor(Theme.WHITE)
                    .setBorderRadius(new BorderRadius(8))
                    .setPadding(20);
//                    .setProperty(Property.BOX_SHADOW, "0 2px 4px rgba(0, 0, 0, 0.1)");

            // Task Details
            Cell detailsCell = new Cell()
                    .setBorder(null)
                    .setPaddingLeft(20)
                    .add(new Paragraph(task.getDescription())
                            .setBold()
                            .setFontSize(13)
                            .setFontColor(Theme.TEXT_PRIMARY)
                            .setMarginBottom(10))
                    .add(createInfoRow("Assignee", task.getAssignee()))
                    .add(createInfoRow("Due Date", formatDate(task.getDueDate())));

            // Status Badge
            Cell statusCell = new Cell()
                    .setBorder(null)
                    .setVerticalAlignment(VerticalAlignment.TOP)
                    .add(createStatusBadge(task.getStatus()));

            taskCard.addCell(detailsCell);
            taskCard.addCell(statusCell);

            document.add(taskCard);
        }

        private static Table createStatusBadge(String status) {
            DeviceRgb[] colors = getStatusColors(status);

            Table badge = new Table(1)
                    .setWidth(UnitValue.createPercentValue(70))
                    .setMarginLeft(12)
                    .setBorder(null);

            Cell badgeCell = new Cell()
                    .setBackgroundColor(colors[0])
                    .setBorderRadius(new BorderRadius(4))
                    .setPadding(6)
                    .setBorder(null)
                    .add(new Paragraph(status)
                            .setFontSize(10)
                            .setFontColor(colors[1])
                            .setTextAlignment(TextAlignment.CENTER));

            badge.addCell(badgeCell);
            return badge;
        }

        private static DeviceRgb[] getStatusColors(String status) {
            if (status == null) return Theme.PENDING;

            switch (status.toLowerCase()) {
                case "completed": return Theme.COMPLETED;
                case "in progress": return Theme.IN_PROGRESS;
                default: return Theme.PENDING;
            }
        }
    }

    // Main service methods
    public ByteArrayInputStream createPdf(String username, List<WeekData> weekDataList) {
        log.info("Starting PDF generation for user: {}", username);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(0, 36, 72, 36);

            Header.add(document, username);
            addProjectInfo(document);

            if (weekDataList != null && !weekDataList.isEmpty()) {
                for (WeekData weekData : weekDataList) {
                    addWeekSection(document, weekData);
                }
            }

            document.close();
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Error generating PDF: ", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addProjectInfo(Document document) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setPadding(20)
                .setBackgroundColor(Theme.BACKGROUND);

        infoTable.addCell(new Cell()
                .setBorder(null)
                .setPadding(8)
                .add(new Paragraph("Project Details")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(10))
                .add(createInfoRow("Mentor", "Ronak R. Patel").setFontSize(12))
                .add(createInfoRow("Project", "Project Management System").setFontSize(12)));

        document.add(infoTable);
        document.add(new Paragraph().setMarginBottom(30));
    }

    private void addWeekSection(Document document, WeekData weekData) {
        Table weekHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10);

        weekHeader.addCell(new Cell()
                .setBackgroundColor(Theme.SECONDARY)
                .setPadding(10)
                .add(new Paragraph("Week " + weekData.getWeekNumber())
                        .setFontSize(13)
                        .setBold()
                        .setFontColor(Theme.WHITE)));

        document.add(weekHeader);

        if (weekData.getTasks() != null) {
            for (TaskData task : weekData.getTasks()) {
                TaskCard.add(document, task);
            }
        }
    }

    // Utility methods
    private static Paragraph createInfoRow(String label, String value) {
        return new Paragraph()
                .add(new Text(label + ": ")
                        .setFontColor(Theme.TEXT_SECONDARY)
                        .setFontSize(10))
                .add(new Text(value)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setFontSize(10))
                .setMarginBottom(8);
    }

    private static String formatDate(Date date) {
        return date != null ?
                new SimpleDateFormat("MMM dd, yyyy").format(date) :
                "Not set";
    }

    // Data models
    @Data
    @AllArgsConstructor
    public static class WeekData {
        private int weekNumber;
        private List<TaskData> tasks;
    }

    @Data
    @AllArgsConstructor
    public static class TaskData {
        private String description;
        private String assignee;
        private Date dueDate;
        private String status;
    }
}