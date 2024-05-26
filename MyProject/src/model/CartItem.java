package model;

public class CartItem {
	public static final int CARTIFONUM = 6;
	private Performance item;
	private String customerID; // 구매자 id 추가하고 cart에 id 와 pw 제거함
	private String performanceId; // 공연id
	private String performanceName; // 공연명
	private int quantity; // 인원수
	private int totalPrice; // 총구매금액
	private String seatNum;	//좌석 번호
	

	public CartItem() {
		super();
	}

	//Cart.insertPerformance에서 사용
	public CartItem(Performance item, int quantity,Customer nowUser,String seatNum) {
		super();
		this.item = item;
		this.customerID=nowUser.getCustomerId();
		this.performanceId=item.getPerformanceID();
		this.performanceName=item.getPerformanceName();
		this.quantity=quantity;
		updateTotalPrice();
		this.seatNum=seatNum;
	}
	
	//모든 변수 받아서 생성함(Admin.setWithoutUserPaymentList,Main.setPaymaentToList,Cart.setCartToList()에서 사용
	public CartItem(String customerID, String performanceId, String performanceName, int quantity, int totalPrice,String seatNum) {
		super();
		this.customerID = customerID;
		this.performanceId = performanceId;
		this.performanceName = performanceName;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.seatNum=seatNum;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public String getPerformanceName() {
		return performanceName;
	}
	public void setPerformanceName(String performanceName) {
		this.performanceName = performanceName;
	}
	public Performance getItem() {
		return item;
	}
	public void setItem(Performance item) {
		this.item = item;
	}
	public String getPerformanceId() {
		return performanceId;
	}
	public void setPerformanceId(String performanceId) {
		this.performanceId = performanceId;
	}
	 public String getSeatNum() {
		return seatNum;
	}
	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public void updateTotalPrice() {
		totalPrice = this.item.getTicketPrice() * this.quantity;
	}
	

}