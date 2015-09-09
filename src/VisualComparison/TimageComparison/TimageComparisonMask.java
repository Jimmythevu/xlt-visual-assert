package VisualComparison.TimageComparison;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import VisualComparison.ImageComparison;

/**
 * Tests if the mask is working as expected. Specifically, it tests if 
 * a difference in the images is marked and detected if the mask image is black 
 * and if a difference in the images is detected if the mask image is white.
 * Includes separate tests for the following ImageComparison parameters:
 * 
 * PixelPerBlockX = 10, PixelPerBlockY = 10, threshold = 0.1
 * PixelPerBlockX = 1, PixelPerBlockY = 1, threshold = 0.1
 * PixelPerBlockX = 1, PixelPerBlockY = 1, threshold = 0.0
 * 
 * @author damian
 *
 */
public class TimageComparisonMask {

//The reference image is fully black while the screenshot image is black up to row 200, then white between
//	rows 200 and 300. The maskimage is black from row 250 to row 300.
	
//	The tests test if the resulting markedImage is marked between rows 250 and 300 (it shoudn't be)
//	and if the resulting markedImage is marked between rows 200 and 250 (it should be)
	
		private static BufferedImage reference;
		private static BufferedImage screenshot;
		
		private final static File directory = SystemUtils.getJavaIoTmpDir();
		private static File fileMask = new File(directory, "/fileMask.png");
		private static File fileOut = new File(directory, "/fileOut.png");
		
		private final static int rgbBlack = Color.BLACK.getRGB();
		private final static int rgbWhite = Color.WHITE.getRGB();
		private final int rgbMarked = Color.RED.getRGB();
				
		@BeforeClass
		public static void initializeImages() throws IOException {
//			Initializes the reference, screenshot and the maskImage;
			reference = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

			for (int w=0; w<reference.getWidth(); w++) { 
				for (int h=0; h<reference.getHeight(); h++) {
					reference.setRGB(w, h, rgbBlack);
				}
			}
			
			screenshot = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
			for (int w=0; w<screenshot.getWidth(); w++) { 
				for (int h=0; h<screenshot.getHeight(); h++) {
					if (h >= 200) { 
						screenshot.setRGB(w, h, rgbWhite);
					}
					else {
						screenshot.setRGB(w, h, rgbBlack);
					}
				}	
			}
			
			BufferedImage maskImage = new BufferedImage(300, 300,BufferedImage.TYPE_INT_BGR);
			for (int w=0; w<screenshot.getWidth(); w++) { 
				for (int h=0; h<screenshot.getHeight(); h++) {
					if (h >= 250) { 
						maskImage.setRGB(w, h, rgbBlack);
					}
					else {
						maskImage.setRGB(w, h, rgbWhite);
					}
				}	
			}
			ImageIO.write(maskImage, "PNG", fileMask);
		}	
		
		/**
		 * Tests if the parts where the mask is black and there were differences was NOT marked
		 * ImageComparison parameters: 10, 10, 0.1, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyHidden() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(10, 0.1, false, "FUZZYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=250; h<reference.getHeight(); h++) {
					Assert.assertEquals(rgbWhite, output.getRGB(w, h));
				}
			}
		}
		
		/**
		 * Tests if the parts where the mask is white and there were differences was marked
		 * ImageComparison parameters: 10, 10, 0.1, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyMarked() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(10, 0.1, false, "FUZZYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			boolean hasRed = false;
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=200; h<250; h++) {
					if (rgbMarked == output.getRGB(w, h)) {
						hasRed = true;
					}
				}
			}
			Assert.assertTrue("Unmasked part with changes should be marked", hasRed);
		}
		
		/**
		 * Tests if the parts where the mask is black and there were differences was NOT marked
		 * ImageComparison parameters: 1, 1, 0.1, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyHiddenPixelFuzzyEqual() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(1, 0.1, false, "PIXELFUZZYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=250; h<reference.getHeight(); h++) {
					Assert.assertEquals(rgbWhite, output.getRGB(w, h));
				}
			}
		}
		
		/**
		 * Tests if the parts where the mask is white and there were differences was marked
		 * ImageComparison parameters: 1, 1, 0.1, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyMarkedPixelFuzzyEqual() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(1, 0.1, false, "PIXELFUZZYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=200; h<250; h++) {
					Assert.assertEquals(rgbMarked, output.getRGB(w, h));
				}
			}
		}
		
		/**
		 * Tests if the parts where the mask is black and there were differences was NOT marked
		 * ImageComparison parameters: 1, 1, 0.00, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyHiddenExactlyEqual() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(1, 0.00, false, "EXACTLYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=250; h<reference.getHeight(); h++) {
					Assert.assertEquals(rgbWhite, output.getRGB(w, h));
				}
			}
		}
		
		/**
		 * Tests if the parts where the mask is white and there were differences was marked
		 * ImageComparison parameters: 1, 1, 0.00, false
		 * 
		 * @throws IOException
		 */
		@Test
		public void changesCorrectlyMarkedExactlyEqual() throws IOException {
			ImageComparison imagecomparison = new ImageComparison(1, 0.00, false, "EXACTLYEQUAL");
			imagecomparison.isEqual(reference, screenshot, fileMask, fileOut);
			BufferedImage output = ImageIO.read(fileOut);
			
			for (int w=0; w<reference.getWidth(); w++) {
				for (int h=200; h<250; h++) {
					Assert.assertEquals(rgbMarked, output.getRGB(w, h));
				}
			}
		}
		
		/**
		 * Deletes the temporary files which were created for this test
		 */
//		Deletes the created files after the test.
		@AfterClass
		public static void deleteFile() {
			fileMask.delete();
			fileOut.delete();
		}
}
