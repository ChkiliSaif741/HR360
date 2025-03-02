package tests;

public class TempUser {
    private int id;
    private String name;
    private String email;

    public TempUser(int id,String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TempUser{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

