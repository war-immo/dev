package de.zorgk.drums;

public class XmlRest implements RiffXmlInterface {

	private RiffXmlInterface parent;
	private RiffXmlAttributes attributes;

	public XmlRest(RiffXmlInterface parent, RiffXmlAttributes attributes) {
		this.parent = parent;
		this.attributes = attributes;
	}

	@Override
	public RiffInterface getRiffInterface() {
		return new RestRiff(attributes.getBeatsPerMinute(),
				attributes.getLength());
	}

	@Override
	public void addChild(RiffXmlInterface child) {
		parent.addChild(child);
	}

	@Override
	public void characters(String contents) {
		parent.characters(contents);
	}

}
