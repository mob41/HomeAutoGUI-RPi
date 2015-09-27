package com.rpi.homeautogui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.rpi.hoau_socketmodules.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

@SuppressWarnings("serial")
public class RelayModuleStatus extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void RelayModuleStatusStart() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RelayModuleStatus frame = new RelayModuleStatus();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public RelayModuleStatus() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 320);
		setUndecorated(MainScreen.windowUndecorated);
		setExtendedState(MainScreen.windowExtended);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JLabel lblModule1 = new JLabel("Module 1");
		lblModule1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		
		JLabel lblStatus = new JLabel("Status: Testing Connection...");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 15));
		
		JButton btnTestConnection = new JButton("Test Connection / Reconnect");
		btnTestConnection.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				btnTestConnection.setEnabled(false);
				lblStatus.setText("Status: Testing Connection...");
				String status = SocketClient.client("askConnection", "192.168.168.124", 7979, 1000);
				switch (status){
				case "systembusy":
					lblStatus.setText("Status: System Busy");
					break;
				case "connectionok":
					lblStatus.setText("Status: Connected.");
					break;
				case "error":
					lblStatus.setText("Status: Couldn't connect. Network error.");
					break;
				}
				btnTestConnection.setEnabled(true);
			}
		});
		btnTestConnection.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				btnTestConnection.setEnabled(false);
				lblStatus.setText("Status: Testing Connection...");
				String status = SocketClient.client("getLightStatus", "192.168.168.124", 7979, 1000);
				switch (status){
				case "systembusy":
					lblStatus.setText("Status: System Busy");
					break;
				case "connectionok":
					lblStatus.setText("Status: Connected.");
					break;
				case "error":
					lblStatus.setText("Status: Couldn't connect. Network error.");
					break;
				}
				btnTestConnection.setEnabled(true);
			}
		});
		btnTestConnection.setVisible(false);
		btnTestConnection.setVisible(true);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dispose();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(174)
							.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(26)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnTestConnection, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblModule1)
									.addGap(40)
									.addComponent(lblStatus)))))
					.addContainerGap(162, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(52)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblModule1)
						.addComponent(lblStatus))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnTestConnection, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
					.addComponent(btnExit, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
					.addGap(44))
		);
		contentPane.setLayout(gl_contentPane);
	}
}