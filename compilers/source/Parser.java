/**
 *	a parser for regular expressions. handcoded recursive descent 
 *	ad-hoccish
 */

public class Parser
{
	protected TokenProducer tp;
	public Parser(TokenProducer t)
	{
		this.tp=t;assert(t!=null);
	}
	public RegExp run()
	throws ScannerException,
		ParserException
	{
		return listExp();
	}
	/**
	 *	return a list expr after munching parenthesis
	 *	will lookahead and much the STAR if parenthesis ends with it
	 */
	protected RegExp parExp()
	throws ParserException,ScannerException
	{
		//System.out.println("parexp");
	
		//munch LPAREN
		tp.nextToken();
		
		RegExp r=listExp();
		
		switch(tp.lookahead().type)
		{
			case EOF:	
				throw new ParserException("EOF inside parenthesis");
			case RPAREN:
				tp.nextToken();
				if(tp.lookahead().type==TokenType.STAR)
				{
					tp.nextToken(); //munch star
					r=new StarRegExp(r);
				}
				break;
			case PLUS:
			case QUESTIONMARK:
			case STAR:
			case LPAREN:
			case PIPE:
			case CHAR:
				assert(false);
		}
		return r;		
	}
	/**
	 *	process a list of regexps 
	 *	will trust charexp and parexp to munch stars after them
	 */
	protected RegExp listExp()
	throws ParserException,ScannerException
	{
		//System.out.println("listexp");	
		RegExp r=null;
		while(tp.hasMoreTokens())
		{
			switch(tp.lookahead().type)
			{
				case EOF:
					assert(false);
				case RPAREN:
					if(r==null)
						return new EmptyRegExp();
					else
						return r;
				case STAR:
					if(r==null)
						throw new ParserException("listexpression starts with STAR:"+tp.lookahead().debug());
					r=starExp(r);
					break;
				case PLUS:
					if(r==null)
						throw new ParserException("listexpression starts with PLUS:"+tp.lookahead().debug());
					r=plusExp(r);
					break;
				case QUESTIONMARK:
					if(r==null)
						throw new ParserException("listexpression starts with ?:"+tp.lookahead().debug());
					r=questionExp(r);
					break;
				case LPAREN:
					if(r!=null)
						r=new ListRegExp(r,parExp());
					else
						r=parExp();
					break;
				case PIPE:
					if(r==null)
						r=new EmptyRegExp();
					r=orExp(r);
					break;
				case CHAR:
					if(r!=null)
						r=new ListRegExp(r,charExp());
					else
						r=charExp();
					break;				
			}
		}
		if(r==null)
			r=new EmptyRegExp();
		return r;
	}
	/**
	 *	munch one char. and if there is a following star, munch it too
	 */
	protected RegExp charExp()
	throws ParserException,ScannerException
	{
		//System.out.println("charexp");
		RegExp r=new CharRegExp(tp.nextToken());
		switch(tp.lookahead().type)
		{
			case STAR:
				r=starExp(r);
				break;
			case PLUS:
				r=plusExp(r);
				break;
			case QUESTIONMARK:
				r=questionExp(r);
				break;
		}
		return r;
	}
	/**
	 *	munch star and starify the expression
	 */
	protected RegExp starExp(RegExp r)
	throws ParserException,ScannerException
	{
		//System.out.println("starexp");
		assert(r!=null);
		tp.nextToken(); //munch star
		return new StarRegExp(r);
	}
	/**
	 *	munch a plus and plusify expression
	 */
	protected RegExp plusExp(RegExp r)
	throws ParserException,ScannerException
	{
		assert(r!=null);
		tp.nextToken(); //munch plus
		return new ListRegExp(r,new StarRegExp(r));
	}
	/**
	 *	munch a questionmark and questionmarkize expression
	 */
	protected RegExp questionExp(RegExp r)
	throws ParserException,ScannerException
	{
		assert(r!=null);
		tp.nextToken(); //munch '?' 
		return new OrRegExp(new EmptyRegExp(),r);	
	}
	/**
	 *	munch the PIPE and continue with a listexp
	 */
	protected RegExp orExp(RegExp leftSide)
	throws ParserException,ScannerException
	{
		//System.out.println("orexp");
	
		tp.nextToken(); //munch pipe
		switch(tp.lookahead().type)
		{
			case PIPE: 
				throw new ParserException("attempting to || ? no dual pipes please"+tp.lookahead().debug());
			case STAR:
				throw new ParserException("Star cannot exist after |"+tp.lookahead().debug());
			case PLUS:
			case QUESTIONMARK:
				throw new ParserException("plus or questionmark cannot exist after | "+tp.lookahead().debug());
			case EOF:
			case RPAREN:
				//empty right side of OR
				return new OrRegExp(leftSide,new EmptyRegExp());
			case CHAR:
			case LPAREN:
				return new OrRegExp(leftSide,listExp());
		}
		assert(false);
		return null;
	}
}
