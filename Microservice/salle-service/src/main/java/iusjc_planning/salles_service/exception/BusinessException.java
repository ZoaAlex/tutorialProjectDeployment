package iusjc_planning.salles_service.exception;

/**
 * Exception levée lors d'une violation de règle métier
 */
public class BusinessException extends RuntimeException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}