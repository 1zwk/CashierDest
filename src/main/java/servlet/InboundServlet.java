package servlet;

import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedReader;
import java.sql.*;

@WebServlet("/inbound")
public class InboundServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");


        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        int stock = Integer.parseInt(req.getParameter("stock"));//从请求体中得到的参数都是字符串，需要自己转换。
        String unit = req.getParameter("unit");
        int discount = Integer.parseInt(req.getParameter("discount"));

        //价格有小数,需要转换数据库中的int类型
        String price = req.getParameter("price");//字符串“12.34”
        Double doublePrice = Double.valueOf(price);//double 12.34
        int intPrice = new Double(doublePrice*100).intValue();


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try{
            String sql = "insert into goods(name,introduce,stock,unit,price,discount) values(?,?,?,?,?,?)";
            connection = DBUtil.getConnection(true);
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,introduce);
            preparedStatement.setInt(3,stock);
            preparedStatement.setString(4,unit);
            preparedStatement.setInt(5,intPrice);
            preparedStatement.setInt(6,discount);

            int ret = preparedStatement.executeUpdate();//todo
            if(ret == 1){
                resp.sendRedirect("goodsbrowse.html");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
    }
}
