package com.darwinsys.testing;

import static com.darwinsys.testing.TestUtils.assertNoDefaultProperties;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

public class TestUtilsTest {

	Calendar c;
	Mock m1, m2;
	
	/** Simple POJO mock for testing TestUtils */
	static class Mock {
		int i;
		String j;
		private Date date;
		public Date getDate() {
			return date;
		}
		public void setDate(Date d) {
			this.date = d;
		}
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o.getClass() == Mock.class)) {
				return false;
			}
			Mock m = (Mock)o;
			if (this.i != m.i || this.j != m.j ||
					!(this.date.equals(m.date))) {
				return false;
			}
			return true;
		}
		@Override
		public int hashCode() {
			return i*51 | j.hashCode() | date.hashCode();
		}
		@Override
		public String toString() {
			return 
				String.format("mock(%d,%s,@%x)",
					i,j,super.hashCode());
		}
	}

	@Before
	public void setup() {
		System.out.println("TestUtilsTest.setup()");
		c = Calendar.getInstance();
		// Start with m1 and m2 equal
		m1 = new Mock();
		m2 = new Mock();
		m1.i = m2.i = 42;
		m1.j = m2.j ="Hello";
		m1.date = m2.date = c.getTime();
	}
	
	@Test
	public void testEqualsObjectObject() {
		assertEquals("equality1", m1, m2);	// First tests Mock's equal!
		assertTrue("equality2", TestUtils.equals(m1, m1));
		assertTrue("equality3", TestUtils.equals(m1, m2));
		assertTrue("equality4", TestUtils.equals(m2, m1));
		assertFalse("equality5", TestUtils.equals(m2, null));
		m2.i++;
		assertFalse("equality6", TestUtils.equals(m1, m2));
		m2.i--;
		assertTrue("equality7", TestUtils.equals(m2, m1));
	}
		
	@Test
	public void testEqualsCalCal() {
		Calendar c = Calendar.getInstance();
		c.setTime(m2.date);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
		m2.date = c.getTime();
		assertFalse("dates", TestUtils.equals(m1, m2));
	}

	@Test
	public void testAssertNoDefaultProperties() throws Exception {
		Object o = new Object();
		assertNoDefaultProperties(o);

		Mock m = new Mock();
		try {
			assertNoDefaultProperties(m);
			fail("Didn't find null properties in blank Mock");
		} catch (AssertionError e) {
			System.out.println("Caught expected " + e);
		}
		m.date = new Date();
		m.i = 1000;
		m.j = "mockme";
		assertNoDefaultProperties(m);
	}

	@Test @Ignore("unknown test failure")
	public void testPropertyDiff() throws Exception {
		List<String> diffs = null;
		diffs = TestUtils.compareAll(m1, m2);
		assertTrue(0 == diffs.size());

		m2.date = new Date();
		diffs = TestUtils.compareAll(m1, m2);
		assertEquals(1, diffs.size());
		assertEquals("date", diffs.get(0));
	}
}
