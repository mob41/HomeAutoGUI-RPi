package com.rpi.homeautogui;

import java.awt.EventQueue;
import com.rpi.hoau_socketmodules.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class LightMenu {

	JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void LightMenuStart() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LightMenu window = new LightMenu();
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
	public LightMenu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setUndecorated(MainScreen.windowUndecorated);
		frame.setExtendedState(MainScreen.windowExtended);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource( "image/ha_bg.fw.png")))));
		}
		catch(IOException e){
			
		}
		JButton btnLight1 = new JButton("");
		btnLight1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (SocketServer.switchStatus1 == 0)
				{
					System.out.println("LM: Sending switchOn to Module 1");
					SocketClient.client("switchOn", Conf.module1ip, Conf.module1port, 1000);
				}
				else
				{
					System.out.println("LM: Sending switchOff to Module 1");
					SocketClient.client("switchOff", Conf.module1ip, Conf.module1port, 1000);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					System.err.println("THREAD: Unable to start thread to sleep 1000. Java problem?");
				}
				if (SocketServer.switchStatus1 == 1){
					btnLight1.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_light1_on.fw.png")));
				}
				else
				{
					btnLight1.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_light1_off.fw.png")));
				}
				
			}
		});
		SocketClient.getStatus(); //Get Switches' status
		if (SocketServer.switchStatus1 == 0)
		{
			btnLight1.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_light1_off.fw.png")));
		}
		else
		{
			btnLight1.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_light1_on.fw.png")));
		}
		
		JButton btnLight2 = new JButton("2");
		
		JButton btnLight3 = new JButton("3");
		
		JButton btnLight4 = new JButton("4");
		
		JButton btnBackToMenu = new JButton("");
		btnBackToMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				MainScreen.MainScreenVisible();
				frame.dispose();
			}
		});
		btnBackToMenu.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_backtomenu.fw.png")));
		
		JButton btnLight5 = new JButton("5");
		
		JButton btnLight6 = new JButton("6");
		
		JButton btnLight7 = new JButton("7");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnLight5, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLight6, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLight7, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnBackToMenu, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnLight1, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLight2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLight3, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(btnLight4, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(18, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(52, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnLight3, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnLight4, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnLight2, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnLight1, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnLight5, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLight6, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLight7, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBackToMenu, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
					.addGap(54))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
