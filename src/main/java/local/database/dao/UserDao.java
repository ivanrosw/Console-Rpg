package local.database.dao;

import local.models.User;

public interface UserDao {
    void add(User user);
    boolean isExistsUsername(String username);
    boolean isCorrectUserPassword(User user);
    long getUserIdByName(String username);
}
