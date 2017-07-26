package gui.dialogitems;

import gui.I_ContainerView;
import gui.I_SimpleView;
import gui.renders.I_Render;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class OmCheckbox extends CheckBox implements I_Widget {


	public OmCheckbox (String text,  boolean ckecked, EventHandler<ActionEvent> fun) {
		setText(text);
		setSelected(ckecked);
		setOnAction(fun);
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
	
	public void omSetCheck (boolean val) {
		setSelected(val);
	}
	 
	public boolean omIsSelected () {
	    return isSelected();
	}
	 

	public void omSetText (String str) {
		setText(str);
	}
		 
	public String omGetText () {
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
