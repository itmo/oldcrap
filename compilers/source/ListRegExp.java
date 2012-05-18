/**
 *	list regular expression. will match a concatenatio of its subexpressions. 
 */
public class ListRegExp
extends RegExp
{
	protected RegExp left;
	protected RegExp right;
	public ListRegExp(RegExp left,RegExp right)
	{
		this.left=left;assert(left!=null);
		this.right=right;assert(right!=null);
		//TBD if either is emptyregexp, this equals the other
		//TBD if both are empty.equals empty
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
		if(l)
		{
			return right;
		}
		if(r)
		{
			return left;
		}
		return this;		
	}
	public ExpType type()
	{
		return ExpType.List;
	}
}
