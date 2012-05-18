import java.util.*;
/**
 *	an interpreting matcher for regular expressions
 *	
 */
public class InterpretingMatcher
implements Matcher
{
	protected RegExp r;
	public InterpretingMatcher(RegExp r)
	{
		assert(r!=null);
		this.r=r;
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
		
		LinkedList<State> stateStack=
			new LinkedList<State>();
		State st=new State();
			st.start=pos;
			st.end=end;
			st.s=s; //!! 
		st.visited.add(r);
		
		stateStack.addFirst(st);
		while(stateStack.size()>0)
		{
			st=stateStack.removeFirst();
			while(st.visited.size()>0)
			{
				if(!process(st,stateStack))
				{
					//stop processing this one
					st.visited.clear();
				}else{
					if(st.start==st.end && st.visited.size()==0)
						return true;
				}
			}
		}
				
		return false;
	}
	/**
	 *	process given state 
	 */
	protected boolean process(State s,LinkedList<State> stateStack)
	{
		RegExp r=s.visited.removeFirst();
		switch(r.type())
		{
			case List:
				ListRegExp lr=(ListRegExp)r;
				//match left&right
				s.visited.addFirst(lr.right());
				s.visited.addFirst(lr.left());
				break;
			case Char:
				CharRegExp ch=(CharRegExp)r;
				if(s.start==s.end)
					return false;
				if(ch.getChar()!=s.s.charAt(s.start))
					return false;
				s.start++;
				break;
			case Or:{
				OrRegExp or=(OrRegExp)r;
				//push two states. return false
				State a=s.copy();
				State b=s.copy();
				a.visited.addFirst(or.left());
				b.visited.addFirst(or.right());
				stateStack.addFirst(b);
				stateStack.addFirst(a);
				return false;} //drop this state
			case Star:{
				StarRegExp st=(StarRegExp)r;
				RegExp mul=st.mul();
				State a=s.copy();
				State b=s.copy();
					b.visited.addFirst(mul);
				State c=s.copy();
					c.visited.addFirst(st);
					c.visited.addFirst(mul);
				stateStack.addFirst(a);
				stateStack.addFirst(b);
				stateStack.addFirst(c);
				
				return false;//kill this state
				}
			case Empty:
				break;
		}
		return true;
	}
	class State
	{
		int start;
		int end;
		String s;
		LinkedList<RegExp> visited=
			new LinkedList<RegExp>();
		public State copy()
		{
			State st=new State();
			st.visited.addAll(visited);
			st.start=start;
			st.end=end;
			st.s=s;
			return st;
		}
	}
}
