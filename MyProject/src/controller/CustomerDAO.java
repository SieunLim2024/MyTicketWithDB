package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import exception.CartException;
import main.Main;
import model.Admin;
import model.Customer;

public class CustomerDAO {
	// 유저를 리스트로
	public static void setUserToList(ArrayList<Customer> userList) {
		userList.clear();
		String sql = "CALL USERTBL_SELECT(?)";
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;

		try {
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.registerOutParameter(1, Types.REF_CURSOR);
			cstmt.execute();
			rs=(ResultSet)cstmt.getObject(1);
			while (rs.next()) {
				Customer cus = new Customer();
				cus.setCustomerId(rs.getString("customerid"));
				cus.setPw(rs.getString("pw"));
				cus.setCustomerName(rs.getString("customername"));
				cus.setPhone(rs.getString("phone"));
				cus.setAddress(rs.getString("address"));
				cus.setAge(rs.getInt("age"));
				cus.setGrade(rs.getString("grade"));
				cus.setAccumulatedPayment(rs.getInt("accumulatedpayment"));
				cus.setMileage(rs.getInt("mileage"));

				userList.add(cus);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, rs, con);
		}
	}

	// 회원가입
	public static void joinMembership() {
		System.out.println("회원 가입 하시겠습니까? Y|N");
		String str = main.Main.sc.nextLine();
		boolean quit = false;
		boolean ageFlag = false;
		if (str.toUpperCase().equals("Y")) {
			String id = null;
			while (!quit) {
				System.out.print("아이디: ");
				id = main.Main.sc.nextLine();
				main.Main.duplicateID = controller.CustomerManager.serchId(id);
				if (main.Main.duplicateID == true) {

					continue;
				} else {
					quit = true;
				}
				String customerId = id;
				System.out.print("비밀번호: ");
				String pw = main.Main.sc.nextLine();
				System.out.print("이름:");
				String customerName = main.Main.sc.nextLine();
				System.out.print("연락처:");
				String phone = main.Main.sc.nextLine();
				System.out.print("주소: ");
				String address = main.Main.sc.nextLine();
				int age = 0;
				while (!ageFlag) {
					System.out.print("나이 (숫자 만): ");
					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "");// 숫자 이외 공백 처리
					if (input.length() == 0) {// 숫자를 한번도 입력하지 않으면
						input = "0";// null 방지 (사실 필요 없으니 보험삼아)
						System.out.println("숫자만 입력해주세요.");
						continue;
					}
					if (input.equals("0")) {
						System.out.println("1세 미만은 가입이 불가합니다.");
						continue;
					} else {
						age = Integer.parseInt(input);
						ageFlag = true;
					} // end of else if
				} // end of while

				String grade = "Basic";// 회원 가입시 등급 무조건 Basic
				int accumulatedPayment = 0;// 회원 가입시 구매 총금액 무조건 0
				int mileage = 0;// 회원 가입시 마일리지 무조건 0
//				= "0";// 회원 가입시 구매회수 무조건 0
//				= "0";// 회원 가입시 카트담아 둔 것 무조건 0

				Connection con = null;
				CallableStatement cstmt = null;
				try {
					String sql = "CALL USERTBL_INSERT(?,?,?,?,?,?,?,?,?)";
					con = controller.DBUtil.makeConnection();
					cstmt = con.prepareCall(sql);
					cstmt.setString(1, customerId);
					cstmt.setString(2, pw);
					cstmt.setString(3, customerName);
					cstmt.setString(4, phone);
					cstmt.setString(5, address);
					cstmt.setInt(6, age);
					cstmt.setString(7, grade);
					cstmt.setInt(8, accumulatedPayment);
					cstmt.setInt(9, mileage);

					int value = cstmt.executeUpdate();

					if (value == 0) {
						System.out.println(customerId + " 등록완료");
					} else {
						System.out.println(customerId + " 등록 실패");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DBUtil.closeResources(cstmt, con);
				}
				main.Main.userList.clear();
				controller.CustomerDAO.setUserToList(main.Main.userList);
			} // end of while
		} else {
			System.out.println("회원 가입을 취소합니다.");
			quit = true;
		}
	}// end of joinMembership

