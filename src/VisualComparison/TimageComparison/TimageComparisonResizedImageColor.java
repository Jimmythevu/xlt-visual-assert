package VisualComparison.TimageComparison;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import VisualComparison.ImageComparison;


public class TimageComparisonResizedImageColor {
	
//	If one image is smaller then the other, the resizeImage method will adapt their size and fill the formerly nonexistent pixels 
//	with transparent black. This class tests whether or not the methods detect the difference between transparent black and black
	
//  This Test is supplemented by the TimageComparisonInfluenceAlpha Test, this one tests both alpha detection and resizing, but it does
//	not check alpha detection as thoroughly; there are some redundancies
	
	private static BufferedImage reference;
	private static BufferedImage screenshot;
		
	private final static File directory = SystemUtils.getJavaIoTmpDir();
	private static File fileMask = new File(directory, "/fileMask.png");
	private static File fileOut = new File(directory, "/fileOut.png");
	
	private final static int rgbBlack = Color.BLACK.getRGB();
	
	//	Initializes two black images, one with a size of 300*300px, the other 1*1px
	@BeforeClass
	public static void initializeImages() throws IOException {
		reference = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB);
		for (int w=0; w<reference.getWidth(); w++) { 
			for (int h=0; h<reference.getHeight(); h++) {
				reference.setRGB(w, h, rgbBlack);
			}
		}

		screenshot = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		for (int w=0; w<screenshot.getWidth(); w++) { 
			for (int h=0; h<screenshot.getHeight(); h++) {
					screenshot.setRGB(w, h, rgbBlack);
			}	
		}
		
	}

//  	Tests the exactly Equal method 
	@Test
	public void testExactlyEqual() throws IOException {
		ImageComparison imagecomparison = new ImageComparison(1, 1, 0.00, false);
		boolean result = imagecomparison.fuzzyEqual(reference, screenshot, fileMask, fileOut);
		Assert.assertFalse("Failure,  images of different size shoudn't be equal - testExactlyEqual", result);
	}	
	
//		Tests the pixelFuzzyEqual method with a very high threshold.
//		No matter how high the threshold, the pixelFuzzyEqual method will always return false if it detects a transparency 
	@Test
	public void testPixelFuzzyEqual() throws IOException {
		ImageComparison imagecomparison = new ImageComparison(1, 1, 0.9, false);
		boolean result = imagecomparison.fuzzyEqual(reference, screenshot, fileMask, fileOut);
		Assert.assertFalse("Failure,  images of different size shoudn't be equal - testPixelFuzzyEqual", result);
	}	
	
//	Tests the fuzzyEqual method with a threshold barely below one
	@Test
	public void testFuzzyEqual() throws IOException {
		ImageComparison imagecomparison = new ImageComparison(2, 2, 0.99999999, false);
		boolean result = imagecomparison.fuzzyEqual(reference, screenshot, fileMask, fileOut);
		Assert.assertFalse("Failure,  images of different size shoudn't be equal - testFuzzyEqual", result);
	}	
	
//	Tests the fuzzyEqual method with a threshold of one. This should return true.
	@Test
	public void testFuzzyEqualThresholdOfOne() throws IOException {
		ImageComparison imagecomparison = new ImageComparison(2, 2, 1, false);
		boolean result = imagecomparison.fuzzyEqual(reference, screenshot, fileMask, fileOut);
		Assert.assertTrue("Failure, a threshold of one should return true - testFuzzyEqualThresholdOfOne", result);
	}
	
	@AfterClass
	public static void deleteFile() {
		fileMask.delete();
		fileOut.delete();
	}
}


