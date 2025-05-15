package com.studentOrbit.generate_report_app.Pdf.NormalReport;

import com.itextpdf.kernel.colors.DeviceRgb;

public class Theme {
    public static final DeviceRgb SECONDARY = new DeviceRgb(44, 62, 80);
    public static final DeviceRgb BACKGROUND = new DeviceRgb(247, 250, 252);
    public static final DeviceRgb TEXT_PRIMARY = new DeviceRgb(45, 55, 72);
    public static final DeviceRgb TEXT_SECONDARY = new DeviceRgb(113, 128, 150);
    public static final DeviceRgb WHITE = new DeviceRgb(255, 255, 255);

    public static final DeviceRgb[] COMPLETED = {
            new DeviceRgb(198, 246, 213),
            new DeviceRgb(39, 103, 73)
    };

    public static final DeviceRgb[] IN_PROGRESS = {
            new DeviceRgb(190, 227, 248),
            new DeviceRgb(44, 82, 130)
    };

    public static final DeviceRgb[] PENDING = {
            new DeviceRgb(254, 235, 200),
            new DeviceRgb(146, 64, 14)
    };
}
