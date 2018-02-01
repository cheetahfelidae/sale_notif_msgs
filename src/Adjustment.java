public class Adjustment {
    private String name;
    private String oper;
    private float value;

    Adjustment(String name, String oper, float value) {
        this.name = name;
        this.oper = oper;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getOper() {
        return oper;
    }

    public float getValue() {
        return value;
    }

}
