package main;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Collectors;

import exception.CartException;
import model.Admin;
import model.Cart;
import model.CartItem;
import model.Customer;
import model.Performance;
import view.MainMenuChoice;

public class Main {
	public static final int INFONUM = 10; // 회원 정보 항목 개수
	public static Scanner sc = new Scanner(System.in);
	public static ArrayList<Customer> userList = new ArrayList<Customer>();
	public static ArrayList<CartItem> paymentList = new ArrayList<>();
	public static ArrayList<CartItem> totalPaymentList = new ArrayList<>();
	public static ArrayList<CartItem> totalCartList = new ArrayList<>();
	public static Cart cart = new Cart();
	public static Cart payment = new Cart();
	public static boolean checkLogin = false;
	public static boolean duplicateID = false;
	public static Customer nowUser = null; // 로그인 한 계정 저장
	public static int nowUserIndex = 0; // 로그인 한 계정 저장

	public static void main(String[] args) {
		controller.CustomerDAO.setUserToList(userList);
		controller.PerformanceDAO.setPerformanceToList();
		// 로그인
		login();
		if (checkLogin) { // 로그인 성공시에 메인 메뉴로
			controller.PaymentDAO.setPaymaentToList(totalPaymentList, nowUser);
			// 로그인 한 고객의 카트 정보와 구매 정보 파일에서 불러옴
			controller.CartDAO.setCartToList(nowUser);
			// 메인 메뉴로
			mainMenu(nowUser);
		}
	}

	// 로그인
	private static void login() {
		boolean quit = false;
		while (!quit) {
			view.LoginMenuView.LoginMenuView();
			System.out.print("메뉴 선택>> ");
			String input = sc.nextLine().replaceAll("[^1-4]", "0");// 1~4 이외는 0, 메뉴에 0 없어야함
			int num = Integer.parseInt(input); // 형 변환
			if (num < 1 || num > 4) {
				System.out.println("메뉴를 바르게 입력해주세요.");
				continue;
			} // end of if
			
			switch (num) {
			case view.LoginMenuChoice.LOGIN:
				nowUser = controller.CustomerManager.userLogin();
				if (checkLogin) {
					quit = true;
				}
				break;
			case view.LoginMenuChoice.SIGNUP:
				controller.CustomerDAO.joinMembership();
				break;
			case view.LoginMenuChoice.ADMIN:
				Admin.adminLogin();
				break;
			case view.LoginMenuChoice.EXIT:
				quit = true;
				System.out.println("종료합니다.");
				break;
			}// end of switch
		} // end of while
	}// end of login


	// 메인 메뉴
	private static void mainMenu(Customer nowUser) {
		boolean quit = false;
		while (!quit) {
			view.MainMenuViewer.MainMenuViewer();
			
			System.out.print("메뉴 선택>> ");
			String input = sc.nextLine().replaceAll("[^1-4]", "0");// 1~4 이외는 0, 메뉴에 0 없어야함
			int num = Integer.parseInt(input); // 형 변환
			
			if (num < 1 || num > 4) {
				System.out.println("메뉴를 바르게 입력해주세요.");
				continue;
			} // end of if
			
			switch (num) {
			case MainMenuChoice.MYPAGE:
				view.MainMypageViewer.MainMypageViewer();
				
				System.out.println(nowUser); // 회원 정보 출력
				controller.CartManager.printTotalPayment(totalPaymentList, nowUser);// 누적 구매 내역 출력
				break;
			case MainMenuChoice.CARTPAGE:
				cart();
				break;
			case MainMenuChoice.PERFORPAGE:
				selectGenre(); // 장르 고르게 하기
				break;
			case MainMenuChoice.EXIT:
				quit = true;
				System.out.println("종료합니다.");
				break;
			}// end of switch
		} // end of while
	}// end of mainMenu

	// 장르 선택
	private static void selectGenre() {
		ArrayList<Performance> showList = new ArrayList<Performance>();

		showList.addAll(Admin.performanceList); // 깊은 복사해둠.
		boolean quit = false;
		while (!quit) {
			view.GenreMenuViewer.GenreMenuViewer();
			System.out.print("메뉴 선택>> ");
			String input = sc.nextLine().replaceAll("[^1-5]", "0");// 1~4 이외는 0으로
			int num = Integer.parseInt(input); // 형 변환
			
			if (num < 1 || num > 5) {
				System.out.println("메뉴를 바르게 입력해주세요.");
				continue;
			} // end of if
			
			switch (num) {
			case view.GenreMenuChoice.ALL:
				controller.PerformanceManager.printPerformance(showList);
				// 고른거 보여주고...
				sortOrCart(showList);
				break;
			case view.GenreMenuChoice.MUSICAL:
				ArrayList<Performance> showMList = null;
				showMList = (ArrayList<Performance>) showList.stream().filter(s -> s.getGenre().equals("뮤지컬"))
						.collect(Collectors.toList());
				controller.PerformanceManager.printPerformance(showMList);
				// 고른거 보여주고...
				sortOrCart(showMList);
				break;
			case view.GenreMenuChoice.DRAMA:
				ArrayList<Performance> showPList = null;
				showPList = (ArrayList<Performance>) showList.stream().filter(s -> s.getGenre().equals("연극"))
						.collect(Collectors.toList());
				controller.PerformanceManager.printPerformance(showPList);
				// 고른거 보여주고...
				sortOrCart(showPList);
				break;
			case view.GenreMenuChoice.CONCERT:
				ArrayList<Performance> showCList = null;
				showCList = (ArrayList<Performance>) showList.stream().filter(s -> s.getGenre().equals("콘서트"))
						.collect(Collectors.toList());
				controller.PerformanceManager.printPerformance(showCList);
				// 고른거 보여주고...
				sortOrCart(showCList);
				break;
			case view.GenreMenuChoice.EXIT:
				quit = true;
				System.out.println("종료합니다.");
				showList.clear();// 초기화
				break;
			}// end of switch
		} // end of while
	}
	
