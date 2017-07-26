package gui.dialogitems;

import gui.I_ContainerView;
import gui.I_SimpleView;
import gui.renders.I_Render;
import javafx.event.Event;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;


public class OmPopUp extends ComboBox<String> implements I_Widget {
	
	@FunctionalInterface
	public interface ActionFun {
		   void itemAction (OmPopUp e);
		}
	
	ActionFun actionFun;
	
	public OmPopUp (String[] list, ActionFun fun, int font, int x, int y, int w, int h, String val) {
		super();
		for (String item : list) {
			getItems().add(item);
		}
		actionFun = fun;
		setFocusTraversable(false);
		setPrefWidth(w);
		setPrefHeight(h);
		//setMinHeight(h);
		setStyle("-fx-font-size: " + font);
		relocate (x,y);
		setValue(val);
		//setOnAction(ActionEvent ev) -> {actionFun.itemAction(this);});
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
	
	public String omGetSelectedItem () {
		return getSelectionModel().getSelectedItem().toString();
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
