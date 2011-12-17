package de.zorgk.drums;

import java.util.Random;

/**
 * decorates HitInterfaces with human timing
 * 
 * @author immanuel
 * 
 */

public class HumanVelocityDecorator implements HitDecorator {

	private static final Random randomNumber = new Random();

	private float sigmaKick, sigmaSnare, sigmaOther;

	private class DecoratedInterface implements HitInterface {

		private HitInterface drum;
		private double sigma;
		private float dB;

		public DecoratedInterface(HitInterface drum, double sigma) {
			this.drum = drum;
			this.sigma = sigma;
		}

		@Override
		public void hit(long frame) {
			double offset = randomNumber.nextGaussian() * sigma;
			if (offset < -2.*sigma) {
				offset = -2.*sigma;
			}
			if (offset > 2. * sigma) {
				offset = 2. * sigma;
			}
			
			drum.hit(frame,(float) (dB+offset));
		}

		@Override
		public void hit(long frame, float dB) {
			double offset = randomNumber.nextGaussian() * sigma;
			if (offset < -2.*sigma) {
				offset = -2.*sigma;
			}
			if (offset > 2. * sigma) {
				offset = 2. * sigma;
			}
			
			this.dB = dB;
			
			
			drum.hit(frame,(float) (dB+offset));
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

	public HumanVelocityDecorator(float kickDb, float snareDb, float otherDb) {
		this.sigmaKick = kickDb/2.f;
		this.sigmaSnare = snareDb/2.f;
		this.sigmaOther = otherDb/2.f;
	}

	@Override
	public HitInterface decorate(HitInterface drum, String drumName) {
		if (drumName.startsWith("kick"))
			return new DecoratedInterface(drum, sigmaKick);
		if (drumName.startsWith("snare"))
			return new DecoratedInterface(drum, sigmaSnare);

		return new DecoratedInterface(drum, sigmaOther);
	}

	@Override
	public boolean cancelsOut(HitDecorator other, String drumName) {
		if (other.getClass() == this.getClass())
			return true;
		return false;
	}

}