	//공연예매하거나 가격순으로 정렬하거나...
	private static void sortOrCart(ArrayList<Performance> showList) {
		boolean quit = false;
		while (!quit) {
			view.GenreMenu2Viewer.GenreMenu2Viewer();
			System.out.print("메뉴 선택>> ");
			String input = sc.nextLine().replaceAll("[^1-4]", "0");// 1~4 이외는 0, 메뉴에 0 없어야함
			int num = Integer.parseInt(input); // 형 변환
			
			if (num < 1 || num > 4) {
				System.out.println("메뉴를 바르게 입력해주세요.");
				continue;
			} // end of if
			
			
			switch (num) {
			case view.GenreMenu2Choice.LOW:
				ArrayList<Performance> showLowList = new ArrayList<Performance>();
				showLowList.addAll(showList); // 깊은 복사해둠.
				Collections.sort(showLowList);
				controller.PerformanceManager.printPerformance(showLowList);
				break;
			case view.GenreMenu2Choice.HEIGHT:
				ArrayList<Performance> showHighList = new ArrayList<Performance>();
				showHighList.addAll(showList); // 깊은 복사해둠.
				Collections.sort(showHighList, Comparator.reverseOrder());
				controller.PerformanceManager.printPerformance(showHighList);
				break;
			case view.GenreMenu2Choice.ADDCART:
				controller.CartManager.askaddCart();
				break;
			case view.GenreMenu2Choice.EXIT:
				quit = true;
				System.out.println("이전 메뉴로 돌아갑니다.");
				break;
			}// end of switch
		} // end of while
	}


	// 메인 메뉴에서 카트 선택시 ...
	private static void cart() {
		boolean quit = false;
		while (!quit) {
			cart.printCart();
			view.CartPageMenuViewer.CartPageMenuViewer();
			System.out.print("메뉴 선택>> ");
			String input = Main.sc.nextLine().replaceAll("[^1-4]", "0");// 1~4 이외는 0, 메뉴에 0 없어야함
			int num = Integer.parseInt(input); // 형 변환
			
			if (num < 1 || num > 4) {
				System.out.println("메뉴를 바르게 입력해주세요.");
				continue;
			} // end of if
			
			switch (num) {
			case view.CartPageMenuChoice.ITEMREMOVE:
				try {
					controller.CartManager.cartRemoveItem();
				} catch (CartException e) {
					System.out.println(e.getMessage());
				}
				break;
			case view.CartPageMenuChoice.ALLREMOVE:
				try {
					controller.CartManager.cartClear(nowUser);
				} catch (CartException e) {
					System.out.println(e.getMessage());

				}
				break;
			case view.CartPageMenuChoice.BUY:
				try {
					if (cart.cartCount == 0) {
						throw new CartException("장바구니가 비어있습니다. 장바구니에 공연을 담아주세요.");
					} else {
						controller.CartManager.buy();
					}
				} catch (CartException e) {
					System.out.println(e.getMessage());
				}
				break;
			case view.CartPageMenuChoice.EXIT:
				quit = true;
				System.out.println("이전 메뉴로 돌아갑니다.");
				break;
			}// end of switch
		} // end of while
	}// end of cart
	
	// 관리자 메뉴
		public static void adminMenu() {
			boolean quit = false;
			while (!quit) {
				view.AdminMenuViewer.AdminMenuViewer();
				System.out.print("메뉴 선택>> ");
				String input = sc.nextLine().replaceAll("[^1-5]", "0");// 1~4 이외는 0, 메뉴에 0 없어야함
				int num = Integer.parseInt(input); // 형 변환
				
				if (num < 1 || num > 5) {
					System.out.println("메뉴를 바르게 입력해주세요.");
					continue;
				} // end of if
				
				switch (num) {
				case view.AdminMenuChoice.ADDPERFOR:
					controller.PerformanceDAO.addPerformance();
					break;
				case view.AdminMenuChoice.REMOVEPERFOR:
					controller.PerformanceDAO.deletePerformance();
					break;
				case view.AdminMenuChoice.PRINTUSER:
					controller.CustomerManager.printCustomer();
					break;
				case view.AdminMenuChoice.REMOVEUSER:
					controller.CustomerDAO.deleteUser();
					break;
				case view.AdminMenuChoice.EXIT:
					quit = true;
					System.out.println("관리자 메뉴를 종료합니다.");
					break;

				}// end of switch
			} // end of while
		}// end of admiMenu
	
//	// 공연 파일 읽어 공연 개수 계산 함
//	private static int countPerformance() {
//		try {
//			FileReader fr = new FileReader("performance.txt");
//			BufferedReader reader = new BufferedReader(fr);
//			String str;
//			int num = 0; // 공연 개수
//			while ((str = reader.readLine()) != null) {
//				if (str.contains("pID")) {
//					++num;
//				}
//			}
//			reader.close();
//			fr.close();
//			return num;
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		return 0;
//	}
	
//	// 현재 로그인 한 유저의 카트정보를 totalCartList에 넣어준다.
//	private static void addUserCartToTotal() {
//		for (int i = 0; i < cart.cartItem.size(); i++) {
//			totalCartList.add(cart.cartItem.get(i));
//		}
//
//	}
}