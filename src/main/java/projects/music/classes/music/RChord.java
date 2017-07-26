package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.tools.Fraction;
import kernel.tools.ST;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Parallel_S_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_InRSeqChord;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.RNote;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.classes.music.RNote.RNoteDrawable;
import projects.music.classes.music.Rest.RestDrawable;
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

@Omclass(icon = "139", editorClass = "projects.music.classes.music.RChord$RChordEditor")
public class RChord extends Parallel_S_MO implements I_RT, I_InRSeqChord {

	@Omvariable
	public List<Integer> lmidic;
	@Omvariable
	public List<Integer> lvel;
	@Omvariable
	public List<Integer> lchan;
	@Omvariable
	public List<Long> loffset;
	@Omvariable
	public Fraction dur;

	public boolean cont_p = false;
	Point2D ambitus;

	//appele par group ou measure
	public RChord (Fraction dur, RChord chord, double tempo, boolean iscont) {
		cont_p = iscont;
		initChordRythmic (chord.lmidic, chord.lvel, chord.loffset, dur, chord.lchan, tempo);
	}

	@Ombuilder(definputs="(6000) ; (80); (0); 1/4; (1)")
	public RChord (List<Integer> themidic, List<Integer> thevel, List<Long> theoffset, Fraction thedur, List<Integer> thechan) {
		this (themidic, thevel, theoffset, thedur, thechan, 60);
	}

	public RChord (List<Integer> themidic, List<Integer> thevel, List<Long> theoffset, Fraction thedur, List<Integer> thechan, int tempo) {
		setOffset(0);
		initChordRythmic(themidic, thevel, theoffset, thedur, thechan, tempo);
	}

	public RChord () {
		List<Integer> themidic = new ArrayList<Integer> ();
		themidic.add(6000);
		List<Integer> thevel = new ArrayList<Integer> ();
		thevel.add(80);
		List<Long> theoffset = new ArrayList<Long> ();
		theoffset.add((long) 0);
		List<Integer> thechan = new ArrayList<Integer> ();
		thechan.add(1);
		setOffset(0);
		initChordRythmic(themidic, thevel, theoffset, new Fraction(1,4), thechan, 60);
	}

	public RChord (int midic) {
		List<Integer> themidic = new ArrayList<Integer> ();
		themidic.add(midic);
		List<Integer> thevel = new ArrayList<Integer> ();
		thevel.add(80);
		List<Long> theoffset = new ArrayList<Long> ();
		theoffset.add((long) 0);
		List<Integer> thechan = new ArrayList<Integer> ();
		thechan.add(1);
		setOffset(0);
		initChordRythmic(themidic, thevel, theoffset, new Fraction(1,4), thechan, 60);
	}

	public void initChordRythmic (List<Integer> themidic, List<Integer> thevel,
			List<Long> theoffset, Fraction thedur, List<Integer> thechan, double tempo) {
		lmidic = themidic;
		lvel = thevel;
		lchan = thechan;
		loffset = theoffset;

		int sizevel = thevel.size();
		int sizechan = thechan.size();
		int sizeoffset = theoffset.size();
		int vel = 60;
		int chan = 0;
		long offset = 0;
		RNote newnote;
		for (int i=0; i< themidic.size(); i++){
			if (i < sizevel) vel = thevel.get(i);
			if (i < sizechan) chan = thechan.get(i);
			if (i < sizeoffset) offset = theoffset.get(i);
			newnote = new RNote(themidic.get(i), vel,thedur,tempo,chan,offset);
			addElement(newnote);
			newnote.setTempo(tempo);
		}
		setSlots();
		setQDurs(thedur);
		setTempo(tempo);
		//setDurPar();
	}

	public void setSlots () {
		int min = 128000;
		int max = 0;
		lmidic = new ArrayList<Integer>();
		loffset = new ArrayList<Long>();
		lvel = new ArrayList<Integer>();
		lchan = new ArrayList<Integer>();
		for (MusicalObject note : getElements()) {
			min = Math.min(min, ((RNote) note).midic);
			max = Math.max(max, ((RNote) note).midic);
			lmidic.add(((RNote) note).midic);
			lvel.add(((RNote) note).vel);
			lchan.add(((RNote) note).chan);
			loffset.add(((RNote) note).getOffset());
		}
		ambitus = new Point2D(min,max);
	}

	//////////////////////////////////////////////////////////////////

	public List<Integer> getLMidic () {
		return lmidic;
	}

	public void setLMidic (List<Integer> midic) {
		lmidic = midic;
	}

	public List<Integer> getLVel () {
		return lmidic;
	}

