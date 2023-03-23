package chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.io.*;

@SuppressWarnings("serial")
public class ChessBoard extends JPanel implements MouseListener {
	private Socket socket;
	public static final int MARGIN = 30; // �߾�
	public static final int GRID_SPAN = 65; // ������
	public static final int ROWS = 10;// ��������
	public static final int COLS = 10;// ��������

	Point[] chessList = new Point[(ROWS + 1) * (COLS + 1)]; // ��ʼÿ������Ԫ��Ϊnull
	boolean isBlack = true;// ĬȻ��ʼ�Ǻ�������
	boolean isyourturn = true;// Ĭ�Ͽ�ʼʱӵ�������Ȩ��
	boolean gameOver = false;// ��Ϸ�Ƿ����
	int chessNumber; // ��ǰ���̵����Ӹ���
	int xIndex, yIndex; // ��ǰ���µ����ӵ�����

	@SuppressWarnings("unused")
	private String chesscolor = "��";

	public void setcolor(String color) {
		this.chesscolor = color;
	}

	public ChessBoard(Socket socket) {
		this.socket = socket;
		setBackground(Color.ORANGE);// ���ñ�����ɫΪ�ٻ�ɫ
		addMouseListener(this);// ��Ӽ�����
	}

	// ����
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// ������
		for (int i = 0; i <= ROWS; i++) { // ������
			g.drawLine(MARGIN, MARGIN + i * GRID_SPAN, MARGIN + COLS * GRID_SPAN, MARGIN + i * GRID_SPAN);
		}
		for (int i = 0; i <= COLS; i++) {// ������
			g.drawLine(MARGIN + i * GRID_SPAN, MARGIN, MARGIN + i * GRID_SPAN, MARGIN + ROWS * GRID_SPAN);
		}
		// ������
		for (int i = 0; i < chessNumber; i++) {
			int xPos = chessList[i].getx() * GRID_SPAN + MARGIN; // ���񽻲���x����,xyת��Ϊ����ϵ�����
			int yPos = chessList[i].gety() * GRID_SPAN + MARGIN;// ���񽻲���y����
			g.setColor(chessList[i].getcolor()); // ������ɫ
			g.fillOval(xPos - Point.CHESS_D / 2, yPos - Point.CHESS_D / 2, Point.CHESS_D, Point.CHESS_D);

		} // z x$y

	}

	public void mousePressed(MouseEvent e) {// ��갴��������ϰ���ʱ���á�
		if(isBlack) {
			yourcolor=Color.BLACK;
		}else {
			yourcolor=Color.WHITE;
		}
		// ��Ϸ�Ѿ�������������
		if (gameOver) {
			String msg = "���ζԾ��ѽ���";
			JOptionPane.showMessageDialog(this, msg);
			return;
		}
					
		// ������Ļغϣ�������
		if (!isyourturn) {
			String msg = "�ⲻ����Ļغ�";
			JOptionPane.showMessageDialog(this, msg);
			System.out.println("�ⲻ����Ļغ�ִ����");
			return;
		}
		String colorName = isBlack ? "����" : "����";

		xIndex = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN; // �������������λ��ת��������������
		yIndex = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;

		// ���������⣬������
		if (xIndex < 0 || xIndex > ROWS || yIndex < 0 || yIndex > COLS)
			return;
		// x,yλ���Ѿ������Ӵ��ڣ�������
		if (findChess(xIndex, yIndex))
			return;
		// ������һ������
		gobacknumber=1;//������һ�����ӣ����Է��أ�
		Point ch = new Point(xIndex, yIndex, isBlack ? Color.black : Color.white);
		chessList[chessNumber++] = ch;
		sendchess(xIndex, yIndex);
		repaint(); // ֪ͨϵͳ���»���
		if (isWin(xIndex,yIndex)) {
			// ����ʤ����Ϣ,�����ټ�������
			String msg = String.format("��ϲ��%sӮ�ˣ�", colorName);
			JOptionPane.showMessageDialog(this, msg);
			gameOver = true;
		}
		isBlack = !isBlack;
	}

	// ---------------------�����������
	// �ͻ��˽��յ���Ϣ������������ʱ�ķ���
	public void setnewchess(int xIndex, int yIndex) {
		if (chessNumber != 0) {
			isyourturn = !isyourturn;// ����Ȩ�ޱ����
			System.out.println("Ȩ�ޱ����");
		}
		if (findChess(xIndex, yIndex)) {
			return;
		}
		System.out.println("setnewchessִ����");
		Point ch = new Point(xIndex, yIndex, isBlack ? Color.black : Color.white);
		chessList[chessNumber++] = ch;
		if(isWin(xIndex,yIndex)) {
			String msg = "���ζԾ��ѽ���";
			JOptionPane.showMessageDialog(this, msg);
		}
		repaint();
		isBlack = !isBlack;
	}

	public int getxIndex() {
		return xIndex;
	}

	public int getyIndex() {
		return yIndex;
	}

	// ����
	public void sendchess(int x, int y) {
		try {
			StringBuffer xy = new StringBuffer();
			xy.append("|");
			xy.append(x);
			xy.append("|");
			xy.append(y);
			System.out.println("xy:" + xy);
			PrintWriter socout = new PrintWriter(socket.getOutputStream());
			String readline = xy.toString();
			socout.println(readline);
			socout.flush();
			System.out.println("����������");
		} catch (Exception e) {
			System.out.println("���������쳣��" + e);
		}
	}
	// ---------------------------------

	// ����MouseListener�ķ���
	public void mouseClicked(MouseEvent e) {
	} // ��갴��������ϵ��������²��ͷţ�ʱ���á�

	public void mouseEntered(MouseEvent e) {
	}// �����뵽�����ʱ���á�

	public void mouseExited(MouseEvent e) {
	}// ����뿪���ʱ���á�

	public void mouseReleased(MouseEvent e) {
	} // ��갴ť��������ͷ�ʱ���á�

	// �����������в����Ƿ�������Ϊx,y�����Ӵ���
	private boolean findChess(int x, int y) {
		for (Point c : chessList) {
			if (c != null && c.getx() == x && c.gety() == y)
				return true;
		}
		return false;
	}

	private boolean isWin(int xIndex,int yIndex) {// �ж��Ƿ�Ӯ
		int continueCount = 1; // �������ӵĸ���
		// ��������Ѱ��
		for (int x = xIndex - 1; x >= 0; x--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, yIndex, c) != null) {
				continueCount++;
			} else
				break;
		}
		// ������Ѱ��
		for (int x = xIndex + 1; x <= ROWS; x++) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, yIndex, c) != null) {
				continueCount++;
			} else
				break;
		}
		if (continueCount >= 5) {
			return true;
		} else
			continueCount = 1;

		// ��������Ѱ��
		for (int y = yIndex - 1; y >= 0; y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(xIndex, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// ��������Ѱ��
		for (int y = yIndex + 1; y <= ROWS; y++) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(xIndex, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		if (continueCount >= 5) {
			return true;
		} else
			continueCount = 1;

		// б��
		// ����Ѱ��
		for (int x = xIndex + 1, y = yIndex - 1; y >= 0 && x <= COLS; x++, y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// ����Ѱ��
		for (int x = xIndex - 1, y = yIndex + 1; y <= ROWS && x >= 0; x--, y++) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		if (continueCount >= 5) {
			return true;
		} else
			continueCount = 1;

		// б��
		// ����Ѱ��
		for (int x = xIndex - 1, y = yIndex - 1; y >= 0 && x >= 0; x--, y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// ����Ѱ��
		for (int x = xIndex + 1, y = yIndex + 1; y <= ROWS && x <= COLS; x++, y++) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		if (continueCount >= 5) {
			return true;
		} else
			continueCount = 1;

		return false;
	}

	private Point getChess(int xIndex, int yIndex, Color color) {
		for (Point c : chessList) {// foreach������
			if (c != null && c.getx() == xIndex && c.gety() == yIndex && c.getcolor() == color)
				return c;
		}
		return null;
	}

	public void restartGame() {
		if (chessNumber != 0) {
			try {
				String readline = "���¿�ʼ";
				PrintWriter socout = new PrintWriter(socket.getOutputStream());
				socout.println(readline);
				socout.flush();
				System.out.println("�������¿�ʼ");
			} catch (Exception e) {
				System.out.println("�������¿�ʼ�쳣��" + e);
			}
		}
		// �������
		for (int i = 0; i < chessList.length; i++)
			chessList[i] = null;
		// �ָ���Ϸ��صı���ֵ
		yourcolor=null;
		isyourturn = true;
		isBlack = true;
		gameOver = false;// ��Ϸ�Ƿ����
		chessNumber = 0; // ��ǰ���̵����Ӹ���
		repaint();

	}
	private int  gobacknumber=0;
	public void getgobacknumber() {
		System.out.println("��ǰ�����ȨֵΪ��" + gobacknumber);
	}

	// �Ƿ��ܻ��壻
	@SuppressWarnings("unused")
	private Color yourcolor=null;
	public boolean cangoback() {
		int white = 0;
		int black = 0;
		for (int i = 0; i < chessNumber; i++) {
			if(chessList[i].getcolor().equals(Color.WHITE) ){
				white++;
			}else {
				black++;
			}	
		}
		if(yourcolor==null) {
			return false;
		}
		else if(yourcolor.equals(Color.BLACK)) 
		{
			if(black>white) {
				return true;
			}
			else {
				return false;
			}
		}else
		{
			if(white>black) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	// ����
	public void getgoback() {
		if (chessNumber== 0)
			return;

		chessList[chessNumber - 1] = null;
		chessNumber--;
		if (chessNumber> 0) {
			xIndex = chessList[chessNumber - 1].getx();
			yIndex = chessList[chessNumber - 1].gety();
		}
		isBlack = !isBlack;
		repaint();
	}
	public void goback() {
		if (chessNumber== 0) {
			String msg="��ǰ���̲�û������";
			JOptionPane.showMessageDialog(this, msg);
			return;
		}
		//----------------------------------------------------------------------------
		try {
			if(!cangoback()) {
				String msg="����δ��û���Ȩ��";
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			String readline="����";
			PrintWriter socout=new PrintWriter(socket.getOutputStream());
			socout.println(readline);
			socout.flush();
			System.out.println("���ͻ���");
			System.out.println("Ӧ����Ȩ�޷����˱��");
			if( gobacknumber==1) {
				isyourturn=!isyourturn;
				gobacknumber=0;
			}//����֮��Ȩ�ޱ��
			chessList[chessNumber - 1] = null;
			chessNumber--;
			if (chessNumber> 0) {
				xIndex = chessList[chessNumber - 1].getx();
				yIndex = chessList[chessNumber - 1].gety();
			}		
			isBlack = !isBlack;
			repaint();
			return;
		}catch(Exception e) {
			System.out.println("���ͻ����쳣��"+e);
		}
	}

	// Dimension: ���Σ����ô�С���������ô򿪵ı߿���С����ʾ
	public Dimension getPreferredSize() {
		return new Dimension(MARGIN * 2 + GRID_SPAN * COLS, MARGIN * 2 + GRID_SPAN * ROWS);
	}
}
