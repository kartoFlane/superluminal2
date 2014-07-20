package com.kartoflane.superluminal2.mvc.controllers.props;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.props.ArcPropView;

/**
 * A prop that serves to visualise a range of values in the form of a (partial) circle.
 * Think pie chart.<br>
 * <br>
 * 
 * Values:<br>
 * {@link #setStartAngle(int)} to specify the starting angle.<br>
 * {@link #setArcSpan(int)} to specify the range (beginning at starting angle)<br>
 * <br>
 * Note: 0 degrees points <b>north</b>, angles increase <b>counter-clockwise</b><br>
 * <br>
 * Appearance:<br>
 * {@link #setPaintRim(boolean)} to specify whether the prop should appear as a circle or a ring<br>
 * {@link #setBackgroundColor(int, int, int)} to specify the color of the prop (paintRim == true)<br>
 * {@link #setBorderColor(int, int, int)} to specify the color of the prop (paintRim == false)<br>
 * {@link #setBorderThickness(int)} to specify the thickness of the ring (paintRim == false)
 * 
 * @author kartoFlane
 *
 */
public class ArcPropController extends PropController {
	private int startAngle = 0;
	private int arcSpan = 0;
	private boolean rim = false;

	/**
	 * Creates a new arc prop controller.
	 * 
	 * @param parent
	 *            the parent of this controller which it will follow
	 * @param id
	 *            the string used to identify and distinguish this prop
	 */
	public ArcPropController(AbstractController parent, String id) {
		super(parent, new BaseModel(), new ArcPropView(), id);
	}

	/**
	 * Sets the starting angle, in degrees.<br>
	 * 0 by default.<br>
	 * <br>
	 * Note: 0 degrees points <b>north</b>, angles increase <b>counter-clockwise</b>
	 */
	public void setStartAngle(int angle) {
		startAngle = angle;
	}

	/**
	 * The starting angle, in degrees.<br>
	 * 0 by default.<br>
	 * <br>
	 * Note: 0 degrees points <b>north</b>, angles increase <b>counter-clockwise</b>
	 * 
	 * @return the starting angle, in degrees
	 */
	public int getStartAngle() {
		return startAngle;
	}

	/**
	 * Sets the span of the arc represented by this prop, in degrees.<br>
	 * 0 by default. <br>
	 * <br>
	 * Note: 0 degrees points <b>north</b>, angles increase <b>counter-clockwise</b>
	 */
	public void setArcSpan(int angle) {
		arcSpan = angle;
	}

	/**
	 * The span of the arc represented by this prop, in degrees.<br>
	 * 0 by default.<br>
	 * <br>
	 * Note: 0 degrees points <b>north</b>, angles increase <b>counter-clockwise</b>
	 * 
	 * @return the arc span
	 */
	public int getArcSpan() {
		return arcSpan;
	}

	/**
	 * Specifies whether the prop should appear as a filled circle, or a ring.<br>
	 * False by default.
	 * 
	 * @param rim
	 *            true if the prop should appear as a ring, false if it should appear as a filled circle.
	 */
	public void setPaintRim(boolean rim) {
		this.rim = rim;
	}

	/**
	 * @return true if the prop appears as a ring, false if it appears as a filled circle.
	 */
	public boolean getPaintRim() {
		return rim;
	}
}
