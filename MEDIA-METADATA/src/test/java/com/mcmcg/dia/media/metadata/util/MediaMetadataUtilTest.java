package com.mcmcg.dia.media.metadata.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class MediaMetadataUtilTest {

	@Test
	public void test() {
		assertTrue(MediaMetadataUtil.isNumeric("51"));
	}
	
	@Test
	public void LastDot() {
		assertFalse(MediaMetadataUtil.isNumeric("70.71."));
	}

}