	// 회원 삭제
	public static void deleteUser(){
		if (getCountUser() == 0) {
			System.out.println("DB에 저장된 고객이 없습니다.");
			return;
		}
		String inputID = null;
		boolean quit = false;
		while (!quit) {
			System.out.print("삭제할 고객 계정의 ID를 입력하세요 (취소시 '취소' 입력):");
			inputID = main.Main.sc.nextLine();

			if (inputID.equals("취소")) {
				return;
			}

			int usercheck = searchUser(inputID);
			if (usercheck == 1) {
				System.out.println("해당 계정을 찾았습니다.");
				quit = true;
			} else {
				System.out.println("해당 계정을 찾지 못했습니다.");
			}
		} // end of while
		controller.CartManager.cartClear(inputID); 
		
//		controller.CartDAO.deleteUserCart(inputID);
//		controller.PaymentDAO.deleteUserPayment(inputID);//프로시저 테스트
		
		
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL USERTBL_DELETE(?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, inputID);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(inputID + "회원 정보 삭제 완료");
			} else {
				System.out.println(inputID + "회원 정보 삭제 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, con);
		}
		controller.CustomerDAO.setUserToList(Main.userList);
	}

	// 회원 삭제를 위한 유저 검색
	public static int searchUser(String inputID) {
		int cnt = 0;
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			String sql = "{CALL USERTBL_COUNT(?,?)}";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, inputID);
			cstmt.registerOutParameter(2, Types.INTEGER);

			cstmt.executeQuery();
			cnt = cstmt.getInt(2);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, rs, con);
		}
		return cnt;
	}

	public static int getCountUser() {
		int cnt = 0;
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			String sql = "{CALL USERTBL_TOTALCOUNT(?)}";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.registerOutParameter(1, Types.INTEGER);

			cstmt.executeQuery();
			cnt = cstmt.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeResources(cstmt, rs, con);
		}
		return cnt;
	}
	
	// (탈퇴하는 회원의) 구매 내역 삭제
	public static void removePayment() {
		
//		controller.PaymentDAO.setPaymaentToList(Main.totalPaymentList, Main.userList.get(idIndex));// 일단 총 리스트 만듬...
//		controller.CartManager.printTotalPayment(Main.totalPaymentList, Main.userList.get(idIndex));// 삭제될 내용 보여줌
//		System.out.println("위 내역을 삭제 합니다.");
//		Main.totalPaymentList.clear();// 다 보여줬으니 비워줌
//		// 보여준 부분 제외한 리스트 만들어야함
//		setWithoutUserPaymentList(Main.userList.get(idIndex));
//		// 그걸을 파일에 저장
//		savePaymentFile(withoutUserPaymentList);
//		// 리스트 초기화(다시 넣어줄 필요는 없음)
//		withoutUserPaymentList.clear();
//		flag = true;
//		
//		return flag;
	}

//	// 유저 삭제
//	private static boolean deleteCustomer(int idIndex, boolean flag) {
//		System.out.println("ID\t이름\t연락처\t\t주소\t\t나이\t등급\t누적결제금액\t마일리지");
//		System.out.println(Main.userList.get(idIndex).toString());
//		System.out.println("해당 계정을 삭제하겠습니까? Y|N ");
//		String str = Main.sc.nextLine();
//		if (str.toUpperCase().equals("Y")) {
//			boolean removePaymentflag = false;
//
//			removePaymentflag = removePayment(idIndex, removePaymentflag);// 계정 삭제 전에 구매 내역도 제거해줌
//			if (removePaymentflag) {
//				System.out.println("해당 계정의 구매 내역이 삭제 되었습니다.");
//				Admin.removeUserflag = true;
//			} else {
//				System.out.println("해당 계정의 구매 내역 삭제가 실패했습니다.");
////			} // end of else if
//
//			Main.userList.remove(idIndex);
//			flag = true;
//		} else {
//			flag = false;
//		} // end of else if
//		return flag;
//	}

}
