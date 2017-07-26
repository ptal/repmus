package projects.music.classes.music;


import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.text.Font;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.tools.Fraction;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.RTree;
import projects.music.classes.abstracts.Sequence_S_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.RNote.RNoteDrawable;
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
import projects.music.editors.drawables.I_Drawable;


@Omclass(icon = "228", editorClass = "projects.music.classes.music.Measure$MeasureEditor")
public class Measure extends Sequence_S_MO implements I_RT {

	@Ombuilder(definputs="(4/4 (2 (1 (1 1 1)) -1)) ; ( RChord((6000), (80), (0), 1/8, (1)) ) ; 60")
	public Measure (RTree thetree, List<RChord> thechords, double tempo) {

		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		thetree.setDurChilds();
		setQDurs(tree.dur);
		fillMeasure(chords.get(0), tempo);
		System.out.println ("en measure " + tree.dur);
	}

	public Measure (RTree thetree, List<RChord> thechords, double tempo, RChord lastchord) {
		tree = thetree;
		chords = thechords;
		setTempo(tempo);
		setRTdur(tree.dur);
		System.out.println ("en measure avec last 1 " + qdur);
		setRTproplist(tree.proplist);
		System.out.println ("en measure avec last 2 " + qdur);
		fillMeasure(lastchord, tempo);
		System.out.println ("en measure avec last 3 " + qdur + " tree dur " + tree.dur);
	}

	public Measure () {
		List<RTree> group = new ArrayList<RTree>();
		group.add(new RTree(1,null));
		group.add(new RTree(-1,null));
		group.add(new RTree(1,null));
		RTree gt = new RTree(1,group);

		List<RTree> prop = new ArrayList<RTree>();
		prop.add(new RTree(1,null));
		prop.add(new RTree(-1,null));
		prop.add(gt);
		prop.add(new RTree(1,null));
		tree = new RTree(new Fraction (2,4),prop);
		chords = new ArrayList<RChord>();
		chords.add(new RChord());
		setTempo(60);
		setRTdur(tree.dur);
		setRTproplist(tree.proplist);
		fillMeasure(chords.get(0), 60);
	}

	public void fillMeasure (RChord lastchord, double tempo) {
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
						//newchord = new RChord (item.dur, chords.get(i++), tempo, item.cont_p);
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

	public Fraction getSignature () {
		return tree.getSignature();
	}

//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new MeasureDrawable (this, params, 0, root);
	}


//////////////////////EDITOR//////////////////////
public static class MeasureEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.Measure$MeasurePanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.Measure$MeasureControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Measure$MeasureTitle";
}

}

//////////////////////PANEL//////////////////////
public static class MeasurePanel extends MusicalPanel {

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
public static class MeasureControl extends MusicalControl {

}

//////////////////////TITLE//////////////////////
public static class MeasureTitle extends MusicalTitle {

}

//////////////////////DRAWABLE//////////////////////

	public static class MeasureDrawable extends ContainerDrawable {

	int staffnum = 0;

	public MeasureDrawable (Measure theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		initMeasureDrawable(theRef, params, thestaffnum);
	}
	public MeasureDrawable (Measure theRef, MusicalParams params, int thestaffnum) {
		initMeasureDrawable(theRef,  params, thestaffnum);
	}

	public void initMeasureDrawable (Measure theRef, MusicalParams theparams, int thestaffnum){
		ref = theRef;
		staffnum = thestaffnum;
		params = theparams;
		int size = params.fontsize.get();
		for (MusicalObject obj : theRef.getElements()) {
			if (obj instanceof RChord) {
				RChordDrawable gchord = new RChordDrawable ((RChord) obj, params, staffnum);
				gchord.stem_p = true;
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
	}

	@Override
	public void drawObject(I_Render g, Rectangle rect,
			 List<I_Drawable> selection, double packStart,
			double deltax, double deltay) {
		if (isRoot_p ())
			super.drawObject(g, rect, selection, packStart, deltax, deltay);
		int size = params.fontsize.get();
		StaffSystem staffsys = params.getStaff();
		MultipleStaff staff = staffsys.getStaffs().get(staffnum);
		double staffToppixels = staff.getTopPixel(size);
		double staffBottompixels = staff.getBottomPixel(size);
		MeasureDrawable previous = (MeasureDrawable) prevBrother();
		double xPos =  packStart + deltax;
		if (previous != null) {
			FX.omDrawLine(g, xPos, staffToppixels, xPos, staffBottompixels);
			Measure prevmes = (Measure) previous.ref;
			Measure curmes = (Measure) this.ref;
			long nume = curmes.getSignature ().getNumerator();
			long denom = curmes.getSignature ().getDenominator();
			if  (prevmes.getSignature ().getDenominator() != denom ||
					prevmes.getSignature ().getNumerator() != nume) {
				Font oldfont = FX.omGetFont(g);
				Font thefont = params.getFont("singSize");
				FX.omSetFont(g, thefont);
				FX.omDrawString(g, xPos + size/4 , staffToppixels + size/4, nume+"");
				FX.omDrawString(g, xPos + size/4 , staffToppixels + size/2 + + size/4, denom+"");
				FX.omSetFont(g, oldfont);
			}
		}
		else {
			Measure curmes = (Measure) this.ref;
			long nume = curmes.getSignature ().getNumerator();
			long denom = curmes.getSignature ().getDenominator();
			Font oldfont = FX.omGetFont(g);
			Font thefont = params.getFont("singSize");
			FX.omSetFont(g, thefont);
			FX.omDrawString(g, xPos + size/4 , staffToppixels + size/4, nume+"");
			FX.omDrawString(g, xPos + size/4 , staffToppixels + size/2 + + size/4, denom+"");
			FX.omSetFont(g, oldfont);
		}
	}

	///////////Spacing
	@Override
	public void computeCX(SpacedPacket pack, int size) {
		pack.updatePacket(size, 0, 0, 0);
		//setRectangle(x0, y(), deltal + deltar - x0 + strsize, h());
	}

	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {
		timelist.add(new SpacedPacket(this, ref.getOnsetMS(), true));
		for (I_Drawable obj : getInside()) {
			obj.collectTemporalObjects(timelist);
		}
	}


}
//////////////////////////////////////////////////////////////////////

}

