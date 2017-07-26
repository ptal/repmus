package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import javafx.geometry.Point2D;
import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.tools.ST;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Parallel_L_MO;
import projects.music.classes.music.Note.NoteDrawable;
import projects.music.classes.music.RNote.RNoteDrawable;
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

@Omclass(icon = "139", editorClass = "projects.music.classes.music.Chord$ChordEditor")
public class Chord extends Parallel_L_MO {
	
	@Omvariable
	public List<Integer> lmidic;
	@Omvariable
	public List<Integer> lvel;
	@Omvariable
	public List<Integer> lchan;
	@Omvariable
	public List<Long> loffset;
	@Omvariable
	public List<Long> ldur;
	
	Point2D ambitus;

	
	public Chord (int [] themidic, int [] thevel, long [] theoffset, long [] thedur, int [] thechan) {
		setOffset(0);
		initChord (ST.a2l (themidic), ST.a2l (thevel), ST.a2long (theoffset), ST.a2long (thedur), ST.a2l (thechan));
	}
		
	@Ombuilder(definputs="(6000); (80); (0); (1000); (1)")
	public Chord (List<Integer> themidic, List<Integer> thevel, List<Long> theoffset, List<Long> thedur, List<Integer> thechan) {
		setOffset(0);
		initChord(themidic, thevel, theoffset, thedur, thechan);
	}
	
	
	//@Ombuilder
	public Chord (List<MusicalObject> thenotes) {
		this.addElement(thenotes);
		setOffset(0);
		setSlots();
		setDurPar();
	}
	
	public Chord() {
		List<MusicalObject> thenotes = new ArrayList<MusicalObject> ();
		thenotes.add(new Note());
		this.addElement(thenotes);
		setOffset(0);
		setSlots();
		setDurPar();
	 }
	
	public void initChord (List<Integer> themidic, List<Integer> thevel, List<Long> theoffset, List<Long> thedur, List<Integer> thechan) {
		int sizevel = thevel.size();
		int sizedur = thedur.size();
		int sizechan = thechan.size();
		int sizeoffset = theoffset.size();
		long dur = 1000;
		int vel = 60;
		int chan = 0;
		long offset = 0;
		for (int i=0; i< themidic.size(); i++){
			if (i < sizevel) vel = thevel.get(i);
			if (i < sizedur) dur = thedur.get(i);
			if (i < sizechan) chan = thechan.get(i);
			if (i < sizeoffset) offset = theoffset.get(i);
			addElement(new Note(themidic.get(i), vel,dur,chan,offset));
		}
		setSlots();
		setDurPar();
	}
	
	public void setSlots () {
		int min = 128000;
		int max = 0;
		lmidic = new ArrayList<Integer>();
		loffset = new ArrayList<Long>();
		lvel = new ArrayList<Integer>();
		lchan = new ArrayList<Integer>();
		ldur = new ArrayList<Long>();
		for (MusicalObject note : getElements()) {
			min = Math.min(min, ((Note) note).midic);
			max = Math.max(max, ((Note) note).midic);
			lmidic.add(((Note) note).midic);
			lvel.add(((Note) note).vel);
			lchan.add(((Note) note).chan);
			ldur.add(((Note) note).getDuration());
			loffset.add(((Note) note).getOffset());
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
	
	public List<Long> getLDur () {
		return ldur;
	}
	
	public void setLDur (List<Long> durs) {
		ldur = durs;
	}

	public Point2D getAmbitus () {
		return ambitus;
	}
	
//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new ChordDrawable (this, params, 0, root);
	}

	//////////////////////EDITOR//////////////////////
    public static class ChordEditor extends MusicalEditor {
    	
    	
        @Override
    	public String getPanelClass (){
    		return "projects.music.classes.music.Chord$ChordPanel";
    	}
        
        @Override
    	public String getControlsClass (){
    		return "projects.music.classes.music.Chord$ChordControl";
    	}
        
        @Override
    	public String getTitleBarClass (){
    		return "projects.music.classes.music.Chord$ChordTitle";
    	}
        
    }
    
    //////////////////////PANEL//////////////////////
    public static class ChordPanel extends MusicalPanel {
    	
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
    public static class ChordControl extends MusicalControl {
    	
    }	
    
    //////////////////////TITLE//////////////////////
    public static class ChordTitle extends MusicalTitle {
    	
    }	
    
    //////////////////////DRAWABLE//////////////////////
    public static class ChordDrawable extends ContainerDrawable {

	boolean stem_p;
	boolean tied_p;
	String stemdir;

	final int headSizefactor = 4; 
	final int altSizefactor = 3; 
	public int staffnum = 0;

	public ChordDrawable (Chord theRef, MusicalParams params, int thestaffnum) {
		InitChordDrawable(theRef, params, thestaffnum);
	}
	
	public ChordDrawable (Chord theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		InitChordDrawable(theRef, params, thestaffnum);
	}
	
	public void InitChordDrawable (Chord theRef, MusicalParams theparams, int thestaffnum) {
		ref = theRef;
		params = theparams;
		staffnum = thestaffnum;
		StaffSystem staffSystem = params.getStaff();
		MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
		Scale scale = Scale.getScale (params.scale.get());
		stem_p = params.showstem.get();
		String chordmode = params.chordmode.get();
		List<MusicalObject> notes = theRef.getElements();
		List<MusicalObject> copie = ((List<MusicalObject>) ((ArrayList<MusicalObject>) notes).clone());
		if (editor_root) {
			if (chordmode.equals("arpUp") || chordmode.equals("chord"))
				Collections.sort(copie, pitchUp);
			else if (chordmode.equals("arpDown"))
				Collections.sort(copie, pitchDown);

			int size = params.fontsize.get();
			makeSpaceObjectList();
			consTimeSpaceCurve(size, 0, params.zoom.get());
		}
		int i = 0;
		int pos;
		int j;
		boolean found;
		List<Point2D> taked = new ArrayList<Point2D>();
		setStemDir(staff.getMidiCenter());
		for (MusicalObject note : copie) {
			NoteDrawable gnote = new NoteDrawable ((Note) note, params, staffnum);
	
		if (chordmode.equals("chord")) {
			gnote.centerX = 0;
			pos = staff.DemiDents(((Note) note).getMidic(),scale);
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
			}
		else if (chordmode.equals("onset")){
			gnote.centerX = ((Note) note).getOffset() * onesecond / 1000.0;
			}
		else {
			gnote.centerX = 1.5 * i;
			stem_p = false;
		}
		inside.add(gnote);
		gnote.setFather(this);
		i++;
	}
	if (chordmode.equals("chord")) setAltPositions(getInside(), staff, scale);
	makeGraphicExtras ();
	}

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
		midic = ((Note) ((NoteDrawable) note).ref).getMidic();
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
					((NoteDrawable) note).deltaAlt = i;
				}
				i++;
			}
		}
	}
}

