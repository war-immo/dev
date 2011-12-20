package de.zorgk.drums;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Riff for changing parameter values of drums etc...
 * 
 * @author immanuel
 *
 */

public class ControlRiff implements RiffInterface, Cloneable {
	
	private class SetParameterStruct {
		HitInterface drum;
		long parameter;
		float value;
		
		public SetParameterStruct(HitInterface drum, long parameter, float value) {
			this.drum = drum;
			this.parameter = parameter;
			this.value = value;
		}
	}
	
	private ArrayList<SetParameterStruct> changes;
	
	public ControlRiff() {
		this.changes = new ArrayList<SetParameterStruct>();
	}
	
	public void setParameter(HitInterface drum, long parameter, float value) {
		this.changes.add(new SetParameterStruct(drum, parameter, value));
	}

	@Override
	public boolean timeElapse(long framestart, long framenextstart,
			LinkedList<RiffInterface> stack, SamplerSetup sampler) {
		
		/**
		 * do parameter changes
		 */
		
		for (Iterator<SetParameterStruct> it=changes.iterator();it.hasNext();) {
			SetParameterStruct parameter = it.next();
			parameter.drum.setFloat(parameter.parameter, parameter.value);
		}
		
		
		/**
		 * call the next RiffInterface on the stack...
		 */
		
		stack.pop();
		
		if (stack.isEmpty())
			return false;
		
		return stack.peek().timeElapse(framestart, framenextstart, stack, sampler);
	}

	@Override
	public RiffInterface getClone() throws CloneNotSupportedException {
	 
		return (RiffInterface)this.clone();
	}

}
