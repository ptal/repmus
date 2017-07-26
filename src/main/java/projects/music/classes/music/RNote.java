package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.tools.Fraction;
import projects.music.classes.abstracts.Simple_S_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_RT;
import projects.music.editors.MusChars;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.Figure;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.I_FigureDrawable;
import projects.music.editors.drawables.SimpleDrawable;
import projects.music.midi.I_PlayEvent;
import projects.music.midi.PlayEvent.PlayEventMidi;
import projects.music.classes.abstracts.RTree;

@Omclass(icon = "137", editorClass = "projects.music.classes.music.RNote$RNoteEditor")
public class RNote extends Simple_S_MO implements I_RT {

	@Omvariable
	public int midic = 0;
	@Omvariable
    public int vel = 0;
	@Omvariable
	public int chan = 0;

	List<Integer> domaine = null;
	boolean undefRhythm = false;
	public List<Integer> posibles = new ArrayList<Integer>();

	@Omvariable
	public Fraction dur = new Fraction (1,4);

	public RNote (int themidic, int thevel, Fraction thedur, double tempo, int thechan, long offset) {
		super ();
		setOffset(offset);
		setQDurs(thedur);
		midic = themidic;
		vel = thevel;
		chan = thechan;
		List<Long> tiednotes = decomposeDur(thedur.num);
		List<RTree> proplist = new ArrayList<RTree>();
		tiednotes.forEach((x) -> {proplist.add(new RTree (x,true));});
		tree = new RTree(thedur,proplist);
	}

	@Ombuilder(definputs="6000; 80; 29/12; 60; 1")
	public RNote (int midic, int vel, Fraction dur, double tempo, int chan) {
		this (midic, vel, dur, tempo, chan, 0);
	}

	public RNote() {
	    this (6000,80,new Fraction(5,1),60, 1,0);
	}

//////////////////////////////////////

	public int getMidic () {
		return midic;
	}

	public void setMidic (int themidic) {
		midic = themidic;
	}

	public int getChan () {
		return chan;
	}

	public void setChan (int thechan) {
		chan = thechan;
	}

	public List<Integer> getDom () {
		return domaine;
	}

	public void setDom (List<Integer> dom) {
		domaine = dom;
	}

	public boolean getUndefRhythm () {
		return undefRhythm;
	}

	public void setUndefRhythm (boolean val) {
		undefRhythm = val;
	}


	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new RNoteDrawable (this, params, 0, root);
	}

	public int getVel () {
		return vel;
	}

	public void setVel (int thevel) {
		vel = thevel;
	}

	@Override
	public void PrepareToPlayMidi (long at , int approx, List<I_PlayEvent> list) {

    	try {
    		ShortMessage msg1 = new ShortMessage ();
			msg1.setMessage(ShortMessage.NOTE_ON + getChan(), getMidic()/100, getVel());
			ShortMessage msg2 = new ShortMessage ();
	    	msg2.setMessage(ShortMessage.NOTE_OFF + getChan(), getMidic()/100, 0);
			PlayEventMidi noteOn = new PlayEventMidi(msg1, at);
			PlayEventMidi noteOff = new PlayEventMidi(msg2, at + this.getDuration());
			list.add(noteOn);
			list.add(noteOff);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}


//////////////////////////////////////////////////
//////////////////////EDITOR//////////////////////
//////////////////////////////////////////////////
public static class RNoteEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.RNote$RNotePanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.RNote$RNoteControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.RNote$RNoteTitle";
}

}

//////////////////////PANEL//////////////////////
public static class RNotePanel extends MusicalPanel {

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
public static class RNoteControl extends MusicalControl {

}

//////////////////////TITLE//////////////////////
public static class RNoteTitle extends MusicalTitle {

}

//////////////////////DRAWABLE//////////////////////
public static class RNoteDrawable extends ContainerDrawable {
	public String head = MusChars.head_4;
	public int points = 0;

	String altChar = "";
	int posY;
	int auxlines;
	int altSize = 0;
	public int deltaHead = 0;
	public int deltaAlt = 0;
	boolean tied_p = false;
	boolean up_p = true;
	final int headSizefactor = 4;
	final int altSizefactor = 3;

	List<I_FigureDrawable> figures = null;

	Font omicronFont ;
	Font omHeads ;
	int staffnum = 0;
	public int[]  domain = null;


