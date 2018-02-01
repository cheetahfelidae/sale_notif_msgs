import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    private static final String DEFAULT_LOG_FILE = System.getProperty("user.dir") + "/log"; // log file will in the current directory
    private static final String MSG_FILE = "/Users/cheetah/Sites/sales_notif_msgs/msgs.csv";

    public static void main(String[] args) {
        try {
            SalesProcess salesProcess = new SalesProcess(new LogFile(DEFAULT_LOG_FILE));
            salesProcess.read_file(MSG_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O Ooops: " + e.getMessage());
        }
    }

}
