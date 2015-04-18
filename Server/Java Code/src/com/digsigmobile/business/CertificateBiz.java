package com.digsigmobile.business;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Persistence;
import com.digsigmobile.persistence.certificate.CmdCreateCertificate;
import com.digsigmobile.persistence.certificate.CmdRetrieveCertificate;
import com.digsigmobile.persistence.certificate.CmdRetrieveMaxCertId;

public class CertificateBiz 
{

	/**
	 * Persistence object
	 */
	private Persistence persistence = null;
	
	public CertificateBiz() throws DatabaseException {
		this.persistence = Persistence.getInstance();
		if (!persistence.isConnected()) persistence.connect();
		
	}
	
	/**
	 * method stores the details of the given certificate bean in the database
	 * @param certificate
	 * @return certificateID 
	 * @throws DatabaseException
	 */
	public CertificateId createCertificate(CertificateBean certificate) throws DatabaseException 
	{
		CmdRetrieveMaxCertId maxCert = new CmdRetrieveMaxCertId();
		persistence.run(maxCert);
		if(maxCert.getMaxCertId() != null) {
			certificate.setCertificateId(maxCert.getMaxCertId());
			CmdCreateCertificate crtCert = new CmdCreateCertificate(certificate);
			persistence.run(crtCert);
		}
		persistence.disconnect();
		return maxCert.getMaxCertId();
	}
	
	/**
	 * method retrieves certificate bean given an email address
	 * @param email
	 * @return
	 * @throws DatabaseException
	 */
	public CertificateBean retrieveCertificate(UserId userId) 
	throws DatabaseException 
	{
		CmdRetrieveCertificate retCert = new CmdRetrieveCertificate(userId);
		persistence.run(retCert);
		persistence.disconnect();
		return retCert.getCert();
	} 
	
	//need to gather inputs on revoking certificates
	public void revokeCertificate(CertificateId certificateId) throws DatabaseException {
		
	}
	
}
