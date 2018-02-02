public class Msg {
    private String name;
    private float value;
    private int num;
    private String oper;

    /**
     * Message type 1: contains the details of 1 sale.
     *
     * @param name
     * @param value
     */
    Msg(String name, float value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Message type 2: contains the details of a sale and the number of occurrences of that sale.
     *
     * @param name
     * @param value
     */
    Msg(String name, float value, int num) {
        this.name = name;
        this.value = value;
        this.num = num;
    }

    /**
     * Message type 3: contains the details of a sale and an adjustment operation to be applied to all stored sales of this product name.
     *
     * @param name
     * @param value
     */
    Msg(String name, float value, String oper) {
        this.name = name;
        this.value = value;
        this.oper = oper;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public int getNum() {
        return num;
    }

    public String getOper() {
        return oper;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
