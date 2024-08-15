package edu.upvictoria.sqlframework.models;

public class Response {
    private String type;
    private String content;
    private Object data;

    public Response(String type, String content, Object data) {
        this.type = type;
        this.content = content;
        this.data = data;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
