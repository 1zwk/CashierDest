<%@ page import="java.sql.Connection" %>
<%@ page import="java.lang.management.GarbageCollectorMXBean" %>
<%@ page import="entity.Goods" %>
<%@ page import="util.DBUtil" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.ResultSet" %><%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/2/18
  Time: 10:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>更新商品</title>
    <meta charset="UTF-8">
    <link rel="stylesheet"/>
    <link rel="stylesheet" href="css/Site.css"/>
    <link rel="stylesheet" href="css/zy.all.css"/>
    <link rel="stylesheet" href="css/font-awesome.min.css"/>
    <link rel="stylesheet" href="css/amazeui.min.css"/>
    <link rel="stylesheet" href="css/admin.css"/>
</head>
<%
    HttpSession session1 = request.getSession();
    int id = Integer.parseInt((String)session1.getAttribute("id"));

    String sql = "select id, name, introduce, stock, unit, price, discount from goods where id = ?";

    Goods goods = new Goods();
    try(Connection connection = DBUtil.getConnection(true);
        PreparedStatement statement = connection.prepareStatement(sql)){
        statement.setInt(1,id);
        try(ResultSet resultSet = statement.executeQuery()){
            if(resultSet.next()){
                goods.setId(resultSet.getInt("id"));
                goods.setName(resultSet.getString("name"));
                goods.setIntroduce(resultSet.getString("introduce"));
                goods.setStock(resultSet.getInt("stock"));
                goods.setUnit(resultSet.getString("unit"));
                goods.setPrice(resultSet.getInt("price"));
                goods.setDiscount(resultSet.getInt("discount"));
            }
        }
    }catch (SQLException e){
        e.printStackTrace();
    }
%>
<body>
<div class="dvcontent">
    <div>
        <!--tab start-->
        <div class="taborder" style="margin: 30px;">
            <div class="hd">
                <ul>
                    <li class="" style="box-sizing: initial;-webkit-box-sizing: initial;">支付订单</li>
                </ul>
            </div>
            <div class="bd">
                <ul class="theme-popbod dform" style="display: none;">
                    <div class="am-cf admin-main" style="padding-top: 0px;">
                        <!-- content start -->

                        <div class="am-cf admin-main" style="padding-top: 0px;">
                            <!-- content start -->
                            <div class="admin-content">
                                <div class="admin-content-body">

                                    <div class="am-g">
                                        <div class="am-u-sm-12 am-u-md-4 am-u-md-push-8">

                                        </div>
                                        <div class="am-u-sm-12 am-u-md-8 am-u-md-pull-4"
                                             style="padding-top: 70px;">
                                            <form class="am-form am-form-horizontal"
                                                  action="updateGoods" method="post">

                                                <div class="am-form-group">
                                                    <label for="goodsID" class="am-u-sm-3 am-form-label">
                                                        需更新商品ID</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsID" required
                                                               placeholder="商品商品ID" value="<%=id%>" name="goodsID">
                                                        <small>商品ID</small>
                                                    </div>
                                                </div>

                                                <div class="am-form-group">
                                                    <label for="name" class="am-u-sm-3 am-form-label">
                                                        商品名称</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="name" required
                                                               placeholder="商品名称" value="<%=goods.getName()%>" name="name">

                                                    </div>
                                                </div>

                                                <div class="am-form-group">
                                                    <label for="stock" class="am-u-sm-3 am-form-label">
                                                        需更新库存</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="stock" required
                                                               placeholder="库存"  name="stock">

                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="introduce" class="am-u-sm-3 am-form-label">
                                                        需更新商品介绍</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="introduce" required
                                                               placeholder="商品介绍" value="<%=goods.getIntroduce()%>" name="introduce">

                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="unit" class="am-u-sm-3 am-form-label">
                                                        需更新商品单位</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="unit" required
                                                               placeholder="商品单位" value="<%=goods.getName()%>" name="unit">

                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="price" class="am-u-sm-3 am-form-label">
                                                        需更新商品价格</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="price" required
                                                               placeholder="商品价格" name="price">

                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="discount" class="am-u-sm-3 am-form-label">
                                                        需更新商品折扣</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="discount" required
                                                               placeholder="商品折扣" name="discount">

                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <div class="am-u-sm-9 am-u-sm-push-3">
                                                        <input type="submit" class="am-btn am-btn-success"
                                                               value="更新商品"/>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>

                                </div>
                                <!-- content end -->
                            </div>
                        </div>
                    </div>
                    <!--tab end-->
                </ul>

                <!--页面动画：渐显-->
                <script src="js/jquery-1.7.2.min.js" type="text/javascript"></script>
                <script src="js/plugs/Jqueryplugs.js" type="text/javascript"></script>
                <script src="js/_layout.js"></script>
                <script src="js/plugs/jquery.SuperSlide.source.js"></script>
                <script>
                    // var num = 1;
                    $(function () {
                        $(".taborder").slide({trigger: "click"});

                    });
                </script>
            </div>
        </div>
    </div>
</div>


</body>
</html>
