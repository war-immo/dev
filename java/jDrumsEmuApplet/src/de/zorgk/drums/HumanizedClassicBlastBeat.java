package de.zorgk.drums;

import java.util.LinkedList;
import java.util.Random;

/**
 * class to test some concepts
 * 
 * @author immanuel
 *
 */
public class HumanizedClassicBlastBeat implements RiffInterface, Cloneable {
	
	boolean started;
	long beatsPerMinute;
	long samplesPerKick;
	long count;
	long startFrame;
	long nextKick;
	int uniformAccuracy;
	long openXclosed;
	long ordXtop;
	Random randomGenerator;
	float openClosed;
	float ordTop;
	float openClosed2Sigma;
	float ordTop2Sigma;
	
	HitInterface kick, snare, hihat;
		
	public HumanizedClassicBlastBeat(long bpm, long hits, float accuracyInMs, float openClosed, float openClosed2Sigma, float ordTop, float ordTop2Sigma, SamplerSetup sampler) {
		this.beatsPerMinute = bpm;
		this.count = hits;
		this.started = false;
		
		this.samplesPerKick = RiffInterface.framesPerSecond*60 / (2*beatsPerMinute);
		
		this.nextKick = 0;
		
		
		
		this.kick = sampler.instruments.get("kick");
		this.snare = sampler.instruments.get("snare");
		this.hihat = sampler.instruments.get("hh");
		
		try {
			this.openXclosed = this.hihat.getParameterNbrName("openXclosed");
		} catch (IllegalArgumentException e) {
			this.openXclosed = -1;
		}
		try {
			this.ordXtop = this.hihat.getParameterNbrName("openXclosed");
		} catch (IllegalArgumentException e) {
			this.ordXtop = -1;
		}
		
		this.uniformAccuracy = (int) (accuracyInMs*RiffInterface.framesPerSecond /1000.f);
		
		this.randomGenerator = new Random();
		
		this.openClosed = openClosed;
		this.openClosed2Sigma = openClosed2Sigma;
		
		this.ordTop = ordTop;
		this.ordTop2Sigma = ordTop2Sigma;
	}
	
	

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		if (!started) {
			started = true;
			
			startFrame = framestart;
			this.kick.hit(framestart+randomGenerator.nextInt(uniformAccuracy));
			this.snare.hit(framestart+randomGenerator.nextInt(uniformAccuracy));
			this.nextKick = 1;
			
			return false;
		}
		
		long next_kick_frame = startFrame + samplesPerKick*nextKick;
		
		if (framestart <= next_kick_frame && next_kick_frame < framenextstart) {
			if (nextKick >= count*2) return true;
			
			
			this.kick.hit(next_kick_frame+randomGenerator.nextInt(uniformAccuracy));
			if (1 == (nextKick&0x1)) {
				this.hihat.setFloat(openXclosed, openClosed + (float)randomGenerator.nextGaussian()*(0.5f*openClosed2Sigma));
				this.hihat.setFloat(ordXtop, ordTop + (float)randomGenerator.nextGaussian()*(0.5f*ordTop2Sigma));
				this.hihat.hit(next_kick_frame+randomGenerator.nextInt(uniformAccuracy));
			} else {
				this.snare.hit(next_kick_frame+randomGenerator.nextInt(uniformAccuracy));
			}
			this.nextKick ++;
		}
		
		
		return false;
	}

	
	@Override
	public RiffInterface getClone() throws CloneNotSupportedException  {
	
			return (RiffInterface)this.clone();
		
	}
}
