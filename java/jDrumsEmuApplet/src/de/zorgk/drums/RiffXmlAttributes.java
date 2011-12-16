package de.zorgk.drums;

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;

/**
 * handles Riff XML tag attributes/parameters
 * 
 * @author immanuel
 * 
 */

public class RiffXmlAttributes {

	/**
	 * canonical attribute storage
	 */

	private Map<String, String> attributes;

	/**
	 * constructor for root node attributes (e.g. defaults)
	 */
	public RiffXmlAttributes() {
		this.attributes = new HashMap<String, String>();

	}

	/**
	 * constructor for child node attributes, we use the following convention:
	 * attributes starting with big letters do not propagate to children, others
	 * do!
	 * 
	 * @param parent
	 *            parent node
	 * @param attributes
	 *            xml attributes
	 */

	public RiffXmlAttributes(RiffXmlAttributes parent, Attributes attributes) {
		this.attributes = new HashMap<String, String>(parent.attributes);

		copyAttributes(attributes);
	}

	private void copyAttributes(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); ++i) {
			if (Character.isUpperCase(attributes.getQName(i).charAt(0)) != true)
				this.attributes.put(attributes.getQName(i),
						attributes.getValue(i));
		}
	}

	/**
	 * give the value corresponding to the attribute according to current
	 * propagation
	 * 
	 * @param qName
	 * @return attribute value or <b>null</b> if not found
	 */
	public String getAttribute(String qName) {
		return attributes.get(qName);
	}

}
