package de.zorgk.drums;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class takes riff xml input from sax and generates intermediate
 * structures that will result in the final RiffInterface
 * 
 * @author immanuel
 * 
 */

public class RiffXmlHandler extends DefaultHandler {

	private LinkedList<RiffXmlAttributes> attributeStack;
	private LinkedList<RiffXmlInterface> riffStack;

	public RiffXmlHandler() {
		attributeStack = new LinkedList<RiffXmlAttributes>();
		attributeStack.push(new RiffXmlAttributes());

		riffStack = new LinkedList<RiffXmlInterface>();
		riffStack.push(new XmlChain(attributeStack.peek()));
	}

	/**
	 * 
	 * @return the main RiffInterface
	 */
	public RiffInterface getRiffInterface() {
		return riffStack.get(0).getRiffInterface();
	}

	/**
	 * element starts, push it on the stack
	 */

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		/**
		 * the root element needs no handling
		 */
		if (qName.equals("riff"))
			return;

		RiffXmlAttributes attrs = new RiffXmlAttributes(attributeStack.peek(),
				attributes);
		attributeStack.push(attrs);

		if (qName.equals("chain")) {
			riffStack.push(new XmlChain(attributeStack.peek()));
		} else { // unknown or decorating tag, use XmlDistribute
			riffStack.push(new XmlDistribute(riffStack.peek()));
		}
	}

	/**
	 * element ends, add tags to html output
	 */

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		/**
		 * the root element needs no handling
		 */
		if (qName.equals("riff"))
			return;

		attributeStack.pop();
		RiffXmlInterface top = riffStack.pop();
		riffStack.peek().addChild(top);
	}

	/**
	 * receive data
	 */

	public void characters(char ch[], int start, int length)
			throws SAXException {
		riffStack.peek().characters(new String(ch, start, length));
	}

}
