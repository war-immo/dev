package de.zorgk.drums;

/**
 * interface for decorating hitinterfaces
 * 
 * @author immanuel
 * 
 */

public interface HitDecorator {

	/**
	 * decorates a given HitInterface
	 * 
	 * @param drum
	 *            interface to be decorated
	 * @param drumName
	 *            name of the drum that is decorated
	 * @return new interface incorporating the decoration
	 */
	public HitInterface decorate(HitInterface drum, String drumName);

	/**
	 * a HitDecorator cancels another HitDecorator, if for all HitInterfaces h:
	 * this.decorate(h) equals this.decorate(other.decorate(h))
	 * 
	 * for example a decorator that overrides the dB parameter to -2. cancels
	 * any hit decorator modifying just the dB parameter.
	 * 
	 * this function may return true even though both HitDecorators would not
	 * cancel out each other, for instance to ensure that only one version of
	 * your class affects the drum output
	 * 
	 * @param other
	 * @param drumName
	 *            name of the drum that is decorated
	 * @return true, if this interface cancels other
	 */
	public boolean cancelsOut(HitDecorator other, String drumName);

}
