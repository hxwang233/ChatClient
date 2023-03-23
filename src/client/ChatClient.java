package client;
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import chess.*;
public class ChatClient extends JFrame{
	static ChatClient cc;
	static boolean flag=false;  //登录标记
	static boolean tip=false;
	StringBuffer info;  //存取用户账号和密码 
	String id;
	String pwd;
	JFrame frame;
	JTextField loginname;  
	JPasswordField password;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	Thread t;  //从服务器接受登录成功信息的线程   登录成功后中断
	
	
	int winX=0;
	int winY=0;
	JList<JLabel> userlist;//左侧用户聊天列表
	JTextArea messageArea;
	JTextArea sendArea;
	DefaultListModel<JLabel> model;
	JFrame tip_frame ;
	public void login() {  //登录
		
		frame = new JFrame("登录");
		
		//窗口设置
			Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
			frame.getContentPane().setBackground(new Color(235,242,249));
			frame.getContentPane().setLayout(null);
			frame.setBounds((int)(scrSize.getWidth()-389)/2,(int)(scrSize.getHeight()-562)/2, 389, 562);
			winX=(int)(scrSize.getWidth()-960)/2;//初始化聊天窗口的位置
			winY=(int)(scrSize.getHeight()-768)/2;//初始化聊天窗口的位置
			frame.setResizable(false);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					try {
						PrintWriter pw=new PrintWriter(sock.getOutputStream());
						pw.println("&NO&");
						pw.flush();
						pw.close();
					//	sock.shutdownInput();
					//	sock.shutdownOutput();
						sock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			});
		//账号框
			loginname = new JTextField();
			loginname.setFont(new Font("微软雅黑", Font.PLAIN, 17));
			loginname.setBounds(75, 238, 239, 35);
			frame.getContentPane().add(loginname);
			loginname.setColumns(10);
		//密码框
			password = new JPasswordField();
			password.setFont(new Font("微软雅黑", Font.PLAIN, 19));
			password.setBounds(75, 285, 239, 35);
			frame.getContentPane().add(password);

