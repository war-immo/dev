package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class HihatMixer implements HitInterface {

	float dB = 1.f;
	float ordXtop = 0.2f;
	float rimXbel = 0.8f;
	float openXclosed = 0.2f;

	HitInterface bel, oord, cord, otop, ctop;

	public HihatMixer(HitInterface oord, HitInterface otop, HitInterface cord,
			HitInterface ctop, HitInterface bel) {
		this.bel = bel;
		this.cord = cord;
		this.oord = oord;
		this.ctop = ctop;
		this.otop = otop;
	}


	@Override
	public void hit(long frame, float dB) {
		this.dB = dB;

		bel.hit(frame, dB + functionTables.GaintodB((1.f - rimXbel)));
		cord.hit(
				frame,
				dB
						+ functionTables.GaintodB(rimXbel * ordXtop
								* (1.f - openXclosed)));
		oord.hit(frame,
				dB + functionTables.GaintodB(rimXbel * ordXtop * openXclosed));
		ctop.hit(
				frame,
				dB
						+ functionTables.GaintodB(rimXbel * (1.f - ordXtop)
								* (1.f - openXclosed)));
		otop.hit(
				frame,
				dB
						+ functionTables.GaintodB(rimXbel * (1.f - ordXtop)
								* openXclosed));
	}

	@Override
	public void stop(long frame) {
		bel.stop(frame);
		oord.stop(frame);
		cord.stop(frame);
		otop.stop(frame);
		ctop.stop(frame);

	}

	@Override
	public void setFloat(long nbr, float value) {
		if (value < 0.f)
			value = 0.f;
		if (value > 1.f)
			value = 1.f;

		switch ((int) nbr) {
		case 0:
			openXclosed = value;
			break;
		case 1:
			ordXtop = value;
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
			return openXclosed;
		case 1:
			return ordXtop;
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
			return "openXclosed";
		case 1:
			return "ordXtop";
		case 2:
			return "rimXbel";
		}
		return "unreachableCode";
	}

	@Override
	public long getParameterNbrName(String name) {
		if (name.equalsIgnoreCase("openXclosed"))
			return 0;
		if (name.equalsIgnoreCase("ordXtop"))
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
