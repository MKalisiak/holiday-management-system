package pl.kalisiak.leave.exceptions;

public class EmailAlreadyTakenException extends Exception {
    
    public EmailAlreadyTakenException() {
        super();
    }

    public EmailAlreadyTakenException(String msg) {
        super(msg);
    }

}