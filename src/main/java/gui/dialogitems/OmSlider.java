package gui.dialogitems;


import gui.I_ContainerView;
import gui.I_SimpleView;
import gui.renders.I_Render;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OmSlider extends Slider implements I_Widget {
	
	public OmSlider (int dir, double min, double max, double value) {
		super(min, max, value);
		if (dir == 0)
			setOrientation(Orientation.HORIZONTAL);
		else setOrientation(Orientation.VERTICAL);
		setShowTickMarks(true);
		setShowTickLabels(true);
		setBlockIncrement(1);
	}
	

	@Override
	 public void omSetEnabled(boolean bool){
		 setDisable (bool);
	 }
	 
	@Override
	 public boolean omGetEnabled(){
		 return isDisabled ();
	 }
	
	@Override
	public Point2D omViewSize() {
		return new Point2D (prefWidth(-1), prefHeight(-1));
	}

	@Override
	public void omSetViewSize(double w, double h) {
		setPrefWidth(w);
		setPrefHeight(h);
	}
	

	@Override
	public Point2D omViewPosition() {
		return new Point2D (getLayoutX(), getLayoutY());
	}
	
	
	@Override
	public void omSetViewPosition(double x, double y) {
		relocate (x,y);
	}
	

	@Override
	public void callAction() {
	}

	@Override
	public Color omGetBackground() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void omSetBackground(Color color) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Font omGetFont() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void omSetFont(Font font) {
		// TODO Auto-generated method stub
	}

	@Override
	public Cursor omGetCursor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void omSetCursor(Cursor cursor) {
		// TODO Auto-generated method stub
	}

	@Override
	public I_ContainerView omViewContainer() {
		// TODO Auto-generated method stub
		return (I_ContainerView) getParent();
	}


	@Override
	public void omSetText(String str) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String omGetText() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void omUpdateView(boolean changedObject_p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void omViewDrawContents(I_Render r) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public I_SimpleView getPaneWithPoint(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

}