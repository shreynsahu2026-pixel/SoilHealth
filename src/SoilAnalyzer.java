import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.sql.*;
import java.util.*;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

// ==========================================
// 1. SOIL RECORD - ENCAPSULATION
// ==========================================

class SoilRecord {

    private String horizon;
    private String depth;

    private double ph;
    private double oc;
    private double ec;

    private double na;
    private double k;
    private double ca;
    private double mg;

    public SoilRecord(String horizon, String depth,
                      double ph, double oc, double ec,
                      double na, double k, double ca, double mg) {

        this.horizon = horizon;
        this.depth = depth;

        this.ph = ph;
        this.oc = oc;
        this.ec = ec;

        this.na = na;
        this.k = k;
        this.ca = ca;
        this.mg = mg;
    }

    public String getHorizon() { return horizon; }
    public String getDepth() { return depth; }

    public double getPh() { return ph; }
    public double getOc() { return oc; }
    public double getEc() { return ec; }

    public double getNa() { return na; }
    public double getK() { return k; }
    public double getCa() { return ca; }
    public double getMg() { return mg; }
}


// ==========================================
// 2. ABSTRACT CLASS - ABSTRACTION
// ==========================================

abstract class SoilAnalysis {

    public abstract AnalysisResult analyze(List<SoilRecord> records);

}


// ==========================================
// 3. ANALYSIS RESULT - ENCAPSULATION
// ==========================================

class AnalysisResult {

    private double avgPH;
    private double avgOC;
    private double avgEC;

    private double avgNa;
    private double avgK;
    private double avgCa;
    private double avgMg;

    private double minPH;
    private double maxPH;

    private int totalHorizons;

    private String soilCondition;
    private String organicStatus;

    private List<SoilRecord> records;

    public AnalysisResult(double avgPH, double avgOC, double avgEC,
                          double avgNa, double avgK,
                          double avgCa, double avgMg,
                          double minPH, double maxPH,
                          int totalHorizons,
                          String soilCondition,
                          String organicStatus,
                          List<SoilRecord> records) {

        this.avgPH = avgPH;
        this.avgOC = avgOC;
        this.avgEC = avgEC;

        this.avgNa = avgNa;
        this.avgK = avgK;
        this.avgCa = avgCa;
        this.avgMg = avgMg;

        this.minPH = minPH;
        this.maxPH = maxPH;

        this.totalHorizons = totalHorizons;

        this.soilCondition = soilCondition;
        this.organicStatus = organicStatus;

        this.records = records;
    }

    // Convert results into JSON for the website

    public String toJson() {

        StringBuilder json = new StringBuilder();

        json.append("{");

        json.append("\"average\":{");

        json.append("\"pH\":").append(format(avgPH)).append(",");
        json.append("\"oc\":").append(format(avgOC)).append(",");
        json.append("\"ec\":").append(format(avgEC)).append(",");

        json.append("\"na\":").append(format(avgNa)).append(",");
        json.append("\"k\":").append(format(avgK)).append(",");
        json.append("\"ca\":").append(format(avgCa)).append(",");
        json.append("\"mg\":").append(format(avgMg));

        json.append("},");

        json.append("\"analysis\":{");

        json.append("\"soilCondition\":\"").append(soilCondition).append("\",");
        json.append("\"organicStatus\":\"").append(organicStatus).append("\",");

        json.append("\"minPH\":").append(format(minPH)).append(",");
        json.append("\"maxPH\":").append(format(maxPH)).append(",");

        json.append("\"totalHorizons\":").append(totalHorizons);

        json.append("},");

        json.append("\"horizons\":[");

        for (int i = 0; i < records.size(); i++) {

            SoilRecord r = records.get(i);

            if (i > 0) {
                json.append(",");
            }

            String condition;

            if (r.getPh() < 6.5) {
                condition = "Acidic";
            }
            else if (r.getPh() <= 7.5) {
                condition = "Near Neutral";
            }
            else {
                condition = "Alkaline";
            }

            json.append("{");

            json.append("\"horizon\":\"").append(escape(r.getHorizon())).append("\",");
            json.append("\"depth\":\"").append(escape(r.getDepth())).append("\",");

            json.append("\"pH\":").append(format(r.getPh())).append(",");
            json.append("\"oc\":").append(format(r.getOc())).append(",");
            json.append("\"ec\":").append(format(r.getEc())).append(",");

            json.append("\"na\":").append(format(r.getNa())).append(",");
            json.append("\"k\":").append(format(r.getK())).append(",");
            json.append("\"ca\":").append(format(r.getCa())).append(",");
            json.append("\"mg\":").append(format(r.getMg())).append(",");

            json.append("\"condition\":\"").append(condition).append("\"");

            json.append("}");
        }

        json.append("]}");

        return json.toString();
    }

    private String format(double value) {
        return String.format(Locale.US, "%.4f", value);
    }

    private String escape(String value) {

        if (value == null) return "";

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}


// ==========================================
// 4. SOIL ANALYZER
// INHERITANCE + POLYMORPHISM
// ==========================================

public class SoilAnalyzer extends SoilAnalysis {

    private static final String URL =
        "jdbc:mysql://localhost:3306/Soil_data";

    private static final String USER = "root";

    private static final String PASSWORD =
        System.getenv("MYSQL_PASSWORD");


    // Read data from MySQL

