package pl.kalisiak.leave.model;

public enum Department {
    IT("IT"),
    HR("Human Resources"),
    ADMINISTRATION("Administration"),
    SALES("Sales"),
    PRODUCTION("Production");

    private String value;

    private Department(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}