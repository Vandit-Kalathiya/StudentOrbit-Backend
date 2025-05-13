package com.studentOrbit.generate_report_app.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.studentOrbit.generate_report_app.Helper.MarksReportGenerateRequest;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Student.Student;
import com.studentOrbit.generate_report_app.entity.Task.Rubrics;
import com.studentOrbit.generate_report_app.entity.Task.Task;
import com.studentOrbit.generate_report_app.entity.Weeks.Week;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StudentPerformanceReport {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${base_url}")
    private String BASE_URL;

    // Custom Theme class
    private static class Theme {
        static final Color PRIMARY = new DeviceRgb(25, 91, 255);
        static final Color SECONDARY = new DeviceRgb(44, 62, 80);
        static final Color BACKGROUND = new DeviceRgb(247, 250, 252);
        static final Color TEXT_PRIMARY = new DeviceRgb(45, 55, 72);
        static final Color TEXT_SECONDARY = new DeviceRgb(113, 128, 150);
        static final Color WHITE = ColorConstants.WHITE;
        static final Color LIGHT_BLUE = new DeviceRgb(240, 247, 255);
        static final Color GOOD_SCORE = new DeviceRgb(202, 229, 255);
        static final Color AVERAGE_SCORE = new DeviceRgb(255, 243, 205);
        static final Color RATING_EXCELLENT = new DeviceRgb(40, 167, 69);
        static final Color RATING_GOOD = new DeviceRgb(0, 123, 255);
        static final Color RATING_AVERAGE = new DeviceRgb(255, 193, 7);
        static final Color RATING_POOR = new DeviceRgb(220, 53, 69);
    }

    // Header method
    private static void addHeader(Document document, StudentData studentData) {
        Table header = new Table(new float[]{1, 2, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(1)
                .setMarginBottom(15);

        header.addCell(new Cell()
                .add(new Paragraph("CE396").setFontSize(12).setBold())
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.LEFT)
                .setPaddingTop(10));

        header.addCell(new Cell()
                .add(new Paragraph("Student Performance Report")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER)
                .setPaddingTop(10));

        header.addCell(new Cell()
                .add(new Paragraph(studentData.getStudentId()).setFontSize(12).setBold())
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(10));

        document.add(header);
    }

    // Footer class
    private static class Footer implements IEventHandler {

        public Footer() {
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

    // Student/Group Info method
    private static void addStudentOrGroupInfo(Document document, StudentData studentData) {
        Table infoTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginBottom(10)
                .setBackgroundColor(Theme.BACKGROUND);

        infoTable.addCell(new Cell()
                .add(new Paragraph(studentData.getProjectName())
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph("Performance Report for " + studentData.getStudentName())
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginTop(5))
                .setBorder(Border.NO_BORDER)
                .setPadding(10));

        Table twoColumnTable = new Table(2)
                .setWidth(UnitValue.createPercentValue(100));

        twoColumnTable.addCell(new Cell()
                .add(new Paragraph("Student: " + studentData.getStudentName())
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .add(new Paragraph("ID: " + studentData.getStudentId())
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .setBackgroundColor(Theme.BACKGROUND));

        twoColumnTable.addCell(new Cell()
                .add(new Paragraph("Start Date: 01-01-2025")
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .add(new Paragraph("Progress: " + studentData.getOverallPercentage() + "%")
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .setBackgroundColor(Theme.BACKGROUND));

        infoTable.addCell(new Cell()
                .add(twoColumnTable)
                .setBorder(Border.NO_BORDER));

        document.add(infoTable);
    }

    private HttpEntity<Void> getEntity(HttpServletRequest request) {
        String token = "null";

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

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HttpEntity with headers
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return entity;
    }

    private Student getStudentDetails(String username, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);

        String url = BASE_URL + "/students/u/" + username;
        ResponseEntity<Student> res = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {
        });

        if (!res.hasBody()) {
            throw new RuntimeException("Student not fount");
        }

        return res.getBody();
    }

    private Group getGroupDetails(String name, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);

        String url = BASE_URL + "/faculty/groups/" + ("gid/" + name);
//        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        ResponseEntity<Group> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    private List<Student> getGroupMembers(String name, HttpServletRequest request) {
        HttpEntity<Void> entity = getEntity(request);

        String url2 = BASE_URL + "/faculty/groups/" + ("members/" + name);

        return Objects.requireNonNull(restTemplate.exchange(
                url2,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Set<Student>>() {
                }
        ).getBody()).stream().toList();
    }

    public ByteArrayInputStream main(MarksReportGenerateRequest marksRepostGenerateRequest, HttpServletRequest request) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (marksRepostGenerateRequest.getReportType().equals("student")) {
                StudentData studentData = createSampleStudentData(marksRepostGenerateRequest, request);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdfDoc = new PdfDocument(writer);
                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new Footer());
                Document document = new Document(pdfDoc);

                addHeader(document, studentData);
                addStudentOrGroupInfo(document, studentData);
                addCriteriaLegend(document, studentData.getCriteria());
                for (Week week : studentData.getWeeks()) {
                    addWeekSection(document, week, studentData.getCriteria());
                }
                addPerformanceSummary(document, studentData);

                document.close();
            } else {
//                GroupData studentData = createSampleStudentData();
//                PdfWriter writer = new PdfWriter(outputStream);
//                PdfDocument pdfDoc = new PdfDocument(writer);
//                pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, new Footer());
//                Document document = new Document(pdfDoc);
//
//                addHeader(document, studentData);
//                addStudentOrGroupInfo(document, studentData);
//                addCriteriaLegend(document, studentData.getCriteria());
//                for (Week week : studentData.getWeeks()) {
//                    addWeekSection(document, week, studentData.getCriteria());
//                }
//                addPerformanceSummary(document, studentData);
//
//                document.close();
            }


            System.out.println("PDF generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private StudentData createSampleStudentData(MarksReportGenerateRequest marksReportGenerateRequest, HttpServletRequest request) {
        StudentData data = new StudentData();

        Student student = getStudentDetails(marksReportGenerateRequest.getName(), request);
        Group group = getGroupDetails(marksReportGenerateRequest.getProjectId(), request);

        data.setStudentName(student.getStudentName());
        data.setStudentId(student.getUsername());
        data.setProjectName(group.getGroupName());

        List<Criterion> criteria = Arrays.asList(
                new Criterion("CQ", "Code Quality"),
                new Criterion("TC", "Teamwork & Collaboration"),
                new Criterion("TM", "Task Completion on Time"),
                new Criterion("CI", "Creativity & Innovation"),
                new Criterion("PS", "Problem-Solving Ability"),
                new Criterion("RU", "Research & Understanding"),
                new Criterion("EE", "Effort & Engagement")
        );
        data.setCriteria(criteria);

        Map<String, String> criterionMap = criteria.stream()
                .collect(Collectors.toMap(Criterion::getName, Criterion::getAbbreviation));

        List<Week> weeks = new ArrayList<>();
        int totalMarks = 0;
        int totalPossibleMarks = 0;

        for (com.studentOrbit.generate_report_app.entity.Weeks.Week groupWeek : group.getWeeks()) {
            Week week = new Week(groupWeek.getWeekNumber());

            for (com.studentOrbit.generate_report_app.entity.Task.Task groupTask : groupWeek.getTasks()) {
                Task task = new Task(groupTask.getName(), groupTask.getDescription());

                int scoredMarks = groupTask.getScoredMarks();
                task.setPointsEarned(scoredMarks);
                task.setTotalPoints(28);
                task.setScore((scoredMarks * 100) / 28);

                Map<String, Integer> ratings = new HashMap<>();
                for (Rubrics rubric : groupTask.getRubrics()) {
                    String abbreviation = criterionMap.get(rubric.getRubricName());
                    if (abbreviation != null) {
                        ratings.put(abbreviation, rubric.getRubricScore());
                    }
                }
                task.setRatings(ratings);

                week.addTask(task);

                totalMarks += scoredMarks;
                totalPossibleMarks += 28;
            }

            weeks.add(week);
        }

        data.setWeeks(weeks);
        data.setTotalMarksEarned(totalMarks);
        data.setTotalPossibleMarks(totalPossibleMarks);

        // Calculate overall percentage if needed
        data.setOverallPercentage(totalPossibleMarks > 0 ? (totalMarks * 100) / totalPossibleMarks : 0);

        return data;
    }

    private static void addCriteriaLegend(Document document, List<Criterion> criteria) {
        // Create the table with dynamic columns based on criteria size
        Table legendTable = new Table(criteria.size())
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(5) // Increased margin for separation
                .setMarginBottom(5)
                .setBorder(new SolidBorder(Theme.TEXT_SECONDARY, 0.5f)) // Subtle table border
                .setBorderRadius(new BorderRadius(6)) // Rounded corners
                .setBackgroundColor(Theme.BACKGROUND); // Light background for the whole table

        // Add criteria cells with enhanced styling
        int index = 0;
        for (Criterion criterion : criteria) {
            // Alternate background colors for visual distinction
            Color cellBackground = (index % 2 == 0) ? Theme.LIGHT_BLUE : Theme.WHITE;

            Cell cell = new Cell()
                    .add(new Paragraph()
                            .add(new Text(criterion.getAbbreviation() + ": ")
                                    .setBold()
                                    .setFontSize(12)
                                    .setFontColor(Theme.SECONDARY)) // Darker color for abbreviation
                            .add(new Text(criterion.getName())
                                    .setFontSize(10)
                                    .setFontColor(Theme.TEXT_PRIMARY))) // Lighter color for name
                    .setBackgroundColor(cellBackground)
                    .setBorder(Border.NO_BORDER) // No individual cell borders
                    .setPadding(8) // Increased padding for breathing room
                    .setTextAlignment(TextAlignment.CENTER) // Centered text
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            legendTable.addCell(cell);
            index++;
        }

        // Add a subtle header-like effect with a title above the table
        document.add(new Paragraph("Evaluation Criteria")
                .setFontSize(14)
                .setBold()
                .setFontColor(Theme.SECONDARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5)
                .setMarginBottom(5));

        // Add the table to the document
        document.add(legendTable);

        // Add spacing below for separation from the next section
        document.add(new Paragraph(" ").setMarginBottom(15));
    }

//    private static void addWeekSection(Document document, Week week, List<Criterion> criteria) {
//        // Week header
//        Table weekHeader = new Table(1)
//                .setWidth(UnitValue.createPercentValue(100))
//                .setMarginTop(20);
//
//        weekHeader.addCell(new Cell()
//                .setBackgroundColor(Theme.SECONDARY)
//                .setBorderTopLeftRadius(new BorderRadius(4))
//                .setBorderTopRightRadius(new BorderRadius(4))
//                .setBorder(Border.NO_BORDER)
//                .setPadding(8)
//                .setPaddingLeft(16)
//                .add(new Paragraph()
//                        .add(new Text("Week " + week.getWeekNumber())
//                                .setFontSize(13)
//                                .setBold()
//                                .setFontColor(Theme.WHITE))
//                        .add(new Text("     ( " + week.getTasks().size() + " tasks )")
//                                .setFontSize(10)
//                                .setFontColor(Theme.WHITE))));
//
//        document.add(weekHeader);
//
//        // Tasks
//        for (Task task : week.getTasks()) {
//            Table taskCard = new Table(new float[]{7, 3})
//                    .setWidth(UnitValue.createPercentValue(100))
//                    .setMarginTop(10)
//                    .setBorder(Border.NO_BORDER)
//                    .setBorderLeft(new SolidBorder(Theme.SECONDARY, 2.5f))
//                    .setMarginLeft(5)
//                    .setMarginRight(5)
//                    .setBackgroundColor(Theme.BACKGROUND);
//
//            Cell detailsCell = new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setPaddingLeft(20)
//                    .setPaddingTop(6)
//                    .setPaddingBottom(3);
//
//            detailsCell.add(new Paragraph(task.getName())
//                    .setFontSize(13)
//                    .setBold()
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(6));
//
//            detailsCell.add(new Paragraph("Description: " + task.getDescription())
//                    .setFontSize(10)
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(6));
//
//            detailsCell.add(new Paragraph("Score: " + task.getScore() + "% (" + task.getPointsEarned() + "/" + task.getTotalPoints() + ")")
//                    .setFontSize(10)
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(8));
//
//            Table ratingsTable = new Table(criteria.size())
//                    .setWidth(UnitValue.createPercentValue(100));
//
//            for (Criterion criterion : criteria) {
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(criterion.getAbbreviation())
//                                .setFontSize(10)
//                                .setFontColor(Theme.TEXT_PRIMARY))
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(5));
//            }
//
//            for (Criterion criterion : criteria) {
//                int rating = task.getRatings().getOrDefault(criterion.getAbbreviation(), 0);
//                Color ratingColor = getRatingColor(rating);
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(String.valueOf(rating))
//                                .setFontSize(12)
//                                .setBold()
//                                .setFontColor(Theme.TEXT_PRIMARY))
//                        .setBackgroundColor(ratingColor)
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(5));
//            }
//
//            detailsCell.add(ratingsTable);
//            taskCard.addCell(detailsCell);
//
//            taskCard.addCell(new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setVerticalAlignment(VerticalAlignment.TOP));
//
//            document.add(taskCard);
//        }
//    }

