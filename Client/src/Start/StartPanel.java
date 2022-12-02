package Start;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Network.ChatDTO;
import Panel.RoomPanel;

public class StartPanel extends JPanel {
	private MainFrame mainFrame;
	private JTextField idTextField;
	private ImageIcon startBackgroundImg = new ImageIcon("./src/img/body_background.jpg");
	private JTextField ipTextField;
	private JTextField portTextField;

	private String id;

	private Socket socket;
	private String ip;
	private String port;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public void paintComponent(Graphics g) {
		g.drawImage(startBackgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}

	public StartPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setLayout(null);

		JLabel ipLabel = new JLabel("Port:");
		ipLabel.setFont(new Font("휴먼편지체", Font.BOLD, 20));
		ipLabel.setBounds(419, 457, 71, 33);
		add(ipLabel);

		ipTextField = new JTextField("127.0.0.1");
		ipTextField.setFont(new Font("휴먼편지체", Font.BOLD, 15));
		ipTextField.setColumns(10);
		ipTextField.setBounds(503, 414, 116, 33);
		add(ipTextField);

		JLabel portLabel = new JLabel("IP:");
		portLabel.setFont(new Font("휴먼편지체", Font.BOLD, 20));
		portLabel.setBounds(419, 414, 71, 33);
		add(portLabel);

		portTextField = new JTextField("30000");
		portTextField.setFont(new Font("휴먼편지체", Font.BOLD, 15));
		portTextField.setColumns(10);
		portTextField.setBounds(503, 457, 116, 33);
		add(portTextField);

		// id라벨
		JLabel idLabel = new JLabel("ID:");
		idLabel.setFont(new Font("휴먼편지체", Font.BOLD, 20));
		idLabel.setBounds(419, 500, 71, 33);
		add(idLabel);

		// id입력 field
		idTextField = new JTextField();
		idTextField.setFont(new Font("휴먼편지체", Font.BOLD, 15));
		idTextField.setBounds(503, 500, 116, 33);
		add(idTextField);
		idTextField.setColumns(10);

		ConnectAction connectListener = new ConnectAction();
		JButton startBtn = new JButton("");
		startBtn.setFont(new Font("휴먼편지체", Font.BOLD, 25));
		startBtn.setBounds(378, 199, 283, 205);
		startBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/game_start.png")));
		startBtn.setBorderPainted(false); // 버튼의 외곽선을 없애줌
		startBtn.setContentAreaFilled(false); // 버튼의 내용영역 채우기 안함
		startBtn.setFocusPainted(false); // 버튼이 선택 되었을 때 생기는 테두리 사용 안함
		add(startBtn);
		startBtn.addActionListener(connectListener);
	}

	class ConnectAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ip = ipTextField.getText().trim();
			port = portTextField.getText().trim();
			id = idTextField.getText();
			mainFrame.setId(id);
			connectServer();
			makeStream();
			mainFrame.setSocket(socket);
			mainFrame.changePanel("CharacterSelectPanel");
		}
	}

	// 서버 접속 연결 메소드
	private void connectServer() {
		try {
			socket = new Socket(ip, Integer.parseInt(port));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void makeStream() {
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			mainFrame.setOOS(oos);
			mainFrame.setOIS(ois);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}