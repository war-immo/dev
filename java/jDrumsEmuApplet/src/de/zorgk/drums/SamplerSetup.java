package de.zorgk.drums;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * handles the setup of drum instruments: responsible for the creation of the
 * HitInterfaces and the name map
 * 
 * @author immanuel
 * 
 */
public class SamplerSetup {

	public static final String[] named_resources = { "kick1",
			"../res/kick-a.wav", "kick2", "../res/kick-b.wav", "snare",
			"../res/snare.wav", "hh-bell", "../res/hh-bell.wav", "hh-cord",
			"../res/hh-closed-0.wav", "hh-ctop", "../res/hh-closed-1.wav",
			"hh-oord", "../res/hh-open-0.wav", "hh-otop",
			"../res/hh-open-1.wav", "rd-bell", "../res/ride_bell.wav",
			"rd-nord", "../res/ride_0.wav", "rd-eord", "../res/ride_1.wav",
			"rd-rim", "../res/ride_rim.wav", "15-ord", "../res/cr15_0.wav",
			"15-top", "../res/cr15_1.wav", "15-rim", "../res/cr15_2.wav",
			"15-bell", "../res/cr15_b.wav", "18-ord", "../res/cr18_0.wav",
			"18-top", "../res/cr18_1.wav", "18-rim", "../res/cr18_2.wav",
			"18-bell", "../res/cr18_b.wav", "12-ord", "../res/sp12_0.wav",
			"12-top", "../res/sp12_1.wav", "12-rim", "../res/sp12_2.wav",
			"12-bell", "../res/sp12_b.wav", "8-ord", "../res/sp8_0.wav",
			"8-bell", "../res/sp8_b.wav", "9-ord", "../res/sp9_0.wav",
			"19-ord", "../res/ch19_0.wav", "19-top", "../res/ch19_1.wav" };

	public static final String[][] round_robin_names = { { "kick", "kick1",
			"kick2" } };

	public SampledDrum[] drums;
	/**
	 * maps drum names to hit interfaces. NOTICE: only lowercase drum names can be accessed via Riff XML
	 */
	public Map<String, HitInterface> instruments;

	public SamplerSetup(AudioFormat format)
			throws UnsupportedAudioFileException, IOException,
			URISyntaxException {

		drums = new SampledDrum[named_resources.length / 2];
		for (int i = 0; i < drums.length; ++i) {
			drums[i] = new SampledDrum(named_resources[i * 2 + 1], format);
			drums[i].setOffsetDb(-10.f);
		}

		instruments = new HashMap<String, HitInterface>();
		for (int i = 0; i < drums.length; ++i) {
			instruments.put(named_resources[i * 2], drums[i]);
		}

		for (int i = 0; i < round_robin_names.length; ++i) {
			RoundRobin r = new RoundRobin(
					instruments.get(round_robin_names[i][1]));
			for (int j = 2; j < round_robin_names[i].length; ++j) {
				r.add(instruments.get(round_robin_names[i][j]));
			}
			instruments.put(round_robin_names[i][0], r);
		}

		instruments.put("hh", new HihatMixer(instruments.get("hh-oord"),
				instruments.get("hh-otop"), instruments.get("hh-cord"),
				instruments.get("hh-ctop"), instruments.get("hh-bell")));

		instruments.put("ride", new RideMixer(instruments.get("rd-nord"),
				instruments.get("rd-eord"), instruments.get("rd-rim"),
				instruments.get("rd-bell")));

		instruments.put("15", new CrashMixer(instruments.get("15-ord"),
				instruments.get("15-top"), instruments.get("15-rim"),
				instruments.get("15-bell")));

		instruments.put("18", new CrashMixer(instruments.get("18-ord"),
				instruments.get("18-top"), instruments.get("18-rim"),
				instruments.get("18-bell")));

		instruments.put("12", new CrashMixer(instruments.get("12-ord"),
				instruments.get("12-top"), instruments.get("12-rim"),
				instruments.get("12-bell")));

		instruments.put("19", new ChinaMixer(instruments.get("19-ord"),
				instruments.get("19-top")));

		instruments.put("8", new SplashMixer(instruments.get("8-ord"),
				instruments.get("8-bell")));

		instruments.put("9", instruments.get("9-ord"));
		
		/**
		 * some level adjustments
		 */
		
		((SampledDrum) instruments.get("snare")).setOffsetDb(0.f);
		
	}
	
	/**
	 * 
	 * @return a freshly created ArrayList containing the default decorators
	 */
	
	public ArrayList<HitDecorator> getNewDefaultDecoratorList() {
		ArrayList<HitDecorator> decoratorChain = new ArrayList<HitDecorator>();
		
		decoratorChain.add(new HumanTimingDecorator(4,8,12));
		decoratorChain.add(new HumanVelocityDecorator(0,2,2));
		decoratorChain.add(new HumanParameterDecorator(0.16f, "hh", "ordXtop"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "hh", "openXclosed"));
		decoratorChain.add(new HumanParameterDecorator(0.1f, "hh", "rimXbel"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "ride", "nrmXelv"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "ride", "rimXord"));
		decoratorChain.add(new HumanParameterDecorator(0.1f, "ride", "rimXbel"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "15", "ordXtop"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "15", "rimXord"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "15", "rimXbel"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "18", "ordXtop"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "18", "rimXord"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "18", "rimXbel"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "12", "ordXtop"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "12", "rimXord"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "12", "rimXbel"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "19", "ordXtop"));
		decoratorChain.add(new HumanParameterDecorator(0.2f, "8", "rimXbel"));
		
		return decoratorChain;
	}

	public void addToBuffer(long framestart, long[] o_buffer, int samples,
			int offset) {
		for (int i = 0; i < drums.length; ++i) {
			drums[i].addToBuffer(framestart, o_buffer, samples, offset);
		}
	}
}
