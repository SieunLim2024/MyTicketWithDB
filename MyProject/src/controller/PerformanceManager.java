package controller;

import java.util.ArrayList;

import model.Admin;
import model.Performance;

public class PerformanceManager {
	// 좌석번호 선택
	public static String askSeatNum(int numTicket, String inputID, int numIndex) {
		StringBuffer seatNum = new StringBuffer();// 수량 만큼 문자 수정해야해서 String 대신 StringBuffer
		boolean flag = false;// 이미 고른 좌석인지 검사
		int count = 0;// 좌석 번호 정해진 수
		while (!flag) {
			System.out.println(
					"--------------------------------------------------------------------------------------------");
			printSeats(Admin.performanceList, inputID);
			System.out.print("좌석 번호를 골라주세요 (ex:1A): ");
			String input = main.Main.sc.nextLine();

			String[] seatYX = input.split("");// seatYX[0]은 열번호 seatYX[1]은 행 번호
			String[][] seats = Admin.performanceList.get(numIndex).getSeats();// 좌석 받아옴
			String xNum = seatYX[1].toUpperCase();// 일단 대문자로 바꿔줌

			try {
				if (seats[Integer.parseInt(seatYX[0].toString()) - 1][((int) (xNum.charAt(0))) - 65].equals("■")) {
					System.out.println("이미 선택된 좌석입니다.");
				} else if (seats[Integer.parseInt(seatYX[0].toString()) - 1][((int) (xNum.charAt(0))) - 65]
						.equals("x")) {
					System.out.println("선택이 불가한 좌석입니다.");
				} else {
					System.out.println("선택 가능한 좌석입니다.");
					++count;
					// 선택한 좌석 표시 바꿔주기
					seats[Integer.parseInt(seatYX[0].toString()) - 1][((int) (xNum.charAt(0))) - 65] = "■";// 아스키코드로
					seatNum.append(input);
					// 바꾼후
//						Admin.performanceList.get(numIndex).setSeats(seats);// 좌석정보 리스트에 업데이트
					StringBuffer sb = new StringBuffer();
					for (int k = 0; k < Admin.performanceList.get(numIndex).getYseats(); k++) {
						for (int l = 0; l < Admin.performanceList.get(numIndex).getXseats(); l++) {
							sb.append(seats[k][l]);
						}
					} // end of for
					controller.PerformanceDAO.updatePerformance(Admin.performanceList.get(numIndex).getPerformanceID(),
							sb.toString());
//						Admin.savePerformanceFile(Admin.performanceList);// 파일에 다시 저장

					if (count != numTicket) {// 마지막 티켓이 아니라면...
						seatNum.append(",");
					}
					if (count == numTicket) {// 살 티켓 수만큼 좌석 번호 정했다면
						flag = true;
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("좌석을 예시와 같은 형식으로 입력해주세요");
			} catch (NumberFormatException e) {
				System.out.println("좌석을 예시와 같은 형식으로 입력해주세요");
			} catch (Exception e) {
				System.out.println("좌석을 바르게 입력해주세요");
			} // end of try catch
		} // end of while
		return seatNum.toString();// StringBuffer string으로 형변환
	}

	// 선점된 좌석 비선점 상태로 되돌려준다.
	public static void resetSeats(int indexInCart, int indexInPer) {
		String[] seatNum = main.Main.cart.cartItem.get(indexInCart).getSeatNum().split(",");
		String[][] updateSeats = Admin.performanceList.get(indexInPer).getSeats();// 리스트에서 좌석 정보 받아오기
		for (int i = 0; i < seatNum.length; i++) {
			String seatNumstr = seatNum[i].toString();// 형 변환
			updateSeats[Integer.parseInt(seatNumstr.substring(0, 1)) - 1][((int) (seatNumstr.substring(1, 2).charAt(0)))
					- 65] = "□";
		} // end of for

		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < Admin.performanceList.get(indexInPer).getYseats(); k++) {
			for (int l = 0; l < Admin.performanceList.get(indexInPer).getXseats(); l++) {
				sb.append(updateSeats[k][l]);
			}
		} // end of for

		controller.PerformanceDAO.updatePerformance(Admin.performanceList.get(indexInPer).getPerformanceID(),
				sb.toString());
	}// end of resetSeats
	
	// 공연 삭제
	public static boolean removePerformance(int idIndex, boolean flag) {
		System.out.println(Admin.performanceList.get(idIndex).toString());
		System.out.println("해당 공연을 삭제하겠습니까? Y|N ");
		String str = main.Main.sc.nextLine();
		if (str.toUpperCase().equals("Y")) {
			Admin.performanceList.remove(idIndex);
			flag = true;
		} else {
			flag = false;
		} // end of else if
		return flag;

	}// end of removePerformance
	
	
	// 공연(요약본)출력
		public static void printPerformanceList(ArrayList<Performance> list) {
			System.out.println("공연ID\t\t  공연일\t\t장르\t공연명");
			for (int i = 0; i < list.size(); i++) {
				System.out.print(list.get(i).getPerformanceID() + "   ");
				System.out.print(list.get(i).getDayOfPerformance() + "\t");
				System.out.print(list.get(i).getGenre() + "\t");
				System.out.print(list.get(i).getPerformanceName() + "\n");
			} // end of for
		}// end of printPerformanceList
		
		
		
		//받은 리스트에 있는 공연 목록 출력
		public static void printPerformance(ArrayList<Performance> list) {
			System.out.println("공연 목록: ");
			System.out.println("============================================================================================");
			System.out.println("공연ID\t\t  장르\t가격\t공연일\t    시청연령\t잔여좌석\t공연 장소\t\t공연명");
			if (list.size() == 0) {
				System.out.println("등록된 공연이 없습니다..");
			} else {
				for (int i = 0; i < list.size(); i++) {
					System.out.print(list.get(i).getPerformanceID() + "  |");
					System.out.print(list.get(i).getGenre() + "\t|");
					System.out.print(list.get(i).getTicketPrice() + "\t|");
					System.out.print(list.get(i).getDayOfPerformance() + " |");
					if (list.get(i).getLimitAge() < 10) {
						System.out.print(" " + list.get(i).getLimitAge() + "\t\t|");
					} else {
						System.out.print(list.get(i).getLimitAge() + "\t\t|");
					}
					System.out.print((list.get(i).getTotalSeats() - list.get(i).getSoldSeats()) + "/"
							+ list.get(i).getTotalSeats() + "\t|");
					System.out.print(list.get(i).getVenue() + "\t|");
					System.out.print(list.get(i).getPerformanceName());
					System.out.println("\t");
				}
			}//end of else if
		}
		
		//현재 해당하는 공연의 좌석 상태 출력
		public static void printSeats(ArrayList<Performance> list,String performanceId) {
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getPerformanceID().equals(performanceId)) {
					//2차원 배열 출력
					System.out.print("  ");
					for(int j=0;j<list.get(i).getXseats();j++) {
						System.out.print((char)(j+65)+" ");
					}
					System.out.println();//줄 바꿈
					for(int k=0;k<list.get(i).getYseats();k++) {
						System.out.print(k+1+" ");
						for(int j=0;j<list.get(i).getXseats();j++) {
							System.out.print(list.get(i).getSeats()[k][j]+" ");
						}
						System.out.println();//줄 바꿈
					}
				}
			}
		}//end of printSeats

}
