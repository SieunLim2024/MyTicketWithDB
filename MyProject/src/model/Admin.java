package model;

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
import java.util.Date;
import java.util.Scanner;

import main.Main;

public class Admin {
	public static Scanner sc = new Scanner(System.in);
	public static boolean adminLogin = false;
	public static final int PERINFONUM = 9;
	public static ArrayList<Performance> performanceList = new ArrayList<Performance>();
	public static ArrayList<Customer> CustomerList = new ArrayList<Customer>();
	public static ArrayList<CartItem> withoutUserPaymentList = new ArrayList<CartItem>();
	public static boolean removeUserflag = false;
	public static boolean removePayflag = false;
	public static boolean removeflag = false;

	// 관리자 로그인
	public static void adminLogin() {
		BufferedReader reader = null;
		String id = null;
		String pw = null;
		try {
			reader = new BufferedReader(new FileReader("admin.txt"));
			id = reader.readLine();
			pw = reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("관리자 정보를 입력하세요");

		System.out.print("아이디: ");
		String adminId = sc.nextLine();
		System.out.print("비밀번호: ");
		String adminPW = sc.nextLine();

		Admin admin = new Admin();
		if (adminId.equals(id) && adminPW.equals(pw)) {

			System.out.println("관리자 로그인에 성공 하였습니다.");
			adminLogin = true;
			main.Main.adminMenu();// 관리자 메뉴로
		} else {
			System.out.println("아이디나 비밀번호가 맞지 않습니다.");
		}

	}

	

	

	

	

	

	



}