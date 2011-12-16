package de.zorgk.drums;

import java.util.ArrayList;

/**
 * Riff XML anti-chain handler
 * 
 * @author immanuel
 * 
 */

public class XmlAntiChain implements RiffXmlInterface {

	private ArrayList<RiffInterface> riffs;

	private RiffXmlAttributes attributes;

	public XmlAntiChain(RiffXmlAttributes attributes) {
		riffs = new ArrayList<RiffInterface>();
		this.attributes = attributes;
	}

	@Override
	public RiffInterface getRiffInterface() {
		RiffInterface[] riffArray = new RiffInterface[riffs.size()];

		for (int i = 0; i < riffArray.length; ++i)
			riffArray[i] = riffs.get(i);

		return new RiffAntiChain(riffArray);
	}

	@Override
	public void addChild(RiffXmlInterface child) {
		RiffInterface riff = child.getRiffInterface();
		if (null != riff)
			riffs.add(riff);
	}

	@Override
	public void characters(String contents) {
		/**
		 * noop
		 */
	}
}
