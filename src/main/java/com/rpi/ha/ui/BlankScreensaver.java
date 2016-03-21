package com.rpi.ha.ui;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BlankScreensaver {

	public static boolean screensaver = false;
	public static JFrame frame;

	public static void start() {
		if (screensaver){
			return;
		}
		screensaver = true;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BlankScreensaver window = new BlankScreensaver();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public BlankScreensaver() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				screensaver = false;
			}
		});
		frame.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				screensaver = false;
				frame.dispose();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				screensaver = false;
				frame.dispose();
			}
		});
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				screensaver = false;
				frame.dispose();
			}
		});
		frame.setBounds(100, 100, 480, 320);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");
		
		frame.getContentPane().setCursor(blankCursor);
		
		JPanel blank = new JPanel();
		blank.setBackground(Color.BLACK);
		frame.getContentPane().add(blank, BorderLayout.CENTER);
		
	}

}
