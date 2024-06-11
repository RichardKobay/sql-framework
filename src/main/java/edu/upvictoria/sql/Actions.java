package edu.upvictoria.sql;

import edu.upvictoria.Exceptions.DBAlreadyExistsException;
import edu.upvictoria.Exceptions.DBNotExistsException;
import edu.upvictoria.Exceptions.SQLSyntaxException;
import edu.upvictoria.utils.ConsoleColors;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Actions {

    /*///////////////////////////////////
    * ATTRIBUTES
    * ///////////////////////////////////*/

    private DataBase database;
    private CommandParser commandParser;

    /*///////////////////////////////////
    * CONSTRUCTORS
    * ///////////////////////////////////*/

    public Actions(DataBase database, CommandParser commandParser) {
        this.database = database;
        this.commandParser = commandParser;
    }

    /*///////////////////////////////////
    * GETTERS AND SETTERS
    * ///////////////////////////////////*/

    public DataBase getDatabase() {
        return database;
    }

    public void setDatabase(DataBase database) {
        this.database = database;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    /*///////////////////////////////////
    * METHODS
    * ///////////////////////////////////*/

    public boolean createTable (String tableName, String columns) throws IOException, DBAlreadyExistsException {
        File database = new File(this.database.getDatabase(), tableName + "_sdb.sdb");

        List<String> columnsList = commandParser.parseColumnNames(columns);

        String fileContent = String.join(",", columnsList);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(database))) {
            writer.write(fileContent);
        } catch (IOException e) {
            System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
        }

        try {
            if (database.createNewFile())
                return true;
            throw new DBAlreadyExistsException("The database already exists");
        } catch (IOException e) {
            throw new IOException("Error creating the database");
        }
    }

    public boolean insertData (String tableName, HashMap<String, String> values) throws IOException, DBNotExistsException, SQLSyntaxException {
        File table = new File(this.database.getDatabase(), tableName + "_sdb.sdb");

        if (!table.exists())
            throw new DBNotExistsException("The database does not exists");

        List<String> columns = getColumns(table);

        if (values.size() > columns.size())
            throw new SQLSyntaxException("Too many values provided for the number of columns.");

        for (String column : values.keySet())
            if (!columns.contains(column))
                throw new SQLSyntaxException("The column '" + column + "' does not exist in the table.");

        StringBuilder valuesString = new StringBuilder();
        for (String column : columns) {
            if (values.containsKey(column))
                valuesString.append(values.get(column));
            valuesString.append(",");
        }

        if (!valuesString.isEmpty())
            valuesString.setLength(valuesString.length() - 1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(table, true))) {
            writer.newLine();
            writer.write(valuesString.toString());
        }

        return true;
    }

    public List<String> getColumns (File table) {
        List<String> columns = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(table))) {
            String line = reader.readLine();
            columns = Arrays.asList(line.split(","));
        } catch (IOException e) {
            System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
        }

        return columns;
    }
}
