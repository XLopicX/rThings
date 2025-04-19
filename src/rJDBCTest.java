import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class rJDBCTest {
    static rDotEnv dotenv = new rDotEnv(".env");
    static rJDBC db = new rJDBC(dotenv.get("host"), Integer.parseInt(dotenv.get("port")), dotenv.get("sn"), dotenv.get("user"), dotenv.get("pass"));

    @BeforeAll
    static void connect() {
        db.connect();
    }

    @Test
    @Order(1)
    void select() {
        String firstQuery = new rJDBC.SQLSelectBuilder()
                .select("FIRST_NAME", "LAST_NAME")
                .from("EMPLOYEES")
                .where("LAST_NAME LIKE 'R%'")
                .order("FIRST_NAME DESC")
                .build();
        ResultSet firstExample = db.select(firstQuery);
        try {
            if (firstExample.isBeforeFirst()) {
                while (firstExample.next()) {
                    String nom = firstExample.getString("FIRST_NAME") + " " + firstExample.getString("LAST_NAME");
                    assertEquals("Trenna Rajs", nom);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}