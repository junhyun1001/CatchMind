package Network;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class DrawDTO implements Serializable {
	private static final long serialVersionUID = 2L;

	public String id;
	public String code;
	public String msg;

	public MouseEvent mouse_e;
	public int pen_size; // pen size

	public Color color;

	public String shape;
	public int x1, y1, x2, y2;

	public ImageIcon img;

	public DrawDTO(String id, String code, String msg) {
		this.id = id;
		this.code = code;
		this.msg = msg;
	}

}

// DRAW: Mouse Event
