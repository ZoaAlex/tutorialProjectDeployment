package iusjc_planning.user_service.service;

public class ErreurImport {
    private final int rownum;
    private final String message;

    public ErreurImport(int rownum, String message) {
        this.rownum  = rownum;
        this.message = message;
    }

    public int getRownum()     { return rownum; }
    public String getMessage() { return message; }
}
