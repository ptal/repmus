package projects.music.classes.music;

import java.util.ArrayList;
import java.util.List;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.tools.ST;
// import projects.constraints.AIS;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Sequence_L_MO;
import projects.music.classes.music.Chord.ChordDrawable;
import projects.music.classes.music.Silence.SilenceDrawable;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.drawables.ContainerDrawable;
import projects.music.editors.drawables.I_Drawable;
import resources.json.JsonRead;


	@Omclass(icon = "138", editorClass = "projects.music.classes.music.SeqChord$SeqChordEditor")
	public class SeqChord extends Sequence_L_MO {
		@Omvariable
		public List<List<Integer>> lmidic = new ArrayList<List<Integer>>();
		@Omvariable
		public List<Long> ldur = new ArrayList<Long>();
		@Omvariable
		public List<List<Integer>> lchan = new ArrayList<List<Integer>>();
		@Omvariable
		public List<List<Integer>> lvel = new ArrayList<List<Integer>>();
		@Omvariable
		public List<List<Long>> loffset = new ArrayList<List<Long>>();


		@Ombuilder(definputs="((6000) (6000) (6000) (6000)); (1000 -2000 500 -500); ((80)); ((0)); ((1))")
		public SeqChord(List<List<Integer>> Lmidic, List<Long> Ldur, List<List<Integer>> Lvel, List<List<Long>> Loffset,
				List<List<Integer>> Lchan) {
			initSeqChord(Lmidic, Ldur, Lvel,  Loffset, Lchan);
		}

		public SeqChord() {
			ChordSeq cs  = (ChordSeq) JsonRead.getFile("/Users/agonc/JAVAWS/organum/chord-seq.json");
			initSeqChord(cs.lmidic, ST.createList(10, (long) 1000), cs.lvel,  cs.loffset, cs.lchan);
		}

		public void initSeqChord (List<List<Integer>> Lmidic,
					List<Long> Ldur, List<List<Integer>> Lvel,
					List<List<Long>> Loffset,
					List<List<Integer>> Lchan)  {
			removeAllelements();
			int sizevel = Lvel.size();
			int sizedur = Ldur.size();
			int sizechan = Lchan.size();
			int sizeoffset = Loffset.size();
			int [] avel = {60};
			int [] achan = {1};
			long [] aoffset = {1};
			long onset = 0;
			long dur = 1000 ;//cambiar avec last de la liste
			List<Integer> vel = ST.a2l (avel);
			List<Integer> chan = ST.a2l (achan);
			List<Long> offset = ST.a2long (aoffset);
			for (int i=0; i< Lmidic.size(); i++) {
				if (i < sizevel) vel = Lvel.get(i);
				if (i < sizechan) chan = Lchan.get(i);
				if (i < sizeoffset) offset = Loffset.get(i);
				if (i < sizedur) dur = Ldur.get(i);
				if (dur > 0) {
					Chord newch = new Chord (Lmidic.get(i), vel,offset,ST.createList(1, dur),chan);
					newch.setOffset(onset);
					addElement(newch);
				}
				else {
					dur = dur * -1;
					Silence newsil = new Silence(dur);
					newsil.setOffset(onset);
					addElement(newsil);
				}
				onset = onset + dur;
			}
			setDurSeq();
			setSlots();
		}

		public void setSlots () {
			lmidic = new ArrayList<List<Integer>>();
			lchan = new ArrayList<List<Integer>>();
			ldur = new ArrayList<Long>();
			lvel = new ArrayList<List<Integer>>();
			loffset = new ArrayList<List<Long>>();
			for (MusicalObject chord : getElements()) {
				if (chord instanceof Chord) {
					lmidic.add(((Chord) chord).lmidic);
					lvel.add(((Chord) chord).lvel);
					loffset.add(((Chord) chord).loffset);
					lchan.add(((Chord) chord).lchan);
					ldur.add(((Chord) chord).ldur.get(0));
				}
				else
					ldur.add(((Silence) chord).dur);
			}
		}

		public I_Drawable makeDrawable (MusicalParams params) {
			return new SeqChordDrawable (this, params, 0, true);
		}

	//////////////////////////////////////////////////
	//////////////////////EDITOR//////////////////////
	//////////////////////////////////////////////////
	public static class SeqChordEditor extends MusicalEditor {


	@Override
	public String getPanelClass (){
	return "projects.music.classes.music.SeqChord$SeqChordPanel";
	}

	@Override
	public String getControlsClass (){
	return "projects.music.classes.music.SeqChord$SeqChordControl";
	}

	@Override
	public String getTitleBarClass (){
	return "projects.music.classes.music.SeqChord$SeqChordTitle";
	}

	}

	//////////////////////PANEL//////////////////////
	public static class SeqChordPanel extends MusicalPanel {

	// AIS csp = null;

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
		}
	}

		public void setCSP () {
		//csp = new AIS(this);
		updatePanel(true);
		}

		public void solveCSP () {
			// if (csp != null)
			// 	csp.solve();
			// csp = null;
		}


	@Override
	public int getZeroPosition () {
	return 2;
	}

	}


	//////////////////////CONTROL//////////////////////
	public static class SeqChordControl extends MusicalControl {

	}

	//////////////////////TITLE//////////////////////
	public static class SeqChordTitle extends MusicalTitle {

	}

	//////////////////////DRAWABLE//////////////////////

	public static class SeqChordDrawable extends ContainerDrawable {
		int staffnum =0;

		public SeqChordDrawable (SeqChord theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
			editor_root = ed_root;
			InitSeqChordDrawable(theRef, params, thestaffnum);
		}

		public SeqChordDrawable (SeqChord theRef, MusicalParams theparams, int thestaffnum) {
			InitSeqChordDrawable(theRef, theparams, thestaffnum);
		}

		public void InitSeqChordDrawable (SeqChord theRef, MusicalParams theparams, int thestaffnum) {
			params = theparams;
			ref = theRef;
			staffnum = thestaffnum;
			for (MusicalObject chord : theRef.getElements()) {
				if (chord instanceof Chord) {
					ChordDrawable gchord = new ChordDrawable ((Chord) chord, params, staffnum);
					inside.add(gchord);
					gchord.setFather(this);
				}
				else {
					SilenceDrawable gsilence = new SilenceDrawable ((Silence) chord, params, staffnum);
					inside.add(gsilence);
					gsilence.setFather(this);
				}
			}
			if (editor_root) {
				int size = params.fontsize.get();
				makeSpaceObjectList();
				consTimeSpaceCurve(size, 0,params.zoom.get());
			}
		}
	}
}

