package de.zorgk.drums;

import java.util.LinkedList;

public class PatternedHitsRiff implements RiffInterface, Cloneable {

	private HitInterface drum;

	private boolean started;

	private long samplesPerHit;
	private long count;
	private long finalRest;
	private long startFrame;
	private long nextHit;
	private float dB;
	private int[] pattern;

	public PatternedHitsRiff(float beatsPerMinute, float beatsLength,
			float hitCount, HitInterface drum, float dB, int[] pattern) {
		this.drum = drum;
		this.dB = dB;
		this.started = false;

		this.samplesPerHit = (long) (((beatsLength
				* RiffInterface.framesPerSecond * 60.f)) / (beatsPerMinute));
		this.count = (long) hitCount;
		this.finalRest = (long) ((beatsLength * RiffInterface.framesPerSecond
				* 60.f * (hitCount - count)) / (beatsPerMinute));
		this.nextHit = 0;

		if (finalRest == 0) {
			this.count -= 1;
			this.finalRest = samplesPerHit;
		}

		if (pattern.length != 0)
			this.pattern = pattern;
		else {
			this.pattern = new int[1];
			this.pattern[0] = 1;
		}

	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		if (!started) {
			started = true;

			startFrame = framestart;
			if (pattern[(int) (nextHit % pattern.length)] != 0)
				drum.hit(framestart, dB);
			nextHit = 1;

			return false;
		}

		if (nextHit <= count) {

			long next_hit_frame = startFrame + samplesPerHit * nextHit;

			if (framestart <= next_hit_frame && next_hit_frame < framenextstart) {

				if (pattern[(int) (nextHit % pattern.length)] != 0)
					this.drum.hit(next_hit_frame, dB);

				this.nextHit++;
			}

		} else {
			long rest_until = startFrame + samplesPerHit * (nextHit - 1)
					+ finalRest;

			if (rest_until < framenextstart) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
		return (RiffInterface) this.clone();
	}

}
