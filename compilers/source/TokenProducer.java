/**
 *	interface for tokenizers which generate tokens for 
 *	parsers.
 */
public interface TokenProducer
{
	/**
	 *	@return true if there is more tokens
	 */
	public boolean hasMoreTokens()
		throws ScannerException;
	
	/**
	 *	@return the next token without munching it
	 *		will return EOF type token on EOF
	 */
	public Token lookahead()
		throws ScannerException;
	/**
	 *	@return next token and munch it'
	 *		will return EOF type token when EOF
	 */
	public Token nextToken()
		throws ScannerException;
}
