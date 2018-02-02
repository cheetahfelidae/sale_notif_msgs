import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This is a class for processing sales notification messages.
 */
public class SalesProcess {
    private LogFile logFile;
    private ArrayList<Msg> stored_msgs;
    private Hashtable<String, Integer> num_sales_products;
    private ArrayList<Adjustment> adjust_products;

    SalesProcess(LogFile logFile) {
        this.logFile = logFile;
        stored_msgs = new ArrayList<>();
        num_sales_products = new Hashtable<>();
        adjust_products = new ArrayList<>();
    }

    private boolean is_int(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), 10) < 0) return false;
        }
        return true;
    }
    // https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java

    private boolean is_float(String s) {
        try {
            Float.parseFloat(s.replace(',', '.'));
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    //https://stackoverflow.com/questions/39182829/how-to-check-if-a-string-is-parsable-to-float

    /**
     * Increment the number of sales of the product by a desired number.
     *
     * @param name
     * @param num_sales
     */
    private void update_num_sales(String name, int num_sales) {
        Integer prev_num_sales = num_sales_products.get(name);
        if (prev_num_sales != null) {
            num_sales_products.put(name, prev_num_sales + num_sales);
        } else {
            num_sales_products.put(name, num_sales);
        }
    }

    /**
     * Update a value of the product with a desire adjustment operation (add, subtract or multiply).
     *
     * @param name
     * @param value
     * @param oper
     */
    private void update_value(String name, float value, String oper) {
        for (Msg msg : stored_msgs) {
            if (msg.getName().equals(name)) {
                switch (oper) {
                    case "add":
                        msg.setValue(msg.getValue() + value);
                        break;
                    case "subtract":
                        msg.setValue(msg.getValue() - value);
                        break;
                    case "multiply":
                        msg.setValue(msg.getValue() * value);
                        break;
                    default:
                }
            }
        }
    }

    /**
     * Get a value of the product by its name.
     *
     * @param name
     * @return
     */
    private float get_value(String name) {
        for (int i = 0; i < stored_msgs.size(); i++) {
            Msg msg = stored_msgs.get(i);
            if (msg.getName().equals(name)) {
                return msg.getValue();
            }
        }
        return -1;
    }

    /**
     * Log a report detailing the number of sales of each product and their total value.
     */
    private void log_num_sales_and_totals() {
        Set<Entry<String, Integer>> entries = num_sales_products.entrySet();
        for (Entry<String, Integer> ent : entries) {
            String str = "Name: " + ent.getKey() + "\t\t#Msg: " + ent.getValue() + "\t\tTotal: " + get_value(ent.getKey()) * ent.getValue();
            logFile.logInfo(str);
            System.out.println(str);
        }
    }

    /**
     * Log a report of the adjustments that have been made to each sale type while the application was running.
     */
    private void log_adjustments() {
        for (Adjustment adj : adjust_products) {
            String str = "Name: " + adj.getName() + "\t\t#Operation: " + adj.getOper() + "\t\tValue: " + adj.getValue();
            logFile.logInfo(str);
            System.out.println(str);
        }
    }

    /**
     * Attempt to read the CSV file specified by filename.
     * Extract data from each line and categories it to each type of message.
     * Record all messages
     * After every 10th message received, the application will log a report detailing the number of sales of each product and their total value.
     * After 50 messages, the application will log that it is pausing and stops accepting new messages and log a report of the adjustments that have been made to each sale type while the application was running.
     *
     * @param file_name of the file to read
     */
    public void read_file(String file_name) throws IOException {
        FileReader fr = new FileReader(file_name);
        BufferedReader bfr = new BufferedReader(fr);

        String line;
        while ((line = bfr.readLine()) != null) {
            String[] tokens = line.split(",");

            String name = tokens[0];
            Float value = is_float(tokens[1]) ? Float.parseFloat(tokens[1]) : null;

            // message type 1
            if (tokens.length == 2) {
                if (!name.isEmpty() && value != null) {
                    stored_msgs.add(new Msg(name, value));
                    update_num_sales(name, 1);
                }
            }
            // message type 2
            else if (tokens.length == 3) {
                Integer num_sales = is_int(tokens[2]) ? Integer.parseInt(tokens[2]) : null;

                if (!name.isEmpty() && value != null && num_sales != null) {
                    stored_msgs.add(new Msg(name, value, num_sales));
                    update_num_sales(name, num_sales);
                }
            }
            // message type 3
            else if (tokens.length == 4) {
                Integer num_sales = is_int(tokens[2]) ? Integer.parseInt(tokens[2]) : null;
                String oper = tokens[3];

                if (!name.isEmpty() && value != null && num_sales == null && !oper.isEmpty()) {
                    adjust_products.add(new Adjustment(name, oper, value));
                    update_value(name, value, oper);
                }
            }

            if (stored_msgs.size() > 0) {

                // log a report after every 10th messages
                if (stored_msgs.size() % 10 == 0) {
                    log_num_sales_and_totals();
                }

                // stop the process and log a report after 50 messages
                if (stored_msgs.size() % 50 == 0) {
                    log_adjustments();
                    String str = "The application has reached 50 messages and is no longer accepting new messages!";
                    logFile.logInfo(str);
                    System.out.println(str);
                    break;
                }
            }

        }


    }
}
