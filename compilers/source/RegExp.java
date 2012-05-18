
/**
 *	abstract baseclass for regularexpressions
 */
public abstract class RegExp
{
	/**
	 *	@return type of the regexp
	 */
	public abstract ExpType type();
	/**
	 *	reduce regexp according to basic rules to remove 
	 *	redundancies.
	 *	@return the regexp in reduced form or itself
	 *		if irreducible. should also call reduce
	 *		for children before reducing itself
	 */
	public RegExp reduce()
	{
		return this;
	}
}
