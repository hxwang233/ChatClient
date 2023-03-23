package client;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Register extends JFrame{
	AccountSocket socket=null;
	private JTextField account;
	private JPasswordField password;
	private JPasswordField confirm;
	private JTextField answer_1;
	private JTextField answer_2;
	private String[] questions;
	private int question1_selected=0,question2_selected=1;
	
	QuestionComboBox question1,question2;
	
	JLabel label_message_account;
	JLabel label_message_password;
	JLabel label_message_confirm;
	JLabel label_answer_message_1;
	JLabel label_answer_message_2;
	
	public Register() {
//		questions=new String[] {"1","2","3","4","5","6","7","8","9"};
		
		questions=new String[] {"你的生日是:","你的班级是:","你家小猫的名字是:",
				"你家小狗的名字是:","你的小学老师的名字是:","你的初中老师的名字是:",
				"你的高中老师是:","你的高中同学的名字是:","你喜欢的国家的是:"};
		this.createForm();

	}
	/**登录*/
	public void doRegister() {
		boolean canlogin=isCanRegister();
		String taccount=account.getText();
		String tconfirm=String.valueOf(confirm.getPassword());
		String tanswer1=answer_1.getText();
		String tanswer2=answer_2.getText();
		if(canlogin) {
			socket=new AccountSocket(8114);//本地访问
			//socket=new AccountSocket("3s.dkys.org",28989);//外网访问
			socket.sendRequest("REGISTER#account="+taccount+"&password="+tconfirm+
					"&answer1ID="+question1_selected+"&answer1="+tanswer1+
					"&answer2ID="+question2_selected+"&answer2="+tanswer2);
			String response=socket.getResponse();
			System.out.println(response);
			if(response.equals("exist")) {
				label_message_account.setText("用户已存在");
				label_message_account.setVisible(true);
			}else if(response.equals("success")) {
				JOptionPane.showMessageDialog(null, "注册成功！");
			}
			socket.closeClientSocket();
		}

	}
	/**界面创建*/
	private void createForm() {
		Dimension scrSize=Toolkit.getDefaultToolkit().getScreenSize(); 
		getContentPane().setBackground(new Color(235,242,249));
		getContentPane().setLayout(null);
		this.setTitle("用户注册");
		this.setBounds((int)(scrSize.getWidth()-578)/2+20,(int)(scrSize.getHeight()-479)/2+20, 628, 518);
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

		JLabel label_title = new JLabel("用户注册");
		label_title.setFont(new Font("微软雅黑", Font.BOLD, 36));
		label_title.setBounds(242, 12, 155, 40);
		getContentPane().add(label_title);
	//用户名
		JLabel label_account = new JLabel("用户名");
		label_account.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_account.setBounds(129, 78, 72, 18);
		getContentPane().add(label_account);
		
		account = new JTextField();
		account.setBounds(202, 76, 258, 24);
		getContentPane().add(account);
		account.setColumns(10);
		
		label_message_account = new JLabel("该用户名已存在");
		label_message_account.setVisible(false);
		label_message_account.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_message_account.setBounds(478, 78, 113, 18);
		getContentPane().add(label_message_account);
	//密码
		JLabel label_password = new JLabel("输入密码");
		label_password.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_password.setBounds(129, 108, 72, 18);
		getContentPane().add(label_password);
		
		password = new JPasswordField();
		password.setBounds(202, 107, 258, 24);
		getContentPane().add(password);
		
		label_message_password = new JLabel("密码不得为空");
		label_message_password.setVisible(false);
		label_message_password.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_message_password.setBounds(478, 108, 113, 18);
		getContentPane().add(label_message_password);
	//确认密码
		JLabel label_confirm = new JLabel("确认密码");
		label_confirm.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_confirm.setBounds(129, 140, 72, 18);
		getContentPane().add(label_confirm);
		
		confirm = new JPasswordField();
		confirm.setBounds(202, 138, 258, 24);
		getContentPane().add(confirm);
		
		label_message_confirm = new JLabel("");
		label_message_confirm.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_message_confirm.setBounds(478, 140, 141, 18);
		getContentPane().add(label_message_confirm);
	//密保问题1
		JLabel label_secretSecurity_1 = new JLabel("请选择密保问题");
		label_secretSecurity_1.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_secretSecurity_1.setBounds(129, 196, 127, 18);
		getContentPane().add(label_secretSecurity_1);
		
		question1 = new QuestionComboBox(questions,1);
		question1.setBounds(250, 194, 210, 24);
		question1.removeItemAt(question2_selected);
		getContentPane().add(question1);
		question1.addItemListener(new comboBoxLinstener());

		
		answer_1 = new JTextField();
		answer_1.setBounds(119, 230, 341, 24);
		getContentPane().add(answer_1);
		answer_1.setColumns(10);
		
		label_answer_message_1 = new JLabel("答案不得为空");
		label_answer_message_1.setVisible(false);
		label_answer_message_1.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_answer_message_1.setBounds(478, 232, 127, 18);
		getContentPane().add(label_answer_message_1);
	//密保问题2
		JLabel label_secretSecurity_2 = new JLabel("请选择密保问题");
		label_secretSecurity_2.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_secretSecurity_2.setBounds(129, 266, 127, 18);
		getContentPane().add(label_secretSecurity_2);
		
		question2 = new QuestionComboBox(questions,2);
		question2.setBounds(250, 266, 210, 24);
		question2.removeItemAt(question1_selected);
		getContentPane().add(question2);
		question2.addItemListener(new comboBoxLinstener());

		
		answer_2 = new JTextField();
		answer_2.setBounds(119, 302, 341, 24);
		getContentPane().add(answer_2);
		answer_2.setColumns(10);
		
		label_answer_message_2 = new JLabel("答案不得为空");
		label_answer_message_2.setVisible(false);
		label_answer_message_2.setFont(new Font("微软雅黑", Font.BOLD, 12));
		label_answer_message_2.setBounds(478, 304, 127, 18);
		getContentPane().add(label_answer_message_2);
	//按钮
		JButton register = new JButton("立即注册");
		register.setFont(new Font("微软雅黑", Font.BOLD, 12));
		register.setBounds(119, 366, 341, 34);
		getContentPane().add(register);
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doRegister();
			}
		});
		JButton cancel = new JButton("取消");
		cancel.setFont(new Font("微软雅黑", Font.BOLD, 12));
		cancel.setBounds(119, 412, 341, 34);
		getContentPane().add(cancel);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				if(!(socket==null)) {
					if(!socket.isClosed())
						socket.closeClientSocket();
				}
			}
		});
		System.out.println(question1_selected+","+question2_selected);
		this.setVisible(true);
	}
	/**
	 * combobox事件监听
	 */
	class comboBoxLinstener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			if(e.getStateChange()==ItemEvent.SELECTED) {
				QuestionComboBox temp=(QuestionComboBox)e.getSource();
				int index=temp.getSelectedIndex();
				temp.updateComboBoxItemList(index);
				System.out.println(question1_selected+","+question2_selected);
			}
		}
		
	}
	/**
	 * 下拉菜单类
	 */
	class QuestionComboBox extends JComboBox<String>{
		private int id;
		public QuestionComboBox(String[] question,int comboBoxId) {
			super(question);
			this.id=comboBoxId;
		}
		public int getComboBoxId() {
			return id;
		}
		/**获取当前combobox选中的index*/
		public int getComboBoxSelectedItemIndex() {
			if(id==1) {
				return question1_selected;
			}else {
				return question2_selected;
			}
		}
		/**刷新两列表值*/
		public void updateComboBoxItemList(int selected) {
			int index=selected;
			if(id==1) {
				if(selected>=question2_selected)
					index=selected+1;
				question2.insertItemAt(questions[question1_selected], question1_selected);
				question2.removeItemAt(index);
				question1_selected=index;
				if(question1_selected<question2_selected)
					question2_selected++;
				if(index<question2_selected)
					question2_selected--;
			}else {
				if(selected>=question1_selected)
					index=selected+1;
				question1.insertItemAt(questions[question2_selected], question2_selected);
				question1.removeItemAt(index);
				question2_selected=index;
				if(question2_selected<question1_selected)
					question1_selected++;
				if(index<question1_selected)
					question1_selected--;
			}
			
		}
	}
	/**判断是否可以注册,所有条件是否满足*/
	public boolean isCanRegister() {
		boolean canreg=true;
		boolean baccount=true,bpassword=true,bconfirm=true,banswer1=true,banswer2=true;
		String taccount=account.getText();
		String tpassword=String.valueOf(password.getPassword());
		String tconfirm=String.valueOf(confirm.getPassword());
		System.out.println(taccount+","+tpassword+","+tconfirm);
		if(taccount.equals("")) {//用户名不为空
			label_message_account.setText("用户名不得为空");
			label_message_account.setVisible(true);
			baccount=false;
		}else {
			label_message_account.setVisible(false);
			baccount=true;
		}
		if(tpassword.equals("")) {//密码不为空
			label_message_password.setVisible(true);
			bpassword=false;
		}else {
			label_message_password.setVisible(false);
			bpassword=true;
			if(tconfirm.equals("")) {//确认密码不为空
				label_message_confirm.setText("请再次确认密码");
				label_message_confirm.setVisible(true);
				bconfirm=false;
			}else {
				if(!tpassword.equals(tconfirm)) {//两次密码相同
					label_message_confirm.setText("两次输入的密码不同");
					label_message_confirm.setVisible(true);
					bconfirm=false;
				}else {
					label_message_confirm.setVisible(false);
					bconfirm=true;
				}
			}
		}
		//密保不为空
		if(answer_1.getText().equals("")) {
			label_answer_message_1.setVisible(true);
			banswer1=false;
		}else {
			label_answer_message_1.setVisible(false);
			banswer1=true;
		}
		if(answer_2.getText().equals("")) {
			label_answer_message_2.setVisible(true);
			banswer2=false;
		}else {
			label_answer_message_2.setVisible(false);
			banswer2=true;
		}
		canreg=baccount&&bpassword&&bconfirm&&banswer1&&banswer2;
		System.out.println(canreg);
		return canreg;
	}
	
}
