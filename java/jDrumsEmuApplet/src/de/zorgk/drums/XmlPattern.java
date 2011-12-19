package de.zorgk.drums;

import java.util.ArrayList;

public class XmlPattern implements RiffXmlInterface {

	private RiffXmlInterface parent;
	private RiffXmlAttributes attributes;
	private ArrayList<Integer> pattern;

	public XmlPattern(RiffXmlInterface parent, RiffXmlAttributes attributes) {
		this.parent = parent;
		this.attributes = attributes;
		this.pattern = new ArrayList<Integer>();
	}

	@Override
	public RiffInterface getRiffInterface() {
		if (attributes.getDrum() != null) {
			int [] pat = new int[pattern.size()];
			for (int i=0;i<pattern.size();++i)
				pat[i] = pattern.get(i);

			return new PatternedHitsRiff(attributes.getBeatsPerMinute(),
					attributes.getPartLength(), attributes.getLength()
							/ attributes.getPartLength(),
					attributes.getDrum(), attributes.getDeziBel(), pat);
		}

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
		for (int i=0;i<contents.length();++i) {
			if (contents.charAt(i) == '-') 
				pattern.add(0);
			if (contents.charAt(i) == '+')
				pattern.add(1);
		}
	}

}
