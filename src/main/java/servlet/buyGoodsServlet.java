package servlet;

import common.OrderStautus;
import entity.Goods;
import java.util.List;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@WebServlet("/buyGoodsServlet")
//如果HTML是以<a>标签跳转的，必须使用doGet()方法获取。
//get一般用于查询。post一般用于和数据库进行交互。
public class buyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        List<Goods> list = (List<Goods>) session.getAttribute("list");
        System.out.println("list:"+list);
        //设置order时间和状态
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setFinish_time(LocalDateTime.now().format(formatter));
        order.setOrder_status(OrderStautus.OK);
        //提交订单
        boolean effect = this.commitOrder(order);
        //当你插入数据库成功，—— 》购买——》
        //遍历把数据库,把货物库存修改
        if (effect) {
            if(list == null){
                System.out.println("list："+list);
            }else {
                System.out.println("list"+list);
                for (Goods goods : list) {
                    if (updataAfterBuy(goods, goods.getPayGoodsNum())) {
                        PrintWriter writer = resp.getWriter();
                        writer.println("更新成功");
                        writer.println("<div><input type=\"submit\" class=\"am-btn am-btn-success\"" +
                                " value=\"返回首页\" onclick=\"javascript:window.location.href='index.html'\"/>\n" +
                                "<input type=\"submit\" class=\"am-btn am-btn-success\"" +
                                " value=\"返回商品列\" onclick=\"javascript:window.location.href='goodsbrowse.html'\"/> </div>");
                        System.out.println("更新成功");
                        return;
                    } else {
                        PrintWriter writer = resp.getWriter();
                        writer.println("更新失败");
                        writer.println("<div> <input type=\"submit\" class=\"am-btn am-btn-success\"" +
                                " value=\"返回首页\" onclick=\"javascript:window.location.href='index.html'\"/>\n" +
                                "<input type=\"submit\" class=\"am-btn am-btn-success\"" +
                                " value=\"返回商品列\" onclick=\"javascript:window.location.href='goodsbrowse.html'\"/> </div>");
                        System.out.println("更新失败");
                    }
                }
            }
            resp.sendRedirect("index.html");
        }
    }

    //更新库存
    private boolean updataAfterBuy(Goods goods, int goodsBuyNum) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean effect = false;
        try {
            String sql = "update goods set stock = ? where id = ?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, goods.getStock() - goodsBuyNum);
            preparedStatement.setInt(2, goods.getId());
            if(preparedStatement.executeUpdate()==1){
                effect = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return effect;
    }

    //此时需要插入多条程序，有可能失败，需要用到事务的回滚
    private boolean commitOrder(Order order) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String insertOrder = "insert into `order`(id,account_id,account_name," +
                    "create_time,finish_time,actual_amount,total_money,order_status) " +
                    "values(?,?,?,now(),?,?,?,?)";
            String insertOrserItem = "insert into order_item(order_id, goods_id, goods_name," +
                    " goods_introduce, goods_num, goods_unit, goods_price, goods_discount) " +
                    "values(?,?,?,?,?,?,?,?)";
            connection = DBUtil.getConnection(false);//手动提交
            preparedStatement = connection.prepareStatement(insertOrder);
            preparedStatement.setString(1, order.getId());
            preparedStatement.setInt(2, order.getAccount_id());
            preparedStatement.setString(3, order.getAccount_name());
            preparedStatement.setInt(5, order.getActual_amountInt());
            preparedStatement.setInt(6, order.getTotal_amountInt());
            preparedStatement.setInt(7, order.getOrder_statusDesc().getFlag());//插入枚举值

            if (preparedStatement.executeUpdate() == 0) {
                throw new RuntimeException("插入订单失败");
            }

            //插入订单成功后开始插入订单项
            preparedStatement = connection.prepareStatement(insertOrserItem);
            for (OrderItem orderItem : order.orderItemList) {
                preparedStatement.setString(1, orderItem.getOrder_id());
                preparedStatement.setInt(2, orderItem.getGoods_id());
                preparedStatement.setString(3, orderItem.getGoods_name());
                preparedStatement.setString(4, orderItem.getGoods_introduce());
                preparedStatement.setInt(5, orderItem.getGoods_num());
                preparedStatement.setString(6, orderItem.getGoods_unit());
                preparedStatement.setInt(7, orderItem.getGoods_priceInt());
                preparedStatement.setInt(8, orderItem.getGoods_discount());

                preparedStatement.addBatch();//将每一项缓存
            }
            //把缓存批量插入，并且该方法会保存所有的执行结果进一个数组,1:成功，2：失败
            int[] effects = preparedStatement.executeBatch();

            for (int i : effects) {
                if (i == 0) {
                    throw new RuntimeException("插入订单项失败");
                }
            }
            //手动提交
            connection.commit();

        } catch (Exception e) {
            //这里有两个异常，SQL异常和自己定义的插入异常，
            // 如果连接不为空，说明SQL正常，插入失败，需要回滚.
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        } finally {
            DBUtil.close(connection, preparedStatement, null);
        }
        return true;
    }
}
