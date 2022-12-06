package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
import Start.StartPanel;

public class GamePanel extends JPanel {
	private ImageIcon bodyBackgroundImg = new ImageIcon("./src/img/body_background.jpg");

	private MainFrame mainFrame;
	private RoomPanel roomPanel;
	public DrawingPanel drawingPanel;

	private JTextPane textArea;
	public JScrollPane chatScrollPane;
	private JTextField chatTextField;
	private JButton readyBtn;
//	private ArrayList<String> wordArr;

	private String id;
	private boolean isReady = true;
	public String word;

	// �׸���
	private String color;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	public JLabel wordLabel = new JLabel("");

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
//		wordArr = roomPanel.getWordsList();

		setBackground(new Color(48, 106, 169));
		setLayout(null);

		JLabel lblNewLabel = new JLabel("Catch");
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.BOLD, 42));
		lblNewLabel.setBounds(12, 10, 128, 27);
		lblNewLabel.setHorizontalAlignment(JLabel.CENTER);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Mind");
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setFont(new Font("Tw Cen MT Condensed Extra Bold", Font.BOLD, 42));
		lblNewLabel_1.setBounds(12, 47, 128, 26);
		lblNewLabel_1.setHorizontalAlignment(JLabel.CENTER);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel();
		lblNewLabel_2.setBounds(0, 83, 1040, 13);
		lblNewLabel_2.setIcon(new ImageIcon(StartPanel.class.getResource("/img/background_line.png")));
		add(lblNewLabel_2);

		// �׸� �׸��� �г�
		drawingPanel = new DrawingPanel(mainFrame, this);
		add(drawingPanel);
		add(drawingPanel.paintPanel);

		// �÷��̾� ���� �г�

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

		JButton exitBtn = new JButton();
		exitBtn.setBounds(927, 51, 79, 32);
		exitBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/goToRoomBtn.png")));
		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(exitBtn);

		JLabel roomNameLabel = new JLabel();
		roomNameLabel.setForeground(new Color(255, 255, 255));
		roomNameLabel.setText("Room Name");
		roomNameLabel.setFont(new Font("�޸�����ü", Font.BOLD, 40));
		roomNameLabel.setBounds(332, 10, 375, 60);
		roomNameLabel.setHorizontalAlignment(JLabel.CENTER);
		add(roomNameLabel);

		readyBtn = new JButton("�غ�");
		readyBtn.setBackground(Color.WHITE);
		readyBtn.setBounds(869, 708, 107, 43);
		add(readyBtn);
		wordLabel.setForeground(new Color(255, 255, 255));

		wordLabel.setBounds(47, 621, 127, 74);
		wordLabel.setFont(new Font("�޸�����ü", Font.BOLD, 40));
		wordLabel.setHorizontalAlignment(JLabel.CENTER);
		add(wordLabel);

		readyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendReady(isReady);
				if (!isReady)
					readyBtn.setText("���");
				else
					readyBtn.setText("�غ�");
			}
		});

		exitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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

	// ������ �غ���� ����
	public void sendReady(boolean isReady) {
		GameDataDTO gameDataDTO = new GameDataDTO(id, "READY", isReady);
		sendObject(gameDataDTO);
		this.isReady = !isReady;
	}

	// textArea�� ���
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

	// ������ Object ����
	public void sendObject(Object obj) {
		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void changeReadyBtn() {
		readyBtn.setVisible(false);
	}

	public void sendMessage(String msg) {
		ChatDTO chatDTO = new ChatDTO(id, "GAMECHAT", msg);
		sendObject(chatDTO);
	}

	public void sendWord(String word) {
		GameDataDTO gameDataDTO = new GameDataDTO(id, "ANSWER", word);
		sendObject(gameDataDTO);
	}

	public void setWord(String word) {
		this.word = word;
		wordLabel.setText(word);
	}

	public void sendEnd() {
		GameDataDTO gameDataDTO = new GameDataDTO("CLIENT", "END", "End game");
		sendObject(gameDataDTO);
	}

	// keyboard enter key ġ�� ������ ����
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == chatTextField) {
				String msg = null;
				msg = chatTextField.getText();
				sendMessage(msg); // ������ ����
				sendWord(msg.trim());
				chatTextField.setText("");
				chatTextField.requestFocus();
			}
		}
	}
}
