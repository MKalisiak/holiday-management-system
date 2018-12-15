package pl.kalisiak.leave.exceptions;

public class FinishBeforeStartException extends Exception {
    
    public FinishBeforeStartException() {
        super();
    }

    public FinishBeforeStartException(String msg) {
        super(msg);
    }

}