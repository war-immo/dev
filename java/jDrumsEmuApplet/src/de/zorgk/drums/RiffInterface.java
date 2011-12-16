package de.zorgk.drums;

import java.util.*;

/**
 * Interface for objects that will play a drum riff
 * 
 * @author immanuel
 * 
 */
public interface RiffInterface {

	static long framesPerSecond = 44100;

	/**
	 * do necessary stuff for the given time interval in frames.
	 * 
	 * @param framestart
	 *            start of interval
	 * @param framenextstart
	 *            start of next interval (one frame after interval ends)
	 * @param stack
	 *            for modification of the RiffInterface-stack by the object
	 * @param sampler
	 *            sampler object (use for hitting drums etc.)
	 * @return true to be removed from stack (after reaching the end of the
	 *         riff)
	 */
	boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler);

	/**
	 * clone the interface object, such that it can be processed independently
	 * 
	 * @return copy
	 */
	RiffInterface getClone() throws CloneNotSupportedException;
}
