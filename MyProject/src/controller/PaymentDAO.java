package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.CartItem;
import model.Customer;

public class PaymentDAO {

	// db에서 로그인 한 사람의 구매 내역만 뽑아서 list에 넣음
	public static void setPaymaentToList(ArrayList<CartItem> totalPaymentList, Customer nowUser) {
		totalPaymentList.clear();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT * FROM paymenttbl WHERE customerid=?";
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

				totalPaymentList.add(ci);
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
	}// end of setPaymentToList
	
	public static void insertPaymentList() {
		Connection con = null;
		CallableStatement cstmt = null;
		for(int i=0; i<main.Main.cart.cartItem.size();i++) {
			try {
				String sql = "CALL PAYMENTTBL_INSERT(?,?,?,?,?,?)";
				con = controller.DBUtil.makeConnection();
				cstmt = con.prepareCall(sql);
				cstmt.setString(1, main.Main.cart.cartItem.get(i).getCustomerID());
				cstmt.setString(2, main.Main.cart.cartItem.get(i).getPerformanceId());
				cstmt.setString(3, main.Main.cart.cartItem.get(i).getPerformanceName());
				cstmt.setInt(4, main.Main.cart.cartItem.get(i).getQuantity());
				cstmt.setInt(5, main.Main.cart.cartItem.get(i).getTotalPrice());
				cstmt.setString(6, main.Main.cart.cartItem.get(i).getSeatNum());
				
				int value = cstmt.executeUpdate();
				
				if (value == 1) {
					System.out.println(main.Main.cart.cartItem.get(i).getPerformanceName() + " 등록완료");
				} else {
					System.out.println(main.Main.cart.cartItem.get(i).getPerformanceName() + " 등록 실패");
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
		controller.PaymentDAO.setPaymaentToList(main.Main.totalPaymentList, main.Main.nowUser);
	}

}
