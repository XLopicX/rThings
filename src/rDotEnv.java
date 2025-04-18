import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class rDotEnv {
    private String path;
    private Map<String, String> items = new HashMap<>();

    public rDotEnv(String path) {
        this.path = path;
        load();
    }

    public void load() {
        try {
            FileReader fr = new FileReader(this.path);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(":"); // 0: la key | 1: el valor (com un dotenv normal)
                this.items.put(values[0], values[1]);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String get(String key) {
        return this.items.get(key);
    }
}
