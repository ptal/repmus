package gui;

import gui.renders.I_Render;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public interface I_SimpleView {
	
	//Color+Font
	public Color omGetBackground ();
	public void omSetBackground(Color color);
	public Font omGetFont();
	public void omSetFont(Font font);

	public Cursor omGetCursor();
	public void omSetCursor(Cursor cursor);


	//Size+position
	public Point2D omViewSize ();
	public void omSetViewSize (double x, double y);
	public default void omSetViewSize(Point2D size) {
		omSetViewSize(size.getX(), size.getY());
	}
	
	public Point2D omViewPosition ();
	public void omSetViewPosition (double x, double y);
	public default void omSetViewPosition(Point2D pos) {
		omSetViewPosition(pos.getX(), pos.getY());
	}
	
	public default double h () {return omViewSize().getY();}
	public default double w () {return omViewSize().getX();}
	public default double x () {return omViewPosition().getX();}
	public default double y () {return omViewPosition().getY();}

	//
	public I_ContainerView omViewContainer ();
	//
	public void omUpdateView (boolean changedObject_p);
	public void omViewDrawContents (I_Render r);
	
	public I_SimpleView getPaneWithPoint (double x, double y);
	
	//public ScrollPane omGetScroller ();
	
	}

