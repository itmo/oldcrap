import java.util.*;
/**
 *	describes a Finite Automaton. with methods to find out if it is
 *	deterministic or not and to determinize it if/when required
 */
public class FA
{
	protected HashMap<String,HashSet<String>> epsilons=
		new HashMap<String,HashSet<String>>();
	protected HashMap<String,State> states=
		new HashMap<String,State>();
	protected HashMap<String,HashMap<Character,ArrayList<Transition>>> transitions=
		new HashMap<String,HashMap<Character,ArrayList<Transition>>>();
	public FA()
	{
		states.put("start",new State("start"));
	}
	/**
	 *	generate FA from regexp, result might be 
	 *	NFA or DFA 
	 */
	public FA(RegExp r)
	{
		assert(r!=null);
		construct(r);
	}
	public FA(String regexp)
	throws ScannerException,ParserException
	{
		assert(regexp!=null);
		Tokenizer tok=new Tokenizer(regexp);
		Parser scanner=new Parser(tok);
		RegExp r=scanner.run();
		construct(r);
	}
	protected void construct(RegExp r)
	{
		//TBD 
		states.put("start",new State("start"));
		String lastState="start";
		lastState=construct(r,lastState,null);
		
		//when we get the last expression, make the last state endstate
		setEndState(lastState,true);
		removeEpsilons();
	}
	/**
	 *	remove epsilon transitions one by one 
	 */
	protected void removeEpsilons()
	{		
		if(epsilons.size()==0)
			return;
		/*	add epsilon transitions for each 
			double-epsilon*/
		boolean change=true;
		while(change)
		{
			change=false;
			String[][] eps=getEpsilons();
			for(int i=0;i<eps.length;i++)
			{
				HashSet<String> tos=
					epsilons.get(eps[i][1]);
				
				if(tos!=null&&tos.size()>0)
				{
					String[] toss=tos.toArray(new String[0]);
					for(int j=0;j<toss.length;j++)
						if(!toss[j].equals(eps[i][0]))
							change|=addEpsilon(eps[i][0],toss[j]);
				}
			}
		}
		/*
			make shortcuts for epsilon transitions*/
		String[][] eps=getEpsilons();
		for(int i=0;i<eps.length;i++)
		{
			String from=eps[i][0];
			String to=eps[i][1];
			//get all transitions to epsilon from
				//connect them to eps-to
			Transition[] trans;
			trans=getTransitionsTo(from);
			for(int j=0;j<trans.length;j++)
			{
				//TBD should we check for self-connection
				addTransition(
					trans[i].from(),
					trans[i].getChar(),
					to);
			}
			//get all transitions from eps-to
				//connect eps-from to eps-to->to
			trans=getTransitions(to);
			for(int j=0;j<trans.length;j++)
			{
				//TBD should we check for self-connection
				addTransition(
					from,
					trans[i].getChar(),
					trans[i].to());
			}
		}
		/*
			set end states */
		change=true;
		while(change)
		{
			change=false;
			
			for(int i=0;i<eps.length;i++)
			{
				if(isEndState(eps[i][1])&&
				   !isEndState(eps[i][0]))
				{
					setEndState(eps[i][0],true);
					change=true;
				}
			}
		}
		/*	remove epsilons*/
		epsilons.clear();
		/*
			-for each epsilon
				!!!needs a hashmap from->arrayof(to) to avoid 
				   n2 problems
				-check if the destination has an epsilon transition
				-if this would not create an epsilon-loop.
				-if it does, add an e transition from first from
				 to last to
					-add this to list of epsilons
					-start over until no more additions
			-for each epsilon
				-for the from end
					-for each transition to from end
						-add similar to to end
				-for the to end
					-for each transition from to end
						-add similar from  from 
						 end to destination
			-while change 
				-for each epsilon whose to state is an endstate
				 and from is not, set from as end
			-check for dead states?
			-
		*/
	}
	/**
	 *	@return epsilon transitions as an array of String[2] where
	 *		from is in  0 and to is in 1 
	 */
	protected String[][] getEpsilons()
	{
		ArrayList<String[]> ret=
			new ArrayList<String[]>();
		
		Iterator<String> it=epsilons.keySet().iterator();
		while(it.hasNext())
		{
			String from=it.next();
			HashSet<String> set=epsilons.get(from);
			if(set!=null&&set.size()>0)
			{
				Iterator<String> itt=set.iterator();
				while(itt.hasNext())
				{
					ret.add(new String[]{from,itt.next()});
				}				
			}
		}
		
		return ret.toArray(new String[0][]);
	}
	/**
	 *	@return true if the epsilon was really added and not
	 *		a duplicate
	 */
	protected boolean addEpsilon(String from,String to)
	{
		assert(from!=null);assert(to!=null);
		HashSet<String> tos=epsilons.get(from);
		if(tos==null)
		{
			tos=new HashSet<String>();
			epsilons.put(from,tos);
		}
		boolean ret=!tos.contains(to);
		tos.add(to);
		return ret;
	}
	/**
	 *	construct states and transitions. leftstate is the state on the
	 *	left to link to. if rightCandidate is not null, it should be 
	 *	linked to instead of a new generated state
	 *	anyhow should return exactly one endstate for this expression
	 */
	protected String construct(RegExp r,String leftState,String rightCandidate)
	{
		assert(r!=null);
		assert(leftState!=null);
		switch(r.type())
		{
			case List:{
				ListRegExp lr=(ListRegExp)r;
				RegExp right=lr.right();
				RegExp left=lr.left();
				leftState=construct(left,leftState,null);
				return construct(right,leftState,rightCandidate);}
			case Char:
				CharRegExp cr=(CharRegExp)r;
				char c=cr.getChar();
				if(rightCandidate==null)
					rightCandidate=allocateState();
				addTransition(leftState,c,rightCandidate);
				return rightCandidate;
			case Star:
				StarRegExp sr=(StarRegExp)r;
				return construct(sr.mul(),leftState,leftState);
			case Empty:
				/*	epsilon should not be required if we dont have to 
					link to a right candidate , we can just return the 
					left state*/
				if(rightCandidate==null)
					return leftState; //no epsilon this time
					//this is a hack to make stuff quicker. should work
				addEpsilon(leftState,rightCandidate);
				return rightCandidate;
			case Or:{
				OrRegExp or=(OrRegExp)r;
				RegExp left=or.left();
				RegExp right=or.right();
				if(rightCandidate==null)
					rightCandidate=allocateState();
				String le=construct(left,leftState,rightCandidate);
				String re=construct(right,leftState,rightCandidate);
				assert(le.equals(re)&&le.equals(rightCandidate));
				return rightCandidate;
				}
		}
		assert(false);
		return null;
	}
	int seed=0;
	protected String allocateState()
	{
		seed++;
		return "q"+seed;
	}
	//----------------manipulation
	/**
	 * 	first state is always "start" 
	 *	add a new transition and the destination state
	 *	if required, source state must exist
	 */
	public void addTransition(String from,char c,String to)
	{
		assert(states.get(from)!=null);
		State f=states.get(from);
		State t=states.get(to);
		if(t==null)
		{
			t=new State(to);
			states.put(to,t);
		}
		Transition trans=new Transition(from,c,to);
		HashMap<Character,ArrayList<Transition>> transs=transitions.get(from);
		if(transs==null)
		{
			transs=new HashMap<Character,ArrayList<Transition>>();
			transitions.put(from,transs);
		}
		Character ch=new Character(c);
		ArrayList<Transition> trs=transs.get(ch);
		if(trs==null)
		{
			trs=new ArrayList<Transition>();
			transs.put(ch,trs);
		}
		if(!trs.contains(trans))
		{
			trs.add(trans);
		}
	}
	//----------------
	/**
	 *	@return true if this automaton is deterministic
	 */	
	public boolean isDeterministic()
	{
		Iterator<HashMap<Character,ArrayList<Transition>>> it=transitions.values().iterator();
		while(it.hasNext())
		{
			Iterator<ArrayList<Transition>> itc=it.next().values().iterator();
			while(itc.hasNext())
			{
				if(itc.next().size()>1)
					return false; //two transitions from same 
							//state with same char
			}
		}
		return true;
	}
	/**
	 *	@return the transition state for this state and char
	 *		or states if a NFA
	 *		null if no transitions
	 */
	public String[] getTransitions(String state,char c)
	{
		assert(states.get(state)!=null);
		HashMap<Character,ArrayList<Transition>> st=
			transitions.get(state);
		if(st==null||st.size()==0)
			return null;
		ArrayList<Transition> sts=st.get(new Character(c));
		if(sts==null||sts.size()==0)
			return null;
		HashSet<String> ret=new HashSet<String>();
		
		Iterator<Transition> it=sts.iterator();
		while(it.hasNext())
		{
			String stat=it.next().to();
			ret.add(stat);
		}
		assert(ret.size()>0);
		return ret.toArray(new String[0]);
	}
	/**
	 *	get the input characters for this state
	 *	or an empty array if none
	 */
	public char[] getCharacters(String state)
	{
		assert(states.get(state)!=null);
		HashSet<Character> ar=
			new HashSet<Character>();

		HashMap<Character,ArrayList<Transition>> st=
			transitions.get(state);
		if(st!=null)
		{
			ar.addAll(st.keySet());
		}
		
		Character[] car=ar.toArray(new Character[ar.size()]);
		char[] ret=new char[car.length];
		for(int i=0;i<car.length;i++)
			ret[i]=car[i];
		return ret;
	}
	/**
	 *	@return one transition from getTransitions 
	 *		(exactly one if deterministic) or
	 *		a null if no valid transition
	 */
	public String getTransition(String state,char c)
	{
		assert(states.get(state)!=null);
		HashMap<Character,ArrayList<Transition>> st=
			transitions.get(state);
		if(st==null)
			return null;
		ArrayList<Transition> ar=st.get(new Character(c));
		if(ar!=null&&ar.size()>0)
		{
			return ar.get(0).to();
		}
		return null;
	}
	/**
	 *	@return all transitions for this state
	 */
	public Transition[] getTransitions(String state)
	{
		assert(states.get(state)!=null);
		HashMap<Character,ArrayList<Transition>> st=
			transitions.get(state);
		if(st==null)
			return new Transition[0];
		ArrayList<Transition> ret=new ArrayList<Transition>();
		
		Iterator<ArrayList<Transition>> it=st.values().iterator();
		while(it.hasNext())
		{
			Iterator<Transition> itt=it.next().iterator();
			while(itt.hasNext())
			{
				Transition t=itt.next();
				if(!ret.contains(t))
					ret.add(t);
			}
		}
		
		return ret.toArray(new Transition[0]);
	}
	/**
	 *	@return all transitions which end at given state
	 */	
	public Transition[] getTransitionsTo(String state)
	{
		assert(states.get(state)!=null);
		ArrayList<Transition> ret=
			new ArrayList<Transition>();
		
		Iterator<HashMap<Character,ArrayList<Transition>>> it=
			transitions.values().iterator();
		while(it.hasNext())
		{
			HashMap<Character,ArrayList<Transition>> trs=it.next();
			if(trs!=null)
			{
				Iterator<ArrayList<Transition>> ita=trs.values().iterator();
				while(ita.hasNext())
				{
					ArrayList<Transition> ar=ita.next();
					if(ita!=null)
					{
						Iterator<Transition> itc=ar.iterator();
						while(itc.hasNext())
						{
							Transition t=itc.next();
							if(t.to().equals(state))
								ret.add(t);
						}
					}
				}
			}
		}
		
		return ret.toArray(new Transition[ret.size()]);
	}
	public String getStartState()
	{
		return "start";  //always
	}
	public boolean isEndState(String state)
	{
		assert(states.get(state)!=null);
		return states.get(state).isEndState();
	}
	public void setEndState(String state,boolean end)
	{
		assert(states.get(state)!=null);
		states.get(state).setEnd(end);
	}
	public String[] getStates()
	{
		return states.keySet().toArray(new String[0]);
	}
	
