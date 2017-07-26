package projects.music.classes.abstracts;

import java.util.ArrayList;
import java.util.List;

import projects.music.classes.music.RChord;
import projects.music.midi.I_PlayEvent;

public class Sequence_S_MO extends Compose_S_MO {

	public List<RChord> chords;


	public void nextChords (int n, RChord defchord) {
		if (chords.size() == 0){
			chords = new ArrayList<RChord>();
			chords.add(defchord);
		}
		else if (chords.size() > 1) {
				if (n >= chords.size()) {
					chords = new ArrayList<RChord>();
					chords.add(defchord);
				}
				else {
					chords = chords.subList(n, chords.size());
				}
			}
	}

	@Override
	public void PrepareToPlayMidi (long at , int approx, List<I_PlayEvent> list) {
		long date = at;
		for (MusicalObject obj : getElements()) {
			System.out.println ("el obj " + obj + " sa dur " + obj.getDuration() + " q dur " + ((Strie_MO) obj).qdur);
			obj.PrepareToPlayMidi(date, approx, list);
			date = date + obj.getDuration();
		}
	}

}
