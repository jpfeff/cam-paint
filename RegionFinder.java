import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * PS-1
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 *
 * Scaffold:
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 *
 * @author Josh Pfefferkorn
 * Dartmouth CS10, Fall 2020
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;					// the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE
		// an extra image for tracking visited pixels
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<ArrayList<Point>>();

		// loop through all pixels
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (visited.getRGB(x, y) == 0) {	// check if visited
					if (colorMatch(new Color(image.getRGB(x, y)), targetColor)) {	// check if correct color
						ArrayList<Point> region = new ArrayList<Point>();	// create new region
						ArrayList<Point> toVisit = new ArrayList<Point>(); // creates a list of pixels that still require visiting
						toVisit.add(new Point(x,y));	// add original point to newly-initialized list
						while (toVisit.size()>0) {		// while there are still pixels which require visiting
							Point cur = toVisit.get(0);	// retrieve a pixel to visit
							toVisit.remove(0);	// remove it once stored
							region.add(cur);			// add it to region
							// loop through 8 surrounding pixels, staying within image bounds
							for (int x2 = Math.max(0, (int) cur.getX() - 1); x2 <= Math.min(image.getWidth()-1, (int) cur.getX() + 1); x2++) {
								for (int y2 = Math.max(0, (int) cur.getY() - 1); y2 <= Math.min(image.getHeight()-1, (int) cur.getY() + 1); y2++) {
									if (visited.getRGB(x2, y2) == 0) { 		// check if already visited
										visited.setRGB(x2, y2, 1);		// if not, mark as visited
										if (colorMatch(new Color(image.getRGB(x2, y2)), targetColor)) {	// check if correct color
											toVisit.add(new Point(x2, y2));	// add to list of pixels to visit
										}
									}
								}
							}
							if (region.size() >= minRegion) {	// check if region is big enough to consider
								regions.add(region);			// if so, add to list of regions
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE

		// compares each color channel, ensuring that differences don't exceed maxColorDiff
		if (Math.abs(c1.getRed()-c2.getRed())<= maxColorDiff && Math.abs(c1.getBlue()-c2.getBlue())<= maxColorDiff && Math.abs(c1.getGreen()-c2.getGreen())<= maxColorDiff) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE

		ArrayList<Point> maxRegion = new ArrayList<Point>();
		if (regions !=null) {
			if (regions.size()>0) {
				maxRegion = regions.get(0);        // sets biggest region to first item
				for (int k = 1; k < regions.size(); k++) {    // loops through regions beginning at index 1
					if (regions.get(k).size() > maxRegion.size()) {
						maxRegion = regions.get(k);        // if current region is bigger than previously biggest region, update maxRegion accordingly
					}
				}
			}
		}
		else return null;
		return maxRegion;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE
		for (int m = 0; m<regions.size();m++) {	// loop through list of regions
			Color newColor = new Color((int)(Math.random()*16777217)); // create new random color
			for (int i = 0; i<regions.get(m).size(); i++) {	// loop through points within current region
				recoloredImage.setRGB((int)(regions.get(m).get(i).getX()), (int)(regions.get(m).get(i).getY()), newColor.getRGB()); // recolor each point in region to random color
			}
		}
	}
}
