package com.hotelworkforce.executive.util;

import com.hotelworkforce.executive.model.ChartPoint;
import java.util.List;

public final class Json {
    private Json() {}

    public static String chartPoints(List<ChartPoint> points) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) sb.append(',');
            ChartPoint p = points.get(i);
            sb.append("{\"label\":\"").append(escape(p.getLabel())).append("\",\"value\":").append(p.getValue()).append('}');
        }
        return sb.append(']').toString();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
