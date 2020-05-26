package local.database.dao;

import local.models.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    boolean isExistsUsername(String username);
    boolean isCorrectUserPassword(User user);
    long getIdByName(String username);
    List<Long> getCharactersId(long userId);
}
