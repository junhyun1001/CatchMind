package Network;

import java.util.Vector;

public class GameRoom {
	private static final long serialVersionUID = 4L;
	public int roomId; // 방 고유번호
	public int maxUser = 4; // 최대 방 인원 수
	private int user_count;
	private Vector<String> roomUserVec = new Vector<>(); // room user list
	private String roomOwner;
	private String roomName;

	public GameRoom(String roomName, String user) {
		this.roomName = roomName;
		this.user_count = 0;
		this.roomOwner = user;
	}

	public int enterRoom(String username) {
		if (maxUser > user_count) {
			this.roomUserVec.add(username);
			this.user_count++;
			return this.user_count;
		} else
			return -1; // 입장실패시 -1 리턴
	}

	public int exitRoom(String username) { // String -> UserService
		this.roomUserVec.remove(username);
		this.user_count--;
		return this.user_count;
	}

	public String getRoomName() {
		return this.roomName;
	}

	public int getRoomUserCount() {
		return this.user_count;
	}

	public int getRoomId() {
		return this.roomId;
	}
}