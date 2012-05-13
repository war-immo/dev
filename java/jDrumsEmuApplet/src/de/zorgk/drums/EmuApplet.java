package de.zorgk.drums;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class EmuApplet extends JApplet {

	private static final int build_nbr = 4;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3218208748277098775L;

	public static final int sRate = 44100;
	public static final int channels = 2;
	public static final int buffer_frames = 64;
	private volatile int line_buffer_size = 4096 * channels * 4;

	private volatile Thread player = null;

	private final EmuApplet parent;

	private CommunicationStream to_line = new CommunicationStream();

	public class EmptyRiff implements RiffInterface {
		@Override
		public boolean timeElapse(long framestart, long framenextstart,
				LinkedList<RiffInterface> stack, SamplerSetup sampler) {

			return false;
		}

		@Override
		public RiffInterface getClone() {

			return this;
		}
	}

	public class playbackDriver implements LineListener, Runnable {

		private SourceDataLine line = null;

		public playbackDriver(SourceDataLine line) {
			this.line = line;
		}

		@Override
		public void run() {

			out("Starting player thread...");
			out("Line format: " + line.getFormat().toString());

			Thread thisThread = Thread.currentThread();

			LinkedList<RiffInterface> trampoline = new LinkedList<RiffInterface>();
			trampoline.push(new EmptyRiff());

			line.addLineListener(this);

			byte[] b_buffer = new byte[channels * 4 * buffer_frames];
			long[] i_buffer = new long[channels * buffer_frames];

			long frames_elapsed = 0;

			SamplerSetup sampler = null;
			try {
				sampler = new SamplerSetup(format, parent);
			} catch (UnsupportedAudioFileException e) {
				out("UnsuppoertedAudioFileException " + e.toString());

			} catch (IOException e) {
				out("IOException " + e.toString());
			} catch (URISyntaxException e) {
				out("URISyntaxException " + e.toString());
			}

			if (sampler == null) {
				out("Error initializing sampler...");
				return;
			}

			try {
				line.open(format, line_buffer_size);
			} catch (LineUnavailableException e1) {
				out("LineUnavailabe: " + e1.getStackTrace().toString());
				return;
			}

			for (int idx = 0; idx < i_buffer.length; ++idx) {
				i_buffer[idx] = 0;
			}

			for (int idx = 0; idx < b_buffer.length; ++idx) {
				b_buffer[idx] = 0;
			}

			line.write(b_buffer, 0, buffer_frames * channels * 4);

			line.start();
			out("Starting playback loop...");

			while (player == thisThread) {

				if (trampoline.peek().timeElapse(frames_elapsed,
						frames_elapsed + buffer_frames, trampoline, sampler)) {
					trampoline.pop();
				}

				for (int idx = 0; idx < i_buffer.length; ++idx) {
					i_buffer[idx] = 0;
				}
				sampler.addToBuffer(frames_elapsed, i_buffer, buffer_frames, 0);

				for (int idx = 0; idx < i_buffer.length; ++idx) {
					int b_idx = idx << 2;
					int f;
					long l = i_buffer[idx];
					if (l >= Integer.MAX_VALUE)
						f = Integer.MAX_VALUE;
					else if (l <= Integer.MIN_VALUE)
						f = Integer.MIN_VALUE;
					else
						f = (int) l;
					b_buffer[b_idx] = (byte) (f >> 24);
					b_buffer[b_idx + 1] = (byte) (f >> 16);
					b_buffer[b_idx + 2] = (byte) (f >> 8);
					b_buffer[b_idx + 3] = (byte) (f);
				}
				frames_elapsed += buffer_frames;
				line.write(b_buffer, 0, buffer_frames * channels * 4);
				if (!to_line.empty()) {
					int nbr = to_line.info();
					Object o = to_line.object();
					to_line.pop();
					switch (nbr) {
					case 0:
						trampoline = new LinkedList<RiffInterface>();
						trampoline.push(new EmptyRiff());
						break;
					case 1:
						trampoline.push(new EmptyRiff());
						break;
					case 2:
						if (trampoline.size() > 1)
							trampoline.pop();
						break;
					case 3:
						try {
							trampoline.push(RiffXmlToRiffInterface
									.transformXml((String) o, sampler));
						} catch (Exception e) {
							out("\n\n");
							StackTraceElement[] s = e.getStackTrace();
							for (int i = 0; i < s.length; ++i) {
								out(s[i].toString());

							}
							out("... ERROR: " + e.getMessage());
						}
						break;
					case 4:
						sampler.speedFactor = (Float) o * 0.01f;
						break;
					default:
						out("\nunknown to_line: " + nbr + ", " + o.toString());
					}

				}

			}

			line.removeLineListener(this);
			line.close();

			out("Exiting player thread...");

		}

		@Override
		public void update(LineEvent arg0) {

			out("LineEvent: " + arg0.toString());

		}

	};

	AudioFormat format = new AudioFormat(new Float(sRate), 32, channels, true,
			true);
	DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	Line.Info[] lines = AudioSystem.getSourceLineInfo(info);
	int currentLine = -1;

	JTextArea textArea = null;
	private JTextField textField;
	private JTextArea textRiffXml;
	
	/**
	 * Stop current playback
	 */
	public void Stop() {
		to_line.push(0, "Stop");
	}
	
	/**
	 * Pause current playback
	 */
	public void Pause() {
		to_line.push(1, "Pause");
	}
	
	/**
	 * Step forward
	 */
	public void Next() {
		to_line.push(2, "Next");
	}
	
	/**
	 * add riff
	 * @param riff riff as xml
	 */
	 
	public void AddThis(String riff) {
		to_line.push(3, riff);
	}

	/**
	 * Create the applet.
	 */
	public EmuApplet() {

		this.parent = this;
		// this.parent = null;

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		Panel panel = new Panel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		tabbedPane.addTab("Line", null, panel, null);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.TRAILING);
		panel.add(panel_1);

		JLabel lblNewLabel = new JLabel("Requested buffer size: ");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_1.add(lblNewLabel);

		textField = new JTextField();

		panel_1.add(textField);
		textField.setColumns(10);
		textField.setText("" + line_buffer_size);

		JButton btnNewButton = new JButton("Restart line!");
		panel_1.add(btnNewButton);

		JList list = new JList(lines);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				initLine(arg0.getFirstIndex());
			}
		});

		panel.add(list);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int current = currentLine;
				currentLine = -1;
				initLine(current);
			}
		});

		textArea = new JTextArea();

		JScrollPane scrollPane = new JScrollPane(textArea);
		tabbedPane.addTab("Debug", null, scrollPane, null);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Control", null, scrollPane_1, null);

		JToolBar toolBar = new JToolBar();
		scrollPane_1.setColumnHeaderView(toolBar);

		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				to_line.push(0, "Stop");
			}
		});
		toolBar.add(btnStop);

		JButton btnPause = new JButton("Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				to_line.push(1, "Pause");
			}
		});
		toolBar.add(btnPause);

		JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				to_line.push(2, "Next");
			}
		});
		toolBar.add(btnNext);

		JButton btnAddRiffXml = new JButton("Add Riff XML (below)");
		btnAddRiffXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				to_line.push(3, textRiffXml.getText());
			}
		});
		toolBar.add(btnAddRiffXml);
		final JFileChooser fcxml = new JFileChooser();
		final JFileChooser fcwav = new JFileChooser();
		fcxml.setFileFilter(new FileNameExtensionFilter("RIFF XML", "XML"));
		fcwav.setFileFilter(new FileNameExtensionFilter("WAVe audio", "WAV"));

		JButton btnSave = new JButton("Save XML");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (fcxml.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
					try {
						FileWriter writer = new FileWriter(fcxml
								.getSelectedFile());

						writer.write(textRiffXml.getText());
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		JSlider slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent arg0) {

				to_line.push(4,
						new Float(((JSlider) arg0.getSource()).getValue()));
			}
		});
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(25);
		slider.setPaintTicks(true);
		slider.setToolTipText("speed factor");
		slider.setValue(100);
		slider.setMaximum(200);
		toolBar.add(slider);
		toolBar.add(btnSave);

		JButton btnLoad = new JButton("Load XML");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (fcxml.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {

					byte[] buffer = new byte[(int) fcxml.getSelectedFile()
							.length()];

					InputStream s;
					try {
						s = new FileInputStream(fcxml.getSelectedFile());
						s.read(buffer);
						textRiffXml.setText(new String(buffer));
						s.close();
					} catch (FileNotFoundException e1) {

						e1.printStackTrace();
					} catch (IOException e2) {

						e2.printStackTrace();
					}

				}

			}
		});
		toolBar.add(btnLoad);

		JButton btnExport = new JButton("Export -> .WAV");

		btnExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (fcwav.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
					try {

						RiffExporter.Export(fcwav.getSelectedFile(),
								textRiffXml.getText(), parent);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}

			}
		});

		toolBar.add(btnExport);

		textRiffXml = new JTextArea();
		textRiffXml
				.setText("<?xml version=\"1.0\"?>\n"
						+ "<!DOCTYPE riff SYSTEM \"https://raw.github.com/war-immo/dev/master/dtd/riffxml.1.0.0.dtd\">\n"
						+ "<riff>\n"
						+ "	<chain Repeat=\"32\" bpm=\"180\">\n"
						+ "		<syn>\n"
						+ "			<hs length=\"4\" drum=\"kick\" part=\"0.25\"/>\n"
						+ "			<pattern length=\"4\" part=\"0.5\" drum=\"snare\">--+---+---</pattern>\n"
						+ "			<hs length=\"4\" drum=\"hh\" part=\"0.5\">\n"
						+ "				<control>openXclosed=0.1</control>\n"
						+ "			</hs>\n" + "		</syn>\n" + "	</chain>\n"
						+ "</riff>");
		textRiffXml.setBackground(Color.WHITE);
		textRiffXml.setFont(new Font("Courier", Font.BOLD, 16));
		scrollPane_1.setViewportView(textRiffXml);

		out("Build: " + build_nbr);
		out("Desired format: " + format.toString());

		tabbedPane.setSelectedIndex(2);

		initLine(lines.length - 1);
		list.setSelectedIndex(lines.length - 1);

		out("de.zorgk.drums.EmuApplet initalization completed.");
	}

	public void initLine(int nbr) {

		if (nbr == currentLine)
			return;
		currentLine = nbr;

		try {
			line_buffer_size = Integer.parseInt(textField.getText());
			out("line_buffer_size=" + line_buffer_size);
		} catch (NumberFormatException e) {

		}

		if (player != null) {
			out("Killing current player....");
			Thread killit = player;
			player = null;
			killit.interrupt();
		}

		out("Attemping to init line #" + nbr + ": " + lines[nbr]);

		SourceDataLine line = null;
		try {
			line = (SourceDataLine) AudioSystem.getLine(lines[nbr]);
		} catch (LineUnavailableException e) {
			out("Error: Line Unavailable!!");
			out(e.getStackTrace().toString());
			return;
		}

		playbackDriver pd = new playbackDriver(line);

		player = new Thread(pd);
		player.start();

	}

	public synchronized void cout(String data) {
		textArea.append(data);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	public synchronized void out(String data) {
		textArea.append(data + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	/**
	 * 
	 * create a JFrame and then run the applet
	 * 
	 * @param args
	 */

	public static void main(String[] args) {

		if (args.length > 0) {

			boolean print_help = false;

			if (args[0].equalsIgnoreCase("--export")) {

				if (args.length == 3) {

					EmuApplet applet = new EmuApplet();

					String riff = null;

					try {
						InputStreamReader reader = new InputStreamReader(
								new FileInputStream(args[1]), "UTF-8");

						StringBuffer file_data = new StringBuffer(1000);

						char[] buf = new char[1024];

						int num_read = 0;

						try {
							while ((num_read = reader.read(buf)) != -1) {
								String readData = String.valueOf(buf, 0,
										num_read);
								file_data.append(readData);
								buf = new char[1024];
							}
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						riff = file_data.toString();
					} catch (UnsupportedEncodingException e1) {

						e1.printStackTrace();
					} catch (FileNotFoundException e1) {

						e1.printStackTrace();
					}

					try {
						RiffExporter.Export(new File(args[2]), riff, applet);
					} catch (IOException e) {
						e.printStackTrace();
					}

					System.exit(0);

				} else
					print_help = true;

			} else
				print_help = true;

			if (print_help) {
				System.out
						.println("Usage: java -jar drumsEmu.jar [--export XML-INPUT WAV-OUTPUT]");
			}

		} else {

			EmuApplet applet = new EmuApplet();

			JFrame frame = new JFrame("de.zorgk.drums.EmuApplet");
			frame.getContentPane().add(applet);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
		}
	}
}
