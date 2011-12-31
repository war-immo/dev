package de.zorgk.drums;

/**
 * ignore child tags
 * 
 * @author immanuel
 * 
 */

public class XmlSkip implements RiffXmlInterface {


	@Override
	public RiffInterface getRiffInterface() {
		return null;
	}

	@Override
	public void addChild(RiffXmlInterface child) {

	}

	@Override
	public void characters(String contents) {

	}

}
