package common;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum OrderStautus {
    PLAYING(1,"待支付"), OK(2,"支付完成");
    private int flag;
    private String desc;

    OrderStautus(int flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    //通过数字获取订单状态
    public static OrderStautus valueOf(int flag) {
        for(OrderStautus orderStautus : OrderStautus.values()){
            if(orderStautus.flag == flag){
                return orderStautus;
            }
        }
        throw new RuntimeException("获取订单状态异常");
    }
}
