package model;

import java.util.Scanner;

public class Customer {
	private String customerId;// key
	private String pw;
	private String customerName;// compare
	private String phone;
	private String address;
	private int age;// 여기까지 회원가입

	private String grade;
	private int accumulatedPayment; // 누적결제금액, 등급 평가시 사용할 것
	private int mileage; // 기본 고객부터 마일리지 적립
//	private int buyNum;//누적 구매 항목수(수량 x)
//	private int cartNum;//
	
	public static final int CUSTOMERINFONUM=11;

	
	
	public Customer() {
		super();
	}
	public Customer(String customerId, String pw,String customerName, String phone,  String address, int age) {
		super();
		this.customerId = customerId;
		this.pw = pw;
		this.customerName = customerName;
		this.phone = phone;
		this.address = address;
		this.age = age;
		
		this.grade="Basic";
		this.accumulatedPayment=0;
		this.mileage=0;
//		this.buyNum=0;
//		this.cartNum=0;
	}
	public Customer( String customerId, String pw,String customerName, String phone, String address, int age, String grade,
			int accumulatedPayment, int mileage) {
		super();
		this.customerId = customerId;
		this.pw = pw;
		this.customerName = customerName;
		this.phone = phone;
		this.address = address;
		this.age = age;
		this.grade = grade;
		this.accumulatedPayment = accumulatedPayment;
		this.mileage = mileage;
	}
	
	
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public int getAccumulatedPayment() {
		return accumulatedPayment;
	}
	public void setAccumulatedPayment(int accumulatedPayment) {
		this.accumulatedPayment = accumulatedPayment;
	}
	public int getMileage() {
		return mileage;
	}
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}
//	public int getBuyNum() {
//		return buyNum;
//	}
//	public void setBuyNum(int buyNum) {
//		this.buyNum = buyNum;
//	}
//	public int getCartNum() {
//		return cartNum;
//	}
//	public void setCartNum(int cartNum) {
//		this.cartNum = cartNum;
//	}
	@Override
	public String toString() {
		return customerId +"\t"+customerName +"\t"+phone +"\t"+ address+"\t"+ age +"\t"+ grade +"\t"+ accumulatedPayment +"\t\t"+ mileage;
	}
}