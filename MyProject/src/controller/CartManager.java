package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import exception.CartException;
import main.Main;
import model.Admin;
import model.CartItem;
import model.Customer;
import model.Performance;

public class CartManager {
	// 장바구니에 담기
	public static void addCart(ArrayList<Performance> performanceList) {
		boolean quit = false;
		while (!quit) {
			System.out.print("장바구니에 추가할 공연의 ID를 입력하세요 : ");
			String inputID = main.Main.sc.nextLine();

			boolean flag = false; // 일치여부
			int numIndex = -1; // 인덱스 번호
			int numTicket = 0;

			for (int i = 0; i < performanceList.size(); i++) {
				if (inputID.equals(performanceList.get(i).getPerformanceID())) {
					numIndex = i;
					flag = true;
					break;
				}
			} // end of for

			// 일치하면 장바구니 추가 여부를 묻는다.
			if (flag) {
				System.out.println("장바구니에 추가하겠습니까? Y|N ");
				String str = main.Main.sc.nextLine();
				if (str.toUpperCase().equals("Y")) {
					System.out.print("추가할 수량을 입력해주세요: ");

					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "0");// 0~9 이외는 0
					numTicket = Integer.parseInt(input); // 형 변환

					// 잔여 좌석과 살 티켓 수량 비교
					if (Admin.performanceList.get(numIndex).getSoldSeats() + numTicket > Admin.performanceList
							.get(numIndex).getTotalSeats()) {
						System.out.println("잔여 좌석보다 구매 수량이 많습니다.");
					} else if (numTicket == 0) {
						System.out.println("수량을 바르게 입력해주세요.");
					} else {

//							Performance.printSeats(Admin.performanceList, inputID);// 좌석 보여주기
						// 좌석 고르기
						String seatNum = controller.PerformanceManager.askSeatNum(numTicket, inputID, numIndex)
								.toUpperCase();// 고른 좌석 받음.
						System.out.println(
								"--------------------------------------------------------------------------------------------");
						System.out.println(performanceList.get(numIndex).getPerformanceName() + " 공연이 장바구니에 추가되었습니다.");
						System.out.println("선택한 좌석은 " + seatNum + " 입니다.");
						// 장바구니에 넣기 이미 카트에 항목 있으면 수량&좌석번호만 업데이트 아니면 항목 추가
						if (!isCartInPerformance(performanceList.get(numIndex).getPerformanceID(),
								numTicket, seatNum)) { // 이미 카트에 항목 있으면 수량과 좌석 번호만만 업데이트
							insertCartDB(numIndex, numTicket, main.Main.nowUser, seatNum); // 아니면
						} // end of if
					} // end of else if(수량이 잔여 좌석 내인지...)
					controller.CartDAO.setCartToList(main.Main.nowUser.getCustomerId());
				} // end of if(Y인지 검사)
				quit = true;
			} else {
				System.out.println("다시 입력해주세요");
			}
		}
	}

	// 카트 모두 비우기
	public static void cartClear(String customerId){
		if (main.Main.cart.cartCount == 0) {
			System.out.println("장바구니에 항목이 없습니다.");
		} else {
			System.out.println("장바구니에 모든 항목을 삭제 하겠습니까? Y|N ");
			Scanner input = new Scanner(System.in);
			String str = input.nextLine();

			if (str.toUpperCase().equals("Y")) {

				int indexInCart = -1;
				int indexInPer = -1;
				for (int i = 0; i < main.Main.cart.cartCount; i++) {
					for (int j = 0; j < Admin.performanceList.size(); j++) {
						if (main.Main.cart.cartItem.get(i).getPerformanceId()
								.equals(Admin.performanceList.get(j).getPerformanceID())) {
							// 카트리스트에서의 인덱스 확인
							indexInCart = i;
							// 공연 리스트에서의 인덱스 확인
							indexInPer = j;
							// 선점좌석 초기화
							controller.PerformanceManager.resetSeats(indexInCart, indexInPer);
						} // end of if
					} // end of for
				} // end of for
					// 카트 리스트 모두 비우기
				deleteCart(customerId);

			} // end of if
		} // end of if else
	}// end of cartClear

	public static void askaddCart() {
//		countPerformance();
		addCart(Admin.performanceList);

//		// 전체 카트 리스트 초기화
//		main.Main.totalCartList.clear();
//		// 로그인 한 유저 이외의 카트 정보 리스트를 totalCartList에 넣어줌
//		cart.setWithoutUserCartList(nowUser);
//		// totalCartList에 현재 유저의 카트 정보도 넣어주기
//		addUserCartToTotal();
//		// 파일 덮어씌우기
//		saveCartFile(totalCartList);
	}

	// 결제
	public static void buy() throws CartException {
		int count = 0;// 구매 개수
		System.out.println("장바구니에 모든 항목을 결제하고 장바구니는 비워집니다.");
		System.out.println("결제하시겠습니까? Y|N ");
		String str = main.Main.sc.nextLine();
		if (str.toUpperCase().equals("Y")) {
//			main.Main.paymentList.addAll(main.Main.cart.cartItem);
			controller.PaymentDAO.insertPaymentList();
			System.out.println("감사합니다. 결제가 완료 되었습니다.");

			int price = printPayment(main.Main.cart.cartItem);
			deleteCart(main.Main.nowUser.getCustomerId()); // 장바구니 비우기
			
			main.Main.nowUser.setAccumulatedPayment(main.Main.nowUser.getAccumulatedPayment() + price);// 누적 구매액 수정
			main.Main.nowUser.setMileage((int) (main.Main.nowUser.getMileage() + price * 0.01));// 마일리지 1%씩 적립
			if (main.Main.nowUser.getAccumulatedPayment() > 150000) {
				main.Main.nowUser.setGrade("VIP");
			}
//			Admin.saveUserToFile();// 변경된 내용이 있으므로 저장
//
//			for (int j = 0; j < main.Main.paymentList.size(); j++) {
//				for (int i = 0; i < Admin.performanceList.size(); i++) {// 공연리스트에 들어가 있는 것 만큼 돌려서
//					if (Admin.performanceList.get(i).getPerformanceID()
//							.equals(main.Main.paymentList.get(j).getPerformanceId())) {// 구매한
//						controller.PerformanceDAO.updatePerformance(main.Main.paymentList.get(j).getPerformanceId(),
//								Admin.performanceList.get(i).getSoldSeats()
//										+ main.Main.paymentList.get(j).getQuantity());
//					} // end of if
//				} // end of for
//			} // end of for


			controller.PerformanceDAO.setPerformanceToList();// 팔린 티켓 수량 반영
			controller.PaymentDAO.setPaymaentToList(main.Main.paymentList, main.Main.nowUser);

//			writePaymentFile(paymentList);
			System.out.println("구매 내역 작성 완료!");
			main.Main.payment.totalcount = 0;// 초기화
//			paymentList.clear();// 초기화
//			totalPaymentList.clear();// 초기화
//			setPaymaentToList(totalPaymentList, nowUser);// 다시 저장
		} else {
			System.out.println("결제를 취소합니다.");
		} // end of else if
	}// end of buy

	// 장바구니 항목 삭제
	public static void cartRemoveItem() throws CartException {
		if (main.Main.cart.cartCount == 0) {
			throw new CartException("장바구니 항목이 없습니다.");
		} else {
			if (main.Main.cart.cartCount >= 0) {
				printCart();
			} // end of if
			boolean quit = false;
			while (!quit) {
				System.out.print("장바구니에서 삭제할 공연의 ID를 입력하세요 :");
				Scanner input = new Scanner(System.in);
				String inputID = input.nextLine();
				boolean flag = false;
				int indexInCart = -1;
				int indexInPer = -1;

				// 카트리스트에서의 인덱스 확인
				for (int i = 0; i < main.Main.cart.cartCount; i++) {
					if (inputID.equals(main.Main.cart.cartItem.get(i).getPerformanceId())) {
						indexInCart = i;
						flag = true;
						break;
					} // end of if
				} // end of for
					// 공연 리스트에서의 인덱스 확인
				for (int i = 0; i < Admin.performanceList.size(); i++) {
					if (inputID.equals(Admin.performanceList.get(i).getPerformanceID())) {
						indexInPer = i;
						break;
					} // end of if
				} // end of for
				int performancecheck = controller.PerformanceDAO.searchPerformanceID(inputID);

				if (performancecheck == 1 && flag == true) {
					System.out.println("해당 공연을 찾았습니다.");
					quit = true;
					System.out.println("장바구니에서 삭제하겠습니까? Y|N ");
					String str = input.nextLine();
					if (str.toUpperCase().equals("Y")) {
						controller.PerformanceManager.resetSeats(indexInCart, indexInPer); // 선점된 좌석 비선점 상태로 되돌려준다.
						removeCart(inputID, main.Main.nowUser);
//							// totalCartList 초기화
//							totalCartList.clear();
//							// 로그인 한 유저 이외의 카트 정보 리스트를 totalCartList에 넣어줌
//							cart.setWithoutUserCartList(nowUser);
//							// totalCartList에 현재 유저의 카트 정보도 넣어주기
//							addUserCartToTotal();
//							 파일 덮어씌우기
//							saveCartFile(totalCartList);
					} // end of if
					quit = true;
				} else {
					System.out.println("다시 입력해 주세요");
				} // end of if else
			} // end of while
		} // end of else if
	}// end of cartRemoveItem

	// 누적 구매 내역 출력
	public static void printTotalPayment(ArrayList<CartItem> list, Customer nowUser) {
		System.out.println(
				"============================================================================================");
		System.out.println("누적 구매 내역: ");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		if (list.size() == 0) {
			System.out.println("아직 구매 내역이 없습니다.");
		} else {
			System.out.println("수량\t총액\t좌석 번호    공연ID\t\t    공연명");
			for (int i = 0; i < list.size(); i++) {
				System.out.print(list.get(i).getQuantity() + "\t:");
				System.out.print(list.get(i).getTotalPrice() + "\t:");
				System.out.print(list.get(i).getSeatNum() + "\t:");
				System.out.print(list.get(i).getPerformanceId() + "   :");
				System.out.print(list.get(i).getPerformanceName());
				System.out.println("\t");
			}
			System.out.println(
					"--------------------------------------------------------------------------------------------");
			System.out.println("누적 구매 금액: " + nowUser.getAccumulatedPayment());
			System.out.println(
					"--------------------------------------------------------------------------------------------");
		}
	}

	// 영수증 출력
	public static int printPayment(ArrayList<CartItem> list) {
		int totalPrice = 0;
		double discountRate = 0;// 할인율
		if (Main.nowUser.getGrade().equals("VIP")) {
			discountRate = 0.01;// vip면 할인 해줌
		}
		System.out.println(
				"============================================================================================");
		System.out.println("영수증: ");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		if (list.size() == 0) {
			System.out.println("아직 구매 내역이 없습니다.");
		} else {
			System.out.println("수량\t총액\t좌석 번호\t\t공연ID\t\t    공연명");
			for (int i = 0; i < list.size(); i++) {
				System.out.print(list.get(i).getQuantity() + "\t:");
				System.out.print(list.get(i).getTotalPrice() + "\t:");
				System.out.print(list.get(i).getSeatNum() + "\t:");
				System.out.print(list.get(i).getPerformanceId() + "   :");
				System.out.print(list.get(i).getPerformanceName());
				System.out.println("\t");
				totalPrice += list.get(i).getTotalPrice();// 결제할 금액
				model.Cart.totalcount++;
			}
			totalPrice -= ((int) (totalPrice * discountRate));// 할인 적용된 최종 가격
			System.out.println(
					"--------------------------------------------------------------------------------------------");
			System.out.println("할인: " + (int) (totalPrice * discountRate));
			System.out.println("합계: " + totalPrice);
			System.out.println(
					"--------------------------------------------------------------------------------------------");
		}
		return totalPrice;
	}

	// 카트 내용 출력
	public static void printCart() {
		System.out.println(
				"============================================================================================");
		System.out.println("장바구니 등록 목록: ");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		if (main.Main.cart.cartCount == 0) {
			System.out.println("장바구니가 비어있습니다.");
		} else {
			System.out.println("수량\t총액\t좌석 번호\t공연ID\t\t    공연명");
			for (int i = 0; i < main.Main.cart.cartItem.size(); i++) {
				System.out.print(main.Main.cart.cartItem.get(i).getQuantity() + "\t:");
				System.out.print(main.Main.cart.cartItem.get(i).getTotalPrice() + "\t:");
				System.out.print(main.Main.cart.cartItem.get(i).getSeatNum() + "\t:");
				System.out.print(main.Main.cart.cartItem.get(i).getPerformanceId() + "   :");
				System.out.print(main.Main.cart.cartItem.get(i).getPerformanceName());
				System.out.println("\t");
			}
		}
	}

	// 카트 안에 해당 공연이 있는지 확인
	public static boolean isCartInPerformance(String id, int quantity, String seatNum) {
		boolean flag = false;
		for (int i = 0; i < main.Main.cart.cartItem.size(); i++) {
			if (id.equals(main.Main.cart.cartItem.get(i).getPerformanceId())) {// 카트리스트 안에 이미 해당 공연이 들어있다면...
				main.Main.cart.cartItem.get(i).setQuantity(main.Main.cart.cartItem.get(i).getQuantity() + quantity);// 수량만
																													// 증가
				main.Main.cart.cartItem.get(i).setSeatNum(main.Main.cart.cartItem.get(i).getSeatNum() + "," + seatNum);// 좌석
																														// 번호
																														// 추가해줌
				controller.CartDAO.updateCartDB(main.Main.cart.cartItem.get(i).getPerformanceId(),
						main.Main.cart.cartItem.get(i).getQuantity(), main.Main.cart.cartItem.get(i).getSeatNum());// 수량&좌석번호
																													// 증가
				flag = true;
			}
		}

		return flag;
	}

	// 카트에 공연 항목 추가
	public static void insertCartDB(int numIndex, int numTicket, Customer nowUser, String seatNum) {
//			CartItem pItem = new CartItem(p, quantity, nowUser, seatNum);
//			cartItem.add(pItem);
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL CARTTBL_INSERT(?,?,?,?,?,?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, nowUser.getCustomerId());
			cstmt.setString(2, Admin.performanceList.get(numIndex).getPerformanceID());
			cstmt.setString(3, Admin.performanceList.get(numIndex).getPerformanceName());
			cstmt.setInt(4, numTicket);
			cstmt.setInt(5, (Admin.performanceList.get(numIndex).getTicketPrice() * numTicket));
			cstmt.setString(6, seatNum);

			int value = cstmt.executeUpdate();

			if (value == 0) {
				System.out.println(Admin.performanceList.get(numIndex).getPerformanceName() + " 등록 완료");
			} else {
				System.out.println(Admin.performanceList.get(numIndex).getPerformanceName() + " 등록 실패");
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

		controller.CartDAO.setCartToList(nowUser.getCustomerId());
		main.Main.cart.cartCount = main.Main.cart.cartItem.size();

	}

	public static void deleteCart(String customerId) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL CARTTBL_DELETEALL(?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, customerId);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(customerId + "장바구니 모두 삭제 완료");
			} else {
				System.out.println(customerId + "장바구니 모두 삭제 실패");
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
		controller.CartDAO.setCartToList(customerId);
		main.Main.cart.cartCount = 0;
	}
	//선택한 공연만 장바구니에서 삭제
		public static void removeCart(String performanceId, Customer nowUser) {
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
			controller.CartDAO.setCartToList(nowUser.getCustomerId());
			main.Main.cart.cartCount = main.Main.cart.cartItem.size();
		}
}
