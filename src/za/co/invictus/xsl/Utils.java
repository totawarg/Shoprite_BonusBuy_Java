package za.co.invictus.xsl;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Utils {
	
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
		
		for (int i=0; i<q1.getLength(); i++) {
			//System.out.println("comparing " + q1.item(i).getTextContent() + " to " + q2.item(i).getTextContent());
			
			String v1 = q1.item(i).getTextContent();
			
			Node i2 = q2.item(i);
			String v2 = null;
			
			if (i2 != null) {
				v2 = i2.getTextContent();
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
	
}
