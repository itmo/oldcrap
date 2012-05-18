/**
 *	class to benchmark the execution and compilation of several
 *	regular expressions with both interpreted(different interpreted) 
 *	and compiled modes and the sun regex thingy
 */
public class Benchmark
{
	protected static MatcherFactory fact=new MatcherFactory();

	protected static final int amount=10000;
	public static void main(String[] args)
	throws ParserException,ScannerException
	{
		preheat();
		benchmark("a(b|c)*c","abbbbccbbbccbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbcbccbcbcbcbcbccbbccc");
		benchmark("a(a(b|c)*)*ab",
			"aabababababababababababababababababababababacacacacacacacacacacacacacacacacacacacacacacac",
			"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
			"aacacacacacacacacacacababababababababaaaaaaaaaaaaaaaaaaaaaaaaaaacacacacacab");
		benchmark("((a|b)*(a|b))*(a|b)abababababababababab",
			"abababababababababbabababaababababaabababababababababababababababaabababababababababababab");
		//do benchmarks
	}
	public static void preheat()
	throws ParserException,ScannerException
	{
		System.out.println("preheating compilers");
		for(int i=0;i<amount;i++)
		{
			java.util.regex.Pattern.compile("a(b|c)*");
			fact.generateMatcher("a(b|c)*",false);
			fact.generateMatcher("a(b|c)*",true);
		}
		System.out.println("done");
	}
	public static void benchmark(String regex,String...test)
	throws ParserException,ScannerException
	{
		//benchmark compilation of regexp on all systems
		Matcher m1=null;
		Matcher m2=null;
		java.util.regex.Matcher m=null;
		java.util.regex.Pattern p=null;
		long start;
		start=System.currentTimeMillis();
		for(int i=0;i<amount;i++)
		{
			p=java.util.regex.Pattern.compile(regex);
			m=p.matcher(test[0]);
		}
		System.out.println("Compiling sun regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");
		start=System.currentTimeMillis();
		for(int i=0;i<amount;i++)
		{
			m1=fact.generateMatcher(regex,false);
		}
		System.out.println("Compiling uncompiled regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");
		start=System.currentTimeMillis();
		for(int i=0;i<amount;i++)
		{
			m2=fact.generateMatcher(regex,true);
		}
		System.out.println("Compiling compiled regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");
		//pre-heat all compiled versions
		System.out.println("preheating");
		for(int i=0;i<amount;i++)
		{
			p.matcher("aaa");
			m1.match("aaa");
			m2.match("aaa");
		}
		System.out.println("benchmarking");
		//benchmark all compiled versions for all test strings
		for(int j=0;j<test.length;j++)
		{
			m=p.matcher(test[j]);
			start=System.currentTimeMillis();
			for(int i=0;i<amount;i++)
			{
				m1.match(test[j]);
			}
			System.out.println("Matching uncompiled regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");
			start=System.currentTimeMillis();
			for(int i=0;i<amount;i++)
			{
				m2.match(test[j]);
			}
			System.out.println("Matching compiled regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");			
			start=System.currentTimeMillis();
			for(int i=0;i<amount;i++)
			{
				m.matches();
			}
			System.out.println("Matching sun regex "+regex+" took "+(System.currentTimeMillis()-start)+"ms");
		}
	}
}
/*
		Matcher m1=null;
		Matcher m2=null;
		java.util.regex.Pattern p;
		p = java.util.regex.Pattern.compile(regex);
		m1=fact.generateMatcher(regex,true);
		m2=fact.generateMatcher(regex,false);

*/
