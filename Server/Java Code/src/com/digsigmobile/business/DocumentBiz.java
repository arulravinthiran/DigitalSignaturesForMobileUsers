package com.digsigmobile.business;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Persistence;
import com.digsigmobile.persistence.document.CmdCreateDocument;
import com.digsigmobile.persistence.document.CmdRetrieveDocument;
import com.digsigmobile.persistence.document.CmdRetrieveMaxTrustCode;


public class DocumentBiz 
{
	
	/**
	 * Persistence object
	 */
	private Persistence persistence = null;
	
	public DocumentBiz() throws DatabaseException 
	{
		this.persistence = Persistence.getInstance();
		if (!persistence.isConnected()) persistence.connect();
	}
	
	public TrustCode createDocument(DocumentBean document) throws DatabaseException 
	{
		CmdRetrieveMaxTrustCode maxTrCode = new CmdRetrieveMaxTrustCode();
		persistence.run(maxTrCode);
		//System.out.println("In DOc Biz Max Trust code is: "+maxTrCode.getMaxTrustCode());
		if(maxTrCode.getMaxTrustCode() != null)
		{
			document.setTrustCode(maxTrCode.getMaxTrustCode());
			CmdCreateDocument cmdDoc = new CmdCreateDocument(document);
			persistence.run(cmdDoc);
		}
		persistence.disconnect();
		return maxTrCode.getMaxTrustCode();
	}
	
	public DocumentBean retrieveDocument(TrustCode trustCode) throws DatabaseException 
	{
		CmdRetrieveDocument retDoc = new CmdRetrieveDocument(trustCode);
		persistence.run(retDoc);
		persistence.disconnect();
		return retDoc.getDocument();
	}
	
	public void removeDocument(TrustCode trustCode) throws DatabaseException {
		
	}
}
