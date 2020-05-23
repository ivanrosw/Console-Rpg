package local.models;

public class User {
    private long id;
    private String name;
    private String encryptedPassword;

    public User() {
    }

    public User(String name, String encryptedPassword) {
        this.name = name;
        this.encryptedPassword = encryptedPassword;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }
}
