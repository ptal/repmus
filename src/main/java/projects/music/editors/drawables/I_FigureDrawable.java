package projects.music.editors.drawables;

import java.util.List;

import projects.music.classes.music.RNote.RNoteDrawable;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;

public interface I_FigureDrawable  extends I_Drawable {
		public int getBeamsNum ();
		public double getHeadSize(int size);
		public boolean  firstOfChildren ();
		public boolean  lastOfChildren ();
		public double getStemSize (int beamsnumber, int size);
		public double getUpYpos(int size);
		public double getDwnYpos(int size);
		
	}

