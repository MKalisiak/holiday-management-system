package pl.kalisiak.leave.exceptions;

public class NoSuchEmployeeException extends Exception {
    
    public NoSuchEmployeeException() {
        super();
    }

    public NoSuchEmployeeException(String msg) {
        super(msg);
    }

}