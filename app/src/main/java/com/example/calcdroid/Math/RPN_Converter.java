package com.example.calcdroid.Math;

import android.os.Build;

import java.security.InvalidParameterException;
import java.util.*;

public class RPN_Converter {

     private static Map<String, Integer> operationCosts = new HashMap<>();
    static {
        operationCosts.put("ln", 5);
        operationCosts.put("cos",5);
        operationCosts.put("sin",5);
        operationCosts.put("tg",5);
        operationCosts.put("ctg",5);
        operationCosts.put("√", 5);
        operationCosts.put("d/dx", 5);
        operationCosts.put("^",4);
        operationCosts.put("(", 0);
        operationCosts.put("*",3);
        operationCosts.put("/",3);
        operationCosts.put("+", 2);
        operationCosts.put("-", 2);
        operationCosts.put("~", 4);
    }

    private String infix, postfix;
    private ArrayList<String> variables = new ArrayList<>();

    public RPN_Converter(String infix) {
        this.infix = infix;
        Convert();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            variables.sort(null);
        }
    }

    private void Convert() {
        Stack<String> operators = new Stack<>();
        String result = new String();
        for (int i = 0; i < infix.length(); i++) {
            boolean wasOperator = false;
            for (int j = 1; j < Math.min(5, infix.length() - i - j + 1); j++) {
                String operation = infix.substring(i, i+j);
                wasOperator = operationCosts.containsKey(operation);
                if (wasOperator) {
                    if (operation.equals("-") && (i == 0 || operationCosts.containsKey(infix.charAt(i-1))))
                        operation = "~";
                    if (operators.empty() || operationCosts.get(operation) > operationCosts.get(operators.peek()) ||
                    operation.equals("(")) {
                        operators.push(operation);
                        i+=operation.length() - 1;
                    }
                    else {
                        result += operators.pop() + " ";
                        i--;
                    }
                    break;
                }
            }
            Character curChar = infix.charAt(i);
            if (!wasOperator) {
                String operand = (Calculator.numbers.contains(curChar.toString()))?getConstant(i):
                        (Calculator.letters.contains(curChar.toString()))?getIdentifier(i): "";
                i += operand.length()>0?operand.length() - 1:0;
                result += operand + " ";
            }
            if (curChar == ')') {
                try {
                    while (!operators.peek().equals("("))
                        result += operators.pop() + " ";
                    operators.pop();
                }
                catch (EmptyStackException e) {
                    postfix = "Не все открывающие скобки закрыты";
                }
            }
        }
        while (!operators.empty())
            result += operators.pop() + " ";
        postfix = result;
    }

    public Double Evaluate(ArrayList<Double> args) {
        //if (args.size() > variables.size())
        //    throw new InvalidParameterException("Функция " + infix + " не принимает " + variables.size() + " аргументов");
        String postfix_temp = postfix;
        for (int i = 0; i < Math.min(args.size(), variables.size()); i++)
            postfix_temp = postfix_temp.replace(variables.get(i), String.valueOf(args.get(i)));
        Stack<String> operands = new Stack<>();
        ArrayList<String> components = new ArrayList<>();
        for (String component: postfix_temp.split(" "))
            if (!component.equals(""))
                components.add(component);
        for (String component: components) {
            if (operationCosts.containsKey(component))
                operands.push(Calculator.Calculate(operands.pop(), Calculator.isUnary(component)?null:operands.pop(),
                        component));
            else
                operands.push(component);
        }
        return Calculator.parseToDouble(operands.pop());
    }

    public static String simplifyConstants(String postfix) {
        String res = new String();
        Stack<String> operands = new Stack<>(),
        values = new Stack<>();
        ArrayList<String> components = new ArrayList<>();

        for (String component: postfix.split(" "))
            if (!component.equals(""))
                components.add(component);

        for (String component: components) {
            if (operationCosts.containsKey(component)) {
                operands.push(Calculator.CalculateInString(operands.pop(), Calculator.isUnary(component)?null:operands.pop(),
                        component));
            }
            else if (Calculator.isIdentifier(component)) {

            }

            else
                operands.push(component);
        }
        return res;
    }

    public static String toInfix(String postfix) {
        return null;
    }

    private String getConstant(int index) {
        String constant = new String();
        for (int i = index; i < infix.length() && Calculator.numbers.contains(((Character)infix.charAt(i)).toString()); i++)
            constant += infix.charAt(i);
        return constant;
    }

    private String getIdentifier(int index) {
        String identifier = new String();
        if (Calculator.letters.contains(((Character)infix.charAt(index)).toString()))
            identifier += infix.charAt(index);
        if (index + 1 < infix.length() && infix.charAt(index+1) == '_') {
            identifier += '_';
            if (index+2 < infix.length()) {
                if (infix.charAt(index+2) == '(') {
                    for (int i = index+2; i < infix.length() && infix.charAt(i) != ')'; i++)
                        identifier += infix.charAt(i);
                    if (index + identifier.length() < infix.length())
                        identifier += ')';
                }
                else
                    identifier += infix.charAt(index+2);
            }
        }
        variables.add(identifier);
        return identifier;
    }

    public String getPostfix() {
        return postfix;
    }

    public ArrayList<String> getVariables() {
        return variables;
    }
}
