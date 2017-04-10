package projects.music.classes.music;

import gui.FXCanvas;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.tools.Fraction;
import bonsai.AIS3;
import bonsai.runtime.sugarcubes.*;
import inria.meije.rc.sugarcubes.*;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.RTree;
import projects.music.classes.abstracts.Sequence_S_MO;
import projects.music.classes.interfaces.I_RT;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.classes.music.Measure.MeasureDrawable;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.Rest.RestDrawable;
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
import resources.json.JsonRead;


@Omclass(icon = "223", editorClass = "projects.music.classes.music.Voice$VoiceEditor")
public class Voice extends Sequence_S_MO implements I_RT {
	List<RChord> originalchords;
	public List<RTree> treelist;

	@Ombuilder(definputs=" ( (4/4 ( 1 1 1 1)) ) ; ( RChord ((6000) , (80), (0), 1/4, (1) ) ; 60")
	public Voice (List<RTree> thetree, List<RChord> thechords, double tempo) {
		treelist = thetree;
		chords = thechords;
		originalchords = chords;
		RChord lastchord = chords.get(0);
		setTempo(tempo);
		fillVoice(lastchord, tempo);
	}

	public RTree defrt () {
		List<RTree> rep = new ArrayList<RTree>();
		rep.add(new RTree(1,null));
		rep.add(new RTree(1,null));
		rep.add(new RTree(1,null));
		rep.add(new RTree(1,null));
		return new RTree(new Fraction (4,4),rep);
	}

	public Voice () {
		List<RTree> mes = new ArrayList<RTree>();
		for (int i=0 ; i<2;i++){
			mes.add(defrt());
		}
		treelist = mes;
		chords = new ArrayList<RChord>();
		chords.add(new RChord());
		RChord lastchord = chords.get(0);
		setTempo(60);
		fillVoice(lastchord, 60);
	}

	/*public Voice () {
		Voice copie = (Voice) JsonRead.getFile("/Users/agonc/JAVAWS/organum/invoice.json");
		treelist = copie.treelist;
		chords = copie.originalchords;
		originalchords=copie.originalchords;
		setTempo(copie.getTempo());
		RChord lastchord = chords.get(0);
		fillVoice(lastchord, 60);
	}
	*/

	public void fillVoice (List<RTree> thetree, List<RChord> thechords, double tempo) {
		removeAllelements();
		treelist = thetree;
		chords = thechords;
		originalchords = thechords;
		setTempo(tempo);
		RChord lastchord = chords.get(0);
		fillVoice(lastchord, 60);
	}

	public void fillVoice (RChord lastchord, double tempo) {
		Fraction mesonset = new Fraction (0);
		for (RTree item : treelist) {
			int hmchords = item.countChords ();
			Measure mes = new Measure (item , chords, tempo, lastchord);
			mes.setQoffset(mesonset);
			mesonset = Fraction.addition(mesonset, item.dur.abs());
			addElement(mes);
			if (hmchords > 0) {
				nextChords(hmchords, lastchord);
				lastchord = chords.get(0);
			}
		}
		setQDurs(mesonset);
	}

//////////////////////////////////////////////////
public I_Drawable makeDrawable (MusicalParams params) {
	return new VoiceDrawable (this, params, 0, true);
}


//////////////////////EDITOR//////////////////////
public static class VoiceEditor extends MusicalEditor {


@Override
public String getPanelClass (){
return "projects.music.classes.music.Voice$VoicePanel";
}

@Override
public String getControlsClass (){
return "projects.music.classes.music.Voice$VoiceControl";
}

@Override
public String getTitleBarClass (){
return "projects.music.classes.music.Voice$VoiceTitle";
}

}

//////////////////////PANEL//////////////////////
public static class VoicePanel extends MusicalPanel {
	private SpaceMachine machine;
	private AIS3 ais;

	public void KeyHandler(String car){
		if (!car.isEmpty() && car.charAt(0) >= '1' && car.charAt(0) <= '9') {
			if (machine != null) {
				int note = car.charAt(0) - '1';
				System.out.println(ais.notes[note]);
				System.out.println(ais.constraints);
				ais.constraints.join(ais.notes[note].eq(note));
			}
		}
		switch (car) {
			case "h" : takeSnapShot ();
				break;
			case "c":
				System.out.println("Set CSP");
				setCSP();
				break;
			case "espace":
				System.out.println("Step CSP");
				stepCSP();
				break;
		}
	}

	public void setCSP() {
    ais = new AIS3(10, this);
    Program program = ais.execute();
    machine = SpaceMachine.createDebug(program);
		updatePanel(true);
	}

	public void stepCSP () {
		if (machine != null) {
			if (machine.execute() == MachineStatus.Terminated) { // We explored all solution.
				machine = null;
			}
			else {
				ais.updatePanel();
				// machine.commit();
			}
		}
	}

	@Override
	public int getZeroPosition () {
		return 2;
	}
}

//////////////////////CONTROL//////////////////////
public static class VoiceControl extends MusicalControl {

}

//////////////////////TITLE//////////////////////
public static class VoiceTitle extends MusicalTitle {

}

//////////////////////DRAWABLE//////////////////////

public static class VoiceDrawable extends ContainerDrawable {

int staffnum = 0;

public VoiceDrawable (Voice theRef, MusicalParams params, int thestaffnum) {
	initVoiceDrawable(theRef,  params, thestaffnum);
}

public VoiceDrawable (Voice theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
	editor_root = ed_root;
	initVoiceDrawable(theRef, params, thestaffnum);
}

public void initVoiceDrawable (Voice theRef, MusicalParams theparams, int thestaffnum){
	ref = theRef;
	staffnum = thestaffnum;
	params = theparams;
	int size = params.fontsize.get();
	for (MusicalObject obj : theRef.getElements()) {
		MeasureDrawable gmes = new MeasureDrawable ((Measure) obj, params, staffnum);
		inside.add(gmes);
		gmes.setFather(this);
	}
	if (editor_root) {
		makeSpaceObjectList();
		consTimeSpaceCurve(size, 0, params.zoom.get());
	}
  }



}

//////////////////////////////////////////////////////////////////////

}

