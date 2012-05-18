import java.io.*;

/**
 *	thrown when the parser detects something weird
 */
public class ParserException
extends IOException
{
	public ParserException(String s)
	{
		super(s);
	}
}