    public List<SoilRecord> readSoilData() throws Exception {

        List<SoilRecord> records = new ArrayList<>();

        String query = "SELECT * FROM sangur_soil_data";

        try (
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query)
        ) {

            while (rs.next()) {

                SoilRecord record = new SoilRecord(

                    rs.getString("Horizon"),
                    rs.getString("Depth"),

                    rs.getDouble("pH"),
                    rs.getDouble("O.C."),
                    rs.getDouble("EC"),

                    rs.getDouble("Na"),
                    rs.getDouble("K"),
                    rs.getDouble("Ca"),
                    rs.getDouble("Mg")

                );

                records.add(record);
            }
        }

        return records;
    }


    // POLYMORPHISM:
    // Overriding the abstract method from SoilAnalysis

    @Override
    public AnalysisResult analyze(List<SoilRecord> records) {

        double totalPH = 0;
        double totalOC = 0;
        double totalEC = 0;

        double totalNa = 0;
        double totalK = 0;
        double totalCa = 0;
        double totalMg = 0;

        double minPH = Double.MAX_VALUE;
        double maxPH = -Double.MAX_VALUE;

        for (SoilRecord r : records) {

            totalPH += r.getPh();
            totalOC += r.getOc();
            totalEC += r.getEc();

            totalNa += r.getNa();
            totalK += r.getK();
            totalCa += r.getCa();
            totalMg += r.getMg();

            minPH = Math.min(minPH, r.getPh());
            maxPH = Math.max(maxPH, r.getPh());
        }

        int count = records.size();

        if (count == 0) {
            throw new IllegalStateException("No soil records found.");
        }

        double avgPH = totalPH / count;
        double avgOC = totalOC / count;
        double avgEC = totalEC / count;

        double avgNa = totalNa / count;
        double avgK = totalK / count;
        double avgCa = totalCa / count;
        double avgMg = totalMg / count;

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

        return new AnalysisResult(

            avgPH, avgOC, avgEC,
            avgNa, avgK, avgCa, avgMg,
            minPH, maxPH,
            count,
            soilCondition,
            organicStatus,
            records
        );
    }


    // Start HTTP server

    public static void main(String[] args) {

        try {

            SoilAnalyzer soilAnalyzer = new SoilAnalyzer();

            // Polymorphism: parent reference, child object

            SoilAnalysis analyzer = soilAnalyzer;

            // Test database connection

            soilAnalyzer.readSoilData();

            System.out.println("Connected to database successfully!");

            HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
            );

                server.createContext("/api/signup", exchange -> {

                // Allow website to communicate with Java
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
                );

                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Methods", "POST, OPTIONS"
                );

                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Headers", "Content-Type"
                );

                // Handle browser preflight request
                if (exchange.getRequestMethod().equals("OPTIONS")) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                try {

                    String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                    );

                    Map<String, String> fields = new HashMap<>();

                    for (String pair : body.split("&")) {

                        String[] parts = pair.split("=", 2);

                        if (parts.length == 2) {
                            fields.put(
                                URLDecoder.decode(parts[0], "UTF-8"),
                                URLDecoder.decode(parts[1], "UTF-8")
                            );
                        }
                    }

                    String username = fields.get("username");
                    String email = fields.get("email");
                    String password = fields.get("password");

                    if (username == null || email == null || password == null
                            || username.isBlank() || email.isBlank()
                            || password.isBlank()) {

                        sendResponse(exchange, 400,
                            "{\"message\":\"All fields are required.\"}");

                        return;
                    }

                    Auth.register(username, email, password);

                    sendResponse(exchange, 200,
                        "{\"message\":\"Account created successfully!\"}");

                } catch (java.sql.SQLIntegrityConstraintViolationException e) {

                    sendResponse(exchange, 409,
                        "{\"message\":\"Username or email already exists.\"}");

                } catch (Exception e) {

                    e.printStackTrace();

                    sendResponse(exchange, 500,
                        "{\"message\":\"Registration failed.\"}");

                } finally {
                    exchange.close();
                }

            });
                                
            server.createContext("/api/soil", exchange -> {

                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
                );

                exchange.getResponseHeaders().add(
                    "Content-Type", "application/json; charset=UTF-8"
                );

                if (exchange.getRequestMethod().equals("OPTIONS")) {

                    exchange.getResponseHeaders().add(
                        "Access-Control-Allow-Methods", "GET, OPTIONS"
                    );

                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                if (!exchange.getRequestMethod().equals("GET")) {

                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                    return;
                }

                try {

                    List<SoilRecord> records = soilAnalyzer.readSoilData();

                    AnalysisResult result = analyzer.analyze(records);

                    byte[] response = result.toJson().getBytes("UTF-8");

                    exchange.sendResponseHeaders(200, response.length);

                    exchange.getResponseBody().write(response);

                }
                catch (Exception e) {

                    e.printStackTrace();

                    byte[] error = "{\"error\":\"Database error\"}"
                        .getBytes("UTF-8");

                    exchange.sendResponseHeaders(500, error.length);

                    exchange.getResponseBody().write(error);

                }
                finally {

                    exchange.close();
                }

            });

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
    private static void sendResponse(
        com.sun.net.httpserver.HttpExchange exchange,
        int status,
        String message) throws IOException {

    byte[] response = message.getBytes(StandardCharsets.UTF_8);

    exchange.getResponseHeaders().set(
        "Content-Type", "application/json; charset=UTF-8"
    );

    exchange.sendResponseHeaders(status, response.length);

    exchange.getResponseBody().write(response);
}
}
