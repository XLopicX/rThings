import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        rDotEnv dotenv = new rDotEnv(".env");
        rJDBC db = new rJDBC(dotenv.get("host"), Integer.parseInt(dotenv.get("port")), dotenv.get("sn"), dotenv.get("user"), dotenv.get("pass"));
        db.connect();

    }
}