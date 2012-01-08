package de.zorgk.drums;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.sun.media.sound.WaveFileWriter;

public class RiffExporter {
	
	public static class EndOfRiff implements RiffInterface {
		
		private boolean eof;
		
		public EndOfRiff() {
			eof = false;
		}
		
		public boolean isDone() {
			return eof;
		}
		
		@Override
		public boolean timeElapse(long framestart, long framenextstart,
				LinkedList<RiffInterface> stack, SamplerSetup sampler) {
			eof = true;
			
			return false;
		}

		@Override
		public RiffInterface getClone() {

			return this;
		}
	}
	
	public static void Export(File file, String riff, EmuApplet parent) throws IOException {
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		EndOfRiff eor = new EndOfRiff();
		
		LinkedList<RiffInterface> trampoline = new LinkedList<RiffInterface>();
		trampoline.push(eor);
		
		byte[] b_buffer = new byte[EmuApplet.channels * 4 * EmuApplet.buffer_frames];
		long[] i_buffer = new long[EmuApplet.channels * EmuApplet.buffer_frames];

		long frames_elapsed = 0;
		
		AudioFormat format = new AudioFormat(new Float(EmuApplet.sRate), 32, EmuApplet.channels, true,
				true);
		
		AudioFormat format_extern = new AudioFormat(new Float(EmuApplet.sRate), 32, EmuApplet.channels, true,
				false);
		
		SamplerSetup sampler = null;
		try {
			sampler = new SamplerSetup(format, parent);
		} catch (UnsupportedAudioFileException e) {
			System.out.println("UnsuppoertedAudioFileException " + e.toString());

		} catch (IOException e) {
			System.out.println("IOException " + e.toString());
		} catch (URISyntaxException e) {
			System.out.println("URISyntaxException " + e.toString());
		}

		if (sampler == null) {
			System.out.println("Error initializing sampler...");
			return;
		}
		
		for (int idx = 0; idx < i_buffer.length; ++idx) {
			i_buffer[idx] = 0;
		}

		for (int idx = 0; idx < b_buffer.length; ++idx) {
			b_buffer[idx] = 0;
		}
		
		try {
			trampoline.push(RiffXmlToRiffInterface
					.transformXml(riff, sampler));
		} catch (Exception e) {
			System.out.println("\n\n");
			StackTraceElement[] s = e.getStackTrace();
			for (int i = 0; i < s.length; ++i) {
				System.out.println(s[i].toString());

			}
			System.out.println("... ERROR: " + e.getMessage());
			return;
		}
		
		while (eor.isDone() == false) {
			
			if (trampoline.peek().timeElapse(frames_elapsed,
					frames_elapsed + EmuApplet.buffer_frames, trampoline, sampler)) {
				trampoline.pop();
			}

			for (int idx = 0; idx < i_buffer.length; ++idx) {
				i_buffer[idx] = 0;
			}
			
			sampler.addToBuffer(frames_elapsed, i_buffer, EmuApplet.buffer_frames, 0);

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
				/* little-endian output format */
				b_buffer[b_idx + 3] = (byte) (f >> 24);
				b_buffer[b_idx + 2] = (byte) (f >> 16);
				b_buffer[b_idx + 1] = (byte) (f >> 8);
				b_buffer[b_idx] = (byte) (f);
			}
			frames_elapsed += EmuApplet.buffer_frames;
			
			buffer.write(b_buffer, 0, EmuApplet.buffer_frames * EmuApplet.channels * 4);
			
		}
		
		System.out.println("exported frames = "+(frames_elapsed));
		
		ByteArrayInputStream is = new ByteArrayInputStream(buffer.toByteArray());
		AudioInputStream ais = new AudioInputStream(is, format_extern, frames_elapsed);
		
		FileOutputStream os = new FileOutputStream(file);
		
		WaveFileWriter writer = new WaveFileWriter();
		writer.write(ais, AudioFileFormat.Type.WAVE, os);
		
		os.close();
		
		
		
	}

}
