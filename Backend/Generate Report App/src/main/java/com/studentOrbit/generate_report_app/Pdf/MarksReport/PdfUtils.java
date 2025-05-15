package com.studentOrbit.generate_report_app.Pdf.MarksReport;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.*;
import com.studentOrbit.generate_report_app.Model.*;

import java.util.List;

public class PdfUtils {
    public static void addHeader(Document document, StudentData studentData) {
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

    public static void addStudentOrGroupInfo(Document document, StudentData studentData) {
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

    public static void addCriteriaLegend(Document document, List<Criterion> criteria) {
        Table legendTable = new Table(criteria.size())
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(5)
                .setMarginBottom(5)
                .setBorder(new SolidBorder(Theme.TEXT_SECONDARY, 0.5f))
                .setBorderRadius(new BorderRadius(6))
                .setBackgroundColor(Theme.BACKGROUND);

        int index = 0;
        for (Criterion criterion : criteria) {
            Color cellBackground = (index % 2 == 0) ? Theme.LIGHT_BLUE : Theme.WHITE;

            Cell cell = new Cell()
                    .add(new Paragraph()
                            .add(new Text(criterion.getAbbreviation() + ": ")
                                    .setBold()
                                    .setFontSize(12)
                                    .setFontColor(Theme.SECONDARY))
                            .add(new Text(criterion.getName())
                                    .setFontSize(10)
                                    .setFontColor(Theme.TEXT_PRIMARY)))
                    .setBackgroundColor(cellBackground)
                    .setBorder(Border.NO_BORDER)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);

            legendTable.addCell(cell);
            index++;
        }

        document.add(new Paragraph("Evaluation Criteria")
                .setFontSize(14)
                .setBold()
                .setFontColor(Theme.SECONDARY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5)
                .setMarginBottom(5));

        document.add(legendTable);
        document.add(new Paragraph(" ").setMarginBottom(15));
    }

    public static void addWeekSection(Document document, Week week, List<Criterion> criteria) {
        Table weekHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setMarginTop(5)
                .setBackgroundColor(Theme.SECONDARY)
                .setBorderTopLeftRadius(new BorderRadius(6))
                .setBorderTopRightRadius(new BorderRadius(6))
                .setPadding(12)
                .setBorderBottom(new SolidBorder(Theme.TEXT_SECONDARY, 1f));

        weekHeader.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .setPaddingLeft(20)
                .add(new Paragraph()
                        .add(new Text("Week " + week.getWeekNumber())
                                .setFontSize(18)
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

        for (Task task : week.getTasks()) {
            Table taskCard = new Table(new float[]{3, 7})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(10)
                    .setBackgroundColor(Theme.BACKGROUND)
                    .setPadding(12)
                    .setBorder(new SolidBorder(new com.itextpdf.kernel.colors.DeviceRgb(230, 230, 230), 0.5f));

            Cell scoreCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(12)
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250))
                    .setWidth(UnitValue.createPointValue(100));

            Table scoreContent = new Table(1)
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(250, 250, 250));

            Table scoreBadge = new Table(1)
                    .setWidth(UnitValue.createPointValue(60))
                    .setBackgroundColor(getScoreColor(task.getScore()))
                    .setBorderRadius(new BorderRadius(50))
                    .setPadding(8)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginBottom(6);

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

            scoreContent.addCell(new Cell()
                    .setBorder(Border.NO_BORDER)
                    .add(new Paragraph(task.getPointsEarned() + "/" + task.getTotalPoints())
                            .setFontSize(11)
                            .setFontColor(Theme.TEXT_SECONDARY)
                            .setTextAlignment(TextAlignment.CENTER)));

            scoreCell.add(scoreContent);
            taskCard.addCell(scoreCell);

            Cell detailsCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setPaddingLeft(20)
                    .setPaddingTop(10)
                    .setPaddingBottom(10)
                    .setBackgroundColor(Theme.WHITE);

            detailsCell.add(new Paragraph(task.getName())
                    .setFontSize(15)
                    .setBold()
                    .setFontColor(Theme.TEXT_PRIMARY)
                    .setMarginBottom(6));

            detailsCell.add(new Paragraph()
                    .add(new Text("Description: ").setBold().setFontColor(Theme.TEXT_SECONDARY))
                    .add(new Text(task.getDescription()).setFontColor(Theme.TEXT_PRIMARY))
                    .setFontSize(11)
                    .setMarginBottom(4));

            Table ratingsTable = new Table(criteria.size())
                    .setWidth(UnitValue.createPercentValue(90))
                    .setMarginTop(0)
                    .setBorderRadius(new BorderRadius(6))
                    .setBackgroundColor(Theme.LIGHT_BLUE);

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

    public static Color getScoreColor(int score) {
        if (score >= 80) return new com.itextpdf.kernel.colors.DeviceRgb(46, 204, 113);
        if (score >= 50) return new com.itextpdf.kernel.colors.DeviceRgb(241, 196, 15);
        return new com.itextpdf.kernel.colors.DeviceRgb(231, 76, 60);
    }

    public static Color getRatingColor(int rating) {
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

    public static void addPerformanceSummary(Document document, StudentData studentData) {
        document.add(new Paragraph(" ").setMarginTop(25));

        Table summaryHeader = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(Theme.LIGHT_BLUE)
                .setBorderBottom(new SolidBorder(Theme.SECONDARY, 1.5f))
                .setMarginBottom(12);

        summaryHeader.addCell(new Cell()
                .add(new Paragraph("Performance Summary")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(Theme.SECONDARY)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBorder(Border.NO_BORDER)
                .setPadding(10));

        document.add(summaryHeader);

        Table summaryTable = new Table(new float[]{3, 2})
                .setWidth(UnitValue.createPercentValue(60))
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setBackgroundColor(Theme.BACKGROUND)
                .setBorder(new SolidBorder(Theme.TEXT_SECONDARY, 0.5f))
                .setPadding(6)
                .setMarginBottom(20);

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

    public static void addEnhancedSummaryRow(Table table, String label, String value, Color rowBackground) {
        table.addCell(new Cell()
                .add(new Paragraph(label)
                        .setFontSize(10)
                        .setFontColor(Theme.TEXT_SECONDARY))
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
}