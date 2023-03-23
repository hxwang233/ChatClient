package server;

import java.util.*;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import javax.swing.UIManager;

import server.ChatServer.ClientLogin;
import server.ChatServer.go;
public class ChatServer extends JFrame{
	static int index=0;
	static boolean flag=false;
	Socket sock;
	static Map<String,String> init;
	static Map<String,Socket> allsock;
	JTextArea showInfo;//信息显示区域
	boolean packFrame = false;
	public ChatServer() {
		Frame1 frame = new Frame1();
		showInfo = new JTextArea();

		if (packFrame) {
			frame.pack();
		}
		else {
			frame.validate();
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);
/*		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
		getContentPane().setBackground(new Color(235,242,249));
		getContentPane().setLayout(null);
		this.setTitle("服务器窗口");
		this.setBounds((int)(scrSize.getWidth()/2)-360,(int)(scrSize.getHeight()/2)-240, 960, 720);
		this.setResizable(false);
		showInfo = new JTextArea();
		showInfo.setBounds(14, 164, 926, 508);
		JScrollPane JSPta=new JScrollPane(showInfo);
		JSPta.setBounds(14, 164, 926, 508);
		getContentPane().add(JSPta);
		this.setVisible(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				}
		});*/
	}
	
	class ClientLogin implements Runnable{
		BufferedReader logreader;
		PrintWriter logwriter;
		public ClientLogin(Socket clientSocket) {
			try {
				sock=clientSocket;
				InputStreamReader isReader=new InputStreamReader(sock.getInputStream());
				logreader=new BufferedReader(isReader);
				logwriter=new PrintWriter(sock.getOutputStream());
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String info=null;	
			String id=null;
			String pwd=null;
			try {
				String command=logreader.readLine();
				if(command.equals("LOG")) {
					if((info=logreader.readLine())!=null) {
						System.out.println(info);
						//数据库比对
						try {
							String[] user=info.split("/");
							id=user[0];
							pwd=user[1];				
							UserDaoImpl login=new UserDaoImpl();
							flag=login.loginCheck(id, pwd);
						//	showInfo.append(id+"已登录");
						//	showInfo.setCaretPosition(showInfo.getText().length());
						}catch(Exception e){
							flag=false;
						}
							
					}
					if(flag) {	
						index++;
						if(index==1) {
							allsock=new HashMap<String,Socket>();
						}			
						allsock.put(id, sock);
						StringBuffer str=new StringBuffer();  //存储当前在线id
						
						for(String temp:allsock.keySet()) {
							str.append(temp+"#");
						}				
						logwriter.println("OK");
						logwriter.flush();
						String strinfo=str.toString();
						System.out.println(strinfo);
						logwriter.println(strinfo);   //初始化该用户列表
						logwriter.flush();
						Update ud=new Update(sock,strinfo);   //告诉其他用户有列表更新
						ud.loginupdate();
						flag=false;	
						GroupChat GC=new GroupChat(sock,id);
						GC.GChat();	
						if(!Chat.netmap.isEmpty()) {
							allsock=Chat.netmap;
						}
					}
					else {
						logwriter.println("FAIL");
						logwriter.flush();
					}
				}
				if(command.equals("&NO&")) {
					sock.shutdownInput();
					sock.shutdownOutput();
					sock.close();
				}
			} catch (IOException e1) {
				try {	
					sock.close();
				} catch (IOException e) {
					
				}
				
				
			}			
		}	
	}
	
	class go implements Runnable{
		
		@Override
		public void run() {
			try {
				ServerSocket serversock=new ServerSocket(8113);
				while(true) {
					Socket clientSocket=serversock.accept();		
					Thread t1=new Thread(new ClientLogin(clientSocket));
					t1.start();
				}
			}catch(Exception e) {
				e.printStackTrace();
			}	
			
		}
		
	}
	public void go() {
		new AccountServer(8114, showInfo);//创建账号服务器
		Thread t=new Thread(new go());
		t.start();
	}

	public static void main(String[] args) {
		try {
		      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		    }catch(Exception e) {
		      e.printStackTrace();
		}
		ChatServer cs=new ChatServer();
		cs.go();	
	}

}
