import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class SalesProcess {
    private LogFile logFile;
    private ArrayList<Sale> stored_sales;
    private Hashtable<String, Integer> num_sales_products;
    private ArrayList<Adjustment> adjust_products;

    SalesProcess(LogFile logFile) {
        this.logFile = logFile;
        stored_sales = new ArrayList<>();
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

    private void update_num_sales(String name, int num_sales) {
        Integer prev_num_sales = num_sales_products.get(name);
        if (prev_num_sales != null) {
            num_sales_products.put(name, prev_num_sales + num_sales);
        } else {
            num_sales_products.put(name, num_sales);
        }
    }

    private void update_value(String name, float value, String oper) {
        for (Sale sale : stored_sales) {
            if (sale.getName().equals(name)) {
                switch (oper) {
                    case "add":
                        sale.setValue(sale.getValue() + value);
                        break;
                    case "subtract":
                        sale.setValue(sale.getValue() - value);
                        break;
                    case "multiply":
                        sale.setValue(sale.getValue() * value);
                        break;
                    default:
                }
            }
        }
    }

    private float get_value(String name) {
        for (int i = 0; i < stored_sales.size(); i++) {
            Sale sale = stored_sales.get(i);
            if (sale.getName().equals(name)) {
                return sale.getValue();
            }
        }
        return -1;
    }

    /**
     * log a report detailing the number of sales of each product and their total value.
     */
    private void log_num_sales_and_totals() {
        Set<Entry<String, Integer>> entries = num_sales_products.entrySet();
        for (Entry<String, Integer> ent : entries) {
            String str = "Name: " + ent.getKey() + "\t\t#Sale: " + ent.getValue() + "\t\tTotal: " + get_value(ent.getKey()) * ent.getValue();
            logFile.logInfo(str);
            System.out.println(str);
        }
    }

    private void log_adjustments() {
        for (Adjustment adj : adjust_products) {
            String str = "Name: " + adj.getName() + "\t\t#Operation: " + adj.getOper() + "\t\tValue: " + adj.getValue();
            logFile.logInfo(str);
            System.out.println(str);
        }
    }

    /**
     * Attempts to read the CSV file specified by filename.
     *
     * @param file_name of the file to read
     */
    public void read_file(String file_name) throws IOException {
        FileReader fr = new FileReader(file_name);
        BufferedReader bfr = new BufferedReader(fr);

        String line;
        while ((line = bfr.readLine()) != null) {
            String[] tokens = line.split(",");

            if (tokens.length == 2) {

                String name = tokens[0];
                Float value = is_float(tokens[1]) ? Float.parseFloat(tokens[1]) : null;

                if (!name.isEmpty() && value != null) {
                    stored_sales.add(new Sale(name, value));
                    update_num_sales(name, 1);
                }

            } else if (tokens.length == 3) {

                String name = tokens[0];
                Float value = is_float(tokens[1]) ? Float.parseFloat(tokens[1]) : null;
                Integer num_sales = is_int(tokens[2]) ? Integer.parseInt(tokens[2]) : null;

                if (!name.isEmpty() && value != null && num_sales != null) {
                    stored_sales.add(new Sale(name, value, num_sales));
                    update_num_sales(name, num_sales);
                }

            } else if (tokens.length == 4) {

                String name = tokens[0];
                Float value = is_float(tokens[1]) ? Float.parseFloat(tokens[1]) : null;
                Integer num_sales = is_int(tokens[2]) ? Integer.parseInt(tokens[2]) : null;
                String oper = tokens[3];

                if (!name.isEmpty() && value != null && num_sales == null && !oper.isEmpty()) {
                    adjust_products.add(new Adjustment(name, oper, value));
                    update_value(name, value, oper);
                }
            }

            if (stored_sales.size() % 10 == 0) {
                log_num_sales_and_totals();
            }

            if (stored_sales.size() % 50 == 0) {
                log_adjustments();
            }
        }


    }
}
