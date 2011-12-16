package de.zorgk.drums;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RiffXmlToRiffInterface {

	/**
	 * Transform an Riff XML file into a RiffInterface
	 * 
	 * @param input
	 *            Stream that reads the Riff XML file (UTF-8)
	 * 
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */

	public static RiffInterface transformStream(InputStream input,
			SamplerSetup sampler) throws ParserConfigurationException,
			SAXException, IOException {

		/**
		 * I/O handling in UTF-8
		 */

		Reader reader = new InputStreamReader(input, "UTF-8");
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		RiffXmlHandler handler = new RiffXmlHandler(sampler);

		parser.parse(source, handler);

		return handler.getRiffInterface();
	};

	public static RiffInterface transformXml(String xmlData,
			SamplerSetup sampler) throws ParserConfigurationException,
			SAXException, IOException {
		if (xmlData.startsWith("<?xml"))
			return transformStream(
					new ByteArrayInputStream(xmlData.getBytes("UTF-8")),
					sampler);
		else {
			String hackedData = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ xmlData;
			return transformStream(
					new ByteArrayInputStream(hackedData.getBytes("UTF-8")),
					sampler);
		}
	};

}
