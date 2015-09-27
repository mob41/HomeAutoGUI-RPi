package com.rpi.homeautogui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/*
 *  Home Automation Graphics User Interface Program
 *  Designed for Raspberry Pi TFT Touchscreen 480x320
 *  
 *  Programmed by Anthony Law.
 *  
 *  Copyright (c) 2015 Anthony Law. DO NOT COPY.
 */
public class MainScreen {

	//Window fullscreen settings. Follow step1,2 to unfullscreen
	// Set this to false to Un-Fullscreen (Step 1)
	public static boolean windowUndecorated = true;
	// Set this to JFrame.NORMAL (Default: JFrame MAXIMIZED_BOTH;) (Step 2)
	public static int windowExtended = JFrame.MAXIMIZED_BOTH;
	
	static JFrame MainScreenFrame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		//Print Description
		System.out.println("=========================================");
		System.out.println("Home Automation Graphics User Interface Program");
		System.out.println("Designed for Raspberry Pi TFT Touchscreen 480x320");
		System.out.println("");
		System.out.println("Programmed by Anthony Law.");
		System.out.println("");
		System.out.println("Copyright (c) 2015 Anthony Law. DO NOT COPY.");
		System.out.println("=========================================");
		
		//Launch TCPServer for SOCKET INPUT (Thread)
		com.rpi.hoau_socketmodules.SocketServer.main();
		
		//Place Background to X-Windows OpenBox (feh)
		try {
			Runtime.getRuntime().exec("feh --bg-center /home/pi/normal.png");
		} catch (IOException e1) {
			System.err.print("X-BG: Could not run 'feh' to place background to Xorg!" + "\n");
		}
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					MainScreen window = new MainScreen();
					window.MainScreenFrame.setVisible(true);
				} catch (Exception e) {
					System.err.print("GUI: Could not build window. Graphics error?");
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainScreen() {
		MainScreenStart();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void MainScreenStart() {
		MainScreenFrame = new JFrame();
		MainScreenFrame.setBounds(100, 100, 480, 320);
		MainScreenFrame.setUndecorated(windowUndecorated);
		MainScreenFrame.setExtendedState(windowExtended);
		MainScreenFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			MainScreenFrame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource( "image/ha_bg.fw.png")))));
		}
		catch(IOException e){
			
		}
		JButton btnLight = new JButton("");
		btnLight.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				LightMenu.LightMenuStart();
				MainScreenFrame.setVisible(false);
			}
		});
		btnLight.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_light.fw.png")));
		
		JButton btnScheduledTask = new JButton("");
		btnScheduledTask.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_sche.fw.png")));
		
		JButton btnMusicSystem = new JButton("");
		btnMusicSystem.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_music.fw.png")));
		
		JButton btnAlarm = new JButton("");
		btnAlarm.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_alarm.fw.png")));
		
		JButton btnSettings = new JButton("");
		btnSettings.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_setting.fw.png")));
		
		JButton btnAdvanced = new JButton("");
		btnAdvanced.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_advanced.fw.png")));
		btnAdvanced.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				AdvancedMenu.AdvancedMenuStart();
				MainScreenFrame.setVisible(false);
			}
		});
		GroupLayout groupLayout = new GroupLayout(MainScreenFrame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(44)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAlarm, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLight, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addGap(47)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnSettings, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnScheduledTask, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addGap(47)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnAdvanced, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnMusicSystem, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(54, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnMusicSystem, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnScheduledTask, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnLight, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAlarm, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSettings, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnAdvanced, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(49, Short.MAX_VALUE))
		);
		MainScreenFrame.getContentPane().setLayout(groupLayout);
	}
	
	public static void MainScreenVisible(){
		MainScreenFrame.setLocationRelativeTo(null);
		MainScreenFrame.setVisible(true);
	}
}
