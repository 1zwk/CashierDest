package entity;

import lombok.Data;

@Data
public class OrderItem {
    private Integer id;
    private String order_id;
    private Integer goods_id;
    private String goods_name;
    private String goods_introduce;
    private Integer goods_num;
    private String goods_unit;
    private Integer goods_price;
    private Integer goods_discount;

    public Double getGoods_price() {
        return goods_price * 1.0 / 100;
    }

    public int getGoods_priceInt() {
        return goods_price;
    }

}
