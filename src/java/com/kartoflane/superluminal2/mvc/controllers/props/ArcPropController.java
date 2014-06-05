package com.kartoflane.superluminal2.mvc.controllers.props;

import com.kartoflane.superluminal2.mvc.controllers.AbstractController;
import com.kartoflane.superluminal2.mvc.models.BaseModel;
import com.kartoflane.superluminal2.mvc.views.props.ArcPropView;

public class ArcPropController extends PropController {
	private int startAngle = 0;
	private int arcSpan = 0;

	public ArcPropController(AbstractController parent, String id) {
		super(parent, new BaseModel(), new ArcPropView(), id);
	}

	public void setStartAngle(int angle) {
		startAngle = angle;
	}

	public int getStartAngle() {
		return startAngle;
	}

	public void setArcSpan(int angle) {
		arcSpan = angle;
	}

	public int getArcSpan() {
		return arcSpan;
	}
}
