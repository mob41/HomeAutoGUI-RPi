package com.rpi.ha.ui.entertain;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rpi.ha.plugin.InstallationReader;

public class EntertainMenu {

	private static final Logger logger = LogManager.getLogger(EntertainMenu.class.getName());
	
	private Object[][] col;
	private String[] row = {"Logo", "Name"};
	private JFrame frame;
	private JTable table;

	private JButton btnStart;

	public static void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EntertainMenu window = new EntertainMenu();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EntertainMenu() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setBounds(100, 100, 480, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		int status = InstallationReader.findNumberOfPlugin();
		if (!(status > 0))
		{
			col = new Object[1][2];
			col[0][0] = new ImageIcon(EntertainMenu.class.getResource("/image/warning.png"));
			if (status == -1){
				col[0][1] = "Installation File is not generated.";
				logger.warn("No installation file detected.");
			}
			else
			{
				col[0][1] = "No Plugin Found";
				logger.info("No plugins found.");
			}
		}
		else
		{
			col = new Object[status][2];
			for (int i = 0; i < status; i++){
				String iconPath = System.getProperty("user.dir") + "/" + InstallationReader.getPluginLogo(i);
				col[i][0] = new ImageIcon(iconPath);
				col[i][1] = InstallationReader.getPluginName(i);
			}
		}
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setEnabled(false);
		splitPane.setResizeWeight(0.9);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setEnabled(false);
		splitPane_1.setResizeWeight(0.5);
		splitPane.setRightComponent(splitPane_1);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnStart.setText("Running...");
				btnStart.setEnabled(false);
				int selected = table.getSelectedRow();
				String fileName = InstallationReader.getPluginFileName(selected);
				if (fileName == null){
					logger.error("The user selected nothing.");
					JOptionPane.showMessageDialog(null, "The plugin you selected is not accessable."
							+ " / No item is selected.", "Error", JOptionPane.ERROR_MESSAGE);
					btnStart.setText("Start");
					btnStart.setEnabled(true);
					return;
				}
				String command = "java -jar " + System.getProperty("user.dir") + "/" + fileName;
				try {
					Runtime.getRuntime().exec(command).waitFor();
				} catch (IOException e1) {
					logger.error("Runtime error! No java? I am running on Java!");
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					logger.warn("The program was interrupted.");
					e1.printStackTrace();
				}
				btnStart.setText("Start");
				btnStart.setEnabled(true);
			}
		});
		splitPane_1.setLeftComponent(btnStart);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			frame.dispose();
			}
		});
		splitPane_1.setRightComponent(btnClose);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		DefaultTableModel model = new DefaultTableModel(col, row);
		table = new JTable(model){
			private static final long serialVersionUID = 1L;

			public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };
        table.setRowHeight(128);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
		scrollPane.setViewportView(table);
		frame.getContentPane().setLayout(groupLayout);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnSoftwareCenter = new JMenu("Software Center");
		menuBar.add(mnSoftwareCenter);
		
		JMenuItem mntmInstallNewSoftware = new JMenuItem("Install New Software");
		mnSoftwareCenter.add(mntmInstallNewSoftware);
		
		JMenuItem mntmRefreshInstallation = new JMenuItem("Refresh installation");
		mnSoftwareCenter.add(mntmRefreshInstallation);
		
	}
	
}
