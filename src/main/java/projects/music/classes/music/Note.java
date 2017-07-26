package projects.music.classes.music;

import gui.FX;
import gui.renders.I_Render;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.sun.javafx.geom.Rectangle;

import kernel.annotations.Ombuilder;
import kernel.annotations.Omclass;
import kernel.annotations.Omvariable;
import kernel.tools.ST;
import projects.music.classes.abstracts.Simple_L_MO;
import projects.music.editors.MusChars;
import projects.music.editors.MusicalControl;
import projects.music.editors.MusicalEditor;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.MusicalTitle;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;
import projects.music.midi.I_PlayEvent;
import projects.music.midi.PlayEvent.PlayEventMidi;

@Omclass(icon = "137", editorClass = "projects.music.classes.music.Note$NoteEditor")
public class Note extends Simple_L_MO  {

	@Omvariable
	public int midic = 0;
	 @Omvariable
	public int vel = 0;
	 @Omvariable
	public int chan = 0;
	 @Omvariable
	public int dur = 0; //voir si ne change rien

/*	@JsonCreator
	public Note (int themidic, int thevel, long dur, int thechan) {
		setOffset(0);
		setDuration(dur);
		midic = themidic;
		vel = thevel;
		chan = thechan;
	}
	*/


	public Note (int themidic, int thevel, long dur, int thechan, long offset) {
		super ();
		setOffset(offset);
		setDuration(dur);
		midic = themidic;
		vel = thevel;
		chan = thechan;
	}

	@Ombuilder(definputs="6000; 80; 1000; 1")
	public Note (int themidic, int thevel, long dur, int thechan) {
		this (themidic,thevel, dur, thechan, 0);
	}

	 public Note() {
	    this (6000,80,1000,1,0);
	 }

	//////////////////////////////////////

	public int getMidic () {
		return midic;
	}

	public void setMidic (int themidic) {
		 midic = themidic;
	}

	public int getChan () {
		return chan;
	}

	public void setChan (int thechan) {
		 chan = thechan;
	}

	public int getVel () {
		return vel;
	}

	public void setVel (int thevel) {
		 vel = thevel;
	}

	public I_Drawable makeDrawable (MusicalParams params, boolean root) {
		return new NoteDrawable (this, params, 0, root);
	}

