package Start;

import java.awt.Toolkit;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;

import Panel.CharacterSelectPanel;
import Panel.GamePanel;
import Panel.RoomPanel;
import Thread.ListenNetwork;

public class MainFrame extends JFrame {
	private StartPanel startPanel = new StartPanel(this);
	private CharacterSelectPanel characterSelectPanel;
	private RoomPanel roomPanel;
	public GamePanel gamePanel;

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private String id;
	private String roomName;
//	private ArrayList<String> userArr;

	public MainFrame() {
		setTitle("캐치마인드");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1040, 800);
		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(Toolkit.getDefaultToolkit().getImage(StartPanel.class.getResource("/img/icon.jpg")));
		setVisible(true);
		add(startPanel);

	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

//	public ArrayList<String> getUserArr() {
//		return userArr;
//	}

//	public void setCharacter(Icon icon) {
//		this.icon = icon;
//	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setOOS(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public void setOIS(ObjectInputStream ois) {
		this.ois = ois;
	}

	public ObjectOutputStream getOOS() {
		return oos;
	}

	public ObjectInputStream getOIS() {
		return ois;
	}

	public void changePanel(String panelName) {
		if (panelName.equals("CharacterSelectPanel")) {
			characterSelectPanel = new CharacterSelectPanel(this);
			getContentPane().removeAll();
			getContentPane().add(characterSelectPanel);
			revalidate();
			repaint();
		} else if (panelName.equals("RoomPanel")) {
			roomPanel = new RoomPanel(this);
			getContentPane().removeAll();
			getContentPane().add(roomPanel);
			revalidate();
			repaint();
		} else if (panelName.equals("GamePanel")) {
			gamePanel = new GamePanel(this, roomPanel);
			getContentPane().removeAll();
			getContentPane().add(gamePanel);
			revalidate();
			repaint();
		}
	}

}
