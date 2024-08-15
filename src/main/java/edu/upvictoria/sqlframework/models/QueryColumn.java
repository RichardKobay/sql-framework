package edu.upvictoria.sqlframework.models;

import java.util.ArrayList;
import java.util.List;

public class QueryColumn {
    private String name;
    private String type;
    private List<QueryConstraint> constraints;

    public QueryColumn(String name, String type, List<QueryConstraint> constraints) {
        this.name = name;
        this.type = type;
        this.constraints = constraints;
    }

    public QueryColumn(String name, String type) {
        this.name = name;
        this.type = type;
        this.constraints = new ArrayList<QueryConstraint>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<QueryConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<QueryConstraint> constraints) {
        this.constraints = constraints;
    }
}
