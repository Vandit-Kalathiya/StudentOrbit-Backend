package com.studentOrbit.generate_report_app.Pdf.NormalReport;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.*;
import com.studentOrbit.generate_report_app.Model.TaskData;
import com.studentOrbit.generate_report_app.entity.Attachment.Attachment;
import com.studentOrbit.generate_report_app.entity.Comment.Comment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class TaskCard {
    public static void add(Document document, TaskData task, boolean isLate, Map<String, List<Attachment>> attachmentMap, List<Comment> comments) {
        Table taskCard = new Table(1)
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

        Cell headerCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setPadding(10);

        Table titleRow = new Table(UnitValue.createPercentArray(new float[]{7, 3}));
        Cell titleCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(task.getTaskName())
                        .setBold()
                        .setFontSize(14)
                        .setFontColor(Theme.TEXT_PRIMARY));
        Cell statusCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .add(createStatusBadge(task.getStatus()));
        titleRow.addCell(titleCell);
        titleRow.addCell(statusCell);
        headerCell.add(titleRow);

        headerCell.add(new Paragraph("Due: " + formatDate(task.getDueDate()))
                .setFontSize(12)
                .setFontColor(Theme.TEXT_SECONDARY)
                .setMarginTop(5));

        List<Attachment> attachments = attachmentMap.getOrDefault(task.getTaskId(), List.of());
        if (!attachments.isEmpty()) {
            headerCell.add(new Paragraph(attachments.size() + " submissions")
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_SECONDARY)
                    .setMarginTop(5));
        }

        taskCard.addCell(headerCell);

        if (task.getCompletionDate() != null) {
            Cell submissionCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setPadding(10)
                    .setPaddingTop(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
            submissionCell.add(new Paragraph("Submitted: " + task.getCompletionDate().format(formatter))
                    .setFontSize(12)
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMarginBottom(10));

            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                submissionCell.add(new Paragraph("Description")
                        .setBold()
                        .setFontSize(12)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(5));
                submissionCell.add(new Paragraph(task.getDescription())
                        .setFontSize(12)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(10));
            }

            if (!attachments.isEmpty()) {
                submissionCell.add(new Paragraph("Files")
                        .setBold()
                        .setFontSize(12)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginBottom(5));

                for (Attachment file : attachments) {
                    Table fileRow = new Table(UnitValue.createPercentArray(new float[]{7, 2, 1}))
                            .setWidth(UnitValue.createPercentValue(100));

                    fileRow.addCell(new Cell().setBorder(Border.NO_BORDER)
                            .add(new Paragraph(file.getFileName())
                                    .setFontSize(12)
                                    .setFontColor(Theme.TEXT_PRIMARY)));

                    fileRow.addCell(new Cell().setBorder(Border.NO_BORDER)
                            .add(new Paragraph(formatFileSize(file.getSize()))
                                    .setFontSize(12)
                                    .setFontColor(Theme.TEXT_SECONDARY)));

                    fileRow.addCell(new Cell().setBorder(Border.NO_BORDER)
                            .add(new Paragraph("Download")
                                    .setAction(file.getDownloadUrl() != null ? PdfAction.createURI(file.getDownloadUrl()) : null)
                                    .setFontColor(new DeviceRgb(59, 130, 246))
                                    .setFontSize(12)));

                    submissionCell.add(fileRow);
                }
            }

            if (!comments.isEmpty()) {
                submissionCell.add(new Paragraph("Comments")
                        .setBold()
                        .setFontSize(12)
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setMarginTop(10)
                        .setMarginBottom(5));

                for (Comment comment : comments) {
                    submissionCell.add(new Paragraph(comment.getCommentDescription())
                            .setFontSize(12)
                            .setFontColor(Theme.TEXT_PRIMARY)
                            .setMarginBottom(5));
                }
            }

            taskCard.addCell(submissionCell);
        }

        document.add(taskCard);
    }

    private static String formatFileSize(Long size) {
        if (size == null) return "N/A";
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        else if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
        else return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }

    private static Table createStatusBadge(String status) {
        DeviceRgb[] colors = getStatusColors(status);
        Table badge = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginLeft(64)
                .setMarginTop(10)
                .setMarginRight(20)
                .setBorder(Border.NO_BORDER);

        Cell badgeCell = new Cell()
                .setBackgroundColor(colors[0])
                .setBorderRadius(new BorderRadius(4))
                .setPadding(4)
                .setBorder(Border.NO_BORDER)
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

    private static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "Not set";
    }
}
