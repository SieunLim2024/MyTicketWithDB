package model;

import java.util.ArrayList;

public class Performance implements Comparable<Performance> {
	public static final int PERIFONUM=12;
	private String performanceID;// key값으로 할 예정
	private String performanceName; // 공연명
	private String genre; // 장르(뮤지컬, 연극, 콘서트)
	private String dayOfPerformance; // 공연일
	private String venue; // 장소
	private int limitAge; // 관람제한연령
	private int totalSeats; // 총좌석수
	private int soldSeats; // 판매좌석수
	private int ticketPrice; // 티켓가격,compare
	private String[][] seats;//좌석 현황
	private int yseats;//몇줄
	private int xseats;//한 줄에 몇명
	

	public Performance() {
		super();
	}

	public void calSeats() {
		seats= new String[yseats][xseats];
	}

	public String getPerformanceID() {
		return performanceID;
	}

	public void setPerformanceID(String performanceID) {
		this.performanceID = performanceID;
	}

	public String getPerformanceName() {
		return performanceName;
	}

	public void setPerformanceName(String performanceName) {
		this.performanceName = performanceName;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getDayOfPerformance() {
		return dayOfPerformance;
	}

	public void setDayOfPerformance(String dayOfPerformance) {
		this.dayOfPerformance = dayOfPerformance;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public int getLimitAge() {
		return limitAge;
	}

	public void setLimitAge(int limitAge) {
		this.limitAge = limitAge;
	}

	public int getTotalSeats() {
		return totalSeats;
	}

	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}

	public int getSoldSeats() {
		return soldSeats;
	}

	public void setSoldSeats(int soldSeats) {
		this.soldSeats = soldSeats;
	}

	public int getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(int ticketPrice) {
		this.ticketPrice = ticketPrice;
	}
	
	public String[][] getSeats() {
		return seats;
	}
	public void setSeats(String[][] seats) {
		this.seats = seats;
	}
	public int getYseats() {
		return yseats;
	}
	public void setYseats(int yseats) {
		this.yseats = yseats;
	}
	public int getXseats() {
		return xseats;
	}
	public void setXseats(int xseats) {
		this.xseats = xseats;
	}
	
	@Override
	public String toString() {
		return "Performance [performanceID=" + performanceID + ", performanceName=" + performanceName + ", genre=" + genre
				+ ", dayOfPerformance=" + dayOfPerformance + ", venue=" + venue + ", limitAge=" + limitAge
				+ ", totalSeats=" + totalSeats + ", soldSeats=" + soldSeats + ", ticketPrice=" + ticketPrice + "]";
	}

	@Override
	public int compareTo(Performance o) {
		return this.ticketPrice-o.ticketPrice;
	}

}