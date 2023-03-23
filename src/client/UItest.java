package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;

public class UItest {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UItest window = new UItest();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UItest() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 363, 253);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 10, 328, 196);
		frame.getContentPane().add(panel);
		
		JButton tipButton = new JButton("\u786E\u8BA4");
		tipButton.setBounds(120, 123, 87, 23);
		panel.setLayout(null);
		panel.add(tipButton);
		
		JLabel tipLabel = new JLabel("\u8D26\u53F7\u6216\u5BC6\u7801\u9519\u8BEF");
		tipLabel.setForeground(Color.RED);
		tipLabel.setFont(new Font("Î¢ÈíÑÅºÚ", Font.PLAIN, 20));
		tipLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tipLabel.setBounds(52, 43, 220, 48);
		panel.add(tipLabel);
	}
}
