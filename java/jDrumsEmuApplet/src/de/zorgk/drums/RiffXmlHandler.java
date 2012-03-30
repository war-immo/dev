package de.zorgk.drums;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
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

	public RiffXmlHandler(SamplerSetup sampler) {
		attributeStack = new LinkedList<RiffXmlAttributes>();
		attributeStack.push(new RiffXmlAttributes(sampler));

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
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		RiffXmlAttributes attrs = new RiffXmlAttributes(attributeStack.peek(),
				attributes);
		attributeStack.push(attrs);

		if (qName.equalsIgnoreCase("chain") || qName.equalsIgnoreCase("meta")
				|| qName.equalsIgnoreCase("c")) {
			riffStack.push(new XmlChain(attributeStack.peek()));
		} else if (qName.equalsIgnoreCase("antichain")
				|| qName.equalsIgnoreCase("syn") || qName.equalsIgnoreCase("a")) {
			riffStack.push(new XmlAntiChain(attributeStack.peek()));
		} else if (qName.equalsIgnoreCase("rest")
				|| qName.equalsIgnoreCase("pause")
				|| qName.equalsIgnoreCase("r")) {
			riffStack
					.push(new XmlRest(riffStack.peek(), attributeStack.peek()));
		} else if (qName.equalsIgnoreCase("hit")
				|| qName.equalsIgnoreCase("kick")
				|| qName.equalsIgnoreCase("stroke")
				|| qName.equalsIgnoreCase("h")) {
			riffStack.push(new XmlHit(riffStack.peek(), attributeStack.peek()));
		} else if (qName.equalsIgnoreCase("hits")
				|| qName.equalsIgnoreCase("kicks")
				|| qName.equalsIgnoreCase("strokes")
				|| qName.equalsIgnoreCase("hs")) {
			riffStack
					.push(new XmlHits(riffStack.peek(), attributeStack.peek()));
		} else if (qName.equalsIgnoreCase("pattern")
				|| qName.equalsIgnoreCase("p") || qName.equalsIgnoreCase("pat")) {
			riffStack.push(new XmlPattern(riffStack.peek(), attributeStack
					.peek()));
		} else if (qName.equalsIgnoreCase("control")
				|| qName.equalsIgnoreCase("cnt")
				|| qName.equalsIgnoreCase("ctr")) {
			riffStack.push(new XmlControl(riffStack.peek(), attributeStack
					.peek()));
		} else if (qName.equalsIgnoreCase("skip")
				|| qName.equalsIgnoreCase("ignore")) {
			riffStack.push(new XmlSkip());
		} else { // unknown or decorating tag or riff tag, use XmlDistribute
			riffStack.push(new XmlDistribute(riffStack.peek()));
		}
	}

	/**
	 * element ends do riff output
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {


		attributeStack.pop();
		RiffXmlInterface top = riffStack.pop();
		riffStack.peek().addChild(top);
	}

	/**
	 * receive data
	 */
	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		riffStack.peek().characters(new String(ch, start, length));
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {

		// TODO RETURN CORRECT DTD FILE

		return new InputSource(new ByteArrayInputStream(
				"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}
}
