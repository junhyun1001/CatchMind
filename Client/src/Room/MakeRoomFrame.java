package Room;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MakeRoomFrame extends JFrame {
	private JTable table;

	private DefaultTableModel model;
	private JTextField room, count;
	
	public MakeRoomFrame(DefaultTableModel model) {
		this.model = model;
		setTitle("방 만들기");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(0, 0, 400, 250);
		setResizable(false);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel roomNameLabel = new JLabel("방 이름:");
		roomNameLabel.setFont(new Font("휴먼편지체", Font.BOLD, 16));
		roomNameLabel.setBounds(12, 28, 60, 25);
		getContentPane().add(roomNameLabel);

		room = new JTextField();
		room.setBounds(84, 30, 290, 21);
		getContentPane().add(room);
		room.setColumns(10);
		
		JButton makeRoomBtn = new JButton("방 생성");
		makeRoomBtn.setBounds(70, 153, 105, 37);
		getContentPane().add(makeRoomBtn);
		setVisible(true);
		
		JButton cancleBtn = new JButton("취소");
		cancleBtn.setBounds(200, 153, 105, 37);
		getContentPane().add(cancleBtn);
		setVisible(true);
		
		makeRoomBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 방만들기
				String inputStr[] = new String[2];
				inputStr[0] = room.getText().trim();
				inputStr[1] = count.getText().trim();
				model.addRow(inputStr);
				dispose();
			}
		});
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
}
