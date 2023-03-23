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
		 * ����������
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
		 * �������߳�
		 */
		public void run(){
			while(true){
				try{
					textArea.append("�˺ŷ�������\n�ȴ�Զ�����ӣ��˿ں�Ϊ��"+ serverSocket.getLocalPort() + "...\n");
					Socket server = serverSocket.accept();
					
					textArea.append("������Ϣ���ִ���һ���߳�...\n");
					new AccountThread(server,textArea);//�����߳�
					
		     		textArea.setCaretPosition(textArea.getText().length());//��Ϣ��λ��ĩβ
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
