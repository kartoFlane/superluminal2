package com.kartoflane.superluminal2.mvc.controllers;

import com.kartoflane.superluminal2.components.LayeredPainter.Layers;
import com.kartoflane.superluminal2.mvc.BaseModel;
import com.kartoflane.superluminal2.mvc.View;
import com.kartoflane.superluminal2.mvc.views.CellView;
import com.kartoflane.superluminal2.ui.ShipContainer;

public class CellController extends AbstractController {

	private CellController(BaseModel model, CellView view) {
		super();
		setModel(model);
		setView(view);

		setSelectable(false);
		setBounded(false);
	}

	public static CellController newInstance(int i, int j) {
		BaseModel model = new BaseModel();
		CellView view = new CellView();
		CellController controller = new CellController(model, view);

		// 1px bigger, since cells are overlapping at borders
		controller.setSize(ShipContainer.CELL_SIZE + 1, ShipContainer.CELL_SIZE + 1);
		controller.setLocation(ShipContainer.CELL_SIZE / 2 + 1 + i * ShipContainer.CELL_SIZE,
				ShipContainer.CELL_SIZE / 2 + 1 + j * ShipContainer.CELL_SIZE);

		return controller;
	}

	@Override
	public void setView(View view) {
		super.setView(view);
		this.view.addToPainter(Layers.GRID);
	}
}
