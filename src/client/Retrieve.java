package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;

import javax.swing.JButton;
import javax.swing.JPasswordField;

public class Retrieve extends JFrame{
	AccountSocket socket;
	private String[] questions;
	private String responseData=null;
	DataList dataList;
	
	private JPanel page1;
	private JPanel page2;
	private JPanel page3;
	private JPanel page4;
	private JTextField account;
	private JButton confirm;
	private JButton cancel;
	private JLabel lblNewLabel2;
	private JLabel page2_label_1;
	private JLabel page2_question1;
	private JTextField answer1;
	private JLabel page2_label_2;
	private JLabel page2_question2;
	private JTextField answer2;
	private JButton page2_confirm;
	private JButton page2_cancel;

	private JPasswordField password;
	private JPasswordField password_confirm;
	private JLabel lblNewLabel;
	private JButton page4_confirm;
	private JLabel account_tip;
	private JLabel answer_message;
	private JLabel password_message;
	private JLabel confirm_message;
	public Retrieve() {
		questions=new String[] {"你的生日是:","你的班级是:","你家小猫的名字是:",
				"你家小狗的名字是:","你的小学老师的名字是:","你的初中老师的名字是:",
				"你的高中老师是:","你的高中同学的名字是:","你喜欢的国家的是:"};
		socket=new AccountSocket(8114);//本地访问
		//socket=new AccountSocket("3s.dkys.org",28989);//外网访问
		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
		getContentPane().setBackground(new Color(235,242,249));
		getContentPane().setLayout(null);
				

				



//第一页面
				page1 = new JPanel();
				page1.setBounds(0, 0, 562, 431);
				getContentPane().add(page1);
				page1.setLayout(null);
				
				JLabel label = new JLabel("找回密码");
				label.setFont(new Font("微软雅黑", Font.BOLD, 30));
				label.setBounds(217, 43, 133, 40);
				page1.add(label);
				
				JLabel label_1 = new JLabel("输入账号");
				label_1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				label_1.setBounds(137, 162, 109, 22);
				page1.add(label_1);
				
				account = new JTextField();
				account.setBounds(137, 196, 292, 32);
				page1.add(account);
				account.setColumns(10);
				
				account_tip = new JLabel("账号不存在！");
				account_tip.setFont(new Font("微软雅黑", Font.PLAIN, 14));
				account_tip.setBounds(345, 240, 84, 22);
				page1.add(account_tip);
				account_tip.setVisible(false);
				
				confirm = new JButton("确认");
				confirm.setFont(new Font("微软雅黑", Font.BOLD, 16));
				confirm.setBounds(137, 287, 292, 40);
				page1.add(confirm);
				confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						searchAccount(account.getText());
					}
				});
				
				cancel = new JButton("返回");
				cancel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
				cancel.setBounds(137, 339, 292, 32);
				page1.add(cancel);
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
						if(!(socket==null)) {
							if(!socket.isClosed())
								socket.closeClientSocket();
						}
					}
				});
				
//第二页面
				page2 = new JPanel();
				page2.setBounds(0, 0, 562, 431);
				getContentPane().add(page2);
				page2.setLayout(null);
				page2.setVisible(false);
				
				lblNewLabel2 = new JLabel("输入密保问题");
				lblNewLabel2.setFont(new Font("微软雅黑", Font.BOLD, 18));
				lblNewLabel2.setBounds(219, 43, 123, 41);
				page2.add(lblNewLabel2);
				
				page2_label_1 = new JLabel("问题1:");
				page2_label_1.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page2_label_1.setBounds(118, 128, 74, 31);
				page2.add(page2_label_1);
				
				page2_question1 = new JLabel("");
				page2_question1.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				page2_question1.setBounds(185, 128, 270, 31);
				page2.add(page2_question1);
				
				answer1 = new JTextField();
				answer1.setBounds(118, 171, 312, 26);
				page2.add(answer1);
				answer1.setColumns(10);
				
				page2_label_2 = new JLabel("问题2:");
				page2_label_2.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page2_label_2.setBounds(118, 209, 55, 25);
				page2.add(page2_label_2);
				
				page2_question2 = new JLabel("");
				page2_question2.setFont(new Font("微软雅黑", Font.PLAIN, 16));
				page2_question2.setBounds(185, 209, 258, 25);
				page2.add(page2_question2);
				
				answer2 = new JTextField();
				answer2.setBounds(118, 246, 312, 26);
				page2.add(answer2);
				answer2.setColumns(10);
				
				page2_confirm = new JButton("确认");
				page2_confirm.setBounds(118, 337, 123, 41);
				page2.add(page2_confirm);
				page2_confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						validateSecretSecurityAnswer();
					}
				});
				
				page2_cancel = new JButton("返回");
				page2_cancel.setBounds(307, 337, 123, 41);
				page2.add(page2_cancel);
				page2_cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						page2.setVisible(false);
						page1.setVisible(true);
					}
				});
				page2.setBackground(new Color(235,242,249));
				
				answer_message = new JLabel("密保回答错误！");
				answer_message.setFont(new Font("微软雅黑", Font.BOLD, 16));
				answer_message.setBounds(307, 284, 123, 26);
				page2.add(answer_message);
				answer_message.setVisible(false);
				
