package edu.upvictoria.sqlframework.models;

public class QueryConstraint {
    private String type;
    private String name;
    private String columnName;
    private String tableReference;
    private String columnReference;

    public QueryConstraint(String type, String name, String columnName) {
        this.type = type;
        this.name = name;
        this.columnName = columnName;
        this.tableReference = "";
        this.columnReference = "";
    }

    public QueryConstraint(String type, String name, String columnName, String tableReference, String columnReference) {
        this.type = type;
        this.name = name;
        this.columnName = columnName;
        this.tableReference = tableReference;
        this.columnReference = columnReference;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableReference() {
        return tableReference;
    }

    public void setTableReference(String tableReference) {
        this.tableReference = tableReference;
    }

    public String getColumnReference() {
        return columnReference;
    }

    public void setColumnReference(String columnReference) {
        this.columnReference = columnReference;
    }
}
