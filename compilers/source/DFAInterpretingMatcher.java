import java.util.*;
/**
 *	an interpreting matcher for regular expressions using 
 *	DFA's
 */
public class DFAInterpretingMatcher
implements Matcher
{
	protected FA fa;
	/**
	 *	likes to have an FA , it will try to convert it to a
	 *	DFA if it is not already
	 */
	public DFAInterpretingMatcher(FA fa)
	{
		assert(fa!=null);
		if(!fa.isDeterministic())
			fa=fa.determinize();
		this.fa=fa;
	}
	public boolean match(String s)
	{
		return match(s,0,s.length());
	}
	public boolean match(String s,int off,int len)
	{
		assert((off+len)<=s.length());
		int pos=off;
		int end=pos+len;
		String state=fa.getStartState();
		while(true)
		{
			if(pos==end)
			{
				return fa.isEndState(state);
			}			
			char c=s.charAt(pos);
			state=fa.getTransition(state,c);
			if(state==null) //no transition for that char
				return false;//fail
			pos++;
		}
	}
}
