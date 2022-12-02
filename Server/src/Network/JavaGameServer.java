package Network;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JavaGameServer extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JTextArea textArea;
	private JTextField txtPortNumber;

	private ServerSocket socket; // 서버소켓
	private Socket client_socket; // accept() 에서 생성된 client 소켓
	private Vector userVec = new Vector(); // 연결된 사용자를 저장할 벡터
	private Vector<String> userNameVec = new Vector<>(); // 대기방에 있는 유저들을 저장하는 벡터
	private Vector<ImageIcon> userIconVec = new Vector<>(); // 대기방에 있는 유저 아이콘을 저장하는 벡터

	private Vector<String> playerNameVec = new Vector<>(); // 게임에 참가 했을 때 이름 저장하는 벡터
	private Vector<ImageIcon> playerIconVec = new Vector<>(); // 게임에 참가 했을 때 아이콘 저장하는 벡터

//	private HashMap<String, ImageIcon> playerInfoMap = new HashMap<>(); // 게임에 입장한 플레이어 정보를 저장
	private Vector roomVec = new Vector<>();
	private int roomId = 1;

	private int readyCount = 0; // 몇명이 준비 했는지 체크하는 변수

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaGameServer frame = new JavaGameServer();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public JavaGameServer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 338, 440);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 300, 298);
		contentPane.add(scrollPane);

		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(13, 318, 87, 26);
		contentPane.add(lblNewLabel);

		txtPortNumber = new JTextField();
		txtPortNumber.setHorizontalAlignment(SwingConstants.CENTER);
		txtPortNumber.setText("30000");
		txtPortNumber.setBounds(112, 318, 199, 26);
		contentPane.add(txtPortNumber);
		txtPortNumber.setColumns(10);

		JButton btnServerStart = new JButton("Server Start");
		btnServerStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					socket = new ServerSocket(Integer.parseInt(txtPortNumber.getText()));
				} catch (NumberFormatException | IOException e1) {
					e1.printStackTrace();
				}
				appendText("Chat Server Running..");
				btnServerStart.setText("Chat Server Running..");
				btnServerStart.setEnabled(false); // 서버를 더이상 실행시키지 못 하게 막는다
				txtPortNumber.setEnabled(false); // 더이상 포트번호 수정못 하게 막는다
				AcceptServer accept_server = new AcceptServer();
				accept_server.start();
			}
		});
		btnServerStart.setBounds(12, 356, 300, 35);
		contentPane.add(btnServerStart);
	}

	public void appendText(String str) {
		// textArea.append("사용자로부터 들어온 메세지 : " + str+"\n");
		textArea.append(str + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void appendObject(ChatDTO msg) {
		// textArea.append("사용자로부터 들어온 object : " + str+"\n");
		textArea.append("code = " + msg.code + "\n");
		textArea.append("id = " + msg.id + "\n");
		textArea.append("data = " + msg.msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	// 새로운 참가자 accept() 하고 user thread를 새로 생성한다.
	class AcceptServer extends Thread {
		@SuppressWarnings("unchecked")
		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					appendText("Waiting new clients ...");
					client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
					appendText("새로운 참가자 from " + client_socket);
					// User 당 하나씩 Thread 생성
					UserService new_user = new UserService(client_socket);
					userVec.add(new_user); // 새로운 참가자 배열에 추가
					new_user.start(); // 만든 객체의 스레드 실행
					appendText("현재 참가자 수 " + userVec.size());
				} catch (IOException e) {
					appendText("accept() error");
					// System.exit(0);
				}
			}
		}
	}

	// User 당 생성되는 Thread
	// Read One 에서 대기 -> Write All
	class UserService extends Thread {
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		private Socket client_socket;
		private Vector user_vc;
		public String id = "";
		public ImageIcon icon;
		public String userStatus;

		public Random rand = new Random();
		public String[] word = { "상어", "닭", "고래", "코끼리", "토끼" };
		private int score = 0; // 정답

		private boolean turn = true; // 출제자인지 아닌지 구분

		public UserService(Socket client_socket) {
			// 매개변수로 넘어온 자료 저장
			this.client_socket = client_socket;
			this.user_vc = userVec;
			try {
				oos = new ObjectOutputStream(client_socket.getOutputStream());
				oos.flush();
				ois = new ObjectInputStream(client_socket.getInputStream());
			} catch (Exception e) {
				appendText("userService error");
			}
		}

		public void login() {
			userNameVec.add(id);
			userIconVec.add(icon);
			appendText("새로운 참가자 " + id + " 입장.");
			writeOne("Welcome to Catchmind server\n");
			writeOne(id + "님 환영합니다.\n"); // 연결된 사용자에게 정상접속을 알림
			String msg = "[" + id + "]님이 입장 하였습니다.\n";
			writeOthers(msg); // 아직 user_vc에 새로 입장한 user는 포함되지 않았다.
			// 유저 목록 보냄
			writeAllUserVec();
			// RoomPanel에 자신한테만 보냄
			writeOneIcon(icon);
			// 방 리스트 로그인하고 업데이트
			updateRoomList();
		}

		public void logout() {
			String msg = "[" + id + "]님이 퇴장 하였습니다.\n";
			userVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
			userNameVec.remove(id);
			userIconVec.remove(icon);
//			playerInfoMap.remove(id);
			writeAll(msg); // 나를 제외한 다른 User들에게 전송
			appendText("사용자 " + "[" + id + "] 퇴장. 현재 참가자 수 " + userVec.size());
			// 로그아웃 후 유저 벡터 보냄
			writeAllUserVec();
			--readyCount;
			if (readyCount < 0)
				readyCount = 0;
			System.out.println("Logout: " + readyCount);

			// 로그아웃 후 플레이어 벡터 보냄
			playerNameVec.remove(id);
			writeAllPlayerVec();

		}

		public void writeOneUserVec(String users) {
			ChatDTO chatDTO = new ChatDTO("SERVER", "USERLIST", users);
			try {
				oos.writeObject(chatDTO);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 모든 유저 목록을 보냄
		public void writeAllUserVec() {
			String users = "";
			for (int i = 0; i < userNameVec.size(); i++) {
				users += userNameVec.elementAt(i);
				users += ",";
			}
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O") {
					user.writeOneUserVec(users);
				}
			}
		}

		// 단어 리스트 전송
		public void writeOneWord(String words) {
			GameDataDTO gameDataDTO = new GameDataDTO("SERVER", "WORD", words);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		// 모든 유저들에게 단어 방송함
		public void writeAllWord() {
			String splitWord = "";
			for (int i = 0; i < word.length; i++) {
				splitWord += word[i];
				splitWord += ",";
			}
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneWord(splitWord);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void writeOthersWord(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == "O")
					user.writeOneWord(str);
			}
		}

		// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void writeAll(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOne(str);
			}
		}

		public void writeOneObject(Object obj) {
			try {
				oos.writeObject(obj);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void writeAllObject(Object obj) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneObject(obj);
			}
		}

		// 유저들에게 점수 전송
		public void writeOneScore(Object obj) {
			GameDataDTO gameDataDTO = new GameDataDTO("SERVER", "SCORE", score);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		public void writeAllScore(Object obj) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneScore(obj);
			}
		}

		// RoomPanel에 자신 아이디와 캐릭터 보냄
		public void writeOneIcon(Object obj) {
			ChatDTO chatDTO = new ChatDTO(id, "ICON", icon);
			try {
				oos.writeObject(chatDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		public void writeAllIcon(Object obj) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneIcon(obj);
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void writeOthers(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == "O")
					user.writeOne(str);
			}
		}

		// UserService Thread가 담당하는 Client 에게 1:1 전송
		public void writeOne(String msg) {
			try {
				ChatDTO chatDTO = new ChatDTO("SERVER", "CHAT", msg);
				oos.writeObject(chatDTO);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		public void writeOneGameChat(String msg) {
			ChatDTO chatDTO = new ChatDTO("SERVER", "GAMECHAT", msg);
			try {
				oos.writeObject(chatDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void writeAllGameChat(String msg) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneGameChat(msg);
			}
		}

		public void writeOneAllReady() {
			GameDataDTO gameDataDTO = new GameDataDTO("SERVER", "ALLREADY", readyCount);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		// turn을 알림
		public void writeAllTurn(boolean turn) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneTurn(turn);
			}
		}

		public void writeOneTurn(boolean turn) {
			GameDataDTO gameDataDTO = new GameDataDTO(id, "START", turn);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("oos.writeObject(ob) error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout();
			}
		}

		// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
		public void writeOthersTurn(boolean turn) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user != this && user.userStatus == "O")
					user.writeOneTurn(turn);
			}
		}

		// 모든 유저가 준비되면 드로잉 패널을 초기화 시킴
		public void writeAllAllReady() {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneAllReady();
			}
		}

		// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
		public void writeAllMessage(String msg) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneObject(msg);
			}
		}

		// 귓속말 전송
		public void writePrivate(String msg) {
			try {
				ChatDTO obcm = new ChatDTO("귓속말", "CHAT", msg);
				oos.writeObject(obcm);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		public void writeOnePlayerVec(String users) {
			GameDataDTO gameDataDTO = new GameDataDTO("SERVER", "UPDATEPLAYER", users);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 모든 유저 목록을 보냄
		public void writeAllPlayerVec() {
			// 플레이어 이름을 문자열 + , 로 보낸 후 클라이언트에서 분리
			String users = "";
			for (int i = 0; i < playerNameVec.size(); i++) {
				users += playerNameVec.elementAt(i);
				users += ",";
			}

			// 아이콘 벡터를 보내야함

			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O") {
					user.writeOnePlayerVec(users);
				}
			}
		}

		// msg형태는 방 이름 자체만 전달함
		// room을 들어가는 처리
		// data가 방이름 인원, 최대인원으로 정해져있으니까 첫번째 인자에 인원수 추가
		// 일단 방이름으로 구분하고 나중에 아이디 사용
		public void enterRoom(GameDataDTO gameDataDTO) {
			playerNameVec.add(gameDataDTO.id); // 방에 입장하는 플레이어 id를 벡터에 추가함
			writeAllPlayerVec();

			playerIconVec.add(icon);
			System.out.println(playerIconVec);

			String roomId = gameDataDTO.data; // 만들때는 부여할 room_id가 없어서 기존 makeroom에 enterroom기능도 추가했다.
			for (int i = 0; i < roomVec.size(); i++) {
				GameRoom gameRoom = (GameRoom) roomVec.elementAt(i);
				if (roomId.equals(Integer.toString(gameRoom.getRoomId()))) {
					int j = gameRoom.enterRoom(gameDataDTO.id); // 입장 사용자 이름
					if (j == -1) // 인원이 꽉차서 입장을 못했을 경우엔
					{
						writeOneRoom("fail"); // 208 방 입장 실패
					} else {
						writeOneRoom(Integer.toString(gameRoom.getRoomId()));
					}
					break;
				}
			}
		}

		// 방 생성을 알림
		public void writeOneRoom(String roomId) {
			GameDataDTO gameDataDTO = new GameDataDTO(id, "MAKEROOM", roomId);
			try {
				oos.writeObject(gameDataDTO);
			} catch (IOException e) {
				appendText("dos.writeObject() error");
				try {
					ois.close();
					oos.close();
					client_socket.close();
					client_socket = null;
					ois = null;
					oos = null;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				logout(); // 에러가난 현재 객체를 벡터에서 지운다
			}
		}

		// 모든 유저한테 룸을 알림
		public void writeAllRoom(String str) {
			for (int i = 0; i < user_vc.size(); i++) {
				UserService user = (UserService) user_vc.elementAt(i);
				if (user.userStatus == "O")
					user.writeOneRoom(str);
			}
		}

		public void updateRoomList() {
			// room리스트의 변경내용을 보내는 부분
			String msg = "";
			for (int i = 0; i < roomVec.size(); i++) { // room갯수만큼 보내주고
				GameRoom gameroom = (GameRoom) roomVec.elementAt(i);
				msg += gameroom.getRoomId() + " " + gameroom.getRoomName() + " "
						+ Integer.toString(gameroom.getRoomUserCount()) + " " + Integer.toString(gameroom.maxUser)
						+ "\n";
			}
			writeAllRoom(msg);
		}

		public void makeAndEnterRoom(GameDataDTO gameDataDTO) {
			GameRoom gameRoom = new GameRoom(gameDataDTO.roomName, gameDataDTO.id);
			gameRoom.roomId = roomId; // 고유 방 번호 부여
			roomId++;
			roomVec.add(gameRoom);
			// System.out.println(Integer.toString(gameRoom.getRoomId()));
			writeOneRoom(Integer.toString(gameRoom.getRoomId()));
		}

		public void exitRoom(GameDataDTO gameDataDTO) {
			String roomId = gameDataDTO.data;
			for (int i = 0; i < roomVec.size(); i++) {
				GameRoom gameRoom = (GameRoom) roomVec.elementAt(i);
				if (roomId.equals(Integer.toString(gameRoom.getRoomId()))) {
					int j = gameRoom.exitRoom(gameDataDTO.id);
					if (j == 0) // 만들어진 방에 남은 인원이 없으면
					{
						roomVec.remove(i);
						writeOneRoom("EXITROOM");
					}
				}
			}
		}

		public void run() {
			while (true) { // 사용자 접속을 계속해서 받기 위해 while문
				try {
					Object object = null;
					String msg = null;
					ChatDTO chatDTO = null;
					DrawDTO drawDTO = null;
					GameDataDTO gameDataDTO = null;

					if (socket == null)
						break;
					try {
						object = ois.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return;
					}
					if (object == null)
						break;

					if (object instanceof ChatDTO) {
						chatDTO = (ChatDTO) object;
						appendObject(chatDTO);
						if (chatDTO.code.matches("LOGIN")) {
							id = chatDTO.id;
							icon = chatDTO.icon;
							userStatus = "O"; // Online 상태
							login();
						} else if (chatDTO.code.matches("CHAT") || chatDTO.code.matches("GAMECHAT")) {
							msg = String.format("[%s] %s", chatDTO.id, chatDTO.msg);
							appendText(msg); // server 화면에 출력
							String[] args = msg.split(" "); // 단어들을 분리한다.
							if (args.length == 1) { // Enter key 만 들어온 경우 Wakeup 처리만 한다.
								userStatus = "O";
							} else if (args[1].matches("/exit")) {
								logout();
								break;
							} else if (args[1].matches("/list")) {
								writeOne("User list\n");
								writeOne("Name\tStatus\n");
								writeOne("-----------------------------\n");
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									writeOne(user.id + "\t" + user.userStatus + "\n");
								}
								writeOne("-----------------------------\n");
							} else if (args[1].matches("/sleep")) {
								userStatus = "S";
							} else if (args[1].matches("/wakeup")) {
								userStatus = "O";
							} else if (args[1].matches("/to")) { // 귓속말
								for (int i = 0; i < user_vc.size(); i++) {
									UserService user = (UserService) user_vc.elementAt(i);
									if (user.id.matches(args[2]) && user.userStatus.matches("O")) {
										String msg2 = "";
										for (int j = 3; j < args.length; j++) {// 실제 message 부분
											msg2 += args[j];
											if (j < args.length - 1)
												msg2 += " ";
										}
										// /to 빼고.. [귓속말] [user1] Hello user2..
										user.writePrivate(args[0] + " " + msg2 + "\n");
										// user.WriteOne("[귓속말] " + args[0] + " " + msg2 + "\n");
										break;
									}
								}
							} else { // 일반 채팅 메시지
								userStatus = "O";
								writeAllObject(chatDTO);
							}
						} else if (chatDTO.code.matches("LOGOUT")) { // logout message 처리
							System.out.println("Logout from Client");
							logout();
							break;
						}
					}

					else if (object instanceof DrawDTO) {
						drawDTO = (DrawDTO) object;
						if (drawDTO.code.matches("DRAW")) {
							writeAllObject(drawDTO);
						} else if (drawDTO.code.matches("ERASEALL")) {
							writeAllObject(drawDTO);
						} else if (drawDTO.code.matches("IMAGE")) {
							writeOneObject(drawDTO);
						}

					} else if (object instanceof GameDataDTO) {
						gameDataDTO = (GameDataDTO) object;
						if (gameDataDTO.code.matches("SCORE")) {
							writeAllScore(score);
						} else if (gameDataDTO.code.matches("READY")) {
							// 사용자가 준비 버튼을 눌렀을 때

							if (gameDataDTO.boolData) {
								++readyCount;
								System.out.println("ready: " + readyCount);
								writeAllGameChat(id + "님이 준비를 하였습니다.");
								if (readyCount == 2) { // 모두 준비가 되면 시작
									// 클라이언트한테 모두 다 지우라고 시킴
									writeAllAllReady();
									writeAllGameChat("----------------------게임을 시작합니다.------------------------");
									// 출제자: turn = true; 플레이어 turn = false;를 방송해야 함
									// 즉 특정 플레이어를 지목해서 설정해줄 수 있어야 함 (playerNameVec을 이용할 수 있나?)
									// 클라이언트에서 받는 boolean 값이 true이면 출제자(페인트 패널이 보여야함)
									// 클라이언트에서 받는 boolean 값이 false이면 맞추는 쪽(페인트 패널이 안보여야 함)
									writeOneWord(word[rand.nextInt(5)]); // 단어 배열에서 랜덤으로 뽑아옴, 출제자한테만 보여줌
									writeOneTurn(true);
									writeOthersTurn(false);

								}
							} else { // 준비 취소 버튼을 눌렀을 때
								--readyCount;
								writeAllGameChat(id + "님이 준비를 취소 하였습니다.");
							}
						} else if (gameDataDTO.code.matches("MAKEROOM")) {
							makeAndEnterRoom(gameDataDTO);
							updateRoomList();
						} else if (gameDataDTO.code.matches("ENTERROOM")) {
							enterRoom(gameDataDTO);
							updateRoomList();
//							writeAllWord(); // 대기방에 입장하면 단어를 미리 가져옴

						} else if (gameDataDTO.code.matches("EXITROOM")) {
							exitRoom(gameDataDTO);
							updateRoomList();
						} else if (gameDataDTO.code.matches("ANSWER")) {
							// 정답을 맞춘 경우 차례를 넘기고 단어를 바꿈
							writeOneWord(word[rand.nextInt(5)]); // 단어 배열에서 랜덤으로 뽑아옴, 출제자한테만 보여줌
							writeOthersWord("제시어");
							writeOneTurn(true);
							writeOthersTurn(false);
						}
					} else { // ... 기타 object는 모두 방송한다.
						writeAllObject(drawDTO);

					}

				} catch (IOException e) {
					appendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						client_socket.close();
						logout(); // 에러가난 현재 객체를 벡터에서 지운다
						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝
			} // while
		} // run
	}

}