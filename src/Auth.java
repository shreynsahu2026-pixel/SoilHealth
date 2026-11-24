import java.sql.*;
import java.security.*;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

public class Auth {

    private static final String URL =
        "jdbc:mysql://localhost:3306/Soil_data";

    private static final String USER = "root";

    private static final String PASSWORD =
        System.getenv("MYSQL_PASSWORD");

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;


    // =================================
    // PASSWORD HASHING
    // =================================

    private static String hashPassword(char[] password)
            throws Exception {

        byte[] salt = new byte[16];

        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
            password, salt, ITERATIONS, KEY_LENGTH
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] hash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return ITERATIONS + ":"
            + Base64.getEncoder().encodeToString(salt) + ":"
            + Base64.getEncoder().encodeToString(hash);
    }


    // =================================
    // PASSWORD VERIFICATION
    // =================================

    private static boolean verifyPassword(
            char[] password, String storedHash) throws Exception {

        String[] parts = storedHash.split(":");

        int iterations = Integer.parseInt(parts[0]);

        byte[] salt = Base64.getDecoder().decode(parts[1]);

        byte[] originalHash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(
            password, salt, iterations, KEY_LENGTH
        );

        SecretKeyFactory factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        byte[] newHash = factory.generateSecret(spec).getEncoded();

        spec.clearPassword();

        return MessageDigest.isEqual(originalHash, newHash);
    }


    // =================================
    // USER REGISTRATION
    // =================================

    public static boolean register(
            String username, String email, String password)
            throws Exception {

        String hashedPassword = hashPassword(password.toCharArray());

        String query =
            "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection con =
                DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);

            stmt.executeUpdate();

            return true;
        }
    }


    // =================================
    // USER LOGIN
    // =================================

    public static boolean login(String email, String password)
            throws Exception {

        String query =
            "SELECT password_hash FROM users WHERE email = ?";

        try (Connection con =
                DriverManager.getConnection(URL, USER, PASSWORD);

             PreparedStatement stmt = con.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    String storedHash = rs.getString("password_hash");

                    return verifyPassword(
                        password.toCharArray(), storedHash
                    );
                }
            }
        }

        return false;
    }

}