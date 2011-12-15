package de.zorgk.drums;

import java.util.LinkedList;

/**
 * this class is dedicated to john cage.
 * @author immanuel
 *
 */

public class RestRiff implements RiffInterface, Cloneable {
	
	private long samples_left;
	
	public RestRiff(long beatsPerMinute, float beats) {
		this.samples_left = (long)((beats * RiffInterface.framesPerSecond * 60)) / (beatsPerMinute);
	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		
		this.samples_left -= framenextstart - framestart;

		return samples_left <= 0;
	}

	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
		
		return (RiffInterface) this.clone();
	}

}
