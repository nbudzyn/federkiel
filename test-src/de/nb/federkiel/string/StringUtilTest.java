/**
 *
 */
package de.nb.federkiel.string;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author nbudzyn 2009
 */
public class StringUtilTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSplitAndTrimSingle() throws Exception {
		assertTrue(Arrays.deepEquals(new String[] {"Peter", "Paul"},
				new String[] {"Peter", "Paul"}));

		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter<br>Paul", "<br>"),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("<br>Peter<br>Paul", "<br>"),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("<br>Peter<br><br>Paul<br>   ", "<br>"),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("   Peter <br>   Paul   <br>   ", "<br>"),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter<br>Paul<br>Mary", "<br>"),
				new String[] {"Peter", "Paul", "Mary"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter<br> Paul<br><br><br>", "<br>"),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter<br><br><br>   <br>Paul<br>  Mary<br>", "<br>"),
				new String[] {"Peter", "Paul", "Mary"}));
	}

	public void testSplitAndTrimArray() throws Exception {
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter</br>Paul",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("<br>Peter</br>Paul",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("<br>Peter</br></br>Paul<br>   ",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("   Peter <br>   Paul   </br>   ",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter</br>Paul<br>Mary",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul", "Mary"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter</br> Paul</br><br></br>",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul"}));
		assertTrue(Arrays.deepEquals(StringUtil.splitAndTrim("Peter<br><br></br>   </br>Paul</br>  Mary<br>",
				new String[] { "<br>", "</br>" }),
				new String[] {"Peter", "Paul", "Mary"}));
	}

}
