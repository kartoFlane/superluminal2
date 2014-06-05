package com.kartoflane.superluminal2.mvc.controllers.props;

import org.eclipse.swt.graphics.Point;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.utils.Utils;

public class OrbitPropController extends PropController {
	private int offset = 0;

	public OrbitPropController(AbstractController parent, String id) {
		super(parent, id);
		setBounded(true);
	}

	public void setOrbitOffset(int o) {
		offset = o;
	}

	public int getOrbitOffset() {
		return offset;
	}

	@Override
	public boolean isWithinBoundingArea(int x, int y) {
		return false;
	}

	@Override
	public Point limitToBoundingArea(int x, int y) {
		Point p = new Point(x, y);
		float angle = (float) Math.toRadians(270 - Utils.angle(p, getParent().getLocation()));
		p.x = getParent().getX() - (int) (Math.cos(angle) * offset);
		p.y = getParent().getY() - (int) (Math.sin(angle) * offset);
		return p;
	}
}