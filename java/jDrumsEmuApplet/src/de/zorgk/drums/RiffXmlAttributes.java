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

	private float speedFactor;
	private float bpm;
	private float length;

	/**
	 * constructor for root node attributes (e.g. defaults)
	 */
	public RiffXmlAttributes() {
		this.attributes = new HashMap<String, String>();
		this.speedFactor = 1.f;
		this.length = 1.f;
		this.bpm = 120.f;
	}
	
	/**
	 * 
	 * @return current effective bpm settings
	 */
	
	public float getBeatsPerMinute() {
		return this.bpm * this.speedFactor;
	}
	
	/**
	 * 
	 * @return current length
	 */
	
	public float getLength() {
		return this.length;
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
		this.speedFactor = parent.speedFactor;
		this.bpm = parent.bpm;
		this.length = parent.length;

		copyAttributes(attributes);
	}

	private void copyAttributes(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); ++i) {
			String qName = attributes.getQName(i);
			String value = attributes.getValue(i);

			/**
			 * canonical attribute management
			 */
			if (Character.isUpperCase(qName.charAt(0)) != true)
				this.attributes.put(qName, value);

			try{
			/**
			 * special attribute management
			 */
				if (qName.equalsIgnoreCase("length")) {
					this.length = Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("length_")) {
					this.length *= Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("bpm")) {
					this.bpm = Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("bpm_")) {
					this.bpm *= Float.parseFloat(value);
				}
			} catch (Exception e) {
				System.err.println("Error in XML attributes: "+qName+", "+value+": "+e);
			}

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
