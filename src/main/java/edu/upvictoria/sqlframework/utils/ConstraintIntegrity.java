package edu.upvictoria.sqlframework.utils;

import dev.soriane.dtdxmlparser.exceptions.ElementDoesNotExistsException;
import dev.soriane.dtdxmlparser.model.xml.Constraint;
import dev.soriane.dtdxmlparser.model.xml.XMLStructure;
import dev.soriane.dtdxmlparser.utils.ConstraintManagement;
import edu.upvictoria.sqlframework.exceptions.ConstraintIntegrityException;
import edu.upvictoria.sqlframework.sql.SqlInterpreter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConstraintIntegrity {

    public static void checkNotNullConstraint(List<String> column) throws ConstraintIntegrityException {
        for (String row : column)
            if (row == null || row.isEmpty() || row.equals("null"))
                throw new ConstraintIntegrityException("The not null constraint is violated");
    }

    public static void checkUniqueConstraint(List<String> column) throws ConstraintIntegrityException {
        for (int i = 0; i < column.size(); i++) {
            for (int j = 0; j < column.size(); j++) {
                if (j == i)
                    continue;

                if (column.get(i).equals(column.get(j)))
                    throw new ConstraintIntegrityException("The unique constraint is violated");
            }
        }
    }

    public static void checkAutoIncrement(List<String> column) throws ConstraintIntegrityException {
        double i = 0;

        for (String row : column) {
            try {
                double num = Double.parseDouble(row);
                if (num < i)
                    throw new ConstraintIntegrityException("The auto-increment constraint is violated");
                i = num;
            } catch (NumberFormatException e) {
                throw new ConstraintIntegrityException("The auto-increment constraint is violated");
            }
        }
    }

    public static void checkPrimaryKey(List<String> column) throws ConstraintIntegrityException {
        checkUniqueConstraint(column);
        checkNotNullConstraint(column);
    }

    public static boolean hasNotNullConstraint(SqlInterpreter interpreter, String tableName, String columnName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        return columnContainsConstraint(interpreter, tableName, columnName, "not-null-constraint");
    }

    public static boolean hasPrimaryKeyConstraint(SqlInterpreter interpreter, String tableName, String columnName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        return columnContainsConstraint(interpreter, tableName, columnName, "primary-key-constraint");
    }

    public static boolean hasUniqueConstraint(SqlInterpreter interpreter, String tableName, String columnName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        return columnContainsConstraint(interpreter, tableName, columnName, "unique-constraint");
    }

    public static boolean hasAutoIncrementConstraint(SqlInterpreter interpreter, String tableName, String columnName) throws ElementDoesNotExistsException, IOException, ParserConfigurationException, SAXException {
        return columnContainsConstraint(interpreter, tableName, columnName, "auto-increment-constraint");
    }

    private static boolean columnContainsConstraint (SqlInterpreter sqlInterpreter, String tableName, String columnName, String constraintType) throws IOException, ParserConfigurationException, SAXException, ElementDoesNotExistsException {
        Constraint constraint = new Constraint(
                columnName,
                constraintType,
                "__fake__placeholder__name__id__fake__const__name"
        );

        XMLStructure xmlStructure = GetXML.getXMLStructure(sqlInterpreter);
        return ConstraintManagement.constraintExists(xmlStructure, tableName, constraint);
    }
}
