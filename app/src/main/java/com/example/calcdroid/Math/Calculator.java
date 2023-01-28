package com.example.calcdroid.Math;

public class Calculator {
    static String[] functions = new String[] {"ln", "cos", "sin", "tg", "ctg", "d/dx", "√", "~"};
    static String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZπ";
    static String numbers = "0123456789πe";
    public static Boolean isUnary(String operation) {
        for (String function: functions)
            if (function.equals(operation))
                return true;
        return false;
    }

    public static boolean isConstant(String constant) {
        for (int i = 0; i < constant.length(); i++)
            if (!numbers.contains(((Character)constant.charAt(i)).toString()))
                return false;
        return true;
    }

    public static boolean isIdentifier(String identifier) {
        return letters.contains(((Character)identifier.charAt(0)).toString());
    }

    public static Boolean inAFunction(String s) {
        for (String function: functions)
            if (function.contains(s))
                return true;
        return false;
    }

    public static String Calculate(String operand1, String operand2, String function) {
        Double num1 = parseToDouble(operand1),
                num2 = parseToDouble(operand2);
        Double result = Double.valueOf(0);
        switch (function) {
            case "+": {
                result = num1 + num2;
                break;
            }
            case "-": {
                result = num2 - num1;
                break;
            }
            case "~": {
                result = -num1;
                break;
            }
            case "*": {
                result = num1 * num2;
                break;
            }
            case "/": {
                result = num2/num1;
                break;
            }
            case "^": {
                result = Math.pow(num2, num1);
                break;
            }
            case "ln": {
                result = Math.log(num1);
                break;
            }
            case "cos": {
                result = Math.cos(num1);
                break;
            }
            case "sin": {
                result = Math.sin(num1);
                break;
            }
            case "tg": {
                result = Math.tan(num1);
                break;
            }
            case "ctg": {
                result = 1/Math.tan(num1);
                break;
            }
            case "√": {
                result = Math.sqrt(num1);
                break;
            }
        }
        return String.valueOf(result);
    }

    public static String CalculateInString(String operand1, String operand2, String function) {

        return "";
    }

    public static Double parseToDouble(String operand) {
        if (operand == null)
            return Double.NaN;
        else if (operand.equals("π"))
            return Math.PI;
        else if (operand.equals("e"))
            return Math.E;
        else
            return Double.parseDouble(operand);
    }
}
