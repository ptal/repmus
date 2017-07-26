package projects.music.classes.music;


import gui.CanvasFX;
import gui.FX;
import gui.renders.GCRender;
import gui.renders.I_Render;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.frames.views.I_EditorParams;
import kernel.tools.Fraction;
import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Simple_L_MO;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.drawables.Figure;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;

@Omclass(icon = "223", editorClass = "projects.music.classes.music.MidiFile$MidiFileEditor")
public class MidiFile extends Simple_L_MO {

	static class MidiRealTime {

		  private static int currentTrack;
		  private static ArrayList<Integer> nextMessageOf;

		  static List<int[]> convertMidi2RealTime(Sequence seq) {
		    currentTrack = 0;
		    nextMessageOf = new ArrayList<>();
		    double currentTempo = 500000;
		    int tickOfTempoChange = 0;
		    double msb4 = 0;

		    List<int[]> list = new ArrayList<>();
		    for (int track = 0; track < seq.getTracks().length; track++) {
		      nextMessageOf.add(0);
		    }

		    MidiEvent nextEvent;
		    while ((nextEvent = getNextEvent(seq)) != null) {
		      int tick = (int) nextEvent.getTick();
		      if (noteIsOff(nextEvent)) {
		        double time = (msb4 + (((currentTempo / seq.getResolution()) / 1000) * (tick
		            - tickOfTempoChange)));
		        int canal = ((int) nextEvent.getMessage().getMessage()[0] & 0x0F);
		        int tmp[] = {currentTrack, (int) (time + 0.5),
		            ((int) nextEvent.getMessage().getMessage()[1] & 0xFF), 0, canal};
		        list.add(tmp);

		      } else if (noteIsOn(nextEvent)) {
		        double time = (msb4 + (((currentTempo / seq.getResolution()) / 1000) * (tick
		            - tickOfTempoChange)));
		        int canal = ((int) nextEvent.getMessage().getMessage()[0] & 0x0F);

		        int tmp[] = {currentTrack, (int) (time + 0.5),
		            ((int) nextEvent.getMessage().getMessage()[1] & 0xFF), 1, canal};
		        list.add(tmp);
		      } else if (changeTemp(nextEvent)) {
		        String a = (Integer.toHexString((int) nextEvent.getMessage().getMessage()[3] & 0xFF));
		        String b = (Integer.toHexString((int) nextEvent.getMessage().getMessage()[4] & 0xFF));
		        String c = (Integer.toHexString((int) nextEvent.getMessage().getMessage()[5] & 0xFF));
		        if (a.length() == 1) {
		          a = ("0" + a);
		        }
		        if (b.length() == 1) {
		          b = ("0" + b);
		        }
		        if (c.length() == 1) {
		          c = ("0" + c);
		        }
		        String whole = a + b + c;
		        int newTempo = Integer.parseInt(whole, 16);
		        double newTime = (currentTempo / seq.getResolution()) * (tick - tickOfTempoChange);
		        msb4 += (newTime / 1000);
		        tickOfTempoChange = tick;
		        currentTempo = newTempo;
		      }
		    }
		    return list;
		  }

		  private static MidiEvent getNextEvent(Sequence seq) {
		    ArrayList<MidiEvent> nextEvent = new ArrayList<>();
		    ArrayList<Integer> trackOfNextEvent = new ArrayList<>();
		    for (int track = 0; track < seq.getTracks().length; track++) {
		      if (seq.getTracks()[track].size() - 1 > (nextMessageOf.get(track))) {
		        nextEvent.add(seq.getTracks()[track].get(nextMessageOf.get(track)));
		        trackOfNextEvent.add(track);
		      }
		    }
		    if (nextEvent.size() == 0) {
		      return null;
		    }
		    int closestMessage = 0;
		    int smallestTick = (int) nextEvent.get(0).getTick();
		    for (int trialMessage = 1; trialMessage < nextEvent.size(); trialMessage++) {
		      if ((int) nextEvent.get(trialMessage).getTick() < smallestTick) {
		        smallestTick = (int) nextEvent.get(trialMessage).getTick();
		        closestMessage = trialMessage;
		      }
		    }
		    currentTrack = trackOfNextEvent.get(closestMessage);
		    nextMessageOf.set(currentTrack, (nextMessageOf.get(currentTrack) + 1));
		    return nextEvent.get(closestMessage);
		  }

