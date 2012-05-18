
/**
 *	or-expression, will match either of the subexpressions and then
 *	munch the matched one. 
 */
public class OrRegExp
extends RegExp
{
	protected RegExp left;
	protected RegExp right;
	public OrRegExp(
		RegExp left,
		RegExp right)
	{
		this.left=left;assert(left!=null);
		this.right=right;assert(right!=null);
	}
	public RegExp left()
	{
		return left;
	}
	public RegExp right()
	{
		return right;
	}
	public RegExp reduce()
	{
		left=left.reduce();
		right=right.reduce();
		boolean l=(left instanceof EmptyRegExp);
		boolean r=(right instanceof EmptyRegExp);
		if(l&r)
		{
			return new EmptyRegExp();
		}
		return this;
	}
	public ExpType type()
	{
		return ExpType.Or;
	}
}
