package edu.upvictoria;

import dev.soriane.scanner.BrScanner;
import edu.upvictoria.Exceptions.DBAlreadyExistsException;
import edu.upvictoria.Exceptions.NotADBException;
import edu.upvictoria.Exceptions.SQLSyntaxException;
import edu.upvictoria.sql.*;
import edu.upvictoria.utils.ConsoleColors;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        DataBase dataBase;

        while (true) {
            String dbPath = "";
            System.out.println("Give me a database path");
            dbPath = BrScanner.readLine();
            try {
                dataBase = new DataBase(dbPath);
            } catch (NotADBException e) {
                System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + "The folder is not a database" + ConsoleColors.ANSI_RESET);
                continue;
            } catch (IOException e) {
                System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + "There was an error opening the database" + ConsoleColors.ANSI_RESET);
                continue;
            }
            break;
        }

        CommandInterpreter commandInterpreter = new CommandInterpreter(
                dataBase,
                new Actions(dataBase, new CommandParser())
        );

        String command = "";
        while (!command.equals("quit")) {
            command = Reader.readCommand();
            try {
                commandInterpreter.checkCommand(command);
            } catch (SQLSyntaxException | IOException | DBAlreadyExistsException e) {
                System.out.println(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_BLACK + e.getMessage() + ConsoleColors.ANSI_RESET);
            }
        }
    }
}
