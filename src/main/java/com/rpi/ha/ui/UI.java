package com.rpi.ha.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rpi.ha.Conf;
import com.rpi.ha.err.ErrorHandler;
import com.rpi.ha.scene.SceneThread;
import com.rpi.ha.scene.SceneSave;
import com.rpi.ha.ui.entertain.EntertainMenu;
import com.rpi.ha.widget.HKOweather;
import com.rpi.ha.widget.KmbApi;
import com.rpi.ha.widget.TimeFormatting;
import com.rpi.ha.widget.ToastMessage;

public class UI {
	
	private static final Logger logger = LogManager.getLogger(UI.class.getName());

	public boolean timedNoScreenSaver = false;
	public static DefaultTableModel busArrTimeModel = new DefaultTableModel();
	private static final String[] colIdent = {"Bus No.", "Location", "Arrive in", "Remaining"};
	public JFrame frame;
	private static JLabel lblWeatherimg;
	public static JLabel lbltemp = new JLabel("");
	private static Timer timer1;
	private Timer timer2;
	private static Timer timer3;
	private JLabel lbldate;
	private JLabel lbltime;
	private static Timer timer4;
	private static Timer timer5;
	public static boolean undecorated = true;
	public static int extendedState = JFrame.MAXIMIZED_BOTH;
	
	private static boolean updatingArrTime = false;

	public static void main() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new UI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public UI() {
		initialize();
	}
	
