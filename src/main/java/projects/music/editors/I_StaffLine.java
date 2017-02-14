package projects.music.editors;

import projects.music.editors.StaffSystem.MultipleStaff;
import gui.renders.I_Render;

public interface I_StaffLine {
	
	public void setInitEndY (double y);
	public int getHeight ();
	public int getInterspace ();
	public void draw(I_Render g, double x, double y, double w, double dent, boolean tempo, 
			boolean selected_p);
	public  MultipleStaff ClickInKey  (double x, double y, int size);
	public  void offSelected  ();
	public double getTopPixel (int size);
	public double getBottomPixel (int size);
		
}
