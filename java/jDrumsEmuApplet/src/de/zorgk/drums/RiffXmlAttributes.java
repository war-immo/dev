package de.zorgk.drums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private float part;
	private float dB;
	private ArrayList<HitDecorator> decoratorChain;
	private HitInterface drum;
	private HitInterface decoratedDrum;
	private SamplerSetup sampler;
	private String drumName;

	/**
	 * constructor for root node attributes (e.g. defaults)
	 */
	public RiffXmlAttributes(SamplerSetup sampler) {
		this.attributes = new HashMap<String, String>();
		this.speedFactor = 1.f;
		this.length = 1.f;
		this.bpm = 120.f;
		this.part = 1.f;
		this.decoratorChain = new ArrayList<HitDecorator>();
		this.sampler = sampler;
		this.drum = sampler.instruments.get("snare");
		this.decoratedDrum = this.drum;
		this.dB = 0.f;
		this.drumName = "";
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
		this.attributes = new HashMap<String, String>();

		for (Iterator<String> it_qnames = parent.attributes.keySet().iterator(); it_qnames
				.hasNext();) {
			String qName = it_qnames.next();

			if (Character.isUpperCase(qName.charAt(0)) != true) {
				this.attributes.put(qName, parent.attributes.get(qName));
			}
		}

		this.speedFactor = parent.speedFactor;
		this.bpm = parent.bpm;
		this.length = parent.length;
		this.part = parent.part;
		this.decoratorChain = new ArrayList<HitDecorator>(parent.decoratorChain);
		this.sampler = parent.sampler;
		this.drum = parent.drum;
		this.decoratedDrum = parent.decoratedDrum;
		this.dB = parent.dB;
		this.drumName = parent.drumName;

		copyAttributes(attributes);
	}

	/**
	 * parse XML tag attributes, used in the constructors
	 * 
	 * @param attributes
	 */
	private void copyAttributes(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); ++i) {
			String qName = attributes.getQName(i);
			String value = attributes.getValue(i);

			/**
			 * canonical attribute management
			 */
			this.attributes.put(qName, value);

			try {
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
				} else if (qName.equalsIgnoreCase("part")) {
					this.part = Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("part_")) {
					this.part *= Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("db")) {
					this.dB = Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("db_")) {
					this.dB += Float.parseFloat(value);
				} else if (qName.equalsIgnoreCase("drum")) {
					this.drumName = value.toLowerCase();
					this.drum = sampler.instruments.get(drumName);
					decorateInstrument();
				}
			} catch (Exception e) {
				System.err.println("Error in XML attributes: " + qName + ", "
						+ value + ": " + e);
			}

		}

	}

	/**
	 * apply all decorators to the current drum
	 */
	private void decorateInstrument() {
		boolean[] is_canceled = new boolean[decoratorChain.size()];

		for (int i = 0; i < is_canceled.length; ++i)
			is_canceled[i] = false;

		/**
		 * check for canceling decorators
		 */

		for (int j = is_canceled.length - 1; j >= 0; --j) {
			if (is_canceled[j] != true) {
				for (int i = 0; i < j; ++i) {
					if (is_canceled[i] != true) {
						if (decoratorChain.get(j).cancelsOut(
								decoratorChain.get(i), drumName)) {
							is_canceled[i] = true;
						}
					}
				}
			}
		}

		HitInterface intermediate = this.drum;
		for (int j = is_canceled.length - 1; j >= 0; --j) {
			if (is_canceled[j] != true) {
				intermediate = decoratorChain.get(j).decorate(intermediate, drumName);
			}
		}

		this.decoratedDrum = intermediate;
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
	 * 
	 * @return current part length
	 */

	public float getPartLength() {
		return this.part;
	}

	/**
	 * 
	 * @return current dB value for hits
	 */
	public float getDeziBel() {
		return this.dB;
	}

	/**
	 * 
	 * @return the sampler object
	 */
	public SamplerSetup getSampler() {
		return this.sampler;
	}

	/**
	 * 
	 * @return HitInterface that corresponds to the current decorated drum
	 */
	public HitInterface getDrum() {
		return this.decoratedDrum;
	}

}
