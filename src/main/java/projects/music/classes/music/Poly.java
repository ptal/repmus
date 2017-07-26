package projects.music.classes.music;


import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
// import projects.constraints.AIS;
// import projects.constraints.Canons;
// import projects.constraints.Contrepoint21;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Parallel_S_MO;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.I_Drawable;
import projects.music.classes.music.RSeqChord.RSeqChordDrawable;
import projects.music.classes.music.Voice;
import projects.music.classes.music.Voice.VoiceDrawable;
import resources.json.JsonRead;


@Omclass(icon = "224", editorClass = "projects.music.classes.music.Poly$PolyEditor")
public class Poly extends Parallel_S_MO {

	@Ombuilder(definputs=" ( Voice ( ( (4/4 ( 1 1 1 1)) ) , ( RChord ((6000), (80), (0), 1/8, (1)) ) , 60) Voice ( ( (4/4 ( 1 1 1 1)) ) , ( RChord ((6000), (80), (0), 1/8, (1)) ) , 60) ) ")
	public Poly (List<MusicalObject> thevoices) {
		for (MusicalObject obj : thevoices)
			addElement(obj);
	}

	/*public Poly () {
		Poly poly = (Poly) JsonRead.getFile("/Users/agonc/Repmus-project/repmus-master/main/src/resources/json/poly.json");
		for (MusicalObject obj : poly.getElements())
			addElement(obj);
		addElement(new RSeqChord());
	}*/

	public Poly () {
		Voice v1 = (Voice) JsonRead.getFile("/Users/agonc/Repmus-project/repmus-master/main/src/resources/json/violon1.json");
		Voice v2 = (Voice) JsonRead.getFile("/Users/agonc/Repmus-project/repmus-master/main/src/resources/json/violon2.json");

		addElement(v1);
		addElement(v2);
	}

	public MusicalParams getParams() {
		MusicalParams params = new MusicalParams();
		params.setStaff ("[G G]");
		return params;
	}




//////////////////////////////////////////////////
public I_Drawable makeDrawable (MusicalParams params, boolean root) {
return new PolyDrawable (this, params, root);
}


//////////////////////EDITOR//////////////////////
public static class PolyEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.Poly$PolyPanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.Poly$PolyControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Poly$PolyTitle";
}

}

//////////////////////PANEL//////////////////////
public static class PolyPanel extends MusicalPanel {


//Canons csp = null;

	// Contrepoint21 csp = null;

public void KeyHandler(String car){
	switch (car) {
	case "h" : takeSnapShot ();
		break;
	case "c":
		setCSP();
		break;
	case "s":
		solveCSP();
		break;
	case "n":
		nextCSP();
		break;
	default : super.KeyHandler(car); break;
	}
}

	public void setCSP () {
		// csp = new Contrepoint21 (this);
		init();
	}


	public void solveCSP () {
		// if (csp != null) {
		// 	if (csp.solve())
		// 		init();
		// 	else csp = null;
		// }
	}

	public void nextCSP () {
		// if (csp != null) {
		// 	if (csp.next())
		// 		init();
		// 	else csp = null;
		// }
	}

	@Override
	public int getZeroPosition () {
		return 2;
	}
}


//////////////////////CONTROL//////////////////////
public static class PolyControl extends MusicalControl {

}

//////////////////////TITLE//////////////////////
public static class PolyTitle extends MusicalTitle {

}

//////////////////////DRAWABLE//////////////////////

public static class PolyDrawable extends ContainerDrawable {

public PolyDrawable (Poly theRef, MusicalParams params, boolean ed_root) {
		editor_root = ed_root;
		initPolyDrawable(theRef, params);
}

public PolyDrawable (Poly theRef, MusicalParams theparams) {
	initPolyDrawable(theRef, theparams);
}

public void initPolyDrawable (Poly theRef, MusicalParams theparams){
	ref = theRef;
	params = theparams;
	int i = 0;
	int size = params.fontsize.get();
	for (MusicalObject obj : theRef.getElements()) {
		if (obj instanceof Voice) {
			VoiceDrawable gvoice = new VoiceDrawable ((Voice) obj, params, i++);
			inside.add(gvoice);
			gvoice.setFather(this);
		}
		else if (obj instanceof RSeqChord) {
			RSeqChordDrawable gvoice = new RSeqChordDrawable ((RSeqChord) obj, params, i++);
			inside.add(gvoice);
			gvoice.setFather(this);
		}
	}
	if (editor_root) {
		makeSpaceObjectList();
		consTimeSpaceCurve(size, 0,params.zoom.get());
	}
  }

@Override
public void drawObject(I_Render g, Rectangle rect,
		 List<I_Drawable> selection, double packStart,
		double deltax, double deltay) {
	int size = params.fontsize.get();
	if (isRoot_p ()) {
		consTimeSpaceCurve(size, packStart, params.zoom.get()); // pourquoi ? a borrar
		for (SpacedPacket packed : timespacedlist) {
			for (I_Drawable obj : packed.objectlist ){
				obj.drawObject (g, rect, selection, packed.start, deltax, deltay);
			}
		}
	}
	drawContainersObjects(g, rect,  selection, deltax);
}

}

//////////////////////////////////////////////////////////////////////

}

