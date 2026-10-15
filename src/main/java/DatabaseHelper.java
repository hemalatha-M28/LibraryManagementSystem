import java.sql.*;

public class DatabaseHelper
{
    static String url = "jdbc:mysql://localhost:3306/librarydb";
    static String username = "root";
    static String password = "";

    public static Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(url, username, password);
    }

    public static void initializeDatabase()
    {
        try
        {
            Connection conn = getConnection();

            Statement st = conn.createStatement();

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS book (" +
                "book_id VARCHAR(20) PRIMARY KEY, " +
                "book_title VARCHAR(100), " +
                "author_name VARCHAR(100), " +
                "status VARCHAR(20))"
            );

            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM book");

            rs.next();

            if(rs.getInt(1) == 0)
            {
                String sql = "INSERT INTO book VALUES (?, ?, ?, ?)";

                PreparedStatement ps = conn.prepareStatement(sql);

                String ids[] = {"B101", "B102", "B103", "B104", "B105"};

                String titles[] = {
                    "Java Basics",
                    "C Programming",
                    "Data Structures",
                    "Operating Systems",
                    "Database Management"
                };

                String authors[] = {
                    "James",
                    "Dennis",
                    "Mark",
                    "Galvin",
                    "Korth"
                };

                String statuses[] = {
                    "Available",
                    "Issued",
                    "Available",
                    "Reserved",
                    "Lost"
                };

                for(int i = 0; i < 5; i++)
                {
                    ps.setString(1, ids[i]);
                    ps.setString(2, titles[i]);
                    ps.setString(3, authors[i]);
                    ps.setString(4, statuses[i]);

                    ps.executeUpdate();
                }

                ps.close();
            }

            rs.close();
            st.close();
            conn.close();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}