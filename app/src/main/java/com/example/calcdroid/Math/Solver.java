package com.example.calcdroid.Math;

import android.text.SpannableString;
import com.example.calcdroid.Math.Classifiers.Classifier;

import java.util.ArrayList;

public class Solver {
    MathFunction function;
    SpannableString result;
    String equation;
    Classifier clf;
    public Solver(String equation, ArrayList<Integer> subscriptIndices, ArrayList<Integer> superscriptIndices) {
        String[] sides = equation.split("=");
        if (sides.length > 1) {
            Parser parser = new Parser(sides[0]+"-("+sides[1]+")", subscriptIndices, superscriptIndices);
            function = parser.Parse();
            solveFor(function.getVariables().get(0));
        }
        else {
            Parser parser = new Parser(sides[0], subscriptIndices, superscriptIndices);
            function = parser.Parse();
            simplify();
        }
    }

    private void solveFor(String var) {
        if (clf != null) {
            equation = "";
            ArrayList<String> solutions = clf.solveFor(var);
            for (int i = 1; i <= solutions.size(); i++)
                equation += var + "_" + (i < 10? i :("("+ i + ")")) + ", ";
            result = setScripts(equation.substring(0, equation.length() - 2));
        }
        else
             result = SpannableString.valueOf("Такого решать не умею :(");
    }

    private SpannableString setScripts(String equation) {
        return null;
    }

    private void simplify() {
        //1. упростить константы
        //2. попытаться увидеть канонический вид
        //3. если увидели, разложить на множители если возможно
        //4. если ничего, раскрыть все скобки и вывести
        sumplifyConstants();
        if (tryToClassify()) {

        }
        else {
            expandBrackets();
            sumplifyConstants();
        }
    }

    private void expandBrackets() {

    }

    private boolean tryToClassify() {
        return false;
    }

    private void sumplifyConstants() {
        equation = RPN_Converter.simplifyConstants(function.getPostfixNotation());
    }

    public SpannableString getResult() {
        return result;
    }
}
