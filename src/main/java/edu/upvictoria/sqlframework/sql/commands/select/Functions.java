package edu.upvictoria.sqlframework.sql.commands.select;

import java.util.LinkedHashMap;
import java.util.List;

public class Functions {
    private static boolean isNull (String val) {
        return val == null || val.isEmpty() || val.equals("null");
    }

    public static void avg(LinkedHashMap<String, List<String>> table, List<String> list, String column) {
        List<String> values = table.get(column);

        double avg = 0.0;

        for (String value : values) {
            if (isNull(value))
                continue;
            try {
                double n = Double.parseDouble(value);
                avg += n;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }
        }

        avg = avg / values.size();

        for (int i = 0; i < values.size(); i++) {
            list.add(String.valueOf(avg));
        }
    }

    public static void max(LinkedHashMap<String, List<String>> table, List<String> list, String column) {
        List<String> values = table.get(column);

        double max = 0.0;

        for (String value : values) {
            if (isNull(value))
                continue;
            try {
                double n = Double.parseDouble(value);
                if (n > max)
                    max = n;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }
        }

        for (int i = 0; i < values.size(); i++) {
            list.add(String.valueOf(max));
        }
    }

    public static void min(LinkedHashMap<String, List<String>> table, List<String> list, String column) throws NumberFormatException {
        List<String> values = table.get(column);

        double min = 0.0;

        try {
            min = Double.parseDouble(values.get(0));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The operation could not be performed with the operand types");
        }

        for (String value : values) {
            if (isNull(value))
                continue;
            try {
                double n = Double.parseDouble(value);
                if (n < min)
                    min = n;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }
        }

        for (int i = 0; i < values.size(); i++) {
            list.add(String.valueOf(min));
        }
    }

    public static void round(LinkedHashMap<String, List<String>> table, List<String> list, String column) throws NumberFormatException {
        List<String> values = table.get(column);

        double min = 0.0;

        try {
            min = Double.parseDouble(values.get(0));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("The operation could not be performed with the operand types");
        }

        for (String value : values) {
            if (isNull(value))
                continue;
            try {
                double n = Double.parseDouble(value);
                if (n < min)
                    min = n;
            } catch (NumberFormatException e) {
                throw new NumberFormatException("The operation could not be performed with the operand types");
            }
        }

        for (int i = 0; i < values.size(); i++) {
            list.add(String.valueOf(min));
        }
    }

    public static void upper(LinkedHashMap<String, List<String>> table, List<String> list, String column) throws NumberFormatException {
        List<String> values = table.get(column);

        for (String value : values) {
            if (isNull(value))
                continue;
            list.add(value.toUpperCase());
        }
    }

    public static void lower(LinkedHashMap<String, List<String>> table, List<String> list, String column) throws NumberFormatException {
        List<String> values = table.get(column);

        for (String value : values) {
            if (isNull(value))
                continue;
            list.add(value.toLowerCase());
        }
    }
    public static void operate (LinkedHashMap<String, List<String>> table, List<String> list, String column) throws NumberFormatException {
        List<String> values = table.get(column);

        for (String value : values) {
            if (isNull(value))
                continue;
            list.add(value.toLowerCase());
        }
    }


}
