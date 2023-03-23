package chess;

import java.awt.*;//�õ�����FlowLayout,BorderLayout����
import java.awt.event.*;//ʵ��ActionListener�ӿ�
import java.net.Socket;

import javax.swing.*;
@SuppressWarnings("serial")
public class StartChessJFrame extends JFrame{
	
	//���尴ť,���
	private ChessBoard chessboard;
	private JPanel three_button;//�Ű�ť��
	private JButton start_button;
	private JButton back_button;
	private JButton test;
	@SuppressWarnings("unused")
	private Socket socket;
	//���������
	public StartChessJFrame(Socket socket ) {
		this.socket=socket;
		//���ñ���
		setTitle("������");

		//��������
		//------------------------------------------------
		chessboard=new ChessBoard(socket);
		add(chessboard,BorderLayout.CENTER);
		//------------------------------------------------
		//��ʼ�ڽ���Ű�ť
		//ʵ�л�����
		three_button=new JPanel();
		start_button=new JButton("���¿�ʼ");
		back_button=new JButton("����");
		test=new JButton("test");
		//FlowLayout����
		three_button.setLayout(new FlowLayout(FlowLayout.LEFT));//�����
		three_button.add(start_button);
		three_button.add(back_button);
		three_button.add(test);
		//��������ť�ŵ��ϱ�
		add(three_button,BorderLayout.SOUTH);
		//����ҳ��ر��¼�
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//------------------------------------------------
		//ע������¼�
		buttonlistener touch=new buttonlistener();
		this.start_button.addActionListener(touch);
		back_button.addActionListener(touch);
		test.addActionListener(touch);
		add(chessboard);
		//���ô�С
		
		pack();//����Ӧ��С
	}
	public class buttonlistener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			Object o=e.getSource();
			if(o==start_button) {
				System.out.println("���¿�ʼ");
				chessboard.restartGame();
			}else if(o==back_button) {
				System.out.println("����");
				chessboard.goback();
			}else if(o==test) {
				chessboard.getgobacknumber();
			}
//			}else if(o==exit_button) {
//				System.out.println("����");
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
