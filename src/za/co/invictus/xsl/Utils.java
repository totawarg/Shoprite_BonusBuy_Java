package za.co.invictus.xsl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.lookup.Channel;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;
import com.sap.aii.mapping.lookup.SystemAccessor;
import com.sap.aii.mapping.lookup.XmlPayload;
import com.sap.aii.mapping.value.api.IFIdentifier;
import com.sap.aii.mapping.value.api.IFRequest;
import com.sap.aii.mapping.value.api.IFResponse;
import com.sap.aii.mapping.value.api.ValueMappingException;
import com.sap.aii.mapping.value.api.XIVMFactory;
import com.sap.aii.mapping.value.api.XIVMService;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {

	public static final String SITE = "Site";
	public static final String SUPPLIER = "Supplier";
	public static final String ITEM = "Item";
	

	/**
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static boolean compare(String i1, String i2) {
		System.out.println("item 1=" + i1 + ", item 2=" + i2);
		return false;
	}

	/**
	 * 
	 * @param q1
	 * @param q2
	 * @return
	 */
	public static boolean compareQueues(NodeList q1, NodeList q2) {

		if ((q1.getLength() == q2.getLength()) == false) {
			return false;
		}

		for (int i = 0; i < q1.getLength(); i++) {
			System.out.println("comparing " + getFirstLevelTextContent(q1.item(i)) + " to "
					+ getFirstLevelTextContent(q2.item(i)));

			String v1 = getFirstLevelTextContent(q1.item(i));

			Node i2 = q2.item(i);
			String v2 = null;

			if (i2 != null) {
				v2 = getFirstLevelTextContent(i2);
			}

			if (v2 == null) {
				return false;
			}

			if (v1.matches(v2) == false) {
				return false;
			}
		}

		return true;

	}

	public static String getFirstLevelTextContent(Node node) {
		NodeList list = node.getChildNodes();
		StringBuilder textContent = new StringBuilder();
		for (int i = 0; i < list.getLength(); ++i) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				textContent.append(child.getTextContent());
		}
		return textContent.toString();
	}

	public static String bonusBuyFileName(String filePrefix, String branchCode, String sourceMessageMessageID,
			String sourceCreationDateTime, String fileExtension) {
		if (filePrefix == null)
			filePrefix = "";
		String dateTime = convertUTCtoSouthAfricaTimezone("yyyy-MM-dd'T'HH:mm:ss'Z'", sourceCreationDateTime,
				"yyyyMMddHHmmss" + "");
		String fileName = filePrefix + "." + branchCode + "." + dateTime + "." + sourceMessageMessageID + "."
				+ fileExtension;
		return fileName;
	}

	public static String convertUTCtoSouthAfricaTimezone(String utcDateTimeFormat, String utcDate,
			String southAfricaDateTimeFormat) {

		DateFormat utcFormat = new SimpleDateFormat(utcDateTimeFormat);
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcdate = null;
		try {
			utcdate = utcFormat.parse(utcDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateFormat saFormat = new SimpleDateFormat(southAfricaDateTimeFormat);
		saFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
		return saFormat.format(utcdate);
	}

	public static String convertUTCtoSouthAfricaTimezone(String utcDate) {

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcdate = null;
		try {
			utcdate = utcFormat.parse(utcDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateFormat saFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		saFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
		return saFormat.format(utcdate);
	}

	/**
	 * UTC yyyy-MM-dd'T'HH:mm:ss'Z' to yyyy-MM-dd
	 * 
	 * @param utcDate
	 * @return
	 */
	public static String convertUTCtoSouthAfricaTimezone2(String utcDate) {

		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcdate = null;
		try {
			utcdate = utcFormat.parse(utcDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DateFormat saFormat = new SimpleDateFormat("yyyy-MM-dd");
		saFormat.setTimeZone(TimeZone.getTimeZone("Africa/Johannesburg"));
		return saFormat.format(utcdate);
	}

	/**
	 * 
	 * @param site
	 * @return
	 */
	public static String addCheckDigitToSite(String site) {
		return addCheckDigit(site, SITE);
	}

	/**
	 * 
	 * @param objectKey
	 *            Key for the object example Site or Supplier or Item
	 * @param objectType
	 *            Type of object Site or Item or Supplier , this
	 * @return
	 */
	public static String addCheckDigit(String objectKey, String objectType) {
		int functionReturnValue = 0;
		if (objectKey != null && objectType != null) {
			String ID = objectKey;
			int newID = 0;
			int intID1 = 0;
			int intID2 = 0;
			int intID3 = 0;
			int intID4 = 0;
			int intID5 = 0;
			int intID6 = 0;
			int intTotal = 0;
			int intCheckDigit = 0;

			int noDigits = 0;
			if (objectType.equals(SITE)) {
				noDigits = 5;
			} else {
				noDigits = 6;
			}

			String strID = String.format("%0" + noDigits + "d", Integer.parseInt(ID));

			intID1 = Character.getNumericValue(strID.charAt(0));
			intID2 = Character.getNumericValue(strID.charAt(1));
			intID3 = Character.getNumericValue(strID.charAt(2));
			intID4 = Character.getNumericValue(strID.charAt(3));
			intID5 = Character.getNumericValue(strID.charAt(4));
			if (objectType.equals(SITE) == false) {
				intID6 = Character.getNumericValue(strID.charAt(5));
			}

			// Weighing factor is different for different length:
			if (objectType.equals(ITEM) == true) {
				intTotal = ((intID1 * 1)
						+ ((intID2 * 7) + ((intID3 * 3) + ((intID4 * 1) + ((intID5 * 7) + (intID6 * 3))))));
			} else if (objectType.equals(SITE) == true) {
				intTotal = ((intID1 * 8) + ((intID2 * 7) + ((intID3 * 4) + ((intID4 * 3) + (intID5 * 2)))));
			} else if (objectType.equals(SUPPLIER) == true) {
				if ((ID.length() == 6)) {
					intTotal = ((intID1 * 8)
							+ ((intID2 * 7) + ((intID3 * 4) + ((intID4 * 3) + ((intID5 * 2) + (intID6 * 1))))));
				} else {
					intTotal = ((intID1 * 0)
							+ ((intID2 * 8) + ((intID3 * 7) + ((intID4 * 4) + ((intID5 * 3) + (intID6 * 2))))));
				}
			} else {
				functionReturnValue = Integer.parseInt(strID);

			}

			//
			if (Integer.parseInt(Integer.toString(intTotal).substring(Integer.toString(intTotal).length() - 1)) == 0) {
				intCheckDigit = 0;
			} else {
				intCheckDigit = (10 - Integer
						.parseInt(Integer.toString(intTotal).substring(Integer.toString(intTotal).length() - 1)));
			}

			newID = Integer.parseInt((ID + Integer.toString(intCheckDigit)));
			functionReturnValue = newID;

		}

		return new Integer(functionReturnValue).toString();

	}

	public static String getPreviousOrFutureDate(String inputDateFormat, String inputDate, String outputDateFormat,
			int numberOfDays) {

		DateFormat inDateFormat = new SimpleDateFormat(inputDateFormat);
		DateFormat outDateFormat = new SimpleDateFormat(outputDateFormat);

		Calendar cal = Calendar.getInstance();
		Date indate = null;
		String outdate = null;
		try {
			indate = inDateFormat.parse(inputDate);
			cal.setTime(indate);
			cal.add(Calendar.DATE, numberOfDays);
			outdate = outDateFormat.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outdate;
	}

	public static String getPreviousDateForActivationStatusCode2(String inputDate) {

		DateFormat inDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat outDateFormat = new SimpleDateFormat("yyyy-MM-dd");

		Calendar cal = Calendar.getInstance();
		Date indate = null;
		String outdate = null;
		try {
			indate = inDateFormat.parse(inputDate);
			cal.setTime(indate);
			cal.add(Calendar.DATE, -1);
			outdate = outDateFormat.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outdate;
	}

	public static String getSystemProperty(String prop) {
		String value = null;
		value = (String) System.getProperty(prop);
		return value;
	}

	public static String getPISID() {
		String value = getSystemProperty("SAPSYSTEMNAME");
		if (value == null) {
			value = "PIP";
		}
		return value;
	}

	public static void main(String[] a) {
		System.out.println(getPreviousDateForActivationStatusCode2("2016-03-01"));
	}

	public static String soapLookUP(String inputXML, String businessSystem, String channelName)
			throws StreamTransformationException {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		InputStream response = null;
		BufferedReader br = null;
		/**
		 * This is a generic SOAP Looup service. Input is XML String the
		 * webservice is expecting output is XML string returned by Webservice
		 * Addtional UDF specific to your requirement needs to be built for
		 * buidling input XML and parsing output XML
		 * 
		 * 
		 **/
		try {

			Channel channel = LookupService.getChannel(businessSystem, channelName);
			SystemAccessor accessor = LookupService.getSystemAccessor(channel);
			inputStream = new ByteArrayInputStream(inputXML.getBytes());
			XmlPayload payload = LookupService.getXmlPayload(inputStream);

			Payload SOAPOutPayload = accessor.call(payload);
			response = SOAPOutPayload.getContent();
			br = new BufferedReader(new InputStreamReader(response));
			String line;

			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			throw new StreamTransformationException(e.getMessage());
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (response != null) {
					response.close();
					response = null;
				}
				if (br != null) {
					br.close();
					br = null;
				}
			} catch (IOException e) {
				throw new StreamTransformationException(e.getMessage());
			}
		}
		return sb.toString();
	}
	public static String getLocationData(String businessSystem,String channelName,String inputXML) throws StreamTransformationException{
		return soapLookUP(inputXML, businessSystem, channelName);
		
	}
	public static String getTestStoreIDbySAPStoreID(String sapStoreID, String environment,String businessSystem,String channelName)
			throws StreamTransformationException{
		String testStoreID="";
		
		String inputXML="<?xml version=\"1.0\" encoding=\"UTF-8\"?><tem:GetData xmlns:tem=\"http://tempuri.org/\"/>";
		String responseXML=null;
		try{
		       if(responseXML==null){ 
		    	   responseXML=getLocationData(businessSystem, channelName,inputXML);
		       }
		       String xpathExp="GetDataResponse/GetDataResult/*[local-name()='diffgram']/*[local-name()='NewDataSet']/*[local-name()='TestStores'][*[local-name()='SAPLiveStore']="+sapStoreID+"][*[local-name()='Environment']='"+environment+"']/*[local-name()='SAPTestStore']";
		       		System.out.println("XPATH:"+xpathExp);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				 InputSource is = new InputSource(new StringReader(responseXML));
					Document doc = dBuilder.parse(is);
				  XPathFactory xpathFactory = XPathFactory.newInstance();
					XPath xpath = xpathFactory.newXPath();
					XPathExpression expr =xpath.compile(xpathExp);
				  NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
					if(nodes!=null && nodes.getLength()>0){
				  	testStoreID=nodes.item(0).getTextContent();
				  	System.out.println("Test Store ID: "+testStoreID);	
					}else{
									throw new StreamTransformationException("No LocationID Found "+sapStoreID+" "+environment+" "+xpathExp );
					}
		    if(testStoreID.equals("0") || testStoreID==null || testStoreID.length()==0){
								throw new StreamTransformationException("Invalid Test Store Number : " + testStoreID);
			}
		} catch (SAXException e) {
					throw new StreamTransformationException(e.getMessage());
		} catch (IOException e) {
					throw new StreamTransformationException(e.getMessage());
		} catch (DOMException e) {
					throw new StreamTransformationException(e.getMessage());
		}catch (ParserConfigurationException e) {
					throw new StreamTransformationException(e.getMessage());
		}catch(XPathExpressionException e){
				 throw new StreamTransformationException(e.getMessage());
		}


		return   testStoreID;
	}
	
	public static String getPIToDatariteEnvLookup(String key) throws StreamTransformationException{
		String sourceContext="http://sap.com/xi/XI";
		String sourceAgency="SAP";
		String sourceScheme="sap.pi.env";
		String targetContext="http://sap.com/xi/XI";
		String targetAgency="DATARITE";
		String targetScheme="datarite.env";
		return valueMappingLookup(sourceContext, sourceAgency, sourceScheme, targetContext, targetAgency, targetScheme, key);
	}
	
	public static String valueMappingLookup(String sourceContext, String sourceAgency, String sourceScheme, String
			targetContext, String targetAgency, String targetScheme, String key) throws StreamTransformationException{
		String value=new String();
		try {
		IFIdentifier sourceIdentifier =XIVMFactory.newIdentifier(sourceAgency, sourceScheme);
		IFIdentifier targetIdentifier = XIVMFactory.newIdentifier(targetAgency,targetScheme);
		IFRequest mRequest = XIVMFactory.newRequest(sourceIdentifier,targetIdentifier, key); 
		
			IFResponse mResponse = XIVMService.executeMapping(mRequest);
			if(mResponse.hasTargetValues())
			 {
				value = mResponse.getSingleTargetValue();
			 } else{
				 value=key;
			 }
		} catch (ValueMappingException e) {
			throw new StreamTransformationException(e.getMessage());
		} 
		
		
		return value;
	}
		
}
	

