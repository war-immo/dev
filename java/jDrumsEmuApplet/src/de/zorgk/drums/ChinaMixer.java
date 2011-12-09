package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ChinaMixer implements HitInterface {
	float dB=1.f;
	float ordXtop=0.f;

	
	HitInterface ord,top;
	
	public ChinaMixer(HitInterface ord, HitInterface  top) {

		this.top = top;
		this.ord = ord;

	}

	@Override
	public void hit(long frame) {

		ord.hit(frame,dB+functionTables.GaintodB(1.f-ordXtop));
		top.hit(frame,dB+functionTables.GaintodB(ordXtop));

	}

	@Override
	public void hit(long frame, float dB) {
		this.dB = dB;
		
		ord.hit(frame,dB+functionTables.GaintodB(1.f-ordXtop));
		top.hit(frame,dB+functionTables.GaintodB(ordXtop));
	}

	@Override
	public void stop(long frame) {

		ord.stop(frame);
		top.stop(frame);

		
	}

	@Override
	public void setFloat(long nbr, float value) {
		if (value < 0.f) value = 0.f;
		if (value > 1.f) value = 1.f;
		
		
		switch((int)nbr) {
		case 0:
			ordXtop = value; break;

		}

	}

	@Override
	public float getFloat(long nbr) {
		switch((int)nbr) {
		case 0:
			return ordXtop;

		default:
				return 0;
		}
		
	}

	@Override
	public long howManyNamedParameters() {
		return 1;
	}

	@Override
	public long getParameterNbr(long id) {
		if (id != 0) throw new IllegalArgumentException("id unknown");
		return 0;
	}

	@Override
	public String getParameterName(long nbr) { 
		if (nbr != 0) throw new IllegalArgumentException("parameter unknown");
		return "ordXtop";
	}

	@Override
	public long getParameterNbrName(String name) {
		if (name != "ordXtop") throw new IllegalArgumentException("parameter unknown");
		return 0;
	}
	
	@Override
	public Object exportAllParameters() {
		Map<Long, Float> parameterValues = new TreeMap<Long,Float>();
		long count = this.howManyNamedParameters();
		for (long i=0; i < count; ++i) {
			parameterValues.put(getParameterNbr(i), getFloat(getParameterNbr(i)));
		}
		
		return parameterValues;
	}
	
	@Override
	public void restoreParameters(Object previousState) {
		@SuppressWarnings("unchecked")
		Map<Long, Float> parameterValues = (Map<Long, Float>) previousState;
		
		for (Iterator<Long> it = parameterValues.keySet().iterator(); it.hasNext();)  {
			long p = it.next();
			setFloat(p,parameterValues.get(p));
		}
	}
	
}
