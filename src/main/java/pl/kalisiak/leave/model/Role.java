package pl.kalisiak.leave.model;

public enum Role {
    EMPLOYEE("EMPLOYEE"),
    HR("HR"),
    MANAGEMENT("MANAGEMENT"),
    CEO("CEO");

    private String value;

    private Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "ROLE_" + this.value;
    }
}