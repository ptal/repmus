package projects.music.classes.music;

import java.util.ArrayList;
import java.util.List;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Parallel_L_MO;
import projects.music.classes.music.ChordSeq.ChordSeqDrawable;
import projects.music.classes.music.SeqChord.SeqChordDrawable;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.I_Drawable;

	@Omclass(icon = "224", editorClass = "projects.music.classes.music.MultiSeq$MultiSeqEditor")
	public class MultiSeq extends Parallel_L_MO {
		
		@Ombuilder(definputs=" (ChordSeq ( ((6000) (6100)), (0 3000), ((1000)), ((80)), ((0)), ((1)), 100 ) " +
				"SeqChord ( ((6000) (6000) (6000) (6000)), ( 500 -500 1000 -1000), ((80)), ((0)), ((1)) )  )")
		public MultiSeq (List<MusicalObject> thevoices) {
			for (MusicalObject obj : thevoices) 
				addElement(obj);
		}
		
		public MultiSeq () {
			List<MusicalObject> voices = new ArrayList<MusicalObject> ();
			voices.add(new ChordSeq());
			voices.add(new SeqChord());
			for (MusicalObject obj : voices) 
				addElement(obj);
		}
	
		public MusicalParams getParams() {
			MusicalParams params = new MusicalParams();
			params.setStaff ("[G {G F}]");
			return params;
		}
		
	//////////////////////////////////////////////////
	public I_Drawable makeDrawable (MusicalParams params) {
	return new MultiSeqDrawable (this, params, true);
	}

	//////////////////////EDITOR//////////////////////
	public static class MultiSeqEditor extends MusicalEditor {


	@Override
	public String getPanelClass (){
	return "projects.music.classes.music.MultiSeq$MultiSeqPanel";
	}

	@Override
	public String getControlsClass (){
	return "projects.music.classes.music.MultiSeq$MultiSeqControl";
	}

	@Override
	public String getTitleBarClass (){
	return "projects.music.classes.music.MultiSeq$MultiSeqTitle";
	}

	}

	//////////////////////PANEL//////////////////////
	public static class MultiSeqPanel extends MusicalPanel {
		

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
	public static class MultiSeqControl extends MusicalControl {

	}	

	//////////////////////TITLE//////////////////////
	public static class MultiSeqTitle extends MusicalTitle {

	}	

	//////////////////////DRAWABLE//////////////////////

	public static class MultiSeqDrawable extends ContainerDrawable {

	public MultiSeqDrawable (MultiSeq theRef, MusicalParams params, boolean ed_root) {
		editor_root = ed_root;
		initMultiSeqDrawable(theRef, params);
	}
		
	public MultiSeqDrawable (MultiSeq theRef, MusicalParams params) {
		initMultiSeqDrawable(theRef, params);
	}	

	public void initMultiSeqDrawable (MultiSeq theRef, MusicalParams theparams){
		ref = theRef;
		params = theparams;
		int size = params.fontsize.get();
		int i = 0;
		for (MusicalObject obj : theRef.getElements()) {
			if (obj instanceof ChordSeq) {
				ChordSeqDrawable gvoice = new ChordSeqDrawable ((ChordSeq) obj, params, i++);
				inside.add(gvoice);
				gvoice.setFather(this);
			}
			else {
				SeqChordDrawable gvoice = new SeqChordDrawable ((SeqChord) obj, params, i++);
				inside.add(gvoice);
				gvoice.setFather(this);
			}
		}	
		if (editor_root) {
			makeSpaceObjectList();
			consTimeSpaceCurve(size, 0,params.zoom.get());
		}
	  }	

/*	@Override
	public void drawObject(I_Render g, FXCanvas panel, Rectangle rect,
			MusicalParams params, StaffSystem staffsys, List<I_Drawable> selection, double packStart, 
			double deltax, double deltay) {
		int size = params.fontsize.get();
		if (isRoot_p ()) {
			System.out.println("draw " + " " + this + " " + " " + panel + " " + staffsys);
			consTimeSpaceCurve(size, packStart, params.zoom.get());
			for (SpacedPacket packed : timespacedlist) {
				for (I_Spatializable obj : packed.objectlist ){
					obj.drawObject (g, panel, rect, params, staffsys, selection, packed.start, deltax, deltay);
				}
			}
		}
		drawContainersObjects(g,  panel,  rect,  params, staffsys, selection, deltax);
	}*/
	}

	//////////////////////////////////////////////////////////////////////
}