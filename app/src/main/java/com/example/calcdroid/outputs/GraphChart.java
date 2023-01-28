package com.example.calcdroid.outputs;

import android.content.Context;
import android.graphics.Color;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;

public class GraphChart extends LineChart {
    float minX, minY, maxX, maxY;
    public GraphChart(Context context) {
        this(context, null);
    }

    public GraphChart(Context context, AttributeSet set) {
        super(context, set);
        minX = -5; minY = -5; maxX = 5; maxY = 5;
        setSettings();

        drawAxis();
    }

    private void drawAxis() {
        LimitLine xLimitLine = new LimitLine(0f),
        yLimitLine = new LimitLine(0f);
        xLimitLine.setLineColor(Color.BLUE);
        xLimitLine.setLineWidth(2);
        yLimitLine.setLineColor(Color.BLUE);
        yLimitLine.setLineWidth(2);
        getXAxis().addLimitLine(xLimitLine);
        getAxisLeft().addLimitLine(yLimitLine);
    }

    void setSettings() {
        setTouchEnabled(true);
        setDragEnabled(true);
        setScaleEnabled(true);
        setDoubleTapToZoomEnabled(true);
        setClickable(true);

        getXAxis().setGridLineWidth(1.5f);
        getAxis(YAxis.AxisDependency.LEFT).setGridLineWidth(1.5f);
    }

    public void zoomToCenter() {
        maxX = getHighestVisibleX();
        minX = getLowestVisibleX();
        maxY = getYMax();
        minY = getYMin();


        resetViewPortOffsets();
        setVisibleXRange(1, 10);
        setVisibleYRange(1, 10, YAxis.AxisDependency.LEFT);
        centerViewTo(0, 0, YAxis.AxisDependency.LEFT);
    }

}
