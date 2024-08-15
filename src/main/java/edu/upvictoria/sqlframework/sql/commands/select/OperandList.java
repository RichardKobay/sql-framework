package edu.upvictoria.sqlframework.sql.commands.select;

import java.util.List;

public class OperandList {

    private static boolean isNull (String op1, String op2) {
        return op1 == null || op2 == null || op1.isEmpty() || op1.equals("null") || op2.equals("null") || op2.isEmpty();
    }

    protected static void greaterThan (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }

            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 > op2));
        }
    }

    protected static void and(List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }

            boolean op1;
            boolean op2;

            try {
                op1 = Boolean.parseBoolean(opColumn1.get(i));
                op2 = Boolean.parseBoolean(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 && op2));
        }
    }

    protected static void or(List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }

            boolean op1;
            boolean op2;

            try {
                op1 = Boolean.parseBoolean(opColumn1.get(i));
                op2 = Boolean.parseBoolean(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 || op2));
        }
    }

    protected static void equals (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            res.add(String.valueOf(opColumn1.get(i).equals(opColumn2.get(i))));
        }
    }

    protected static void greaterOrEqualsThan (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 >= op2));
        }
    }

    protected static void lowerOrEqualsThan (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 <= op2));
        }
    }

    protected static void lowerThan (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 < op2));
        }
    }

    protected static void plus (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 + op2));
        }
    }

    protected static void substract (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 - op2));
        }
    }

    protected static void multiply (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 * op2));
        }
    }

    protected static void divide (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 / op2));
        }
    }
    protected static void mod (List<String> res, List<String> opColumn1, List<String> opColumn2) {
        for (int i = 0; i < opColumn1.size(); i++) {
            if (isNull(opColumn1.get(i), opColumn2.get(i))) {
                res.add("false");
                continue;
            }
            double op1;
            double op2;

            try {
                op1 = Double.parseDouble(opColumn1.get(i));
                op2 = Double.parseDouble(opColumn2.get(i));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }

            res.add(String.valueOf(op1 % op2));
        }
    }
}
