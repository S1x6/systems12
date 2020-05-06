package com.s1x6.systems1.db.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.List;

public abstract class DAO<T> {

    private Connection connection;

    public DAO(Connection connection) {
        this.connection = connection;
    }

    protected Connection getConnection() {
        return connection;
    }
    public abstract List<T> findAll();
    public abstract T findById(BigInteger id);
    public abstract boolean insertSql(List<T> entities);
    public abstract boolean insertPreparedStatement(List<T> entities);
    public abstract boolean insertBatch(List<T> entities);
    public abstract void deleteAll();
}
