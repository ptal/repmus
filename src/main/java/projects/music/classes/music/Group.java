package projects.music.classes.music;

import gui.FX;
import gui.FXCanvas;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import javafx.geometry.Point2D;
import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.tools.Fraction;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.RTree;
import projects.music.classes.abstracts.Sequence_S_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_InRSeqChord;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.Rest.RestDrawable;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.Figure;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.I_FigureDrawable;
import projects.music.editors.drawables.SimpleDrawable;

@Omclass(icon = "226", editorClass = "projects.music.classes.music.RChord$RChordEditor")
public class Group extends Sequence_S_MO implements I_RT, I_InRSeqChord {
	
	@Ombuilder(definputs="(1 (1 1 1)) ; ( RChord((6000), (80), (0), 1/8, (1))"
			+ "RChord ((6500) , (80), (0), 1/4, (1) )"
			+ "RChord ((7200) , (80), (0), 1/4, (1) ) ) ; 60")
	public Group (RTree thetree, List<RChord> thechords, double tempo) {
		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		thetree.setDurChilds();
		setQDurs(tree.dur);
		fillGroup(chords.get(0), tempo);
	}
	
	public Group (RTree thetree, List<RChord> thechords, double tempo, RChord lastchord) {
		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		setRTdur(tree.dur);
		setRTproplist(tree.proplist);
		fillGroup(lastchord, tempo);
	}
	
	public Group () {
		List<RTree> prop = new ArrayList<RTree>();
		prop.add(new RTree(1,null));
		prop.add(new RTree(1,null));
		prop.add(new RTree(1,null));
		tree = new RTree(new Fraction (1),prop);
		chords = new ArrayList<RChord>();
		chords.add(new RChord());
		setTempo(60);
		setRTdur(tree.dur);
		setRTproplist(tree.proplist);
		fillGroup(chords.get(0), 60);
	}
	
	public void fillGroup (RChord lastchord, double tempo) {
		Fraction objonset = new Fraction(0);
		Fraction objdur;
		for (RTree item : tree.getProporsitons()) {
			int hmchords = item.countChords ();
			if (item.isFigure()) {
				if (item.isRest()) {
					Rest newrest = new Rest ();
					newrest.setTempo(tempo);
					newrest.setQoffset(objonset);
					objdur = item.dur.abs();
					newrest.setQDurs(objdur);
					addElement(newrest);
				}
				else {
					RChord newchord;
					if (item.cont_p)
						newchord = new RChord (item.dur, lastchord, tempo, item.cont_p);
					else
						newchord = new RChord (item.dur, chords.get(0), tempo, item.cont_p);
					newchord.setQoffset(objonset);
					objdur = item.dur;
					addElement(newchord);
				}
			}
			else {
				if (item.isGrace()) {
					objdur = new Fraction(0);
				}
				else {
					Group newgroup = new Group (item , chords, tempo, lastchord);
					newgroup.setQoffset(objonset);
					objdur = item.dur;
					addElement(newgroup);
				}
			}
			objonset = Fraction.addition (objonset, objdur.abs());
			if (hmchords > 0) {
				nextChords(hmchords, lastchord);
				lastchord = chords.get(0);
			}
		}
	}
	
	/*(defmethod get-group-ratio ((self group))
			   (let* ((tree (tree self))
			          (extent (car tree))
			          (addition (loop for item in (second tree) sum (floor (abs (if (listp item) (car item) item))))))
			     (cond
			      ((= (round (abs addition)) 1) nil)
			      ((integerp (/ extent addition)) addition)
			      ;; never happen
			      ((and (integerp (/ extent addition)) 
			             (or (power-of-two-p (/ extent addition))
			                 (and (integerp (/ addition extent)) 
			                      (power-of-two-p (/ addition extent)))))  nil)
			      (t addition))))
			      */
	
	public Point2D getAmbitus () {
		List<MusicalObject> notes = new ArrayList<MusicalObject> ();
		getObjsOfClass(RNote.class, notes);
		double min = 6000;
		double max = 6000;
		for (MusicalObject note : notes) {
			min = Math.min(min, ((RNote) note).getMidic());
			max = Math.max(max, ((RNote) note).getMidic());
		}
		return new Point2D(min,max);
	}		     
	
