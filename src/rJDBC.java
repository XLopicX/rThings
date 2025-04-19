import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class rJDBC {
    private String hostname;
    private int port;
    private String SN;
    private String username;
    private String password;
    private Connection con;

    public rJDBC(String hostname, int port, String SN, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.SN = SN;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.con = DriverManager.getConnection(String.format("jdbc:oracle:thin:@//%s:%d/%s", this.hostname, this.port, this.SN), this.username, this.password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found " + e);
        }
        catch (SQLException e) {
            throw new RuntimeException("Credentials wrong? " + e);
        }
    }

    public ResultSet select(String query) {
        try {
            Statement st = con.createStatement();
            return st.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SQLSelectBuilder {
        private String columnes = "";
        private String taula = "";
        private String condicio = "";
        private String groupby = "";
        private String orderby = "";

        public SQLSelectBuilder select(String... columnes) {
            this.columnes += String.join(",", columnes);
            return this;
        }

        public SQLSelectBuilder from(String taula) {
            this.taula = taula;
            return this;
        }

        public SQLSelectBuilder where(String condicio) {
            if (this.condicio.equals("")) {
                this.condicio += "WHERE " + condicio;
            }
            else {
                this.condicio += " AND " + condicio;
            }
            return this;
        }

        public SQLSelectBuilder group(String groupby) {
            if (this.groupby.equals("")) {
                this.groupby += "GROUP BY " + groupby;
            }
            else {
                this.groupby += ", " + groupby;
            }
            return this;
        }

        public SQLSelectBuilder order(String orderby) {
            if (this.orderby.equals("")) {
                this.orderby += "ORDER BY " + orderby;
            }
            else {
                this.orderby += ", " + orderby;
            }
            return this;
        }

        public String build() {
            return String.format("SELECT %s FROM %s %s %s %s", this.columnes, this.taula, this.condicio, this.groupby, this.orderby);
        }
    }
}
