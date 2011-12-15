package de.zorgk.drums;

import java.util.LinkedList;


public class SimpleHitsRiff implements RiffInterface, Cloneable {

	private HitInterface drum;

	private boolean started;
	private long beatsPerMinute;
	private long samplesPerHit;
	private long count;
	private long startFrame;
	private long nextHit;
	private float dB;

	public SimpleHitsRiff(long beatsPerMinute, float beatsLength,
			long hitCount, HitInterface drum, float dB) {
		this.drum = drum;
		this.dB = dB;
		this.started = false;
		this.beatsPerMinute = beatsPerMinute;
		this.samplesPerHit =(long) ( (beatsLength
				* RiffInterface.framesPerSecond * 60))
				/ (beatsPerMinute);
		this.count = hitCount;
		this.nextHit = 0;
		
		System.out.println(samplesPerHit);

	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		if (!started) {
			started = true;

			startFrame = framestart;
			drum.hit(framestart, dB);
			nextHit = 1;


			return false;
		}

		long next_hit_frame = startFrame + samplesPerHit * nextHit;

		if (framestart <= next_hit_frame && next_hit_frame < framenextstart) {
			if (nextHit >= count)
			{

				return true;
			}

			this.drum.hit(next_hit_frame, dB);

			this.nextHit++;
		} 

		return false;
	}

	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
		return (RiffInterface) this.clone();
	}

}
