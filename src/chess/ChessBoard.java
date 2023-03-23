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
	public static final int MARGIN = 30; // 边距
	public static final int GRID_SPAN = 65; // 网格间距
	public static final int ROWS = 10;// 棋盘行数
	public static final int COLS = 10;// 棋盘列数

	Point[] chessList = new Point[(ROWS + 1) * (COLS + 1)]; // 初始每个数组元素为null
	boolean isBlack = true;// 默然开始是黑棋先下
	boolean isyourturn = true;// 默认开始时拥有下棋的权限
	boolean gameOver = false;// 游戏是否结束
	int chessNumber; // 当前棋盘的棋子个数
	int xIndex, yIndex; // 当前刚下的棋子的坐标

	@SuppressWarnings("unused")
	private String chesscolor = "黑";

	public void setcolor(String color) {
		this.chesscolor = color;
	}

	public ChessBoard(Socket socket) {
		this.socket = socket;
		setBackground(Color.ORANGE);// 设置背景颜色为橘黄色
		addMouseListener(this);// 添加监听器
	}

	// 绘制
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// 画棋盘
		for (int i = 0; i <= ROWS; i++) { // 画横线
			g.drawLine(MARGIN, MARGIN + i * GRID_SPAN, MARGIN + COLS * GRID_SPAN, MARGIN + i * GRID_SPAN);
		}
		for (int i = 0; i <= COLS; i++) {// 画纵线
			g.drawLine(MARGIN + i * GRID_SPAN, MARGIN, MARGIN + i * GRID_SPAN, MARGIN + ROWS * GRID_SPAN);
		}
		// 画棋子
		for (int i = 0; i < chessNumber; i++) {
			int xPos = chessList[i].getx() * GRID_SPAN + MARGIN; // 网格交叉点的x坐标,xy转换为面板上的坐标
			int yPos = chessList[i].gety() * GRID_SPAN + MARGIN;// 网格交叉点的y坐标
			g.setColor(chessList[i].getcolor()); // 设置颜色
			g.fillOval(xPos - Point.CHESS_D / 2, yPos - Point.CHESS_D / 2, Point.CHESS_D, Point.CHESS_D);

		} // z x$y

	}

	public void mousePressed(MouseEvent e) {// 鼠标按键在组件上按下时调用。
		if(isBlack) {
			yourcolor=Color.BLACK;
		}else {
			yourcolor=Color.WHITE;
		}
		// 游戏已经结束，不能下
		if (gameOver) {
			String msg = "本次对局已结束";
			JOptionPane.showMessageDialog(this, msg);
			return;
		}
					
		// 不是你的回合，不能下
		if (!isyourturn) {
			String msg = "这不是你的回合";
			JOptionPane.showMessageDialog(this, msg);
			System.out.println("这不是你的回合执行了");
			return;
		}
		String colorName = isBlack ? "黑棋" : "白棋";

		xIndex = (e.getX() - MARGIN + GRID_SPAN / 2) / GRID_SPAN; // 将鼠标点击的坐标位置转换成网格索引。
		yIndex = (e.getY() - MARGIN + GRID_SPAN / 2) / GRID_SPAN;

		// 落在棋盘外，不能下
		if (xIndex < 0 || xIndex > ROWS || yIndex < 0 || yIndex > COLS)
			return;
		// x,y位置已经有棋子存在，不能下
		if (findChess(xIndex, yIndex))
			return;
		// 新生成一个棋子
		gobacknumber=1;//刚下了一课棋子，可以返回；
		Point ch = new Point(xIndex, yIndex, isBlack ? Color.black : Color.white);
		chessList[chessNumber++] = ch;
		sendchess(xIndex, yIndex);
		repaint(); // 通知系统重新绘制
		if (isWin(xIndex,yIndex)) {
			// 给出胜利信息,不能再继续下棋
			String msg = String.format("恭喜，%s赢了！", colorName);
			JOptionPane.showMessageDialog(this, msg);
			gameOver = true;
		}
		isBlack = !isBlack;
	}

	// ---------------------联机下棋相关
	// 客户端接收到消息，设置新棋子时的方法
	public void setnewchess(int xIndex, int yIndex) {
		if (chessNumber != 0) {
			isyourturn = !isyourturn;// 下棋权限变更；
			System.out.println("权限变更了");
		}
		if (findChess(xIndex, yIndex)) {
			return;
		}
		System.out.println("setnewchess执行了");
		Point ch = new Point(xIndex, yIndex, isBlack ? Color.black : Color.white);
		chessList[chessNumber++] = ch;
		if(isWin(xIndex,yIndex)) {
			String msg = "本次对局已结束";
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

	// 发送
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
			System.out.println("发送了棋子");
		} catch (Exception e) {
			System.out.println("发送棋子异常：" + e);
		}
	}
	// ---------------------------------

	// 覆盖MouseListener的方法
	public void mouseClicked(MouseEvent e) {
	} // 鼠标按键在组件上单击（按下并释放）时调用。

	public void mouseEntered(MouseEvent e) {
	}// 鼠标进入到组件上时调用。

	public void mouseExited(MouseEvent e) {
	}// 鼠标离开组件时调用。

	public void mouseReleased(MouseEvent e) {
	} // 鼠标按钮在组件上释放时调用。

	// 在棋子数组中查找是否有索引为x,y的棋子存在
	private boolean findChess(int x, int y) {
		for (Point c : chessList) {
			if (c != null && c.getx() == x && c.gety() == y)
				return true;
		}
		return false;
	}

	private boolean isWin(int xIndex,int yIndex) {// 判断那方赢
		int continueCount = 1; // 连续棋子的个数
		// 横向向西寻找
		for (int x = xIndex - 1; x >= 0; x--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, yIndex, c) != null) {
				continueCount++;
			} else
				break;
		}
		// 横向向东寻找
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

		// 纵向向上寻找
		for (int y = yIndex - 1; y >= 0; y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(xIndex, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// 纵向向下寻找
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

		// 斜向
		// 东北寻找
		for (int x = xIndex + 1, y = yIndex - 1; y >= 0 && x <= COLS; x++, y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// 西南寻找
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

		// 斜向
		// 西北寻找
		for (int x = xIndex - 1, y = yIndex - 1; y >= 0 && x >= 0; x--, y--) {
			Color c = isBlack ? Color.black : Color.white;
			if (getChess(x, y, c) != null) {
				continueCount++;
			} else
				break;
		}
		// 东南寻找
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
		for (Point c : chessList) {// foreach语句遍历
			if (c != null && c.getx() == xIndex && c.gety() == yIndex && c.getcolor() == color)
				return c;
		}
		return null;
	}

	public void restartGame() {
		if (chessNumber != 0) {
			try {
				String readline = "重新开始";
				PrintWriter socout = new PrintWriter(socket.getOutputStream());
				socout.println(readline);
				socout.flush();
				System.out.println("发送重新开始");
			} catch (Exception e) {
				System.out.println("发送重新开始异常：" + e);
			}
		}
		// 清除棋子
		for (int i = 0; i < chessList.length; i++)
			chessList[i] = null;
		// 恢复游戏相关的变量值
		yourcolor=null;
		isyourturn = true;
		isBlack = true;
		gameOver = false;// 游戏是否结束
		chessNumber = 0; // 当前棋盘的棋子个数
		repaint();

	}
	private int  gobacknumber=0;
	public void getgobacknumber() {
		System.out.println("当前悔棋的权值为：" + gobacknumber);
	}

	// 是否能悔棋；
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

	// 悔棋
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
			String msg="当前棋盘并没有棋子";
			JOptionPane.showMessageDialog(this, msg);
			return;
		}
		//----------------------------------------------------------------------------
		try {
			if(!cangoback()) {
				String msg="你暂未获得悔棋权限";
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			String readline="悔棋";
			PrintWriter socout=new PrintWriter(socket.getOutputStream());
			socout.println(readline);
			socout.flush();
			System.out.println("发送悔棋");
			System.out.println("应悔棋权限发生了变更");
			if( gobacknumber==1) {
				isyourturn=!isyourturn;
				gobacknumber=0;
			}//悔棋之后权限变更
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
			System.out.println("发送悔棋异常："+e);
		}
	}

	// Dimension: 矩形，设置大小，若不设置打开的边框将最小化显示
	public Dimension getPreferredSize() {
		return new Dimension(MARGIN * 2 + GRID_SPAN * COLS, MARGIN * 2 + GRID_SPAN * ROWS);
	}
}
