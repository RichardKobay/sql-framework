package edu.upvictoria.sqlframework.sql.commands.select;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.utils.XMLUtil;
import edu.upvictoria.sqlframework.exceptions.CsvElementDoesNotExistsException;

import java.util.*;

public class Where {

    public static List<Boolean> where(LinkedHashMap<String, List<String>> table, String query) throws CsvElementDoesNotExistsException, NumberFormatException, ElementDoesNotExistsException {
        if (query.isEmpty())
            return allColumns(table);

        List<Boolean> result = new ArrayList<>();
        checkClause(table, query);
        for (String res : table.get("__temp__")) {
            try {
                result.add(Boolean.parseBoolean(res));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The were clause is not valid");
            }
        }
        table.remove("__temp__");
        return result;
    }

    public static List<String> whereSelect(LinkedHashMap<String, List<String>> table, String query) throws CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        List<String> result = new ArrayList<>();
        checkClause(table, query);

        for (String res : table.get("__temp__")) {
            result.add(res);
        }
        table.remove("__temp__");

        return result;
    }

    public static List<Boolean> allColumns(LinkedHashMap<String, List<String>> table) throws CsvElementDoesNotExistsException {
        List<Boolean> result = new ArrayList<>();
        Iterator<Map.Entry<String, List<String>>> iterator = table.entrySet().iterator();

        if (!iterator.hasNext())
            throw new CsvElementDoesNotExistsException("The column does not exists in the csv file");

        Map.Entry<String, List<String>> entry = iterator.next();
        for (String res : entry.getValue()) {
            result.add(true);
        }

        return result;
    }

    private static void checkClause(LinkedHashMap<String, List<String>> table, String clause) throws CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        Stack<String> characters = new Stack<>();
        int nParentheses = 0;

        for (String s : clause.split(" ")) {
            if (s.equals("("))
                nParentheses++;

            characters.push(s);

            if (s.equals(")"))
                manageInternParentheses(table, characters);
        }
    }

    private static void manageInternParentheses(LinkedHashMap<String, List<String>> table, Stack<String> characters) throws CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        Queue<String> queue = new LinkedList<>();

        if (characters.peek().equals(")"))
            characters.pop();

        while (!characters.peek().equals("("))
            queue.add(characters.pop());

        if (characters.peek().equals("("))
            characters.pop();

        try {
            if (characters.peek() != null && isFunction(characters.peek()))
                operateFunction(table, queue, characters);
        } catch (EmptyStackException ignored) {
        }

        List<String> postfix = infixToPostfix(queue);
        int nOperators = numOfOperators(postfix);
        int finalNOperators = nOperators;

        for (int i = 0; i < postfix.size(); i++) {
            if (isOperator(postfix.get(i))) {
                operate(table, postfix.get(i - 1), postfix.get(i), postfix.get(i - 2), nOperators);
                postfix.remove(i);
                postfix.remove(i - 1);
                postfix.remove(i - 2);
                postfix.add(i - 2, "__temp__" + nOperators);
                i = i - 3;
                nOperators--;
                if (nOperators == 0)
                    deletePreviousTemps(table, finalNOperators);
                continue;
            }
        }

        characters.push("__temp__");

        System.out.println("a");
    }

    private static void operateFunction(LinkedHashMap<String, List<String>> table, Queue<String> funParam, Stack<String> characters) throws CsvElementDoesNotExistsException, ElementDoesNotExistsException {
        System.out.println("aaa");

        if (!(funParam.size() > 1)) {
            operateFunctionCol(table, characters, funParam);
            return;
        }

        manageInternParentheses(table, characters);
        System.out.println("flag");
    }

    private static void operateFunctionCol(LinkedHashMap<String, List<String>> table, Stack<String> characters, Queue<String> funParam) throws CsvElementDoesNotExistsException {
        if (!table.containsKey(funParam.peek()))
            throw new CsvElementDoesNotExistsException("The column " + funParam.peek() + " does not exist");

        String fun = characters.pop();
        String col = funParam.peek();

        List<String> newList = new ArrayList<>();

        if (fun.equalsIgnoreCase("AVG"))
            Functions.avg(table, newList, col);
        if (fun.equalsIgnoreCase("MIN"))
            Functions.min(table, newList, col);
        if (fun.equalsIgnoreCase("MAX"))
            Functions.max(table, newList, col);
        if (fun.equalsIgnoreCase("ROUND"))
            Functions.round(table, newList, col);
        if (fun.equalsIgnoreCase("UPPER"))
            Functions.upper(table, newList, col);
        if (fun.equalsIgnoreCase("LOWER"))
            Functions.lower(table, newList, col);
        if (fun.equalsIgnoreCase("OPERATE"))
            Functions.lower(table, newList, col);

        table.put("__temp__", newList);
    }

    private static void deletePreviousTemps(LinkedHashMap<String, List<String>> table, int nOperators) {
        List<String> temp = List.copyOf(table.get("__temp__" + 1));
        for (int i = 1; i <= nOperators; i++)
            table.remove("__temp__" + i);

        table.put("__temp__", temp);
    }

    private static void operate(LinkedHashMap<String, List<String>> table, String operator_1, String operand, String operator_2, int nOperators) throws ElementDoesNotExistsException {
        List<String> temp = new ArrayList<>();
        String op1Type = getOperatorType(operator_1);
        String op2Type = getOperatorType(operator_2);
        List<String> opColumn1 = new ArrayList<>();
        List<String> opColumn2 = new ArrayList<>();

        createOperatorList(table, opColumn1, operator_1, op1Type);
        createOperatorList(table, opColumn2, operator_2, op2Type);

        if (operand.equals("="))
            OperandList.equals(temp, opColumn1, opColumn2);
        if (operand.equals("+"))
            OperandList.plus(temp, opColumn1, opColumn2);
        if (operand.equals("-"))
            OperandList.substract(temp, opColumn1, opColumn2);
        if (operand.equals("*"))
            OperandList.multiply(temp, opColumn1, opColumn2);
        if (operand.equals("/"))
            OperandList.divide(temp, opColumn1, opColumn2);
        if (operand.equals("%"))
            OperandList.mod(temp, opColumn1, opColumn2);
        if (operand.equals(">"))
            OperandList.greaterThan(temp, opColumn1, opColumn2);
        if (operand.equals("<"))
            OperandList.lowerThan(temp, opColumn1, opColumn2);
        if (operand.equals(">="))
            OperandList.greaterOrEqualsThan(temp, opColumn1, opColumn2);
        if (operand.equals("<="))
            OperandList.lowerOrEqualsThan(temp, opColumn1, opColumn2);
        if (operand.equalsIgnoreCase("AND"))
            OperandList.and(temp, opColumn1, opColumn2);
        if (operand.equalsIgnoreCase("OR"))
            OperandList.or(temp, opColumn1, opColumn2);

        table.put("__temp__" + nOperators, temp);
    }

    private static void createOperatorList(LinkedHashMap<String, List<String>> table, List<String> operatorColumn, String operator, String operatorType) throws ElementDoesNotExistsException {
        if (operatorType.equals("column")) {
            try {
                operatorColumn.addAll(table.get(operator));
            } catch (NullPointerException e) {
                throw new ElementDoesNotExistsException("The column " + operator + " does not exists");
            }
            return;
        }

        int rowSize = table.entrySet().iterator().next().getValue().size();

        if (operatorType.equals("numeric")) {
            for (int i = 0; i < rowSize; i++) {
                operatorColumn.add(String.valueOf(operator));
            }
            return;
        }

        if (operatorType.equals("string")) {
            for (int i = 0; i < rowSize; i++) {
                operatorColumn.add(operator.replace("'", ""));
            }
        }
    }

    private static String getOperatorType(String operator) {
        if (isNumeric(operator))
            return "numeric";
        if (isString(operator))
            return "string";

        return "column";
    }

    private static boolean isString(String str) {
        return str.startsWith("'") && str.endsWith("'");
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<String> infixToPostfix(Queue<String> infix) {
        Stack<String> operators = new Stack<String>();
        List<String> postfix = new ArrayList<String>();

        for (String current : infix) {
            if (isOperator(current)) {
                while (!operators.isEmpty() && hasLowerPrecedence(current, operators.peek())) {
                    postfix.add(operators.pop());
                }
                operators.push(current);
            } else {
                postfix.add(current);
            }
        }

        while (!operators.isEmpty()) {
            postfix.add(operators.pop());
        }

        return postfix;
    }

    private static boolean isOperator(String operator) {
        return List.of("+", "-", "*", "/", "AND", "OR", ">=", "<=", "<", ">", "=", "%", "NOT").contains(operator);
    }

    private static boolean isFunction(String functionName) {
        return List.of("AVG", "MAX", "MIN", "ROUND", "UPPER", "LOWER").contains(functionName.toUpperCase());
    }

    private static boolean hasLowerPrecedence(String op1, String op2) {
        return precedence(op1) < precedence(op2);
    }

    private static int precedence(String op) {
        return switch (op) {
            case "AND", "OR" -> 1;
            case ">=", "<=", "<", ">", "=" -> 2;
            case "+", "-" -> 3;
            case "/", "*", "%" -> 4;
            case "NOT" -> 5;
            default -> 6;
        };
    }

    private static int numOfOperators(List<String> postfix) {
        int numOfOperators = 0;
        for (String c : postfix) {
            if (isOperator(c))
                numOfOperators++;
        }

        return numOfOperators;
    }
}
