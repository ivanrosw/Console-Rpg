package local.database.dao;

import local.database.dao.implementations.UserDaoImpl;

public class DaoFactory {

    private DaoFactory() {
    }

    public static UserDao getUserDao() {
        return new UserDaoImpl();
    }
}
