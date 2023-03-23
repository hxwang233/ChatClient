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
	static boolean flag=false;  //��¼���
	static boolean tip=false;
	StringBuffer info;  //��ȡ�û��˺ź����� 
	String id;
	String pwd;
	JFrame frame;
	JTextField loginname;  
	JPasswordField password;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	Thread t;  //�ӷ��������ܵ�¼�ɹ���Ϣ���߳�   ��¼�ɹ����ж�
	
	
	int winX=0;
	int winY=0;
	JList<JLabel> userlist;//����û������б�
	JTextArea messageArea;
	JTextArea sendArea;
	DefaultListModel<JLabel> model;
	JFrame tip_frame ;
	public void login() {  //��¼
		
		frame = new JFrame("��¼");
		
		//��������
			Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
			frame.getContentPane().setBackground(new Color(235,242,249));
			frame.getContentPane().setLayout(null);
			frame.setBounds((int)(scrSize.getWidth()-389)/2,(int)(scrSize.getHeight()-562)/2, 389, 562);
			winX=(int)(scrSize.getWidth()-960)/2;//��ʼ�����촰�ڵ�λ��
			winY=(int)(scrSize.getHeight()-768)/2;//��ʼ�����촰�ڵ�λ��
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
		//�˺ſ�
			loginname = new JTextField();
			loginname.setFont(new Font("΢���ź�", Font.PLAIN, 17));
			loginname.setBounds(75, 238, 239, 35);
			frame.getContentPane().add(loginname);
			loginname.setColumns(10);
		//�����
			password = new JPasswordField();
			password.setFont(new Font("΢���ź�", Font.PLAIN, 19));
			password.setBounds(75, 285, 239, 35);
			frame.getContentPane().add(password);

		//ע���˺ű�ǩ
			JLabel label_2 = new JLabel("ע���˺�");
			label_2.setFont(new Font("΢���ź�", Font.PLAIN, 12));
			label_2.setBounds(75, 332, 72, 18);
			label_2.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					new Register();
				}
			});
			frame.getContentPane().add(label_2);
		//�һ������ǩ
			JLabel label_3 = new JLabel("�һ�����");
			label_3.setFont(new Font("΢���ź�", Font.PLAIN, 12));
			label_3.setBounds(260, 332, 54, 18);
			label_3.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					new Retrieve();
				}
			});
			frame.getContentPane().add(label_3);
		
		//��¼��
			JButton btnNewButton = new JButton("��¼");
			btnNewButton.setForeground(Color.WHITE);
			btnNewButton.setFont(new Font("΢���ź�", Font.BOLD, 16));
			btnNewButton.setBackground(new Color(0,163,255));
			btnNewButton.addActionListener(new loginButtonListener());//��Ӽ����¼�
			btnNewButton.setBounds(75, 362, 239, 35);
			frame.getContentPane().add(btnNewButton);
			frame.setVisible(true);
			
		setUpNetworking();   //��������
		t=new Thread(new LoginStatus());
		t.start();
		
	}
	public void failLogin(){
		tip_frame = new JFrame();
		tip_frame.setBounds(100, 100, 363, 253);

		
		JPanel tip_panel = new JPanel();
		tip_panel.setBounds(10, 10, 328, 196);
		tip_frame.getContentPane().add(tip_panel);
		
		JButton tipButton = new JButton("ȷ��");
		tipButton.setBounds(120, 123, 87, 23);
		tip_panel.setLayout(null);
		tip_panel.add(tipButton);
		tipButton.addActionListener(new tip());
		
		JLabel tipLabel = new JLabel("�˺Ż��������");
		tipLabel.setForeground(Color.RED);
		tipLabel.setFont(new Font("΢���ź�", Font.PLAIN, 20));
		tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tipLabel.setBounds(52, 43, 220, 48);
		tip_panel.add(tipLabel);
		
	//	tip_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tip_frame.getContentPane().setLayout(null);
		tip_frame.setVisible(true);
	}

	public void go(String [] list) {  //���������� ��ʼ����
		
		frame = new JFrame("������ - ���id��"+id);
		
		//��������
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
		
		//��������б���
				JScrollPane chatListScrollPane = new JScrollPane();
				chatListScrollPane.setBounds(12, 73, 232, 648);
				frame.getContentPane().add(chatListScrollPane);
				userlist = new JList<>();
				chatListScrollPane.setViewportView(userlist);
				
				ListCellRenderer<JLabel> lcr=new CellRenderer();//������Ⱦ��
				model= new DefaultListModel<>();
				userlist.setModel(model);
				userlist.setFixedCellHeight(60);
				userlist.setCellRenderer(lcr);//�����Ⱦ��
				userlist.addListSelectionListener(new CellListener());//�����¼�����
		
				for(String temp:list) {
					if(temp.equals(id))
						continue;
					else 
						model.addElement(new JLabel(temp));//����б�Ԫ��
				}
		
				JButton sendButton = new JButton("����");
				sendButton.setBounds(837, 685, 105, 36);
				frame.getContentPane().add(sendButton);
				sendButton.addActionListener(new SendButtonListener());
				
				JButton backbutton = new JButton("����");
				backbutton.setBounds(715, 685, 105, 36);
				frame.getContentPane().add(backbutton);
				backbutton.addActionListener(new BackButtonListener());
				
		//����������ʾ����
				JScrollPane ChatMessageScrollPane = new JScrollPane();
				ChatMessageScrollPane.setBounds(256, 73, 686, 454);
				frame.getContentPane().add(ChatMessageScrollPane);
				messageArea = new JTextArea();
				ChatMessageScrollPane.setViewportView(messageArea);
		
		//������Ϣ�������
				JScrollPane sendMessageScrollPane = new JScrollPane();
				sendMessageScrollPane.setBounds(256, 539, 686, 144);
				frame.getContentPane().add(sendMessageScrollPane);
				
				sendArea = new JTextArea();
				sendMessageScrollPane.setViewportView(sendArea);
				
				messageArea.setLineWrap(true);
				messageArea.setWrapStyleWord(true);
				messageArea.setEditable(false);
				
		HistoryImpl HImpl=new HistoryImpl();    //��ȡ��ʷ��¼
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
	
	private void setUpNetworking() {  //��������˽�������
		try {
			sock=new Socket("127.0.0.1",8113);
			InputStreamReader isreader=new InputStreamReader(sock.getInputStream());
			reader=new BufferedReader(isreader);
			writer=new PrintWriter(sock.getOutputStream());
			System.out.println("���ӳɹ�");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
/*	private void setUpNetworking() {  //��������˽�������
		try {
			InetAddress inet=InetAddress.getByName("3s.dkys.org");
			sock = new Socket(inet, 28989);
			InputStreamReader isreader=new InputStreamReader(sock.getInputStream());
			reader=new BufferedReader(isreader);
			writer=new PrintWriter(sock.getOutputStream());
			System.out.println("���ӳɹ�");
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
					writer.println("LOG");  //���͵�¼����
					writer.println(info);   //�����Լ����˺����뵽����������֤
					writer.flush();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				loginname.setText("");    //���
				loginname.requestFocus(); //���»�ý���
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
				if(flag) {//����¼�ɹ�
					if((init=reader.readLine())!=null) {  //�ӷ������˻�ȡ��ǰ�Ѿ���¼��id
						initList=init.split("#");
					}
					frame.dispose();
					cc.go(initList); //��ʼ������
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
//					if(message.equals("UPDATELIST")) {  //�������û���¼ ʵʱ���������б�
//						System.out.println("read��"+message);
//						while((message=reader.readLine())!=null) {  
//							updatelist=message.split("#");
//							frame.dispose();
//							cc.go(updatelist);
//							break;
//						}
//						break;
//					}
//					else {	
//						System.out.println("read��"+message);
//						messageArea.append(message+"\n");
//						messageArea.setCaretPosition(messageArea.getText().length());
//					}
//				}
//			}
			try {
				while((message=reader.readLine())!=null) {
					if(message.equals("UPDATELIST")) {  //�������û���¼ ʵʱ���������б�
						System.out.println("read��"+message);
						while((message=reader.readLine())!=null) {  
							updatelist=message.split("#");
							frame.dispose();
							cc.go(updatelist);	 //ʵʱ����
							break;
						}
						break;
					}
					else {	
						System.out.println("===========");
						System.out.println("read��"+message);
						//-----------------------------------------����
						if(canstartgame(message))
						{   
							game=new StartChessJFrame(sock);
							game.setVisible(true);
							while((message=reader.readLine())!=null) {
								System.out.println("�������Ϣ"+message);
								String [] gamemsg=message.split("\\|");
								if(gamemsg.length==4) {
									if(gamemsg[3].equals(" ���¿�ʼ")) {
										System.out.println("��������¿�ʼ");
										game.touchrestart();
									}else if(gamemsg[3].equals(" ����")) {
										System.out.println("����˻���");
										if(gamemsg[0].equals("����")) {
											game.getgoback();
										}
									}
								}
								else if(gamemsg.length>4){
									System.out.println("�õ�����");
									System.out.println(message);
									String []xy=message.split("\\|");
									int x,y;
									x=Integer.parseInt(xy[4]);
									y=Integer.parseInt(xy[5]);
									game.setchess(x, y);
								}
								else if(gamemsg.length==3){
									System.out.println("�õ�����");
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
			if(yiqihuiyou[3].equals(" �������")) {
				return true;
			}else 
			{
				return false;
			}
		}
	}
	
		//��ǩ��Ⱦ��
		class CellRenderer implements ListCellRenderer<JLabel>{
			JLabel label;
			public CellRenderer() {
				label=new JLabel();
				label.setBounds(12, 6, 208, 48);
				label.setBackground( new Color(235,242,249));
				label.setOpaque(true);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setFont(new Font("΢���ź�", Font.BOLD, 20));
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
		//�б��¼�����
		class CellListener implements ListSelectionListener{

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()) {
					JList<JLabel> temp=(JList<JLabel>) e.getSource();
					System.out.println("˽�ģ�"+temp.getSelectedValue().getText()); 
					try {
						writer.println("&PCHAT&");  //��������˷���˽������
						writer.flush();
						writer.println(temp.getSelectedValue().getText()); //��������˷���Ҫ˽�ĵ�id
						System.out.println(temp.getSelectedValue().getText());
						writer.flush();
					}catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		//���ڱ仯����
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
