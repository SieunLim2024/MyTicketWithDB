package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import main.Main;
import model.CartItem;
import model.Customer;

public class PaymentDAO {

	// db에서 로그인 한 사람의 구매 내역만 뽑아서 list에 넣음
	public static void setPaymaentToList(ArrayList<CartItem> paymentList, Customer nowUser) {
		paymentList.clear();
		String sql = "CALL PAYMENTTBL_SELECT(?,?)";
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;

		try {
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, nowUser.getCustomerId());
			cstmt.registerOutParameter(2, Types.REF_CURSOR);
			cstmt.execute();
			rs=(ResultSet)cstmt.getObject(2);
			while (rs.next()) {
				CartItem ci = new CartItem();
				ci.setCustomerID(rs.getString("customerID"));
				ci.setPerformanceId(rs.getString("performanceId"));
				ci.setPerformanceName(rs.getString("performanceName"));
				ci.setQuantity(rs.getInt("quantity"));
				ci.setTotalPrice(rs.getInt("totalPrice"));
				ci.setSeatNum(rs.getString("seatNum"));

				paymentList.add(ci);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, rs, con);
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
				
				int totalPrice=main.Main.cart.cartItem.get(i).getTotalPrice();
				if (Main.nowUser.getGrade().equals("VIP")) {
					totalPrice *= 0.99;// vip면 할인 해줌
				}
				cstmt.setInt(5, totalPrice);
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
				DBUtil.closeResources(cstmt, con);
			}
			
		}
		setPaymaentToList(main.Main.paymentList, main.Main.nowUser);
	}

	public static void deleteUserPayment(String inputID) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL PAYMENTTBL_DELETE_USER(?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, inputID);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(inputID + "회원 구매 정보 삭제 완료");
			} else {
				System.out.println(inputID + "회원 구매 정보 삭제 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, con);
		}
//		setPaymaentToList(main.Main.totalPaymentList, main.Main.nowUser);
	}
		
	

}
