package edu.upvictoria.sql;

import edu.upvictoria.Exceptions.DBAlreadyExistsException;
import edu.upvictoria.Exceptions.SQLSyntaxException;
import edu.upvictoria.utils.ConsoleColors;

import java.io.IOException;
import java.util.HashMap;

public class CommandInterpreter {

    private DataBase dataBase;
    private Actions actions;
    private CommandParser commandParser;
    private Select select;
    private Where where;

    public CommandInterpreter(DataBase dataBase, Actions actions) {
        this.dataBase = dataBase;
        this.actions = actions;
        this.commandParser = actions.getCommandParser();
        this.select = new Select();
        this.where = new Where();
    }

    public String checkCommand (String command) throws SQLSyntaxException, IOException, DBAlreadyExistsException {
        command = command.trim().replace(";", "");
        if (command.toLowerCase().trim().startsWith("create table")) {
            HashMap<String, String> parsedCommand = commandParser.parseCreateTable(command);
            if (actions.createTable(parsedCommand.get("table"), parsedCommand.get("columns")))
                return "Table created Successfully";
        }

        if (command.toLowerCase().trim().startsWith("select")) {
            HashMap selectMap = commandParser.parseSelect(command);
            return "";
        }

        if (command.toLowerCase().trim().startsWith("update")) {
            return "";
        }

        if (command.toLowerCase().trim().startsWith("insert")) {
            return "";
        }

        if (command.toLowerCase().trim().startsWith("delete")) {
            return "";
        }

        if (command.toLowerCase().trim().startsWith("show tables")) {
            return "";
        }

        if (command.toLowerCase().trim().startsWith("use")) {
            return "";
        }

        if (command.toLowerCase().trim().startsWith("open_sql")) {
            return "";
        }

        throw new SQLSyntaxException("Command not found");
    }
}
