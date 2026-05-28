package Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {
    public Connection con;

    public Database() {
        con = createConnection();
    }

    Connection createConnection() {
        try {
            // Create driver
            Class.forName("org.postgresql.Driver");
            // Create a connection
            String url = "postgresql://neondb_owner:npg_4zxK5dmIEhZD@ep-lucky-heart-apwes3oz.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require";
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println("Error when connecting to database : " + e.getMessage());
        }
        return null;
    }
}
