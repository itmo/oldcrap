
/**
 *	empty regexp.. matches everything and munches nothing. 
 *	just reduced unless a part of "or" expression.
 */
public class EmptyRegExp
extends RegExp
{
	public ExpType type()
	{
		return ExpType.Empty;
	}
}
