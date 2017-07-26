package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import javafx.geometry.Point2D;
import javafx.scene.text.Font;
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

@Omclass(icon = "226", editorClass = "projects.music.classes.music.RChord$RChordEditor")
public class Group extends Sequence_S_MO implements I_RT, I_InRSeqChord {

	@Ombuilder(definputs="(1/4 (1 1 (1 (1 1 1)) 1 1)) ; ( RChord((8000), (80), (0), 1/8, (1))"
			+ "RChord ((8000) , (80), (0), 1/4, (1) )"
			+ "RChord ((8000) , (80), (0), 1/4, (1) ) ) ; 60")
	public Group (RTree thetree, List<RChord> thechords, double tempo) {
		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		thetree.setDurChilds();
		setQDurs(tree.dur);
		fillGroup(chords.get(0), tempo);

		System.out.println ("en groupe sin last" + tree.dur);
	}

	public Group (RTree thetree, List<RChord> thechords, double tempo, RChord lastchord) {


		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		setQDurs(tree.dur);
		setRTdur(tree.dur);
		setRTproplist(tree.proplist);
		fillGroup(lastchord, tempo);
		System.out.println ("en groupe " + tree.dur + " " + qdur);
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
		setQDurs(objonset);
	}

	public Point2D getAmbitus () {
		List<MusicalObject> notes = new ArrayList<MusicalObject> ();
		getObjsOfClass(RNote.class, notes);
		double min = 12700;
		double max = 0;
		for (MusicalObject note : notes) {
			min = Math.min(min, ((RNote) note).getMidic());
			max = Math.max(max, ((RNote) note).getMidic());
		}
		return new Point2D(min,max);
	}

	//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new GroupDrawable (this, params, 0, root);
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
		String uniteStr = null;
		int uniteDepht = 0;

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
				Fraction dur = ((Strie_MO) obj).getQDurs();
				if (obj instanceof RChord) {
					//Fraction dur = ((RChord) obj).getQDurs();
					long newden= Strie_MO.findBeatSymbol(dur.denum);
					Fraction newdur = new Fraction(dur.num,newden);
					List<I_FigureDrawable> figures = new ArrayList<I_FigureDrawable> ();
					for (Figure item : Strie_MO.dur2symbols (newdur))
						figures.add(item);
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
			if (isRootandGroup_p ()) {
				setStemDirSize(staff.getMidiCenter());
				ybarpos = getYbarPos(size);
				setUniteStr();
			}
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
			up_p = (moyenne < (midicenter * 100));
		}

		public int setUniteStr () {
			int rep = 0;
			for (I_Drawable item : this.getInside()) {
				if (item instanceof GroupDrawable)
					rep = Math.max(rep, ((GroupDrawable) item).setUniteStr());
			}
			uniteStr = "3:4";
			if (uniteStr != null) {
				rep = rep + 1;
				uniteDepht = rep;
			}
			return rep;
		}

		public double getYbarPos (int size) {
			List<I_FigureDrawable> atomes = getAtomes();
			if (up_p){
				double rep = 1000000;
				for (I_FigureDrawable item : atomes) {
					double upYpos = item.getUpYpos(size);
					rep = Math.min(rep, upYpos - item.getStemSize(item.getBeamsNum(),  size));
				}
			 	return  rep;
			} else{
				double rep = 0;
				for (I_FigureDrawable item : atomes) {
					double dwnYpos = item.getDwnYpos(size);
					rep = Math.max(rep, dwnYpos + item.getStemSize(item.getBeamsNum(), size));
				}
			 	return rep;
			}
		}

	public void drawContainersObjects (I_Render g, Rectangle rect,
			 List<I_Drawable> selection, double deltax) {
		for (I_Drawable item : this.getInside()) {
			if (item instanceof RChordDrawable)
				((RChordDrawable) item).drawContainersObjects(g, rect, selection, deltax);
			else if (item instanceof RestDrawable)
				((RestDrawable) item).drawContainersObjects(g, rect,selection, deltax);
			else if (item instanceof GroupDrawable)
				((GroupDrawable) item).drawContainersObjects(g, rect, selection, deltax);
		}
		int size = params.fontsize.get();
		if (isRootandGroup_p ())  {
			groupDrawStems(g, up_p, deltax, ybarpos);
			drawBeams(g, up_p, size, deltax, getAtomes(), ybarpos, 1 + (size * 3/16));
			drawNumDens(g, up_p, size, deltax);
		}
	}


