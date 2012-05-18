/**
 *	a set of unit tests for the regular expression thingy
 *
 *	the test framework makes the assumption that java regex matcher is 
 *	always right and hence if ours works the same, it is correct too
 */
public class Unit
{
	protected static MatcherFactory fact=new MatcherFactory();
	public static void main(String[] args)
	throws ScannerException,ParserException
	{
		boolean testAssert=false;
		assert(testAssert=true);
		if(!testAssert)
			throw new RuntimeException("please enable asserts");
		//---------------
		test("aa*","aaaaa","a","aaaab");
		test("ab?","a","ab","aaa","abb");
		test("ab+","a","ab","abb");
		test("a(a|b)*\\\\","aabbaabbaa\\","abbababba");
		test("aabb","aabb","aabbc");
		test("a*b","aaaaaab","bbb","abbbbb");
		test("a(ba)*c","aac","abc","ac","ab");
		test("a(b|c)*b","abcbcbcbcbcbb","abcbcbcbcbcbc");
		test("a(a(b|c)*)*ab","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
	}
	protected static void test(String regex,String...test)
	throws ParserException,ScannerException
	{
		Matcher m1=null;
		Matcher m2=null;
		java.util.regex.Pattern p;
		p = java.util.regex.Pattern.compile(regex);
		m1=fact.generateMatcher(regex,true);
		m2=fact.generateMatcher(regex,false);
		for(int i=0;i<test.length;i++)
		{
			java.util.regex.Matcher m = p.matcher(test[i]);
			boolean b=m.matches();
			assert(b==m1.match(test[i])):"failed compiled:"+regex+" was not "+b;
			assert(b==m2.match(test[i])):"failed uncompiled:"+regex+" was not "+b;
		}
	}
}
