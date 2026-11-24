import java.sql.*;
import java.util.Locale;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class SoilAnalyzer {

    static String url = "jdbc:mysql://localhost:3306/Soil_data";
    static String user = "root";
    static String password = System.getenv("MYSQL_PASSWORD");

    public static void main(String[] args) {

        try {

            // Test database connection
            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Connected to database successfully!");

            con.close();

            // Create HTTP server
            HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
            );

            // Create API endpoint
            server.createContext("/api/soil", exchange -> {

                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
                );

                exchange.getResponseHeaders().add(
                    "Content-Type", "application/json; charset=UTF-8"
                );

                if (!exchange.getRequestMethod().equals("GET")) {
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                    return;
                }

                try {

                    String json = getSoilData();

                    byte[] response = json.getBytes("UTF-8");

                    exchange.sendResponseHeaders(200, response.length);

                    exchange.getResponseBody().write(response);

                } catch (Exception e) {

                    String error = "{\"error\":\"Database error\"}";

                    byte[] response = error.getBytes("UTF-8");

                    exchange.sendResponseHeaders(500, response.length);

                    exchange.getResponseBody().write(response);

                    e.printStackTrace();

                } finally {

                    exchange.close();

                }

            });

            // Start server
            server.start();

            System.out.println("\n================================");
            System.out.println("    SOIL ANALYZER SERVER STARTED");
            System.out.println("================================");

            System.out.println("API: http://localhost:8080/api/soil");
            System.out.println("Waiting for website requests...");

        }

        catch (Exception e) {

            System.out.println("Server Error: " + e.getMessage());

        }

    }


    // Retrieve soil data and perform calculations

    public static String getSoilData() throws Exception {

        Connection con = DriverManager.getConnection(url, user, password);

        String query = "SELECT * FROM sangur_soil_data";

        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery(query);

        double totalPH = 0;
        double totalOC = 0;
        double totalEC = 0;

        double totalNa = 0;
        double totalK = 0;
        double totalCa = 0;
        double totalMg = 0;

        double minPH = Double.MAX_VALUE;
        double maxPH = -Double.MAX_VALUE;

        int count = 0;

        StringBuilder horizons = new StringBuilder();

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

            // Individual pH interpretation

            String condition;

            if (ph < 6.5) {
                condition = "Acidic";
            }
            else if (ph <= 7.5) {
                condition = "Near Neutral";
            }
            else {
                condition = "Alkaline";
            }

            // Store individual horizon data as JSON

            if (horizons.length() > 0) {
                horizons.append(",");
            }

            horizons.append("{");

            horizons.append("\"horizon\":\"")
                    .append(jsonEscape(horizon)).append("\",");

            horizons.append("\"depth\":\"")
                    .append(jsonEscape(depth)).append("\",");

            horizons.append("\"pH\":").append(ph).append(",");
            horizons.append("\"oc\":").append(oc).append(",");
            horizons.append("\"ec\":").append(ec).append(",");

            horizons.append("\"na\":").append(na).append(",");
            horizons.append("\"k\":").append(k).append(",");
            horizons.append("\"ca\":").append(ca).append(",");
            horizons.append("\"mg\":").append(mg).append(",");

            horizons.append("\"condition\":\"")
                    .append(condition).append("\"");

            horizons.append("}");

        }

        rs.close();
        stmt.close();
        con.close();

        if (count == 0) {
            return "{\"error\":\"No soil records found\"}";
        }

        // Calculate averages

        double avgPH = totalPH / count;
        double avgOC = totalOC / count;
        double avgEC = totalEC / count;

        double avgNa = totalNa / count;
        double avgK = totalK / count;
        double avgCa = totalCa / count;
        double avgMg = totalMg / count;

        // Overall soil interpretation

        String soilCondition;

        if (avgPH < 6.5) {
            soilCondition = "Acidic";
        }
        else if (avgPH <= 7.5) {
            soilCondition = "Near Neutral";
        }
        else {
            soilCondition = "Alkaline";
        }

        // Organic carbon interpretation

        String organicStatus;

        if (avgOC < 0.5) {
            organicStatus = "Low";
        }
        else if (avgOC <= 0.75) {
            organicStatus = "Medium";
        }
        else {
            organicStatus = "High";
        }

        // Construct JSON response

        StringBuilder json = new StringBuilder();

        json.append("{");

        // Average parameters

        json.append("\"average\":{");

        json.append("\"pH\":").append(format(avgPH)).append(",");
        json.append("\"oc\":").append(format(avgOC)).append(",");
        json.append("\"ec\":").append(format(avgEC)).append(",");

        json.append("\"na\":").append(format(avgNa)).append(",");
        json.append("\"k\":").append(format(avgK)).append(",");
        json.append("\"ca\":").append(format(avgCa)).append(",");
        json.append("\"mg\":").append(format(avgMg));

        json.append("},");

        // Overall analysis

        json.append("\"analysis\":{");

        json.append("\"soilCondition\":\"")
            .append(soilCondition).append("\",");

        json.append("\"organicStatus\":\"")
            .append(organicStatus).append("\",");

        json.append("\"minPH\":").append(format(minPH)).append(",");
        json.append("\"maxPH\":").append(format(maxPH)).append(",");

        json.append("\"totalHorizons\":").append(count);

        json.append("},");

        // Depth-wise data

        json.append("\"horizons\":[");
        json.append(horizons);
        json.append("]");

        json.append("}");

        return json.toString();

    }


    // Format decimal values

    private static String format(double value) {

        return String.format(Locale.US, "%.4f", value);

    }


    // Escape special characters in JSON strings

    private static String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");

    }

}