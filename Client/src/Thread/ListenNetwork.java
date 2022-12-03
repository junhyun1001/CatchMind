package Thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;

import Network.ChatDTO;
import Network.DrawDTO;
import Network.GameDataDTO;
import Panel.DrawingPanel;
import Panel.PlayerInfo;
import Panel.RoomPanel;
import Start.MainFrame;

//Server Message�� �����ؼ� ȭ�鿡 ǥ��
public class ListenNetwork extends Thread {
	private MainFrame mainFrame;
	private RoomPanel roomPanel;
	private Socket socket;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public ListenNetwork(MainFrame mainFrame, RoomPanel roomPanel) {
		this.mainFrame = mainFrame;
		this.roomPanel = roomPanel;

		socket = mainFrame.getSocket();
		oos = mainFrame.getOOS();
		ois = mainFrame.getOIS();

	}

	public void run() {
		while (true) {
			try {
				Object object = null;
				String msg = null;
				ChatDTO chatDTO;
				DrawDTO drawDTO;
				GameDataDTO gameDataDTO;
				try {
					object = ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					break;
				}
				if (object == null)
					break;
				if (object instanceof ChatDTO) {
					chatDTO = (ChatDTO) object;
					msg = String.format("[%s] %s", chatDTO.id, chatDTO.msg);
					switch (chatDTO.code) {
					case "LOGIN":
						roomPanel.appendUserList(chatDTO.id);
						break;
					case "ICON":
						roomPanel.characterLabel.setIcon(chatDTO.icon);
						roomPanel.userNameLabel.setText(chatDTO.id);
						break;
					case "USERLIST":
						roomPanel.updateUserList(chatDTO.msg);
						break;
					case "CHAT": // chat message
						roomPanel.appendText(msg);
						break;
					case "GAMECHAT":
						mainFrame.gamePanel.appendText(msg);
						break;
					}
				} else if (object instanceof DrawDTO) {
					drawDTO = (DrawDTO) object;
					switch (drawDTO.code) {
					case "DRAW":
						mainFrame.gamePanel.drawingPanel.doMouseEvent(drawDTO);
						break;
					case "ERASEALL":
						mainFrame.gamePanel.drawingPanel.eraseAll();
						break;
					case "IMAGE":
						mainFrame.gamePanel.drawingPanel.AppendImage(drawDTO.img);
						break;
					}
				} else if (object instanceof GameDataDTO) {
					gameDataDTO = (GameDataDTO) object;
					switch (gameDataDTO.code) {
					// �ܾ �޾ƿ�
					case "WORD":
						mainFrame.gamePanel.setWord(gameDataDTO.data);
						break;
					case "SCORE":
						roomPanel.setScore(gameDataDTO);
						System.out.println(gameDataDTO.id + "���� ������ ȹ���߽��ϴ�. " + gameDataDTO.score);
						break;

					case "UPDATEPLAYER":
						// �÷��̾ ���ӹ濡 �����ϸ� �г��� ������Ʈ ��Ŵ
						roomPanel.setPlayer(gameDataDTO);
						break;
					case "ALLREADY":
						// ��� �÷��̾ �غ� �Ǹ� �׸����� �ʱ�ȭ ��Ű�� �غ� ��ư ����
						mainFrame.gamePanel.drawingPanel.eraseAll();
						mainFrame.gamePanel.changeReadyBtn();
						break;
					case "MAKEROOM":
						roomPanel.setRoomData(gameDataDTO);
						if (gameDataDTO != null) {
							roomPanel.makeRow();
						}
						break;
					case "START":
						// START ���������� ������ �������� ���ʸ� �����ش�.
						if (gameDataDTO.boolData == true) {
							mainFrame.gamePanel.appendText("----------------------������ �Դϴ�.----------------------");
							mainFrame.gamePanel.drawingPanel.showSelectPanel();
						} else {
							mainFrame.gamePanel.appendText("----------------------������ ���߼���.----------------------");
							mainFrame.gamePanel.drawingPanel.hideSelectPanel();
						}
						break;
					}
				}
			} catch (IOException e) {
				roomPanel.appendText("ois.readObject() error");
				e.printStackTrace();
				try {
					ois.close();
					oos.close();
					socket.close();
					break;
				} catch (Exception ee) {
					break;
				} // catch�� ��
			} // �ٱ� catch����
		} // while()74
	} // run()
}
// class