package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import server.PrivateChat.PrivateChatHandler;

public class Chat {  //Ϊ�����ṩ��ͬ�Ĺ��췽��  
	static Map<String,Socket> netmap;  //Ⱥ������˽�����๲��  ����ͬһ��
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
