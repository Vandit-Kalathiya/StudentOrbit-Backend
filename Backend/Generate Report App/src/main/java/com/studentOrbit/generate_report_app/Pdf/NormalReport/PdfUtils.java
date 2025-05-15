package com.studentOrbit.generate_report_app.Pdf.NormalReport;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.*;
import com.studentOrbit.generate_report_app.Model.TaskData;
import com.studentOrbit.generate_report_app.Model.WeekData;
import com.studentOrbit.generate_report_app.entity.Groups.Group;
import com.studentOrbit.generate_report_app.entity.Student.Student;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PdfUtils {
    public static void addProjectInfo(Document document, Group group, List<Student> members) {
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorderRadius(new BorderRadius(5))
                .setBackgroundColor(Theme.BACKGROUND);

        infoTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
                .add(new Paragraph(group.getGroupName())
                        .setFontSize(14)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(10)
                        .setTextAlignment(TextAlignment.CENTER))
                .add(new Paragraph(group.getGroupDescription() != null ? group.getGroupDescription() : "No description")
                        .setFontSize(12)
                        .setMarginBottom(5)
                        .setTextAlignment(TextAlignment.CENTER)));

        Map<String, String> leftColumnData = new LinkedHashMap<>();
        leftColumnData.put("Mentor", group.getMentor() != null && group.getMentor().getName() != null ? group.getMentor().getName() : "N/A");
        leftColumnData.put("Members", members.stream().map(Student::getUsername).collect(Collectors.joining(", ")));
        leftColumnData.put("Group Leader", group.getGroupLeader() != null ? group.getGroupLeader() : "N/A");

        Map<String, String> rightColumnData = new LinkedHashMap<>();
        rightColumnData.put("Start Date", group.getStartDate() != null ? group.getStartDate() : "N/A");
        rightColumnData.put("Progress", "15%"); // Placeholder; calculate dynamically if possible
        rightColumnData.put("Status", group.getProjectStatus() != null ? group.getProjectStatus() : "N/A");

        Table twoColumnTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .setWidth(UnitValue.createPercentValue(100));

        Cell leftColumnCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        for (Map.Entry<String, String> entry : leftColumnData.entrySet()) {
            leftColumnCell.add(new Paragraph(entry.getKey() + ": " + entry.getValue())
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMargin(0)
                    .setTextAlignment(TextAlignment.LEFT));
        }
        twoColumnTable.addCell(leftColumnCell);

        Cell rightColumnCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        for (Map.Entry<String, String> entry : rightColumnData.entrySet()) {
            rightColumnCell.add(new Paragraph(entry.getKey() + ": " + entry.getValue())
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMargin(0)
                    .setTextAlignment(TextAlignment.LEFT));
        }
        twoColumnTable.addCell(rightColumnCell);

        infoTable.addCell(new Cell().setBorder(Border.NO_BORDER).add(twoColumnTable));
        document.add(infoTable);
        document.add(new Paragraph().setMarginBottom(10));
    }

    public static void addWeekSection(Document document, WeekData weekData, Map<String, List<com.studentOrbit.generate_report_app.entity.Attachment.Attachment>> attachmentMap, String reportType, String identifier) {
        Table weekHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(20);

        weekHeader.addCell(new Cell()
                .setBackgroundColor(Theme.SECONDARY)
                .setBorderTopLeftRadius(new BorderRadius(4))
                .setBorderTopRightRadius(new BorderRadius(4))
                .setBorder(Border.NO_BORDER)
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
                boolean isLate = isLate(task.getDueDate(), task.getCompletionDate());
                TaskCard.add(document, task, isLate, attachmentMap, task.getComments());
            }
        }

        if (reportType.equalsIgnoreCase("group")) {
            addGroupWeekStatistics(document, weekData);
        } else if (reportType.equalsIgnoreCase("student")) {
            addStudentWeekStatistics(document, weekData, identifier);
        }
    }

    private static boolean isLate(LocalDate dueDate, LocalDate completionDate) {
        if (dueDate == null || completionDate == null) return false;
        return completionDate.isAfter(dueDate);
    }

    private static void addGroupWeekStatistics(Document document, WeekData weekData) {
        Table statsTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10)
                .setBackgroundColor(Theme.BACKGROUND)
                .setBorderRadius(new BorderRadius(5));

        statsTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
                .add(new Paragraph("Week " + weekData.getWeekNumber() + " Statistics")
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)));

        Map<String, Long> tasksPerStudent = weekData.getTasks().stream()
                .flatMap(task -> task.getAssignees().stream())
                .collect(Collectors.groupingBy(
                        assignee -> assignee,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : tasksPerStudent.entrySet()) {
            statsTable.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setPadding(5)
                    .add(new Paragraph(entry.getKey() + ": " + entry.getValue() + " tasks")
                            .setFontSize(10)
                            .setFontColor(Theme.TEXT_PRIMARY)));
        }

        long lateTasks = weekData.getTasks().stream()
                .filter(task -> isLate(task.getDueDate(), task.getCompletionDate()))
                .count();

        statsTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .add(new Paragraph("Late Tasks: " + lateTasks)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY)));

        document.add(statsTable);
    }

    private static void addStudentWeekStatistics(Document document, WeekData weekData, String studentId) {
        Table statsTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(10)
                .setBackgroundColor(Theme.BACKGROUND)
                .setBorderRadius(new BorderRadius(5));

        statsTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(10)
                .add(new Paragraph("Week " + weekData.getWeekNumber() + " Statistics for " + studentId)
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)));

        long completedTasks = weekData.getTasks().stream()
                .filter(task -> task.getAssignees().contains(studentId) && "COMPLETED".equalsIgnoreCase(task.getStatus()))
                .count();

        long lateTasks = weekData.getTasks().stream()
                .filter(task -> task.getAssignees().contains(studentId) && isLate(task.getDueDate(), task.getCompletionDate()))
                .count();

        statsTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .add(new Paragraph("Completed Tasks: " + completedTasks)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY)));

        statsTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .add(new Paragraph("Late Tasks: " + lateTasks)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_PRIMARY)));

        document.add(statsTable);
    }
}
