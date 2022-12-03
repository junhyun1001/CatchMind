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

//Server Message를 수신해서 화면에 표시
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
					// 단어를 받아옴
					case "WORD":
						mainFrame.gamePanel.setWord(gameDataDTO.data);
						break;
					case "SCORE":
						roomPanel.setScore(gameDataDTO);
						System.out.println(gameDataDTO.id + "님이 점수를 획득했습니다. " + gameDataDTO.score);
						break;

					case "UPDATEPLAYER":
						// 플레이어가 게임방에 입장하면 패널을 업데이트 시킴
						roomPanel.setPlayer(gameDataDTO);
						break;
					case "ALLREADY":
						// 모든 플레이어가 준비 되면 그림판을 초기화 시키고 준비 버튼 숨김
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
						// START 프로토콜을 받으면 서버에서 차례를 정해준다.
						if (gameDataDTO.boolData == true) {
							mainFrame.gamePanel.appendText("----------------------출제자 입니다.----------------------");
							mainFrame.gamePanel.drawingPanel.showSelectPanel();
						} else {
							mainFrame.gamePanel.appendText("----------------------문제를 맞추세요.----------------------");
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
				} // catch문 끝
			} // 바깥 catch문끝
		} // while()74
	} // run()
}
// class