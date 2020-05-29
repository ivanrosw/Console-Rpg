package local.consolerpg.database.dao;

import local.consolerpg.models.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    boolean isExistsUsername(String username);
    boolean isCorrectUserPassword(User user);
    long getIdByName(String username);
    List<Long> getCharactersId(long userId);
}
