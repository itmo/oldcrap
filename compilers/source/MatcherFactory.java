import java.util.*;
import java.io.*;
/**
 *	factory to compile given regular expressions to matcher objects which
 *	can be used to match strings
 */
public class MatcherFactory
{
	public Matcher generateMatcher(String regexp,boolean compiled)
	throws ParserException,
		ScannerException
	{
		//generate matcher
		
		Tokenizer tok=new Tokenizer(regexp);
		
		Parser scanner=new Parser(tok);
		RegExp r=scanner.run();
		
		/*	reduce whatever possible*/
		r.reduce();
		
		/*
			-RegEx from Sun takes around 130-250ms to match aa*
			-InterpretingMatcher is a pure regexp interpreting
			 recursive matcher aa* is 3000+ms 
			-DFAInterpretingMatcher is an interpreting matcher
			 using DFA and a while loop. will take around 
			 850ms to match aa*
			-FAMatcherFactory will generate a compiled version
			 , a prototype hand assembly matches aa* in 
			 25ms or so
			 	-production aa* spaghetti 36ms
		*/
			FA fa=new FA(r);
			fa=fa.determinize();
			//fa=fa.minimize();
		Matcher ret=null;
		if(compiled)
		{
			
			//ret=new InterpretingMatcher(r);
			
			try{
				ret=new FAMatcherFactory().generate(fa);
			}catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
		}else{
			//ret=new InterpretingMatcher(r);
			ret=new DFAInterpretingMatcher(fa);
		}
		return ret;
	}
}
