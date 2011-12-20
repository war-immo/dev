package de.zorgk.drums;

/**
 * Riff XML control tag management
 * 
 * A control sequence is of one of the forms, multiple sequences can be
 * separated by ,
 * 
 * PARAMETERNAME = NEWVALUE sets the current instrument's parameter
 * PARAMETERNAME to NEWVALUE
 * 
 * PARAMETERNAME@DRUMNAME = NEWVALUE sets the DRUMNAME drum's parameter
 * PARAMETERNAME to NEWVALUE
 * 
 * @author immanuel
 * 
 */

public class XmlControl implements RiffXmlInterface {

	private RiffXmlInterface parent;
	private RiffXmlAttributes attributes;

	private String contents;

	public XmlControl(RiffXmlInterface parent, RiffXmlAttributes attributes) {
		this.parent = parent;
		this.attributes = attributes;
		this.contents = "";
	}

	@Override
	public RiffInterface getRiffInterface() {
		ControlRiff control = new ControlRiff();

		String[] sequences = contents.split(",");
		for (int i = 0; i < sequences.length; ++i) {
			String line = sequences[i];
			String[] line_parts = line.split("=");
			if (line_parts.length != 2) {
				System.err.println("Cannot parse <control> sequence: " + line);
				continue;
			}
			try {
				float value = Float.parseFloat(line_parts[1].trim());

				String parameterName = line_parts[0].trim();

				HitInterface drum;

				if (parameterName.contains("@")) {
					String[] para_drum = parameterName.split("@");
					String drumname = para_drum[1].trim().toLowerCase();
					drum = attributes.getSampler().instruments.get(drumname);
					
					parameterName = para_drum[0].trim();
				} else
					drum = attributes.getDrum();
				
				if (drum == null) {
					System.err.println("<control> drum not found: " + line);
					continue;
				} 
				
				long parameter = drum.getParameterNbrName(parameterName);
				
				control.setParameter(drum, parameter, value);

			} catch (NumberFormatException e) {
				System.err.println("Cannot parse <control> sequence value: "
						+ line);
			} catch (IllegalArgumentException e) {
				System.err.println("<control> parameter unknown: " + line);
			}
		}

		return control;
	}

	@Override
	public void addChild(RiffXmlInterface child) {
		parent.addChild(child);
	}

	@Override
	public void characters(String contents) {
		this.contents += contents;
	}

}