public void setStemDir (double center) {
	Point2D amb = ((Chord) ref).getAmbitus();
	double moyenne = amb.getX() + ((amb.getY() - amb.getX())/2);
	if (moyenne < center * 100)
		stemdir = "up";
	else
		stemdir = "dw";
}

/////////////////////////////////////////////

public void drawObject(I_Render g, Rectangle rect, 
		 List<I_Drawable> selection, double x0, double deltax, double deltay) {
	int size = params.fontsize.get();
	double zoom = params.zoom.get()/100.0;
	double posx = getCX() * zoom;
	for (I_Drawable note : getInside()) {
		note.drawObject (g,rect, selection, posx + (((NoteDrawable) note).centerX * size), 
				deltax, deltay);
		}
	collectRectangle();
	if (stem_p) {
		drawStem(g, posx + deltax + size * 0.278, y() - size/8, w(), h() - (size / 6), size);
	}
	//drawRectSelection(g);
	drawExtras(g, deltax);
}

public void drawStem (I_Render g, double x, double y, double w, double h, int size) {
	double stemsize =  (size*3.5)/6;
	FX.omDrawLine (g, x, y+h, x, y - stemsize);
	setRectangle(x(), y() - stemsize, w(), h()  + stemsize);
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


	public  Comparator<MusicalObject> pitchUp = new Comparator<MusicalObject>() {
		public int compare(MusicalObject n1, MusicalObject n2) {
			return  ((Note) n1).midic - ((Note) n2).midic;
		}
	};

	public  Comparator<MusicalObject> pitchDown = new Comparator<MusicalObject>() {
		public int compare(MusicalObject n1, MusicalObject n2) {
			return  ((Note) n2).midic - ((Note) n1).midic;
		}
	};

	public  Comparator<I_Drawable> byHead = new Comparator<I_Drawable>() {
		public int compare(I_Drawable n1, I_Drawable n2) {
			return  ((NoteDrawable) n2).deltaHead - ((NoteDrawable) n1).deltaHead;
		}
	};

	@Override
	public void computeCX(SpacedPacket pack, int size) {
		double x0 = pack.start;
		double deltal = 0;
		double deltar = 0;
		double strsize = 7.176000118255615;
		for (I_Drawable note : getInside()) {
			NoteDrawable thenote = (NoteDrawable) note;
			deltal = Math.max( deltal , (thenote.deltaAlt * strsize * 3) / 10 );
			deltar = Math.max( deltar ,  (thenote.deltaHead * strsize));
		}
		pack.updatePacket(0, 0, deltal, deltar + strsize);
	} 
	
	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {
		timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), false));
	}


}

}

