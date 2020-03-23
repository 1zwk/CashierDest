package servlet;

import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.sql.*;


/**
 * 更新商品
 * 1.首先判断更新商品的id是否存在，
 * 2.如果不存在，更新失败。
 * 3.如果存在，根据ID找到货物，对该货物属性更新，
 * */


@WebServlet("/updateGoods")
public class UpdateGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("daole");
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        int goodsId  = Integer.valueOf(req.getParameter("goodsID"));
        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        int stock = Integer.parseInt(req.getParameter("stock"));//从请求体中得到的参数都是字符串，需要自己转换。
        String unit = req.getParameter("unit");
        int discount = Integer.parseInt(req.getParameter("discount"));
        //价格有小数,需要转换数据库中的int类型
        String price = req.getParameter("price");//字符串“12.34”
        Double doublePrice = Double.valueOf(price);//double 12.34
        int intPrice = new Double(doublePrice*100).intValue();

        //检验
        System.out.println("name"+name);
        System.out.println("price"+intPrice);
        System.out.println("discount"+discount);
        System.out.println("stock"+stock);
        System.out.println("id"+goodsId);
        System.out.println("unit"+unit);

        //得到商品判断是否存在，存在就更新，不存在就提示
        Goods goods = this.getGoods(goodsId);
        if(goods == null){
            System.out.println("无该商品");
            resp.sendRedirect("index.html");
        }else{
            goods.setName(name);
            goods.setUnit(unit);
            goods.setPrice(intPrice);
            goods.setIntroduce(introduce);
            goods.setDiscount(discount);
            goods.setStock(stock);

            //更新
            boolean effect = this.modifyGoods(goods);
            if(effect){
                System.out.println("更新成功");
                resp.sendRedirect("goodsbrowse.html");
            }else{
                System.out.println("更新失败");
                Writer writer = resp.getWriter();
                writer.write("<h1>更新失败</h1>");
                resp.sendRedirect("updategoods.html");
            }
        }
    }

    private boolean modifyGoods(Goods goods){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean effect = false;
        try{
            String sql = "update goods set name=?,introduce=?, stock=?,unit=?,price=?,discount=? where id = ?";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,goods.getName());
            preparedStatement.setString(2,goods.getIntroduce());
            preparedStatement.setInt(3,goods.getStock());
            preparedStatement.setString(4,goods.getUnit());
            preparedStatement.setInt(5,goods.getPriceInt());
            preparedStatement.setInt(6,goods.getDiscount());
            preparedStatement.setInt(7,goods.getId());
            //判断是否是执行成功，成功就返回true
            effect = (preparedStatement.executeUpdate()==1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return effect;
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
        goods.setPrice(resultSet.getInt("price"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setStock(resultSet.getInt("stock"));
        return goods;
    }
}
