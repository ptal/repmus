package projects.music.classes.music;


import gui.FX;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.text.Font;
import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.frames.views.I_EditorParams;

import com.sun.javafx.geom.Rectangle;

import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Sequence_L_MO;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;
import projects.music.editors.drawables.StaffBound;


@Omclass(icon = "224", editorClass = "projects.music.classes.music.Sequence$SequenceEditor")
public class Sequence extends Sequence_L_MO {
	
	@Ombuilder(definputs=" ( Voice ( ( (4/4 ( 1 1 1 1)) ) , ( RChord ((6000), (80), (0), 1/8, (1)) ) , 60) "
			+ "Silence ( 3000 ) "
			+ "ChordSeq ( ((6000) (6100)), (0 3000), ((1000)), ((80)), ((0)), ((1)), 100 ) )")
	public Sequence (List<MusicalObject> theelems) {
		long onset = 0;
		for (MusicalObject obj : theelems)  {
			addElement(obj);
			obj.setOffset(onset);
			onset = onset + obj.getDuration();
		}
		setDurSeq();
	}
	
	public Sequence () {
		List<MusicalObject> elems = new ArrayList<MusicalObject> ();
		elems.add(new RNote());
		
		// elems.add(new Note());
		elems.add(new Chord());
		elems.add(new MidiFile());
		elems.add(new RChord());
		//elems.add(new ChordSeq());
		//elems.add(new MultiSeq());
		//elems.add(new Voice());
		//elems.add(new Poly());
		/*elems.add(new RSeqChord());
		elems.add(new SeqChord());
		elems.add(new Parallel());
		elems.add(new MultiSeq());
		;*/
		long onset = 0;
		for (MusicalObject obj : elems)  {
			addElement(obj);
			obj.setOffset(onset);
			onset = onset + obj.getDuration();
		}
		setDurSeq();
	}
	
	
	public MusicalParams getParams() {
		MusicalParams params = new MusicalParams();
		return params;
	}
	
	public  void drawPreview (I_Render g, MusicalPanel canvas, double x, double x1, double y, double y1, I_EditorParams edparams) {
		/*MusicalParams params = (MusicalParams) edparams;
		int size = params.fontsize.get();
		g.omSetFont(params.getFont("headSize"));
		List<I_Drawable> selection = new ArrayList<I_Drawable> ();
		StaffSystem staffSystem = params.getStaff();
		staffSystem.setMarges(0, 1);
		boolean tempo = params.showtempo.get();
		Font oldFont = g.omGetFont();
		g.omSetFont(params.getFont("singSize"));
		staffSystem.draw(g, 0, 0, (x1 -x) , size/4, tempo, false, size);
		g.omSetFont(oldFont);
		double deltax = (staffSystem.getXmarge() + 2)*size; //double deltax = this.getZeroPosition()*size;
		double deltay = staffSystem.getYmarge(); //double deltax = this.getZeroPosition()*size;
		Rectangle rect = new Rectangle ((int) x, (int) y ,(int) (x1 -x) ,(int) (y1 - y));
		I_Drawable graphObj = makeDrawable(params);
		graphObj.drawObject (g,  canvas, rect, params, staffSystem, selection, 0 , deltax, deltay);*/
	}

	
//////////////////////////////////////////////////
public I_Drawable makeDrawable (MusicalParams params, boolean root) {
return new SequenceDrawable (this, params, root);
}


//////////////////////EDITOR//////////////////////
public static class SequenceEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.Sequence$SequencePanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.Sequence$SequenceControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Sequence$SequenceTitle";
}

}

//////////////////////PANEL//////////////////////
public static class SequencePanel extends MusicalPanel {
	
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
	
	public void drawLineSystem (I_Render g, MusicalParams params) {
		
	}


}	

//////////////////////CONTROL//////////////////////
public static class SequenceControl extends MusicalControl {

}	

//////////////////////TITLE//////////////////////
public static class SequenceTitle extends MusicalTitle {

}	

//////////////////////DRAWABLE//////////////////////

public static class SequenceDrawable extends ContainerDrawable {
	List<MusicalParams> paramslist = new ArrayList<MusicalParams> ();
	
public SequenceDrawable (Sequence theRef, MusicalParams params) {
	ref = theRef;
	initSequenceDrawable(theRef,  params);
}	

public SequenceDrawable (Sequence theRef, MusicalParams params, boolean ed_root) {
	editor_root = ed_root;
	initSequenceDrawable(theRef,  params);
}

public void initSequenceDrawable (Sequence theRef, MusicalParams theparams){
	params = theparams;
	ref = theRef;
	int size = params.fontsize.get();
	for (MusicalObject obj : theRef.getElements()) {
		MusicalParams param = obj.getParams();
		param.getStaff().setMarges(1, 4);
		paramslist.add(param);
		I_Drawable drawable = obj.makeDrawable(param, false); 
		inside.add(drawable);
		drawable.setFather(this);
	}	
	if (isRoot_p ()) {
		makeSpaceObjectList();
		consTimeSpaceCurve(size, 0, params.zoom.get());
	}
  }	

public void drawLineSystemInRect (I_Render g, MusicalParams params, double x, double y, double w, double h) {
	StaffSystem staffSystem = params.getStaff();
	boolean tempo = false;
	int size = params.fontsize.get();
	Font oldFont = g.omGetFont();
	g.omSetFont(params.getFont("singSize"));
	staffSystem.draw(g, x, y, w , size/4, tempo, false, size);
	g.omSetFont(oldFont);
}

@Override
public void drawObject(I_Render g,  Rectangle rect,
		List<I_Drawable> selection, double packStart, 
		double deltax, double deltay) {
	if (isRoot_p ()) {
		for (SpacedPacket packed : timespacedlist) {
			for (I_Drawable obj : packed.objectlist ){
				obj.drawObject (g, rect, selection, packed.start, deltax, deltay);
			}
		}
	}
	drawContainersObjects(g,  rect, selection, deltax);
	int size = params.fontsize.get();
	Font thefont = params.getFont("normal2.3Size");
	Font oldfont = FX.omGetFont(g);
	FX.omSetFont(g, thefont);
	for (int i = 0; i < 20000; i = i+ 1000) {
		double x = deltax + time2pixel(i, size);
		FX.omDrawLine(g, x, 0, x, 50);
		FX.omDrawString(g, x, 50, Math.round(x - deltax)+"");
		FX.omDrawString(g, x, 60, Math.round(interpole(i))+"");
		FX.omDrawString(g, x, 70, i+"");
	}
	FX.omSetFont(g, oldfont);
}

@Override
public void collectTemporalObjects(List<SpacedPacket> timelist) {
	int i = 0;
	for (I_Drawable obj : getInside()) {
		StaffBound ss = new StaffBound(paramslist.get(i), ((SimpleDrawable) obj).ref, false);
		StaffBound es  = new StaffBound(paramslist.get(i), ((SimpleDrawable) obj).ref, true);
		timelist.add(new SpacedPacket(ss, obj.getRef().getOnsetMS(), true));
		timelist.add(new SpacedPacket(es, obj.getRef().getOnsetMS() + obj.getRef().getDuration(), true));
		obj.collectTemporalObjects(timelist);
		i++;
	}
	}
}

}

//////////////////////////////////////////////////////////////////////


