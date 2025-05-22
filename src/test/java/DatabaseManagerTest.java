import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @BeforeEach
    void resetDB() {
        DatabaseManager.initializeDatabase();
        DatabaseManager.clearFAQs();
    }

    @Test
    void testInsertSampleFAQs() {
        assertTrue(DatabaseManager.faqTableIsEmpty(), "Table should start empty");

        DatabaseManager.insertSampleFAQs();
        assertFalse(DatabaseManager.faqTableIsEmpty(), "Table should not be empty after insert");
    }

    @Test
    void testConnectionNotNull() throws Exception {
        Connection conn = DatabaseManager.connect();
        assertNotNull(conn, "Database connection should not be null");
        conn.close();
    }
}