	public void drawNumDen (I_Render g, boolean up_p, int size, double deltax) {
		List<I_FigureDrawable> atomes = getAtomes();
		I_FigureDrawable fig1 = atomes.get(0);
		I_FigureDrawable fig2 = atomes.get(atomes.size()-1);
		Font thefont = params.getFont("normal2.3Size");
		double ssize = FX.omStringSize(uniteStr,  thefont);
		FX.omSetFont(g, thefont);
		if (up_p) {
			double y0 = ybarpos - size/16 - (size/3 * uniteDepht);
			double x0 = fig1.getCX() + deltax;
			//double x1 = fig2.getCX()+deltax + fig2.getStrSize(size) +size/4;
			//double x1 = fig2.getCX()+deltax + 7.176000118255615 +size/4;
			double x1 = fig2.getCX()+deltax  +size/4;
			FX.omDrawLine(g, x0, y0, x0 +(x1-x0-ssize-4)/2 , y0);
			FX.omDrawLine(g, x0 + (x1-x0+ssize+4)/2, y0, x1 , y0);
			FX.omDrawLine(g, x0, y0, x0 , y0 + size/5);
			FX.omDrawLine(g, x1, y0, x1 , y0 + size/5);
			FX.omDrawString(g, x0 + (x1-x0-ssize)/2, y0 + size/16, uniteStr);
		}
		else {
			double y0 = ybarpos + size/10 + (size/3 * uniteDepht);
			double x0 = fig1.getCX() + deltax - size/4;
			//double x1 = fig2.getCX()+deltax + fig2.getStrSize(size) ;
			double x1 = fig2.getCX() + deltax + 7.176000118255615 ;
			FX.omDrawLine(g, x0, y0, x0 +(x1-x0-ssize-4)/2 , y0);
			FX.omDrawLine(g, x0 + (x1-x0+ssize+4)/2, y0, x1 , y0);
			FX.omDrawLine(g, x0, y0, x0 , y0 - size/5);
			FX.omDrawLine(g, x1, y0, x1 , y0 - size/5);
			FX.omDrawString(g, x0 + (x1-x0-ssize)/2, y0 + size/16, uniteStr);
		}
	}

	public void drawNumDens(I_Render g, boolean up_p, int size, double deltax) {
		if (uniteStr != null)
			drawNumDen (g, up_p, size, deltax);
		for (I_Drawable item : this.getInside()) {
			if (item instanceof GroupDrawable)
				((GroupDrawable) item).drawNumDen(g, up_p, size, deltax );
		}
	}

	public void groupDrawStems(I_Render g, boolean up_p, double deltax, double ybarpos) {
		for (I_Drawable item : this.getInside()) {
			if (item instanceof RChordDrawable)
				((RChordDrawable) item).groupDrawStems(g, up_p, deltax, ybarpos);
			else if (item instanceof RestDrawable)
				((RestDrawable) item).groupDrawStems(g, up_p, deltax, ybarpos);
			else if (item instanceof GroupDrawable)
				((GroupDrawable) item).groupDrawStems(g, up_p, deltax, ybarpos);
		}
	}


	public List<I_FigureDrawable> getAtomes (){
		List<I_FigureDrawable> rep = new ArrayList<I_FigureDrawable> ();
		for (I_Drawable obj : getInside())
			if (obj instanceof RChordDrawable || obj instanceof RestDrawable)
				rep.add((I_FigureDrawable) obj);
			else if (obj instanceof GroupDrawable)
				rep.addAll(((GroupDrawable) obj).getAtomes());
		return rep;
	}
}

//////////////////////////////////////////////////////////////////////

}
