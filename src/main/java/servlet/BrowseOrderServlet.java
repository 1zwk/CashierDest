package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import common.OrderStautus;
import entity.Account;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        System.out.println("浏览订单");
        /**
         * 1.根据当前用户进行查找订单
         * 2.查询结果可能为多个，list
         * 3.如果结果为空，说明无订单
         * 4.如果不是空，把list转换为json，发送给前端
         */
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");

        List<Order> list = this.searchOrder(account.getId());
        System.out.println("list"+list);

        ObjectMapper mapper = new ObjectMapper();
        PrintWriter pw = resp.getWriter();
        mapper.writeValue(pw, list);
        Writer writer = resp.getWriter();
        writer.write(pw.toString());

    }

    private List<Order> searchOrder(Integer accountId) {
        List<Order> list = new ArrayList<>();

        String fileName = "query_order_by_account.sql";
        String sql = getSql(fileName);

        try (Connection connection = DBUtil.getConnection(true);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {

                Order order = null;
                while(resultSet.next()) {
                    //订单解析
                    if(order == null) {
                        order = new Order();
                        extractOrder(order, resultSet);
                        list.add(order);
                    }

                    String orderId = resultSet.getString("order_id");

                    if(!order.getId().equals(orderId)) {
                        order = new Order();
                        extractOrder(order, resultSet);
                        list.add(order);
                    }

                    //订单项解析
                    OrderItem orderItem = extractOrderItem(resultSet);
                    order.orderItemList.add(orderItem);

                }
                System.out.println(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    /*private List<Order> searchOrder(int accountId) {
        List<Order> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String sql = this.getSql("query_order_by_account.sql");
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);
            resultSet = preparedStatement.executeQuery();
            Order order = null;
            while (resultSet.next()) {
                //1.订单解析，需要判断。
                //2.相同订单直接就解析，添加list，如果不同，就在new一张订单解析，添加。
                if (order == null) {
                    order = new Order();
                    this.extractOrder(order, resultSet);
                    list.add(order);
                }
                String orderId = resultSet.getString("order_id");
                if (!orderId.equals(order.getId())) {
                    order = new Order();
                    this.extractOrder(order, resultSet);
                    list.add(order);
                }

                //2.订单项解析
                OrderItem orderItem = this.extractOrderItem(resultSet);
                //把订单项插入到订单里
                order.orderItemList.add(orderItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, preparedStatement, resultSet);
        }

        return list;
    }*/

    private void extractOrder(Order order, ResultSet resultSet) {
        try {
            order.setId(resultSet.getString("order_id"));
            order.setAccount_id(resultSet.getInt("account_id"));
            order.setAccount_name(resultSet.getString("account_name"));
            order.setTotal_amount(resultSet.getInt("total_money"));
            order.setActual_amount(resultSet.getInt("actual_amount"));
            order.setCreate_time(resultSet.getString("create_time"));
            order.setFinish_time(resultSet.getString("finish_time"));
            order.setOrder_status(OrderStautus.valueOf(resultSet.getInt("order_status")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private OrderItem extractOrderItem(ResultSet resultSet) {
        OrderItem orderItem = new OrderItem();
        try {
            orderItem.setId(resultSet.getInt("id"));
            orderItem.setOrder_id(resultSet.getString("order_id"));
            orderItem.setGoods_name(resultSet.getString("goods_name"));
            orderItem.setGoods_id(resultSet.getInt("goods_id"));
            orderItem.setGoods_price(resultSet.getInt("goods_price"));
            orderItem.setGoods_num(resultSet.getInt("goods_num"));
            orderItem.setGoods_unit(resultSet.getString("goods_unit"));
            orderItem.setGoods_introduce(resultSet.getString("goods_introduce"));
            orderItem.setGoods_discount(resultSet.getInt("goods_discount"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderItem;
    }

    private String getSql(String s) {
        InputStream is = BrowseOrderServlet.class.getClassLoader().getResourceAsStream(s);
        StringBuilder sb = new StringBuilder();

        if(is == null) {
            throw new RuntimeException("文件加载失败");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while((line = br.readLine()) != null) {
                sb.append(" ").append(line);//为了防止sql语句相互粘连，需要先加一个空格。
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("转化sql语句发生异常");
        }
    }

    /*private String getSql(String sqlName) {
        //this.getClass()获取当前类对象
        //getClassLoader()获取类加载器
        InputStream in = this.getClass().getClassLoader().
                getResourceAsStream("scricpt/" + "query_order_by_account.sql".substring(1) + ".sql");
        if (in == null) {
            throw new RuntimeException("加载sql文件出错");
        } else {
            //字节转字符
            InputStreamReader isr = new InputStreamReader(in);
            //字符缓冲流
            BufferedReader reader = new BufferedReader(isr);
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(reader.readLine());
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(" ").append(line);//为了防止sql语句相互粘连，需要先加一个空格。
                }
                System.out.println(sb.toString());
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("转化sql异常");
            }
        }

    }*/
}
