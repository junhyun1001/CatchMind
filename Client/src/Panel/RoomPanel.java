package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Network.ChatDTO;
import Network.GameDataDTO;
import Start.MainFrame;
import Start.StartPanel;
import Thread.ListenNetwork;

public class RoomPanel extends JPanel {
	private ImageIcon bodyBackgroundImg = new ImageIcon("./src/img/body_background.jpg");

	private MainFrame mainFrame;

	public PlayerInfo p1;
	public PlayerInfo p2;
	public PlayerInfo p3;
	public PlayerInfo p4;

	public String playerList[];

	private JTextPane textArea;
	private JTextField chatTextField;
	private JTextPane userListArea;
	public JLabel characterLabel;

	private DefaultTableModel roomModel; // JList 데이터
	private JTable roomTable;

	private ArrayList<String> userArr; // 현재 접속한 유저 목록을 저장
	private ArrayList<ImageIcon> iconArr;
	private ArrayList<String> wordArr; // RoomPanel이 만들어질 때 단어 목록을 서버에서 가져옴

	private String id;
	public ImageIcon icon;
	public JLabel userNameLabel;

	// 네트워크 및 전송 오브젝트
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	static String roomName;
	static String roomId;

	String roomData = null;
	String roomList[];

	public void paintComponent(Graphics g) {
		g.drawImage(bodyBackgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	};

	public RoomPanel(MainFrame mainFrame) {
		socket = mainFrame.getSocket();
		id = mainFrame.getId();
		ois = mainFrame.getOIS();
		oos = mainFrame.getOOS();
//		userArr = mainFrame.getUserArr();

		p1 = new PlayerInfo();
		p2 = new PlayerInfo();
		p3 = new PlayerInfo();
		p4 = new PlayerInfo();

		setBounds(0, 0, 1040, 800);
		setLayout(null);
		setBackground(new Color(255, 255, 255));

		setBounds(0, 0, 1040, 800);
		setLayout(null);
		setBackground(new Color(235, 246, 254));

//		JPanel menuPanel = new JPanel();
//		menuPanel.setBounds(34, 10, 971, 83);
//		add(menuPanel);
//		menuPanel.setLayout(null);
//		menuPanel.setBorder(new LineBorder(Color.black, 1));
//		menuPanel.setBackground(new Color(235, 246, 254));

		JButton makeRoomBtn = new JButton();
		makeRoomBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/makeRoomBtn.png")));
		makeRoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String createRoomName = JOptionPane.showInputDialog(RoomPanel.this, "방 이름", "방 만들기", 3);
				if (createRoomName != null) {
					GameDataDTO gameDataDTO = new GameDataDTO(id, "MAKEROOM", "");
					gameDataDTO.roomName = createRoomName;
					sendObject(gameDataDTO);
				}
			}
		});
		makeRoomBtn.setBounds(671, 51, 79, 32);
		add(makeRoomBtn);

		JLabel lblNewLabel = new JLabel("Catch");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.BOLD, 42));
		lblNewLabel.setBounds(456, 10, 128, 27);
		lblNewLabel.setHorizontalAlignment(JLabel.CENTER);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Mind");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.BOLD, 42));
		lblNewLabel_1.setBounds(456, 47, 128, 26);
		lblNewLabel_1.setHorizontalAlignment(JLabel.CENTER);
		add(lblNewLabel_1);

		// -------------- 대기방 리스트 테이블 ------------- //

		String[] roomField = { "방 번호", "방이름", "참가인원", "최대인원" };
		Object[][] rooms = new Object[][] { {} };

		roomModel = new DefaultTableModel(rooms, roomField) {
			@Override
			public boolean isCellEditable(int row, int column) { // 수정, 입력 불가
				return false;
			}
		};
		roomTable = new JTable(roomModel);
		roomTable.setRowHeight(40);
		roomTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		roomTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		roomTable.getColumnModel().getColumn(2).setPreferredWidth(20);
		roomTable.getColumnModel().getColumn(3).setPreferredWidth(20);
		roomTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane roomScroll = new JScrollPane(roomTable);
		roomScroll.setBounds(34, 132, 716, 327);
		add(roomScroll);

		roomTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String data = roomList[roomTable.getSelectedRow()];
					String data2[] = data.split(" ");
					roomId = data2[0];
					roomName = data2[1];
					GameDataDTO gameDataDTO = new GameDataDTO(id, "ENTERROOM", roomId);
					sendObject(gameDataDTO);
					// 방에 입장하는 플레이어 정보를 서버에 넘겨줌
					RoomPanel.this.setVisible(false);
					mainFrame.changePanel("GamePanel");
				}
			}
		});

		// -------------- 대기방 유저 리스트 테이블 ------------- //

		JScrollPane userScrollPane = new JScrollPane();
		userScrollPane.setBounds(793, 132, 212, 327);
		add(userScrollPane);

		userListArea = new JTextPane();
		userListArea.setEditable(false);
		userScrollPane.setViewportView(userListArea);

		JLabel roomListLabel = new JLabel("방 목록");
		roomListLabel.setForeground(new Color(255, 255, 255));
		roomListLabel.setFont(new Font("휴먼편지체", Font.BOLD, 18));
		roomListLabel.setBounds(34, 103, 80, 20);
		add(roomListLabel);

		JLabel chatListLabel = new JLabel("채팅");
		chatListLabel.setForeground(new Color(255, 255, 255));
		chatListLabel.setFont(new Font("휴먼편지체", Font.BOLD, 18));
		chatListLabel.setBounds(34, 469, 80, 20);
		add(chatListLabel);

		JLabel profileLabel = new JLabel("내 프로필");
		profileLabel.setForeground(new Color(255, 255, 255));
		profileLabel.setFont(new Font("휴먼편지체", Font.BOLD, 18));
		profileLabel.setBounds(793, 464, 80, 20);
		add(profileLabel);

		JLabel userListLabel = new JLabel("유저 목록");
		userListLabel.setForeground(new Color(255, 255, 255));
		userListLabel.setFont(new Font("휴먼편지체", Font.BOLD, 18));
		userListLabel.setBounds(793, 103, 80, 20);
		add(userListLabel);

		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane.setBounds(34, 494, 716, 179);
		add(chatScrollPane);

		textArea = new JTextPane();
		textArea.setEditable(false);
		chatScrollPane.setViewportView(textArea);

		chatTextField = new JTextField();
		chatTextField.setBounds(34, 683, 716, 40);
		add(chatTextField);
		chatTextField.setColumns(10);

		// 네트워크 수신 스레드 작동
		ListenNetwork net = new ListenNetwork(mainFrame, this);
		net.start();

		JLabel lblNewLabel_2 = new JLabel();
		lblNewLabel_2.setBounds(0, 83, 1040, 13);
		lblNewLabel_2.setIcon(new ImageIcon(StartPanel.class.getResource("/img/background_line.png")));
		add(lblNewLabel_2);

		userNameLabel = new JLabel("UserName");
		userNameLabel.setForeground(new Color(255, 255, 255));
		userNameLabel.setBounds(830, 666, 143, 27);
		add(userNameLabel);
		userNameLabel.setFont(new Font("휴먼편지체", Font.PLAIN, 18));
		userNameLabel.setHorizontalAlignment(JLabel.CENTER);

		characterLabel = new JLabel();
		characterLabel.setBounds(830, 528, 143, 128);
		add(characterLabel);
		characterLabel.setHorizontalAlignment(JLabel.CENTER);

		JButton exitBtn = new JButton();
		exitBtn.setBounds(927, 51, 79, 32);
		exitBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/exitBtn.png")));
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		add(exitBtn);

		TextSendAction chatAction = new TextSendAction();
		chatTextField.addActionListener(chatAction);
	}

	public void setRoomData(GameDataDTO gameDataDTO) {
		roomData = gameDataDTO.data; // 방 번호, 이름, 현재 인원, 총 인원이 담긴 문자열을 받아옴
	}

	public String getRoomId() {
		return roomId;
	}

	public void makeRow() {
		String tempRoomList[] = roomData.split("\\n");// 방정보 한줄씩 구분 \\n로 개행문자구분. 임시 배열사용
		// (row를 전부삭제하고 나서 다시 추가하는 과정 (update RoomList)
		if (tempRoomList != null) {
			roomModel.removeRow(0);
			for (int j = 0; j <= tempRoomList.length - 1; j++)
				roomModel.addRow(tempRoomList[j].split(" ")); // room에 대한 것
		}

		roomList = tempRoomList;
	}

	// 모든 플레이어가 준비 되면 WORD코드를 받고, WORD 코드를 받으면 단어를 저장시킴
