package com.s1x6.systems1.db;

import java.sql.*;

public class DatabaseHelper implements AutoCloseable {

    private Connection c;

    public DatabaseHelper(String url, String username, String password) throws SQLException {
        c = DriverManager.getConnection(url, username, password);
        Statement st = c.createStatement();
        st.execute(nodeTableCreateStatement);
        st.execute(tagTableCreateStatement);
        st.closeOnCompletion();
    }

    public Connection getConnection() {
        return c;
    }

    private static String nodeTableCreateStatement =
            "CREATE OR REPLACE FUNCTION create_nodetable ()\n" +
                    "  RETURNS void AS\n" +
                    "$func$\n" +
                    "BEGIN\n" +
                    "   IF EXISTS (SELECT FROM pg_catalog.pg_tables \n" +
                    "              WHERE  schemaname = 'public'\n" +
                    "              AND    tablename  = 'nodes') THEN\n" +
                    "      RAISE NOTICE 'Table public.nodes already exists.';\n" +
                    "   ELSE\n" +
                    "CREATE TABLE nodes(" +
                    "id bigint primary key," +
                    "lon numeric," +
                    "lat numeric," +
                    "uid bigint," +
                    "\"user_name\" varchar);\n" +
                    "   END IF;\n" +
                    "END\n" +
                    "$func$ LANGUAGE plpgsql;\n" +
                    "SELECT create_nodetable()";
    private static String tagTableCreateStatement =
            "CREATE OR REPLACE FUNCTION create_tagtable ()\n" +
                    "  RETURNS void AS\n" +
                    "$func$\n" +
                    "BEGIN\n" +
                    "   IF EXISTS (SELECT FROM pg_catalog.pg_tables \n" +
                    "              WHERE  schemaname = 'public'\n" +
                    "              AND    tablename  = 'tags') THEN\n" +
                    "      RAISE NOTICE 'Table public.tags already exists.';\n" +
                    "   ELSE\n" +
                    "CREATE TABLE tags(" +
                    "id serial primary key," +
                    "key varchar," +
                    "value varchar," +
                    "node_id bigint);\n" +
                    "   END IF;\n" +
                    "END\n" +
                    "$func$ LANGUAGE plpgsql;" +
                    "SELECT create_tagtable()";

    @Override
    public void close() throws SQLException {
        c.close();
    }
}