//第三页面
				page3 = new JPanel();
				page3.setBounds(0, 0, 562, 431);
				getContentPane().add(page3);
				page3.setLayout(null);
				page3.setVisible(false);
				
				JLabel lblNewLabel3 = new JLabel("更改密码");
				lblNewLabel3.setFont(new Font("微软雅黑", Font.BOLD, 22));
				lblNewLabel3.setBounds(238, 63, 98, 40);
				page3.add(lblNewLabel3);
				
				JLabel page3_label_1 = new JLabel("新密码:");
				page3_label_1.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page3_label_1.setBounds(97, 161, 53, 44);
				page3.add(page3_label_1);
				
				password = new JPasswordField();
				password.setBounds(168, 171, 246, 28);
				page3.add(password);
				
				password_message = new JLabel("密码不得为空");
				password_message.setFont(new Font("微软雅黑", Font.BOLD, 12));
				password_message.setBounds(423, 173, 105, 23);
				page3.add(password_message);
				password_message.setVisible(false);
				
				JLabel page3_label_2 = new JLabel("确认密码:");
				page3_label_2.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page3_label_2.setBounds(81, 203, 69, 40);
				page3.add(page3_label_2);
				
				password_confirm = new JPasswordField();
				password_confirm.setBounds(168, 211, 246, 28);
				page3.add(password_confirm);
				
				confirm_message = new JLabel("请再次确认密码");
				confirm_message.setFont(new Font("微软雅黑", Font.BOLD, 12));
				confirm_message.setBounds(423, 216, 127, 18);
				page3.add(confirm_message);
				confirm_message.setVisible(false);
				
				JButton page3_confirm = new JButton("确认修改");
				page3_confirm.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page3_confirm.setBounds(168, 287, 105, 43);
				page3.add(page3_confirm);
				page3_confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						modifyPassword();
					}
				});
				
				JButton page3_cancel = new JButton("取消");
				page3_cancel.setFont(new Font("微软雅黑", Font.BOLD, 16));
				page3_cancel.setBounds(309, 286, 105, 44);
				page3.add(page3_cancel);
				page3_cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						page3.setVisible(false);
						page2.setVisible(true);
						password.setText("");
						password.setText("");
					}
				});
				page3.setBackground(new Color(235,242,249));
						
