/**
 *	starregexp. will match zero to many times the given
 *	subexpression. 
 */
public class StarRegExp
extends RegExp
{
	protected RegExp mul;
	public StarRegExp(RegExp m)
	{
		mul=m;assert(m!=null);
	}
	public RegExp mul()
	{
		return mul;
	}
	public RegExp reduce()
	{
		mul=mul.reduce();
		if(mul instanceof EmptyRegExp)
			return mul;
		return this;
	}
	public ExpType type()
	{
		return ExpType.Star;
	}
}
