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

import server.GroupChat.GroupChatHandler;

public class PrivateChat extends Chat{   //和群聊共用一个Map
//	static int index=0;
//	static Map<String,PrintWriter> netmap;
//	Socket mySock;
//	String uid;
//	String id;
	public PrivateChat(Socket mySock,String id,String uid) {
		super(mySock,id,uid);
	}
	public void PChat() throws IOException{	
/*		
 		index++;
		if(index==1) {
			netmap=new HashMap<String,PrintWriter>();
		}
		PrintWriter writer=new PrintWriter(mySock.getOutputStream());
		netmap.put(id, writer);
*/
		Thread pt=new Thread(new PrivateChatHandler(mySock));
		pt.start();
	}
	public class PrivateChatHandler implements Runnable{	 //处理该用户私聊的线程类
		BufferedReader reader;
		Socket sock;
		public PrivateChatHandler(Socket clientSocket) {
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
					else if(message.equals("&BACKGCHAT&")) {    //判断是否返回群聊
						GroupChat GC=new GroupChat(sock,id);    
						GC.GChat();
						break; //?????????不知道将来会不会有问题
					}
					else if(message.equals("&PCHAT&")) {  //判断是否私聊其他人
						uid=reader.readLine();
					}
					else {   //处理私聊信息
						System.out.println("read:"+message);
						tellOnlyOne(message);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public void tellOnlyOne(String message) {
//		String[] mes=message.split("#");
//		String uid=mes[0];
//		String word=mes[1];
		String aim=null;  //群聊目标id
		for(String key:netmap.keySet()) {
			if(key.equals(this.uid)) {
				aim=key;
			}
		}
		if(aim!=null) {
			PrintWriter pw1;
			try {
				pw1 = new PrintWriter(netmap.get(this.id).getOutputStream());	
				pw1.println("私聊 |"+this.uid+"|：| "+message);
				if(!aim.equals(this.id)) {  //判断是否私聊自己
					PrintWriter pw2=new PrintWriter(netmap.get(aim).getOutputStream());;
					pw2.println("来自|"+this.id+"|的私聊：| "+message);
					pw2.flush();			
				}	
				pw1.flush();
			} catch (IOException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	}
}


