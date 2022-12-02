package Panel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Network.ChatDTO;
import Network.DrawDTO;
import Start.MainFrame;
import Start.StartPanel;

public class DrawingPanel extends JPanel {
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private ImageIcon board = new ImageIcon("./src/img/board.png");
	public SelectPaintPanel paintPanel = new SelectPaintPanel(this);

	private static String id;
	// 네트워크 및 전송 오브젝트
	private Socket socket;
	private ObjectInputStream ois;
	private static ObjectOutputStream oos;

	static String shape = "free"; // 도형의 형태를 담는 변수
	private static int stroke = 5; // 펜 굵기

	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image tmpImage = null;
	private Graphics g; // Graphics2D 클래스의 사용을 위해 선언
	private Graphics2D g2D;

	int x, y, ox, oy; // 움직인 후의 좌표(x, y)와 움직이기 전의 좌표(ox, oy)

	// 도형 변수
	int sx, sy, ex, ey; // 사각형 그리기
	int xdif, ydif, xlen, ylen;

	private static JButton line, lineRect, fillRect, lineCircle, fillCircle;

	private static JButton eraseBtn;
	private static JButton eraseAllBtn;
	private static JButton imgBtn;
	private static JSlider slider;

	private static JButton blackBtn;
	private static JButton redBtn;
	private static JButton blueBtn;
	private static JButton greenBtn;
	private static JButton yellowBtn;

	private static Color color; // 기본 컬러
	private static Color black = new Color(0, 0, 0);
	private static Color red = new Color(255, 0, 0);
	private static Color blue = new Color(0, 0, 255);
	private static Color green = new Color(0, 255, 0);
	private static Color yellow = new Color(255, 255, 0);
	private static Color white = new Color(255, 255, 255);

	// 버퍼 Panel
	private Graphics g2, g3;
	private Image panelImage = null;

	private Frame frame;
	private FileDialog file;

	public void paintComponent(Graphics g) {
		this.g = getGraphics();
		g2D = (Graphics2D) this.g;
		g.drawImage(board.getImage(), 0, 0, null);
		createPaint();
		g2.drawImage(board.getImage(), 0, 0, null);
		g3.drawImage(board.getImage(), 0, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	};

	public DrawingPanel(MainFrame mainFrame, GamePanel gamePanel) {
		this.mainFrame = mainFrame;
		this.gamePanel = gamePanel;
		socket = mainFrame.getSocket();
		id = mainFrame.getId();
		ois = mainFrame.getOIS();
		oos = mainFrame.getOOS();
		setBounds(250, 127, 527, 342);
		setLayout(null);

		color = black;

		MyMouseEvent mouse = new MyMouseEvent();
		addMouseListener(mouse);

		ImageSendAction imgAction = new ImageSendAction();
		imgBtn.addActionListener(imgAction);

		addMouseMotionListener(mouse);

	}

	public void doMouseEvent(DrawDTO cm) {
		if (cm.id.matches(id)) // 본인 것은 이미 Local 로 그렸다.
			return;

		// 색 지정
		g2.setColor(cm.color);
		// 펜 사이즈 지정
		Graphics2D g2d = (Graphics2D) g2;
		g2d.setStroke(new BasicStroke(cm.pen_size, BasicStroke.CAP_ROUND, 0));

		if (cm.mouse_e.getID() == MouseEvent.MOUSE_PRESSED) {
			ox = cm.mouse_e.getX(); // 직선 시작점 기억
			oy = cm.mouse_e.getY();
			sx = cm.x1; // 사각형 시작점 기억
			sy = cm.y1;
		}

		else if (cm.mouse_e.getID() == MouseEvent.MOUSE_DRAGGED) {
			if (cm.shape.equals("line")) {
				x = cm.mouse_e.getX();
				y = cm.mouse_e.getY();
				g2.drawLine(ox, oy, x, y);
				ox = x;
				oy = y;
			}
		} else if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
			ex = cm.x2;
			ey = cm.y2;

			xdif = ex - sx;
			ydif = ey - sy;
			xlen = Math.abs(ex - sx);
			ylen = Math.abs(ey - sy);

			if (cm.shape.equals("lineRect")) {
				if (xdif < 0 && ydif > 0) // 오른쪽 위에서 왼쪽 아래
					g2.drawRect(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0) // 왼쪽 위에서 오른쪽 아래
					g2.drawRect(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0) // 오른쪽 아래에서 왼쪽 위
					g2.drawRect(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0) // 왼쪽 아래에서 오른쪽 위
					g2.drawRect(sx, ey, xlen, ylen);
			} else if (cm.shape.equals("fillRect")) {
				if (xdif < 0 && ydif > 0) // 오른쪽 위에서 왼쪽 아래
					g2.fillRect(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0) // 왼쪽 위에서 오른쪽 아래
					g2.fillRect(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0) // 오른쪽 아래에서 왼쪽 위
					g2.fillRect(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0) // 왼쪽 아래에서 오른쪽 위
					g2.fillRect(sx, ey, xlen, ylen);
			} else if (cm.shape.equals("lineCircle")) {
				if (xdif < 0 && ydif > 0)// 오른쪽 위에서 왼쪽 아래
					g2.drawOval(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0)// 왼쪽 위에서 오른쪽 아래
					g2.drawOval(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0)// 오른쪽 아래에서 왼쪽 위
					g2.drawOval(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0)// 왼쪽 아래에서 오른쪽 위
					g2.drawOval(sx, ey, xlen, ylen);
			} else if (cm.shape.equals("fillCircle")) {
				if (xdif < 0 && ydif > 0)// 오른쪽 위에서 왼쪽 아래
					g2.fillOval(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0)// 왼쪽 위에서 오른쪽 아래
					g2.fillOval(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0)// 오른쪽 아래에서 왼쪽 위
					g2.fillOval(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0)// 왼쪽 아래에서 오른쪽 위
					g2.fillOval(sx, ey, xlen, ylen);
			}
		}
		g.drawImage(panelImage, 0, 0, paintPanel);
	}