	public RNoteDrawable (RNote theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		InitRNoteDrawable(theRef, params, thestaffnum);
	}

	public RNoteDrawable (RNote theRef, MusicalParams params, int thestaffnum, List<I_FigureDrawable> figure) {
		figures = figure;
		for (I_Drawable item : figures)
			((Figure) item).ref=this;
		InitRNoteDrawable(theRef, params, thestaffnum);
	}

	public RNoteDrawable (RNote theRef, MusicalParams params, int thestaffnum) {
		InitRNoteDrawable(theRef, params, thestaffnum);
	}


	public void InitRNoteDrawable (RNote theRef, MusicalParams theparams, int thestaffnum) {
		staffnum = thestaffnum;
		params = theparams;
		int size = params.fontsize.get();
		StaffSystem staffSystem = params.getStaff();
		MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
		Scale scale = Scale.getScale (params.scale.get());
		double staffToppixels = staff.getTopPixel(size);
		double staffBottompixels = staff.getBottomPixel(size);
		ref = theRef;
		List<Integer> dom = ((RNote) ref).getDom();
		if ( dom != null) {
			domain = new int[2];
			domain[0] = staff.getPosY(scale, dom.get(0));
			domain[1] = staff.getPosY(scale, dom.get(1));
		} else {
			altChar = scale.getAlteration(theRef.getMidic());
			if (altChar != "") deltaAlt = 1;
			altSize = altChar.length();
			auxlines = StaffSystem.setAuxLines (theRef.getMidic(), staff); //combaiar a staff
			posY = staff.getPosY (scale, theRef.getMidic());
			if (  (posY * size/8) <  ((staffBottompixels - staffToppixels) / 2))
				up_p = false;
			if (figures == null) {
				figures = new ArrayList<I_FigureDrawable> ();
				for (Figure item : Strie_MO.dur2symbols (theRef.getQDurs()))
						figures.add(item);
				for (I_Drawable item : figures)
					((Figure) item).ref=this;
			}
		}
		if (editor_root) {
			makeSpaceObjectList();
			consTimeSpaceCurve(size, 0,params.zoom.get());
		}
	}

	@Override
	public void drawObject(I_Render g, Rectangle rect, List<I_Drawable> selection,
				double x0, double deltax, double deltay) {

			int size = params.fontsize.get();
			int dent = size/4;
			StaffSystem staffSystem = params.getStaff();
			MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
			double stafftoppixels = staff.getTopPixel(size);
			double yPos = stafftoppixels + (posY * dent/2);
			double tiesize = 0;
			//setRectangle(x(), yPos, w(), h());
			Figure fig;
			int len = figures.size();
			int startgroup = -1;
			long numgroup = 0;
			for (int i = 0; i < len ; i++ ) {
				fig = (Figure) figures.get(i);
				if (i+1 < len)
					tiesize = figures.get(i+1).getCX() - fig.getCX() - ((Figure) figures.get(i)).getStrSize(size) ;
				else
					tiesize = 0;
				double xPos =  fig.getCX() + deltax;
				if (fig.hasDenom() && startgroup == -1) {
					startgroup = i;
					numgroup = fig.denom;
				}
				if (domain == null) {
					drawNoteHead(g, params, staffSystem, size, fig.head,
							fig.stem_p && (startgroup == len-1 || startgroup == -1),
							fig.beamsnum, fig.points,
							xPos, yPos, tiesize, up_p , fig.getStrSize(size));
				}
				else {
					drawDomain(g, staff, size, 0, fig.stem_p, fig.beamsnum, fig.points, xPos, 1, tiesize, false, fig.head);
				}
			}
			if (startgroup != -1)
				drawUnite(g, params, startgroup, numgroup, deltax, size);
		//	if (tied_p) drawTie(g, size, xPos, yPos);
		//drawExtras (g, view, size, xPos, yPos, zoom);
	}

