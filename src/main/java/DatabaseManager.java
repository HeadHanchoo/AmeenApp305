import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;




public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/data/ameen.db";


    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS faq (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question TEXT NOT NULL,
                    answer TEXT NOT NULL
                );
                """;
            stmt.execute(sql);
            System.out.println("✅ FAQ table ready.");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
    public static void insertSampleFAQs() {
        String insertSQL = "INSERT INTO faq (question, answer) VALUES (?, ?)";
        String checkSQL = "SELECT COUNT(*) FROM faq WHERE LOWER(TRIM(question)) = LOWER(TRIM(?))";


        try (Connection conn = connect()) {
            PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
            PreparedStatement checkStmt = conn.prepareStatement(checkSQL);

            String[][] sampleData = {
                    {"How do I renew my ID?",
                            "1. Open the Absher platform.\n2. Go to 'Civil Affairs'.\n3. Select 'ID Renewal'."},

                    {"How can I check my traffic violations?",
                            "1. Log in to Absher.\n2. Click on 'Traffic Violations' under 'Services'."},

                    {"How do I book a hospital appointment?",
                            "1. Open the Sehatty app.\n2. Go to 'Health Services'.\n3. Select 'Book Appointment'."},

                    {"How do I change my mobile number in Absher?",
                            "1. Log in to Absher.\n2. Go to 'Settings' > 'User Info'.\n3. Choose 'Edit Mobile Number'."},

                    {"How do I report a technical issue?",
                            "1. Open the Absher app or website.\n2. Go to the 'Technical Support' section."},

                    {"Where can I find my digital ID?",
                            "1. Open the Absher app.\n2. Go to 'My Services'.\n3. Tap on 'Digital ID'."},

                    {"How do I view my National ID vaccination record in Tawakkalna?",
                            "1. Open the Tawakkalna app.\n2. Go to your profile.\n3. Tap 'Health Passport'."},

                    {"How do I pay a traffic fine?",
                            "1. Check your fines in Absher.\n2. Pay using your bank’s app or an ATM."},

                    {"How do I report a lost official document?",
                            "1. Log in to Absher.\n2. Go to 'Civil Affairs'.\n3. Choose 'Report Lost Document'."},

                    {"How do I track a mail delivery from Saudi Post?",
                            "1. Use the SPL (Saudi Post) app.\n2. Enter your tracking number."},

                    {"How do I report a car accident?",
                            "1. Use the Najm app.\n2. Log in.\n3. Click on 'Report New Accident'."},

                    {"How do I apply for a business license?",
                            "1. Go to the Balady platform.\n2. Log in.\n3. Apply under 'Municipal Licenses'."},

                    {"How do I get a COVID-19 travel permit?",
                            "1. Open the Tawakkalna app.\n2. Check the 'Permits' section."},

                    {"How do I file a health insurance claim?",
                            "1. Open the Tawuniya app.\n2. Select your policy.\n3. Choose 'Submit Claim'."},

                    {"How do I renew my vehicle registration?",
                            "1. Open Absher.\n2. Go to 'Vehicles'.\n3. Select 'Renew Registration'."},

                    {"How do I update my address in the national system?",
                            "1. Use the National Address Platform or SPL.\n2. Log in.\n3. Update your details."},

                    {"How do I register a new driver?",
                            "1. Log in to Absher.\n2. Go to 'Vehicles' > 'Add Driver'.\n3. Fill in the required info."},

                    {"How do I view and download my electricity bill?",
                            "1. Open the SECO (Saudi Electricity Company) app or website.\n2. Go to 'Bills'."}
            };

            for (String[] qa : sampleData) {
                checkStmt.setString(1, qa[0]);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    insertStmt.setString(1, qa[0]);
                    insertStmt.setString(2, qa[1]);
                    insertStmt.executeUpdate();
                }
            }

            System.out.println("✅ Sample FAQs inserted (without duplicates).");
        } catch (SQLException e) {
            System.out.println("❌ Error inserting sample FAQs: " + e.getMessage());
        }
    }
    public static void clearFAQs() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM faq");
            System.out.println("🧹 FAQ table cleared.");
        } catch (SQLException e) {
            System.out.println("❌ Error clearing FAQs: " + e.getMessage());
        }
    }
    public static boolean faqTableIsEmpty() {
        String checkSQL = "SELECT COUNT(*) FROM faq";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSQL)) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.out.println("❌ Error checking if FAQ table is empty: " + e.getMessage());
            return false;
        }
    }





}
