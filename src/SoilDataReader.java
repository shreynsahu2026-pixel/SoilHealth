import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SoilDataReader {

    public static void main(String[] args) {

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/Soil_data";
        String user = "root";
        String password = "Sher@123";

        try {

            // Connect to MySQL
            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to MySQL successfully!");

            Statement stmt = con.createStatement();

            // Fetch Sangur data
            String query = "SELECT * FROM sangur_soil_data";

            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--- SANGUR SOIL DATA ---");

            while (rs.next()) {

                System.out.println(
                    rs.getString("Horizon") + " | " +
                    rs.getString("Depth") + " | " +
                    rs.getDouble("pH") + " | " +
                    rs.getDouble("EC") + " | " +
                    rs.getDouble("Na") + " | " +
                    rs.getDouble("K") + " | " +
                    rs.getDouble("Ca") + " | " +
                    rs.getDouble("Mg")
                );

            }

            rs.close();

            // Fetch Gangotali data
            query = "SELECT * FROM gangotali_series";

            rs = stmt.executeQuery(query);

            System.out.println("\n--- GANGOTOLI SOIL DATA ---");

            while (rs.next()) {

                System.out.println(
                    rs.getString("Horizon") + " | " +
                    rs.getString("Depth") + " | " +
                    rs.getDouble("pH") + " | " +
                    rs.getDouble("EC") + " | " +
                    rs.getDouble("Na") + " | " +
                    rs.getDouble("K") + " | " +
                    rs.getDouble("Ca") + " | " +
                    rs.getDouble("Mg")
                );

            }

            rs.close();
            stmt.close();
            con.close();

            System.out.println("\nDatabase connection closed.");

        }

        catch (Exception e) {

            System.out.println("Error: " + e.getMessage());

        }

    }
}
