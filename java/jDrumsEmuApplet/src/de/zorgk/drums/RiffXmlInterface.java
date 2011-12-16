package de.zorgk.drums;

/**
 * interface for RiffXmlToRiffInterface intermediate structures
 * 
 * @author immanuel
 * 
 */

public interface RiffXmlInterface {

	/**
	 * 
	 * @return corresponding RiffInterface or <b>null</b>
	 */
	public RiffInterface getRiffInterface();

	/**
	 * adds a child node
	 * 
	 * @param child
	 */
	public void addChild(RiffXmlInterface child);

	/**
	 * retrieve contents of the xml tag (non-meta-data)
	 * 
	 * @param contents
	 */
	public void characters(String contents);
}
