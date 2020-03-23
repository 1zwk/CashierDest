package servlet;

import common.OrderStautus;
import entity.Account;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/buyOne")
public class BuyOneServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        //生成货物
        List<Goods> goodsList = new ArrayList<>();
        int id = Integer.parseInt(req.getParameter("goodsId"));
        int num = Integer.parseInt(req.getParameter("goodsNum"));
        Goods goods = this.getGoods(id);
        if(goods != null) {
            if (goods.getStock() < num) {
                Writer writer = resp.getWriter();
                writer.write("<h2>此货物不足</h2>");
                writer.write("<a href=\"goodsbrowse.html\">\n" +
                        "                <li>返回商品列</li>\n" +
                        "            </a>");
                throw new RuntimeException("货物不足");
            } else {
                goods.setPayGoodsNum(num);
            }
        }else{
            Writer writer = resp.getWriter();
            writer.write("<h2>没有此货物</h2>");
            throw new RuntimeException("没有此货物");
        }

        goodsList.add(goods);

        //获取当前session
        HttpSession session = req.getSession();
        session.setAttribute("list",goodsList);
        Account account = (Account) session.getAttribute("user");

        //生成订单
        Order order = new Order();
        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        order.setAccount_name(account.getUsername());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setCreate_time(LocalDateTime.now().format(dateTimeFormatter));
        order.setFinish_time(LocalDateTime.now().format(dateTimeFormatter));
        int CountPrice = 0;
        int realPrice = 0;
        //每一个货物都是一个orderItem,给订单添加订单项
        for(Goods item :goodsList){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder_id(order.getId());
            orderItem.setGoods_id(item.getId());
            orderItem.setGoods_name(item.getName());
            orderItem.setGoods_num(item.getPayGoodsNum());
            orderItem.setGoods_discount(item.getDiscount());
            orderItem.setGoods_introduce(item.getIntroduce());
            orderItem.setGoods_unit(item.getUnit());
            orderItem.setGoods_price(item.getPriceInt());
            order.orderItemList.add(orderItem);

            //计算当前价格
            int currentPrice = item.getPayGoodsNum() * item.getPriceInt();
            CountPrice += currentPrice;
            //计算折扣后价格
            int discountPrice = currentPrice * item.getDiscount()/100;//折扣记录的是分，需要除以100
            realPrice += discountPrice;
        }
        order.setTotal_amount(CountPrice);
        order.setActual_amount(realPrice);
        order.setOrder_status(OrderStautus.PLAYING);

        //记录一个session
        HttpSession session1 = req.getSession();
        session1.setAttribute("order",order);


        //打印页面
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<p>【用户名称】: " + order.getAccount_name() + "</p>\n" +
                "<p>【订单编号】: " + order.getId() + "</p>\n" +
                "<p>【订单状态】: " + order.getOrder_statusDesc().getDesc() + "</p>\n" +
                "<p>【创建时间】: " + order.getCreate_time() + "</p>\n" +
                "<p>编号 名称 数量 单位 单价(元)</p>\n" +
                "<ol>\n");
        for (OrderItem orderItem : order.getOrderItemList()) {
            sb.append("<li>" + " " + orderItem.getGoods_name() + " " +
                    orderItem.getGoods_num() + " " + orderItem.getGoods_unit() + " " +
                    orderItem.getGoods_price() + "</li>\n");
        }
        sb.append("</ol>\n" +
                "<p>【总金额】:  " + order.getTotal_amount() + "</p>\n" +
                "<p>【优惠金额】: " + order.getDiscount() + "</p>\n" +
                "<p>【实际金额】: " + order.getActual_amount() + "</p>\n" +
                "<a href=\"buyGoodsServlet\">支付</a>\n" +
                "<a href=\"index.html\">取消</a>\n" +
                "</body>\n" +
                "</html>");

        PrintWriter writer = resp.getWriter();
        writer.write(sb.toString());
    }

    private Goods getGoods(int goodsId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Goods goods = null;

        try{
            String sql = "select * from goods where id = ?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1,goodsId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                goods = this.extractGoods(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return goods;
    }

    private Goods extractGoods(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setDiscount(resultSet.getInt("discount"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setName(resultSet.getString("name"));
        //把数字转换成小数 覆写getPrice（）方法，这样返回的json就会调用覆写的方法，返回double
        goods.setPrice(resultSet.getInt("price"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setStock(resultSet.getInt("stock"));
        return goods;
    }
}
