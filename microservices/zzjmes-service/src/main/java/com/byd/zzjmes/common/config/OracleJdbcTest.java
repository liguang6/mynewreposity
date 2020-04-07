package com.byd.zzjmes.common.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OracleJdbcTest {

	private static Connection conn = null;
	private static String driver = "oracle.jdbc.driver.OracleDriver"; // 驱动
	private static String url = "jdbc:oracle:thin:@10.23.1.163:1521:testdb"; // 连接字符串
	private static String username = "wmstest"; // 用户名
	private static String password = "wms2019"; // 密码

	// 获得连接对象
	private static synchronized Connection getConn() {
		if (conn == null) {
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, username, password);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	// 执行查询语句
	public List<Map<String, Object>> queryMasterDataMapNo(String sql, boolean isSelect) throws SQLException {
		PreparedStatement pstmt;
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			pstmt = getConn().prepareStatement(sql);
			// 建立一个结果集，用来保存查询出来的结果
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> map_no = new HashMap<String, Object>();
				map_no.put("MAP_NO", rs.getString("MAP_NO"));
				result.add(map_no);
			}
			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	// 关闭连接
	public void close() {
		try {
			getConn().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
