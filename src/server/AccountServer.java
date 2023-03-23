package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JTextArea;


public class AccountServer {
	
	public AccountServer(int port,JTextArea textArea) {

		AccountServerThread thread=new AccountServerThread(textArea);
		thread.runServer(port);
	}
	class AccountServerThread extends Thread{
		private ServerSocket serverSocket;
		private JTextArea textArea;
		
		public AccountServerThread(JTextArea textArea){
			  this.textArea=textArea;
		}
		/**
		 * 开启服务器
		 */
		public void runServer(int port) {
			try {
				serverSocket = new ServerSocket(port);
			}catch (IOException e1) {
				e1.printStackTrace();
			}   
			Thread t = new Thread(this);
			t.start();
		}
		/**
		 * 服务器线程
		 */
		public void run(){
			while(true){
				try{
					textArea.append("账号服务器：\n等待远程连接，端口号为："+ serverSocket.getLocalPort() + "...\n");
					Socket server = serverSocket.accept();
					
					textArea.append("发送消息，又创了一个线程...\n");
					new AccountThread(server,textArea);//创建线程
					
		     		textArea.setCaretPosition(textArea.getText().length());//信息定位到末尾
		         }catch(SocketTimeoutException s){
		        	 			textArea.append("Socket timed out!\n");
		        	 break;
		         }catch(IOException e){
		            e.printStackTrace();
		            break;
		         }
		      }
		}
	}
}