//    private static void addWeekSection(Document document, Week week, List<Criterion> criteria) {
//        // Week header (unchanged)
//        Table weekHeader = new Table(1)
//                .setWidth(UnitValue.createPercentValue(100))
//                .setMarginTop(20);
//
//        weekHeader.addCell(new Cell()
//                .setBackgroundColor(Theme.SECONDARY)
//                .setBorderTopLeftRadius(new BorderRadius(4))
//                .setBorderTopRightRadius(new BorderRadius(4))
//                .setBorder(Border.NO_BORDER)
//                .setPadding(8)
//                .setPaddingLeft(16)
//                .add(new Paragraph()
//                        .add(new Text("Week " + week.getWeekNumber())
//                                .setFontSize(13)
//                                .setBold()
//                                .setFontColor(Theme.WHITE))
//                        .add(new Text("     ( " + week.getTasks().size() + " tasks )")
//                                .setFontSize(10)
//                                .setFontColor(Theme.WHITE))));
//
//        document.add(weekHeader);
//
//        // Tasks
//        for (Task task : week.getTasks()) {
//            Table taskCard = new Table(new float[]{7, 3})
//                    .setWidth(UnitValue.createPercentValue(100))
//                    .setMarginTop(10)
//                    .setBorder(Border.NO_BORDER)
//                    .setBorderLeft(new SolidBorder(Theme.SECONDARY, 2.5f))
//                    .setMarginLeft(5)
//                    .setMarginRight(5)
//                    .setBackgroundColor(Theme.BACKGROUND);
//
//            Cell detailsCell = new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setPaddingLeft(20)
//                    .setPaddingTop(6)
//                    .setPaddingBottom(3);
//
//            detailsCell.add(new Paragraph(task.getName())
//                    .setFontSize(13)
//                    .setBold()
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(6));
//
//            detailsCell.add(new Paragraph("Description: " + task.getDescription())
//                    .setFontSize(10)
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(6));
//
//            detailsCell.add(new Paragraph("Score: " + task.getScore() + "% (" + task.getPointsEarned() + "/" + task.getTotalPoints() + ")")
//                    .setFontSize(10)
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(8));
//
//            // Enhanced Ratings Table
//            Table ratingsTable = new Table(criteria.size())
//                    .setWidth(UnitValue.createPercentValue(100))
//                    .setBorderRadius(new BorderRadius(8)) // Rounded corners for the whole table
//                    .setMarginTop(5);
//
//            // Header row with abbreviations
//            for (Criterion criterion : criteria) {
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(criterion.getAbbreviation())
//                                .setFontSize(10)
//                                .setFontColor(Theme.TEXT_PRIMARY)
//                                .setBold())
//                        .setBackgroundColor(new DeviceRgb(240, 240, 240)) // Light gray background
//                        .setBorder(Border.NO_BORDER)
//                        .setBorderRadius(new BorderRadius(6)) // Rounded individual cells
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(6)
//                        .setMargin(2));
//            }
//
//            // Ratings row
//            for (Criterion criterion : criteria) {
//                int rating = task.getRatings().getOrDefault(criterion.getAbbreviation(), 0);
//                Color ratingColor = getRatingColor(rating);
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(String.valueOf(rating))
//                                .setFontSize(12)
//                                .setBold()
//                                .setFontColor(Theme.TEXT_PRIMARY))
//                        .setBackgroundColor(ratingColor)
//                        .setBorder(Border.NO_BORDER)
//                        .setBorderRadius(new BorderRadius(6)) // Rounded individual cells
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(6)
//                        .setMargin(2));
//            }
//
//            detailsCell.add(ratingsTable);
//            taskCard.addCell(detailsCell);
//
//            taskCard.addCell(new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setVerticalAlignment(VerticalAlignment.TOP));
//
//            document.add(taskCard);
//        }
//    }


