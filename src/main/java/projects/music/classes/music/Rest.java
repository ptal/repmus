package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import javafx.scene.text.Font;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.tools.Fraction;
import kernel.tools.ST;
import projects.music.classes.abstracts.Simple_S_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_InRSeqChord;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.RNote.RNoteDrawable;
import projects.music.editors.MusChars;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.drawables.Figure;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.I_FigureDrawable;
import projects.music.editors.drawables.SimpleDrawable;
import projects.music.midi.I_PlayEvent;
import projects.music.midi.PlayEvent.PlayEventMidi;

@Omclass(icon = "142", editorClass = "projects.music.classes.music.Rest$RestEditor")
public class Rest extends Simple_S_MO implements I_RT, I_InRSeqChord {

	//tester avec 1 5 23 1/2 1/3 1/4 1/12 1/21 1/1000
	public Rest () {
		this (new Fraction(1,4));
	}

	@Ombuilder (definputs="1/4")
	public Rest (Fraction  dur) {
		super ();
		setQDurs(dur);

	}

	public Rest (Fraction  dur, double tempo) {
		super ();
		setTempo(tempo);
		setQDurs(dur);
	}

	/////////////////////////////////////////////

	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new RestDrawable (this, params, 0, root);
	}



//////////////////////////////////////////////////
//////////////////////EDITOR//////////////////////
//////////////////////////////////////////////////
public static class RestEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.Rest$RestPanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.Rest$RestControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Rest$RestTitle";
}

}

//////////////////////PANEL//////////////////////
public static class RestPanel extends MusicalPanel {

public void KeyHandler(String car){
switch (car) {
case "h" : takeSnapShot ();
break;
case "c": delegate.setScaleX( delegate.getScaleX() * 1.1);
delegate.setScaleY( delegate.getScaleY() * 1.1);
break;
case "C": delegate.setScaleX( delegate.getScaleX() * 0.9);
delegate.setScaleY( delegate.getScaleY() * 0.9);
break;
}
}

@Override
public int getZeroPosition () {
return 2;
}

}


//////////////////////CONTROL//////////////////////
public static class RestControl extends MusicalControl {

}

//////////////////////TITLE//////////////////////
public static class RestTitle extends MusicalTitle {

}

//////////////////////DRAWABLE//////////////////////
public static class RestDrawable extends SimpleDrawable  implements I_FigureDrawable {
	List<Figure> figures;
	Font omicronFont ;
	Font omHeads ;
	int beamsNum;
	int staffnum = 0;

	public RestDrawable (Rest theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		initRestDrawable(theRef, params, thestaffnum);
	}


	public RestDrawable (Rest theRef, MusicalParams params, int thestaffnum) {
		initRestDrawable(theRef, params, thestaffnum);
	}

	public void initRestDrawable (Rest theRef, MusicalParams theparams, int thestaffnum) {
		ref = theRef;
		params = theparams;
		staffnum = thestaffnum;
		int size = params.fontsize.get();
		omicronFont = new Font("omicron",size);
		omHeads = new Font("omheads",size);
		Fraction duration = theRef.getQDurs();
		figures = Strie_MO.dur2symbols (new Fraction (duration.getNumerator() * -1, duration.getDenominator()));
		beamsNum = figures.get(0).beamsnum;
	}

	@Override
	public void drawObject(I_Render g, Rectangle rect,
			List<I_Drawable> selection, double x0,
			double deltax, double deltay) {
		FX.omSetFont(g, omHeads);
		int size = params.fontsize.get();
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double staffToppixels = staff.getTopPixel(size);
		double staffBottompixels = staff.getBottomPixel(size);
		double xPos =  getCX() + deltax;
		double yPos = staffToppixels + (staffBottompixels - staffToppixels)/2;
		int i = 1;
		for (Figure fig : figures) {
			drawRestHead(g, staffsys, size, fig.head, fig.beamsnum, fig.points, xPos, yPos);
			xPos = xPos + (size * 1.5);
			i = i+1;
		}
	}

	public void drawRestHead (I_Render g, StaffSystem staffsys, int size, String head, int  beamsNum, int points,
			double xPos, double yPos) {
		//Draw head
		double strsize = FX.omStringSize(head,omHeads);
		FX.omDrawString(g, xPos , yPos, head);
		//Draw points
		drawPoints(g, xPos + strsize + size/5, yPos + size/8, points, size);
	}

	public void drawPoints (I_Render g, double x, double y,int num, int size) {
		double space =  size / 5;
		for (int i = 0; i < num; i++)
			FX.omDrawString (g, x + i * space, y , MusChars.dot);
	}

	public void groupDrawStems(I_Render g, boolean up_p, double deltax, double ybarpos) {

	}


	//////////////////////////////////////////////////
	////////////I_FigureDrawable
	@Override
	public int getBeamsNum() {
		return figures.get(0).beamsnum;
	}

	@Override
	public double getHeadSize(int size) {
		return figures.get(0).getStrSize(size);
	}

	////////////
	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {
		  timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), true));
	}


	@Override
	public void computeCX(SpacedPacket pack, int size) {
		pack.updatePacket(0, 0, size/4, size/4);
	}


	@Override
	public double getUpYpos(int size) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double getDwnYpos(int size) {
		// TODO Auto-generated method stub
		return 0;
	}


}
//////////////////////////////////////////////////////
}


