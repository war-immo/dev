package de.zorgk.drums;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class RideMixer implements HitInterface {
	float dB=1.f;
	float rimXord=0.f;
	float rimXbel=1.f;
	float nrmXelv=0.f;

	
	HitInterface bel,nord,eord,rim;
	
	public RideMixer(HitInterface nord, HitInterface  eord,HitInterface  rim, HitInterface bel) {
		this.bel = bel;
		this.eord = eord;
		this.nord = nord;
		this.rim = rim;
	}

	@Override
	public void hit(long frame) {
		bel.hit(frame,dB+functionTables.GaintodB((1.f-rimXbel)));
		nord.hit(frame,dB+functionTables.GaintodB(rimXbel*rimXord*(1.f-nrmXelv)));
		eord.hit(frame,dB+functionTables.GaintodB(rimXbel*rimXord*nrmXelv));
		rim.hit(frame,dB+functionTables.GaintodB(rimXbel*(1.f-rimXord)));
	}

	@Override
	public void hit(long frame, float dB) {
		this.dB = dB;
		
		bel.hit(frame,dB+functionTables.GaintodB((1.f-rimXbel)));
		nord.hit(frame,dB+functionTables.GaintodB(rimXbel*rimXord*(1.f-nrmXelv)));
		eord.hit(frame,dB+functionTables.GaintodB(rimXbel*rimXord*nrmXelv));
		rim.hit(frame,dB+functionTables.GaintodB(rimXbel*(1.f-rimXord)));
	}

	@Override
	public void stop(long frame) {
		bel.stop(frame);
		nord.stop(frame);
		eord.stop(frame);
		rim.stop(frame);
		
	}

	@Override
	public void setFloat(long nbr, float value) {
		if (value < 0.f) value = 0.f;
		if (value > 1.f) value = 1.f;
		
		
		switch((int)nbr) {
		case 0:
			nrmXelv = value; break;
		case 1:
			rimXord = value; break;
		case 2:
			rimXbel = value; break;
		}

	}

	@Override
	public float getFloat(long nbr) {
		switch((int)nbr) {
		case 0:
			return nrmXelv;
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
		if ((id <0)||(id>3)) throw new IllegalArgumentException("id unknown");
		return id;
	}

	@Override
	public String getParameterName(long nbr) { 
		if ((nbr<0)||(nbr>3)) throw new IllegalArgumentException("parameter unknown");
		switch ((int)nbr) {
		case 0:
			return "nrmXelv";
		case 1:
			return "rimXord";
		case 2:
			return "rimXbel";
		}
		return "unreachableCode";
	}

	@Override
	public long getParameterNbrName(String name) {
		if (name == "nrmXelv") return 0;
		if (name == "rimXord") return 1;
		if (name == "rimXbel") return 2;
			
		throw new IllegalArgumentException("parameter unknown");
		
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
