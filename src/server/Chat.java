package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.PrivateChat.PrivateChatHandler;

public class Chat {  //为子类提供不同的构造方法  
	static Map<String,Socket> netmap;  //群聊子类私聊子类共用  保持同一性
	Socket mySock;
	String id;
	String uid;
	public Chat(Socket mySock,String id) {
		this.mySock=mySock;
		this.id=id;
	}
	public Chat(Socket mySock,String id,String uid){
		this.mySock=mySock;
		this.id=id;
		this.uid=uid;
	}
}
