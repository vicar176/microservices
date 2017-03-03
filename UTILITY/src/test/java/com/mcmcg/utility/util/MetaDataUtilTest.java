package com.mcmcg.utility.util;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetaDataUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testConvertToDate() throws Exception {
		// Given
		String Given = "Jun. 17, 2008";
		// when
		Date expected = MetaDataUtil.convertToDate(Given);
		// Then
		assertNotNull(expected);
	}

	@Test
	public final void testConvertToDatePhrase() throws Exception {
		// Given
		String Given = "the day before next thursday";
		// when
		Date expected = MetaDataUtil.convertToDate(Given);
		// Then
		assertNotNull(expected);
	}

	@Test
	public final void testConvertToDateDouble() throws Exception {
		// Given
		String Given = "03/30/1987 12-05-2015";
		// when
		Date expected = MetaDataUtil.convertToDate(Given);
		// Then
		assertNotNull(expected);
	}

	@Test
	public final void testConvertToDateNull() throws Exception {
		// when
		Date expected = MetaDataUtil.convertToDate(null);
		// Then
		assertNull(expected);
	}

	@Test
	public final void testConvertToDateRange() throws Exception {
		// Given
		String Given = "April 1st to Christmas";
		// when
		Date expected = MetaDataUtil.convertToDate(Given);
		// Then
		assertNotNull(expected);
	}
	
	@Test
	public final void testConvertToDateFormat() throws Exception {
		// Given
		String Given = "30 03 1987";
		// when
		Date expected = MetaDataUtil.convertToDate(Given, "dd MM yyyy");
		// Then
		assertNotNull(expected);
	}

}
