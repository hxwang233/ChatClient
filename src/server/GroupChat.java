package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
public class GroupChat extends Chat{
//	static Map<String,PrintWriter> Gnetmap;
//	Socket sock;
//	String id;
	static int index=0;   //当前加入群聊的人数
	

	public GroupChat(Socket clientSocket,String id) {
		super(clientSocket,id);
	}
	
	public void GChat() throws IOException{
		index++;		
		if(index==1) {  //第一个人加入群聊 创建map记录id->PrintWriter(socket)
			netmap=new HashMap<String,Socket>();
		}
//		PrintWriter writer=new PrintWriter(this.mySock.getOutputStream());
		netmap.put(id, this.mySock);		//添加记录		
		Thread t2=new Thread(new GroupChatHandler(this.mySock));
		t2.start();
	}
	
	public class GroupChatHandler implements Runnable{  //处理该用户群聊的线程类
		BufferedReader reader;
		Socket sock;
		public GroupChatHandler(Socket clientSocket) {
			try {
				sock=clientSocket;
				InputStreamReader isReader=new InputStreamReader(sock.getInputStream());
				reader=new BufferedReader(isReader);			
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String message;	
			String uid;
			try {
				while((message=reader.readLine())!=null) {
					if(message.equals("&LOGOFF&")) {
						Update ud=new Update();
						ud.logoffupdate(netmap, id);
						sock.shutdownInput();
						sock.shutdownOutput();
						sock.close();				
						break;
					}
					else if(message.equals("&BACKGCHAT&")) {  //什么也不做
						message=null;
					}
					else if(message.equals("&PCHAT&")) {
						if((uid=reader.readLine())!=null) {
							PrivateChat PC=new PrivateChat(sock,id,uid);
							PC.PChat();
							break; //?????????不知道将来会不会有问题
						}
					}
					else {  //处理群聊信息
						System.out.println("read:"+message);
						tellEveryOne(id+"： "+message);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}	
	}
	public void tellEveryOne(String message) {
		for(Socket temp:netmap.values()) {
			try {
				PrintWriter writer=new PrintWriter(temp.getOutputStream());
				writer.println(message);
				writer.flush();
				UserDaoImpl udi=new UserDaoImpl();
				udi.AddGChatHistory(message);  //将群里记录存入用户本地数据库
															//这里会有问题 
															//因为测试的时候服务器经常关闭，所以会清
															//historyCount变量（数据库主键）会清零
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
