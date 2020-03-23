package entity;

import common.OrderStautus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_amount;
    private OrderStautus order_status;//枚举

    //订单项也要存储到订单中
    public List<OrderItem> orderItemList = new ArrayList<>();

    //为了浏览订单
    public String getOrder_status(){
        return order_status.getDesc();
    }

    public OrderStautus getOrder_statusDesc(){
        return order_status;
    }

    public Double getActual_amount(){
        return actual_amount*1.0/100;
    }
    public int getActual_amountInt(){
        return actual_amount;
    }
    public Double getTotal_amount(){
        return total_amount*1.0/100;
    }
    public int getTotal_amountInt(){
        return total_amount;
    }
    //优惠
    public Double getDiscount(){
        return (this.getTotal_amountInt()-this.getActual_amountInt())*1.00/100;
    }
}
