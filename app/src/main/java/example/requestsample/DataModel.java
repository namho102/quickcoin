package example.requestsample;


public class DataModel {

    public String coinId;
    public String coinName;
    public double price;

    public DataModel(String id, String name, double price)
    {
        this.coinId = id;
        this.coinName = name;
        this.price = price;
    }
}
