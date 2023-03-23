package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import server.GroupChat.GroupChatHandler;

public class Update {
	static int index=0;
	static ArrayList<PrintWriter> list;
	String updateList=null;
	Socket sock;
	public Update() {
		
	}
	public Update(Socket clientSocket,String str) {
		sock=clientSocket;
		updateList=str;
	}
	
	public void loginupdate(){
		index++;
		if(index==1) {
			list=new ArrayList<PrintWriter>();
		}
		try {
			PrintWriter writer = new PrintWriter(sock.getOutputStream());
			list.add(writer);	
		} catch (IOException e1) {
			e1.printStackTrace();
		}		
		ArrayList<PrintWriter> temp=new ArrayList<PrintWriter>();
		temp.addAll(list);
		temp.remove(temp.size()-1);
		Iterator<PrintWriter> it=list.iterator();
		System.out.println(list);
		while(it.hasNext()) {
			try {
				PrintWriter pw=(PrintWriter)it.next();
				pw.println("UPDATELIST");
				pw.flush();
				pw.println(updateList);
				pw.flush();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void logoffupdate(Map<String,Socket> allsock,String id) {
		allsock.remove(id);
		StringBuffer updata=new StringBuffer();
		for(String temp:allsock.keySet()) {
			updata.append(temp+"#");
		}
		for(Socket sock:allsock.values()) {
			try {
				PrintWriter pw=new PrintWriter(sock.getOutputStream());
				pw.println("UPDATELIST");
				pw.flush();
				pw.println(updata.toString());
				pw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