	//pas de probleme s'il faut le sortir du staff
	public void drawUnite (I_Render g, MusicalParams params, int start, long num, double deltax, int size) {
		Figure fig1 = (Figure) figures.get(start);
		Figure fig2 = (Figure) figures.get(figures.size() - 1);
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double stafftoppixels = staff.getTopPixel(size);

		double yPos = stafftoppixels + (posY * size/8);
		double barYPos;
		if (up_p){
			barYPos = 1000000;
			for (int i = start; i < figures.size() ; i++ ) {
				Figure item = (Figure) figures.get(i);
				barYPos = Math.min(barYPos, yPos - item.getStemSize(item.getBeamsNum(),  size));
			}
			} else{
				barYPos = 0;
				for (int i = start; i < figures.size() ; i++ ) {
					Figure item = (Figure) figures.get(i);
					barYPos = Math.max(barYPos, yPos + item.getStemSize(item.getBeamsNum(), size));
				}
			}

		Font thefont = params.getFont("normal2.3Size");
		String thenum = num+"";
		double ssize = FX.omStringSize(thenum,  thefont);
		Font oldfont = FX.omGetFont(g);
		FX.omSetFont(g, thefont);
		if (up_p) {
			double y0 = barYPos - size/3;
			double x0 = fig1.getCX() + deltax;
			double x1 = fig2.getCX()+deltax + fig2.getStrSize(size) +size/4;
			FX.omDrawLine(g, x0, y0, Math.max(x0+ 4, x1 - ssize + 1) , y0);
			FX.omDrawLine(g, x0, y0, x0 , y0 + size/4);
			FX.omDrawString(g, Math.max(x0+ 4, x1 - ssize + 1), y0 + size/8, num+"");
		}
		else {
			double y0 = barYPos + size/3;
			double x0 = fig1.getCX() + deltax - size/4;
			double x1 = fig2.getCX()+deltax + fig2.getStrSize(size) ;
			FX.omDrawLine(g, x0, y0, Math.max(x0+ 4, x1 - ssize + 1) , y0);
			FX.omDrawLine(g, x0, y0, x0 , y0 - size/4);
			FX.omDrawString(g, Math.max(x0+ 4, x1 - ssize + 1), y0 + size/6, num+"");
		}
		FX.omSetFont(g, oldfont);
		if (start != figures.size() - 1) {
			for (int i = start; i < figures.size() ; i++ )
				drawSimpleStem(g, ((Figure) figures.get(i)), stafftoppixels, deltax, size, up_p, barYPos);
			drawBeams(g, up_p, size, deltax, figures, barYPos, 1.0 + size * 3/16);
		}
	}

	public void drawSimpleStem (I_Render g, Figure fig, double stafftoppixels,double deltax,
			int size, boolean up_p, double ybarpos) {

		double strsize = fig.getStrSize(size);
		double y = stafftoppixels + (posY * size/8);
		if (up_p) {
			double x = fig.getCX() + deltax + strsize - 0.5;
			FX.omDrawLine(g, x, y, x, ybarpos);
		}
		else {
			double x = fig.getCX() + deltax +  0.5;
			FX.omDrawLine(g, x, y, x, ybarpos);
		}
	}


	public void drawNoteHead (I_Render g, MusicalParams params, StaffSystem staffsys, int size, String head, boolean stem_p, int  beamsNum, int points,
			double xPos, double yPos, double tieSize , boolean down_p, double strsize) {

		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		Font omicronFont = params.getFont("microSize");
		Font omHeads = params.getFont("headSize");
		double posAlt = xPos;
		double x0 = xPos;
		double y0 = yPos;
		if (altChar != "")
			posAlt = Math.round (xPos - ((deltaAlt * size * 3) / 10));
		else posAlt = xPos;
		xPos =  xPos + (deltaHead * strsize) ;
		//Draw head
		if (((RNote) ref).getUndefRhythm())
			FX.omDrawString(g, xPos , yPos, MusChars.head_x);
		else FX.omDrawString(g, xPos , yPos, head);
		setRectangle(x(), yPos - size/8, w(), size/4);
		//Draw points
		drawPoints(g, xPos + strsize + size/5, yPos + + size/8, points, size);
		//Draw alteration
		if (altChar != "") {
			FX.omSetFont(g, omicronFont);
			FX.omDrawString(g, posAlt, yPos, altChar);
			FX.omSetFont(g, omHeads);
			setRectangle(posAlt, yPos - size/8 , xPos + strsize + size/5 - posAlt , size/4);
		}
		//Draw slurs
		if (tieSize != 0) {
			drawSlur(g, xPos + strsize, yPos + (size * 1/8), tieSize, down_p, size);
		}
		//Draw auxlines
		StaffSystem.drawAuxLines (g, auxlines, size, xPos, yPos, staff, strsize);
		//draw stem
		if (stem_p && beamsNum != -1)
			drawStem(g, xPos + strsize, yPos - size/8, size, beamsNum, strsize, up_p, (size/8) * 7);
		x0 = Math.min (x0, x());
		y0 = Math.min (y0, y());
		setRectangle(x0, y0 , x()+w() - x0, y()+h() - y0);
	}