	@Override
	public void PrepareToPlayMidi (long at , int approx, List<I_PlayEvent> list) {

    	try {
    		ShortMessage msg1 = new ShortMessage ();
			msg1.setMessage(ShortMessage.NOTE_ON + getChan(), getMidic()/100, getVel());
			ShortMessage msg2 = new ShortMessage ();
	    	msg2.setMessage(ShortMessage.NOTE_OFF + getChan(), getMidic()/100, 0);
			PlayEventMidi noteOn = new PlayEventMidi(msg1, at);
			PlayEventMidi noteOff = new PlayEventMidi(msg2, at + this.getDuration());
			list.add(noteOn);
			list.add(noteOff);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}
	//////////////////////////////////////////////////
	//////////////////////EDITOR//////////////////////
	//////////////////////////////////////////////////
    public static class NoteEditor extends MusicalEditor {


        @Override
    	public String getPanelClass (){
    		return "projects.music.classes.music.Note$NotePanel";
    	}

        @Override
    	public String getControlsClass (){
    		return "projects.music.classes.music.Note$NoteControl";
    	}

        @Override
    	public String getTitleBarClass (){
    		return "projects.music.classes.music.Note$NoteTitle";
    	}

    }

    //////////////////////PANEL//////////////////////
    public static class NotePanel extends MusicalPanel {

    	public void KeyHandler(String car){
   		 switch (car) {
			 default : super.KeyHandler(car);;
   		 }
    	}

    	@Override
    	public int getZeroPosition () {
    		return 2;
    	}

    }


    //////////////////////CONTROL//////////////////////
    public static class NoteControl extends MusicalControl {

    }

    //////////////////////TITLE//////////////////////
    public static class NoteTitle extends MusicalTitle {

    }

    /////////////////////DRAWABLE///////////////////
    public static class NoteDrawable extends SimpleDrawable {
    	public String head = MusChars.head_1_4;
    	String altChar = "";
    	int centerY;
    	int auxLines;
    	int altSize = 0;
    	public int deltaHead = 0;
    	public int deltaAlt = 1;
    	boolean selected;
    	boolean stem_p;
    	boolean tied_p;
    	int auxlines;

    	final int headSizefactor = 4;
    	final int altSizefactor = 3;

    	public double centerX;
    	public int staffnum = 0;

    public NoteDrawable (Note theRef, MusicalParams params, int thestaffnum, boolean ed_root) {
    		editor_root = ed_root;
    		InitNoteDrawable(theRef, params, thestaffnum);
    }

    public NoteDrawable (Note theRef, MusicalParams params, int thestaffnum) {
    	InitNoteDrawable (theRef, params, thestaffnum);
    }

    public void InitNoteDrawable (Note theRef, MusicalParams theparams, int thestaffnum) {
    	params = theparams;
    	staffnum = thestaffnum;
    	int size = params.fontsize.get();
    	StaffSystem staffSystem = params.getStaff();
    	MultipleStaff staff = staffSystem.getStaffs().get(staffnum);
    	Scale scale = Scale.getScale (params.scale.get());
    	ref = theRef;
    	altChar = scale.getAlteration(theRef.midic);
    	altSize = altChar.length();
    	auxlines = StaffSystem.setAuxLines (((Note) ref).getMidic(), staff);
    	centerY = staff.getPosY (scale, theRef.getMidic());
    	centerX = (2 + staffSystem.getXmarge()) * size;
    	double staffToppixels = staff.getTopPixel(size);
    	setRectangle ((2 + staffSystem.getXmarge()) * size,
    						staffToppixels + (centerY * size/8),
    						size/2, size/4);
    	/*if (editor_root) {
			makeSpaceObjectList();
			consTimeSpaceCurve(size, 0,params.zoom.get());
		}*/
    }

    @Override
    public void drawObject(I_Render g, Rectangle rect, List<I_Drawable> selection, double x0,
    		double deltax, double deltay) {
    	Font omicronFont = params.getFont("microSize");
    	Font omHeads = params.getFont("headSize");;
    	StaffSystem staffsys = params.getStaff();
    	int size = params.fontsize.get();
    	int dent = size/4;
    	MultipleStaff staff = staffsys.getStaffs().get(staffnum);
    	double stafftoppixels = staff.getTopPixel(size);
    	int xPos = (int) Math.round (x() + x0 + ((deltaHead * size) / headSizefactor));
    	long posAlt = xPos;
    	if (altChar != "") posAlt = Math.round (x() + x0 - ((deltaAlt * size * 3) / 10));
    	double yPos = stafftoppixels + (centerY * dent/2);
    	double strsize = FX.omStringSize(head,omHeads);
    	FX.omDrawString(g, xPos, yPos, head);
    	if (altChar != "") {
    		FX.omSetFont(g, omicronFont);
    		FX.omDrawString(g, posAlt, yPos, altChar);
    		FX.omSetFont(g, omHeads);
    	}
    	StaffSystem.drawAuxLines (g, auxlines, size, xPos, yPos, staff, strsize);
    	drawSlot (g, xPos, yPos, params);
    }

    public void drawSelection(I_Render g, List<I_Drawable> selection){
    	if  (selection.contains(ref))
    		drawRectSelection(g);
    }

    public String  getDynFromVel (int vel) {
    	if (vel <= 0)
    		return MusChars.dynamicsArray[0];
    	else for (int i = 1; i < MusChars.dynamicsMidiArray.length; i++) {
    			if (MusChars.dynamicsMidiArray [i] > vel)
    				return MusChars.dynamicsArray[i-1];
    			}
    	return MusChars.dynamicsArray[MusChars.dynamicsMidiArray.length - 1];
    }

    public void drawSlot (I_Render g, double xPos, double yPos, MusicalParams params) {
    	Color oldcolor = FX.omGetColorFill(g);
    	String slot = params.slotmode.get();
    	int size = params.fontsize.get();
    	int zoom = params.zoom.get();
    	Note thenote = ((Note) ref);
    	int chan = thenote.getChan();
    	Color thecolor = FX.SixtheenColors [(chan - 1) % 16];
    	FX.omSetColorFill(g, thecolor);
    	if (slot.equals("channel")){
    		Font oldFont = FX.omGetFont(g);
    		FX.omSetFont(g, params.getFont("sing4/5Size"));
    		FX.omDrawString (g, xPos + 3 + size / headSizefactor , yPos, "" +  thenote.getChan());
    		FX.omSetFont(g,oldFont);
    	}
    	else if (slot.equals("dur")){
    		FX.omFillRect (g, xPos + size / headSizefactor, yPos, ms2pixel(thenote.getDuration(), size), size/12);
    	}
    	else if (slot.equals("dyn")){
    		Font oldFont = FX.omGetFont(g);
    		FX.omSetFont(g, params.getFont("extrasSize"));
    		FX.omDrawString (g, xPos + 3 + size / headSizefactor , yPos, "" +  getDynFromVel(thenote.getVel()));
    		FX.omSetFont(g,oldFont);
    	}
    	else if (slot.equals("port")){
    		Font oldFont = FX.omGetFont(g);
    		FX.omSetFont(g, params.getFont("sing4/5Size"));
    	//	FX.omDrawString (g, xPos + 3 + size / headSizefactor , yPos, "" +  thenote.getPort());
    		FX.omSetFont(g,oldFont);
    	}
    	FX.omSetColorFill(g, oldcolor);
    }

    public void drawExtras (I_Render g, MusicalPanel view, int size, double xPos, double yPos, int zoom) {

    }

    @Override
    public I_Drawable getClickedObject(double x, double y) {
    	I_Drawable rep = null;
    	Rectangle rect = new Rectangle ((int) x(), (int) y(), (int) w(), (int) h() );
    	if (rect.contains((int) x,(int) y))
    		rep = this;
    	return rep;
    }

    @Override
    public void collectTemporalObjects(List<SpacedPacket> timelist) {
  	  timelist.add(new SpacedPacket(this, this.ref.getOnsetMS(), false));
  }

    }

    /*(defmethod om-get-menu-context ((object grap-note))
    		  (alteration-menucontext object))

    		(defun alteration-menucontext (object)
    		  (let* ((alt (find-alt-list (midic (reference object))))
    		        (item1 (first alt)) (item2 (second alt)) (item3 (third alt))
    		        (menu1 (om-new-leafmenu (string+ (get-note-name (car item1)) (case (second item1) (-2 "bb") (-1 "b") (0 ".") (1 "#") (2 "##") (t "")))
    		                                #'(lambda ()
    		                                    (set-note-tonalite (reference object) (car item1) (second item1))
    		                                    (update-panel (panel (editor (om-front-window)))))
    		                                ))
    		        (menu2 (om-new-leafmenu (string+ (get-note-name (car item2)) (case (second item2) (-2 "bb") (-1 "b") (0 ".") (1 "#") (2 "##") (t "")))
    		                           #'(lambda ()
    		                               (set-note-tonalite (reference object) (car item2) (second item2))
    		                               (update-panel (panel (editor (om-front-window)))))
    		                           ))
    		        (menu3 (when (car item3) (om-new-leafmenu (string+ (get-note-name (car item3)) (case (second item3) (-2 "bb") (-1 "b") (0 ".") (1 "#") (2 "##") (t "")))
    		                                                  #'(lambda ()
    		                                                      (set-note-tonalite (reference object) (car item3) (second item3))
    		                                                      (update-panel (panel (editor (om-front-window)))))
    		                                                  )))
    		        (menureset (om-new-leafmenu (string+ "init.")
    		                                #'(lambda ()
    		                                    (set-tonalite (reference object) nil)
    		                                    (update-panel (panel (editor (om-front-window)))))
    		                                )))
    		    (remove nil (list menu1 menu2 menu3 menureset))))*/

}
