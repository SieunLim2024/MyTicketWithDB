package controller;

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
						if (!main.Main.cart.isCartInPerformance(performanceList.get(numIndex).getPerformanceID(),
								numTicket, seatNum)) { // 이미 카트에 항목 있으면 수량과 좌석 번호만만 업데이트
							main.Main.cart.insertCartDB(numIndex, numTicket, main.Main.nowUser, seatNum); // 아니면
						} // end of if
					} // end of else if(수량이 잔여 좌석 내인지...)
					controller.CartDAO.setCartToList(main.Main.nowUser);
				} // end of if(Y인지 검사)
				quit = true;
			} else {
				System.out.println("다시 입력해주세요");
			}
		}
	}

	// 카트 모두 비우기
	public static void cartClear(Customer nowUser) throws CartException {
		if (main.Main.cart.cartCount == 0) {
			throw new CartException("장바구니에 항목이 없습니다.");
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
				main.Main.cart.deleteCart(nowUser);

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
			main.Main.paymentList.addAll(main.Main.cart.cartItem);
			controller.PaymentDAO.insertPaymentList();
			System.out.println("감사합니다. 결제가 완료 되었습니다.");
			main.Main.cart.deleteCart(main.Main.nowUser); // 장바구니 비우기

			int price =printPayment(main.Main.paymentList);

			main.Main.nowUser.setAccumulatedPayment(main.Main.nowUser.getAccumulatedPayment() + price);// 누적 구매액 수정
			main.Main.nowUser.setMileage((int) (main.Main.nowUser.getMileage() + price * 0.01));// 마일리지 1%씩 적립
//			nowUser.setBuyNum(nowUser.getBuyNum() + payment.totalcount);// 누적 구매 항목수 수정
			if (main.Main.nowUser.getAccumulatedPayment() > 150000) {
				main.Main.nowUser.setGrade("VIP");
			}
//			Admin.saveUserToFile();// 변경된 내용이 있으므로 저장

			for (int j = 0; j < main.Main.paymentList.size(); j++) {
				for (int i = 0; i < Admin.performanceList.size(); i++) {// 공연리스트에 들어가 있는 것 만큼 돌려서
					if (Admin.performanceList.get(i).getPerformanceID().equals(main.Main.paymentList.get(j).getPerformanceId())) {// 구매한
						controller.PerformanceDAO.updatePerformance(main.Main.paymentList.get(j).getPerformanceId(),
								Admin.performanceList.get(i).getSoldSeats() + main.Main.paymentList.get(j).getQuantity());
//						Admin.performanceList.get(i).setSoldSeats(
//								Admin.performanceList.get(i).getSoldSeats() + paymentList.get(j).getQuantity());
					} // end of if
				} // end of for
			} // end of for
			
			

			Admin.performanceList.clear();// 초기화
			controller.PerformanceDAO.setPerformanceToList();// 팔린 티켓 수량 반영

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
					main.Main.cart.printCart();
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
					
					if (performancecheck ==1&&flag == true) {
						System.out.println("해당 공연을 찾았습니다.");
						quit = true;
						System.out.println("장바구니에서 삭제하겠습니까? Y|N ");
						String str = input.nextLine();
						if (str.toUpperCase().equals("Y")) {
							controller.PerformanceManager.resetSeats(indexInCart, indexInPer); // 선점된 좌석 비선점 상태로 되돌려준다.
							main.Main.cart.removeCart(inputID, main.Main.nowUser);
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

		//누적 구매 내역 출력
		public static void printTotalPayment(ArrayList<CartItem> list, Customer nowUser) {
			System.out.println("============================================================================================");
			System.out.println("누적 구매 내역: ");
			System.out.println("--------------------------------------------------------------------------------------------");
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
				}
				System.out.println("--------------------------------------------------------------------------------------------");
				System.out.println("누적 구매 금액: " + nowUser.getAccumulatedPayment());
				System.out.println("--------------------------------------------------------------------------------------------");
			}
		}
		
		//영수증 출력
		public static int printPayment(ArrayList<CartItem> list) {
			int totalPrice = 0;
			double discountRate = 0;// 할인율
			if (Main.nowUser.getGrade().equals("VIP")) {
				discountRate = 0.01;// vip면 할인 해줌
			}
			System.out.println("============================================================================================");
			System.out.println("영수증: ");
			System.out.println("--------------------------------------------------------------------------------------------");
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
				System.out.println("--------------------------------------------------------------------------------------------");
				System.out.println("할인: " + (int) (totalPrice * discountRate));
				System.out.println("합계: " + totalPrice);
				System.out.println("--------------------------------------------------------------------------------------------");
			}
			return totalPrice;
		}
}
