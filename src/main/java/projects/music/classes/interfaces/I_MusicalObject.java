package projects.music.classes.interfaces;

import java.util.List;

import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.extras.I_Extra;
import projects.music.midi.I_PlayEvent;
import kernel.I_OMObject;

public interface I_MusicalObject extends I_OMObject{

	public long getOffset ();
	public void setOffset(long offset);

	public long getDuration ();
	public void setDuration(long dur);

	public MusicalObject getFather();
	public void setFather(MusicalObject container);

	public long getOnsetMS ();

	public List<I_Extra> getExtras ();
	public void addExtra  (I_Extra extra);

	public void PrepareToPlayMidi (long at , int approx, List<I_PlayEvent> list);

}
