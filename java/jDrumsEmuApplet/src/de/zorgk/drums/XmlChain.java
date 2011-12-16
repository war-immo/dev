package de.zorgk.drums;

import java.util.ArrayList;

/**
 * Riff XML chain handler
 * 
 * @author immanuel
 * 
 */

public class XmlChain implements RiffXmlInterface {

	private ArrayList<RiffInterface> riffs;

	private RiffXmlAttributes attributes;

	public XmlChain(RiffXmlAttributes attributes) {
		riffs = new ArrayList<RiffInterface>();
		this.attributes = attributes;
	}

	@Override
	public RiffInterface getRiffInterface() {
		RiffInterface[] riffArray = new RiffInterface[riffs.size()];

		for (int i = 0; i < riffArray.length; ++i)
			riffArray[i] = riffs.get(i);

		try {
			return new RiffChain(riffArray, Integer.parseInt(attributes
					.getAttribute("Repeat")));
		} catch (Exception e) {
			return new RiffChain(riffArray);
		}

	}

	@Override
	public void addChild(RiffXmlInterface child) {
		riffs.add(child.getRiffInterface());
	}

	@Override
	public void characters(String contents) {
		/**
		 * noop
		 */
	}
}
