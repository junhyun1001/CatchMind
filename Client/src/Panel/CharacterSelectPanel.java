package Panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Network.ChatDTO;
import Start.MainFrame;
import Start.StartPanel;

public class CharacterSelectPanel extends JPanel {
	private MainFrame mainFrame;

	private ImageIcon bodyBackgroundImg = new ImageIcon("./src/img/body_background.jpg");
	private JLabel myCharacter;
	private JLabel idLabel;

	private String id;
	private ImageIcon icon;

	private Socket socket;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	// ĳ���� ��ư
	private JButton c1;
	private JButton c2;
	private JButton c3;
	private JButton c4;
	private JButton c5;
	private JButton c6;
	private JButton c7;
	private JButton c8;
	private JButton c9;
	private JButton c10;
	private JButton c11;
	private JButton c12;
	private JButton c13;
	private JButton c14;
	private JButton c15;
	private JButton c16;
	private JButton c17;

	// ��� �׸���
	public void paintComponent(Graphics g) {
		g.drawImage(bodyBackgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	};

	public Socket getSocket() {
		return socket;
	}

	public CharacterSelectPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.id = mainFrame.getId();
		this.ois = mainFrame.getOIS();
		this.oos = mainFrame.getOOS();

		setBounds(0, 0, 1040, 800);
		setLayout(null);

		generateCharacterBtn();
		
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
		
		JLabel lblNewLabel_2 = new JLabel();
		lblNewLabel_2.setBounds(0, 83, 1040, 13);
		lblNewLabel_2.setIcon(new ImageIcon(StartPanel.class.getResource("/img/background_line.png")));
		add(lblNewLabel_2);

		myCharacter = new JLabel();
		myCharacter.setBounds(349, 561, 150, 150);
		add(myCharacter);

		idLabel = new JLabel("ID: " + mainFrame.getId());
		idLabel.setForeground(new Color(255, 255, 255));
		idLabel.setFont(new Font("�޸�����ü", Font.BOLD, 40));
		idLabel.setBounds(511, 612, 284, 54);
		add(idLabel);

		JButton startBtn = new JButton("");
		startBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/startGameBtn.png")));
		startBtn.setBounds(869, 51, 79, 32);
		setFont(new Font("�޸�����ü", Font.BOLD, 25));
		add(startBtn);
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendIdIcon();
//				sendIcon();
				mainFrame.changePanel("RoomPanel");
			}
		});

		setVisible(true);

	}

	// ������ ID ����
	private void sendIdIcon() {
		ChatDTO chatDTO = new ChatDTO(id, "LOGIN", icon);
		sendObject(chatDTO);
	}

	public void sendObject(Object obj) {
		try {
			oos.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class CharacterBtnListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			myCharacter.setIcon(btn.getIcon()); // ������ ĳ���� Ȯ��
			icon = (ImageIcon) btn.getIcon(); // icon ������ ĳ���� ����
		}

	}

	public void generateCharacterBtn() {
		CharacterBtnListener listener = new CharacterBtnListener();
		// ĳ���� ����
		c1 = new JButton("");
		c1.setIcon(new ImageIcon(StartPanel.class.getResource("/character/��ġ����.gif")));
		c1.setBounds(74, 147, 87, 96);
		c1.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c1.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c1.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c1);

		c2 = new JButton("");
		c2.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�κ�����.gif")));
		c2.setBounds(235, 149, 81, 94);
		c2.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c2.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c2.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c2);

		c3 = new JButton("");
		c3.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�˱�.gif")));
		c3.setBounds(390, 159, 89, 84);
		c3.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c3.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c3.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c3);

		c4 = new JButton("");
		c4.setIcon(new ImageIcon(StartPanel.class.getResource("/character/����������.gif")));
		c4.setBounds(553, 143, 98, 100);
		c4.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c4.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c4.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c4);

		c5 = new JButton("");
		c5.setIcon(new ImageIcon(StartPanel.class.getResource("/character/��������.gif")));
		c5.setBounds(725, 147, 70, 94);
		c5.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c5.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c5.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c5);

		c6 = new JButton("");
		c6.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�ٳ�������.gif")));
		c6.setBounds(869, 155, 96, 88);
		c6.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c6.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c6.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c6);

		c7 = new JButton("");
		c7.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�ݰ�����.gif")));
		c7.setBounds(95, 270, 77, 90);
		c7.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c7.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c7.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c7);

		c8 = new JButton("");
		c8.setIcon(new ImageIcon(StartPanel.class.getResource("/character/��������.gif")));
		c8.setBounds(268, 270, 83, 89);
		c8.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c8.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c8.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c8);

		c9 = new JButton("");
		c9.setIcon(new ImageIcon(CharacterSelectPanel.class.getResource("/character/����������.png")));
		c9.setBounds(447, 282, 146, 86);
		c9.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c9.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c9.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c9);

		c10 = new JButton("");
		c10.setIcon(new ImageIcon(StartPanel.class.getResource("/character/���������.gif")));
		c10.setBounds(689, 273, 88, 95);
		c10.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c10.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c10.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c10);

		c11 = new JButton("");
		c11.setIcon(new ImageIcon(StartPanel.class.getResource("/character/��������.gif")));
		c11.setBounds(873, 270, 70, 97);
		c11.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c11.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c11.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c11);

		c12 = new JButton("");
		c12.setIcon(new ImageIcon(CharacterSelectPanel.class.getResource("/character/����������.png")));
		c12.setBounds(74, 397, 70, 93);
		c12.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c12.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c12.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c12);

		c13 = new JButton("");
		c13.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�ϰ�����.gif")));
		c13.setBounds(219, 401, 75, 89);
		c13.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c13.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c13.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c13);

		c14 = new JButton("");
		c14.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�ָԹ�����.gif")));
		c14.setBounds(369, 401, 79, 87);
		c14.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c14.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c14.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c14);

		c15 = new JButton("");
		c15.setIcon(new ImageIcon(StartPanel.class.getResource("/character/�丶������.gif")));
		c15.setBounds(523, 401, 82, 92);
		c15.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c15.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c15.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c15);

		c16 = new JButton("");
		c16.setIcon(new ImageIcon(StartPanel.class.getResource("/character/��������.gif")));
		c16.setBounds(680, 399, 117, 85);
		c16.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c16.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c16.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c16);

		c17 = new JButton("");
		c17.setIcon(new ImageIcon(CharacterSelectPanel.class.getResource("/character/�Ǹ�����.gif")));
		c17.setBounds(872, 399, 86, 90);
		c17.setBorderPainted(false); // ��ư�� �ܰ����� ������
		c17.setContentAreaFilled(false); // ��ư�� ���뿵�� ä��� ����
		c17.setFocusPainted(false); // ��ư�� ���� �Ǿ��� �� ����� �׵θ� ��� ����
		add(c17);

		c1.addActionListener(listener);
		c2.addActionListener(listener);
		c3.addActionListener(listener);
		c4.addActionListener(listener);
		c5.addActionListener(listener);
		c6.addActionListener(listener);
		c7.addActionListener(listener);
		c8.addActionListener(listener);
		c9.addActionListener(listener);
		c10.addActionListener(listener);
		c11.addActionListener(listener);
		c12.addActionListener(listener);
		c13.addActionListener(listener);
		c14.addActionListener(listener);
		c15.addActionListener(listener);
		c16.addActionListener(listener);
		c17.addActionListener(listener);

	}
}
