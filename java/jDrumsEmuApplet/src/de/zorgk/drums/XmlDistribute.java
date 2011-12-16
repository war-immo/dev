package de.zorgk.drums;

/**
 * distribute tag to parent tag
 * 
 * @author immanuel
 * 
 */

public class XmlDistribute implements RiffXmlInterface {

	private RiffXmlInterface destination;

	public XmlDistribute(RiffXmlInterface target) {
		destination = target;
	}

	@Override
	public RiffInterface getRiffInterface() {
		return null;
	}

	@Override
	public void addChild(RiffXmlInterface child) {
		destination.addChild(child);
	}

	@Override
	public void characters(String contents) {
		destination.characters(contents);
	}

}