	public void drawDomain (I_Render g, MultipleStaff staff, int size, int pos, boolean stem_p, int  beamsNum, int points,
			double xPos, int numnotes, double tieSize, boolean down_p, String head) {
		double rectw = Math.max(4, size/4/numnotes);
		double x0 = x();
		double y0 = y();

		Font omicronFont = params.getFont("microSize");
		Font omHeads = params.getFont("headSize");


		double stafftoppixels = staff.getTopPixel(size);
		double yPos = stafftoppixels + (domain[1] * size/8);
		double recth = ((domain[0] - domain[1]) * size/8);
		Color color = FX.omGetColorFill(g);
		FX.omSetColorFill(g, FX.SixtheenColors[pos % 16]);
		FX.omFillRect(g, xPos + pos*rectw , yPos, rectw, recth);
		FX.omSetColorFill(g, color);
		setRectangle(xPos , yPos, rectw, domain[1] - domain[0]);
		//Draw points
		drawPoints(g, xPos + rectw + size/5, rectw/2 + size/8, points, size);
		//Draw slurs
		if (tieSize != 0)
			drawSlur(g, xPos + rectw, yPos + (size * 1/8), tieSize, down_p, size);
		//draw stem
		if (stem_p && beamsNum != -1)
			drawStem(g, xPos + rectw, yPos - size/8, size, beamsNum, rectw, up_p, (size*2.5)/6);
		x0 = Math.min (x0, x());
		y0 = Math.min (y0, y());
		setRectangle(x0, y0 , x()+w() - x0, y()+h() - y0);


		Scale scale = Scale.getScale (params.scale.get());
		int k = 0;
		for (int posible : ((RNote) ref).posibles) {
			double posY = stafftoppixels + (staff.getPosY (scale, posible) * size/8);
			String altChar = scale.getAlteration(posible);

			if (((RNote) ref).posibles.size() == 2 && k == 0) {
				FX.omSetColorFill(g, FX.SixtheenColors[6]);
				FX.omFillRect(g, xPos - 4 , posY - 4, size/3 + 6, size/3+4);
				FX.omSetColorFill(g, color);
			}
			if (altChar != "") {
				FX.omSetFont(g, omicronFont);
				FX.omDrawString(g, xPos - size/3, posY, altChar);
				FX.omSetFont(g, omHeads);
			}
			FX.omDrawString(g, xPos , posY, head);
			k++;
		}
	}

	public void drawPoints (I_Render g, double x, double y,int num, int size) {
		double space =  size / 5;
		for (int i = 0; i < num; i++)
			FX.omDrawString (g, x + i * space, y , MusChars.dot);
	}

	public void drawSlur (I_Render g, double x0, double y0, double w, boolean dwn_p, int size) {
		if (dwn_p)
			FX.omDrawBezierCurve(g, x0, y0, x0 + w, y0, x0, y0 + size/4,  x0 + w , y0 + size/4);
		else
			FX.omDrawBezierCurve(g, x0, y0-size/4, x0 + w, y0-size/4, x0, y0 - size/2,  x0 + w , y0 - size/2);
	}

	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {
		double tempo = ((RNote) ref).getTempo();
		timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), true));
		long onset = this.ref.getOnsetMS();
		for (int i=1; i < figures.size(); i++){
			onset = onset + ((Figure) figures.get(i-1)).getDurMS(tempo);
			timelist.add(new SpacedPacket(figures.get(i),onset, true));
		}
	}

	@Override
	public void computeCX(SpacedPacket pack, int size) {
		Figure fig = (Figure) figures.get(0);
		if (domain == null) {
			double x0 = pack.start;
			double strsize = fig.getStrSize(size);
			double deltal = 0;
			double deltar = 0;
			deltal = (deltaAlt * strsize * 3) / 10;
			deltar =  (deltaHead * strsize) + strsize;
			pack.updatePacket(0, 0, deltal, deltar);
			setRectangle(x0, y(), deltal + deltar - x0 + strsize, h());
		}
	}

}
//////////////////////////////////////////////////////


}
