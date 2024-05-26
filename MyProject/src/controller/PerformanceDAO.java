package controller;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.Admin;
import model.Performance;

public class PerformanceDAO {

	// db에 좌석 선점 상태 돌려서 update
	public static void updatePerformance(String performanceID, String seats) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL PERFORMANCETBL_UPDATE_SEATS(?,?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, seats);
			cstmt.setString(2, performanceID);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(performanceID + "좌석 선점 정보 업데이트 완료");
			} else {
				System.out.println(performanceID + "좌석 선점 정보 업데이트 실패");
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
		setPerformanceToList();

	}
	//좌석 판매 수 업데이트
	public static void updatePerformance(String performanceId, int soldSeats) {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL PERFORMANCETBL_UPDATE_SOLD(?,?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setInt(1, soldSeats);
			cstmt.setString(2, performanceId);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(performanceId + "좌석 판매 정보 업데이트 완료");
			} else {
				System.out.println(performanceId + "좌석 판매 정보 업데이트 실패");
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
		setPerformanceToList();
	}

	// 공연 추가
	public static void addPerformance() {
		boolean nameFlag = false;
		boolean ageFlag = false;
		boolean seatFlag = false;
		boolean priceFlag = false;
		boolean genreFlag = false;
		boolean yFlag = false;
		if (Admin.adminLogin = true) {
//				String[] writePerformance = new String[Performance.PERIFONUM];
			System.out.println("공연 정보를 추가하겠습니까? Y|N");
			String str = main.Main.sc.nextLine();

			if (str.toUpperCase().equals("Y")) {
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("YYMMDDHHmmss");
				String strDate = formatter.format(date);
				// 공연 id는 추가하는 시간(초단위까지)일치 하지 않으면 중복되지 않음
				String performanceID = "pID" + strDate;
				System.out.println("공연ID: " + performanceID);
				String performanceName = null;
				while (!nameFlag) {
					System.out.print("공연명 : ");
					performanceName = main.Main.sc.nextLine();

					if (performanceName.length() == 0) {// 숫자를 한번도 입력하지 않으면
						System.out.println("공연명을 입력해주세요");
						continue;
					} else if (performanceName.length() > 20) {// db글자수 맞춰서
						System.out.println("공연명은 20자 이내로 적어주세요.");
					}
					nameFlag = true;
				}

				// ---------------------------------------------------------------------------------------------------------
				String genre = null;
				while (!genreFlag) {
					System.out.println("[뮤지컬, 연극, 콘서트]");
					System.out.print("장르 :");
					String input = main.Main.sc.nextLine();

					if (!(input.equals("뮤지컬") || input.equals("연극") || input.equals("콘서트"))) {// 장르 바르게입력하지 않으면
						System.out.println("장르를 바르게 입력해주세요.");
						continue;
					}
					genre = input;
					genreFlag = true;
				}
				// ---------------------------------------------------------------------------------------------------------
				System.out.print("공연일 예)2024-01-01 :");
				String dayOfPerformance = main.Main.sc.nextLine();
				System.out.print("공연 장소: ");
				String venue = main.Main.sc.nextLine();
				// ---------------------------------------------------------------------------------------------------------
				int limitAge = 0;
				while (!ageFlag) {
					System.out.print("관람제한연령 (숫자 만): ");
					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "");// 숫자 이외 공백 처리

					if (input.length() == 0) {// 숫자를 한번도 입력하지 않으면
						input = "0";// null 방지 (사실 필요 없으니 보험삼아)
						System.out.println("숫자만 입력해주세요.");
						continue;
					}
					limitAge = Integer.parseInt(input);
					ageFlag = true;
				}
				// ---------------------------------------------------------------------------------------------------------
				int totalSeats = 0;
				while (!seatFlag) {
					System.out.print("총 좌석수 (숫자만): ");
					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "");// 숫자 이외 공백 처리

					if (input.length() == 0) {// 숫자를 한번도 입력하지 않으면
						input = "0";
						System.out.println("숫자만 입력해주세요.");
						continue;
					}
					if (input.equals("0")) {
						System.out.println("0석 미만은 입력이 불가합니다.");
						continue;
					} else {
						totalSeats = Integer.parseInt(input);
						seatFlag = true;
					}
				}
				// ---------------------------------------------------------------------------------------------------------
				int soldSeats = 0; // 공연 추가시 판매좌석수 무조건 0으로
				// ---------------------------------------------------------------------------------------------------------
				int ticketPrice = 0;
				while (!priceFlag) {
					System.out.print("티켓 가격 (숫자만): ");
					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "");// 숫자 이외 공백 처리

					if (input.length() == 0) {// 숫자를 한번도 입력하지 않으면
						input = "0";
						System.out.println("숫자만 입력해주세요.");
						continue;
					}
					if (input.equals("0")) {
						System.out.println("0원 미만은 입력이 불가합니다.");
						continue;
					} else {
						ticketPrice = Integer.parseInt(input);
						priceFlag = true;
					}
				}
				// ---------------------------------------------------------------------------------------------------------
				int yseats = 0;
				while (!yFlag) {
					System.out.print("공연관 좌석 열(숫자만): ");
					String input = main.Main.sc.nextLine().replaceAll("[^0-9]", "");// 숫자 이외 공백 처리

					if (input.length() == 0) {// 숫자를 한번도 입력하지 않으면
						input = "0";
						System.out.println("숫자만 입력해주세요.");
						continue;
					}
					if (input.equals("0")) {
						System.out.println("0열 미만은 입력이 불가합니다.");
						continue;
					} else {
						yseats = Integer.parseInt(input);
						yFlag = true;
					}
				}
				// ---------------------------------------------------------------------------------------------------------
				int unable = 0;// 앉을 수 없는 좌석 수
				int xseats = 0;
				// 나머지가 있으면 한칸 늘려주기
				if (totalSeats % yseats > 0) {
					xseats = (int) (totalSeats / yseats) + 1;
					unable = xseats - totalSeats % yseats;
				} else {
					xseats = (int) (totalSeats / yseats);
				}
				// ---------------------------------------------------------------------------------------------------------
				StringBuffer seat = new StringBuffer(); // 수정 횟수가 많으므로 String 대신 StringBuffer 사용
				for (int i = 0; i < totalSeats; i++) {
					seat.append("□");// 총 좌석수 만큼 빈자리 만들어준다.
				}
				for (int i = 0; i < unable; i++) {
					seat.append("x");// 앉을 수 없는 자리는 x로 표시
				}
				String seats = seat.toString();
				// ---------------------------------------------------------------------------------------------------------
				String sql="CALL performancetbl_INSERT(?,?,?,?,?,?,?,?,?,?,?,?)";
;
				Connection con = null;
				CallableStatement cstmt = null;

				try {
					con = controller.DBUtil.makeConnection();
					cstmt = con.prepareCall(sql);
					cstmt.setString(1, performanceID);
					cstmt.setString(2,performanceName );
					cstmt.setString(3,genre );
					cstmt.setString(4,dayOfPerformance );
					cstmt.setString(5,venue );
					cstmt.setInt(6,limitAge );
					cstmt.setInt(7,totalSeats );
					cstmt.setInt(8,0 );
					cstmt.setInt(9,ticketPrice );
					cstmt.setString(10,seats );
					cstmt.setInt(11, yseats);
					cstmt.setInt(12,xseats );

					int value = cstmt.executeUpdate();
					if (value == 0) {
						System.out.println(performanceName + " 등록완료");
					} else {
						System.out.println(performanceName + " 등록 실패");
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
				Admin.performanceList.clear();// 변경 사항 있으므로 초기화
				setPerformanceToList();// 다시 저장

			} else {
				System.out.println("공연을 추가하지 않습니다.");
			}
		} else {
			System.out.println("관리자 로그인이 필요합니다.");
		}

	}

	// 공연 삭제
	public static void deletePerformance() {
		// 파일->리스트
		System.out.println(
				"============================================================================================");
		System.out.println("현재 공연 목록");
		System.out.println(
				"--------------------------------------------------------------------------------------------");
		// 현재 존재하는 공연(요약본)출력
		PerformanceManager.printPerformanceList(Admin.performanceList);

		if (getCountPerformance() == 0) {
			System.out.println("저장된 공연이 없습니다.");
			return;
		}

		boolean quit = false;
		String inputID = null;

		while (!quit) {
			System.out.print("삭제할 공연의 ID를 입력하세요 (취소시 '취소' 입력):");
			inputID = main.Main.sc.nextLine();

			if (inputID.equals("취소")) {
				return;
			}
			// 공연 아이디로 검색
			int performancecheck = searchPerformanceID(inputID);

			if (performancecheck == 1) {
				System.out.println("해당 공연을 찾았습니다.");
				quit = true;
			} else {
				System.out.println("해당 공연을 찾지 못했습니다.");
			}
		} // end of while

		Connection con = null;
		CallableStatement cstmt = null;
		try {
			String sql = "CALL PERFORMANCETBL_DELETE(?)";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.setString(1, inputID);
			int value = cstmt.executeUpdate();
			if (value == 0) {
				System.out.println(inputID + "공연 삭제 완료");
			} else {
				System.out.println(inputID + "공연 삭제 실패");
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
		setPerformanceToList();

	}
	
	public static int getCountPerformance() {
		int cnt = 0;
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		try {
			String sql = "{CALL PERFORMANCETBL_TOTALCOUNT(?)}";
			con = controller.DBUtil.makeConnection();
			cstmt = con.prepareCall(sql);
			cstmt.registerOutParameter(1, Types.INTEGER);

			cstmt.executeQuery();
			cnt = cstmt.getInt(1);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
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
		return cnt;
	}
	
	// 아이디로 검색
		public static int searchPerformanceID(String inputID) {
			int cnt = 0;
			Connection con = null;
			CallableStatement cstmt = null;
			ResultSet rs = null;
			try {
				String sql = "{CALL performancetbl_count(?,?)}";
				con = controller.DBUtil.makeConnection();
				cstmt = con.prepareCall(sql);
				cstmt.setString(1, inputID);
				cstmt.registerOutParameter(2, Types.INTEGER);

				cstmt.executeQuery();
				cnt = cstmt.getInt(2);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
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
			return cnt;
		}
		
		// DB에 저장된 내용 리스트로...
		public static void setPerformanceToList() {
			Admin.performanceList.clear();
			String sql = "SELECT * FROM performancetbl";
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				con = controller.DBUtil.makeConnection();
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					Performance p = new Performance();
					p.setPerformanceID(rs.getString("performanceid"));
					p.setPerformanceName(rs.getString("performancename"));
					p.setGenre(rs.getString("genre"));
					p.setDayOfPerformance(rs.getString("dayofperformance"));
					p.setVenue(rs.getString("venue"));
					p.setLimitAge(rs.getInt("limitage"));
					p.setTotalSeats(rs.getInt("totalseats"));
					p.setSoldSeats(rs.getInt("soldseats"));
					p.setTicketPrice(rs.getInt("ticketprice"));
					p.setYseats(rs.getInt("yseats"));
					p.setXseats(rs.getInt("xseats"));

					String[][] setSeat;// 좌석 2차원 배열로
					String[] seatsSplit = new String[rs.getString("seats").length()];// 여기에 끊어서 하나씩 넣어줄거임(총 좌석수로 크기 잡음)
					seatsSplit = rs.getString("seats").split("");// 한줄로 된거 하나씩 끊음
					setSeat = new String[rs.getInt("yseats")][rs.getInt("xseats")];
					int index = 0;
					for (int j = 0; j < rs.getInt("yseats"); j++) {
						for (int k = 0; k < rs.getInt("xseats"); k++) {
							setSeat[j][k] = seatsSplit[index];
							index++;
						}
					}
					p.setSeats(setSeat);

					Admin.performanceList.add(p);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (pstmt != null) {
						pstmt.close();
					}
					if (con != null) {
						con.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}// end of setPerformanceToList

}
