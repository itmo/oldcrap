/**
 *	test class for testing regular expressions
 */
public class Main
{
	public static void main(String[] args)
	throws ParserException,ScannerException
	{
		MatcherFactory fact=new MatcherFactory();
		long start=0;
		int amount=10000;
		
		java.util.regex.Pattern p;
		p = java.util.regex.Pattern.compile("aa*");
		start=System.currentTimeMillis();
 		for(int i=0;i<amount;i++)
		{
			java.util.regex.Matcher m = p.matcher("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
			boolean b = m.matches();	
		}
		System.out.println("regex took:"+amount+"/"+(System.currentTimeMillis()-start)+"ms");
	
		p = java.util.regex.Pattern.compile("a*(b|c|d|e|f|g|h|i)*a*");
		start=System.currentTimeMillis();
 		for(int i=0;i<amount;i++)
		{
			java.util.regex.Matcher m = p.matcher("aaaaaaaaaaaaaaaaaaaaaaaaaaaidefaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			boolean b = m.matches();	
		}
		System.out.println("regex2 took:"+amount+"/"+(System.currentTimeMillis()-start)+"ms");
		
		boolean compile=false;
		for(int i=0;i<2;i++)
		{
			String comp="";
			if(compile)
				comp=" Compiled ";
			Matcher m=null;
			System.out.println("compiling..");
			start=System.currentTimeMillis();
			for(int j=0;j<amount;j++)
			{
				m=fact.generateMatcher("a(b|c)*b",compile);
			}
			System.out.println(comp+"compilation:"+amount+" took:"+(System.currentTimeMillis()-start)+"ms");
			
			
			m=fact.generateMatcher("aa*",compile);
			

			start=System.currentTimeMillis();
			for(int j=0;j<amount;j++)
			{
				m.match("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab");
			}
			System.out.println(comp+"1:"+amount+" took:"+(System.currentTimeMillis()-start)+"ms");
			assert(m.match("aaaaaaa"));
			assert(!m.match("aaaaab"));

			m=fact.generateMatcher("a*(b|c|d|e|f|g|h|i)*a*",compile);


			start=System.currentTimeMillis();
			for(int j=0;j<amount;j++)
			{
				m.match("aaaaaaaaaaaaaaaaaaaaaaaaaaaidefaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			}
			System.out.println(comp+"2:"+amount+" took:"+(System.currentTimeMillis()-start)+"ms");
			
			
			m=fact.generateMatcher("ab?",compile);
			System.out.println("generated for ab?");
			assert(m.match("a"));
			assert(m.match("ab"));
			assert(!m.match("abb"));
			
			m=fact.generateMatcher("ab+",compile);
			System.out.println("generated for ab+");
			assert(!m.match("a"));
			assert(m.match("ab"));
			assert(m.match("abb"));

			m=fact.generateMatcher("a(a|b)*\\\\",compile);
			assert(m.match("aabbaabbaa\\"));
			assert(!m.match("abbababba"));

			m=fact.generateMatcher("aabb",compile);
			assert(m.match("aabb"));
			assert(!m.match("aabbc"));
			
			m=fact.generateMatcher("a*b",compile);
			assert(m.match("aaaaab"));
			assert(!m.match("bbb"));
			
			m=fact.generateMatcher("a(ba)*c",compile);
			assert(!m.match("aababc"));
			assert(m.match("ababac"));
			
			m=fact.generateMatcher("a(a|b)c",compile);
			assert(m.match("aac"));
			assert(m.match("abc"));
			assert(!m.match("ac"));
			assert(!m.match("ab"));
			//TBD benchmark generation
			//TBD benchmark compiled/uncompiled matching
			start=System.currentTimeMillis();
			for(int j=0;j<amount;j++)
			{
				m.match("aabbaabbaa\\");
			}
			System.out.println(comp+""+amount+" took:"+(System.currentTimeMillis()-start)+"ms");
			
			//non-determinism..
			m=fact.generateMatcher("a(b|c)*b",compile);
			
			assert(m.match("abcbcbcbcbcbb"));
			assert(!m.match("abcbcbcbcbcbcbc"));
			compile=!compile;
			System.out.println("toggling compiling");
		}
		
	}
}