//    private static void addWeekSection(Document document, Week week, List<Criterion> criteria) {
//        // Week header - Sleek and Modern
//        Table weekHeader = new Table(1)
//                .setWidth(UnitValue.createPercentValue(100))
//                .setMarginTop(15)
//                .setBackgroundColor(Theme.SECONDARY)
//                .setBorderTopLeftRadius(new BorderRadius(4))
//                .setBorderTopRightRadius(new BorderRadius(4))
//                .setPadding(10);
//
//        weekHeader.addCell(new Cell()
//                .setBorder(Border.NO_BORDER)
//                .setPaddingLeft(16)
//                .add(new Paragraph()
//                        .add(new Text("Week " + week.getWeekNumber())
//                                .setFontSize(16)
//                                .setBold()
//                                .setFontColor(Theme.WHITE))
//                        .add(new Text("  •  " + week.getTasks().size() + " Tasks")
//                                .setFontSize(12)
//                                .setFontColor(Theme.WHITE))
//                        .setTextAlignment(TextAlignment.LEFT)));
//
//        document.add(weekHeader);
//
//        // Tasks - Card-like Design with Big Marks
//        for (Task task : week.getTasks()) {
//            Table taskCard = new Table(new float[]{7, 3}) // Reversed layout: score first, details second
//                    .setWidth(UnitValue.createPercentValue(100))
//                    .setMarginTop(15)
//                    .setBorderRadius(new BorderRadius(12))
//                    .setBackgroundColor(Theme.BACKGROUND)
//                    .setPadding(10)
//                    .setBorder(new SolidBorder(new DeviceRgb(220, 220, 220), 1f)); // Subtle border
//
//            // Big Score Cell
//            Cell scoreCell = new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setPadding(10)
//                    .setBackgroundColor(new DeviceRgb(245, 245, 245)) // Light highlight
//                    .setBorderRadius(new BorderRadius(10));
//
//            scoreCell.add(new Paragraph(String.valueOf(task.getScore() + "%"))
//                    .setFontSize(24) // Big, bold score
//                    .setBold()
//                    .setFontColor(getScoreColor(task.getScore()))
//                    .setMarginBottom(4));
//            scoreCell.add(new Paragraph(task.getPointsEarned() + "/" + task.getTotalPoints())
//                    .setFontSize(12)
//                    .setFontColor(Theme.TEXT_SECONDARY));
//
//            taskCard.addCell(scoreCell);
//
//            // Details Cell
//            Cell detailsCell = new Cell()
//                    .setBorder(Border.NO_BORDER)
//                    .setPaddingLeft(15)
//                    .setPaddingTop(8)
//                    .setPaddingBottom(8);
//
//            detailsCell.add(new Paragraph(task.getName())
//                    .setFontSize(14)
//                    .setBold()
//                    .setFontColor(Theme.TEXT_PRIMARY)
//                    .setMarginBottom(6));
//
//            detailsCell.add(new Paragraph(task.getDescription())
//                    .setFontSize(11)
//                    .setFontColor(Theme.TEXT_SECONDARY)
//                    .setMarginBottom(4));
//
//            Table ratingsTable = new Table(criteria.size())
//                    .setWidth(UnitValue.createPercentValue(80))
//                    .setMarginTop(4);
//
//            for (Criterion criterion : criteria) {
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(criterion.getAbbreviation())
//                                .setFontSize(10)
//                                .setBold()
//                                .setFontColor(Theme.TEXT_SECONDARY))
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(3)
//                        .setBorder(Border.NO_BORDER));
//            }
//
//            for (Criterion criterion : criteria) {
//                int rating = task.getRatings().getOrDefault(criterion.getAbbreviation(), 0);
//                Color ratingColor = getRatingColor(rating);
//                ratingsTable.addCell(new Cell()
//                        .add(new Paragraph(String.valueOf(rating))
//                                .setFontSize(10)
//                                .setBold()
//                                .setFontColor(Theme.WHITE))
//                        .setBackgroundColor(ratingColor)
//                        .setBorderRadius(new BorderRadius(50)) // Circular cells
//                        .setWidth(UnitValue.createPointValue(15)) // Fixed width for circles
//                        .setHeight(UnitValue.createPointValue(15)) // Fixed height for circles
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setPadding(5)
//                        .setMargin(3));
//            }
//
//            detailsCell.add(ratingsTable);
//            taskCard.addCell(detailsCell);
//
//            document.add(taskCard);
//        }
//    }


    private static void addWeekSection(Document document, Week week, List<Criterion> criteria) {
        // Week Header - Enhanced with gradient and date context
        Table weekHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(5) // Increased for separation
                .setBackgroundColor(Theme.SECONDARY)
                .setBorderTopLeftRadius(new BorderRadius(6))
                .setBorderTopRightRadius(new BorderRadius(6))
                .setPadding(12)
                .setBorderBottom(new SolidBorder(Theme.TEXT_SECONDARY, 1f)); // Subtle underline effect

        weekHeader.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(20)
                .add(new Paragraph()
                        .add(new Text("Week " + week.getWeekNumber())
                                .setFontSize(18) // Slightly larger for prominence
                                .setBold()
                                .setFontColor(Theme.WHITE))
                        .add(new Text("  •  " + week.getTasks().size() + " Tasks")
                                .setFontSize(12)
                                .setFontColor(Theme.WHITE))
                        .add(new Text("  (Jan " + (week.getWeekNumber() * 7 - 6) + " - " + (week.getWeekNumber() * 7) + ", 2025)")
                                .setFontSize(10)
                                .setFontColor(Theme.TEXT_SECONDARY))
                        .setTextAlignment(TextAlignment.LEFT)));

        document.add(weekHeader);

        // Tasks - Enhanced Card-like Design
        for (Task task : week.getTasks()) {
            Table taskCard = new Table(new float[]{3, 7}) // Adjusted ratio for balance
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10)
                    .setBackgroundColor(Theme.BACKGROUND)
                    .setPadding(12)
                    .setBorder(new SolidBorder(new DeviceRgb(230, 230, 230), 0.5f));

            // Enhanced Score Cell with Fixed Alignment
            Cell scoreCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(12)
                    .setBackgroundColor(new DeviceRgb(250, 250, 250))
                    .setWidth(UnitValue.createPointValue(100)); // Fixed width for consistency

            // Inner table to stack score badge and points vertically
            Table scoreContent = new Table(1)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setBackgroundColor(new DeviceRgb(250, 250, 250)); // Match outer cell background

            // Score Badge
            Table scoreBadge = new Table(1)
                    .setWidth(UnitValue.createPointValue(60))
                    .setBackgroundColor(getScoreColor(task.getScore()))
                    .setBorderRadius(new BorderRadius(50))
                    .setPadding(8)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(6); // Space between badge and points

            scoreBadge.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(task.getScore() + "%")
                            .setFontSize(20)
                            .setBold()
                            .setFontColor(Theme.WHITE)
                            .setTextAlignment(TextAlignment.CENTER)));

            scoreContent.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(scoreBadge));

            // Points Text
            scoreContent.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(task.getPointsEarned() + "/" + task.getTotalPoints())
                            .setFontSize(11)
                            .setFontColor(Theme.TEXT_SECONDARY)
                            .setTextAlignment(TextAlignment.CENTER)));

            scoreCell.add(scoreContent);
            taskCard.addCell(scoreCell);

            // Enhanced Details Cell
            Cell detailsCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(20)
                    .setPaddingTop(10)
                    .setPaddingBottom(10)
                    .setBackgroundColor(Theme.WHITE); // Two-tone effect with score cell

            detailsCell.add(new Paragraph(task.getName())
                    .setFontSize(15) // Slightly larger for emphasis
                    .setBold()
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMarginBottom(6));

            // Description with label
            detailsCell.add(new Paragraph()
                    .add(new Text("Description: ").setBold().setFontColor(Theme.TEXT_SECONDARY))
                    .add(new Text(task.getDescription()).setFontColor(Theme.TEXT_PRIMARY))
                    .setFontSize(11)
                    .setMarginBottom(4));

            // Enhanced Ratings Table - Grid Layout
            Table ratingsTable = new Table(criteria.size())
                    .setWidth(UnitValue.createPercentValue(90))
                    .setMarginTop(0)
                    .setBorderRadius(new BorderRadius(6))
                    .setBackgroundColor(Theme.LIGHT_BLUE);

            // Header Row
            for (Criterion criterion : criteria) {
                ratingsTable.addCell(new Cell()
                        .add(new Paragraph(criterion.getAbbreviation())
                                .setFontSize(10)
                                .setBold()
                                .setFontColor(Theme.SECONDARY))
                        .setBackgroundColor(Theme.WHITE)
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
            }

            // Ratings Row
            for (Criterion criterion : criteria) {
                int rating = task.getRatings().getOrDefault(criterion.getAbbreviation(), 0);
                Color ratingColor = getRatingColor(rating);
                ratingsTable.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(rating))
                                .setFontSize(11)
                                .setBold()
                                .setFontColor(Theme.WHITE))
                        .setBackgroundColor(ratingColor)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5)
                        .setMargin(2));
            }

            detailsCell.add(ratingsTable);
            taskCard.addCell(detailsCell);

            document.add(taskCard);
        }
    }

    // Helper method for score color
    private static Color getScoreColor(int score) {
        if (score >= 80) return new DeviceRgb(46, 204, 113); // Green
        if (score >= 50) return new DeviceRgb(241, 196, 15); // Yellow
        return new DeviceRgb(231, 76, 60);                   // Red
    }

    private static Color getRatingColor(int rating) {
        switch (rating) {
            case 4:
                return Theme.RATING_EXCELLENT;
            case 3:
                return Theme.RATING_GOOD;
            case 2:
                return Theme.RATING_AVERAGE;
            default:
                return Theme.RATING_POOR;
        }
    }

    private static void addPerformanceSummary(Document document, StudentData studentData) {
        // Add spacing before the section
        document.add(new Paragraph(" ").setMarginTop(25)); // Slightly increased for better separation

        // Enhanced Summary Header with underline effect
        Table summaryHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(Theme.LIGHT_BLUE)
                .setBorderBottom(new SolidBorder(Theme.SECONDARY, 1.5f)) // Subtle underline
                .setMarginBottom(12); // Adjusted spacing after header

        summaryHeader.addCell(new Cell()
                .add(new Paragraph("Performance Summary")
                        .setFontSize(16) // Larger for emphasis
                        .setBold()
                        .setFontColor(Theme.SECONDARY) // Darker color for contrast
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setPadding(10));

        document.add(summaryHeader);

        // Enhanced Summary Table with card-like styling
        Table summaryTable = new Table(new float[]{3, 2}) // Adjusted ratio for better label-value balance
                .setWidth(UnitValue.createPercentValue(60)) // Wider for readability
                .setHorizontalAlignment(HorizontalAlignment.CENTER) // Centered on page
                .setBackgroundColor(Theme.BACKGROUND)
                .setBorder(new SolidBorder(Theme.TEXT_SECONDARY, 0.5f)) // Thin border for card effect
                .setPadding(6) // Inner padding for content
                .setMarginBottom(20); // Spacing after table

        addEnhancedSummaryRow(summaryTable, "Total Marks Earned",
                String.valueOf(studentData.getTotalMarksEarned()),
                Theme.BACKGROUND);

        addEnhancedSummaryRow(summaryTable, "Total Possible Marks",
                String.valueOf(studentData.getTotalPossibleMarks()),
                Theme.LIGHT_BLUE);

        summaryTable.addCell(new Cell()
                .add(new Paragraph("Overall Percentage")
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_SECONDARY))
                .setBorder(Border.NO_BORDER)
                .setPadding(6)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBackgroundColor(Theme.BACKGROUND));

        summaryTable.addCell(new Cell()
                .add(new Paragraph(studentData.getOverallPercentage() + "%")
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.SECONDARY)
                        .setBackgroundColor(studentData.getOverallPercentage() >= 80 ? Theme.GOOD_SCORE : Theme.AVERAGE_SCORE)
                        .setPadding(4)
                        .setBorderRadius(new BorderRadius(4)))
                .setBorder(Border.NO_BORDER)
                .setPadding(6)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBackgroundColor(Theme.BACKGROUND));

        document.add(summaryTable);
    }

    // Enhanced row method with alternating colors and better styling
    private static void addEnhancedSummaryRow(Table table, String label, String value, Color rowBackground) {
        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_SECONDARY)) // Lighter color for labels
                .setBorder(Border.NO_BORDER)
                .setPadding(6)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBackgroundColor(rowBackground));

        table.addCell(new Cell()
                .add(new Paragraph(value)
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER)
                .setPadding(6)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBackgroundColor(rowBackground));
    }


    private static void addSummaryRow(Table table, String label, String value) {
        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph(value)
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER));
    }

    @Data
    static class StudentData {
        private String studentName;
        private String studentId;
        private String projectName;
        private int overallPercentage;
        private List<Criterion> criteria;
        private List<Week> weeks;
        private int totalMarksEarned;
        private int totalPossibleMarks;
    }

    @Data
    static class Criterion {
        private String abbreviation;
        private String name;

        public Criterion(String abbreviation, String name) {
            this.abbreviation = abbreviation;
            this.name = name;
        }
    }

    @Data
    static class Week {
        private int weekNumber;
        private List<Task> tasks;

        public Week(int weekNumber) {
            this.weekNumber = weekNumber;
            this.tasks = new ArrayList<>();
        }

        public void addTask(Task task) {
            tasks.add(task);
        }
    }

    @Data
    static class Task {
        private String name;
        private String description;
        private int score;
        private int pointsEarned;
        private int totalPoints;
        private Map<String, Integer> ratings;

        public Task(String name, String description) {
            this.name = name;
            this.description = description;
            this.ratings = new HashMap<>();
        }
    }
}
