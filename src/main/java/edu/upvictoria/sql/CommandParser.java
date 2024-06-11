package edu.upvictoria.sql;

import edu.upvictoria.poo.Exceptions.SQLSyntaxException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
    public HashMap<String, String> parseSelect (String command) throws SQLSyntaxException {
        HashMap<String, String> map = new HashMap<>();

        String selectRegex = "(?i)SELECT\\s+(.+?)\\s+FROM";
        String fromRegex = "(?i)FROM\\s+([a-z]+)\\s*(WHERE|GROUP BY|ORDER BY|$)";
        String whereRegex = "(?i)WHERE\\s+(.+?)\\s*(GROUP BY|ORDER BY|$)";
        String groupByRegex = "(?i)GROUP BY\\s+(.+?)\\s*(ORDER BY|$)";
        String orderByRegex = "(?i)ORDER BY\\s+(.+?)\\s*$";


        Matcher selectMatcher = Pattern.compile(selectRegex).matcher(command);
        if (selectMatcher.find()) {
            String selectPart = selectMatcher.group(1).trim();
            if (selectPart.isEmpty())
                throw new SQLSyntaxException("Invalid SQL syntax: SELECT clause is empty.");
            map.put("SELECT", selectPart);
        } else {
            throw new SQLSyntaxException("Invalid SQL syntax: Missing or incorrect SELECT clause.");
        }

        Matcher fromMatcher = Pattern.compile(fromRegex).matcher(command);
        if (fromMatcher.find()) {
            String fromPart = fromMatcher.group(1).trim();
            if (fromPart.isEmpty())
                throw new SQLSyntaxException("Invalid SQL syntax: FROM clause is empty.");
            map.put("FROM", fromPart);
        } else {
            throw new SQLSyntaxException("Invalid SQL syntax: Missing or incorrect FROM clause.");
        }

        Matcher whereMatcher = Pattern.compile(whereRegex).matcher(command);
        if (whereMatcher.find()) {
            String wherePart = whereMatcher.group(1).trim();
            if (wherePart.isEmpty())
                throw new SQLSyntaxException("Invalid SQL syntax: WHERE clause is empty.");
            map.put("WHERE", wherePart);
        } else {
            map.put("WHERE", "");
        }

        Matcher groupByMatcher = Pattern.compile(groupByRegex).matcher(command);
        if (groupByMatcher.find()) {
            String groupByPart = groupByMatcher.group(1).trim();
            if (groupByPart.isEmpty())
                throw new SQLSyntaxException("Invalid SQL syntax: GROUP BY clause is empty.");
            map.put("GROUP BY", groupByPart);
        } else {
            map.put("GROUP BY", "");
        }

        Matcher orderByMatcher = Pattern.compile(orderByRegex).matcher(command);
        if (orderByMatcher.find()) {
            String orderByPart = orderByMatcher.group(1).trim();
            if (orderByPart.isEmpty())
                throw new SQLSyntaxException("Invalid SQL syntax: ORDER BY clause is empty.");
            map.put("ORDER BY", orderByPart);
        } else {
            map.put("ORDER BY", "");
        }

        return map;
    }

    public HashMap<String, String> parseCreateTable (String command) throws SQLSyntaxException {
        HashMap<String, String> map = new HashMap<>();

        String createTableRegex = "(?i)^\\s*CREATE\\s+TABLE\\s+([a-z][a-z0-9_-]*[a-z0-9]|[a-z]+)\\s*\\((.+)\\)\\s*$";
        String tableNameRegex = "^[a-z][a-z0-9]*([-_][a-z0-9]+)*$";
        String columnsRegex = "^\\s*([a-zA-Z][a-zA-Z0-9_\\s]*\\s+[a-zA-Z0-9_\\s]+(?:,\\s*[a-zA-Z][a-zA-Z0-9_\\s]*\\s+[a-zA-Z0-9_\\s]+)*)\\s*$";
        Matcher matcher = Pattern.compile(createTableRegex).matcher(command);

        if (!matcher.find())
            throw new SQLSyntaxException("Invalid SQL syntax");

        String tableName = matcher.group(1).trim();
        String columns = matcher.group(2).trim();

        if (!Pattern.compile(tableNameRegex).matcher(tableName).matches())
            throw new SQLSyntaxException("Invalid SQL syntax: Table name is incorrect.");

        if (!Pattern.compile(columnsRegex).matcher(columns).matches())
            throw new SQLSyntaxException("Invalid SQL syntax: Columns definition are incorrect.");

        map.put("table", tableName);
        map.put("columns", columns);

        return map;
    }

    public HashMap< String, String> parseInsert (String command) throws SQLSyntaxException {
        HashMap<String, String> map = new HashMap<>();

        Pattern pattern = Pattern.compile(
                "INSERT\\s+INTO\\s+(\\w+)\\s*(\\(([^)]*)\\))?\\s*VALUES\\s*\\(([^)]*)\\)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(command);

        if (!matcher.find()) {
            throw new SQLSyntaxException("Invalid SQL syntax");
        }

        String tableName = matcher.group(1);

        String columnsPart = matcher.group(3);
        String valuesPart = matcher.group(4);

        String[] columns = columnsPart != null ? columnsPart.split("\\s*,\\s*") : new String[0];
        String[] values = valuesPart.split("\\s*,\\s*");

        if (columns.length != 0 && columns.length != values.length)
            throw new SQLSyntaxException("The number of columns does not match the number of values");

        for (String value : values)
            if ((value.startsWith("'") && !value.endsWith("'")) || (!value.startsWith("'") && value.endsWith("'")))
                throw new SQLSyntaxException("Unmatched quotes in value: " + value);

        if (columns.length == 0)
            for (String value : values)
                map.put("", value.replaceAll("^'|'$", ""));
        else
            for (int i = 0; i < columns.length; i++)
                map.put(columns[i].trim(), values[i].replaceAll("^'|'$", "").trim());

        return map;
    }

    public List<String> parseColumnNames (String columns) {
        List<String> columnsList = Arrays.asList(columns.split(","));

        columnsList.replaceAll(s -> s.trim().split(" ")[0].trim());

        return columnsList;
    }
}
