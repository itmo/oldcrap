/**
 *	character regular expression, will match exactly one char
 *	and then munch it. 
 */
public class CharRegExp
extends RegExp
{
	protected char c;
	public CharRegExp(Token t)
	{
		//System.out.println("new charRegExp:>"+t.c+"<");
		assert(t!=null);
		assert(t.type==TokenType.CHAR);
		this.c=t.c;
	}
	public char getChar()
	{
		return c;
	}
	public ExpType type()
	{
		return ExpType.Char;
	}
}