	public void setLVel (List<Integer> midic) {
		lmidic = midic;
	}

	public List<Integer> getLChan () {
		return lmidic;
	}

	public void setLChan (List<Integer> midic) {
		lmidic = midic;
	}


	public Point2D getAmbitus () {
		return ambitus;
	}

//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new RChordDrawable (this, params, 0, root);
	}


	//////////////////////EDITOR//////////////////////
    public static class RChordEditor extends MusicalEditor {


        @Override
    	public String getPanelClass (){
    		return "projects.music.classes.music.RChord$RChordPanel";
    	}

        @Override
    	public String getControlsClass (){
    		return "projects.music.classes.music.RChord$RChordControl";
    	}

        @Override
    	public String getTitleBarClass (){
    		return "projects.music.classes.music.RChord$RChordTitle";
    	}

    }

    //////////////////////PANEL//////////////////////
    public static class RChordPanel extends MusicalPanel {

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
    public static class RChordControl extends MusicalControl {

    }

    //////////////////////TITLE//////////////////////
    public static class RChordTitle extends MusicalTitle {

    }

    //////////////////////DRAWABLE//////////////////////
    public static class RChordDrawable extends ContainerDrawable  implements I_FigureDrawable {

	public String head = MusChars.head_4;
	String altChar = "";
	int posY;
	final int headSizefactor = 4;
	final int altSizefactor = 3;

	public  boolean stem_p;
	boolean tied_p;
	boolean up_p = true;
	List<I_FigureDrawable> figures = null;

	boolean root_p = false;
	boolean grace_p = false;
	List<RChordDrawable> rchordList;

	public int beamsNum = 0;
	int staffnum = 0;

	public RChordDrawable (RChord theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		InitRChordDrawable(theRef, params, thestaffnum);
	}

	public RChordDrawable (RChord theRef, MusicalParams params, int thestaffnum, List<I_FigureDrawable> figure) {
		figures = figure;
		for (I_Drawable item : figures)
			((Figure) item).ref=this;
		InitRChordDrawable(theRef, params, thestaffnum);
	}

	public RChordDrawable (RChord theRef, MusicalParams params, int thestaffnum) {
		InitRChordDrawable(theRef, params, thestaffnum);
	}

	public void InitRChordDrawable (RChord theRef, MusicalParams theparams, int thestaffnum) {
		ref = theRef;
		if (figures == null) {
			figures = new ArrayList<I_FigureDrawable> ();
			for (Figure item : Strie_MO.dur2symbols (((RChord) ref).getQDurs())) {
				figures.add(item);
				item.ref=this;
			}
		}
		params = theparams;
		staffnum = thestaffnum;
		int size = params.fontsize.get();
		StaffSystem staffSystem = params.getStaff();
		MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
		Scale scale = Scale.getScale (params.scale.get());
		stem_p = params.showstem.get();
		List<MusicalObject> notes = theRef.getElements();
		List<MusicalObject> copie = ((List<MusicalObject>) ((ArrayList<MusicalObject>) notes).clone());
		Collections.sort(copie, pitchUp);
		int pos;
		int j;
		boolean found;
		List<Point2D> taked = new ArrayList<Point2D>();
		setStemDir(staff.getMidiCenter());
		for (MusicalObject note : copie) {
			RNoteDrawable gnote = new RNoteDrawable ((RNote) note, params, staffnum, figures);
			gnote.centerX = 0;
			pos = staff.DemiDents(((RNote) note).getMidic(),scale);
			j = 0;
			found = false;
			while (found == false) {
				if (! (taked.contains(new Point2D(j,pos))
		           	//  || taked.contains(new Point2D(j,pos+1)) ||taked.contains(new Point2D(j,pos-1))
		           	)) {
					found = true;
					taked.add(new Point2D(j,pos));
					taked.add(new Point2D(j,pos+1));
					taked.add(new Point2D(j,pos-1));
					gnote.deltaHead = j;
		           	}
		           	j++;
		        	}
				inside.add(gnote);
				gnote.setFather(this);
			}
			setAltPositions(getInside(), staff, scale);
			beamsNum = ((Figure) figures.get(0)).beamsnum;
			if (editor_root) {
				makeSpaceObjectList();
				consTimeSpaceCurve(size, 0, params.zoom.get());
			}
		}

		public  Comparator<MusicalObject> pitchUp = new Comparator<MusicalObject>() {
			public int compare(MusicalObject n1, MusicalObject n2) {
				return  ((RNote) n1).midic - ((RNote) n2).midic;
			}
		};

		public  Comparator<I_Drawable> byHead = new Comparator<I_Drawable>() {
			public int compare(I_Drawable n1, I_Drawable n2) {
				return  ((RNoteDrawable) n2).deltaHead - ((RNoteDrawable) n1).deltaHead;
			}
		};

		//ATTENTION a la taille de lalteration
		public void setAltPositions (List<I_Drawable> notes, MultipleStaff staff, Scale scale) {
			List<Point2D> taked = new ArrayList<Point2D>();
			Collections.sort(notes, byHead);
			int pos;
			int i;
			int midic;
			String alte;
			int altesize;
			boolean found;
			for (I_Drawable note : notes) {
				midic = ((RNote) ((RNoteDrawable) note).ref).getMidic();
				alte = scale.getAlteration(midic);
				altesize = alte.length();
				if (alte != "") {
					pos = staff.DemiDents(midic,scale);
					i = 1;
					found = false;
					while (found == false) {
						if (! (taked.contains(new Point2D(i,pos)) ||
								taked.contains(new Point2D(i,pos+3)) ||
								taked.contains(new Point2D(i,pos-3))  ||
								taked.contains(new Point2D(i,pos+2)) ||
								taked.contains(new Point2D(i,pos-2)) ||
								taked.contains(new Point2D(i,pos+1)) ||
								taked.contains(new Point2D(i,pos-1)))) {
							found = true;
							taked.add(new Point2D(i,pos));
							taked.add(new Point2D(i,pos+1));
							taked.add(new Point2D(i,pos-1));
							taked.add(new Point2D(i,pos+2));
							taked.add(new Point2D(i,pos-2));
							taked.add(new Point2D(i,pos+3));
							taked.add(new Point2D(i,pos-3));
							((RNoteDrawable) note).deltaAlt = i;
						}
						i++;
					}
				}
			}
		}

	public void setStemDir (double center) {
		Point2D amb = ((RChord) ref).getAmbitus();
		double moyenne = amb.getX() + ((amb.getY() - amb.getX())/2);
		if (moyenne < center * 100)
			up_p = true;
		else
			up_p = false;
	}

	/////////////////////////////////////////////
	@Override
	public void drawObject(I_Render g, Rectangle rect,
			List<I_Drawable> selection, double packetStart,
			double deltax, double deltay) {
		int size = params.fontsize.get();
		int numnotes = getInside().size();
		int len = figures.size();
		int dent = size/4;
		double spaceX = 0;
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double stafftoppixels = staff.getTopPixel(size);
		double xPos = getCX() + deltax;
		double yPos;
		double strsize = 0;
		int j =0;
		Figure fig;
		double tiesize = 0;
		setRectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
		int startgroup = -1;
		long numgroup = 0;
		for (int i = 0; i < len ; i++ ) {
			fig = (Figure) figures.get(i);
			if (i+1 < len)
				tiesize = figures.get(i+1).getCX() - fig.getCX() - fig.getStrSize(size);
			else
				tiesize = 0;
			if (i == 0)
				xPos = getCX() + deltax;
			else
				xPos = fig.getCX() + deltax;
			j=0;
			double initpos=Integer.MAX_VALUE;
			double endpos=0;
			double x_0 = x();
			double y_0 = y();
			if (fig.hasDenom() && startgroup == -1) {
				startgroup = i;
				numgroup = fig.denom;
			}
			for (I_Drawable note : getInside()) {
				RNoteDrawable thenote = (RNoteDrawable) note;
				thenote.setRectangle(Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0);
				strsize = fig.getStrSize(size);
				yPos = stafftoppixels + (thenote.posY * dent/2);
				if (((RNote) thenote.ref).getDom() != null) {
					initpos = Math.min(yPos,stafftoppixels + (thenote.domain[0] * dent/2));
					endpos = Math.max(yPos,stafftoppixels + (thenote.domain[1] * dent/2));
					thenote.drawDomain (g, staff, size, j, false, fig.beamsnum, fig.points,
							xPos, numnotes, tiesize, j < len/2, fig.head);
				}
				else {
					initpos = Math.min(yPos,initpos);
					endpos = Math.max(yPos,endpos);
					thenote.drawNoteHead(g, params, staffsys, size, fig.head, false, fig.beamsnum, fig.points,
						xPos, yPos, tiesize, j < len/2, strsize);
				}
				//thenote.drawRectSelection1(g, new Color(0.2, 0.2, 0.8, 0.3));
				j++;
			}
			collectRectangle();
			drawStem(g, xPos + strsize - 0.5, endpos-size/8, size, fig.beamsnum, strsize, true, endpos - initpos + (size*7)/8);
			/*if (fig.stem_p && (startgroup == len-1 || startgroup == -1) && stem_p) {
				if (up_p)
					drawStem(g, xPos + strsize - 0.5, endpos-size/8, size, fig.beamsnum, strsize, up_p, endpos - initpos + (size*7)/8);
				else drawStem(g, xPos + strsize + 0.5, initpos-size/8, size, fig.beamsnum, strsize, up_p, endpos - initpos + (size*7)/8);
			}*/
			x_0 = Math.min (x_0, x());
			y_0 = Math.min (y_0, y());
			setRectangle(x_0, y_0 , x()+w() - x_0, y()+h() - y_0);
			spaceX = spaceX + (size * 2) + strsize ;
		}
		if (startgroup != -1)
			drawUnite(g, params, startgroup, numgroup, deltax, size);

		//drawRectSelection1(g, new Color(0.8, 0.2, 0.2, 0.2));
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

	public void groupDrawStems(I_Render g, boolean up_p, double deltax, double ybarpos) {
		int size = params.fontsize.get();
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double stafftoppixels = staff.getTopPixel(size);
		double ystart ;
		double xstart;
		RNoteDrawable note;
		double strsize = ((Figure) figures.get(0)).getStrSize(size);
		List<I_Drawable> inside = getInside();
		if (up_p) {
			note = (RNoteDrawable) inside.get(0);
			ystart = stafftoppixels + (note.posY * size/8);
			xstart = centerX + deltax + strsize - 0.5;
			FX.omDrawLine(g, xstart, ystart, xstart, ybarpos);
		}
		else {
			note = (RNoteDrawable) inside.get(inside.size() - 1);
			ystart = stafftoppixels + (note.posY * size/8);;
			xstart = centerX + deltax + 0.5;
			FX.omDrawLine(g, xstart, ystart, xstart, ybarpos);
		}
	}

/*@Override
public I_Drawable getClickedObject(double x, double y) {
	SimpleDrawable rep = null;
	for (I_Drawable note : getInside()) {
		if (rep == null)
			rep = ((NoteDrawable) note).getClickedObject(x, y);
		}
	if (rep == null) {
		Rectangle rect = getRectangle();
		if (rect.contains((int) x,(int) y))
			rep = ref;
	}
	return rep;
}
*/

	///////////Spacing
	@Override
	public void computeCX(SpacedPacket pack, int size) {
		Figure fig = (Figure) figures.get(0);
		double x0 = pack.start;
		double deltal = 0;
		double deltar = 0;
		double strsize = fig.getStrSize(size);
		for (I_Drawable note : getInside()) {
			RNoteDrawable thenote = (RNoteDrawable) note;
			deltal = Math.max( deltal , (thenote.deltaAlt * strsize * 3) / 10 );
			deltar = Math.max( deltar ,  (thenote.deltaHead * strsize));
		}
		pack.updatePacket(0, 0, deltal, deltar+ strsize );
		setRectangle(x0, y(), deltal + deltar - x0 + strsize, h());
	}

	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {
		double tempo = ((RChord) ref).getTempo();
		timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), true));
		long onset = ref.getOnsetMS() + ((Figure) figures.get(0)).getDurMS(tempo);
		for (int i=1; i < figures.size(); i++){
			timelist.add(new SpacedPacket(figures.get(i),onset, true));
			onset = onset + ( (Figure) figures.get(i)).getDurMS(tempo);
		}
	}


	//////////////////////////////////////////////////
	////////////I_FigureDrawable
	@Override
	public int getBeamsNum() {
		return ((Figure) figures.get(0)).beamsnum;
	}

	@Override
	public double getHeadSize(int size) {
		return ((Figure)figures.get(0)).getStrSize(size);
	}

	@Override
	public double getUpYpos(int size) {
		double rep = 0;
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double stafftoppixels = staff.getTopPixel(size);
		for (I_Drawable note : getInside()) {
			RNoteDrawable thenote = (RNoteDrawable) note;
			rep = Math.max( rep , stafftoppixels + (thenote.posY * size/8));
		}
		return rep;
	}

	@Override
	public double getDwnYpos(int size) {
		double rep = 1000000;
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double stafftoppixels = staff.getTopPixel(size);
		for (I_Drawable note : getInside()) {
			RNoteDrawable thenote = (RNoteDrawable) note;
			rep = Math.min( rep , stafftoppixels + (thenote.posY * size/8));
		}
		return rep;
	}
   }
  ////////////// END DRAWABLE//////////////


}
