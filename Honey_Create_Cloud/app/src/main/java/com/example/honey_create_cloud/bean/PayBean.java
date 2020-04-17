package com.example.honey_create_cloud.bean;

/**
 * Created by wangpan on 2020/4/15
 */
public class PayBean {

    /**
     * type : 1
     * itemId : id1
     * price : 6666
     * number : 1
     */

    private int type;
    private String itemId;
    private int price;
    private int number;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "PayBean{" +
                "type=" + type +
                ", itemId='" + itemId + '\'' +
                ", price=" + price +
                ", number=" + number +
                '}';
    }
}
