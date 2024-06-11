package edu.upvictoria.utils;

import edu.upvictoria.poo.Exceptions.DBFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HashMapParser {
    public static HashMap<String, ArrayList<String>> parse (File database) throws IOException, DBFormatException {
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(database))) {
            String line;
            String[] headers = null;

            if ((line = br.readLine()) != null) {
                headers = line.split(",");

                for (String header : headers)
                    map.put(header.trim(), new ArrayList<>());
            }

            if (headers == null)
                throw new DBFormatException("No headers found");

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length != headers.length)
                    throw new DBFormatException("The database format is not correct");

                for (int i = 0; i < headers.length; i++)
                    map.get(headers[i]).add(values[i]);
            }
        } catch (IOException e) {
            throw new IOException("There was an error reading the file");
        }

        return map;
    }
}
