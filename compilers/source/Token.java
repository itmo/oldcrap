
/**
 *	a single token when tokenizing the regular expression
 */
public class Token
{
	TokenType type;
	char c;
	int position;
	String context;
	public String debug()
	{
		return "col:"+position+">"+context+"<";
	}
}
