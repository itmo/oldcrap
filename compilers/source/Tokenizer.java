/**
 *	tokenizes the given string and passes the stuff on to the given
 *	consumer
 */
public class Tokenizer
implements TokenProducer
{
	protected String regexp;
	protected Token la;
	protected int pos=0;
	public Tokenizer(String regexp)
	{
		this.regexp=regexp;assert(regexp!=null);
	}
	public boolean hasMoreTokens()
	throws ScannerException
	{
		return lookahead().type!=TokenType.EOF;
	}
	public Token lookahead()
	throws ScannerException
	{
		if(la==null)
		{
			la=nextToken();
		}
		return la;
	}
	public Token nextToken()
	throws ScannerException
	{
		if(la!=null)
		{
			Token t=la;
			la=null;
			return t;
		}
		Token t=new Token();
		if(pos<regexp.length())
		{
			char c=regexp.charAt(pos);
			t.position=pos;
			int st=pos-5;if(st<0)st=0;
			int en=pos+5;if(en>regexp.length())en=regexp.length();
			t.context=regexp.substring(st,en);
			switch(c)
			{
				case '(':
					t.type=TokenType.LPAREN;
					break;
				case ')':
					t.type=TokenType.RPAREN;
					break;
				case '*':
					t.type=TokenType.STAR;
					break;
				case '+':
					t.type=TokenType.PLUS;
					break;
				case '?':
					t.type=TokenType.QUESTIONMARK;
					break;
				case '|':
					t.type=TokenType.PIPE;
					break;
				case '\\':
					t.type=TokenType.CHAR;
					pos++;
					if(pos>=regexp.length())
						throw new ScannerException(
							"Bad regular expression, escape at the end of expression");
					t.c=regexp.charAt(pos);
					break;
				default:
					t.type=TokenType.CHAR;	
					t.c=regexp.charAt(pos);				
					break;
			}
			pos++;			
		}else{
			t.type=TokenType.EOF;
			return t;
		}
		return t;
	}
}
