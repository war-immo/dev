package de.zorgk.drums;

/**
 * Interface for objects that behave like a sampled drum
 * 
 * @author immanuel
 *
 */
public interface HitInterface {
	
	/**
	 * hit the drum (again) and if necessary stop the current sound, use last or default dB value
	 * 
	 * @param frame    hit at this frame
	 */
	public void hit(long frame);
	/**
	 * hit at frame, use new dB value
	 * 
	 * @param frame
	 * @param dB
	 */
	public void hit(long frame, float dB); 
	
	/**
	 * stop the current sound
	 * @param frame     stop at this frame
	 */
	public void stop(long frame); 
	
	/**
	 * Set a parameter value specific to the drum
	 * @param nbr    parameter number
	 * @param value new value
	 */
	public void setFloat(long nbr, float value);
	
	/**
	 * Get a parameter value specific to the drum
	 * @param nbr
	 * @return  the current value of the parameter
	 */
	public float getFloat(long nbr);  

	/**
	 * 
	 * @return number of named parameters the drum offers
	 */
	public long howManyNamedParameters();
	
	/**
	 * returns the parameter number of the named parameter
	 * @param    index of the named parameter
	 * @return   parameter number to be used with setFloat/getFloat
	 * @throws IllegalArgumentException 
	 */
	public long getParameterNbr(long id);
	
	/**
	 * returns the name of the parameter
	 * @param nbr   parameter number used with setFloat/getFloat
	 * @return  name of the parameter
	 * @throws IllegalArgumentException
	 */
	public String getParameterName(long nbr);
	
	/**
	 * returns the parameter number for a given parameter name
	 * @param name
	 * @return   parameter number to be used with setFloat/getFloat
	 * @throws IllegalArgumentException 
	 */
	public long getParameterNbrName(String name);
	
	/**
	 * export the currently set drum parameters
	 * 
	 * @return an object that can be used to restore all parameters
	 */
	public Object exportAllParameters();
	
	/**
	 * restore the parameters
	 * 
	 * @param previousState   the object must be an object that has been retrieved by export all Parameters
	 * 
	 */
	public void restoreParameters(Object previousState);
}
