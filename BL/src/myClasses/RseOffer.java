package myClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class RseOffer {
    public static final int MKT_CODE = -1;
    public static final int BUY =1;
    public static final int SELL =2;

    private String dateOfDeal;
    protected Integer amount;
    private Integer price;


    public RseOffer(){}

    public RseOffer(String dateOfDeal, Integer amount, Integer price) {
        this.dateOfDeal = dateOfDeal;
        this.amount = amount;
        this.price = price;
    }

    public RseOffer(Integer amount, Integer price) {
        this.dateOfDeal = new SimpleDateFormat("HH:mm:ss:SSS").format(System.currentTimeMillis());
        this.amount = amount;
        this.price = price;
    }

    public String getDateOfDeal() {
        return dateOfDeal;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Date: " + dateOfDeal
                + " Amount of Stocks in deal: " + amount
                + " Price of each Stock: " + price
                + " Total price of deal: " + price * amount;
    }
}
