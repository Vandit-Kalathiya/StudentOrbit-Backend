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
import com.studentOrbit.generate_report_app.Helper.PdfGenerateRequest;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import com.studentOrbit.generate_report_app.entity.Weeks.Week;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDate;
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

    // Theme constants
    private static class Theme {
        // Brand Colors
//        static final DeviceRgb PRIMARY = new DeviceRgb(25, 91, 255);      // Vibrant blue
        static final DeviceRgb SECONDARY = new DeviceRgb(44, 62, 80);     // Dark slate

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

    private static Group groupData;

    private static String reportType;

    private static String studentId;

    private static List<Student> members;

    // Fetching WeekData and TaskData
    public List<WeekData> fetchWeekData(PdfGenerateRequest pdfGenerateRequest, HttpServletRequest request) {
            String type = pdfGenerateRequest.getReportType();

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
        System.out.println("Token : --------------------------------" + pdfGenerateRequest.getProjectName());
        // Set the token in the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Create an HttpEntity with headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = baseUrl + "/faculty/groups/" + ("gid/" + pdfGenerateRequest.getProjectName());
        System.out.println(url);
//        Group group = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                Group.class
//        ).getBody();
        ResponseEntity<Group> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        Group group = response.getBody();


        String url2 = baseUrl + "/faculty/groups/" + ("members/" + pdfGenerateRequest.getProjectName());
        members = Objects.requireNonNull(restTemplate.exchange(
                url2,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Set<Student>>() {
                }
        ).getBody()).stream().toList();

        assert group != null;
        groupData = group;
        reportType = pdfGenerateRequest.getReportType();

        if(type.equalsIgnoreCase("student")){
            studentId = pdfGenerateRequest.getIdentifier();
        }

        List<Week> weeks = group.getWeeks().stream().filter(week -> pdfGenerateRequest.getWeeks().contains(week.getWeekNumber())).toList();

        List<Task> tasks = new ArrayList<>();

        if (type.equalsIgnoreCase("student")) {
            // Fetch tasks from the external API

            weeks.forEach(w -> w.getTasks().forEach(t -> {
                List<String> assignees = t.getAssignee().stream().map(Student::getUsername).toList();
                if(assignees.contains(pdfGenerateRequest.getIdentifier())) {
                    tasks.add(t);
                }
            }));

        } else {
            // Make a perfect api for generating reports
            // Fetch the api to get the group by Batch and Name
            // Then First show all details of group
            // Show details of all weeks and tasks of the group
            // Show project completion date if project is completed
            // also show the number of tasks completed and late completed by student in each week
            // Add time of report generation in bottom left
            // Then show group progress and highlight the late submitted tasks

            // Show all task details including the submitted and completed date, how many docs were submitted (Show all docs name and its submitted date),
            // faculty comments and with date and time in both generation type
            // Show numbers of completed tasks by each student in each week
            // Then Show statistics that which student had done how many tasks in the group till now/completion of project
            // Show total number of tasks completed and late completed by student in individual student report generation

            // Fetch tasks from the external API

            weeks.forEach(w -> tasks.addAll(w.getTasks()));
        }

        tasks.sort(Comparator.comparing(t -> t.getWeek().getWeekNumber()));

        Map<Integer, List<TaskData>> groupedTasks = new LinkedHashMap<>();

        tasks.forEach(task -> {
            List<String> assignees = new ArrayList<>();
            task.getAssignee().forEach(assignee -> assignees.add(assignee.getUsername()));

            TaskData taskData = new TaskData(
                    task.getName(),
                    task.getDescription(),
                    assignees,
                    task.getWeek().getEndDate(),
                    (task.getCompletedDate() != null) ? task.getCompletedDate().toLocalDate() : null,
                    task.getStatus()
            );

            int weekNumber = task.getWeek().getWeekNumber();
            groupedTasks.computeIfAbsent(weekNumber, k -> new ArrayList<>()).add(taskData);
        });

        groupedTasks.forEach((weekNumber, taskDataList) -> {
            WeekData weekData = new WeekData(weekNumber, taskDataList);
            weekDataList.add(weekData);
        });

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

            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamAfter(), page.getResources(), pdfDocument);

            Canvas canvas = new Canvas(pdfCanvas, pageSize);

            Table lineTable = new Table(1)
                    .setWidth(UnitValue.createPercentValue(100));

            Cell lineCell = new Cell()
                    .setHeight(1f)
                    .setBorder(Border.NO_BORDER)
                    .setBorderTop(new SolidBorder(Theme.TEXT_PRIMARY, 0.5f))
                    .setPadding(0);

            lineTable.addCell(lineCell);

            float rightMargin = 20;
            float leftMargin = 20;

            lineTable.setFixedPosition(
                    leftMargin,
                    50,
                    pageSize.getWidth() - leftMargin - rightMargin
            );

            int currentPageNumber = pdfDocument.getPageNumber(page);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                    .setWidth(UnitValue.createPercentValue(100));

            footerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph("Generated At : " + LocalDateTime.now().format(formatter))
                            .setFontColor(Theme.SECONDARY)
                            .setFontSize(10)
                            .setTextAlignment(TextAlignment.LEFT)));

            // Center cell: Application name
            footerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph("StudentOrbit")
                            .setFontColor(Theme.SECONDARY)
                            .setBold()
                            .setFontSize(12)
                            .setTextAlignment(TextAlignment.CENTER)));

            // Right cell: Page number
            footerTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(String.valueOf(currentPageNumber))
                            .setFontColor(Theme.SECONDARY)
                            .setFontSize(10)
                            .setMarginRight(10)
                            .setTextAlignment(TextAlignment.RIGHT)));

            // Position the footer table at the bottom of the page
            footerTable.setFixedPosition(
                    leftMargin, // x position
                    20, // y position for the footer table
                    pageSize.getWidth() - leftMargin - rightMargin // width
            );

            // Add the horizontal line and footer table to the canvas
            canvas.add(lineTable);
            canvas.add(footerTable);

            // Close the canvas
            canvas.close();
        }

    }

    // Header component
    private static class Header {
        static void add(Document document) {
            // Create a table with three columns for left, center, and right alignment
            Table header = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1})) // Adjust column widths
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(Theme.WHITE)
                    .setMarginTop(20.0f);

            // Subject Cell: Left aligned, bold
            Cell subjectCell = new Cell()
                    .add(new Paragraph("CE396")
                            .setFontSize(12)
                            .setBold()
                            .setFontColor(Theme.TEXT_PRIMARY)
                            .setTextAlignment(TextAlignment.LEFT))
                    .setBorder(null)
                    .setPaddingTop(10f);

            // Title Cell: Center aligned, bold
            Cell titleCell = new Cell()
                    .add(new Paragraph("Project Progress Report")
                            .setFontSize(14)
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(Theme.TEXT_PRIMARY))
                    .setBorder(null)
                    .setPaddingTop(10f);

            // Info Cell: Right aligned, bold
            Cell infoCell = new Cell()
                    .add(new Paragraph(reportType.equalsIgnoreCase("student") ? studentId : groupData.getUniqueGroupId())
                            .setFontSize(12)
                            .setBold()
                            .setFontColor(Theme.TEXT_PRIMARY))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBorder(null)
                    .setPaddingTop(10f);

            // Add the cells to the table (left, center, right)
            header.addCell(subjectCell);
            header.addCell(titleCell);
            header.addCell(infoCell);

            // Add table to document
            document.add(header);

            // Add extra space after the header
            document.add(new Paragraph().setMarginBottom(30));  // Extra space after the header
        }
    }

    private static Boolean isLate(LocalDate dueDate, LocalDate completionDate) {
        if (dueDate == null && completionDate == null) {
            return false;
        }
        if(completionDate == null){
            completionDate = LocalDate.now();
        }
        return completionDate.isAfter(dueDate);
    }

    // Task Card component
    private static class TaskCard {
        private static void add(Document document, TaskData task, boolean isLate) {
            Table taskCard = new Table(UnitValue.createPercentArray(new float[]{7, 3}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10)
                    .setBorder(Border.NO_BORDER)
                    .setBorderLeft(new SolidBorder(
                            isLate ? new DeviceRgb(255, 0, 0) : new DeviceRgb(44, 62, 80),
                            2.5f
                    ))
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
                    .add(createInfoRow("Assignees", task.getAssignees().isEmpty() ? "Not assigned" : String.join(", ", task.getAssignees())));

            // Combine Due Date and Completion Date in one row
            String formattedDueDate = (task.getDueDate() != null) ? formatDate(task.getDueDate()) : "No due date set";
            String formattedCompletionDate = (task.getStatus().equals("COMPLETED") && task.getCompletionDate() != null)
                    ? formatDate(task.getCompletionDate()) : "Not completed yet";

            // Add both dates in a single row
            detailsCell.add(createInfoRow("Dates", "Due Date: " + formattedDueDate + " | Completion Date: " + formattedCompletionDate));

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

            return switch (status.toLowerCase()) {
                case "completed" -> Theme.COMPLETED;
                case "in progress" -> Theme.IN_PROGRESS;
                default -> Theme.PENDING;
            };
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
            Header.add(document); // Custom method for adding header
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
        // Create the main table with rounded corners and background color
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorderRadius(new BorderRadius(5))
                .setBackgroundColor(Theme.BACKGROUND);

        // Add title and description
        infoTable.addCell(new Cell()
                .setBorder(null)
                .setPadding(10)
                .add(new Paragraph(groupData.getGroupName())
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(10)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(groupData.getGroupDescription())
                        .setFontSize(12)
                        .setMarginBottom(5)
                        .setTextAlignment(TextAlignment.CENTER)));

        // Dynamic data for the left and right columns
        Map<String, String> leftColumnData = new LinkedHashMap<>();
        leftColumnData.put("Mentor", groupData.getMentor().getName());
        System.out.println(groupData.getStudents());
        List<String> members2 = members.stream().map(Student::getUsername).toList();
        leftColumnData.put("Members", members2.toString().substring(1, members2.toString().length()-1));
        leftColumnData.put("Group Leader", groupData.getGroupLeader());

        Map<String, String> rightColumnData = new LinkedHashMap<>();
        rightColumnData.put("Start Date", groupData.getStartDate());
        rightColumnData.put("Progress", "15%");
        rightColumnData.put("Status", groupData.getProjectStatus());

        // Create a two-column table
        Table twoColumnTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        // Build the left column dynamically
        Cell leftColumnCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        for (Map.Entry<String, String> entry : leftColumnData.entrySet()) {
            leftColumnCell.add(new Paragraph(entry.getKey() + ": " + entry.getValue())
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMargin(0)
                    .setTextAlignment(TextAlignment.LEFT));
        }
        twoColumnTable.addCell(leftColumnCell);

        // Build the right column dynamically
        Cell rightColumnCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        for (Map.Entry<String, String> entry : rightColumnData.entrySet()) {
            rightColumnCell.add(new Paragraph(entry.getKey() + ": " + entry.getValue())
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMargin(0)
                    .setTextAlignment(TextAlignment.LEFT));
        }
        twoColumnTable.addCell(rightColumnCell);

        // Add the two-column table to the main info table
        infoTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(twoColumnTable));

        // Add the infoTable to the document with spacing below
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
                .add(new Paragraph()
                        .add(new Text("Week " + weekData.getWeekNumber())
                                .setFontSize(13)
                                .setBold()
                                .setFontColor(Theme.WHITE))
                        .add(new Text("     ( " + weekData.getTasks().size() + " tasks )")
                                .setFontSize(10)
                                .setFontColor(Theme.WHITE))));

        document.add(weekHeader);

        if (weekData.getTasks() != null) {
            for (TaskData task : weekData.getTasks()) {
                TaskCard.add(document, task, isLate(task.dueDate, task.completionDate));
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

    private static String formatDate(LocalDate date) {
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
        private LocalDate startDate;
        private LocalDate endDate;

        public WeekData(int weekNumber, List<TaskData> tasks){
            this.weekNumber = weekNumber;
            this.tasks = tasks;
        }
    }

    @Data
    @AllArgsConstructor
    public static class TaskData {
        private String taskName;
        private String description;
        private List<String> assignees;
        private LocalDate dueDate;
        private LocalDate completionDate;
        private String status;
    }
}