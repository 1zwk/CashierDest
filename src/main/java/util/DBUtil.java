package util;


import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/cash?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private static volatile DataSource DATADOURCE;

    private static DataSource getDATADOURCE(){
        if(DATADOURCE == null){
            synchronized (DBUtil.class){
                if(DATADOURCE == null){
                    DATADOURCE = new MysqlDataSource();
                    ((MysqlDataSource)DATADOURCE).setURL(URL);
                    ((MysqlDataSource)DATADOURCE).setUser(USERNAME);
                    ((MysqlDataSource)DATADOURCE).setPassword(PASSWORD);
                }
            }
        }
        return DATADOURCE;
    }

    public static Connection getConnection(boolean autoCommit){
        try{
            Connection connection = getDATADOURCE().getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取连接失败");
        }
    }

    public static void close(Connection connection, PreparedStatement preparedStatement,
                             ResultSet resultSet){
        try{
            if(resultSet != null){
                resultSet.close();
            }
            if(preparedStatement != null){
                preparedStatement.close();
            }
            if(connection != null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
