package com.studentOrbit.generate_report_app.Pdf.NormalReport;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FooterHandler implements IEventHandler {
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
                .add(new Paragraph("Generated At: " + LocalDateTime.now().format(formatter))
                        .setFontColor(Theme.SECONDARY)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT)));

        footerTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph("StudentOrbit")
                        .setFontColor(Theme.SECONDARY)
                        .setBold()
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER)));

        footerTable.addCell(new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(String.valueOf(currentPageNumber))
                        .setFontColor(Theme.SECONDARY)
                        .setFontSize(10)
                        .setMarginRight(10)
                        .setTextAlignment(TextAlignment.RIGHT)));

        footerTable.setFixedPosition(
                leftMargin,
                20,
                pageSize.getWidth() - leftMargin - rightMargin
        );

        canvas.add(lineTable);
        canvas.add(footerTable);
        canvas.close();
    }
}
