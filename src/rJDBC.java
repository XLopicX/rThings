import java.sql.*;

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
            DriverManager.getConnection(String.format("jdbc:oracle:thin:@//%s:%d//%s", this.hostname, this.port, this.SN), this.username, this.password);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found " + e);
        }
        catch (SQLException e) {
            System.out.println("Credentials wrong? " + e);
        }

    }
}
