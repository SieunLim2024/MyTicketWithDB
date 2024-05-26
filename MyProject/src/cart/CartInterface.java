package cart;

import java.util.ArrayList;

import model.Customer;
import model.Performance;

public interface CartInterface {
//	void printPerformanceList(ArrayList<Performance> pList); // 전체 공연 정보 목록 출력

	boolean isCartInPerformance(String id,int quantity,String seatNum); // 장바구니에 담긴 갯수를 고객 임의 지정 좌석갯수 증가

	public void insertCartDB(int numIndex, int numTicket, Customer nowUser, String seatNum); // DB에 추가

	void removeCart(String performanceId,Customer nowUser); // 장바구니 performanceId의 항목을 삭제

	void deleteCart(Customer nowUser); // 장바구니의 모든 항목을 삭제
	
	void printCart();
	//공연일 지나면 구매 안됨
}