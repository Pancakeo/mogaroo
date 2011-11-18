package com.mogki.mogging.mogdub.mogaroo.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Locale;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mogki.mogging.mogdub.mogaroo.Config;

/** Main window. Owns references to sub components. **/

public class MainWindow {
	/** Title for the window. **/
	private static final String TITLE = Config.TITLE;
		
	/** Window dimensions. **/
	private static final Dimension WINDOW_SIZE = new Dimension(Config.WIDTH, Config.HEIGHT);
	
	private static final Random RANDOM = new Random();
	private static Point INITIAL_POSITION = new Point(RANDOM.nextInt(Config.X_LOC_DRIFT), RANDOM.nextInt(Config.Y_LOC_DRIFT));
	
	/** JFrame for this window. **/
	private static final JFrame m_frame = createWindow();
	
	/** Reference to our logger. **/
	private static Loggable m_logger;
	
	/** Single instance. **/
	private MainWindow() {

	}
	
	/** Default logger. **/
	public static void logMessage(String message) {
		m_logger.appendMessage(message);
	}
	
	/** Displays the window. **/
	public static void displayWindow() {
		m_frame.setVisible(true);
	}
	
	/** Create the window. **/
	private static JFrame createWindow() {
		JFrame.setDefaultLookAndFeelDecorated(false);
		JPanel.setDefaultLocale(Locale.US);
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(WINDOW_SIZE);
		frame.setLocation(INITIAL_POSITION);
		frame.setTitle(TITLE);
		frame.addComponentListener(new ResizeListener());
		
		frame.setLayout(null);
		frame.add(new FindClassPanel().getPanel());
		
		LogPanel logPanel = new LogPanel();
		m_logger = logPanel;
		
		frame.add(logPanel.getPanel());
		
		return frame;
	}
	
	// On resize, components should be updated.
	private static class ResizeListener implements ComponentListener {
		@Override
		public void componentResized(ComponentEvent arg0) {
			//onResize();
			
		}

		@Override
		public void componentHidden(ComponentEvent arg0) { }

		@Override
		public void componentMoved(ComponentEvent arg0) { }

		@Override
		public void componentShown(ComponentEvent arg0) { }
	}
	
}