	// 마우스 이벤트 서버 전송
	public void sendMouseEvent(MouseEvent e) {
		DrawDTO cm = new DrawDTO(id, "DRAW", "Mouse Event");
		cm.mouse_e = e;
		cm.pen_size = stroke;
		cm.color = color;
		cm.shape = shape;
		cm.x1 = sx;
		cm.y1 = sy;
		cm.x2 = ex;
		cm.y2 = ey;

		sendObject(cm);
	}

	public void paint(Graphics g) {
		super.paint(g);
		// Image 영역이 가려졌다 다시 나타날 때 그려준다.
		g.drawImage(panelImage, 0, 0, this);
	}

	// 숨어있는 버퍼에 그림그리기
	public void createPaint() {
		panelImage = createImage(this.getWidth(), this.getHeight());
		g2 = panelImage.getGraphics();
		g2.setColor(this.getBackground());
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(black);
		g2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);

		tmpImage = createImage(527, 342);
		g3 = tmpImage.getGraphics();
		g3.setColor(this.getBackground());
		g3.fillRect(0, 0, this.getWidth(), this.getHeight());
		g3.setColor(black);
		g3.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
	}

	// 전체 지우기 메소드
	public void eraseAll() {
		repaint();
		createPaint();
	}

	// 출제자가 아니면 패널을 숨김
	public void hideSelectPanel() {
		gamePanel.chatScrollPane.setBounds(250, 475, 527, 225);
		paintPanel.setVisible(false);
	}

	// 출제자면 패널을 보여줌
	public void showSelectPanel() {
		gamePanel.chatScrollPane.setBounds(250, 567, 527, 131);
		paintPanel.setVisible(true);
	}

	public static void sendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {
			g2.setColor(color);
			Graphics2D g2d = (Graphics2D) g2;
			g2d.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, 0));

			x = e.getX();
			y = e.getY();

			if (shape.equals("line"))
				g2.drawLine(ox, oy, x, y);

			ox = x; // 드래그 되는 시점에서 X좌표가 저장 - 밑에서 시작좌표와 끝좌표를 연결 해주어 선이 그어지게된다.
			oy = y; // 드래그 되는 시점에서 Y좌표가 저장 - 밑에서 시작좌표와 끝좌표를 연결 해주어 선이 그어지게된다.
			ex = e.getX();
			ey = e.getY();

			xdif = ex - sx;
			ydif = ey - sy;
			xlen = Math.abs(ex - sx);
			ylen = Math.abs(ey - sy);

			if (shape.equals("lineRect")) {
				g.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				g2.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				if (xdif < 0 && ydif > 0) // 오른쪽 위에서 왼쪽 아래
					g2.drawRect(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0) // 왼쪽 위에서 오른쪽 아래
					g2.drawRect(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0) // 오른쪽 아래에서 왼쪽 위
					g2.drawRect(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0) // 왼쪽 아래에서 오른쪽 위
					g2.drawRect(sx, ey, xlen, ylen);
			} else if (shape.equals("fillRect")) {
				g.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				g2.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				if (xdif < 0 && ydif > 0) // 오른쪽 위에서 왼쪽 아래
					g2.fillRect(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0) // 왼쪽 위에서 오른쪽 아래
					g2.fillRect(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0) // 오른쪽 아래에서 왼쪽 위
					g2.fillRect(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0) // 왼쪽 아래에서 오른쪽 위
					g2.fillRect(sx, ey, xlen, ylen);
			} else if (shape.equals("lineCircle")) {
				g.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				g2.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				if (xdif < 0 && ydif > 0)// 오른쪽 위에서 왼쪽 아래
					g2.drawOval(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0)// 왼쪽 위에서 오른쪽 아래
					g2.drawOval(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0)// 오른쪽 아래에서 왼쪽 위
					g2.drawOval(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0)// 왼쪽 아래에서 오른쪽 위
					g2.drawOval(sx, ey, xlen, ylen);
			} else if (shape.equals("fillCircle")) {
				g.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				g2.drawImage(tmpImage, 0, 0, DrawingPanel.this);
				if (xdif < 0 && ydif > 0)// 오른쪽 위에서 왼쪽 아래
					g2.fillOval(ex, sy, xlen, ylen);
				else if (xdif > 0 && ydif > 0)// 왼쪽 위에서 오른쪽 아래
					g2.fillOval(sx, sy, xlen, ylen);
				else if (xdif < 0 && ydif < 0)// 오른쪽 아래에서 왼쪽 위
					g2.fillOval(ex, ey, xlen, ylen);
				else if (xdif > 0 && ydif < 0)// 왼쪽 아래에서 오른쪽 위
					g2.fillOval(sx, ey, xlen, ylen);
			}

			// panelImage는 paint()에서 이용한다.
			g.drawImage(panelImage, 0, 0, DrawingPanel.this);
			sendMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {
			g3.drawImage(panelImage, 0, 0, DrawingPanel.this);
			ox = e.getX();
			oy = e.getY();
			sx = e.getX();
			sy = e.getY();// 사각형 시작좌표, 원 시작좌표

			sendMouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ex = e.getX();
			ey = e.getY();// 사각형 끝나는좌표

			if (shape.equals("rect")) {// 사각형
				g2.drawRect(sx, sy, ex - sx, ey - sy);
			}
			// repaint();

			sendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public static class SelectPaintPanel extends JPanel implements ActionListener {
		private DrawingPanel drawingPanel;

		public SelectPaintPanel(DrawingPanel drawingPanel) {
			this.drawingPanel = drawingPanel;
			setBounds(250, 467, 527, 90);
			setLayout(null);

			// ****************물감 선택**************** //
			blackBtn = new JButton();
			// blackBtn.setBackground(black);
			blackBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/black-removebg-preview.png")));
			blackBtn.setBorderPainted(false);
			blackBtn.setContentAreaFilled(false);
			blackBtn.setBounds(12, 10, 35, 35);
			add(blackBtn);
			blackBtn.addActionListener(this);
			blackBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = black;
				}
			});

			redBtn = new JButton();
			// redBtn.setBackground(red);
			redBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/red-removebg-preview.png")));
			redBtn.setBorderPainted(false);
			redBtn.setContentAreaFilled(false);
			redBtn.setBounds(59, 10, 35, 35);
			add(redBtn);
			redBtn.addActionListener(this);
			redBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = red;
				}
			});

			blueBtn = new JButton();
			blueBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/blue-removebg-preview.png")));
			blueBtn.setBorderPainted(false);
			blueBtn.setContentAreaFilled(false);
			blueBtn.setBounds(106, 10, 35, 35);
			add(blueBtn);
			blueBtn.addActionListener(this);
			blueBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = blue;
				}
			});

			greenBtn = new JButton();
			greenBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/green-removebg-preview.png")));
			greenBtn.setBorderPainted(false);
			greenBtn.setContentAreaFilled(false);
			greenBtn.setBounds(153, 10, 35, 35);
			add(greenBtn);
			greenBtn.addActionListener(this);
			greenBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = green;
				}
			});

			yellowBtn = new JButton();
			yellowBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/yellow-removebg-preview.png")));
			yellowBtn.setBorderPainted(false);
			yellowBtn.setContentAreaFilled(false);
			yellowBtn.setBounds(200, 10, 35, 35);
			add(yellowBtn);
			yellowBtn.addActionListener(this);
			yellowBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					color = yellow;
				}
			});

			// ****************펜 선택*****************//
			line = new JButton("");
			line.setIcon(new ImageIcon(StartPanel.class.getResource("/img/line.png")));
			line.setBounds(15, 50, 30, 30);
			add(line);
			line.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "line";
					color = black;
				}
			});

			lineRect = new JButton("");
			lineRect.setIcon(new ImageIcon(StartPanel.class.getResource("/img/lineRect.png")));
			lineRect.setBounds(60, 50, 30, 30);
			add(lineRect);
			lineRect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "lineRect";
					color = black;
				}
			});

			fillRect = new JButton("");
			fillRect.setIcon(new ImageIcon(StartPanel.class.getResource("/img/fillRect.png")));
			fillRect.setBounds(105, 50, 30, 30);
			add(fillRect);
			fillRect.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "fillRect";
					color = black;
				}
			});

			lineCircle = new JButton("");
			lineCircle.setIcon(new ImageIcon(StartPanel.class.getResource("/img/lineCircle.png")));
			lineCircle.setBounds(155, 50, 30, 30);
			add(lineCircle);
			lineCircle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "lineCircle";
					color = black;
				}
			});

			fillCircle = new JButton("");
			fillCircle.setIcon(new ImageIcon(StartPanel.class.getResource("/img/fillCircle.png")));
			fillCircle.setBounds(205, 50, 30, 30);
			add(fillCircle);
			fillCircle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "fillCircle";
					color = black;
				}
			});

			// ****************팬 굵기 조절*****************//
			slider = new JSlider(0, 10);
			slider.setBounds(250, 15, 270, 30);
			add(slider);

			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					// TODO Auto-generated method stub
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						stroke = (int) source.getValue();
					}
				}
			});

			// ****************지우개 선택*****************//
			eraseBtn = new JButton();
			eraseBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/eraser.png")));
			eraseBtn.setBorderPainted(false);
			eraseBtn.setBorderPainted(false);
			eraseBtn.setContentAreaFilled(false);
			eraseBtn.setBounds(250, 50, 60, 35);
			add(eraseBtn);
			eraseBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					shape = "line";
					color = white;
				}
			});

			eraseAllBtn = new JButton();
			eraseAllBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/eraseAll.png")));
			eraseAllBtn.setBorderPainted(false);
			eraseAllBtn.setBorderPainted(false);
			eraseAllBtn.setContentAreaFilled(false);
			eraseAllBtn.setBounds(300, 50, 130, 35);
			add(eraseAllBtn);
			eraseAllBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					drawingPanel.eraseAll();
					DrawDTO drawDTO = new DrawDTO(id, "ERASEALL", "eraseAll");
					sendObject(drawDTO);
				}
			});

			// ****************밑그림 선택*****************//
			imgBtn = new JButton();
			imgBtn.setIcon(new ImageIcon(StartPanel.class.getResource("/img/eraseAll.png")));
			imgBtn.setBorderPainted(false);
			imgBtn.setBorderPainted(false);
			imgBtn.setContentAreaFilled(false);
			imgBtn.setBounds(400, 50, 130, 35);
			add(imgBtn);
		}

		// 페인트 색 바꾸기
		@Override
		public void actionPerformed(ActionEvent e) {

		}

	}

	public void AppendImage(ImageIcon ori_icon) {
		Image ori_img = ori_icon.getImage();
		g2.drawImage(ori_img, 0, 0, this.getWidth(), this.getHeight(), this);
		g.drawImage(panelImage, 0, 0, this.getWidth(), this.getHeight(), this);
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				file = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				file.setVisible(true);
				if (file.getDirectory().length() > 0 && file.getFile().length() > 0) {
					DrawDTO drawDTO = new DrawDTO(id, "IMAGE", "image");
					ImageIcon img = new ImageIcon(file.getDirectory() + file.getFile());
					drawDTO.img = img;
					sendObject(drawDTO);
				}
			}
		}
	}

}