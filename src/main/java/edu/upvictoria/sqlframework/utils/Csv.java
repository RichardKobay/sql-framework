package edu.upvictoria.sqlframework.utils;

import java.util.*;

public class Csv {
    public static LinkedHashMap<String, List<String>> csvToHashTable(String csvFile) {
        LinkedHashMap<String, List<String>> table = new LinkedHashMap<>();
        String[] lines = csvFile.split("\n");

        if (lines.length == 0) {
            return table;
        }

        // Extract headers
        String[] headers = lines[0].split(",");
        for (String header : headers) {
            table.put(header, new ArrayList<>());
        }

        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split(",", -1);
            for (int j = 0; j < values.length; j++) {
                String value = values[j];
                if (value.equals("null")) {
                    value = null;
                }
                table.get(headers[j]).add(value);
            }
        }

        return table;
    }

    public static String hashTableToCsv(LinkedHashMap<String, List<String>> table) {
        StringBuilder csvBuilder = new StringBuilder();

        // Extract headers
        List<String> headers = new ArrayList<>(table.keySet());
        csvBuilder.append(String.join(",", headers)).append("\n");

        // Get number of rows
        int numRows = table.values().iterator().next().size();

        // Build CSV rows
        for (int i = 0; i < numRows; i++) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                List<String> column = table.get(header);
                String value = column.get(i);
                if (value == null) {
                    value = "null";
                }
                row.add(value);
            }
            csvBuilder.append(String.join(",", row)).append("\n");
        }

        return csvBuilder.toString();
    }
}
