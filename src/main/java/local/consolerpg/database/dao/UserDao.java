package local.consolerpg.database.dao;

import local.consolerpg.models.User;

public interface UserDao {
    void add(User user);
    boolean isExistsUsername(String username);
    boolean isCorrectUserPassword(User user);
    long getIdByName(String username);
}
