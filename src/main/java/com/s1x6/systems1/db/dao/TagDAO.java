package com.s1x6.systems1.db.dao;

import com.s1x6.systems1.binding.Node;
import com.s1x6.systems1.binding.Tag;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO extends DAO<Tag> {

    public TagDAO(Connection connection) {
        super(connection);
    }

    @Override
    public List<Tag> findAll() {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM tags");
            List<Tag> tags = new ArrayList<>();
            if (rs.next()) {
                do {
                    tags.add(extractTag(rs));
                } while (rs.next());
            }
            return tags;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Tag findById(BigInteger id) {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM tags WHERE id = " + id);
            if (rs.next()) {
                return extractTag(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Tag extractTag(ResultSet rs) throws SQLException {
        Tag tag = new Tag();
        tag.setK(rs.getString("key"));
        tag.setV(rs.getString("value"));
        return tag;
    }

    @Override
    public boolean insertSql(List<Tag> entities) {
        if (entities.isEmpty()) return true;
        try {
            Statement s = getConnection().createStatement();
            boolean success = true;
            for (Tag n : entities) {
                String sql = "INSERT INTO public.tags(key, value) VALUES(" +
                        "'" + n.getK() + "'," +
                        "'" + n.getV() + "'" +
                        ")";
                success &= s.execute(sql);
            }
            s.close();
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertPreparedStatement(List<Tag> entities) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO public.tags(key, value) " +
                    "VALUES(?, ?)");
            for (Tag n : entities) {
                ps.setString(1, n.getK());
                ps.setString(2, n.getV());
                ps.executeUpdate();
            }
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertBatch(List<Tag> entities) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO public.tags(key, value) " +
                    "VALUES(?, ?)");
            for (Tag n : entities) {
                ps.setString(1, n.getK());
                ps.setString(2, n.getV());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void deleteAll() {
        try {
            Statement s = getConnection().createStatement();
            s.execute("DELETE FROM public.tags");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
