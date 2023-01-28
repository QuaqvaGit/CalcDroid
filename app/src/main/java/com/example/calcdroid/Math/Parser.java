package com.example.calcdroid.Math;

import android.annotation.SuppressLint;
import android.os.Build;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.function.Function;

public class Parser {
    String function;
    ArrayList<Integer> superscripts;
    public Parser(String function, ArrayList<Integer> subscriptIndices, ArrayList<Integer> superscriptIndices) {
        this.function = function;
        superscripts = superscriptIndices;
        if (subscriptIndices!= null && !subscriptIndices.isEmpty())
            replaceScripts(subscriptIndices, "_");
        if (superscriptIndices != null && !superscriptIndices.isEmpty())
            replaceScripts(superscripts, "^");
        insertMultiply();
    }

    public MathFunction Parse() {
        RPN_Converter converter = new RPN_Converter(function);
        return new MathFunction(function, converter.getPostfix(), args -> converter.Evaluate(args), converter.getVariables());
    }

    @SuppressLint("NewApi")
    private @NotNull ArrayList<ArrayList<Integer>> getInGroups(@NotNull ArrayList<Integer> list) {
        int groupInd = 0;
        list.sort(null);
        ArrayList<ArrayList<Integer>> groupedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (groupedList.size() < groupInd+1)
                groupedList.add(new ArrayList<>());
            groupedList.get(groupInd).add(list.get(i));
            if (i+1 < list.size() && list.get(i) + 1 != list.get(i + 1))
                groupInd++;
        }
        return groupedList;
    }

    private void replaceScripts(ArrayList<Integer> scriptIndices, String replaced) {
        for (ArrayList<Integer> group: getInGroups(scriptIndices)) {
                function = group.size()>1? function.substring(0, group.get(0)) + replaced + "("
                        + function.substring(group.get(0), group.get(group.size()-1)+1) + ")"
                        + function.substring(group.get(group.size()-1)+1):
                        function.substring(0, group.get(0))+replaced+function.substring(group.get(0));
                if (replaced.equals("_"))
                    for (int i = 0; i < superscripts.size(); i++)
                        if (superscripts.get(i) > group.get(group.size() - 1))
                            superscripts.set(i, superscripts.get(i) + group.size() > 1?replaced.length()+2:replaced.length());
        }
    }

    public boolean isCorrect() {
        //скобки закрыты, нету пустых скобок () нет пустых операций типа ^ _
        int balance = 0;
        for (int i = 0; i < function.length(); i++) {
            String curChar = ((Character)function.charAt(i)).toString();
            balance = curChar.equals("(")?balance+1:curChar.equals(")")?balance-1:balance;
        }
        boolean isEvaluatable = balance == 0 && !function.contains("()") &&
                !function.contains("^=") && !function.contains("(=");
        if (isEvaluatable) {
            try {
                ArrayList<Double> args = new ArrayList<>();
                args.add(0d);
                Parse().Evaluate(args);
            }
            catch (EmptyStackException | NumberFormatException e) {
                isEvaluatable = false;
            }
        }

        return isEvaluatable || function.equals("");
    }

    private void insertMultiply() {
        function = function.replace('×','*').replace('÷', '/');
        //буква-буква(не функция) и цифра-буква
        for (int i = 0; i < function.length() - 1; i++) {
            String current = ((Character)function.charAt(i)).toString(),
                    next = ((Character)function.charAt(i+1)).toString();
            if ((Calculator.numbers.contains(current) && Calculator.letters.contains(next)) ||
                    (Calculator.letters.contains(current) && Calculator.numbers.contains(next)) ||
                    (Calculator.letters.contains(current) && Calculator.letters.contains(next) && !Calculator.inAFunction(current+next)) ||
                    (current.equals(")") && next.equals("(")) ||
                    (Calculator.numbers.contains(current) && next.equals("("))
            )
                function = function.substring(0, i+1) + "*" + function.substring(i+1);
        }
    }

    public String getInfix() {
        return function;
    }
}
