import java.sql.*;

public class SoilAnalyzer {

    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/Soil_data";
        String user = "root";
        String password = "Sher@123";

        try {

            // Connect to MySQL
            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to database successfully!");

            // Retrieve soil data
            String query = "SELECT * FROM sangur_soil_data";

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            // Variables for calculations
            double totalPH = 0;
            double totalOC = 0;
            double totalEC = 0;

            double totalNa = 0;
            double totalK = 0;
            double totalCa = 0;
            double totalMg = 0;

            double minPH = Double.MAX_VALUE;
            double maxPH = Double.MIN_VALUE;

            int count = 0;

            System.out.println("\n================================");
            System.out.println("       SENSOR SOIL ANALYSIS");
            System.out.println("================================");

            // DEPTH-WISE ANALYSIS

            System.out.println("\n--- DEPTH-WISE ANALYSIS ---");

            while (rs.next()) {

                String horizon = rs.getString("Horizon");
                String depth = rs.getString("Depth");

                double ph = rs.getDouble("pH");
                double oc = rs.getDouble("O.C.");
                double ec = rs.getDouble("EC");

                double na = rs.getDouble("Na");
                double k = rs.getDouble("K");
                double ca = rs.getDouble("Ca");
                double mg = rs.getDouble("Mg");

                // Accumulate values
                totalPH += ph;
                totalOC += oc;
                totalEC += ec;

                totalNa += na;
                totalK += k;
                totalCa += ca;
                totalMg += mg;

                // Find minimum and maximum pH
                minPH = Math.min(minPH, ph);
                maxPH = Math.max(maxPH, ph);

                count++;

                // Display individual horizon data
                System.out.println("\nHorizon: " + horizon);
                System.out.println("Depth: " + depth + " cm");

                System.out.printf("pH: %.2f%n", ph);
                System.out.printf("Organic Carbon: %.2f%%%n", oc);
                System.out.printf("EC: %.4f%n", ec);

                System.out.printf("Sodium (Na): %.2f%n", na);
                System.out.printf("Potassium (K): %.2f%n", k);
                System.out.printf("Calcium (Ca): %.2f%n", ca);
                System.out.printf("Magnesium (Mg): %.2f%n", mg);

                // Individual pH interpretation
                if (ph < 6.5) {
                    System.out.println("pH Condition: Acidic");
                }
                else if (ph <= 7.5) {
                    System.out.println("pH Condition: Near Neutral");
                }
                else {
                    System.out.println("pH Condition: Alkaline");
                }

            }

            // OVERALL SOIL ANALYSIS

            if (count > 0) {

                double avgPH = totalPH / count;
                double avgOC = totalOC / count;
                double avgEC = totalEC / count;

                double avgNa = totalNa / count;
                double avgK = totalK / count;
                double avgCa = totalCa / count;
                double avgMg = totalMg / count;

                System.out.println("\n================================");
                System.out.println("       OVERALL SOIL ANALYSIS");
                System.out.println("================================");

                System.out.println("\n--- AVERAGE PARAMETERS ---");

                System.out.printf("Average pH: %.2f%n", avgPH);
                System.out.printf("Average Organic Carbon: %.2f%%%n", avgOC);
                System.out.printf("Average EC: %.4f%n", avgEC);

                System.out.printf("Average Sodium (Na): %.2f%n", avgNa);
                System.out.printf("Average Potassium (K): %.2f%n", avgK);
                System.out.printf("Average Calcium (Ca): %.2f%n", avgCa);
                System.out.printf("Average Magnesium (Mg): %.2f%n", avgMg);

                // SOIL INTERPRETATION

                System.out.println("\n--- SOIL INTERPRETATION ---");

                if (avgPH < 6.5) {
                    System.out.println("Overall Soil Condition: Acidic");
                }
                else if (avgPH <= 7.5) {
                    System.out.println("Overall Soil Condition: Near Neutral");
                }
                else {
                    System.out.println("Overall Soil Condition: Alkaline");
                }

                if (avgOC < 0.5) {
                    System.out.println("Organic Carbon Status: Low");
                }
                else if (avgOC <= 0.75) {
                    System.out.println("Organic Carbon Status: Medium");
                }
                else {
                    System.out.println("Organic Carbon Status: High");
                }

                // pH RANGE

                System.out.println("\n--- pH RANGE ---");

                System.out.printf("Minimum pH: %.2f%n", minPH);
                System.out.printf("Maximum pH: %.2f%n", maxPH);

                // DATA SUMMARY

                System.out.println("\n--- DATA SUMMARY ---");

                System.out.println("Total Soil Horizons: " + count);

            }

            else {
                System.out.println("No soil records found.");
            }

            // Close resources
            rs.close();
            stmt.close();
            con.close();

            System.out.println("\n================================");
            System.out.println("Analysis completed successfully!");
            System.out.println("================================");

        }

        catch (Exception e) {

            System.out.println("Database Error: " + e.getMessage());

        }

    }
}