package server;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.borland.dbswing.*;
import com.borland.jbcl.layout.*;
import com.borland.dx.sql.dataset.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Frame1 extends JFrame {
  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  TableScrollPane tableScrollPane1 = new TableScrollPane();
  JdbNavToolBar jdbNavToolBar1 = new JdbNavToolBar();
  TableScrollPane tableScrollPane2 = new TableScrollPane();
  JdbTable jdbTable1 = new JdbTable();
  Database database1 = new Database();
  QueryDataSet queryDataSet1 = new QueryDataSet();
  DBExceptionHandler dBExceptionHandler1;
  JOptionPane jOptionPane1 = new JOptionPane();
  JdbTextArea jdbTextArea1 = new JdbTextArea();
  JButton jButton1 = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  //Construct the frame
  public Frame1() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  //Component initialization
  private void jbInit() throws Exception  {
    contentPane = (JPanel) this.getContentPane();
    dBExceptionHandler1 = DBExceptionHandler.getInstance();;
    contentPane.setLayout(borderLayout1);
    this.setLocale(java.util.Locale.getDefault());
    this.setResizable(false);
    this.setSize(new Dimension(754, 511));
    this.setTitle("Frame Title");
    jPanel1.setLayout(gridBagLayout1);
    jPanel2.setLayout(borderLayout2);
    database1.setConnection(new com.borland.dx.sql.dataset.ConnectionDescriptor("jdbc:mysql://localhost:3306/qquser?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false", "root", "WHX@YWX", false, "com.mysql.cj.jdbc.Driver"));
    jButton1.setText("excute");
    jButton1.addActionListener(new listener());
    jdbTextArea1.setText("");
    jdbTable1.setLocale(java.util.Locale.getDefault());
    jdbTable1.setVisible(false);
    jPanel1.add(tableScrollPane1,  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(16, 18, 0, 0), 0, 105));
    jPanel1.add(jButton1,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 9, 81, 16), 102, 19));
    tableScrollPane1.getViewport().add(jdbTextArea1, null);
    contentPane.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jdbNavToolBar1, BorderLayout.NORTH);
    jPanel2.add(tableScrollPane2, BorderLayout.CENTER);
    tableScrollPane2.getViewport().add(jdbTable1, null);
    contentPane.add(jPanel1, BorderLayout.NORTH);
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }
  class listener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String strSQL = jdbTextArea1.getText();
	     jdbTable1.setVisible(false);
	     if (strSQL.toLowerCase().trim().startsWith("select")) {
	       try {
	         queryDataSet1.close();
	         queryDataSet1.setQuery(new com.borland.dx.sql.dataset.QueryDescriptor(database1, strSQL, null,true, Load.ALL));
	         jdbNavToolBar1.setDataSet(queryDataSet1);
	         jdbTable1.setDataSet(queryDataSet1);
	         jdbTable1.setVisible(true);
	         queryDataSet1.open();
	     }
	     catch(Exception ex1){
	       DBExceptionHandler.handleException(ex1);
	     }
	   }
	    else{
	      try{
	        int count = database1.executeStatement(strSQL);
	        JOptionPane.showInputDialog(this, "Excuse Successfully");
	      }
	      catch(Exception ex2){
	       DBExceptionHandler.handleException(ex2);
	      }
	     }
	    }
	}
	  
  
}