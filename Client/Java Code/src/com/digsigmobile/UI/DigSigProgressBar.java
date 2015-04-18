package com.digsigmobile.UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;
import javax.swing.JProgressBar;
import javax.swing.JLabel;

public class DigSigProgressBar extends JFrame {

	private JProgressBar progressBar;
    private static JLabel lblYourRequestIs;
    private static JFrame frame; 
	private JPanel contentPane;
	private JTextPane textPane;
	

	/**
	 * Launch the application.
	 */
	public static void showProgressGUI(String titleMessage, boolean isProgressBarToStart)
	{
		try 
		{
			if(isProgressBarToStart)
			{
				frame = new DigSigProgressBar();
				
				String title = frame.getTitle();
				titleMessage = titleMessage + title;
				frame.setTitle(titleMessage);
				
				lblYourRequestIs.setText(titleMessage + " . .");
				frame.setVisible(true);
			}
			
			else
				frame.dispose();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create the frame.
	 */
	public DigSigProgressBar() {
		setTitle(" in progress..");
		setResizable(false);
		
		setBounds(100, 100, 450, 300);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setBounds(139, 215, 146, 14);
		contentPane.add(progressBar);
		
		lblYourRequestIs = new JLabel("Creating Digital Certificate . .");
		lblYourRequestIs.setForeground(Color.RED);
		lblYourRequestIs.setBackground(Color.WHITE);
		lblYourRequestIs.setBounds(76, 11, 248, 14);
		contentPane.add(lblYourRequestIs);
		
		textPane = new JTextPane();
		textPane.setForeground(Color.BLUE);
		textPane.setEditable(false);
		textPane.setBounds(76, 46, 248, 146);
		textPane.setText(" It may take a few minutes to process your request. DO NOT CLOSE THE HOME" +
		" WINDOW TILL THE REQUEST IS COMPLETE. Your patience is appreciated :)");
		
		//styling the text in the text Area to fit in the center
		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		contentPane.add(textPane);
		
	}
}
