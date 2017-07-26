package projects.music.editors.drawables;

import gui.renders.I_Render;

import java.util.List;

import com.sun.javafx.geom.Rectangle;
import projects.music.classes.interfaces.I_MusicalObject;
import projects.music.editors.SpacedPacket;

public interface I_Drawable {
	public I_Drawable getClickedObject (double x, double y);
	public void drawObject (I_Render g,  Rectangle rect, List<I_Drawable> selection, double x0, double deltax, double deltay);
	public void drawContainersObjects (I_Render g,  Rectangle rect, List<I_Drawable> selection, double deltax);
	
	public double h ();
	public double w ();
	public double x ();
	public double y ();

	//
	public List<I_Drawable> getInside ();
	public void setFather (ContainerDrawable thefather);
	public I_MusicalObject getRef();
	//
	
	public void collectTemporalObjects(List<SpacedPacket> timelist);
	
	public void computeCX(SpacedPacket pack, int size);
	public double getCX();
	public void setCX(double cx);
	//public void translateCX(double max, SpacedPacket pack);

	public void consTimeSpaceCurve (int size, double x, int zoom) ;
	

}
