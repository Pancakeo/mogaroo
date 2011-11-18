package com.mogki.mogging.mogdub.mogaroo.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/** Panel for logging messages. **/

public class LogPanel implements Loggable {
	/** Newline character. **/
	private static final String NL = System.getProperty("line.separator");
	
	/** Text area. **/
	private JTextArea m_textArea;
	
	/** Log a message to this panel's text area. **/
	public void appendMessage(String message) {
		m_textArea.append(message + NL);
		// TODO: Auto-scroll.
	}
	
	/** Return a panel with a text area and clear button. **/
	public JPanel getPanel() {
		// TODO: The initial position (panel-wise) depends on the Find Class Panel.
		// Add better relationships between classes.
				
		JPanel panel = new JPanel();
		panel.setBounds(10, 225, 800, 485);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.setLayout(null);
		
		JLabel lblInfo = new JLabel("Log panel...");
		lblInfo.setBounds(10, 5, 100, 25);
		panel.add(lblInfo);
		
		m_textArea = new JTextArea();
		JScrollPane textAreaSp = new JScrollPane(m_textArea);
		textAreaSp.setBounds(5, 30, 790, 350);
		m_textArea.setEditable(false);
		panel.add(textAreaSp);
		
		JButton cmdClearLog = new JButton("Clear Log");
		cmdClearLog.setBounds(10, 400, 250, 25);
		
		cmdClearLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				m_textArea.setText("");
			}
			
		});
		
		panel.add(cmdClearLog);
		
		return panel;
	}
	
}