	//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params) {
		return new GroupDrawable (this, params, 0, true);
	}
	
	//////////////////////EDITOR//////////////////////
	public static class GroupEditor extends MusicalEditor {
		
		@Override
		public String getPanelClass (){
			return "projects.music.classes.music.Group$GroupPanel";
		}

		@Override
		public String getControlsClass (){
			return "projects.music.classes.music.Group$GroupControl";
		}

		@Override
		public String getTitleBarClass (){
			return "projects.music.classes.music.Group$GroupTitle";
		}

	}

	//////////////////////PANEL//////////////////////
	public static class GroupPanel extends MusicalPanel {
		
		@Override
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
	public static class GroupControl extends MusicalControl {

	}	

	//////////////////////TITLE//////////////////////
	public static class GroupTitle extends MusicalTitle {

	}	

	//////////////////////DRAWABLE//////////////////////
	public static class GroupDrawable extends ContainerDrawable {
		Fraction numdenom;
		int chiflevel;
		boolean up_p = true;
		double ybarpos;
		double interBeamSpace;
		int staffnum = 0;

		public GroupDrawable (Group theRef, MusicalParams theparams, int thestaffnum) {
			initGroupDrawable(theRef, theparams, thestaffnum);
		}	
	
		public GroupDrawable (Group theRef, MusicalParams theparams, int thestaffnum, boolean ed_root) {
			editor_root = ed_root;
			initGroupDrawable(theRef, theparams, thestaffnum);
		}
	
		public void initGroupDrawable (Group theRef, MusicalParams theparams, int thestaffnum){
			ref = theRef;
			params = theparams;
			staffnum = thestaffnum;
			int size = params.fontsize.get();
			interBeamSpace = 1 + (size * 3/16);
			for (MusicalObject obj : theRef.getElements()) {
				if (obj instanceof RChord) {
					Fraction dur = ((RChord) obj).getQDurs();
					long newden= Strie_MO.findBeatSymbol(dur.denum);
					Fraction newdur = new Fraction(dur.num,newden);
					List<Figure> figures = Strie_MO.dur2symbols (newdur);
					RChordDrawable gchord = new RChordDrawable ((RChord) obj, params, staffnum, figures);
					gchord.stem_p = false;
					inside.add(gchord);
					gchord.setFather(this);
				}
				else if (obj instanceof Rest)  {
					RestDrawable grest = new RestDrawable ((Rest) obj, params, staffnum);
					inside.add(grest);
					grest.setFather(this);
				}
				else if (obj instanceof Group)  {
					GroupDrawable ggroup = new GroupDrawable ((Group) obj, params, staffnum);
					inside.add(ggroup);
					ggroup.setFather(this);
				}
			}
			if (editor_root) {
				makeSpaceObjectList();
				consTimeSpaceCurve(size, 0, params.zoom.get());
			}
			StaffSystem staffSystem = params.getStaff();
			MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
			if (isRootandGroup_p ()) 
				setStemDirSize(staff.getMidiCenter());
		}	
	
		public boolean isRootandGroup_p () {
			if (this.getFather() == null) 
				return true;
			else
				return ! (this.getFather() instanceof  GroupDrawable);
		}
		
		
		public void setStemDirSize (double midicenter) {
			Point2D amb = ((Group) ref).getAmbitus();
			double moyenne = amb.getX() + ((amb.getY() - amb.getX())/2);
			up_p = (moyenne < midicenter * 100);
			ybarpos = getYbarPos();
		}
	
		public double getYbarPos () {
	/*		if (up_p){
			 min posY + stemsize
			}
			else{
				max posY + stemsize
			}*/
			return 0.0;
		}
			
			
	public void drawContainersObjects (I_Render g, FXCanvas panel, Rectangle rect,
			 List<I_Drawable> selection, double deltax) {
		for (I_Drawable item : this.getInside()) {
			if (item instanceof RChordDrawable)
				((RChordDrawable) item).drawContainersObjects(g, panel, rect, selection, deltax);
			else if (item instanceof RestDrawable)
				((RestDrawable) item).drawContainersObjects(g, panel, rect,selection, deltax);
			else if (item instanceof GroupDrawable)
				((GroupDrawable) item).drawContainersObjects(g, panel, rect, selection, deltax);
		}
		int size = params.fontsize.get();
		if (isRootandGroup_p ())  {
			groupDrawStems(g, up_p, size, deltax);
			drawBeams(g, size, deltax);
			drawNumDen();
		}
	}
	
	public void groupDrawStems(I_Render g, boolean up_p, double size, double deltax) {
		for (I_Drawable item : this.getInside()) {
			if (item instanceof RChordDrawable)
				((RChordDrawable) item).groupDrawStems(g, up_p, size, deltax);
			else if (item instanceof RestDrawable)
				((RestDrawable) item).groupDrawStems(g, up_p, size, deltax);
			else if (item instanceof GroupDrawable)
				((GroupDrawable) item).groupDrawStems(g, up_p, size, deltax);
		}
	}
	
	public void drawBeams(I_Render g, int size, double deltax) {
		List <I_Drawable> atomes =  getAtomes();
		int len = atomes.size();
		I_FigureDrawable cur;
		I_FigureDrawable next;
		I_FigureDrawable prev;
		for (int i = 0; i < len; i++) {
			int  shared;
			int propres;
			cur = (I_FigureDrawable) atomes.get(i);
			if (i+1 == len ) next = null; else next = (I_FigureDrawable) atomes.get(i+1);
			if (i == 0 ) prev = null; else prev = (I_FigureDrawable) atomes.get(i-1);
			
			if (i == 0 || ((SimpleDrawable) cur).firstOfChildren()) {
				shared = getSharedWithNext(cur,next);
				propres = cur.getBeamsNum() - shared;
				if (next != null)
					drawLongBeams (g,cur, shared, next.getCX() - cur.getCX(),size, deltax) ;
				drawCourtBeams (g, cur, propres, size, shared);
			} 
			else if (i+1 == len ) {
				if (prev != null)
					shared = getSharedWithNext(prev,cur);
				else shared = 0;
				propres = cur.getBeamsNum() - shared;
				drawCourtBeams (g, cur, propres, size, shared);
			}
			else {
				if (((SimpleDrawable) cur).lastOfChildren() || ((SimpleDrawable) next).firstOfChildren()) {
					if (prev != null)
						shared = getSharedWithNext(prev,cur);
					else shared = 0;
					propres = cur.getBeamsNum() - shared;
					drawCourtBeams (g, cur, propres, size, shared);
					shared = Math.min(1,Math.min(next.getBeamsNum(),cur.getBeamsNum()));
					drawLongBeams (g, cur, shared,  next.getCX() - cur.getCX(),size, deltax);
				}
				else {
					if (next != null)
						shared = getSharedWithNext(cur,next);
					else shared = 0;
					propres = cur.getBeamsNum() - shared;
					drawLongBeams (g, cur, shared,  next.getCX() - cur.getCX(),size, deltax);
					if (next.getBeamsNum() <= prev.getBeamsNum() && ! ((SimpleDrawable) prev).lastOfChildren())
						drawCourtBeams (g, cur, propres, size, shared);
					else drawCourtBeams (g, cur, propres, size, shared);
				}
			}
		}
	}

    public void drawBeam (I_Render g, double x, double y, double w, double h) {
    	FX.omFillRect(g, x, y, w, h);
    }
    
    public void drawCourtBeams (I_Render g, I_FigureDrawable cur, int n, int size, int shared) {
    	double ygroup;
    	double xpos;
    	if (up_p) {
    		ygroup = y() + (n * interBeamSpace);
    		xpos = cur.getCX() + cur.getHeadSize();
    	}
    	else {
    		ygroup = y() - h() - (n * interBeamSpace);
    		xpos = cur.getCX();
    	}
    	for (int i = 0; i < n; i++) {
    	if (up_p) {
    		drawBeam(g, xpos, ygroup+(i*interBeamSpace), size*1/4, size*1/8);
    	}
    	else {
    		drawBeam(g, xpos, ygroup-(i*interBeamSpace), size*1/4, size*1/8);
    	}
    	}
    }
    
    public void drawLongBeams (I_Render g, I_FigureDrawable cur, int n, double sizex, int size, double deltax ) {
    	double ygroup;
    	double xpos;
    	if (up_p) {
    		ygroup = y() + (n * interBeamSpace);
    		xpos = cur.getCX() + cur.getHeadSize() +deltax;
    	}
    	else {
    		ygroup = y() - h() - (n * interBeamSpace);
    		xpos = cur.getCX() +deltax;
    	}
    	for (int i = 0; i < n; i++) {
    	if (up_p) {
    		drawBeam(g, xpos, ygroup+(i*interBeamSpace), sizex, size*1/8);
    	}
    	else {
    		drawBeam(g, xpos, ygroup-(i*interBeamSpace), sizex, size*1/8);
    	}
    }
   }
                        
    /*                     
                         
                         
               (defun drawNlong-beams (self n dir x sizex y rect zoom size)
   (let* ((ygroup (+ y (if (string-equal dir "up") (second rect) (fourth rect) )))
          (xpos (if (string-equal dir "up")
                  (round (+  x (/ size 3.5) (* zoom (x self))))
                  (round (+  x  (* zoom (x self))))))
          (spacesize (inter-beam-space size)))
     (loop for i from 0 to (- n 1) do
           (if  (string-equal dir "up")
             (draw-beam    xpos (+ ygroup (* spacesize i))
                           sizex (round size 8) (selected self))
             (draw-beam  xpos (- ygroup (* spacesize i))
                         sizex (round size 8) (selected self))))))
*/
               
	public int getSharedWithNext (I_FigureDrawable prev, I_FigureDrawable cur) {
			if (((SimpleDrawable) prev).lastOfChildren() || ((SimpleDrawable) cur).firstOfChildren()) 
				return Math.min(1,Math.min(prev.getBeamsNum(),  cur.getBeamsNum()));
			else
				return Math.min(prev.getBeamsNum(), cur.getBeamsNum());
	}

	public void drawNumDen() {
	
	}
			
	public List<I_Drawable> getAtomes (){
		List<I_Drawable> rep = new ArrayList<I_Drawable> ();
		for (I_Drawable obj : getInside()) 
			if (obj instanceof RChordDrawable || obj instanceof RestDrawable) 
				rep.add(obj);
			else if (obj instanceof GroupDrawable)  
				rep.addAll(((GroupDrawable) obj).getAtomes());
		return rep;
	}
	
	///////////Spacing
	@Override
	public double computeCX(SpacedPacket pack, int size) {
		return 0;
	}
}

//////////////////////////////////////////////////////////////////////
		
}
