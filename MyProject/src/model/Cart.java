package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.w3c.dom.UserDataHandler;

import cart.CartInterface;
import main.Main;

public class Cart implements CartInterface {

	public static ArrayList<CartItem> cartItem = new ArrayList<>();
	public static int cartCount = 0;
	public static int totalcount = 0;// 구매한 항목 수

	public Cart() {
		super();
	}

	public Cart(ArrayList<CartItem> cartItem, ArrayList<CartItem> paymentItem) {
		super();
		this.cartItem = cartItem;
//		this.paymentItem=paymentItem;
	}

	
	//카트 안에 해당 공연이 있는지 확인
	@Override
	public boolean isCartInPerformance(String id, int quantity, String seatNum) {
		boolean flag = false;
		for (int i = 0; i < cartItem.size(); i++) {
			if (id.equals(cartItem.get(i).getPerformanceId())) {// 카트리스트 안에 이미 해당 공연이 들어있다면...
				cartItem.get(i).setQuantity(cartItem.get(i).getQuantity() + quantity);// 수량만 증가
				cartItem.get(i).setSeatNum(cartItem.get(i).getSeatNum() + "," + seatNum);// 좌석 번호 추가해줌
				controller.CartDAO.updateCartDB(cartItem.get(i).getPerformanceId(),(cartItem.get(i).getQuantity()+quantity),(cartItem.get(i).getSeatNum() + "," + seatNum));// 수량&좌석번호 증가
				flag = true;
			}
		}

		return flag;
	}
	
	

	//카트에 공연 항목 추가
	@Override
	public void insertCartDB(int numIndex, int numTicket, Customer nowUser, String seatNum) {
//		CartItem pItem = new CartItem(p, quantity, nowUser, seatNum);
//		cartItem.add(pItem);
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql="CALL CARTTBL_INSERT(?,?,?,?,?,?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);			
			cstmt.setString(1, nowUser.getCustomerId());
			cstmt.setString(2, Admin.performanceList.get(numIndex).getPerformanceID());
			cstmt.setString(3,  Admin.performanceList.get(numIndex).getPerformanceName());
			cstmt.setInt(4, numTicket);
			cstmt.setInt(5, (Admin.performanceList.get(numIndex).getTicketPrice()*numTicket));
			cstmt.setString(6, seatNum);

			int value=cstmt.executeUpdate();
			
			if(value==0) {
				System.out.println(Admin.performanceList.get(numIndex).getPerformanceName()+" 등록 완료");
			}else {
				System.out.println(Admin.performanceList.get(numIndex).getPerformanceName()+" 등록 실패");
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
		
		
		
		controller.CartDAO.setCartToList(nowUser);
		cartCount = cartItem.size();

	}
	//선택한 공연만 장바구니에서 삭제
	@Override
	public void removeCart(String performanceId, Customer nowUser) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL CARTTBL_DELETE(?,?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, nowUser.getCustomerId());
			cstmt.setString(2, performanceId);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(performanceId + "장바구니 삭제 완료");
			} else {
				System.out.println(performanceId + "장바구니 삭제 실패");
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
		controller.CartDAO.setCartToList(nowUser);
		cartCount = cartItem.size();
	}

	@Override
	public void deleteCart(Customer nowUser) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL CARTTBL_DELETEALL(?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, nowUser.getCustomerId());
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(nowUser.getCustomerId() + "장바구니 모두 삭제 완료");
			} else {
				System.out.println(nowUser.getCustomerId() + "장바구니 모두 삭제 실패");
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
		controller.CartDAO.setCartToList(nowUser);
		cartCount = 0;
	}
	
	//카트 내용 출력
	@Override
	public void printCart() {
		System.out.println("============================================================================================");
		System.out.println("장바구니 등록 목록: ");
		System.out.println("--------------------------------------------------------------------------------------------");
		if (cartCount == 0) {
			System.out.println("장바구니가 비어있습니다.");
		} else {
			System.out.println("수량\t총액\t좌석 번호\t공연ID\t\t    공연명");
			for (int i = 0; i < cartItem.size(); i++) {
				System.out.print(cartItem.get(i).getQuantity() + "\t:");
				System.out.print(cartItem.get(i).getTotalPrice() + "\t:");
				System.out.print(cartItem.get(i).getSeatNum() + "\t:");
				System.out.print(cartItem.get(i).getPerformanceId() + "   :");
				System.out.print(cartItem.get(i).getPerformanceName());
				System.out.println("\t");
			}
		}
	}


}