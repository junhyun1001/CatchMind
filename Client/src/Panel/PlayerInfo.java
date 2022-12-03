package Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerInfo extends JPanel {

	public JLabel idLabel;
	public JLabel iconLabel;
	public JLabel scoreLabel;
	public int score;

	public PlayerInfo() {

		setLayout(null);
		setBounds(8, 158, 230, 115);

		iconLabel = new JLabel();
		iconLabel.setBounds(114, 10, 110, 93);
		add(iconLabel);

		idLabel = new JLabel();
		idLabel.setBounds(12, 10, 90, 45);
		add(idLabel);

		scoreLabel = new JLabel();
		scoreLabel.setBounds(12, 57, 90, 45);
		add(scoreLabel);
	}
}