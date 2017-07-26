package projects.music.classes.abstracts;

import java.util.List;

import projects.music.midi.I_PlayEvent;

public class Sequence_L_MO extends Compose_L_MO{

	@Override
	public void PrepareToPlayMidi (long at , int approx, List<I_PlayEvent> list) {
		long date = at;
		for (MusicalObject obj : getElements()) {
			obj.PrepareToPlayMidi(date, approx, list);
			date = date + obj.getDuration();
		}
	}

}
