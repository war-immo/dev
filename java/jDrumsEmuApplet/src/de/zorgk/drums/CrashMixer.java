package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class CrashMixer implements HitInterface {

	float rimXord = 0.2f;
	float rimXbel = 0.8f;
	float ordXtop = 0.2f;

	HitInterface bel, ord, top, rim;

	public CrashMixer(HitInterface ord, HitInterface top, HitInterface rim,
			HitInterface bel) {
		this.bel = bel;
		this.top = top;
		this.ord = ord;
		this.rim = rim;
	}



	@Override
	public void hit(long frame, float dB) {


		bel.hit(frame, dB + functionTables.GaintodB((1.f - rimXbel)));
		ord.hit(frame,
				dB
						+ functionTables.GaintodB(rimXbel * rimXord
								* (1.f - ordXtop)));
		top.hit(frame,
				dB + functionTables.GaintodB(rimXbel * rimXord * ordXtop));
		rim.hit(frame, dB + functionTables.GaintodB(rimXbel * (1.f - rimXord)));
	}

	@Override
	public void stop(long frame) {
		bel.stop(frame);
		ord.stop(frame);
		top.stop(frame);
		rim.stop(frame);

	}

	@Override
	public void setFloat(long nbr, float value) {
		if (value < 0.f)
			value = 0.f;
		if (value > 1.f)
			value = 1.f;

		switch ((int) nbr) {
		case 0:
			ordXtop = value;
			break;
		case 1:
			rimXord = value;
			break;
		case 2:
			rimXbel = value;
			break;
		}

	}

	@Override
	public float getFloat(long nbr) {
		switch ((int) nbr) {
		case 0:
			return ordXtop;
		case 1:
			return rimXord;
		case 2:
			return rimXbel;
		default:
			return 0;
		}

	}

	@Override
	public long howManyNamedParameters() {
		return 3;
	}

	@Override
	public long getParameterNbr(long id) {
		if ((id < 0) || (id > 3))
			throw new IllegalArgumentException("id unknown");
		return id;
	}

	@Override
	public String getParameterName(long nbr) {
		if ((nbr < 0) || (nbr > 3))
			throw new IllegalArgumentException("parameter unknown");
		switch ((int) nbr) {
		case 0:
			return "ordXtop";
		case 1:
			return "rimXord";
		case 2:
			return "rimXbel";
		}
		return "unreachableCode";
	}

	@Override
	public long getParameterNbrName(String name) {
		if (name.equalsIgnoreCase("ordXtop"))
			return 0;
		if (name.equalsIgnoreCase("rimXord"))
			return 1;
		if (name.equalsIgnoreCase("rimXbel"))
			return 2;

		throw new IllegalArgumentException("parameter unknown");

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
