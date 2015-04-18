package com.digsigmobile.UI;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.datatypes.EmailAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Date;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;

import org.apache.commons.io.IOUtils;

@SuppressWarnings("restriction")
public class UIUtil extends JFrame
{
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private File file;
	private static byte[] origDoc = null;
	private static final long MAX_SIZE_MB = 8;
	
	private DocumentBean docBnObj = null;
	/**
	 * Method throws an error dialog
	 * @param rootPane panel`of the root component
	 * @param errorMessage error message string
	 */
	public static void showErrorBox(JRootPane rootPane, String errorMessage)
	{
		JOptionPane.showMessageDialog(rootPane, errorMessage,"Error in Submission!", 
				JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Method throws an information dialog
	 * @param rootPane panel`of the root component
	 * @param titleMessage title message string
	 * @param infoMessage information message string
	 */
	public static void showInformationBox(JRootPane rootPane, String titleMessage, String infoMessage)
	{
		JOptionPane.showMessageDialog(rootPane, infoMessage,titleMessage, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Method throws a warning dialog
	 * @param rootPane panel`of the root component
	 * @param titleMessage title message string 
	 * @param infoMessage information message string
	 */
	public static void showWarningBox(JRootPane rootPane, String titleMessage, String infoMessage)
	{
		JOptionPane.showMessageDialog(rootPane, infoMessage,titleMessage, JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * Method throws a confirmation dialog with Yes / No option
	 * @param rootPane panel`of the root component
	 * @param titleMessage title message string
	 * @param confirmationMessage confirmation message string
	 * @return
	 */
	public static int showConfirmationBox(JRootPane rootPane,String titleMessage, String confirmationMessage)
	{
		int option = JOptionPane.showConfirmDialog(rootPane, confirmationMessage, titleMessage, 
				JOptionPane.YES_NO_OPTION);
		if(option == 0)
			return 0;
		else
			return -1;
	}
	
	public DocumentBean fileChooserHelper() 
	{
		JFileChooser fileChooserObj = new JFileChooser();
		fileChooserObj.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int returnVal = fileChooserObj.showOpenDialog(UIUtil.this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) 
		{   
			int okay = 0;
            try 
            {
            	file = fileChooserObj.getSelectedFile();
                
            	// Get length of file in bytes
            	long fileSizeInBytes = file.length();
            	// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            	long fileSizeInKB = fileSizeInBytes / 1024;
            	// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            	long fileSizeInMB = fileSizeInKB / 1024;
            	
            	//throw error for files exceedig max_size
            	if(fileSizeInMB > MAX_SIZE_MB)
            	{
            		UIUtil.showErrorBox(getRootPane(), "File size exceeds 8 MB!" +
					" Choose another file!");
            		okay = -1;
            	}
            	
                Path path = file.toPath();
            	InputStream inputstream = new FileInputStream(file);
				String MIMEType = Files.probeContentType(path);
				origDoc = IOUtils.toByteArray(inputstream);
				
				docBnObj = new DocumentBean();
				docBnObj.setFileName(file.getName());
				docBnObj.setDocumentFile(origDoc);
				docBnObj.setMimeType(new MimeType(MIMEType));
				docBnObj.setUploadedTime(new Timestamp(new Date().getTime()));
			} 
            catch( MimeTypeParseException mEx)
            {
            	UIUtil.showErrorBox(getRootPane(), "File type not recognized! Please " +
				"choose another file!");
            	okay = -1;
		       mEx.printStackTrace();
            }
            catch (IOException e) 
            {
				UIUtil.showErrorBox(getRootPane(), "File type not recognized! Please " +
						"choose another file!");
				okay = -1;
				e.printStackTrace();
			}
            catch(Exception ex)
            {
            	UIUtil.showErrorBox(getRootPane(), "File type not recognized! Please " +
				"choose another file!");
            	okay = -1;
		        ex.printStackTrace();
            }
            
            if(okay != -1)
            {
            
            	okay = UIUtil.showConfirmationBox(getRootPane(), "Confirm the file to sign!",
                		"Selecting: "+ file.getName() + " for signing! Are you sure that this is the" +
                				" right file?");
                if(okay == 0)
                {
                	return docBnObj;
                }
                else
                {
                	UIUtil.showErrorBox(getRootPane(), "Please choose the right file for signature " +
                			"creation!");
                	//fileUploaded = false;
                	return null;
                }
            }
            
        } 
		else 
        {
			UIUtil.showInformationBox(rootPane, "File selection cancelled!", 
					"File selection cancelled!");
			//fileUploaded = false;
			return null;
        }
		return docBnObj;
		
	}
}
