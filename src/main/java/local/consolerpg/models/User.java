package local.consolerpg.models;

import java.util.List;

public class User {
    
    private long id;
    private String name;
    private String password;
    private List<Long> charactersId;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getCharactersId() {
        return charactersId;
    }

    public void setCharactersId(List<Long> charactersId) {
        this.charactersId = charactersId;
    }
}
