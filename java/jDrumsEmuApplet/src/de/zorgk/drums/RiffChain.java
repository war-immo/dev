package de.zorgk.drums;

import java.util.LinkedList;

import javax.management.RuntimeErrorException;

/**
 * class that implements riff chains, for convenience, because it simply puts
 * all its riffs on the stack and says bye bye!
 * 
 * @author immanuel
 * 
 */

public class RiffChain implements RiffInterface {

	private RiffInterface[] riffs;

	public RiffChain(RiffInterface[] riffs) {
		this.riffs = riffs;
	}
	
	public RiffChain(RiffInterface[] riffs, int repeat) {
		this.riffs = new RiffInterface[riffs.length*repeat];
		int j=0;
		for (int i=0;i<repeat;++i) {
			for (int c=0;c<riffs.length;++c,++j) {
				if (i>0)
					try {
						this.riffs[j] = riffs[c].getClone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				else
					this.riffs[j] = riffs[c];
			}
		}
	}

	public RiffChain(RiffInterface a, RiffInterface b) {
		this.riffs = new RiffInterface[2];
		this.riffs[0] = a;
		this.riffs[1] = b;
	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		if (this.riffs.length == 0)
			return true;

		if (stack.peek() != this)
			throw new RuntimeErrorException(null,
					"RiffChain must be on top for timeElapse!!");
		
		stack.pop();

		for (int j = this.riffs.length - 1; 0 <= j; j--) {
			stack.push(riffs[j]);
		}

		return stack.peek().timeElapse(framestart, framenextstart, stack, sampler);
	}
	
	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
		RiffInterface[] copy = new RiffInterface[riffs.length];
		for (int i=0;i<riffs.length;++i)
			copy[i] = riffs[i].getClone();
	
		return new RiffChain(copy);
	}

}
