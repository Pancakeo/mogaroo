package com.mogki.mogging.mogdub.mogaroo.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.mogki.mogging.mogdub.mogaroo.Config;
import com.mogki.mogging.mogdub.mogaroo.request.FindClass;
import com.mogki.mogging.mogdub.mogaroo.request.FindClassImpl;
import com.mogki.mogging.mogdub.mogaroo.request.FindClassImpl.FindClassParams;

/** UI for Find Class. **/

public class FindClassPanel {
	
	/** Default constructor. **/
	public FindClassPanel() {
		
	}
	
	/** Return a panel with find class operations. **/
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setBounds(5, 5, 800, 200);
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		int x = 10, startX = x;
		int y = 5, rowHeight = 25;
		int colSmall = 100, colMedium = 200, colLarge = 300, colBigBig = 650;
		int vGap = 5, hGap = 5;
		
		JLabel lblInfo = new JLabel("Find a class.");
		lblInfo.setBounds(x, y, colLarge, rowHeight);
		panel.add(lblInfo);
		
		y += rowHeight + vGap;
		
		JLabel lblSummaryUrl = new JLabel("Summary URL:");
		lblSummaryUrl.setBounds(x, y, colSmall, rowHeight); 
		panel.add(lblSummaryUrl);
		
		x += colSmall + hGap;
		
		final JTextField txtSummaryUrl = new JTextField(Config.DEF_SUMM_URL);
		txtSummaryUrl.setBounds(x, y, colBigBig, rowHeight);
		panel.add(txtSummaryUrl);
		
		x = startX;
		y += rowHeight + vGap;
		
		JLabel lblSlnNum = new JLabel("SLN #:");
		lblSlnNum.setBounds(x, y, colSmall, rowHeight); 
		panel.add(lblSlnNum);
		
		x += colSmall + hGap;
		
		final JTextField txtSlnNum = new JTextField(Config.DEF_SLN);
		txtSlnNum.setBounds(x, y, colMedium, rowHeight);
		panel.add(txtSlnNum);
		
		x = startX;
		y += rowHeight + vGap;
				
		JLabel lblUsername = new JLabel("Username:");
		lblUsername.setBounds(x, y, colSmall, rowHeight); 
		panel.add(lblUsername);
		
		x += colSmall + hGap;
		
		final JTextField txtUsername = new JTextField(Config.DEF_USERNAME);
		txtUsername.setBounds(x, y, colMedium, rowHeight);
		panel.add(txtUsername);
				
		x = startX;
		y += rowHeight + vGap;
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(x, y, colSmall, rowHeight);
		panel.add(lblPassword);
		
		x += colSmall + hGap;
		
		final JPasswordField txtPassword = new JPasswordField("");
		txtPassword.setBounds(x, y, colMedium, rowHeight);
		panel.add(txtPassword);
		
		x = startX;
		y += rowHeight + vGap;
		
		JButton cmdLogin = new JButton("Submit Login");
		cmdLogin.setBounds(x, y, colLarge, rowHeight);
		cmdLogin.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String uName = txtUsername.getText();
				String pWord = String.valueOf(txtPassword.getPassword());
				String sln = txtSlnNum.getText();
				String enrSumm = txtSummaryUrl.getText();
				
				FindClassParams params = new FindClassParams(uName, pWord, sln, enrSumm);
				FindClass findClass = new FindClassImpl(params);
				
				findClass.hasFreeSlot();
			}
			
		});
		
		panel.add(cmdLogin);
		
		return panel;
	}
		
}
