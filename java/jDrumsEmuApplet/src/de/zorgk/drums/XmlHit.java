package de.zorgk.drums;

public class XmlHit implements RiffXmlInterface {

	private RiffXmlInterface parent;
	private RiffXmlAttributes attributes;

	public XmlHit(RiffXmlInterface parent, RiffXmlAttributes attributes) {
		this.parent = parent;
		this.attributes = attributes;
	}

	@Override
	public RiffInterface getRiffInterface() {
		if (attributes.getDrum() != null)
			return new SimpleHitsRiff(attributes.getBeatsPerMinute(),
					attributes.getLength(), 1.f, attributes.getDrum(),
					attributes.getDeziBel());

		/**
		 * drum not found / unset --> still waste the time...
		 */

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
