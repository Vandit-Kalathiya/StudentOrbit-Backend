package com.studentOrbit.generate_report_app.Pdf.NormalReport;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

public class Header {
    public static void add(Document document, String reportType, String identifier, String groupId) {
        Table header = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBackgroundColor(Theme.WHITE)
                .setMarginTop(20.0f);

        Cell subjectCell = new Cell()
                .add(new Paragraph("CE396")
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY)
                        .setTextAlignment(TextAlignment.LEFT))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10f);

        Cell titleCell = new Cell()
                .add(new Paragraph("Project Progress Report")
                        .setFontSize(14)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10f);

        Cell infoCell = new Cell()
                .add(new Paragraph(reportType.equalsIgnoreCase("student") ? identifier : groupId)
                        .setFontSize(12)
                        .setBold()
                        .setFontColor(Theme.TEXT_PRIMARY))
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(10f);

        header.addCell(subjectCell);
        header.addCell(titleCell);
        header.addCell(infoCell);

        document.add(header);
        document.add(new Paragraph().setMarginBottom(30));
    }
}
