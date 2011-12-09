package de.zorgk.drums;

import java.util.LinkedList;

public class ClassicBlastBeat implements RiffInterface {
	
	boolean started;
	long beatsPerMinute;
	long samplesPerKick;
	long count;
	long startFrame;
	long nextKick;
	
	HitInterface kick, snare, hihat;
	
	public ClassicBlastBeat(long bpm, long hits, SamplerSetup sampler) {
		this.beatsPerMinute = bpm;
		this.count = hits;
		this.started = false;
		
		this.samplesPerKick = RiffInterface.framesPerSecond*60 / (2*beatsPerMinute);
		
		this.nextKick = 0;
		
		this.kick = sampler.instruments.get("kick");
		this.snare = sampler.instruments.get("snare");
		this.hihat = sampler.instruments.get("hh");
	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		if (!started) {
			started = true;
			
			startFrame = framestart;
			this.kick.hit(framestart);
			this.snare.hit(framestart);
			this.nextKick = 1;
			
			return false;
		}
		
		long next_kick_frame = startFrame + samplesPerKick*nextKick;
		
		if (framestart <= next_kick_frame && next_kick_frame < framenextstart) {
			if (nextKick >= count*2) return true;
			
			
			this.kick.hit(next_kick_frame);
			if (1 == (nextKick&0x1)) {
				this.hihat.hit(next_kick_frame);
			} else {
				this.snare.hit(next_kick_frame);
			}
			this.nextKick ++;
		}
		
		
		return false;
	}

}
