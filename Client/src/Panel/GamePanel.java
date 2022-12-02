package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import Network.ChatDTO;
import Network.GameDataDTO;
import Start.MainFrame;

public class GamePanel extends JPanel {
	private ImageIcon bodyBackgroundImg = new ImageIcon("./src/img/body_background.jpg");

	private MainFrame mainFrame;
	private RoomPanel roomPanel;
	public DrawingPanel drawingPanel;

//	public PlayerInfo p1 = new PlayerInfo();
//	public PlayerInfo p2 = new PlayerInfo();
//	public PlayerInfo p3 = new PlayerInfo();
//	public PlayerInfo p4 = new PlayerInfo();

	private JTextPane textArea;
	public JScrollPane chatScrollPane;
	private JTextField chatTextField;
	private JButton readyBtn;
	private ArrayList<String> wordArr;

	private String id;
	private boolean isReady = true;

	// 그림판
	private String color;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private final JLabel wordLabel = new JLabel("제시어");

	private int count = 0;

	public void paintComponent(Graphics g) {
		g.drawImage(bodyBackgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	};

	public GamePanel(MainFrame mainFrame, RoomPanel roomPanel) {
		this.mainFrame = mainFrame;
		this.roomPanel = roomPanel;
		this.id = mainFrame.getId();
		this.ois = mainFrame.getOIS();
		this.oos = mainFrame.getOOS();
		this.roomPanel = roomPanel;
		wordArr = roomPanel.getWordsList();

		setBackground(new Color(48, 106, 169));
		setLayout(null);

		// 그림 그리는 패널
		drawingPanel = new DrawingPanel(mainFrame, this);
		add(drawingPanel);
		add(drawingPanel.paintPanel);

		// 플레이어 정보 패널

		roomPanel.p1.setBounds(8, 158, 230, 115);
		roomPanel.p2.setBounds(8, 344, 230, 115);
		roomPanel.p3.setBounds(789, 158, 230, 115);
		roomPanel.p4.setBounds(789, 344, 230, 115);

		add(roomPanel.p1);
		add(roomPanel.p2);
		add(roomPanel.p3);
		add(roomPanel.p4);

		chatScrollPane = new JScrollPane();
		chatScrollPane.setBounds(250, 567, 527, 131);
		add(chatScrollPane);

		textArea = new JTextPane();
		textArea.setEditable(false);
		chatScrollPane.setViewportView(textArea);

		chatTextField = new JTextField();
		chatTextField.setBounds(250, 708, 527, 43);
		add(chatTextField);
		chatTextField.setColumns(10);
		chatTextField.addActionListener(new TextSendAction());

		JButton exitBtn = new JButton("나가기");
		exitBtn.setBounds(34, 28, 155, 67);
		add(exitBtn);

		JLabel roomNameLabel = new JLabel();
		roomNameLabel.setText("Room Name");
		roomNameLabel.setFont(new Font("휴먼편지체", Font.BOLD, 40));
		roomNameLabel.setBounds(250, 27, 527, 90);
		roomNameLabel.setHorizontalAlignment(JLabel.CENTER);
		add(roomNameLabel);

		readyBtn = new JButton("준비");
		readyBtn.setBounds(869, 708, 107, 43);
		add(readyBtn);

		wordLabel.setBounds(47, 621, 127, 74);
		add(wordLabel);

		wordLabel.setText(wordArr.get(0));

		readyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendReady(isReady);
				if (!isReady)
					readyBtn.setText("취소");
				else
					readyBtn.setText("준비");
			}
		});

		exitBtn.addActionListener(new ActionListener() {

	         @Override
	         public void actionPerformed(ActionEvent e) {
//	            mainFrame.changePanel("RoomPanel");
	            GamePanel.this.removeAll();
	            GamePanel.this.setVisible(false);
	            
	            roomPanel.revalidate();
	            roomPanel.repaint();
	            getParent().add(roomPanel);
	            roomPanel.setVisible(true);
	            
	            GameDataDTO gameDataDTO = new GameDataDTO(id, "EXITROOM", roomPanel.getRoomId());
	               sendObject(gameDataDTO);
	         }

	      });
		roomNameLabel.setText(RoomPanel.getRoomName());
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(color)) {

		}
	}
	
	// 서버로 준비상태 전송
	public void sendReady(boolean isReady) {
		GameDataDTO gameDataDTO = new GameDataDTO(id, "READY", isReady);
		sendObject(gameDataDTO);
		this.isReady = !isReady;
	}

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

	// 서버로 Object 전송
	public void sendObject(Object obj) {
		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 서버에 점수 보내고 설정해주기
	public void setScore(GameDataDTO gameDataDTO) {

	}
	
	// 서버에 차례 넘기도록 요청
	public void sendNext() {
		GameDataDTO gameDataDTO = new GameDataDTO(id, "NEXT", "");
		sendObject(gameDataDTO);
	}
	
	public void changeReadyBtn() {
		readyBtn.setVisible(false);
	}

	public void sendMessage(String msg) {
		ChatDTO chatDTO = new ChatDTO(id, "GAMECHAT", msg);
		sendObject(chatDTO);
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
}
