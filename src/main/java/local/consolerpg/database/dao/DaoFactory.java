package local.consolerpg.database.dao;

import local.consolerpg.database.dao.implementations.UserDaoImpl;

public class DaoFactory {

    private DaoFactory() {
    }

    public static UserDao getUserDao() {
        return new UserDaoImpl();
    }
}