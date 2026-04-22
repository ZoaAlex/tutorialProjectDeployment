package org.example.coursclasseservice.service;

public class ErreurImport {

    private int rownum;
    private String message;

    public ErreurImport(int rownum, String message) {
        this.rownum = rownum;
        this.message = message;
    }

    public int getRownum()    { return rownum; }
    public String getMessage() { return message; }

    
}
