package Network;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class ChatDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	public String code;
	public String id;
	public String msg;
	public ImageIcon icon;

	public ChatDTO(String id, String code, String msg) {
		this.code = code;
		this.id = id;
		this.msg = msg;
	}

	public ChatDTO(String id, String code, ImageIcon icon) {
		this.id = id;
		this.code = code;
		this.icon = icon;
	}

}

// LOGIN:�α���, LOGOUT:�α׾ƿ�, CHAT:ä�ø޽���, IMAGE:Image