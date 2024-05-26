package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Cart;
import model.CartItem;
import model.Customer;

public class CartDAO {

	// 전체 카트 파일에서 로그인 한 고객의 카트 정보만 찾아오기
	public static void setCartToList(Customer nowUser) {
		Cart.cartItem.clear();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT * FROM carttbl WHERE customerid=?";
			con = controller.DBUtil.makeConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, nowUser.getCustomerId());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				CartItem ci = new CartItem();
				ci.setCustomerID(rs.getString("customerID"));
				ci.setPerformanceId(rs.getString("performanceId"));
				ci.setPerformanceName(rs.getString("performanceName"));
				ci.setQuantity(rs.getInt("quantity"));
				ci.setTotalPrice(rs.getInt("totalPrice"));
				ci.setSeatNum(rs.getString("seatNum"));

//					// 이미 카트에 항목 있으면 수량과 좌석 번호만 업데이트
				if (!main.Main.cart.isCartInPerformance(rs.getString("performanceId"), rs.getInt("quantity"),
						rs.getString("seatNum"))) {
					Cart.cartItem.add(ci); // 아니면 카트에 항목 추가
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Cart.cartCount = Cart.cartItem.size();

	}// end of setCartToList
	
	
	public static void updateCartDB(String performanceId, int quantity,String seatNum) {
		String sql = "CALL CARTTBL_UPDATE(?,?,?)";
		Connection con = null;
		CallableStatement cstmt = null;

		try {
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setInt(1,quantity);
			cstmt.setString(2, seatNum);
			cstmt.setString(3, performanceId);
			int value=cstmt.executeUpdate();
			
			if(value==0) {
				System.out.println(performanceId+"업데이트 완료");
			}else {
				System.out.println(performanceId+"업데이트 실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {

				if (cstmt != null) {
					cstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
