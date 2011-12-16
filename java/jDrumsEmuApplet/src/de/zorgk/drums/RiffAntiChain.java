package de.zorgk.drums;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * implements a meta-Riff that plays riffs in parallel. watch out for side
 * effects of your riffs yourself!
 * 
 * @author immanuel
 * 
 */

public class RiffAntiChain implements RiffInterface {

	private ArrayList<LinkedList<RiffInterface>> parallelStacks;

	public RiffAntiChain(RiffInterface[] riffs) {
		parallelStacks = new ArrayList<LinkedList<RiffInterface>>();
		for (int i = 0; i < riffs.length; ++i) {
			LinkedList<RiffInterface> stack = new LinkedList<RiffInterface>();
			stack.push(riffs[i]);
			parallelStacks.add(stack);
		}
	}

	public RiffAntiChain(RiffInterface a, RiffInterface b) {
		parallelStacks = new ArrayList<LinkedList<RiffInterface>>();
		LinkedList<RiffInterface> stack = new LinkedList<RiffInterface>();
		stack.push(a);
		parallelStacks.add(stack);

		stack = new LinkedList<RiffInterface>();
		stack.push(b);
		parallelStacks.add(stack);
	}

	/**
	 * clone
	 * 
	 * @param parallel
	 */

	private RiffAntiChain(ArrayList<LinkedList<RiffInterface>> parallel) {
		parallelStacks = new ArrayList<LinkedList<RiffInterface>>();
		for (Iterator<LinkedList<RiffInterface>> it = parallel.iterator(); it
				.hasNext();) {
			LinkedList<RiffInterface> stack = new LinkedList<RiffInterface>();
			LinkedList<RiffInterface> original = it.next();
			for (int i = 0; i < original.size(); ++i) {
				try {
					stack.add(original.get(i).getClone());
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			parallelStacks.add(stack);
		}
	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {

		boolean still_going = false;

		Iterator<LinkedList<RiffInterface>> it;

		for (it = parallelStacks.iterator(); it.hasNext();) {
			LinkedList<RiffInterface> local_stack = it.next();
			if (local_stack.isEmpty() == false) {
				if (local_stack.peek().timeElapse(framestart, framenextstart,
						local_stack, sampler))
					local_stack.pop();
				if (local_stack.isEmpty() == false)
					still_going = true;
			}
		}

		return !still_going;
	}

	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
		return new RiffAntiChain(parallelStacks);
	}

}
