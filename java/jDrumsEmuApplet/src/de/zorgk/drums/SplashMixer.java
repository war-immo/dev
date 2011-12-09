package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class SplashMixer implements HitInterface {
	float dB=1.f;
	float rimXbel=1.f;
	
	
	HitInterface bel,ord;
	
	public SplashMixer(HitInterface ord,  HitInterface bel) {
		this.bel = bel;
		
		this.ord = ord;

	}

	@Override
	public void hit(long frame) {
		bel.hit(frame,dB+functionTables.GaintodB((1.f-rimXbel)));
		ord.hit(frame,dB+functionTables.GaintodB(rimXbel));

	}

	@Override
	public void hit(long frame, float dB) {
		this.dB = dB;
		
		bel.hit(frame,dB+functionTables.GaintodB((1.f-rimXbel)));
		ord.hit(frame,dB+functionTables.GaintodB(rimXbel));
	}

	@Override
	public void stop(long frame) {
		bel.stop(frame);
		ord.stop(frame);

		
	}

	@Override
	public void setFloat(long nbr, float value) {
		switch((int)nbr) {
		
		case 2:
			rimXbel = value; break;
		}

	}

	@Override
	public float getFloat(long nbr) {
		switch((int)nbr) {
		
		case 2:
			return rimXbel;
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
		return 2;
	}

	@Override
	public String getParameterName(long nbr) { 
		if (nbr != 0) throw new IllegalArgumentException("parameter unknown");
		return "rimXbel";
	}

	@Override
	public long getParameterNbrName(String name) {
		if (name != "rimXbel") throw new IllegalArgumentException("parameter unknown");
		return 2;
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
