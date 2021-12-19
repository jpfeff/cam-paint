import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;
/**
 * PS-1
 * Webcam-based drawing
 *
 * Scaffold:
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 *
 * @author Josh Pfefferkorn
 * Dartmouth CS10, Fall 2020
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE

		// check display mode; refresh screen accordingly, calling Webcam's draw() method
		if (displayMode == 'w') {
			super.draw(g);
		}
		else if (displayMode == 'r') {
			image = finder.getRecoloredImage();
			super.draw(g);
		}
		else if (displayMode == 'p') {
			image = painting;
			super.draw(g);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		// TODO: YOUR CODE HERE
		// ensure that image and targetColor are both not null
		if (image != null && targetColor != null) {
			// feed the RegionFinder the image
			finder.setImage(image);
			// call the findRegions method, passing in the color from handleMousePress()
			finder.findRegions(targetColor);
			// recolor the image
			finder.recolorImage();
		}

		if (finder.largestRegion() != null ) {
			// loop through the largest region (the brush)
			for (int k = 0; k < finder.largestRegion().size(); k++) {
				// color in that region on the painting with the chosen paintColor
				painting.setRGB((int)finder.largestRegion().get(k).getX(),(int)finder.largestRegion().get(k).getY(), paintColor.getRGB());
			}
		}

	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		// ensures image is not null
		if (image != null) {
			// sets target color to color of image at location of mouse press
			targetColor = new Color(image.getRGB(x, y));
		}
		// display mode set to recolor
		displayMode = 'r';
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
