package gui.dialogitems;

import gui.I_ContainerView;
import gui.I_SimpleView;
import gui.renders.I_Render;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class OmStaticText extends Label implements I_Widget  {
	
	
	public EventHandler<ActionEvent> action;
	
	public OmStaticText (String text, EventHandler<ActionEvent> fun, int font, int x, int y, int w, int h) {
		super(text);
		action = fun;
		omSetViewSize(w,h);
		setStyle("-fx-font-size: " + font);
		omSetViewPosition (x,y);
	}
	
	public OmStaticText (String text) {
		super(text);
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
	
	//////////////////////////////////////////////
	@Override
	public void omSetText(String text) {
		setText(text);	
	}

	@Override
	public String omGetText() {
		return getText();
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
