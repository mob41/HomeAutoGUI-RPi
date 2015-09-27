package com.rpi.homeautogui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class AdvancedMenu extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void AdvancedMenuStart() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdvancedMenu frame = new AdvancedMenu();
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
	public AdvancedMenu() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setUndecorated(MainScreen.windowUndecorated);
		setExtendedState(MainScreen.windowExtended);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		try
		{
			setContentPane(new JLabel(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource( "image/ha_bg.fw.png")))));
		}
		catch(IOException e){
			
		}
		JButton btnBackToMenu = new JButton("");
		btnBackToMenu.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_backtomenu.fw.png")));
		btnBackToMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				MainScreen.MainScreenVisible();
				dispose();
			}
		});
		
		JButton btnRelayModule = new JButton("");
		btnRelayModule.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_relaymodule.fw.png")));
		btnRelayModule.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				RelayModuleStatus.RelayModuleStatusStart();
			}
		});
		
		
		JButton btnExitToBASH = new JButton("");
		btnExitToBASH.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_exittobash.fw.png")));
		btnExitToBASH.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("killall xinit");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		JButton btnReboot = new JButton("");
		btnReboot.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_reboot.fw.png")));
		btnReboot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Dialog.DialogStart();
				try {
					Runtime.getRuntime().exec("sudo reboot");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnHalt = new JButton("");
		btnHalt.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_halt.fw.png")));
		btnHalt.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Dialog.DialogStart();
				try {
					Runtime.getRuntime().exec("sudo halt");
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnExitToOB = new JButton("");
		btnExitToOB.setIcon(new ImageIcon(ClassLoader.getSystemResource( "image/icn_exittoob.fw.png")));
		btnExitToOB.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("killall java");
				} catch (IOException e1) {
				
					e1.printStackTrace();
				}
			}
		});
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(44)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnReboot, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBackToMenu, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addGap(47)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnHalt, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRelayModule, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addGap(47)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnExitToOB, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnExitToBASH, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(51, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap(54, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnExitToBASH, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRelayModule, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBackToMenu, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnReboot, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnHalt, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnExitToOB, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(49, Short.MAX_VALUE))
		);
		getContentPane().setLayout(groupLayout);
	}

}
