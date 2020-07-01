package server.models;

public class CreateAdmin {
    private final String userName;
    private final String password;
    private final String name;
    private final String description;
    private final int role;

    public CreateAdmin(String userName, String password, String name, String description, int role) {
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.description = description;
        this.role = role;
    }
}
