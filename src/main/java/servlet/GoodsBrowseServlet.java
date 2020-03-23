package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Goods;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseGoods")
public class GoodsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Goods> list = new ArrayList<>();//存储结果集

        try {
            String sql = "select * from goods ";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();//存储了整张表的所有信息
            //需要解析结果集
            while (resultSet.next()) {
                //解析resultset数据
                Goods goods = this.extractGoods(resultSet);
                list.add(goods);
            }
            //重要： 把list转换为json，然后发送给前端
            //可以方便的将模型对象转换为JSON
            ObjectMapper mapper = new ObjectMapper();//一个方便的把list转换为流的模型,ok
            //把响应包推送给浏览器
            PrintWriter pw = resp.getWriter();
            //将list转换为json字符串，并将json字符串填充到PrintWriter里
            mapper.writeValue(pw, list);
            //推流送给前端
            Writer writer = resp.getWriter();
            writer.write(pw.toString());


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection, preparedStatement, resultSet);
        }
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
