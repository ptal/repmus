package projects.music.editors.drawables;

import gui.renders.I_Render;
import gui.CanvasFX;
import java.util.List;

import javafx.scene.text.Font;
import projects.music.classes.interfaces.I_MusicalObject;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;

import com.sun.javafx.geom.Rectangle;

public class StaffBound extends SimpleDrawable{

	boolean end_p;
	
	double startx;
	double endx;
	
	public StaffBound (MusicalParams theparams, I_MusicalObject obj, boolean end) {
		end_p = end;
		params = theparams;
		ref = obj;
	}
	
	@Override
	public void computeCX(SpacedPacket pack, int size) {
		if (end_p)
			pack.updatePacket(0, size, 0, 0);
		else 
			pack.updatePacket(size*2, 0, 0, 0);
		//setRectangle(x0, y(), deltal + deltar - x0 + strsize, h());	
	} 
	
	@Override
	public double getCX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCX(double cx) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void consTimeSpaceCurve(int size, double x, int zoom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawObject(I_Render g, Rectangle rect,
			List<I_Drawable> selection, double x0, double deltax, double deltay) {
		if (! end_p) {
			StaffSystem  staff = params.getStaff();
			Font oldFont = g.omGetFont();
			g.omSetFont(params.getFont("singSize"));
			int size = params.fontsize.get();
			double w = ContainerDrawable.time2pixel (ref.getOnsetMS() + ref.getDuration(), size) -
					x0+deltax;
			staff.draw (g, x0+deltax, 0, w, size/4, false, false, size);
			g.omSetFont(oldFont);
		}
	}

}
