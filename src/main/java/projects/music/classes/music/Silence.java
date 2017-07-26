package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.List;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import projects.music.classes.abstracts.Simple_L_MO;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;
import projects.music.midi.I_PlayEvent;


	@Omclass(icon = "138", editorClass = "projects.music.classes.music.Silence$SilenceEditor")
	public class Silence extends Simple_L_MO {

		@Omvariable
		public long dur = 0;


		@Ombuilder(definputs="1000")
		public Silence (long thedur) {
			dur = thedur;
		}

		public Silence() {
			this(1000);
		}

		@Override
		public long getDuration() {
			return dur;
		}


		@Override
		public void setDuration(long thedur) {
			dur = thedur;
		}

		public I_Drawable makeDrawable (MusicalParams params, boolean root) {
			return new SilenceDrawable (this, params, 0, root);
		}



	//////////////////////////////////////////////////
	//////////////////////EDITOR//////////////////////
	//////////////////////////////////////////////////
	public static class SilenceEditor extends MusicalEditor {


	@Override
	public String getPanelClass (){
	return "projects.music.classes.music.Silence$SilencePanel";
	}

	@Override
	public String getControlsClass (){
	return "projects.music.classes.music.Silence$SilenceControl";
	}

	@Override
	public String getTitleBarClass (){
	return "projects.music.classes.music.Silence$SilenceTitle";
	}

	}

	//////////////////////PANEL//////////////////////
	public static class SilencePanel extends MusicalPanel {

	public void KeyHandler(String car){
		switch (car) {
		case "h" : takeSnapShot ();
			break;
		}
	}


	@Override
	public int getZeroPosition () {
	return 2;
	}

	}


	//////////////////////CONTROL//////////////////////
	public static class SilenceControl extends MusicalControl {

	}

	//////////////////////TITLE//////////////////////
	public static class SilenceTitle extends MusicalTitle {

	}

	//////////////////////DRAWABLE//////////////////////
	///////////////////////////////////////////////////
	public static class SilenceDrawable extends SimpleDrawable {
		int staffnum =0;

		public SilenceDrawable (Silence theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
			editor_root = ed_root;
			InitSilenceDrawable(theRef, params, thestaffnum);
		}

		public SilenceDrawable (Silence theRef, MusicalParams params, int thestaffnum) {
			InitSilenceDrawable(theRef, params, thestaffnum);
		}

		public void InitSilenceDrawable (Silence theRef, MusicalParams theparams, int thestaffnum) {
			params = theparams;
			ref = theRef;
			staffnum = thestaffnum;
			int size = params.fontsize.get();
			StaffSystem staffSystem = params.getStaff();
			MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
			double staffToppixels = staff.getTopPixel(size);
			double staffBottompixels = staff.getBottomPixel(size);
			double ycenter = staffToppixels + (staffBottompixels - staffToppixels) / 2;
			setRectangle (0, ycenter - size/8, ms2pixel(((Silence) ref).dur, size),size*3/8);
		}

		@Override
		public void drawObject(I_Render g, Rectangle rect,
				 List<I_Drawable> selection, double x0,
				double deltax, double deltay) {
			//voila le bon exemple
			//les rectangles sont mis avant de dessiner
			//on ne le calcul pas pendant le dessein
			//c'est de la paresse
			int size = params.fontsize.get();
			double x = x0 + x() + deltax;
			FX.omFillRect(g, x, y() + size/8, w(), h()/3);
			FX.omDrawLine(g, x, y(), x, y()+h());
			FX.omDrawLine(g, x+w(), y(), x+w(), y()+h());
		}

		public void collectTemporalObjects(List<SpacedPacket> timelist) {
			  timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), false));
		}

	}

}

