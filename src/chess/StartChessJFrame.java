package chess;

import java.awt.*;//用到其下FlowLayout,BorderLayout布局
import java.awt.event.*;//实现ActionListener接口
import java.net.Socket;

import javax.swing.*;
@SuppressWarnings("serial")
public class StartChessJFrame extends JFrame{
	
	//定义按钮,棋板
	private ChessBoard chessboard;
	private JPanel three_button;//放按钮的
	private JButton start_button;
	private JButton back_button;
	private JButton test;
	@SuppressWarnings("unused")
	private Socket socket;
	//主界面设计
	public StartChessJFrame(Socket socket ) {
		this.socket=socket;
		//设置标题
		setTitle("五子棋");

		//加入棋盘
		//------------------------------------------------
		chessboard=new ChessBoard(socket);
		add(chessboard,BorderLayout.CENTER);
		//------------------------------------------------
		//开始在界面放按钮
		//实列化对象
		three_button=new JPanel();
		start_button=new JButton("重新开始");
		back_button=new JButton("悔棋");
		test=new JButton("test");
		//FlowLayout布局
		three_button.setLayout(new FlowLayout(FlowLayout.LEFT));//左对齐
		three_button.add(start_button);
		three_button.add(back_button);
		three_button.add(test);
		//把三个按钮放到南边
		add(three_button,BorderLayout.SOUTH);
		//设置页面关闭事件
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//------------------------------------------------
		//注册监听事件
		buttonlistener touch=new buttonlistener();
		this.start_button.addActionListener(touch);
		back_button.addActionListener(touch);
		test.addActionListener(touch);
		add(chessboard);
		//设置大小
		
		pack();//自适应大小
	}
	public class buttonlistener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object o=e.getSource();
			if(o==start_button) {
				System.out.println("重新开始");
				chessboard.restartGame();
			}else if(o==back_button) {
				System.out.println("悔棋");
				chessboard.goback();
			}else if(o==test) {
				chessboard.getgobacknumber();
			}
//			}else if(o==exit_button) {
//				System.out.println("结束");
//				System.exit(0);
//			}
		}
	}
	public void setchess(int x,int y) {
		chessboard.setnewchess(x, y);
	}
	public void touchrestart() {
		chessboard.restartGame();
	}
	public void touchgoback() {
		chessboard.goback();
	}
	public void getgoback() {
		chessboard.getgoback();
	}
//	public void setchesscolor(String color) {
//		chessboard.setcolor(color);
//	}
//	public static void main(String [] args) {
//		StartChessJFrame game=new StartChessJFrame();
//		game.setVisible(true);
//	}
}
