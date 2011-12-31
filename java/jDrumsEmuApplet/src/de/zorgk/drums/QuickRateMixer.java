package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * a mixing class for HitInterfaces that mixes the drum depending on the
 * duration between the last and the current hit
 * 
 * @author immanuel
 * 
 */

public class QuickRateMixer implements HitInterface {
	
	private HitInterface slow, quick;
	private long lastHitFrame;
	private float constantSlow, constantQuick, variableTotal, endInMs;
	
	public QuickRateMixer(HitInterface slow, HitInterface quick, float baseSlow, float baseQuick, float equilibriumMs) {
		this.slow = slow;
		this.quick = quick;
		this.lastHitFrame = 0;
		this.constantQuick = baseQuick;
		this.constantSlow = baseSlow;
		this.variableTotal = 1.f - (baseQuick+baseSlow);
		this.endInMs = equilibriumMs * 2.f;
	}

	@Override
	public void hit(long frame, float dB) {
		float ms_since_last = (1000*(frame-lastHitFrame))/HitInterface.sampleRate;
		
		float quickXslow = ms_since_last/endInMs;
		if (quickXslow > 1.f) quickXslow = 1.f;
		
		this.slow.hit(frame, dB + functionTables.GaintodB(this.constantSlow + variableTotal*quickXslow));
		this.quick.hit(frame, dB + functionTables.GaintodB(this.constantQuick + variableTotal*(1.f-quickXslow)));
		
		this.lastHitFrame = frame;
	}

	@Override
	public void stop(long frame) {
		this.slow.stop(frame);
		this.quick.stop(frame);
	}

	@Override
	public void setFloat(long nbr, float value) {
		this.slow.setFloat(nbr, value);
		this.quick.setFloat(nbr, value);
	}

	@Override
	public float getFloat(long nbr) {
		
		return this.slow.getFloat(nbr);
	}

	@Override
	public long howManyNamedParameters() {
		
		return this.slow.howManyNamedParameters();
	}

	@Override
	public long getParameterNbr(long id) {

		return this.slow.getParameterNbr(id);
	}

	@Override
	public String getParameterName(long nbr) {

		return this.slow.getParameterName(nbr);
	}

	@Override
	public long getParameterNbrName(String name) {
		return  this.slow.getParameterNbrName(name);
	}

	@Override
	public Object exportAllParameters() {
		Map<Long, Float> parameterValues = new TreeMap<Long, Float>();
		long count = this.howManyNamedParameters();
		for (long i = 0; i < count; ++i) {
			parameterValues.put(getParameterNbr(i),
					getFloat(getParameterNbr(i)));
		}

		return parameterValues;
	}

	@Override
	public void restoreParameters(Object previousState) {
		@SuppressWarnings("unchecked")
		Map<Long, Float> parameterValues = (Map<Long, Float>) previousState;

		for (Iterator<Long> it = parameterValues.keySet().iterator(); it
				.hasNext();) {
			long p = it.next();
			setFloat(p, parameterValues.get(p));
		}

	}

}
