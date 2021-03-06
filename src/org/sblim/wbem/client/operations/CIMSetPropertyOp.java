/**
 * CIMSetPropertyOp.java
 *
 * (C) Copyright IBM Corp. 2005, 2009
 *
 * THIS FILE IS PROVIDED UNDER THE TERMS OF THE ECLIPSE PUBLIC LICENSE 
 * ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS FILE 
 * CONSTITUTES RECIPIENTS ACCEPTANCE OF THE AGREEMENT.
 *
 * You can obtain a current copy of the Eclipse Public License from
 * http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * @author: Roberto Pineiro, IBM, roberto.pineiro@us.ibm.com  
 * @author: Chung-hao Tan, IBM ,chungtan@us.ibm.com
 * 
 * 
 * Change History
 * Flag       Date        Prog         Description
 *------------------------------------------------------------------------------- 
 * 1535756    2006-08-07  lupusalex    Make code warning free
 * 2807325    2009-06-22  blaschke-oss Change licensing from CPL to EPL
 *
 */

package org.sblim.wbem.client.operations;

import org.sblim.wbem.cim.CIMObjectPath;
import org.sblim.wbem.cim.CIMValue;

public class CIMSetPropertyOp extends CIMOperation {

	protected String iPropertyName;

	protected CIMValue iCimValue;

	public CIMSetPropertyOp(CIMObjectPath objectName, String propertyName, CIMValue cimValue) {
		iMethodCall = "SetProperty";
		iObjectName = objectName;
		iCimValue = cimValue;
		iPropertyName = propertyName;
	}

	
	/**
	 * Returns cimValue
	 *
	 * @return The value of cimValue.
	 */
	public CIMValue getCimValue() {
		return iCimValue;
	}

	
	/**
	 * Returns propertyName
	 *
	 * @return The value of propertyName.
	 */
	public String getPropertyName() {
		return iPropertyName;
	}


}