	private ActionListener updateWeather = new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			updateWeatherFun();
		}
	};
	
	private ActionListener busArrTimeUpdate = new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			updateArrTime();
		}
	};
	
	private ActionListener screensaver = new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			runScreensaver();
		}
	};
	
	private void runScreensaver(){
		if (timedNoScreenSaver){
			lblAutoStopScreensaver.setText("Auto Stop Screensaver (6:00 - 22:30): " + Boolean.toString(timedNoScreenSaver));
			if (BlankScreensaver.screensaver){
				try {
					BlankScreensaver.frame.dispose();
				} catch (NullPointerException ignore) {}
			}
		}
		else
		{
			lblAutoStopScreensaver.setText("Auto Stop Screensaver (6:00 - 22:30): " + Boolean.toString(timedNoScreenSaver));
			BlankScreensaver.start();
		}
	}
	
	private ActionListener updateTime = new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			if (BlankScreensaver.screensaver){
				timer4.stop();
			} else {
				if (!timer4.isRunning()){
					timer4.start();
				}
			}
			
			lbltime.setText(TimeFormatting.getFormattedTime(Conf.hour24));
			lbldate.setText(TimeFormatting.getFormattedDate());
			lblAutoStopScreensaver.setText("Auto Stop Screensaver: " + Boolean.toString(timedNoScreenSaver));
		}
	};

	public static SceneThread sceneRun;
	private JLabel lblNtSS;
	private JLabel lblAutoStopScreensaver;
	private static JScrollPane busArrscroll;
	public static JTable busArrTimeTable;
	public static UI window;
	public JTable scenetable;
	private JTextField fldSceneName;
	private JPanel striggertime;
	private JPanel addnewscenepanel;
	private JComboBox<?> boxSceneAction;
	private JComboBox<?> boxSceneTrigger;
	private JPanel scenetriggerinput;
	private JComboBox<?> boxStartTimeMin;
	private JComboBox<?> boxStartTimeHr;
	private JComboBox<?> boxEndTimeHr;
	private JComboBox<?> boxEndTimeMin;
	private JSplitPane tableActionsplit;
	public DefaultTableModel scenetablemodel;
	private static int oldvalue = 0;
	public static boolean UIstarted = false;
	private static boolean scrollflip = false;
	public JTabbedPane tab;

	private ActionListener autoscroll = new ActionListener(){

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int value = busArrscroll.getVerticalScrollBar().getValue();
			if (!scrollflip){
				int newvalue = value + 15;
				if (newvalue <= oldvalue){
					busArrscroll.getVerticalScrollBar().setValue(busArrscroll.getVerticalScrollBar().getMinimum());
					oldvalue = 0;
					//scrollflip = true;
					return;
				}
				oldvalue = newvalue;
				if (newvalue < busArrscroll.getVerticalScrollBar().getMaximum()){
					busArrscroll.getVerticalScrollBar().setValue(newvalue);
				} else {
					busArrscroll.getVerticalScrollBar().setValue(busArrscroll.getVerticalScrollBar().getMinimum());
				}
			} else {
				int newvalue = value - 15;
				if (newvalue >= oldvalue){
					busArrscroll.getVerticalScrollBar().setValue(busArrscroll.getVerticalScrollBar().getMaximum());
					oldvalue = busArrscroll.getVerticalScrollBar().getMaximum();
					scrollflip = false;
					return;
				}
				oldvalue = newvalue;
				System.out.println(newvalue);
				System.out.println(busArrscroll.getVerticalScrollBar().getMaximum());
				if (newvalue > busArrscroll.getVerticalScrollBar().getMinimum()){
					busArrscroll.getVerticalScrollBar().setValue(newvalue);
				} else {
					busArrscroll.getVerticalScrollBar().setValue(busArrscroll.getVerticalScrollBar().getMinimum());
				}
			}
			
		}
		
	};
	
	private void changeToAddNewScenePanel(){
		fldSceneName.setText("");
		boxSceneAction.setSelectedIndex(0);
		boxSceneTrigger.setSelectedIndex(0);
		striggertime.setVisible(true);
		boxStartTimeMin.setSelectedIndex(0);
		boxStartTimeHr.setSelectedIndex(0);
		boxEndTimeMin.setSelectedIndex(0);
		boxEndTimeHr.setSelectedIndex(0);
		tableActionsplit.setVisible(false);
		addnewscenepanel.setVisible(true);
	}
	
	private void changeBackToSceneTablePanel(){
		fldSceneName.setText("");
		boxSceneAction.setSelectedIndex(0);
		boxSceneTrigger.setSelectedIndex(0);
		striggertime.setVisible(true);
		boxStartTimeMin.setSelectedIndex(0);
		boxStartTimeHr.setSelectedIndex(0);
		boxEndTimeMin.setSelectedIndex(0);
		boxEndTimeHr.setSelectedIndex(0);
		tableActionsplit.setVisible(true);
		addnewscenepanel.setVisible(false);
	}
	
	private static void updateWeatherFun(){
		ToastMessage toast = new ToastMessage("Updating weather...", 2000);
		toast.setVisible(!BlankScreensaver.screensaver);
		new Thread (){
			public void run(){
				if (HKOweather.getWeatherImage() != null)
				{
					lblWeatherimg.setIcon(new ImageIcon(HKOweather.getWeatherImage()));
				}
				int temp = HKOweather.getTemp(23);
				String tempText = temp + "\u00B0C";
				if (temp == 5000)
				{
					//Minus from ErrorAccept
					ErrorHandler.getThread().rejectError();
					tempText = "N/A";
					ToastMessage toast = new ToastMessage("Could not get weather", 2000);
					toast.setVisible(!BlankScreensaver.screensaver);
				}
				lbltemp.setText(tempText);
				window.frame.repaint();
				timer1.restart();
			}
			
		}.start();
	}
	
	private static void adjustColHeight(){
		for (int row = 0; row < busArrTimeTable.getRowCount(); row++)
	    {
	        int rowHeight = busArrTimeTable.getRowHeight();

	        for (int column = 0; column < busArrTimeTable.getColumnCount(); column++)
	        {
	            Component comp = busArrTimeTable.prepareRenderer(busArrTimeTable.getCellRenderer(row, column), row, column);
	            rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
	        }

	        busArrTimeTable.setRowHeight(row, rowHeight);
	    }
	}
		
	private static void adjustColWidth(){

	}
	
	
	private static void updateArrTime(){
		
		if (!updatingArrTime){
			ToastMessage toast = new ToastMessage("Updating bus arrival time...", 2000);
			toast.setVisible(!BlankScreensaver.screensaver);
			new Thread(){
				public void run(){
					updatingArrTime = true;
					
					//busArrTimeModel.setRowCount(0);
					
					//busArrTimeModel.setRowCount(0);
					//busArrTimeModel.addRow(data1);
					//busArrTimeModel.addRow(data2);
					//busArrTimeModel.fireTableDataChanged();
					
					DefaultTableModel newtm = new DefaultTableModel();
					newtm.setColumnIdentifiers(colIdent);
					String[] tablerow;
					KmbApi.getServerTime();
					List<String[]> data1 = KmbApi.getAllETAtableData(KmbApi.ENGLISH_LANG, "PR01W11000");
					List<String[]> data2 = KmbApi.getAllETAtableData(KmbApi.ENGLISH_LANG, "WA13S10000");
					//System.out.println(Arrays.deepToString(data.toArray()));
					for (int i = 0; i < data1.toArray().length; i++){
						tablerow = data1.get(i);
						newtm.addRow(tablerow);
					}
					for (int i = 0; i < data2.toArray().length; i++){
						tablerow = data2.get(i);
						newtm.addRow(tablerow);
					}
					busArrTimeTable.setModel(newtm);
					/*
					Comparator<String> comparator = new Comparator<String>() {
					    public int compare(String s1, String s2) {
					    	
					        int arrive1;
					        int arrive2;
					        try {
					        	arrive1 = Integer.parseInt(s1);
					        } catch (NumberFormatException e){
					        	arrive1 = -1;
					        }
					        try {
					        	arrive2 = Integer.parseInt(s2);
					        } catch (NumberFormatException e){
					        	arrive2 = -1;
					        }
					        return Integer.compare(arrive1, arrive2);
					    	//return s1.compareTo(s2);
					    }
					};
					 TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(newtm);
					 sorter.setComparator(1, comparator);
					 busArrTimeTable.setRowSorter(sorter);
					 */
					//((JXTable) busArrTimeTable).packAll();
					for (int i = 0; i < busArrTimeTable.getColumnCount(); i++) {
					    TableColumn column = busArrTimeTable.getColumnModel().getColumn(i);
					    //Status Colour renderer
					    column.setCellRenderer(getRenderer());
					}
					updatingArrTime = false;
					timer3.restart();
					
					adjustColHeight();
				}
			}.start();
		}
	}
	
	private static void saveConfshowDialog(){
		Conf.writeConf();
		ToastMessage toast = new ToastMessage("Changes saved.", 1500);
		toast.setVisible(!BlankScreensaver.screensaver);
	}
	
	private static TableCellRenderer getRenderer() {
        return new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
                //String remaintime = ((String) value).replaceAll("[^0-9]", "");
                String remainValue = table.getModel().getValueAt(row, 3).toString();
                if (remainValue.equals("Arrived")){
                	tableCellRendererComponent.setBackground(Color.GRAY);
                    tableCellRendererComponent.setForeground(Color.WHITE);
                    return tableCellRendererComponent;
                } else if (remainValue.equals("END") || remainValue.equals("---")){
                	tableCellRendererComponent.setBackground(Color.LIGHT_GRAY);
                    tableCellRendererComponent.setForeground(Color.WHITE);
                    return tableCellRendererComponent;
                }
                String remaintime = remainValue.replaceAll("[^0-9]", "");
                try {
                	int remain = Integer.parseInt(remaintime);
                    if(remain <= -1){
                        tableCellRendererComponent.setBackground(Color.GRAY);
                        tableCellRendererComponent.setForeground(Color.WHITE);
                    } else if(remain <= 5){
                        tableCellRendererComponent.setBackground(Color.RED);
                        tableCellRendererComponent.setForeground(Color.WHITE);
                    } else if(remain <= 15){
                        tableCellRendererComponent.setBackground(Color.ORANGE);
                        tableCellRendererComponent.setForeground(Color.BLACK);
                    } else {
                        tableCellRendererComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                        tableCellRendererComponent.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                    }
                } catch (NumberFormatException e){
                	tableCellRendererComponent.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                	 tableCellRendererComponent.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
                return tableCellRendererComponent;
            }
        };
    }
		  	
	private void initialize() {
		frame = new JFrame();
		frame.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				timer4.restart();
				if (BlankScreensaver.screensaver){
					try {
						BlankScreensaver.frame.dispose();
					} catch (NullPointerException ignore) {}
				}
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				timer4.restart();
				if (BlankScreensaver.screensaver){
					try {
						BlankScreensaver.frame.dispose();
					} catch (NullPointerException ignore) {}
				}
			}
		});
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				timer4.restart();
				if (BlankScreensaver.screensaver){
					try {
						BlankScreensaver.frame.dispose();
					} catch (NullPointerException ignore) {}
				}
			}
		});
		frame.setUndecorated(undecorated);
		frame.setExtendedState(extendedState);
		frame.setBounds(100, 100, 480, 320);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
								
		tab = new JTabbedPane(JTabbedPane.BOTTOM);
		frame.getContentPane().add(tab, BorderLayout.SOUTH);
								
		JPanel light_panel = new JPanel();
		tab.addTab("", new ImageIcon(UI.class.getResource("/image/tabicn_light.fw.png")), light_panel, null);
								
		JPanel settings_panel = new JPanel();
		tab.addTab("", new ImageIcon(UI.class.getResource("/image/tabicn_settings.fw.png")), settings_panel, null);
		GroupLayout gl_settings_panel = new GroupLayout(settings_panel);
		gl_settings_panel.setHorizontalGroup(
			gl_settings_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 475, Short.MAX_VALUE)
		);
		gl_settings_panel.setVerticalGroup(
			gl_settings_panel.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 330, Short.MAX_VALUE)
		);
		settings_panel.setLayout(gl_settings_panel);
								
		JPanel home_panel = new JPanel();
		tab.addTab("", new ImageIcon(UI.class.getResource("/image/tabicn_home.fw.png")), home_panel, null);
		home_panel.setLayout(new BorderLayout(0, 0));
		
		ImagePanel imgbg = new ImagePanel(new ImageIcon(UI.class.getResource("/image/ha_bg.fw.png")).getImage());
		home_panel.add(imgbg);
		
		ImagePanel imgbg2 = new ImagePanel(new ImageIcon(UI.class.getResource("/image/ha_bg.fw.png")).getImage());
		settings_panel.add(imgbg2);
		
		JTabbedPane settingsTab = new JTabbedPane(JTabbedPane.TOP);
		settingsTab.setBounds(12, 94, 456, 214);
		imgbg2.add(settingsTab);
		
		JPanel settings1 = new JPanel();
		settingsTab.addTab("Screensaver", null, settings1, null);
		
		JCheckBox chckbxBlankScreensaverEnabled = new JCheckBox("Blank Screensaver Enabled");
		chckbxBlankScreensaverEnabled.setFont(new Font("Dialog", Font.BOLD, 18));
		chckbxBlankScreensaverEnabled.setSelected(Conf.scrsaver);
		
		JSlider screentimeout = new JSlider();
		screentimeout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				Conf.scrsaver_timeout = screentimeout.getValue();
				saveConfshowDialog();
				timer4.setDelay(0);
				timer4.setInitialDelay((int) TimeUnit.SECONDS.toMillis(Conf.scrsaver_timeout));
				timer4.restart();
			}
		});
		screentimeout.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				lblNtSS.setText("Timeout: " + screentimeout.getValue() + "sec (" + (int) TimeUnit.SECONDS.toMinutes(screentimeout.getValue()) + " mins" + ") " + 
						"(" + TimeUnit.SECONDS.toHours(screentimeout.getValue()) + " hours)");
			}
		});
		screentimeout.setMinimum(15);
		screentimeout.setMaximum(3600);
		screentimeout.setValue(Conf.scrsaver_timeout);
		
		lblNtSS = new JLabel("Timeout: ");
		lblNtSS.setText("Timeout: " + screentimeout.getValue() + "sec (" + (int) TimeUnit.SECONDS.toMinutes(screentimeout.getValue()) + " mins" + ") " + 
		"(" + TimeUnit.SECONDS.toHours(screentimeout.getValue()) + " hours)");
		lblNtSS.setFont(new Font("Dialog", Font.PLAIN, 16));
		
		lblAutoStopScreensaver = new JLabel("Auto Stop Screensaver: " + Boolean.toString(timedNoScreenSaver));
		lblAutoStopScreensaver.setFont(new Font("Dialog", Font.PLAIN, 12));
		
		JButton btnMinusScrTimeout = new JButton("-");
		btnMinusScrTimeout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screentimeout.setValue(screentimeout.getValue() - 1);
				lblNtSS.setText("Timeout: " + screentimeout.getValue() + "sec (" + (int) TimeUnit.SECONDS.toMinutes(screentimeout.getValue()) + " mins" + ") " + 
						"(" + TimeUnit.SECONDS.toHours(screentimeout.getValue()) + " hours)");
				saveConfshowDialog();
			}
		});
		
		JButton btnAddScrTimeout = new JButton("+");
		btnAddScrTimeout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				screentimeout.setValue(screentimeout.getValue() + 1);
				lblNtSS.setText("Timeout: " + screentimeout.getValue() + "sec (" + (int) TimeUnit.SECONDS.toMinutes(screentimeout.getValue()) + " mins" + ") " + 
						"(" + TimeUnit.SECONDS.toHours(screentimeout.getValue()) + " hours)");
				saveConfshowDialog();
			}
		});
		GroupLayout gl_settings1 = new GroupLayout(settings1);
		gl_settings1.setHorizontalGroup(
			gl_settings1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settings1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_settings1.createParallelGroup(Alignment.LEADING)
						.addComponent(screentimeout, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
						.addComponent(chckbxBlankScreensaverEnabled)
						.addComponent(lblNtSS)
						.addComponent(lblAutoStopScreensaver)
						.addGroup(gl_settings1.createSequentialGroup()
							.addComponent(btnMinusScrTimeout)
							.addPreferredGap(ComponentPlacement.RELATED, 291, Short.MAX_VALUE)
							.addComponent(btnAddScrTimeout)))
					.addContainerGap())
		);
		gl_settings1.setVerticalGroup(
			gl_settings1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_settings1.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxBlankScreensaverEnabled)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(screentimeout, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_settings1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnMinusScrTimeout)
						.addComponent(btnAddScrTimeout))
					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
					.addComponent(lblAutoStopScreensaver)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNtSS)
					.addContainerGap())
		);
		settings1.setLayout(gl_settings1);
		
		JPanel clocksetpanel = new JPanel();
		settingsTab.addTab("Clock", null, clocksetpanel, null);
		
		JCheckBox chckbx24Hour = new JCheckBox("24-Hour Clock");
		chckbx24Hour.setSelected(Conf.hour24);
		chckbx24Hour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Conf.hour24 = chckbx24Hour.isSelected();
				saveConfshowDialog();
			}
		});
		chckbx24Hour.setFont(new Font("Dialog", Font.BOLD, 18));
		GroupLayout gl_clocksetpanel = new GroupLayout(clocksetpanel);
		gl_clocksetpanel.setHorizontalGroup(
			gl_clocksetpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clocksetpanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbx24Hour)
					.addContainerGap(331, Short.MAX_VALUE))
		);
		gl_clocksetpanel.setVerticalGroup(
			gl_clocksetpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clocksetpanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbx24Hour)
					.addContainerGap(150, Short.MAX_VALUE))
		);
		clocksetpanel.setLayout(gl_clocksetpanel);
		
		JPanel loggingsetpanel = new JPanel();
		settingsTab.addTab("Logging", null, loggingsetpanel, null);
		
		JCheckBox chckbxEnLogToTerm = new JCheckBox("Enable Logging to terminal");
		chckbxEnLogToTerm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Conf.logging = chckbxEnLogToTerm.isSelected();
				if (Conf.logging){
					logger.info("Logging enabled.");
				}
				else
				{
					logger.info("Logging disabled.");
				}
				saveConfshowDialog();
			}
		});
		chckbxEnLogToTerm.setSelected(Conf.logging);
		chckbxEnLogToTerm.setFont(new Font("Dialog", Font.BOLD, 18));
		
		JCheckBox chckbxEnLogToFile = new JCheckBox("Enable Logging to File");
		chckbxEnLogToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Conf.loggingToFile = chckbxEnLogToFile.isSelected();
				saveConfshowDialog();
			}
		});
		chckbxEnLogToFile.setSelected(Conf.loggingToFile);
		chckbxEnLogToFile.setFont(new Font("Dialog", Font.BOLD, 18));
		GroupLayout gl_loggingsetpanel = new GroupLayout(loggingsetpanel);
		gl_loggingsetpanel.setHorizontalGroup(
			gl_loggingsetpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_loggingsetpanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_loggingsetpanel.createParallelGroup(Alignment.LEADING)
						.addComponent(chckbxEnLogToTerm)
						.addComponent(chckbxEnLogToFile))
					.addContainerGap(183, Short.MAX_VALUE))
		);
		gl_loggingsetpanel.setVerticalGroup(
			gl_loggingsetpanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_loggingsetpanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxEnLogToTerm)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxEnLogToFile)
					.addContainerGap(115, Short.MAX_VALUE))
		);
		loggingsetpanel.setLayout(gl_loggingsetpanel);
		
		JPanel panel = new JPanel();
		settingsTab.addTab("Dev Tools", null, panel, null);
		
		JCheckBox chckbxNoUndecorated = new JCheckBox("No Undecorated (Requires Restart)");
		chckbxNoUndecorated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				undecorated = !chckbxNoUndecorated.isSelected();
				logger.info("Restarting UI.");
				ToastMessage t = new ToastMessage("Restarting UI", 2000);
				t.setVisible(true);
				main();
				frame.dispose();
			}
		});
		chckbxNoUndecorated.setFont(new Font("Dialog", Font.PLAIN, 22));
		chckbxNoUndecorated.setSelected(!undecorated);
		
		JCheckBox chckbxNoFullscreen = new JCheckBox("No Fullscreen");
		chckbxNoFullscreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extendedState = chckbxNoFullscreen.isSelected() ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH;
				frame.setExtendedState(extendedState);
			}
		});
		chckbxNoFullscreen.setSelected(extendedState == JFrame.NORMAL);
		chckbxNoFullscreen.setFont(new Font("Dialog", Font.PLAIN, 22));
		
		JButton btnRestartSrv = new JButton("Restart Jetty");
		btnRestartSrv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		JButton btnImportTestUser = new JButton("Import Test User");
		btnImportTestUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(btnRestartSrv, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnImportTestUser, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
						.addComponent(chckbxNoUndecorated)
						.addComponent(chckbxNoFullscreen))
					.addContainerGap(74, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(chckbxNoUndecorated)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chckbxNoFullscreen)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnImportTestUser, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnRestartSrv, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
					.addContainerGap(33, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		
		JPanel bootmenu = new JPanel();
		settingsTab.addTab("Boot Menu", null, bootmenu, null);
		
		JButton btnCloseTheProgram = new JButton("Close the program");
		btnCloseTheProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int result = JOptionPane.showConfirmDialog(null, "Close the UI?", "Are you sure?", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION){
					System.exit(0);
				}
			}
		});
		
		JButton btnRestartUi = new JButton("Restart UI");
		btnRestartUi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "Restart the UI?", "Are you sure?", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION){
					try {
						Runtime.getRuntime().exec("/home/pi/restart.sh");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}
		});
		
		JButton btnHaltTheSystem = new JButton("Halt the system");
		btnHaltTheSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "Halt the system?", "Are you sure?", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION){
					try {
						Runtime.getRuntime().exec("sudo halt");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton btnRestartTheSystem = new JButton("Restart the system");
		btnRestartTheSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "Restart the system?", "Are you sure?", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION){
					try {
						Runtime.getRuntime().exec("sudo reboot");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton btnTerminalEmulator = new JButton("Terminal Emulator");
		btnTerminalEmulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					try {
						Runtime.getRuntime().exec("xterm");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});
		
		JButton btnBlankScreensaver = new JButton("Blank screensaver");
		btnBlankScreensaver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tab.setSelectedIndex(2);
				new Thread (){
					public void run(){
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						BlankScreensaver.start();
					}
				}.start();
			}
		});
		GroupLayout gl_bootmenu = new GroupLayout(bootmenu);
		gl_bootmenu.setHorizontalGroup(
			gl_bootmenu.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_bootmenu.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_bootmenu.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnBlankScreensaver, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnHaltTheSystem, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnCloseTheProgram, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_bootmenu.createParallelGroup(Alignment.LEADING)
						.addComponent(btnTerminalEmulator, GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
						.addGroup(gl_bootmenu.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(btnRestartTheSystem, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnRestartUi, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_bootmenu.setVerticalGroup(
			gl_bootmenu.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_bootmenu.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_bootmenu.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCloseTheProgram, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRestartUi, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_bootmenu.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnHaltTheSystem, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRestartTheSystem, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_bootmenu.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBlankScreensaver, GroupLayout.PREFERRED_SIZE, 46, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnTerminalEmulator, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(36, Short.MAX_VALUE))
		);
		bootmenu.setLayout(gl_bootmenu);
		
		lblWeatherimg = new JLabel("");
		lblWeatherimg.setIcon(new ImageIcon(UI.class.getResource("/image/loading.gif")));
		lblWeatherimg.setHorizontalAlignment(SwingConstants.CENTER);
		
		lbltemp.setFont(new Font("Myriad Pro Cond", Font.PLAIN, 33));
										home_panel.add(imgbg);
										
										JLabel lblweatherlocate = new JLabel("Kwun Tong");
										lblweatherlocate.setFont(new Font("Myriad Pro Cond", Font.BOLD, 35));
										
										lbltime = new JLabel("00 : 00 : 00 AM");
										lbltime.setFont(new Font("Tahoma", Font.BOLD, 26));
										
										lbldate = new JLabel("01 - 01 - 2015");
										lbldate.setFont(new Font("Tahoma", Font.BOLD, 21));
										
										JPanel scene_panel = new JPanel();
										tab.addTab("", new ImageIcon(UI.class.getResource("/image/tabicn_scene.fw.png")), scene_panel, null);
										
										JPanel media_panel = new JPanel();
										tab.addTab("", new ImageIcon(UI.class.getResource("/image/tabicn_media.fw.png")), media_panel, null);
										
										tab.setSelectedIndex(2);
							
										timer1 = new Timer(36000, updateWeather);
										timer1.start();
										
										timer2 = new Timer(1000, updateTime);
										timer2.start();
										
										timer3 = new Timer(20000, busArrTimeUpdate);
										timer3.start();
										
										timer4 = new Timer((int) TimeUnit.SECONDS.toMillis(Conf.scrsaver_timeout), screensaver);
										timer4.start();
										

		ImagePanel imgbg3 = new ImagePanel(new ImageIcon(UI.class.getResource("/image/ha_bg.fw.png")).getImage());
		scene_panel.add(imgbg3);
		
		JPanel sceneintpanel = new JPanel();
		GroupLayout gl_imgbg3 = new GroupLayout(imgbg3);
		gl_imgbg3.setHorizontalGroup(
			gl_imgbg3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imgbg3.createSequentialGroup()
					.addContainerGap()
					.addComponent(sceneintpanel, GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_imgbg3.setVerticalGroup(
			gl_imgbg3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imgbg3.createSequentialGroup()
					.addGap(85)
					.addComponent(sceneintpanel, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
					.addContainerGap())
		);
		sceneintpanel.setLayout(new CardLayout(0, 0));
		
		tableActionsplit = new JSplitPane();
		tableActionsplit.setEnabled(false);
		tableActionsplit.setResizeWeight(0.8);
		tableActionsplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		sceneintpanel.add(tableActionsplit, "name_273535722487668");
		
		JScrollPane scenetablescroll = new JScrollPane();
		tableActionsplit.setLeftComponent(scenetablescroll);
		
		scenetablemodel = new DefaultTableModel();
		scenetablemodel.setColumnIdentifiers(SceneSave.colident);
		scenetablemodel.setRowCount(0);
		int numOfScenes = SceneSave.getAllScenesAmount();
		for (int i = 0; i < numOfScenes; i++){
			scenetablemodel.addRow(SceneSave.convertSaveDataIntoTableData(SceneSave.getScene(i)));
		}
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(scenetablescroll, popupMenu);
		
		JMenuItem mntmRefresh = new JMenuItem("Refresh");
		mntmRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenetablemodel.setRowCount(0);
				int numOfScenes = SceneSave.getAllScenesAmount();
				if (numOfScenes != -1){
					for (int i = 0; i < numOfScenes; i++){
						scenetablemodel.addRow(SceneSave.getScene(i));
					}
				}
				scenetablemodel.fireTableDataChanged();
			}
		});
		popupMenu.add(mntmRefresh);
		scenetable = new JTable(scenetablemodel);
		scenetable.setFont(new Font("Myriad Pro Cond", Font.PLAIN, 24));
		scenetablescroll.setViewportView(scenetable);
		
		JPanel tableActionPanel = new JPanel();
		tableActionsplit.setRightComponent(tableActionPanel);
		tableActionPanel.setLayout(new BorderLayout(0, 0));
		
		JButton btnEditScene = new JButton("Edit Scene");
		tableActionPanel.add(btnEditScene, BorderLayout.WEST);
		
		JButton btnAddScene = new JButton("Add Scene");
		btnAddScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeToAddNewScenePanel();
			}
		});
		tableActionPanel.add(btnAddScene, BorderLayout.CENTER);
		
		JButton btnDeleteScene = new JButton("Delete Scene");
		btnDeleteScene.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected = scenetable.getSelectedRow();
				if (selected <= -1){
					ToastMessage t = new ToastMessage("Nothing is selected.", 2000);
					t.setVisible(true);
					return;
				}
				scenetablemodel.removeRow(selected);
				SceneSave.removeScene(selected);
				sceneRun.restart();
				scenetablemodel.fireTableDataChanged();
				
			}
		});
		tableActionPanel.add(btnDeleteScene, BorderLayout.EAST);
		
		addnewscenepanel = new JPanel();
		sceneintpanel.add(addnewscenepanel, "name_274667189615893");
		
		JLabel lblNewLabel = new JLabel("Add new scene");
		lblNewLabel.setFont(new Font("Myriad Pro Cond", Font.PLAIN, 26));
		
		JLabel lblSceneName = new JLabel("Scene Name:");
		lblSceneName.setFont(new Font("Dialog", Font.PLAIN, 16));
		
		fldSceneName = new JTextField();
		fldSceneName.setColumns(10);
		
		JLabel lblSceneAction = new JLabel("Scene Action:");
		lblSceneAction.setFont(new Font("Dialog", Font.PLAIN, 16));
		
		boxSceneAction = new JComboBox<Object>(SceneSave.defactions);
		
		JLabel lblSceneTrigger = new JLabel("Scene Trigger:");
		lblSceneTrigger.setFont(new Font("Dialog", Font.PLAIN, 16));
		
		boxSceneTrigger = new JComboBox<Object>(SceneSave.deftriggers);
		
		scenetriggerinput = new JPanel();
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] tabledata = new String[4];
				String readableDuring;
				String during;
				
				tabledata[0] = fldSceneName.getText();
				tabledata[1] = SceneSave.defactions[boxSceneAction.getSelectedIndex()];
				tabledata[2] = SceneSave.deftriggers[boxSceneTrigger.getSelectedIndex()];
				switch (boxSceneTrigger.getSelectedIndex()) {
				//TODO Add more Triggers
				default:
				case 0:
					int shr = boxStartTimeHr.getSelectedIndex();
					int smin = boxStartTimeMin.getSelectedIndex();
					int ehr = boxEndTimeHr.getSelectedIndex();
					int emin = boxEndTimeMin.getSelectedIndex();
					during = SceneSave.transTimeIntoDuring(shr, smin, ehr, emin);
					readableDuring = SceneSave.transTimeIntoReadableDuring(shr, smin, ehr, emin);
					break;
				}
				tabledata[3] = readableDuring;
				String sceneuid = SceneSave.addScene(tabledata[0]);
				SceneSave.addTrigger(tabledata[2], sceneuid, during);
				SceneSave.addAction("StopScrnSave", sceneuid, tabledata[1], "none");
				SceneSave.writeIn();
				sceneRun.restart();
				scenetablemodel.addRow(tabledata);
				scenetablemodel.fireTableDataChanged();
				
				changeBackToSceneTablePanel();
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeBackToSceneTablePanel();
			}
		});
		GroupLayout gl_addnewscenepanel = new GroupLayout(addnewscenepanel);
		gl_addnewscenepanel.setHorizontalGroup(
			gl_addnewscenepanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addnewscenepanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.LEADING)
						.addComponent(scenetriggerinput, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
						.addComponent(lblNewLabel)
						.addGroup(gl_addnewscenepanel.createSequentialGroup()
							.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_addnewscenepanel.createSequentialGroup()
									.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblSceneName)
										.addComponent(lblSceneTrigger))
									.addGap(12))
								.addGroup(gl_addnewscenepanel.createSequentialGroup()
									.addComponent(lblSceneAction)
									.addPreferredGap(ComponentPlacement.UNRELATED)))
							.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(boxSceneTrigger, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(boxSceneAction, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(fldSceneName, GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
					.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_addnewscenepanel.setVerticalGroup(
			gl_addnewscenepanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_addnewscenepanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_addnewscenepanel.createSequentialGroup()
							.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSceneName)
								.addComponent(fldSceneName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(boxSceneAction, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSceneAction))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_addnewscenepanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSceneTrigger)
								.addComponent(boxSceneTrigger, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scenetriggerinput, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addGap(50))
		);
		scenetriggerinput.setLayout(new CardLayout(0, 0));
		
		striggertime = new JPanel();
		scenetriggerinput.add(striggertime, "name_275290111381066");
		
		boxStartTimeMin = new JComboBox<Object>(SceneSave.min);
		
		boxStartTimeHr = new JComboBox<Object>(SceneSave.hr);
		
		JLabel lblscenetimeto = new JLabel("~");
		lblscenetimeto.setFont(new Font("Dialog", Font.PLAIN, 18));
		
		boxEndTimeHr = new JComboBox<Object>(SceneSave.hr);
		
		boxEndTimeMin = new JComboBox<Object>(SceneSave.min);
		GroupLayout gl_striggertime = new GroupLayout(striggertime);
		gl_striggertime.setHorizontalGroup(
			gl_striggertime.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_striggertime.createSequentialGroup()
					.addContainerGap()
					.addComponent(boxStartTimeHr, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(boxStartTimeMin, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblscenetimeto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(boxEndTimeHr, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(boxEndTimeMin, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(80, Short.MAX_VALUE))
		);
		gl_striggertime.setVerticalGroup(
			gl_striggertime.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_striggertime.createSequentialGroup()
					.addGroup(gl_striggertime.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_striggertime.createParallelGroup(Alignment.BASELINE)
							.addComponent(boxStartTimeMin, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblscenetimeto))
						.addComponent(boxStartTimeHr, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
				.addGroup(gl_striggertime.createSequentialGroup()
					.addGroup(gl_striggertime.createParallelGroup(Alignment.BASELINE)
						.addComponent(boxEndTimeMin, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addComponent(boxEndTimeHr, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		striggertime.setLayout(gl_striggertime);
		addnewscenepanel.setLayout(gl_addnewscenepanel);
		imgbg3.setLayout(gl_imgbg3);
		
		ImagePanel imgbg4 = new ImagePanel(new ImageIcon(UI.class.getResource("/image/ha_bg.fw.png")).getImage());
		media_panel.add(imgbg4);
		
		JButton btnEntertainmentMenu = new JButton("Entertainment Menu");
		btnEntertainmentMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EntertainMenu.start();
			}
		});
		btnEntertainmentMenu.setBounds(15, 269, 183, 35);
		imgbg4.add(btnEntertainmentMenu);
		
		ImagePanel imgbg5 = new ImagePanel(new ImageIcon(UI.class.getResource("/image/ha_bg.fw.png")).getImage());
		light_panel.add(imgbg5);
		
		//busArrTimeModel.setColumnIdentifiers(colIdent);
		
		JTabbedPane quickViewTab = new JTabbedPane(JTabbedPane.TOP);
		
		JPanel busArrPane = new JPanel();
		quickViewTab.addTab("Bus Arrival Time", null, busArrPane, null);
		busArrPane.setLayout(new CardLayout(0, 0));
		
		busArrscroll = new JScrollPane();
		busArrPane.add(busArrscroll, "name_61383192761115");
		
		busArrTimeTable = new JTable();
		
		//((JXTable) busArrTimeTable).setEditable(false);
		busArrTimeTable.setFont(new Font("MS Song", Font.PLAIN, 27));
		busArrTimeTable.setRowSelectionAllowed(false);
		busArrscroll.setViewportView(busArrTimeTable);
		GroupLayout gl_imgbg = new GroupLayout(imgbg);
		gl_imgbg.setHorizontalGroup(
			gl_imgbg.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imgbg.createSequentialGroup()
					.addGap(12)
					.addComponent(lblWeatherimg)
					.addGap(5)
					.addGroup(gl_imgbg.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_imgbg.createSequentialGroup()
							.addGap(181)
							.addComponent(lbltime, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_imgbg.createSequentialGroup()
							.addGap(2)
							.addComponent(lbltemp, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblweatherlocate, GroupLayout.PREFERRED_SIZE, 211, GroupLayout.PREFERRED_SIZE)))
				.addComponent(quickViewTab, GroupLayout.PREFERRED_SIZE, 475, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl_imgbg.createSequentialGroup()
					.addGap(307)
					.addComponent(lbldate, GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE))
		);
		gl_imgbg.setVerticalGroup(
			gl_imgbg.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_imgbg.createSequentialGroup()
					.addGap(99)
					.addGroup(gl_imgbg.createParallelGroup(Alignment.LEADING)
						.addComponent(lblWeatherimg)
						.addGroup(gl_imgbg.createSequentialGroup()
							.addGap(42)
							.addComponent(lbltime, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_imgbg.createSequentialGroup()
							.addGap(30)
							.addComponent(lbltemp, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblweatherlocate, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
					.addGap(3)
					.addGroup(gl_imgbg.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_imgbg.createSequentialGroup()
							.addGap(9)
							.addComponent(quickViewTab, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
						.addComponent(lbldate, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
		);
		imgbg.setLayout(gl_imgbg);
		
		UIstarted  = true;
		logger.info("System is ready.");
		updateArrTime();
		updateWeatherFun();
		adjustColHeight();

		addnewscenepanel.setVisible(false);
		
		timer5 = new Timer(500, autoscroll );
		timer5.start();
		
		sceneRun = new SceneThread();
		sceneRun.run();

	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}


class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Image img;

	  public ImagePanel(String img) {
	    this(new ImageIcon(img).getImage());
	  }

	  public ImagePanel(Image img) {
	    this.img = img;
	    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);
	  }

	  public void paintComponent(Graphics g) {
	    g.drawImage(img, 0, 0, null);
	  }

}
