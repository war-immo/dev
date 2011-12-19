package de.zorgk.drums;

import java.util.Random;

/**
 * decorates HitInterfaces with human "parametering" :)
 * 
 * @author immanuel
 * 
 */

public class HumanParameterDecorator implements HitDecorator {

	private static final Random randomNumber = new Random();

	private float sigma;
	private String parameterName;
	private String nameStartsWith;

	private class DecoratedInterface implements HitInterface {

		private HitInterface drum;
		private float sigma;
		private long parameterNumber;

		public DecoratedInterface(HitInterface drum, float sigma, long nbr) {
			this.drum = drum;
			this.sigma = sigma;
			this.parameterNumber = nbr;
		}

		@Override
		public void hit(long frame, float dB) {
			float human_part =  ((float)randomNumber.nextGaussian())*sigma;
			if (human_part < -2.f*sigma) {
				human_part = -2.f*sigma;
			} else if (human_part > 2.f*sigma) {
				human_part = 2.f*sigma;
			}
			
			float parameter_value=drum.getFloat(this.parameterNumber);
			
			drum.setFloat(this.parameterNumber, parameter_value+human_part);
			
			drum.hit(frame,dB);
			
			drum.setFloat(this.parameterNumber, parameter_value);
		}

		@Override
		public void stop(long frame) {

			drum.stop(frame);
		}

		@Override
		public void setFloat(long nbr, float value) {
			drum.setFloat(nbr, value);
		}

		@Override
		public float getFloat(long nbr) {
			
			return drum.getFloat(nbr);
		}

		@Override
		public long howManyNamedParameters() {
			return drum.howManyNamedParameters();
		}

		@Override
		public long getParameterNbr(long id) {
			return drum.getParameterNbr(id);
		}

		@Override
		public String getParameterName(long nbr) {
			return drum.getParameterName(nbr);
		}

		@Override
		public long getParameterNbrName(String name) {
			return drum.getParameterNbrName(name);
		}

		@Override
		public Object exportAllParameters() {
			
			return drum.exportAllParameters();
		}

		@Override
		public void restoreParameters(Object previousState) {
			drum.restoreParameters(previousState);
			
		}

	};

	public HumanParameterDecorator(float plusMinus, String namePrefix, String parameterName) {
		this.sigma = plusMinus/2.f;
		this.nameStartsWith = namePrefix;		
		this.parameterName = parameterName;
	}

	@Override
	public HitInterface decorate(HitInterface drum, String drumName) {
		if (drumName.startsWith(this.nameStartsWith)) {
			long parameterNbr;
			try {
				parameterNbr = drum.getParameterNbrName(this.parameterName);
			} catch (IllegalArgumentException e) {
				/**
				 * IllegalArgumentException -> Parameter name unknown
				 */
				return drum;
			}
			return new DecoratedInterface(drum, this.sigma, parameterNbr);
		}
		return drum;
	}

	@Override
	public boolean cancelsOut(HitDecorator other, String drumName) {
		if (other.getClass() == this.getClass()) {
			HumanParameterDecorator other2 = (HumanParameterDecorator)other;
			if (other2.parameterName.equalsIgnoreCase(this.parameterName)) {
				if (other2.nameStartsWith.startsWith(this.nameStartsWith))
					return true;
			}					
		}
			
		return false;
	}

}
