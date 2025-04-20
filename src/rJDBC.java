import java.sql.*;
import java.util.HashMap;
import java.util.Map;

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

    public void update(String query) {
        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String query) { // codi repetit pero quedara mes net aixi
        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String query) { // codi repetit pero quedara mes net aixi
        try {
            Statement st = con.createStatement();
            st.executeUpdate(query);
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

    public static class SQLUpdateBuilder {
        private String taula = "";
        private String set = "";
        private String condicio = "";

        public SQLUpdateBuilder update(String taula) {
            this.taula = taula;
            return this;
        }

        public SQLUpdateBuilder set(String... valors) {
            Map<String, String> sets = new HashMap<>();

            for (int i = 0; i < valors.length; i += 2) { // Necessitem salts de 2 per separar les keys i els valors
                if (valors[i + 1].startsWith("SELECT") && valors[i + 1].contains("FROM"))  { // Tindria que ser suficient per a detectar si es una select dins un set
                    sets.put(valors[i], String.format("(%s)", valors[i + 1]));
                }
                else {
                    sets.put(valors[i], valors[i + 1]);
                }
            }

            boolean first = true;
            for (Map.Entry<String, String> entry : sets.entrySet()) {
                if (first) {
                    set += "SET " + entry.getKey() + " = " + entry.getValue();
                    first = false;
                } else {
                    set += ", " + entry.getKey() + " = " + entry.getValue();
                }
            }
            return this;
        }

        public SQLUpdateBuilder where(String condicio) {
            if (this.condicio.equals("")) {
                this.condicio += "WHERE " + condicio;
            }
            else {
                this.condicio += " AND " + condicio;
            }
            return this;
        }

        public String build() {
            return String.format("UPDATE %s %s %s", this.taula, this.set, this.condicio);
        }
    }

    public static class SQLDeleteBuilder {
        private String taula = "";
        private String condicio = "";

        public SQLDeleteBuilder from(String taula) {
            this.taula = taula;
            return this;
        }


        public SQLDeleteBuilder where(String condicio) {
            if (this.condicio.equals("")) {
                this.condicio += "WHERE " + condicio;
            }
            else {
                this.condicio += " AND " + condicio;
            }
            return this;
        }

        public String build() {
            return String.format("DELETE FROM %s %s", this.taula, this.condicio);
        }
    }

    public static class SQLInsertBuilder {
        private String taula = "";
        private String columnes = "";
        private String valors = "";
        private String subquery = "";

        public SQLInsertBuilder insert(String... items) { // todo: fer servir hashmap amb 2n valor com a objecte per intentar veure si es possible acceptar diferents tipus i identificar'ho posteriorment al codi per fer el format
            boolean first = true;

            for (int i = 0; i < items.length; i+=2) { // Pq anirem pillant columnes i valors d'aqui els salts de 2
                // Oracle es molt pussy i no accepta DD/MM/YYYY per tant ho adapto detectant amb un regex quan el user introdueix una data
                if (items[i + 1].matches("'\\d{2}/\\d{2}/\\d{4}'")) {
                    items[i + 1] = String.format("TO_DATE(%s, 'DD/MM/YYYY')", items[i + 1]); // no poso els '' a la data perque ja els portara
                }
                if (first) {
                    this.columnes += items[i];
                    this.valors += items[i + 1];
                    first = false;
                }
                else {
                    this.columnes += ", " + items[i];
                    this.valors += ", " + items[i + 1];
                }
            }

            return this;
        }

        public SQLInsertBuilder into(String taula) { // All? what's that
            this.taula = taula;
            return this;
        }

        public SQLInsertBuilder subquery(String subquery) { // Probablement no vols fer servir aixo pq ni ho he provat. lol
            this.subquery = subquery;
            return this;
        }

        public String build() {
            if (this.subquery.isEmpty()) {
                return String.format("INSERT INTO %s (%s) VALUES (%s)", this.taula, this.columnes, this.valors);
            }
            else {
                return String.format("INSERT INTO %s (%s) %s", this.taula, this.columnes, this.subquery);
            }

        }
    }
}
