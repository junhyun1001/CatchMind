package Network;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class GameDataDTO implements Serializable {
	private static final long serialVersionUID = 3L;

	public String id;
	public String code;
	public String data;
	public ImageIcon icon;

	public String roomName;
	public int roomId;

	public int score = 0;
	public boolean boolData;
	
	public GameDataDTO(String id, String code, int score) {
		this.id = id;
		this.code = code;
		this.score = score;
	}

	public GameDataDTO(String id, String code, String data) {
		this.id = id;
		this.code = code;
		this.data = data;
	}
	
	public GameDataDTO(String id, String code, boolean boolData) {
		this.id = id;
		this.code = code;
		this.boolData = boolData;
	}
	
	public GameDataDTO(String id, String code, ImageIcon icon) {
		this.id = id;
		this.code = code;
		this.icon = icon;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getRoomName() {
		return this.roomName;
	}

	public void setRoom_id(int roomId) {
		this.roomId = roomId;
	}

	public int getRoom_id() {
		return this.roomId;
	}

}
