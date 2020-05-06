package com.s1x6.systems1.db.dao;

import com.s1x6.systems1.binding.Node;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NodeDAO extends DAO<Node> {

    public NodeDAO(Connection connection) {
        super(connection);
    }

    @Override
    public List<Node> findAll() {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM nodes");
            List<Node> nodes = new ArrayList<>();
            if (rs.next()) {
                do {
                    nodes.add(extractNode(rs));
                } while (rs.next());
            }
            return nodes;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Node findById(BigInteger id) {
        try {
            ResultSet rs = getConnection().createStatement().executeQuery("SELECT * FROM nodes WHERE id = " + id);
            if (rs.next()) {
                return extractNode(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Node extractNode(ResultSet rs) throws SQLException {
        Node node = new Node();
        node.setId(rs.getBigDecimal("id").toBigInteger());
        node.setLon(rs.getDouble("lon"));
        node.setLat(rs.getDouble("lat"));
        node.setUser(rs.getString("user_name"));
        node.setUid(rs.getBigDecimal("uid").toBigInteger());
        return node;
    }

    @Override
    public boolean insertSql(List<Node> entities) {
        if (entities.isEmpty()) return true;
        try {
            Statement s = getConnection().createStatement();
            boolean success = true;
            for (Node n : entities) {
                String sql = "INSERT INTO public.nodes VALUES(" +
                        n.getId() + ", " +
                        n.getLon() + ", " +
                        n.getLat() + ", " +
                        n.getUid() + ", " +
                        "'" + n.getUser() + "'" +
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
    public boolean insertPreparedStatement(List<Node> entities) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO public.nodes " +
                    "VALUES(?, ?, ?, ?, ?)");
            for (Node n : entities) {
                ps.setBigDecimal(1, new BigDecimal(n.getId()));
                ps.setDouble(2, n.getLon());
                ps.setDouble(3, n.getLat());
                ps.setBigDecimal(4, new BigDecimal(n.getUid()));
                ps.setString(5, n.getUser());
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
    public boolean insertBatch(List<Node> entities) {
        try {
            PreparedStatement ps = getConnection().prepareStatement("INSERT INTO public.nodes " +
                    "VALUES(?, ?, ?, ?, ?)");
            for (Node n : entities) {
                ps.setBigDecimal(1, new BigDecimal(n.getId()));
                ps.setDouble(2, n.getLon());
                ps.setDouble(3, n.getLat());
                ps.setBigDecimal(4, new BigDecimal(n.getUid()));
                ps.setString(5, n.getUser());
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
            s.execute("DELETE FROM public.nodes");
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
