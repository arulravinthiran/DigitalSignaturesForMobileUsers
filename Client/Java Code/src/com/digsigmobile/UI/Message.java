package com.digsigmobile.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.control.TrustCodeVerification;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessageType;

@SuppressWarnings("restriction")
public class Message extends JPanel implements UICallback {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private boolean DEBUG = false;
	private Object[][] data = new Object[2][4];
	private static JFrame frame;
	private static Message newContentPane;

	public Message(int trustCode) 
	{
		super(new GridLayout(1,0));
		TrustCodeVerification trCdeVerfnObj = new TrustCodeVerification(Message.this);

		// Create Trust Code object
		TrustCode trCode;
		try {
			trCode = new TrustCode(trustCode);
		} catch (InvalidInputException e1) {
			UIUtil.showErrorBox(getRootPane(), "Invalid TrustCode!");
			return;
		}

		// verify trust code
		Home.disableButtons(true);
		DigSigProgressBar.showProgressGUI("Digital Signatures Status", true);
		trCdeVerfnObj.verifyTrustCode(trCode);

	}

	private void printDebugData(JTable table) {
		int numRows = table.getRowCount();
		int numCols = table.getColumnCount();
		javax.swing.table.TableModel model = table.getModel();

		System.out.println("Value of data: ");
		for (int i=0; i < numRows; i++) {
			System.out.print("    row " + i + ":");
			for (int j=0; j < numCols; j++) {
				System.out.print("  " + model.getValueAt(i, j));
			}
			System.out.println();
		}
		System.out.println("--------------------------");
	}

	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	public static void createAndDisplayTable(String trustCode) 
	{

		try
		{
			//Create and set up the window.
			frame = new JFrame("DigSigMobile Signature Details");
			frame.setResizable(false);
			frame.setBounds(100, 100, 450, 300);
			//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//Create and set up the content pane.
			newContentPane = new Message(Integer.parseInt(trustCode));

		}
		catch(Exception ex)
		{
			/*UIUtil.showErrorBox(getRootPane(), "Sorry! Server error! Please contact" +
			" the service team!");*/
			ex.printStackTrace();
		}
	}

	

	
	@SuppressWarnings("unchecked")
	@Override
	public void onCallback(int type, Serializable content) {
		DigSigProgressBar.showProgressGUI("", false);
		Home.disableButtons(false);
		switch(type) {
		case SocketMessageType.ACK_TRUSTCODE_VERIFICATION_SUCCESSFUL:
			HashMap<Integer, Row> data = (HashMap<Integer, Row>) content;
			printTable(data);
			break;
		case SocketMessageType.ACK_TRUSTCODE_VERIFICATION_FAILURE:
			UIUtil.showErrorBox(getRootPane(), "Sorry! Server error! Please contact" +
					" the service team!");
			break;
		default:
			UIUtil.showErrorBox(getRootPane(), "Sorry! Server error! Please contact" +
					" the service team!");
			break;
		}
	}

	private void printTable(HashMap<Integer, Row> rowMap) {

		// Check if rowMap is empty
		if(rowMap == null || rowMap.isEmpty()) {
			//System.out.println("Bean empty");
			UIUtil.showErrorBox(getRootPane(), "Invalid trust code!");
			return;
		}
		
		else
		{
			// Initialize variables
			UserBean userBnObj = null;
			DigitalSignatureBean digSigBnObj = null;
			Set<Entry<Integer, Row>> rowMapEntrySet = null;
			Iterator<Entry<Integer, Row>> rowMapItrtr = null;
			Map.Entry<Integer,Row> rowMapEntry = null;
			Row rowObj = new Row(userBnObj, digSigBnObj);

			rowMapEntrySet = rowMap.entrySet();
			rowMapItrtr = rowMapEntrySet.iterator();

			// Set up data
			for(int rowCount = 0; rowCount < rowMap.size(); rowCount++) {
				if(rowMapItrtr.hasNext()) {
					rowMapEntry = (Map.Entry<Integer,Row>)rowMapItrtr.next();

					if(rowMapEntry.getKey() == rowCount) {
						rowObj = (Row) rowMapEntry.getValue();

						userBnObj = new UserBean();
						digSigBnObj = new DigitalSignatureBean();

						userBnObj= (UserBean)rowObj.getUser();

						String name = null;
						if(userBnObj.getFamilyName()!= null)
							name = userBnObj.getFamilyName() +" " +userBnObj.getName();
						else
							name = userBnObj.getName();

						data[rowCount][0] = name;
						//System.out.println("Name : " + name);

						digSigBnObj = (DigitalSignatureBean) rowObj.getDigitalSignature();

						if(digSigBnObj.hasSigned()== false)
							data[rowCount][1] = "Not signed yet";

						else {
							if(digSigBnObj.isValid()) {
								data[rowCount][1] = "Valid signature";
								data[rowCount][2] = 
										digSigBnObj.getSignedTime().toString();
								data[rowCount][3] = 
										digSigBnObj.getSigningReason();
							} else {
								data[rowCount][1] = "Invalid signature";
								data[rowCount][2] = 
										digSigBnObj.getSignedTime().toString();
								data[rowCount][3] = 
										digSigBnObj.getSigningReason();
							}
						}
					}
				}
			}
		}

		/*JButton btnQuit = new JButton("Close");
		btnQuit.setForeground(Color.RED);
		btnQuit.setBounds(357, 17, 66, 23);
		btnQuit.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int option = UIUtil.showConfirmationBox(getRootPane(), "Confirm your action!"
						, "Are you sure that you want to leave the registration?");
					if(option == 0)
					{
						frame.dispose();
					}
				
			}
		});
		add(btnQuit);*/

		// Print table
		//create a table with user-edit prorty set to false
		String[] columnNames = {"User Name", "Signature status",  "Time of Signing", 
		"Reason for signature"};

		final JTable table = new JTable(data, columnNames)
		{
			/**
			 * default serialVersionUID = 1L
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int vColIndex) 
			{
				return false;
			}
		};
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);

		if (DEBUG) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					printDebugData(table);
				}
			});
		}

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Add the scroll pane to this panel.
		add(scrollPane);
		
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);
		
		
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}
}