package projects.music.classes.music;

import java.util.ArrayList;
import java.util.List;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.tools.Fraction;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Sequence_S_MO;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.Rest.RestDrawable;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.I_Drawable;

@Omclass(icon = "226", editorClass = "projects.music.classes.music.RSeqChord$RSeqChordEditor")
public class RSeqChord extends Sequence_S_MO implements I_RT {
	
	@Ombuilder(definputs="(RChord ((7200) , (80), (0), 1/4, (1)))")
	public RSeqChord (List<I_RT> objs) {
		fillSeq(objs);
	}		
	
	public void fillSeq (List<I_RT> objs) {
		Fraction objonset = new Fraction(0);
		for (I_RT item : objs) {
			if (item instanceof RChord) {
				addElement((RChord) item);
				((RChord) item).setQoffset(objonset);
				objonset = Fraction.addition (objonset, ((RChord) item).getQDurs());
			}
			else if (item instanceof Rest){
				addElement((Rest) item);
				((Rest) item).setQoffset(objonset);
				objonset = Fraction.addition (objonset, ((Rest) item).getQDurs());
			} 
			else if (item instanceof Group){
				addElement((Group) item);
				((Group) item).setQoffset(objonset);
				objonset = Fraction.addition (objonset, ((Group) item).getQDurs());
			} 		
		}
	}	
	
	public RSeqChord () {
		List<I_RT> objs = new ArrayList<I_RT>();
		objs.add(new Rest());
		objs.add(new RChord());
		objs.add(new Group());
		objs.add(new Rest());
		fillSeq(objs);
	}
	
//////////////////////////////////////////////////
public I_Drawable makeDrawable (MusicalParams params, boolean root) {
return new RSeqChordDrawable (this, params, 0, root);
} 


//////////////////////EDITOR//////////////////////
public static class RSeqChordEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.RSeqChord$RSeqChordPanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.RSeqChord$RSeqChordControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Group$GroupTitle";
}

}

//////////////////////PANEL//////////////////////
public static class RSeqChordPanel extends MusicalPanel {

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
	public static class RSeqChordControl extends MusicalControl {

	}	

//////////////////////TITLE//////////////////////
	public static class RSeqChordTitle extends MusicalTitle {

	}	

//////////////////////DRAWABLE//////////////////////

	public static class RSeqChordDrawable extends ContainerDrawable {

	int staffnum = 0;
	
	public RSeqChordDrawable (RSeqChord theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
		editor_root = ed_root;
		initGroup(theRef, params, thestaffnum);
	}
	
	public RSeqChordDrawable (RSeqChord theRef, MusicalParams params, int thestaffnum) {
		initGroup(theRef,  params, thestaffnum);
	}	

	public void initGroup (RSeqChord theRef, MusicalParams theparams, int thestaffnum){
		ref = theRef;
		params = theparams;
		staffnum = thestaffnum;
		int size = params.fontsize.get();
		for (MusicalObject obj : theRef.getElements()) {
			if (obj instanceof RChord) {
				RChordDrawable gchord = new RChordDrawable ((RChord) obj, params, staffnum);
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
			consTimeSpaceCurve(size, 0,params.zoom.get());
		}
	}	

}
//////////////////////////////////////////////////////////////////////

}