		  private static boolean noteIsOff(MidiEvent event) {
		    return Integer.toString(event.getMessage().getStatus(), 16).toUpperCase().charAt(0) == '8'
		        ||
		        (noteIsOn(event) && event.getMessage().getLength() >= 3
		            && ((int) event.getMessage().getMessage()[2] & 0xFF) == 0);
		  }

		  private static boolean noteIsOn(MidiEvent event) {
		    return Integer.toString(event.getMessage().getStatus(), 16).toUpperCase().charAt(0) == '9';
		  }

		  private static boolean changeTemp(MidiEvent event) {
		    return Integer.valueOf(
		        ("" + Integer.toString(event.getMessage().getStatus(), 16).toUpperCase().charAt(0)),
		        16) == 15
		        && Integer.valueOf(
		        ("" + Integer.toString(event.getMessage().getStatus(), 16)
		            .toUpperCase()
		            .charAt(1)), 16) == 15
		        && Integer.toString((int) event.getMessage().getMessage()[1], 16).toUpperCase().length()
		        == 2
		        && Integer.toString((int) event.getMessage().getMessage()[1], 16).toUpperCase()
		        .equals("51")
		        && Integer.toString((int) event.getMessage().getMessage()[2], 16).toUpperCase()
		        .equals("3");
		  }
		}


	static class MidiNote {
		int midic;
		long start;
		long dur;
		int track;
		int chan;

		MidiNote (int m, long s, long d,int t, int c) {
			midic=m;
			start=s;
			dur=d;
			track=t;
			chan=c;
		}

		boolean sameNote (int t, int m, int c) {
			return track == t && midic == m && chan == c;
		}
	}


	@Omvariable
	public String path = "/Users/agonc/Desktop/lacrymosa.midi";
	Sequence seq = null;
	int numtracks;
	List<List<MidiNote>> noteTracks;

	@Ombuilder(definputs="\"/Users/agonc/Desktop/lacrymosa.midi\"")
	public MidiFile (String thepath) {
			path = thepath;
			try {
	            seq = MidiSystem.getSequence(new File(path));
	            numtracks = seq.getTracks().length;
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
			// track, time, note, on/off, canal
			List<int[]> OnOfflist = MidiRealTime.convertMidi2RealTime(seq);
			fillTracks(OnOfflist);
	}

	public MidiFile () {
		this("/Users/agonc/Desktop/lacrymosa.midi");
	}

	public void fillTracks (List<int[]> OnOfflist) {
		noteTracks = new ArrayList<List<MidiNote>> ();
		List<MidiNote> openNotes = new ArrayList<MidiNote> ();
		for (int i = 0; i< numtracks; i++)
			noteTracks.add(new ArrayList<MidiNote> ());
		for (int i = 0; i < OnOfflist.size(); i++) {
			int [] event = OnOfflist.get(i);
			if (event[3] == 1) {
				openNotes.add(0,new MidiNote(event[2], event[1], 0, event[0], event[4]));
			}
			else {
				for (int j = 0 ; j < openNotes.size() ; j++) {
					MidiNote cur = openNotes.get(j);
					if (cur.sameNote(event[0], event[2], event[4])) {
						cur.dur = event[1] - cur.start;
						noteTracks.get(cur.track).add(cur);
						openNotes.remove(j);
						break;
					}
				}
			}
		}
	}

	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
			return new MidiFileDrawable (this, params, 0, root);
	}

	public  void drawPreview (I_Render g, CanvasFX canvas, double x, double x1, double y, double y1, I_EditorParams edparams) {

	}

