package entity;

import lombok.Data;

@Data
public class Goods {
    private Integer id;
    private String name;
    private String introduce;
    private Integer stock;
    private String unit;
    private Integer price;//价格 以分为单位存储，避免小数
    private Integer discount;

    private Integer payGoodsNum;//记录当前购买商品的数量

    //覆写一个get,得到以“元”为单位price
    public Double getPrice() {
        return price * 1.0 / 100;
    }

    public int getPriceInt() {
        return price;
    }
}
