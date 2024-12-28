package com.studentOrbit.generate_report_app.Service;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import jakarta.persistence.Column;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.cglib.core.WeakCacheKey;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
public class PdfService {

    @Autowired
    RestTemplate restTemplate;

    public static String baseUrl = "http://localhost:1818";
    @Autowired
    private AopAutoConfiguration aopAutoConfiguration;

    // Theme constants
    private static class Theme {
        // Brand Colors
        static final DeviceRgb PRIMARY = new DeviceRgb(25, 91, 255);      // Vibrant blue
        static final DeviceRgb SECONDARY = new DeviceRgb(44, 62, 80);     // Dark slate
        static final DeviceRgb ACCENT = new DeviceRgb(52, 152, 219);
        static final DeviceRgb NAVY = new DeviceRgb(0, 32, 96); // Light blue

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

    // Fetching WeekData and TaskData
    public List<WeekData> fetchWeekData(String username, String groupName, HttpServletRequest request) {
        List<WeekData> weekDataList = new ArrayList<>();

        // Extract token from cookies
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            throw new RuntimeException("Authentication token not found in cookies");
        }
        System.out.println("Token : --------------------------------" + token);
        // Set the token in the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Create an HttpEntity with headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        if (username != null) {
            // Fetch tasks from the external API
            String url = baseUrl + "/tasks/s/all/" + username;
            System.out.println(url);
            List<Task> tasks = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Task>>() {}
            ).getBody();

            assert tasks != null;

            // Sort tasks by week number
            tasks.sort(Comparator.comparing(t -> t.getWeek().getWeekNumber()));

            // Group tasks by week number
            Map<Integer, List<TaskData>> groupedTasks = new LinkedHashMap<>();

            tasks.forEach(task -> {
                // Convert each Task to TaskData
                List<String> assignees = new ArrayList<>();
                task.getAssignee().forEach(assignee -> assignees.add(assignee.getUsername()));

                TaskData taskData = new TaskData(
                        task.getName(),
                        task.getDescription(),
                        assignees,
                        task.getCompletedDate(),
                        task.getStatus()
                );

                // Group tasks by week number
                int weekNumber = task.getWeek().getWeekNumber();
                groupedTasks.computeIfAbsent(weekNumber, k -> new ArrayList<>()).add(taskData);
            });

            // Convert grouped tasks into WeekData objects
            groupedTasks.forEach((weekNumber, taskDataList) -> {
                WeekData weekData = new WeekData(weekNumber, taskDataList);
                weekDataList.add(weekData);
            });
        }

        return weekDataList;
    }

    private static class FooterHandler implements IEventHandler {

        private final Document doc;

        public FooterHandler(Document doc) {
            this.doc = doc;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent pdfDocumentEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDocument = pdfDocumentEvent.getDocument();
            PdfPage page = pdfDocumentEvent.getPage();
            Rectangle pageSize = page.getPageSize();

            // Create a PdfCanvas for drawing
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument);

            // Create a Canvas wrapper for high-level drawing
            Canvas canvas = new Canvas(pdfCanvas, pageSize);

            // Footer content: Page number
            int currentPageNumber = pdfDocument.getPageNumber(page);
            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            footerTable.addCell(new Cell().setBorder(Border.NO_BORDER)); // Left empty cell
            footerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph("StudentOrbit")
                            .setFontColor(Theme.NAVY)
                            .setFontSize(12)
                            .setMarginLeft(70)
                            .setTextAlignment(TextAlignment.CENTER))); // Center cell
            footerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.valueOf(currentPageNumber))
                            .setFontColor(Theme.NAVY)
                            .setFontSize(12)
                            .setTextAlignment(TextAlignment.RIGHT))); // Right cell

            // Position the footer table at the bottom of the page
            footerTable.setFixedPosition(
                    -40, // x position
                    30, // y position from bottom
                    pageSize.getWidth() // width
            );

            // Add the footer table directly to the canvas
            canvas.add(footerTable);
            canvas.close();
        }
    }

    // Header component
    private static class Header {
        static void add(Document document, String username) {
            Table header = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(Theme.WHITE)
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
        private static void add(Document document, TaskData task) {
            Table taskCard = new Table(UnitValue.createPercentArray(new float[]{7,3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10)
                    .setBorder(Border.NO_BORDER) // Remove all borders
                    .setBorderLeft(new SolidBorder(new DeviceRgb(0, 128, 255), 2))
                    .setMarginLeft(5)
                    .setMarginRight(5)
                    .setBackgroundColor(Theme.BACKGROUND);

            // Task Details
            Cell detailsCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(20)
                    .setPaddingTop(6)
                    .setPaddingBottom(3)
                    .add(new Paragraph(task.getTaskName())
                            .setBold()
                            .setFontSize(13)
                            .setFontColor(Theme.TEXT_PRIMARY)
                            .setMarginBottom(6))
                    .add(new Paragraph(task.getDescription())
                            .setFontSize(10)
                            .setFontColor(Theme.TEXT_PRIMARY)
                            .setMarginBottom(6))
                    .add(createInfoRow("Assignees", String.join(", ", task.getAssignees()))) // Updated to handle multiple assignees
                    .add(createInfoRow("Due Date", formatDate(task.getCompletionDate())));

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
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginLeft(64)
                    .setMarginTop(10)
                    .setMarginRight(20)
                    .setBorder(null);

            Cell badgeCell = new Cell()
                    .setBackgroundColor(colors[0])
                    .setBorderRadius(new BorderRadius(4))
                    .setPadding(4)
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
            document.setMargins(12, 36, 72, 36);

            // Register the FooterHandler
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler(document));

            // Add content to the PDF
            Header.add(document, username); // Custom method for adding header
            addProjectInfo(document);       // Custom method for adding project info

            // Add weeks data
            if (weekDataList != null && !weekDataList.isEmpty()) {
                for (WeekData weekData : weekDataList) {
                    addWeekSection(document, weekData); // Custom method for adding week sections
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
                .setBorderRadius(new BorderRadius(5))
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
        document.add(new Paragraph().setMarginBottom(10));
    }

    private void addWeekSection(Document document, WeekData weekData) {
        Table weekHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

        weekHeader.addCell(new Cell()
                .setBackgroundColor(Theme.SECONDARY)
                .setBorderTopLeftRadius(new BorderRadius(4))
                .setBorderTopRightRadius(new BorderRadius(4))
                .setBorder(null)
                .setPadding(8)
                .setPaddingLeft(16)
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

    private static String formatDate(LocalDateTime date) {
        return date != null ?
                date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) :
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
        private String taskName;
        private String description;
        private List<String> assignees;
        private LocalDateTime completionDate;
        private String status;
    }
}