package projects.music.editors.drawables;

import gui.FXCanvas;
import gui.renders.I_Render;

import java.util.List;

import javafx.scene.text.Font;
import projects.music.editors.MusicalParams;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;

import com.sun.javafx.geom.Rectangle;

public class StaffBound extends SimpleDrawable{

	boolean end_p;
	
	double startx;
	double endx;
	
	public StaffBound (MusicalParams theparams, boolean end) {
		end_p = end;
		params = theparams;
	}
	
	@Override
	public double computeCX(SpacedPacket pack, int size) {
		// TODO Auto-generated method stub
		return 0;
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
	public void translateCX(double max, SpacedPacket pack) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void consTimeSpaceCurve(int size, double x, int zoom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawObject(I_Render g, FXCanvas panel, Rectangle rect,
			List<I_Drawable> selection, double x0, double deltax, double deltay) {
		if (! end_p) {
			StaffSystem  staff = params.getStaff();
			Font oldFont = g.omGetFont();
			g.omSetFont(params.getFont("singSize"));
			int size = params.fontsize.get();
			staff.draw(g, x0, 0, 100, size/4, false, false, size);
			g.omSetFont(oldFont);
		}
	}

}