		//注册账号标签
			JLabel label_2 = new JLabel("注册账号");
			label_2.setFont(new Font("微软雅黑", Font.PLAIN, 12));
			label_2.setBounds(75, 332, 72, 18);
			label_2.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					new Register();
				}
			});
			frame.getContentPane().add(label_2);
		//找回密码标签
			JLabel label_3 = new JLabel("找回密码");
			label_3.setFont(new Font("微软雅黑", Font.PLAIN, 12));
			label_3.setBounds(260, 332, 54, 18);
			label_3.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					new Retrieve();
				}
			});
			frame.getContentPane().add(label_3);
		
		//登录键
			JButton btnNewButton = new JButton("登录");
			btnNewButton.setForeground(Color.WHITE);
			btnNewButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
			btnNewButton.setBackground(new Color(0,163,255));
			btnNewButton.addActionListener(new loginButtonListener());//添加监听事件
			btnNewButton.setBounds(75, 362, 239, 35);
			frame.getContentPane().add(btnNewButton);
			frame.setVisible(true);
			
		setUpNetworking();   //建立连接
		t=new Thread(new LoginStatus());
		t.start();
		
	}
	public void failLogin(){
		tip_frame = new JFrame();
		tip_frame.setBounds(100, 100, 363, 253);

		
		JPanel tip_panel = new JPanel();
		tip_panel.setBounds(10, 10, 328, 196);
		tip_frame.getContentPane().add(tip_panel);
		
		JButton tipButton = new JButton("确认");
		tipButton.setBounds(120, 123, 87, 23);
		tip_panel.setLayout(null);
		tip_panel.add(tipButton);
		tipButton.addActionListener(new tip());
		
		JLabel tipLabel = new JLabel("账号或密码错误");
		tipLabel.setForeground(Color.RED);
		tipLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tipLabel.setBounds(52, 43, 220, 48);
		tip_panel.add(tipLabel);
		
	//	tip_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tip_frame.getContentPane().setLayout(null);
		tip_frame.setVisible(true);
	}

	public void go(String [] list) {  //进入聊天室 开始聊天
		
		frame = new JFrame("聊天室 - 你的id："+id);
		
		//窗口设置
				Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
				frame.getContentPane().setBackground(new Color(235,242,249));
				frame.getContentPane().setLayout(null);
				frame.setLocation(winX, winY);
				frame.setSize(960, 768);
				frame.setResizable(false);
				frame.addComponentListener(new WindowChangeLinstener());
				frame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {					
						try {
							PrintWriter pw=new PrintWriter(sock.getOutputStream());
							pw.println("&LOGOFF&");
							pw.flush();
							pw.close();
						//	sock.shutdownInput();
						//	sock.shutdownOutput();
							sock.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.exit(0);
					}
				});
		
		//左侧聊天列表部分
				JScrollPane chatListScrollPane = new JScrollPane();
				chatListScrollPane.setBounds(12, 73, 232, 648);
				frame.getContentPane().add(chatListScrollPane);
				userlist = new JList<>();
				chatListScrollPane.setViewportView(userlist);
				
				ListCellRenderer<JLabel> lcr=new CellRenderer();//创建渲染器
				model= new DefaultListModel<>();
				userlist.setModel(model);
				userlist.setFixedCellHeight(60);
				userlist.setCellRenderer(lcr);//添加渲染器
				userlist.addListSelectionListener(new CellListener());//创建事件监听
		
				for(String temp:list) {
					if(temp.equals(id))
						continue;
					else 
						model.addElement(new JLabel(temp));//添加列表元素
				}
		
				JButton sendButton = new JButton("发送");
				sendButton.setBounds(837, 685, 105, 36);
				frame.getContentPane().add(sendButton);
				sendButton.addActionListener(new SendButtonListener());
				
				JButton backbutton = new JButton("返回");
				backbutton.setBounds(715, 685, 105, 36);
				frame.getContentPane().add(backbutton);
				backbutton.addActionListener(new BackButtonListener());
				
		//聊天内容显示界面
				JScrollPane ChatMessageScrollPane = new JScrollPane();
				ChatMessageScrollPane.setBounds(256, 73, 686, 454);
				frame.getContentPane().add(ChatMessageScrollPane);
				messageArea = new JTextArea();
				ChatMessageScrollPane.setViewportView(messageArea);
		
		//发送信息输入界面
				JScrollPane sendMessageScrollPane = new JScrollPane();
				sendMessageScrollPane.setBounds(256, 539, 686, 144);
				frame.getContentPane().add(sendMessageScrollPane);
				
				sendArea = new JTextArea();
				sendMessageScrollPane.setViewportView(sendArea);
				
				messageArea.setLineWrap(true);
				messageArea.setWrapStyleWord(true);
				messageArea.setEditable(false);
				
		HistoryImpl HImpl=new HistoryImpl();    //获取历史记录
		ArrayList<String> ghis=HImpl.getGChatHistory();  
		if(ghis!=null) {
			for(String str:ghis) {
				messageArea.append(str+"\n");
				messageArea.setCaretPosition(messageArea.getText().length());
			}
		}
		
		Thread rt=new Thread(new ReaderForIn());
		rt.start();
		frame.setVisible(true);
	}
	
	private void setUpNetworking() {  //与服务器端建立连接
		try {
			sock=new Socket("127.0.0.1",8113);
			InputStreamReader isreader=new InputStreamReader(sock.getInputStream());
			reader=new BufferedReader(isreader);
			writer=new PrintWriter(sock.getOutputStream());
			System.out.println("连接成功");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
/*	private void setUpNetworking() {  //与服务器端建立连接
		try {
			InetAddress inet=InetAddress.getByName("3s.dkys.org");
			sock = new Socket(inet, 28989);
			InputStreamReader isreader=new InputStreamReader(sock.getInputStream());
			reader=new BufferedReader(isreader);
			writer=new PrintWriter(sock.getOutputStream());
			System.out.println("连接成功");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/

	private class loginButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			id=loginname.getText();
			pwd=String.valueOf(password.getPassword());	
			if(!id.equals("")&&!pwd.equals("")) {
				info=new StringBuffer(id);
				info.append("/");
				info.append(pwd);
				try {
					writer.println("LOG");  //发送登录请求
					writer.println(info);   //发送自己的账号密码到服务器端验证
					writer.flush();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				loginname.setText("");    //清空
				loginname.requestFocus(); //重新获得焦点
				password.setText("");
				password.requestFocus();
			}
		}
	}
		
	private class tip implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			tip=true;
			if(tip) {
				tip_frame.dispose();
				t.interrupt();
				frame.dispose();
				cc.login();		
				tip=false;
			}
		}	
	}
	
	private class SendButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {	
				writer.println(sendArea.getText());
				writer.flush();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			sendArea.setText("");
			sendArea.requestFocus();
		}	
	}
	private class BackButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				writer.println("&BACKGCHAT&");
				writer.flush();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			userlist.setSelectedIndices(null);
		}	
	}

	class LoginStatus implements Runnable{
		@Override
		public void run() {
			String message;
			String init;
			String [] initList = null;
			try {
				if((message=reader.readLine())!=null) {
					System.out.println("Status: "+message);
					if(message.equals("OK")) {
						flag=true;
					}
					if(message.equals("FAIL")) {
						flag=false;
					}
				}
				if(flag) {//若登录成功
					if((init=reader.readLine())!=null) {  //从服务器端获取当前已经登录的id
						initList=init.split("#");
					}
					frame.dispose();
					cc.go(initList); //初始化窗口
					t.interrupt();					
				}
				else {
					cc.failLogin();
				}
			}catch(Exception e) {
			}
		}
		
	}
	class ReaderForIn implements Runnable{
		@Override
		public void run() {
			String message;
			String [] updatelist;
			StartChessJFrame game = null;
//			try {
//				while((message=reader.readLine())!=null) {
//					if(message.equals("UPDATELIST")) {  //若有新用户登录 实时更新聊天列表
//						System.out.println("read："+message);
//						while((message=reader.readLine())!=null) {  
//							updatelist=message.split("#");
//							frame.dispose();
//							cc.go(updatelist);
//							break;
//						}
//						break;
//					}
//					else {	
//						System.out.println("read："+message);
//						messageArea.append(message+"\n");
//						messageArea.setCaretPosition(messageArea.getText().length());
//					}
//				}
//			}
			try {
				while((message=reader.readLine())!=null) {
					if(message.equals("UPDATELIST")) {  //若有新用户登录 实时更新聊天列表
						System.out.println("read："+message);
						while((message=reader.readLine())!=null) {  
							updatelist=message.split("#");
							frame.dispose();
							cc.go(updatelist);	 //实时更新
							break;
						}
						break;
					}
					else {	
						System.out.println("===========");
						System.out.println("read："+message);
						//-----------------------------------------下棋
						if(canstartgame(message))
						{   
							game=new StartChessJFrame(sock);
							game.setVisible(true);
							while((message=reader.readLine())!=null) {
								System.out.println("下棋的消息"+message);
								String [] gamemsg=message.split("\\|");
								if(gamemsg.length==4) {
									if(gamemsg[3].equals(" 重新开始")) {
										System.out.println("点击了重新开始");
										game.touchrestart();
									}else if(gamemsg[3].equals(" 悔棋")) {
										System.out.println("点击了悔棋");
										if(gamemsg[0].equals("来自")) {
											game.getgoback();
										}
									}
								}
								else if(gamemsg.length>4){
									System.out.println("得到坐标");
									System.out.println(message);
									String []xy=message.split("\\|");
									int x,y;
									x=Integer.parseInt(xy[4]);
									y=Integer.parseInt(xy[5]);
									game.setchess(x, y);
								}
								else if(gamemsg.length==3){
									System.out.println("得到坐标");
									System.out.println(message);
									String []xy=message.split("\\|");
									int x,y;
									x=Integer.parseInt(xy[1]);
									y=Integer.parseInt(xy[2]);
									game.setchess(x, y);
								}
							}
						}
						//-----------------------------------------
						String [] str=message.split("\\|");
						for(String s:str) {
							messageArea.append(s);	
						}
						messageArea.append("\n");
					}
				}
			}
			catch(Exception e) {
				try {
					sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}	
		public boolean canstartgame(String msg) {
			String[] yiqihuiyou=msg.split("\\|");
			if(yiqihuiyou.length!=4)
			{
				return false;
			}
			if(yiqihuiyou[3].equals(" 以棋会友")) {
				return true;
			}else 
			{
				return false;
			}
		}
	}
	
		//标签渲染类
		class CellRenderer implements ListCellRenderer<JLabel>{
			JLabel label;
			public CellRenderer() {
				label=new JLabel();
				label.setBounds(12, 6, 208, 48);
				label.setBackground( new Color(235,242,249));
				label.setOpaque(true);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("微软雅黑", Font.BOLD, 20));
				label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235,242,249)));
			}
			@Override
			public Component getListCellRendererComponent(JList<? extends JLabel> list, JLabel label, int index,
					boolean isSelected, boolean cellHasFocus) {
				this.label.setText(label.getText());
				label=this.label;
					if(isSelected) 
						label.setBackground(new Color(0,183,238));
					else  
						label.setBackground(new Color(235,242,249));
					
				return label;
			}
		}
		//列表事件监听
		class CellListener implements ListSelectionListener{

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					JList<JLabel> temp=(JList<JLabel>) e.getSource();
					System.out.println("私聊："+temp.getSelectedValue().getText()); 
					try {
						writer.println("&PCHAT&");  //向服务器端发送私聊请求
						writer.flush();
						writer.println(temp.getSelectedValue().getText()); //向服务器端发送要私聊的id
						System.out.println(temp.getSelectedValue().getText());
						writer.flush();
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		//窗口变化监听
		class WindowChangeLinstener implements ComponentListener {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {
				 Component comp = e.getComponent ();
	             winX=comp.getX ();
	             winY=comp.getY ();
			}
			public void componentResized(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
		}
	public static void main(String[] args) {
		cc=new ChatClient();
		cc.login();
	}
}
