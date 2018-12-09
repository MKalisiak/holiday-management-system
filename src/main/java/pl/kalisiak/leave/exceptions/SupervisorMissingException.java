package pl.kalisiak.leave.exceptions;

public class SupervisorMissingException extends Exception {
    
    public SupervisorMissingException() {
        super();
    }

    public SupervisorMissingException(String msg) {
        super(msg);
    }

}