//第四页面
				page4 = new JPanel();
				page4.setBounds(0, 0, 562, 431);
				getContentPane().add(page4);
				page4.setLayout(null);
				page4.setVisible(false);
				
				lblNewLabel = new JLabel("密码更改成功！");
				lblNewLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
				lblNewLabel.setBounds(183, 83, 225, 83);
				page4.add(lblNewLabel);
				
				page4_confirm = new JButton("返回登录页面");
				page4_confirm.setFont(new Font("微软雅黑", Font.BOLD, 22));
				page4_confirm.setBounds(184, 223, 198, 67);
				page4.add(page4_confirm);
				page4_confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				
				
		this.setTitle("找回密码");
		this.setBounds((int)(scrSize.getWidth()-578)/2+20,(int)(scrSize.getHeight()-479)/2+20, 568, 466);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				if(!(socket==null)) {
					if(!socket.isClosed())
						socket.closeClientSocket();
				}
			}
		});
		page1.setBackground(new Color(235,242,249));
		page4.setBackground(new Color(235,242,249));
		this.setVisible(true);
	}
	/**查找账号是否存在*/
	public void searchAccount(String account) {
		dataList=new DataList();
		if(account.equals("")) {
			account_tip.setText("不得为空！");
			account_tip.setVisible(true);
		}else {
			socket.sendRequest("RETRIEVE_Search#account="+account);
			String response=socket.getResponse();
			if(response.equals("exist")) {
				page1.setVisible(false);
				page2.setVisible(true);
				account_tip.setVisible(false);
				responseData=socket.getResponse();//获取用户信息
				dataList=getDataList(responseData);
				dataList.printData();//储存用户信息
				showSecretSecurity();//转到第二页面时显示密保问题
			}else {
				account_tip.setVisible(true);
			}
		}
	}
	/**显示密保问题*/
	public void showSecretSecurity() {
		int answer1ID=Integer.parseInt(dataList.getDataNamed("answer1ID"));
		int answer2ID=Integer.parseInt(dataList.getDataNamed("answer2ID"));
		page2_question1.setText(questions[answer1ID]);
		page2_question2.setText(questions[answer2ID]);
	}
	/**验证密保问题*/
	public void validateSecretSecurityAnswer() {
		boolean canModify=false;
		if(answer1.getText().equals(dataList.getDataNamed("answer1"))) {
			if(answer2.getText().equals(dataList.getDataNamed("answer2"))) {
				canModify=true;
			}
		}
		if(canModify) {
			answer1.setText("");
			answer2.setText("");
			answer_message.setVisible(false);
			page2.setVisible(false);
			page3.setVisible(true);
		}else {
			answer_message.setVisible(true);
		}
	}
	/**修改密码*/
	public void modifyPassword() {
		String tpassword=String.valueOf(password.getPassword());
		String tconfirm=String.valueOf(password_confirm.getPassword());
		if(tpassword.equals("")) {
			password_message.setVisible(true);
		}else {
			password_message.setVisible(false);
			if(tconfirm.equals("")) {
				confirm_message.setText("请再次确认密码");
				confirm_message.setVisible(true);
			}else {
				confirm_message.setVisible(false);
				if(tpassword.equals(tconfirm)) {
					socket.sendRequest("MODIFYPASSWORD#account="+account.getText()+"&password="+tconfirm);
					String response=socket.getResponse();
					if(response.equals("OK")) {
						page3.setVisible(false);
						page4.setVisible(true);
					}
				}else {
					confirm_message.setText("两次密码不符");
					confirm_message.setVisible(true);
				}
			}
		}
		
	}
	

	/**
	 * 获取请求数据列表
	 */
	public DataList getDataList(String requestData) {
		DataList dataList=new DataList();
		String dataName;
		String data;
		int to=0,from=0;
			to=requestData.indexOf("#");
			String inst=requestData.substring(from, to);//获取指令
			dataList.addParameter("inst", inst);
		while(to<requestData.length()) {

			from=to+1;
			to=requestData.indexOf("=",from);
			if(to==-1) {
				to=requestData.length();
			}
			dataName=requestData.substring(from, to);//获取参数名
			
			from=to+1;
			to=requestData.indexOf("&",from);
			if(to==-1) {
				to=requestData.length();
			}
			data=requestData.substring(from,to);//获取数据
			dataList.addParameter(dataName, data);
		}
		return dataList;
	}
	/**
	* 数据列表类
	*/
	class DataList {
		class DataStruct{
			private String name;
			private String data;
			public void setName(String name) {this.name=name;}
			public void setData(String data) {this.data=data;}
			public String getName() {return name;}
			public String getData() {return data;}
		}
		private List<DataStruct> datalist=new ArrayList<DataStruct>();
		/**
		 * 存入数据
		 * @param name 数据名
		 * @param data 具体数据
		 */
		public void addParameter(String name,String data) {
			DataStruct dataStruct=new DataStruct();
			dataStruct.setName(name);
			dataStruct.setData(data);
			datalist.add(dataStruct);
		}
		/**
		 * 根据数据名获取数据内容
		 * @param name
		 * @return data，null
		 */
		public String getDataNamed(String name) {
			Iterator<DataStruct> t=datalist.iterator();
			while(t.hasNext()) {
				DataStruct ds=t.next();
				if(ds.getName().equals(name)) {
					return ds.getData();
				}
			}
			return null;
		}
		/**
		 * 查看存入数据
		 */
		public void printData() {
			Iterator<DataStruct> t=datalist.iterator();
			while(t.hasNext()) {
				DataStruct ds=t.next();
				System.out.println("参数名："+ds.getName()+"数据："+ds.getData());
			}
		}
	}

	
}
