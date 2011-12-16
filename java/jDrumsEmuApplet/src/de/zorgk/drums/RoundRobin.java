package de.zorgk.drums;

import java.util.*;

public class RoundRobin implements HitInterface {

	int current_nbr = 0;
	float dB = 0.f;
	ArrayList<HitInterface> round;

	public RoundRobin(HitInterface first) {
		round = new ArrayList<HitInterface>();
		round.add(first);
	}

	public void add(HitInterface h) {
		round.add(h);
	}

	@Override
	public void hit(long frame) {
		this.hit(frame, dB);
	}

	@Override
	public void hit(long frame, float dB) {
		this.dB = dB;
		current_nbr += 1;
		if (current_nbr >= round.size())
			current_nbr = 0;
		round.get(current_nbr).hit(frame, dB);
	}

	@Override
	public void stop(long frame) {
		for (int i = 0; i < round.size(); ++i)
			round.get(i).stop(frame);
	}

	@Override
	public void setFloat(long nbr, float value) {
		for (int i = 0; i < round.size(); ++i)
			round.get(i).setFloat(nbr, value);
	}

	@Override
	public float getFloat(long nbr) {
		return round.get(0).getFloat(nbr);
	}

	@Override
	public long howManyNamedParameters() {
		return round.get(0).howManyNamedParameters();
	}

	@Override
	public long getParameterNbr(long id) {
		return round.get(0).getParameterNbr(id);
	}

	@Override
	public String getParameterName(long nbr) {
		return round.get(0).getParameterName(nbr);
	}

	@Override
	public long getParameterNbrName(String name) {
		return round.get(0).getParameterNbrName(name);
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
