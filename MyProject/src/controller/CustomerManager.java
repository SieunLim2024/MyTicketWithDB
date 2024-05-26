package controller;

import main.Main;
import model.Customer;

public class CustomerManager {

	// 아이디 중복 검사
	public static boolean serchId(String id) {
		boolean flag = false;
		for (int i = 0; i < main.Main.userList.size(); i++) {
			if (id.equals(main.Main.userList.get(i).getCustomerId())) {
				System.out.println("이미 사용중인 아이디 입니다.");
				flag = true;
				break;
			} // end of if
		} // end of for
		if (flag == false) {
			System.out.println("사용 가능한 아이디 입니다.");
		}
		return flag;
	}

	// 유저 로그인
	public static Customer userLogin() {
		System.out.println("유저 로그인");
		// 유저 로그인 성공시 checkLogin true로 변경
		System.out.print("아이디:");
		String inputId = main.Main.sc.nextLine();
		System.out.print("비밀번호:");
		String inputPw = main.Main.sc.nextLine();
		for (int i = 0; i < main.Main.userList.size(); i++) {
			if (inputId.equals(main.Main.userList.get(i).getCustomerId()) && inputPw.equals(main.Main.userList.get(i).getPw())) {
				main.Main.checkLogin = true;
				main.Main.nowUserIndex = i;
				System.out.println("로그인 성공!");
				break;
			}
		}
		if (main.Main.checkLogin == false) {
			System.out.println("로그인 실패");
		}
		return main.Main.userList.get(main.Main.nowUserIndex);
	}
	
	// 모든 고객 정보 출력
		public static void printCustomer() {
			System.out.println("ID\t\tPW\t이름\t연락처\t\t주소\t\t나이\t등급\t누적결제금액\t마일리지");
			for (int i = 0; i < Main.userList.size(); i++) {
				System.out.print(Main.userList.get(i).getCustomerId() + " ");
				System.out.print(Main.userList.get(i).getPw() + "\t");
				System.out.print(Main.userList.get(i).getCustomerName() + "\t");
				System.out.print(Main.userList.get(i).getPhone() + "\t");
				System.out.print(Main.userList.get(i).getAddress() + "\t");
				System.out.print(Main.userList.get(i).getAge() + "\t");
				System.out.print(Main.userList.get(i).getGrade() + "\t");
				System.out.print(Main.userList.get(i).getAccumulatedPayment() + "\t\t");
				System.out.print(Main.userList.get(i).getMileage() + "\n");

			} // end of for
		}// end of printCusetomer

}
