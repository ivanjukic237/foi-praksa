package hr.tis.praksa.db;

import java.sql.*;

public class Database {

    private static final String DB_URL = "jdbc:h2:./db/praksa";

    private static Database instance;
    private Database() {

    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
            init();
        }
        return instance;
    }

    public static void init() {
        try (Connection connection = Database.getInstance().getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("RUNSCRIPT FROM 'classpath:init.sql'");
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, "sa", "");
    }

}
