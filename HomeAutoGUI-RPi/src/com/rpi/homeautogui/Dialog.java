package com.rpi.homeautogui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;

@SuppressWarnings("serial")
public class Dialog extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void DialogStart() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Dialog frame = new Dialog();
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
	public Dialog() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 320);
		setUndecorated(MainScreen.windowUndecorated);
		setExtendedState(MainScreen.windowExtended);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblStatus = new JLabel("Shutting down...");
		lblStatus.setFont(new Font("Tahoma", Font.PLAIN, 25));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(142)
					.addComponent(lblStatus)
					.addContainerGap(143, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(135)
					.addComponent(lblStatus)
					.addContainerGap(144, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
