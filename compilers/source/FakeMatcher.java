/**
 *	fake implementation of a compiled matcher for 
 *	a regexp 
 *	a(a|b)*\\
 */
public class FakeMatcher
implements Matcher
{
	public boolean match(String s)
	{
		return match(s,0,s.length());
	}
	public boolean match(String s,int off,int l)
	{
		assert(s!=null);
		assert((off+l)<=s.length());
		assert(off>=0);
		assert(l>0);
		int pos=off; //local variable 0
		int end=off+l; //local variable 1
		boolean gmatch=true;
		
		/*	main loop frame to enable continues from generated
			code*/
		boolean match_0=false;
		for(int i_0=0;i_0<1;i_0++)
		{
			/*	generated code here*/
			//a
			if(pos==end)
				continue;
			if(s.charAt(pos)!='a')
				continue;
			pos++;
			System.out.println("matched a");
			//*
			int matches_1=0;
			boolean match_1=true;
			int temp_1=pos;
			//* max munch check
			while(match_1)
			{
				match_1=false;
				//a|b
				int temp_2=pos;
				boolean match_2=false;
				for(int i_2=0;i_2<2;i_2++)
				{
					pos=temp_2;
					match_2=false;
					if(i_2==0)
					{
						//a
						if(pos==end)
							continue;
						if(s.charAt(pos)!='a')
							continue;
						pos++;
						System.out.println("matched a");
					}else{
						//b
						if(pos==end)
							continue;
						if(s.charAt(pos)!='b')
							continue;
						pos++;
						System.out.println("matched b");
					}
					match_2=true;
				}
				if(!match_2)
					continue;				
				matches_1++;
				System.out.println("matching"+matches_1);
				match_1=true;
			}
			//* real check
			for(int i_1=0;i_1<(matches_1+1);i_1++)
			{
				pos=temp_1;
				match_1=true;
				for(int j_1=0;j_1<(matches_1-i_1);j_1++)
				{
					match_1=false;
					//a|b
					int temp_3=pos;
					boolean match_3=false;
					for(int i_3=0;i_3<2;i_3++)
					{
						pos=temp_3;
						match_3=false;
						if(i_3==0)
						{
							//a
							if(pos==end)
								continue;
							if(s.charAt(pos)!='a')
								continue;
							pos++;
						}else{
							//b
							if(pos==end)
								continue;
							if(s.charAt(pos)!='b')
								continue;
							pos++;
						}
						match_3=true;
					}
					if(!match_3)
						continue;
					
					match_1=true;
				}
				if(!match_1)
					continue;
				match_1=false;
				// \\
				if(pos==end)
					continue;
				if(s.charAt(pos)!='\\')
					continue;
				pos++;
			}
			if(!match_1)
				continue;
			/*	generated code end*/
			match_0=true; //to mark success 
		}
		gmatch&=match_0; 
		System.out.println("gm="+gmatch+" "+pos+"=="+end);
		return gmatch&&(pos==end);
	}
	/*
		if(pos==len)
			continue;
		if(s.charAt(pos)!='a')
			continue;
		pos++;
	*/
}
