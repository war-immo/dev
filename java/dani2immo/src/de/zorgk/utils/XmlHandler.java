package de.zorgk.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {

	private Stack<String> qNames;
	private OutputStreamWriter out;
	private Vector<Integer> chord;
	private boolean chordPart;
	private boolean palmMute, slurOn, slurOff, glissando, squeal, dotted;
	private String suffixes;
	private String length;
	private String oldLength;
	private int string;
	private int fret;
	private String data;

	private int numerator, denominator, newNumerator, newDenominator;

	private boolean ignore;
	private int partCounter;

	private boolean gtOpen;
	private boolean repeat;
	private boolean wasRepeat;

	public static final int STRINGS = 7;
	public static final int BARNBRS = 5;

	private int barcount;

	public XmlHandler(OutputStream out) {
		qNames = new Stack<String>();
		try {
			this.out = new OutputStreamWriter(out, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		chord = new Vector<Integer>();

		for (int i = 0; i < STRINGS; ++i)
			chord.add(-1);

		length = "";
		oldLength = "";
		ignore = false;
		repeat = false;
		wasRepeat = false;
		suffixes = "";
		barcount = 0;
		partCounter = 0;
	}

	public void flush() {
		try {
			out.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void clearChord() {
		if (oldLength.isEmpty() == false) {

			if (gtOpen == false) {
				try {
					if (barcount % BARNBRS == 0) {
						out.write("         \\nbr \"" + barcount + "\"\n");

					}

					out.write("         \\gte \"  ");
				} catch (IOException e) {
					e.printStackTrace();
				}
				gtOpen = true;
			}

			if ((denominator > 0) && (numerator > 0)) {
				try {
					out.write("\" \\times " + numerator + "/" + denominator
							+ " { \\gte \" ");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			int max_string = -1;

			boolean has_note = false;

			for (int i = 0; i < STRINGS; ++i) {
				if (chord.get(i) >= 0) {
					has_note = true;
					max_string = i;
				}
			}

			if (has_note) {
				for (int i = 0; i <= max_string; ++i) {
					Integer x = chord.get(i);
					if (x < 0)
						try {
							out.write("-");
						} catch (IOException e) {
							e.printStackTrace();
						}
					else if ((x >= 3) || (x == 0))
						try {
							out.write(x.toString());
						} catch (IOException e) {
							e.printStackTrace();
						}
					else
						try {
							out.write(x.toString());
							if (i < max_string) {
								out.write(" ");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			} else {
				try {
					out.write("r");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				out.write(suffixes);

				out.write(oldLength);

				out.write("  ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if ((denominator > 0) && (numerator > 0)) {
			try {
				out.write("\" } \\gte \" ");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < STRINGS; ++i)
			chord.set(i, -1);

		oldLength = "";
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (ignore)
			return;

		qNames.push(qName);

		data = "";

		if (qName.equalsIgnoreCase("measure")) {
			try {

				out.write("      % measure  ");
				if (attributes.getValue("number") != null) {
					out.write(attributes.getValue("number"));
				}
				out.write("\n");

				gtOpen = false;

				oldLength = "";
				length = "";
				wasRepeat = repeat;
				repeat = false;

				++barcount;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (qName.equalsIgnoreCase("repeat")) {
			if (attributes.getValue("direction") != null)
				if ("forward"
						.equalsIgnoreCase(attributes.getValue("direction"))) {
					try {
						if (wasRepeat)
							out.write("         \\bar \":|:\"\n");
						else
							out.write("         \\bar \"|:\"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					repeat = true;

		}

		if (qName.equalsIgnoreCase("slide")) {
			if (attributes.getValue("type") != null)
				if ("start".equalsIgnoreCase(attributes.getValue("type"))) {
					glissando = true;
				}

		}

		if (qName.equalsIgnoreCase("slur")) {
			if (attributes.getValue("type") != null)
				if ("start".equalsIgnoreCase(attributes.getValue("type"))) {
					slurOn = true;
				} else {
					slurOff = true;
				}

		}

		if (qName.equalsIgnoreCase("note")) {
			chordPart = false;
			palmMute = false;
			slurOff = false;
			slurOn = false;
			glissando = false;
			string = -1;
			squeal = false;
			dotted = false;
			newNumerator = -1;
			newDenominator = -1;
			fret = -1;
		}

		if (qName.equalsIgnoreCase("dot")) {
			dotted = true;
		}

		if (qName.equalsIgnoreCase("chord")) {
			chordPart = true;
		}

		if (qName.equalsIgnoreCase("part")) {
			partCounter++;
			barcount = 0;

			try {
				out.write("part" + ((char) ((int) ('A') + partCounter - 1))
						+ " = {\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (ignore)
			return;

		qNames.pop();

		if (qName.equalsIgnoreCase("measure")) {
			try {

				clearChord();

				if (gtOpen)
					out.write("\"  \n");

				if (repeat)
					out.write("         \\bar \":|\"\n");
				else {
					if (gtOpen)
						out.write("         \\bar \"|\"\n");
					else
						out.write("         \\bar \"||\"\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (qName.equalsIgnoreCase("note")) {

			if (chordPart != true) {
				clearChord();
			}

			if ((0 < string) && (string <= STRINGS)) {
				chord.set(STRINGS - string, fret);
			}

			suffixes = "";
			if (palmMute)
				suffixes += "x";

			if (slurOff)
				suffixes += ")";

			if (slurOn)
				suffixes += "(";

			if (glissando)
				suffixes += "g";

			if (squeal)
				suffixes += "s";

			numerator = newNumerator;
			denominator = newDenominator;

			oldLength = length;

			if (dotted)
				oldLength += ".";
		}

		if (qName.equalsIgnoreCase("string")) {
			string = Integer.parseInt(data);
		}

		if (qName.equalsIgnoreCase("fret")) {
			fret = Integer.parseInt(data);
		}

		if (qName.equalsIgnoreCase("actual-notes")) {
			newDenominator = Integer.parseInt(data);
		}

		if (qName.equalsIgnoreCase("normal-notes")) {
			newNumerator = Integer.parseInt(data);
		}

		if (qName.equalsIgnoreCase("other-technical")) {
			if (data.equalsIgnoreCase("palm mute"))
				palmMute = true;
			if (data.equalsIgnoreCase("vibrato"))
				squeal = true;
		}

		if (qName.equalsIgnoreCase("type")) {
			if (data.equalsIgnoreCase("half")) {
				length = "/2";
			} else if (data.equalsIgnoreCase("whole")) {
				length = "/1";
			} else if (data.equalsIgnoreCase("quarter")) {
				length = "/4";
			} else if (data.equalsIgnoreCase("eighth")) {
				length = "/8";
			} else if (data.equalsIgnoreCase("sixteenth")) {
				length = "/16";
			} else {
				System.err.println("UNKNOWN TYPE " + data);
			}
		}

		if (qName.equalsIgnoreCase("part")) {

			try {
				out.write("} %part" + ((char) ((int) ('A') + partCounter - 1))
						+ "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (ignore)
			return;

		data += new String(ch, start, length);
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {

		return new InputSource(new ByteArrayInputStream(
				"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	}

	public static void transformStream(InputStream in, OutputStream out) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(in, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
		InputSource source = new InputSource(reader);

		source.setEncoding("UTF-8");

		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setValidating(false);

		try {
			SAXParser parser = factory.newSAXParser();



			XmlHandler handler = new XmlHandler(out);

			parser.parse(source, handler);

			handler.flush();

		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		try {
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
