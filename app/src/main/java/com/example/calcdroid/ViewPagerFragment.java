package com.example.calcdroid;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.calcdroid.Math.MathFunction;
import com.example.calcdroid.Math.Parser;
import com.example.calcdroid.Math.Solver;
import com.example.calcdroid.inputs.MathKeyboard;
import com.example.calcdroid.outputs.GraphChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.*;

public class ViewPagerFragment extends Fragment {

    public ViewPagerFragment() {}
    public ViewPagerFragment(int id) {layoutId = id;}
    int layoutId;
    View buttonView;
    Map<EditText, LineDataSet> inputToGraph;
    GraphChart chart;
    MathKeyboard keyboard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(layoutId, container, false);
        keyboard = new MathKeyboard(getContext());
        registerKeyboard(keyboard, (ViewGroup) layout);

        if (layoutId == R.layout.equations_tab)
            registerSolver(layout);
        else if (layoutId == R.layout.graphs_tab) {
            buttonView = inflater.inflate(R.layout.table_button, null);
            chart = layout.findViewById(R.id.graphChart);
            inputToGraph = new HashMap<>();
            registerGraphInput(layout);
        }
        return layout;
    }

    private void registerGraphInput(View layout) {
        TableLayout editTexts = layout.findViewById(R.id.edittexts);
        editTexts.setBackgroundColor(Color.GRAY);

        Button addBtn = buttonView.findViewById(R.id.addButton);
        addBtn.setOnClickListener(v -> {
            View inputView = LayoutInflater.from(getContext()).inflate(R.layout.edittext, null);

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            EditText inputField = inputView.findViewById(R.id.graph_input),
            leftBorderInput = inputView.findViewById(R.id.left_border),
            rightBorderInput = inputView.findViewById(R.id.right_border);
            Button deleteButton = inputView.findViewById(R.id.delete_btn);

            deleteButton.setOnClickListener(btn -> {
                editTexts.removeView(inputView);
                inputToGraph.remove(inputField);
                updateGraph();
            });

            setListener(color, inputField, leftBorderInput, rightBorderInput);
            setListenerForBorder(leftBorderInput, inputField);
            setListenerForBorder(rightBorderInput, inputField);

            keyboard.connectToEditText(inputField);
            keyboard.connectToEditText(leftBorderInput);
            keyboard.connectToEditText(rightBorderInput);


            editTexts.removeView(buttonView);
            editTexts.addView(inputView);
            editTexts.addView(buttonView);
        });
        editTexts.addView(buttonView);
    }

    private void updateGraph() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (Map.Entry<EditText, LineDataSet> entry:inputToGraph.entrySet())
            if (!dataSets.contains(entry.getValue())) {
                dataSets.add(entry.getValue());
            }
        if (dataSets.size() == 0)
            chart.clear();
        else {
            chart.setData(new LineData(dataSets));
            chart.invalidate();
            chart.zoomToCenter();
        }
    }

    private void setListener(int color, EditText inputField, EditText leftBorderInput, EditText rightBorderInput) {
        inputField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("NewApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Boolean yIsIndependent = s.toString().endsWith("=x") || s.toString().startsWith("x="),
                        xIsIndependent = s.toString().endsWith("=y") || s.toString().startsWith("y=");
                Parser parser = new Parser(s.toString(), keyboard.getSubscriptIndices(inputField),
                        keyboard.getSuperscriptIndices(inputField)),
                    leftBorderParser = new Parser(leftBorderInput.getText().toString(), keyboard.getSubscriptIndices(leftBorderInput),
                            keyboard.getSuperscriptIndices(leftBorderInput)),
                    rightBorderParser = new Parser(rightBorderInput.getText().toString(), keyboard.getSubscriptIndices(rightBorderInput),
                            keyboard.getSuperscriptIndices(rightBorderInput));

                String[] sides = parser.getInfix().split("=");
                String functionString = sides.length > 1 && sides[1].length() >= sides[0].length()?sides[1]:sides[0];
                Parser eqParser = new Parser(functionString, null, null);
                if (eqParser.isCorrect() && leftBorderParser.isCorrect() && rightBorderParser.isCorrect()
                        && (xIsIndependent || yIsIndependent)) {

                    MathFunction function = eqParser.Parse();
                    MathFunction leftBorderFunction = leftBorderParser.Parse();
                    double leftBorder = leftBorderFunction.getVariables().size() == 0 && leftBorderFunction.getPostfixNotation().length()!=0
                            ?leftBorderFunction.Evaluate(new ArrayList<>()):-100;
                    MathFunction rightBorderFunction = rightBorderParser.Parse();
                    double rightBorder = rightBorderFunction.getVariables().size() == 0 && rightBorderFunction.getPostfixNotation().length()!=0?
                            rightBorderFunction.Evaluate(new ArrayList<>()):100;

                    LineDataSet dataSet = function.getDataset(leftBorder, rightBorder, yIsIndependent);
                    dataSet.setColor(color);

                    if (inputToGraph.containsKey(inputField))
                        inputToGraph.replace(inputField, dataSet);
                    else
                        inputToGraph.put(inputField, dataSet);
                    updateGraph();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setListenerForBorder(EditText t, EditText parent) {
        t.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                parent.setText(parent.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void registerSolver(View layout) {
        EditText eqInput = layout.findViewById(R.id.eqInput);
        eqInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView resultLabel = layout.findViewById(R.id.eqOutput);
                try {
                    Solver solver = new Solver(s.toString(), keyboard.getSubscriptIndices(eqInput),
                            keyboard.getSuperscriptIndices(eqInput));
                    resultLabel.setText(solver.getResult());
                }
               catch (Exception e) {
                    resultLabel.setText("...");
               }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void registerKeyboard(MathKeyboard keyboard, ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (layout.getChildAt(i) instanceof EditText)
                keyboard.connectToEditText((EditText) layout.getChildAt(i));
        }
    }
}
