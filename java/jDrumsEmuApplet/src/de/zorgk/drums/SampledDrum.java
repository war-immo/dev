package de.zorgk.drums;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.sound.sampled.*;

public class SampledDrum implements HitInterface {

	public AudioInputStream stream = null;

	public static final int channels = 2;

	static final Random rnd = new Random();

	public String debug = "";

	long length;
	int[] buffer;

	long relative_frame = 0;
	long level31 = functionTables.toLvl31(functionTables.dBtoGain(0));
	long next_hit = -1;
	long next_level31 = functionTables.toLvl31(functionTables.dBtoGain(0));

	@Override
	public void setFloat(long nbr, float value) {
	}

	@Override
	public float getFloat(long nbr) {
		return 0;
	}

	public void hit(long frame) {
		next_level31 = level31;
		next_hit = frame;
	}

	public void hit(long frame, float dB) {
		next_level31 = functionTables.toLvl31(functionTables.dBtoGain(dB));
		next_hit = frame;
	}

	public void stop(long frame) {
		next_level31 = 0;
		next_hit = frame;
	}

	public SampledDrum(String name, AudioFormat format)
			throws UnsupportedAudioFileException, IOException,
			URISyntaxException {

		String new_name = name.substring(2);

		URL url = getClass().getResource(new_name);
		if (url != null) {
			debug += "JAR " + new_name;
			debug += "\n" + url.toString();

			InputStream r = getClass().getResourceAsStream(new_name);
			File file = File.createTempFile("WAVRES", "FROMJAR.wav");
			FileOutputStream w = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int count = 0;
			while ((count = r.read(buf)) >= 0) {
				w.write(buf, 0, count);
			}
			w.close();
			debug += "\nTMP=" + file.getAbsolutePath();
			stream = AudioSystem.getAudioInputStream(file);
			file.deleteOnExit();
		} else {
			debug += "File " + name;
			File file = new File(name);
			stream = AudioSystem.getAudioInputStream(file);
		}

		if (stream == null)
			return;

		try {

			AudioInputStream converted = AudioSystem.getAudioInputStream(
					format, stream);
			stream = converted;

			length = stream.getFrameLength();
			byte[] bbuffer = new byte[(int) length * 4 * channels];
			debug += "\nsize=" + stream.read(bbuffer) + "\nlen=" + length;

			buffer = new int[(int) length * channels];
			for (int i = 0; i < buffer.length; ++i) {
				int idx = i << 2;
				buffer[i] = 0;
				for (int b = 0; b < 4; b++) {
					if (bbuffer[idx + b] < 0) {
						buffer[i] = (buffer[i] << 8) + ((int) bbuffer[idx + b])
								+ 0x100;

					} else
						buffer[i] = (buffer[i] << 8) + (int) bbuffer[idx + b];
				}
			}

			stream.close();
		} catch (IllegalArgumentException cerr) {
			AudioFormat format_hack = new AudioFormat(format.getSampleRate(),
					16, channels, true, true);
			AudioInputStream converted = AudioSystem.getAudioInputStream(
					format_hack, stream);
			debug += "\n16->32bit hack";
			stream = converted;
			length = stream.getFrameLength();
			byte[] bbuffer = new byte[(int) length * 2 * channels];
			debug += "\nsize=" + stream.read(bbuffer) + "\nlen=" + length;

			buffer = new int[(int) length * channels];
			for (int i = 0; i < buffer.length; ++i) {
				int idx = i << 1;
				buffer[i] = 0;
				for (int b = 0; b < 2; b++) {
					if (bbuffer[idx + b] < 0) {
						buffer[i] = (buffer[i] << 8) + ((int) bbuffer[idx + b])
								+ 0x100;
					} else
						buffer[i] = (buffer[i] << 8) + (int) bbuffer[idx + b];
				}
				buffer[i] = buffer[i] << 16;
			}
		}

		relative_frame = -length - 1;

	}

	public void addToBuffer(long framestart, long[] o_buffer, int samples,
			int offset) {

		if (framestart == next_hit) {
			if (next_level31 > 0) {
				level31 = next_level31;
				relative_frame = framestart;
			} else {
				relative_frame = -length;
			}

		} else if ((framestart < next_hit) && (next_hit < framestart + samples)) {
			addToBuffer(framestart, o_buffer, (int) (next_hit - framestart),
					offset);
			addToBuffer(next_hit, o_buffer,
					(int) (samples - (next_hit - framestart)),
					(int) (next_hit - framestart) * channels + offset);
			return;
		}

		long pos = framestart - relative_frame;
		if (pos < length) {
			if (length - pos < samples) {
				samples = (int) (length - pos);
			}
			int ipos = (int) (pos * channels);
			for (int i = 0; i < samples * channels; ++i) {
				try {
					o_buffer[i + offset] += (int) (((long) buffer[ipos + i] * level31) >> 31);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println(e.toString());
					System.out.println(offset);
					System.out.println(i);
					System.out.println(ipos);
					System.out.println(pos);
				}
			}
		}
	}

	@Override
	public long howManyNamedParameters() {
		return 0;
	}

	@Override
	public long getParameterNbr(long id) {
		throw new IllegalArgumentException("id unknown");
	}

	@Override
	public String getParameterName(long nbr) {
		throw new IllegalArgumentException("parameter unknown");
	}

	@Override
	public long getParameterNbrName(String name) {
		throw new IllegalArgumentException("parameter unknown");
	}

	@Override
	public Object exportAllParameters() {
		Map<Long, Float> parameterValues = new TreeMap<Long, Float>();
		long count = this.howManyNamedParameters();
		for (long i = 0; i < count; ++i) {
			parameterValues.put(getParameterNbr(i),
					getFloat(getParameterNbr(i)));
		}

		return parameterValues;
	}

	@Override
	public void restoreParameters(Object previousState) {
		@SuppressWarnings("unchecked")
		Map<Long, Float> parameterValues = (Map<Long, Float>) previousState;

		for (Iterator<Long> it = parameterValues.keySet().iterator(); it
				.hasNext();) {
			long p = it.next();
			setFloat(p, parameterValues.get(p));
		}
	}

}
