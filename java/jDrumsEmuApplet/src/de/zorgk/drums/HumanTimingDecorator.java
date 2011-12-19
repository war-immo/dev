package de.zorgk.drums;

import java.util.Random;

/**
 * decorates HitInterfaces with human timing
 * 
 * @author immanuel
 * 
 */

public class HumanTimingDecorator implements HitDecorator {

	private static final Random randomNumber = new Random();

	private float sigmaKick, sigmaSnare, sigmaOther;

	private class DecoratedInterface implements HitInterface {

		private HitInterface drum;
		private double sigma;

		public DecoratedInterface(HitInterface drum, double sigma) {
			this.drum = drum;
			this.sigma = sigma;
		}



		@Override
		public void hit(long frame, float dB) {
			double offset = randomNumber.nextGaussian() * sigma;
			if (offset < 0.) {
				offset = -offset;
			}
			if (offset > 2. * sigma) {
				offset = 2. * sigma;
			}

			long new_frame = frame + (long) offset;

			drum.hit(new_frame, dB);
		}

		@Override
		public void stop(long frame) {
			double offset = randomNumber.nextGaussian() * sigma;
			if (offset < 0.) {
				offset = -offset;
			}
			if (offset > 2. * sigma) {
				offset = 2. * sigma;
			}

			long new_frame = frame + (long) offset;

			drum.stop(new_frame);
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

	public HumanTimingDecorator(float kickMs, float snareMs, float otherMs) {
		this.sigmaKick = kickMs*HitInterface.sampleRate/2000.f;
		this.sigmaSnare = snareMs*HitInterface.sampleRate/2000.f;
		this.sigmaOther = otherMs*HitInterface.sampleRate/2000.f;
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
