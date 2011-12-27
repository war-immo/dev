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
		
		/**
		 * create a string copy...
		 */
		
		StringBuffer input_buffer = new StringBuffer();
		char[] chars = new char[1024];
		int read_chars;
		
		while ((read_chars = source.getCharacterStream().read(chars)) != -1) {
			input_buffer.append(chars, 0, read_chars);			
		}		
		
		String xml_data = input_buffer.toString();
		
		/**
		 * reset variables to be working with a fresh buffer copy...
		 */		
		
		input = new ByteArrayInputStream(xml_data.getBytes("UTF-8"));
		reader = new InputStreamReader(input, "UTF-8");
		source = new InputSource(reader);
		
			

		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		/**
		 * first pass, try with validation
		 */
		
		factory.setValidating(true);
		
		SAXParser parser = factory.newSAXParser();

		RiffXmlHandler handler = new RiffXmlHandler(sampler);
		
		try {
			parser.parse(source, handler);
			
		} catch (Exception e) {
			
			sampler.out("Riff XML: first run error:");
			StackTraceElement[] s = e.getStackTrace();
			for (int i = 0; i < s.length; ++i) {
				sampler.out(s[i].toString());

			}
			sampler.out("... ERROR: " + e.getMessage());
			sampler.out("Starting second run.");
			
			/**
			 * kill the <!DOCTYPE ..> tag
			 */
			
			int idx_doctype = xml_data.indexOf("<!DOCTYPE");
			int idx_doctype_end = xml_data.indexOf(">",idx_doctype);
			
			if ((idx_doctype > 0) && (idx_doctype_end > 0)) {
				xml_data = xml_data.substring(0, idx_doctype) + xml_data.substring(idx_doctype_end+1);
			}
			
			/**
			 * reset variables to be working with a fresh buffer copy...
			 */		
			
			input = new ByteArrayInputStream(xml_data.getBytes("UTF-8"));
			reader = new InputStreamReader(input, "UTF-8");
			source = new InputSource(reader);
			
						
			/**
			 * second pass, without validation
			 */
			
			factory.setValidating(false);
			
			
			parser = factory.newSAXParser();
			
			handler = new RiffXmlHandler(sampler);
			
			parser.parse(source, handler);
		}

		return handler.getRiffInterface();
		
	};

	public static RiffInterface transformXml(String xmlData,
			SamplerSetup sampler) throws ParserConfigurationException,
			SAXException, IOException {
		if (xmlData.trim().startsWith("<?xml"))
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
