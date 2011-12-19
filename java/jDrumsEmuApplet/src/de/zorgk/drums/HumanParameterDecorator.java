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
		private float parameterValue;
		private long parameterNumber;

		public DecoratedInterface(HitInterface drum, float sigma, long nbr, float value) {
			this.drum = drum;
			this.sigma = sigma;
			this.parameterNumber = nbr;
			this.parameterValue = value;
		}

		@Override
		public void hit(long frame) {
			float humanPart =  ((float)randomNumber.nextGaussian())*sigma;
			if (humanPart < -2.f*sigma) {
				humanPart = -2.f*sigma;
			} else if (humanPart > 2.f*sigma) {
				humanPart = 2.f*sigma;
			}
			
			drum.setFloat(this.parameterNumber, this.parameterValue+humanPart);
		
			drum.hit(frame);
		}

		@Override
		public void hit(long frame, float dB) {
			float humanPart =  ((float)randomNumber.nextGaussian())*sigma;
			if (humanPart < -2.f*sigma) {
				humanPart = -2.f*sigma;
			} else if (humanPart > 2.f*sigma) {
				humanPart = 2.f*sigma;
			}
			
			drum.setFloat(this.parameterNumber, this.parameterValue+humanPart);
			
			drum.hit(frame,dB);
		}

		@Override
		public void stop(long frame) {

			drum.stop(frame);
		}

		@Override
		public void setFloat(long nbr, float value) {
			if (nbr == this.parameterNumber) {
				this.parameterValue = value;
			} else
				drum.setFloat(nbr, value);
		}

		@Override
		public float getFloat(long nbr) {
			if (nbr == this.parameterNumber) {
				return this.parameterValue;
			}
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
			drum.setFloat(this.parameterNumber, this.parameterValue);
			return drum.exportAllParameters();
		}

		@Override
		public void restoreParameters(Object previousState) {
			drum.restoreParameters(previousState);
			this.parameterValue = drum.getFloat(this.parameterNumber);
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
			return new DecoratedInterface(drum, this.sigma, parameterNbr,drum.getFloat(parameterNbr));
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