	//////////////////////////////////////////////////

	//////////////////////////////////////////////////
	//////////////////////EDITOR//////////////////////
	//////////////////////////////////////////////////
	public static class MidiFileEditor extends MusicalEditor {


	        @Override
	    	public String getPanelClass (){
	    		return "projects.music.classes.music.MidiFile$MidiFilePanel";
	    	}

	        @Override
	    	public String getControlsClass (){
	    		return "projects.music.classes.music.MidiFile$MidiFileControl";
	    	}

	        @Override
	    	public String getTitleBarClass (){
	    		return "projects.music.classes.music.MidiFile$MidiFileTitle";
	    	}

	    }

	    //////////////////////PANEL//////////////////////
	    public static class MidiFilePanel extends MusicalPanel {

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
	    		return 0;
	    	}

	    	public void init () {
	    		super.init();
	    		MusicalEditor ed = (MusicalEditor) getEditor();
	    		MusicalParams params = (MusicalParams) ed.getParams();
	    		size = params.fontsize.get();
	    		updatePanel(true);
	    	}


	    	@Override
	    	public void omSetViewSize(double w, double h) {
	    		super.omSetViewSize(w,h);
	    		updatePanel(false);
	    	}


	    	public void setGraphObject (I_Drawable Obj) {
	    		graphObj  = Obj;
	    	}

	    	public I_Drawable getGraphObject () {
	    		return graphObj ;
	    	}



	    	@Override
	    	public void updatePanel (boolean changedObject_p){
	    		MusicalEditor ed = (MusicalEditor) getEditor();
	    		if (ed != null) {
	    			MusicalParams params = (MusicalParams) ed.getParams();
	    			MusicalObject object = (MusicalObject) ed.getObject();
	    			if (changedObject_p){
	    				setGraphObject(object.makeDrawable(params, true));
	    		}
	    		GCRender gc = getGCRender();
	            omViewDrawContents(gc);
	    		}
	    	}

	    	public void omViewDrawContents (I_Render g) {
	    		MusicalEditor ed = (MusicalEditor) getEditor();
	    		MusicalParams params = (MusicalParams) ed.getParams();
	    		int size = params.fontsize.get();
	    		FX.omEraseRectContent(g, 0,0, w(), h());
	    		graphObj.drawObject (g, null, null, 0 , 0, 0);
	    		}

	    }


	    //////////////////////CONTROL//////////////////////
	    public static class MidiFileControl extends MusicalControl {

	    }

	    //////////////////////TITLE//////////////////////
	    public static class MidiFileTitle extends MusicalTitle {

	    }

	    /////////////////////DRAWABLE///////////////////
	    public static class MidiFileDrawable extends SimpleDrawable {

	    	int staffnum;

	    public MidiFileDrawable (MidiFile theRef, MusicalParams params, int thestaffnum, boolean root) {
	    	ref = theRef;
			params = params;
			staffnum = thestaffnum;
	    }

	    @Override
	    public void drawObject(I_Render g, Rectangle rect, List<I_Drawable> selection,
				double x0, double deltax, double deltay) {
	    	List<List<MidiNote>> tracks = ((MidiFile) ref).noteTracks;
	    	for (int i = 0; i < tracks.size(); i++) {
	    		List<MidiNote> track = tracks.get(i);
	    		for (int j = 0; j < track.size(); j++) {
		    		MidiNote note = track.get(j);
		    		Color color = FX.SixtheenColors[i%16];
		    		double x = note.start/100;
		    		double y = 500 - note.midic; //
		    		double w = note.dur/100;
		    		double h = 1;
		    		FX.omSetColorFill(g, color);
		    		FX.omFillRect(g, x+x0+deltax, y, w, h);
	    		}
	    	}
	    }

		@Override
		public void collectTemporalObjects(List<SpacedPacket> timelist) {
			timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), true));
		}
	   }
}