	/**
	 *	@return a deterministic version of this automaton
	 */
	public FA determinize()
	{
		/*
			-make a new FA
			-make a mapping of 
				-new states to old states
					-there might be multiple old states
					 per new state
			-get its start staet
				-map it as the 
			-for each new state
				-get all input characters for its component states
				-for each character , get all transitions for 
				 every component state
				-each of these becomes a new transition
				-the destination of this transition is the 
				 combination of all destinations of the 
				 transitions on that letter
				 	->one arrow per letter
				-this might create a new state
				-if it does, add to list of new states
		*/
		FA fa=new FA();
		HashMap<String,String[]> map=
			new HashMap<String,String[]>();
		ArrayList<String> newStates=new ArrayList<String>();
		map.put("start",new String[]{"start"});
		newStates.add("start");
		while(newStates.size()>0)
		{
			String state=newStates.get(0);
			newStates.remove(state);
			String[] components=map.get(state);
			HashSet<Character> inputs=new HashSet<Character>();
			for(String comp:components)
			{
				char[] input=getCharacters(comp);
				for(char c:input)
				{
					inputs.add(c);
				}
			}
			for(Character c:inputs)
			{
				boolean endState=false;
				HashSet<String> destinations=
					new HashSet<String>();
				for(String comp:components)
				{
					String[] ts=getTransitions(comp,c);
					if(ts!=null)
					for(String t:ts)
					{
						if(isEndState(t))
							endState=true;
						destinations.add(t);
					}
				}
				String newState=combineStates(
					destinations.toArray(new String[0])); //TBD
				//generate statename by concatenating components
					//in order
				//if state really is new (check before adding trans)
					//add to list of new states
				if(fa.states.get(newState)==null)
				{
					newStates.add(newState);
					//create mapping newstate->components
					map.put(
						newState,
						destinations.toArray(new String[0]));
					
				}
				//create new transition to state
				fa.addTransition(state,c,newState);
				if(endState)
					fa.setEndState(newState,endState);
			}
		}
		return fa;
	}
	/**
	 *	combine an array of statenames in a predictable way to 
	 *	a new statename..this also assumes that the statenames
	 *	cannot be combined to form already existing statenames
	 */
	protected String combineStates(String[] components)
	{
		String ret="";
		String selected=null;
		do{
			selected=null;
			for(int i=0;i<components.length;i++)
			{
				String c=components[i];
				if(selected==null||
				  (c!=null&&c.hashCode()>selected.hashCode()))
				{
					components[i]=selected;
					selected=c;
				}
			}
			if(selected!=null)
				ret+=selected;
		}while(selected!=null);
		return ret;
	}
	/**
	 *	@return a clone of the automaton
	 */
	public FA copy()
	{
		FA ret=new FA();
		Iterator<State> it=states.values().iterator();
		while(it.hasNext())
		{
			State s=it.next();
			ret.states.put(s.name(),s.copy());
		}
		String[] states=ret.getStates();
		for(String state:states)
		{
			Transition[] trans=ret.getTransitions(state);
			for(Transition t:trans)
				ret.addTransition(t.from(),t.getChar(),t.to());
		}		
		return ret;
	}
	/**
	 *	@return a minimized copy of the automaton.
	 */
	public FA minimize()
	{	
		assert(isDeterministic());
		/*
			-jaa tilat tiloihin ja lopputiloihin
			-jokaiselle tilalle , jokaiselle siirtym‰lle  
			 laske luokka johon siirtym‰ osoittaa
			-jos yhdess‰ luokassa on eri luokkiin osoittavia 
			 siirtymi‰, jaa ne uuteen luokkaan
			-toista kunnes ei synny uusia luokkia
		*/
		/*
			-tarvitaan indeksi josta tarkistaa mihin luokkaan 
			tila kuuluu
			-nimet‰‰n luokat L1, L2,L3 jne.
			-jaetaan tilat luokkiin
				-jokainen luokka on HashSet?
			-jokaiselle luokalle vuorollaan:
				-jokaiselle tilalle luokassa
					-jokaiselle syˆteaakkoselle j‰rjestyksess‰:
						-lis‰t‰‰n stringiin syˆteaakkonen ja 
						 sen luokan nimi johon p‰‰tetila kuuluu
					-lis‰t‰‰n tila stringi‰ vastaavaan lokeroon
					 hashmapissa
				-jos lokeroita on enemm‰n kun yks , jaa luokka kahtia ja
				 aloita alusta
					 
			-kun luokkia ei en‰‰ synny uusia, luo niist‰ FA 
		*/
		
		/*	hashmap for classes. composed of set with
			statenames*/
		HashMap<String,HashSet<String>> classes=
			new HashMap<String,HashSet<String>>();
			
		/*	divide states into end and non-end states*/
		classes.put("noend",new HashSet<String>());
		classes.put("end",new HashSet<String>());
		String[] states=getStates();
		for(int i=0;i<states.length;i++)
		{
			HashSet<String> ar;
			if(isEndState(states[i]))
				ar=classes.get("noend");
			else
				ar=classes.get("end");
			ar.add(states[i]);
		}		
		int classSeed=0;
		boolean change=true;
		while(change)
		{
			change=false;
			String[] clzs=classes.keySet().toArray(new String[0]);
			for(int i=0;i<clzs.length;i++)
			{
				/*	mapping from set of classes to 
					set of states which have those classes
					as destinations*/
				HashMap<HashSet<String>,HashSet<String>> sets=
					new HashMap<HashSet<String>,HashSet<String>>();
				//generate a string:
				//for each state in class
					//for each char at a time 
						//find dest class and add to set
							//char+class
					//add state to hashmap with the set as key
				states=classes.get(clzs[i]).toArray(new String[0]);
				for(int j=0;j<states.length;j++)
				{
					HashSet<String> dests=new HashSet<String>();
					Transition[] trs=getTransitions(states[j]);
					for(Transition t:trs)
					{
						char c=t.getChar();
						String toClass=null;
						String to=t.to();
						Iterator<String> it=classes.keySet().iterator();
						while(it.hasNext())
						{
							String cl=it.next();
							if(classes.get(cl).contains(to))
								toClass=cl;
						}
						toClass="char."+c+" to "+toClass;
						dests.add(toClass);
					}
					HashSet<String> stats=sets.get(dests);
					if(stats==null)
					{
						stats=new HashSet<String>();
						sets.put(dests,stats);
					}
					stats.add(states[j]);
				}
				//if there are more than 2 sets of states
					//remove class and add one class per set
					//set change=true			
				if(sets.size()>1)
				{
					classes.remove(clzs[i]);
					Iterator<HashSet<String>> it=sets.keySet().iterator();
					while(it.hasNext())
					{
						classes.put("L"+classSeed,sets.get(it.next()));
						classSeed++;
					}
					change=true;
				}
			}
		}
		/*	generate FA*/
		FA ret=new FA();
		
		/*	find starting class*/
		String startClass=null;
		Iterator<String> it=classes.keySet().iterator();
		while(startClass==null&&it.hasNext())
		{
			String clz=it.next();
			if(classes.get(clz).contains(getStartState()))
				startClass=clz;
		}
		/*	rename it to "start"*/
		HashSet<String> temp=classes.get(startClass);
		classes.remove(startClass);
		startClass="start";
		classes.put(startClass,temp);
		
		/*	generate transitions and states*/
		String[] clzs=classes.keySet().toArray(new String[0]);
		for(String cl:clzs)
		{
			//add state if missing
			if(ret.states.get(cl)==null)
				ret.states.put(cl,new State(cl));
			String[] stats=classes.get(cl).toArray(new String[0]);
			Transition[] trs=getTransitions(stats[0]);
			for(Transition t:trs)
			{
				char c=t.getChar();
				String to=t.to();
				String toClass=null;
				for(String cc:clzs)
				{
					if(classes.get(cc).contains(to))
						toClass=cc;
				}
				ret.addTransition(cl,c,toClass);
			}
			if(isEndState(stats[0]))
				ret.setEndState(cl,true);
		}	
		return ret;
		//return copy();
	}
	public String toString()
	{
		String ret=""+(isDeterministic()?"D":"N")+"FA:\n";
		String[] states=getStates();
		for(int i=0;i<states.length;i++)
		{
			ret+="\t"+states[i]+":\n";
			if(isEndState(states[i]))
				ret+="\tEndState\n";
			Transition[] trans=getTransitions(states[i]);
			for(int j=0;j<trans.length;j++)
			{
				ret+=	
					"\tTransition\n"+
					"\t\tinput:"+trans[j].getChar()+"\n"+
					"\t\tto:"+trans[j].to()+"\n";
			}
			
		}
		return ret;
	}
	//------------------inner classes
	static class State
	{
		protected String name;
		protected boolean end=false;
		public State(String name)
		{
			this.name=name;assert(name!=null);
		}
		public void setEnd(boolean tr)
		{
			end=tr;
		}
		public String name()
		{
			return name;
		}		
		public State copy()
		{
			State ret=new State(name);
			ret.end=end;
			return ret;
		}
		public boolean isEndState()
		{
			return end;
		}
	}
	static class Transition
	{
		String from;
		char c;
		String to;
		public Transition(String from,char c,String to)
		{
			this.from=from;assert(from!=null);
			this.to=to;assert(to!=null);
			this.c=c;
		}
		public char getChar()
		{
			return c;
		}
		public String from()
		{
			return from;
		}
		public String to()
		{
			return to;
		}
		public boolean equals(Object o)
		{
			if(!(o instanceof Transition))
				return false;
			Transition t=(Transition)o;
			return from.equals(t.from)&&c==t.c&&to.equals(t.to);
		}
	}
	public static void main(String[] args)
	throws ScannerException,ParserException
	{
		FA fa=new FA();
			fa.addTransition(fa.getStartState(),'a',"end");
			fa.setEndState("end",true);
		assert(fa.isDeterministic());
		assert(fa.getStates().length==2);
		
		fa=new FA("aa*");
		System.out.println(fa.toString());
		System.out.println(new SpaghettiGenerator().generate(fa,"doh"));
		
		fa=new FA("aaa");
		assert(fa.isDeterministic());
		//TBD generate code/print it
		fa=new FA("a(a|b)c"); //DFA
		assert(fa.isDeterministic());
		//TBD generate code/print it
		fa=new FA("a(a|b)*a"); //NFA 		
		assert(!fa.isDeterministic());
		System.out.println(fa.toString());
		fa=fa.determinize();
		System.out.println(fa.toString());
		fa=fa.minimize();
		System.out.println(fa.toString());
		
		
		
	}
}