//	public void saveWords(GameDataDTO gameDataDTO) {
//		String wordsList[];
//		wordsList = gameDataDTO.data.split(",");
//		wordArr = new ArrayList<>(Arrays.asList(wordsList));
//
//	}
//
//	public ArrayList<String> getWordsList() {
//		return wordArr;
//	}

	// textArea에 출력
	public void appendText(String msg) {
		msg = msg.trim();
		int len = textArea.getDocument().getLength();

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}

	public void appendUserList(String id) {
		id = id.trim();
		int len = userListArea.getDocument().getLength();

		StyledDocument doc = userListArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), id + "\n", left);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		len = userListArea.getDocument().getLength();
		userListArea.setCaretPosition(len);
	}

	// 서버로 Object 전송
	public void sendObject(Object obj) {
		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			appendText("Send Object Error");
		}
	}

	public static String getRoomName() {
		return roomName;
	}

	// 서버에 채팅 메세지를 보냄
	public void sendMessage(String msg) {
		ChatDTO obcm = new ChatDTO(id, "CHAT", msg);
		sendObject(obcm);
	}

	// userList를 cm.data에서 받아옴
	// userListArea를 지우고 다시 뿌려줌
	public void updateUserList(String users) {
		userListArea.setText("");

		String userList[];
		userList = users.split(",");
		userArr = new ArrayList<>(Arrays.asList(userList));
		for (int i = 0; i < userArr.size(); i++) {
			appendUserList(userArr.get(i));
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == chatTextField) {
				String msg = null;
				msg = chatTextField.getText();
				sendMessage(msg); // 서버로 전송
				chatTextField.setText("");
				chatTextField.requestFocus();
			}
		}
	}

	// 플레이어가 입장할 때 마다 서버에서 플레이어 정보를 받아와서 업데이트 시킴
	public void setPlayer(GameDataDTO gameDataDTO) {
		playerList = gameDataDTO.data.split(",");
		if (playerList.length == 1) {
			p1.idLabel.setText(playerList[0]);
		} else if (playerList.length == 2) {
			p1.idLabel.setText(playerList[0]);
			p2.idLabel.setText(playerList[1]);
		} else if (playerList.length == 3) {
			p1.idLabel.setText(playerList[0]);
			p2.idLabel.setText(playerList[1]);
			p3.idLabel.setText(playerList[2]);
		} else if (playerList.length == 4) {
			p1.idLabel.setText(playerList[0]);
			p2.idLabel.setText(playerList[1]);
			p3.idLabel.setText(playerList[2]);
			p4.idLabel.setText(playerList[3]);
		}
	}

	public void setIconVec(ImageIcon icon) {
//		iconArr = new ArrayList<>();
//		for(int i = 0; i < playerList.length; i++) {
//			iconArr.add(icon);
//		}

		p1.iconLabel.setIcon(icon);
//		else if (playerList.length == 3) {
//			p1.iconLabel.setIcon(iconArr.get(0));
//			p1.iconLabel.setIcon(iconArr.get(1));
//			p1.iconLabel.setIcon(iconArr.get(2));
//		} else if (playerList.length == 4) {
//			p1.iconLabel.setIcon(iconArr.get(0));
//			p1.iconLabel.setIcon(iconArr.get(1));
//			p1.iconLabel.setIcon(iconArr.get(2));
//			p1.iconLabel.setIcon(iconArr.get(3));
//		}
	}

	// 플레이어 점수 세팅
	public void setScore(GameDataDTO gameDataDTO) {
		if (p1.idLabel.getText().equals(gameDataDTO.id)) {
			p1.scoreLabel.setText("점수:" + gameDataDTO.score);
		} else if (p2.idLabel.getText().equals(gameDataDTO.id)) {
			p2.scoreLabel.setText("점수:" + gameDataDTO.score);
		}
	}
}