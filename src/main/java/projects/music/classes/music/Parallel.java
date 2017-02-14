	package projects.music.classes.music;


	import gui.FXCanvas;
import gui.renders.I_Render;

	import java.util.ArrayList;
import java.util.List;

	import javafx.scene.text.Font;
import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.frames.views.I_EditorParams;

	import com.sun.javafx.geom.Rectangle;

	import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Parallel_S_MO;
import projects.music.classes.abstracts.Parallel_L_MO;
import projects.music.classes.music.Parallel.ParallelDrawable;
import projects.music.classes.music.Voice.VoiceDrawable;
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


	@Omclass(icon = "224", editorClass = "projects.music.classes.music.Parallel$ParallelEditor")
	public class Parallel extends Parallel_L_MO {
		
		@Ombuilder(definputs=" ( Voice ( ( (4/4 ( 1 1 1 1)) ) , ( RChord ((6000), (80), (0), 1/8, (1)) ) , 60) "
				+ "Silence ( 3000 ) "
				+ "ChordSeq ( ((6000) (6100)), (0 3000), ((1000)), ((80)), ((0)), ((1)), 100 ) ")
		public Parallel (List<MusicalObject> theelems) {
			long onset = 0;
			for (MusicalObject obj : theelems)  {
				addElement(obj);
				obj.setOffset(onset);
			}
			setDurPar();
		}
		
		public Parallel () {
			List<MusicalObject> elems = new ArrayList<MusicalObject> ();
			elems.add(new Note());
			elems.add(new Chord());
			elems.add(new ChordSeq());
			elems.add(new SeqChord());
			elems.add(new MultiSeq());
			long onset = 0;
			for (MusicalObject obj : elems)  {
				addElement(obj);
				obj.setOffset(onset);
			}
			setDurPar();
		}
		
		
		public MusicalParams getParams() {
			MusicalParams params = new MusicalParams();
			return params;
		}
		
		public  void drawPreview (I_Render g, FXCanvas canvas, double x, double x1, double y, double y1, I_EditorParams edparams) {
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
	public I_Drawable makeDrawable (MusicalParams params) {
		return new ParallelDrawable (this, params, true);
	}


	//////////////////////EDITOR//////////////////////
	public static class ParallelEditor extends MusicalEditor {


	@Override
	public String getPanelClass (){
	return "projects.music.classes.music.Parallel$ParallelPanel";
	}

	@Override
	public String getControlsClass (){
	return "projects.music.classes.music.Parallel$ParallelControl";
	}

	@Override
	public String getTitleBarClass (){
	return "projects.music.classes.music.Parallel$ParallelTitle";
	}

	}

	//////////////////////PANEL//////////////////////
	public static class ParallelPanel extends MusicalPanel {
		
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
	public static class ParallelControl extends MusicalControl {

	}	

	//////////////////////TITLE//////////////////////
	public static class ParallelTitle extends MusicalTitle {

	}	

	//////////////////////DRAWABLE//////////////////////

	public static class ParallelDrawable extends ContainerDrawable {
		List<MusicalParams> paramslist = new ArrayList<MusicalParams> ();
		
	public ParallelDrawable (Parallel theRef, MusicalParams params, boolean ed_root) {
			editor_root = ed_root;
			initParallelDrawable(theRef, params);
	}
		
	public ParallelDrawable (Parallel theRef, MusicalParams theparams) {
		initParallelDrawable(theRef,  theparams);
	}	

	public void initParallelDrawable (Parallel theRef, MusicalParams theparams){
		ref = theRef;
		params = theparams;
		int size = params.fontsize.get();
		int i = 0;
		for (MusicalObject obj : theRef.getElements()) {
			MusicalParams param = obj.getParams();
			param.getStaff().setMarges(1, 4);
			paramslist.add(param);
			I_Drawable drawable = obj.makeDrawable(param);
			inside.add(drawable);
			drawable.setFather(this);
		}	
		if (editor_root) {
			makeSpaceObjectList();
			consTimeSpaceCurve(size, 0,params.zoom.get());
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

	/*@Override
	public void drawObject(I_Render g, FXCanvas panel, Rectangle rect,
			 List<I_Drawable> selection, double packStart, 
			double deltax, double deltay) {
		int size = params.fontsize.get();
		//if (isRoot_p ()) {
			consTimeSpaceCurve(size, packStart, params.zoom.get());
			for (SpacedPacket packed : timespacedlist) {
				for (I_Spatializable obj : packed.objectlist ){
					obj.drawObject (g, panel, rect,  selection, packed.start, deltax, deltay);
				}
			}
		//}
		drawContainersObjects(g,  panel,  rect, selection, deltax);
	}*/
	
	public void collectTemporalObjectsS(List<SpacedPacket> timelist) {
		int i = 0;
		for (I_Drawable obj : getInside()) {
			StaffBound ss = new StaffBound(paramslist.get(i), false);
			StaffBound es  = new StaffBound(paramslist.get(i), true);
			timelist.add(new SpacedPacket(ss, obj.getRef().getOnsetMS()));
			timelist.add(new SpacedPacket(es, obj.getRef().getOnsetMS() + obj.getRef().getDuration()));
			((SimpleDrawable) obj).collectTemporalObjectsS(timelist);
			i++;
		}
	  }
	}
}

	//////////////////////////////////////////////////////////////////////


