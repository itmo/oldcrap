import java.io.*;
/**
 *	thrown when something is wrong with the scanner. 
 */

public class ScannerException
extends IOException
{
	public ScannerException(String s)
	{
		super(s);
	}
}
