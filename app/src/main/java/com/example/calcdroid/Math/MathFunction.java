package com.example.calcdroid.Math;

import android.os.Build;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

public class MathFunction {
    private final ArrayList<String> variables;
    private final String postfix;
    String representation;
    Function<ArrayList<Double>, Double> function;
    Function<ArrayList<String>, String> functionInStrings;

    public MathFunction(String representation, String postfix,
                        Function<ArrayList<Double>, Double> function, ArrayList<String> variables) {
        this.representation = representation;
        this.variables = variables;
        this.function = function;
        this.postfix = postfix;
    }

    public Double Evaluate(ArrayList<Double> args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return function.apply(args);
        }
        return Double.NaN;
    }

    public String EvaluateInStrings(ArrayList<String> args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return functionInStrings.apply(args);
        }
        return "";
    }

    public LineDataSet getDataset(double leftBorder, double rightBorder, boolean inverse) {
        ArrayList<Entry> values = new ArrayList<>();
        for (double var = leftBorder; var <= rightBorder; var+=0.01) {
            ArrayList<Double> args = new ArrayList<>(); args.add(var);
            float value = (float)(double)Evaluate(args);
            if (!Float.isNaN(value
            ))
                values.add(inverse?new Entry(value, (float)var)
                        : new Entry((float)var, value));
        }
        if (inverse)
            Collections.sort(values, new EntryXComparator());
        LineDataSet dataSet = new LineDataSet(values, representation);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawCircles(false);
        return dataSet;
    }

    public String getPostfixNotation() {
        return postfix;
    }

    public ArrayList<String> getVariables() {
        return variables;
    }

    @Override
    public String toString() {
        return representation;
    }
}
