package de.zorgk.drums;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.BorderLayout;
import javax.swing.border.EtchedBorder;
import java.awt.Panel;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.awt.Font;
import java.awt.Color;

public class EmuApplet extends JApplet {

	private static final int build_nbr = 4;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3218208748277098775L;

	private static final int sRate = 44100;
	private static final int channels = 2;
	private static final int buffer_frames = 64;
	private volatile int line_buffer_size = 4096 * channels * 4;

	private volatile Thread player = null;

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
				sampler = new SamplerSetup(format);
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
					switch(nbr) {
					case 0:
						trampoline = new LinkedList<RiffInterface>();
						trampoline.push(new EmptyRiff());
						break;
					case 1:
						trampoline.push(new EmptyRiff());
						break;
					case 2:
						if (trampoline.size()>1)
							trampoline.pop();
						break;
					case 3:
						try {
							trampoline.push(RiffXmlToRiffInterface.transformXml((String)o, sampler));
						} catch (Exception e) {
							out("\n\n");
							StackTraceElement[] s = e.getStackTrace();
							for (int i=0; i < s.length; ++i) {
								out(s[i].toString());
								
							}
							out("... ERROR: "+e.getMessage());
						} 
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
	 * Create the applet.
	 */
	public EmuApplet() {

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbedPane
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		Panel panel = new Panel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);
		tabbedPane.addTab("Line", null, panel, null);

		JList list = new JList(lines);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				initLine(arg0.getFirstIndex());
			}
		});

		panel.add(list);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);

		JLabel lblNewLabel = new JLabel("Requested buffer size: ");
		panel_1.add(lblNewLabel);

		textField = new JTextField();

		panel_1.add(textField);
		textField.setColumns(10);
		textField.setText("" + line_buffer_size);

		JButton btnNewButton = new JButton("Restart line!");
		panel_1.add(btnNewButton);
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
		
		textRiffXml = new JTextArea();
		textRiffXml.setText("<riff>\n<chain Repeat=\"10\" bpm=\"160\">\n   <syn>\n   <hs length=\"4\" drum=\"kick\" part=\"0.25\"/>\n   <pattern length=\"4\" part=\"0.5\" drum=\"snare\">+-+--++-</pattern>\n   <hs length=\"4\" drum=\"hh\" part=\"0.5\" />\n   </syn>\n</chain>\n</riff>");
		textRiffXml.setBackground(Color.WHITE);
		textRiffXml.setFont(new Font("Courier", Font.BOLD, 16));
		scrollPane_1.setViewportView(textRiffXml);
		
		out("Build: " + build_nbr);
		out("Desired format: " + format.toString());

		initLine(0);

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
}
