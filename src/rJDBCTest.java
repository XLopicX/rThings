import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class rJDBCTest {
    static rDotEnv dotenv = new rDotEnv(".env");
    static rJDBC db = new rJDBC(dotenv.get("host"), Integer.parseInt(dotenv.get("port")), dotenv.get("sn"), dotenv.get("user"), dotenv.get("pass")); // La base de dades per aquestes probes es la de les docs de oracle.

    @BeforeAll
    static void connect() {
        db.connect();
    }

    @Test
    @Order(1)
    void select() { // bad example lazy to make new one 3:
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

    @Test
    @Order(2)
    void update() {
        String sentenciaSel = new rJDBC.SQLSelectBuilder()
                .select("department_id")
                .from("departments")
                .where("department_name = 'Marketing'")
                .build();

        String sentenciaUpd = new rJDBC.SQLUpdateBuilder()
                .update("employees")
                .set("department_id", sentenciaSel)
                .where("last_name = 'King'") // Rip brother (demoted)
                .build();

        assertDoesNotThrow(() -> db.update(sentenciaUpd));
    }

    @Test
    @Order(3)
    void delete() {
        String sentenciaDel = new rJDBC.SQLDeleteBuilder()
                .from("employees")
                .where("employee_id = 100") // Bro really fucked up this time
                .build();

        assertDoesNotThrow(() -> db.delete(sentenciaDel));
    }

    @Test
    @Order(4)
    void insert() {
        String sentenciaIns = new rJDBC.SQLInsertBuilder()
                .insert(
                        "employee_id", "100",
                        "first_name", "'Steven'",
                        "last_name", "'King'",
                        "email", "'SKING'",
                        "phone_number", "'515.123.4567'",
                        "hire_date", "'17/06/1987'",
                        "job_id", "'AD_PRES'",
                        "salary", "24000",
                        "commission_pct", "null",
                        "manager_id", "null",
                        "department_id", "90"
                )
                .into("employees")
                .build();

        assertDoesNotThrow(() -> db.insert(sentenciaIns));
    }
}