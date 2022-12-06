package Panel;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;

public class PlayerInfo extends JPanel {

	public JLabel idLabel;
	public JLabel iconLabel;
	public JLabel scoreLabel;
	public int score;

	public PlayerInfo() {

		setLayout(null);
		setBounds(8, 158, 230, 115);
		setBackground(new Color(8, 99, 165));

		iconLabel = new JLabel();
		iconLabel.setBounds(114, 10, 110, 93);
		add(iconLabel);

		idLabel = new JLabel();
		idLabel.setFont(new Font("»ﬁ∏’∆Ì¡ˆ√º", Font.PLAIN, 20));
		idLabel.setForeground(new Color(255, 255, 255));
		idLabel.setBounds(12, 10, 90, 45);
		idLabel.setHorizontalAlignment(JLabel.CENTER);
		add(idLabel);

		scoreLabel = new JLabel();
		scoreLabel.setBounds(12, 57, 90, 45);
		scoreLabel.setHorizontalAlignment(JLabel.CENTER);
		add(scoreLabel);
	}
}