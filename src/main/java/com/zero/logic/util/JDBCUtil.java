package com.zero.logic.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接类
 * @auther Deram Zhao
 * @creatTime 2017/6/2
 */
public class JDBCUtil {

    /**
     * 获取数据库连接
     * @return 数据库连接
     */
    public static Connection getConn(){
        Properties propes;
        Connection conn = null;
        Resource resource = new ClassPathResource("/application.properties");//读取配置文件路径
        try {
            propes = PropertiesLoaderUtils.loadProperties(resource);
            String driver =propes.getProperty("spring.datasource.driver-class-name");
            Class.forName(driver);
            conn = DriverManager.getConnection(
                    propes.getProperty("spring.datasource.url"),
                    propes.getProperty("spring.datasource.username"),
                    propes.getProperty("spring.datasource.password"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }


    /**
     * 根据表名获取表的记录数目
     * @param conn 数据源链接
     * @param tableName 表名
     * @return 数目
     */
    public static int getCount(Connection conn, String tableName) {
        int total = 0;
        String sql = "select count(1) from " + tableName;
        PreparedStatement statement;
        try {
            statement = conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}
