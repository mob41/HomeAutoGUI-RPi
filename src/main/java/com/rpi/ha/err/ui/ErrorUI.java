package com.rpi.ha.err.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.rpi.ha.err.ErrorHandler;

import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;

public class ErrorUI {

	private JFrame frame;
	private JLabel lblOut;
	private int sec = 30;
	private ActionListener clock = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			sec--;
			lblOut.setText("System will automatically restart in " + sec + " sec");
			if (sec <= 0){
				btnRestart.setEnabled(false);
				btnCancel.setEnabled(false);
				lblOut.setText("Restarting");
				try {
					Runtime.getRuntime().exec("sudo reboot");
				} catch (IOException e1){
					e1.printStackTrace();
				}
			}
		};
	
	};
	private Timer timer = new Timer(1000, clock );
	private JButton btnRestart;
	private JButton btnCancel;

	/**
	 * Launch the application.
	 */
	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ErrorUI window = new ErrorUI();
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
	public ErrorUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Error");
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		
		JLabel lblicon = new JLabel("");
		lblicon.setHorizontalAlignment(SwingConstants.CENTER);
		lblicon.setIcon(new ImageIcon(ErrorUI.class.getResource("/image/warning.png")));
		
		JLabel lblErr = new JLabel("Network Error");
		lblErr.setHorizontalAlignment(SwingConstants.CENTER);
		lblErr.setFont(new Font("Tahoma", Font.BOLD, 28));
		
		
		lblOut = new JLabel("System will automatically restart in 30 sec");
		lblOut.setHorizontalAlignment(SwingConstants.CENTER);
		lblOut.setFont(new Font("Tahoma", Font.PLAIN, 21));
		
		btnRestart = new JButton("Restart");
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("sudo reboot");
				} catch (IOException e1){
					e1.printStackTrace();
				}
			}
		});
		btnRestart.setFont(new Font("Tahoma", Font.BOLD, 30));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ErrorHandler.getThread().start();
				frame.dispose();
			}
		});
		btnCancel.setFont(new Font("Tahoma", Font.BOLD, 30));
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addComponent(lblicon, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				.addComponent(lblErr, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				.addComponent(lblOut, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnRestart, GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(lblicon)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblErr)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblOut)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnCancel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnRestart, GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
					.addContainerGap(38, Short.MAX_VALUE))
		);
		frame.getContentPane().setLayout(groupLayout);
		timer.start();
	}
}
