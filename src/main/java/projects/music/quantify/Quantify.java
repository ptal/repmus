package projects.music.quantify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kernel.tools.Fraction;
import kernel.tools.ST;
import projects.music.classes.abstracts.RTree;

public class Quantify {
	
	double distance_weight = 0.5;
	int maximum_pulses = 32;
	int min_pulses = 5;
	int max_division = 8;
	
	////Begin Graces Notes
	class GraceInfo {
		double prev;
		double cur;
	}
	int unquantized_notes = 0;
	List<GraceInfo> global_grace_notes = new ArrayList<GraceInfo>();
	////End Graces Notes
	
	double iota = 0.0001;
	double dist_iota = 0.03;
	double proportions_iota = 0.001;
	
	List<Integer> u_d_h = Arrays.asList(new Integer [] {1, 2, 4, 3, 6, 5, 8, 7, 10, 12, 16, 9, 14, 11,
			13, 15, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32,
			33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50});
	
	List<Integer> forbidden_rythmic_divisions = new ArrayList<Integer>();
	
	//===========accum error
	double accum_error = 0;
	void setError (double to, double from) {accum_error = to - from - iota;}
	void resetError () {accum_error = 0;}
	boolean accum_error_p () {return accum_error > 0;}
	double getAccumError () {return accum_error;}
	//======================
	
	//euclidean distance entre deux listes
	double distance (List<Double> list, List<Double> qlist) {
		double accum =  0.0;
		double len = 0.0;
		double x,y;
		for (int i = 0 ; i != list.size() ; i++) {
			x = list.get(i);
			y = qlist.get(i);
			accum = accum +  ((x-y)*(x-y));
			len = len + 1;
		}
		return  (Math.sqrt(accum) / Math.max(1.0, len));
	}
	
	//proportion distance entre deux listes
		double proportion_distance (List<Double> list, List<Double> qlist, double tmin, double tmax) {
			List<Double> nlist = new ArrayList<Double>();
			List<Double> nqlist = new ArrayList<Double>();
			nlist.addAll(list);
			nlist.add(0, tmin);
			nlist.add(tmax);
			nqlist.addAll(qlist);
			nqlist.add(0, tmin);
			nqlist.add(tmax);
			List<Double> l1 = ST.xTodx(nlist);
			List<Double> l2 = ST.xTodx(nqlist);
			for (int i = 0; i < l1.size(); i++)
				if (l1.get(i) == 0) l1.remove(i);
			for (int i = 0; i < l2.size(); i++)
				if (l2.get(i) == 0) l2.remove(i);
			return  win3(l1,l2);
		}
		
		double win3 (List<Double> var1, List<Double> var2) {
			List<Double> K1 = ST.createList (Math.abs(Math.max(var1.size(), var2.size()) - var1.size()),(double) 1);
			List<Double> N1 = ST.createList (Math.abs(Math.max(var1.size(), var2.size()) - var2.size()),(double) 1);
			K1.addAll(var1);
			N1.addAll(var2);
			List<List<Double>> K = new ArrayList<List<Double>>();
			for (Double item : K1)
				K.add(ST.createList(1,item));
			List<List<Double>> N = new ArrayList<List<Double>>();
			for (Double item : N1)
				N.add(ST.createList(1,item));
			List<List<Double>> B = new ArrayList<List<Double>>();
			for (int i = 0; i < K.size(); i++){
				List<Double> v1 = K.get(i);
				List<Double> v2 = N.get(i);
				if (v1.get(0) > v2.get(0)) {
					B.add(ST.createList(1, Math.pow(v1.get(0)/v2.get(0),3)));
				}
				else {
					B.add(ST.createList(1, Math.pow(v2.get(0)/v1.get(0),3)));
				}
			}
			double rep = 0;
			for (List<Double> item : B)
				rep = rep + Math.pow(ST.summarize(item) / item.size(), 3);
			return  rep;
		}
	    
	
	Integer get_beat_duration (double tempo, Long unit) {
		return (int) Math.round(240000 / tempo / unit);
	}
	
	
	
	int minimum_pulses (List<Double> atack_times, double tmin, double tmax) {
		atack_times = ST.xTodx(ST.removeDuplicates(atack_times));
		int min = (int) (tmax - tmin);
		for (double item : atack_times )
			min = (int) Math.min(min, item);
		min = min * 3;
		return (int) Math.min (maximum_pulses, Math.max(1, (Math.floor ((tmax- tmin) / min))));
	}
	
	//============================
	protected class Quanta {
		int pulses;
		int nb_error;
		double euclidean_distance;
		double proportion_distance;
		List<Double> quantized_times = new ArrayList<Double>();
		
		public String toString() {
		    return "-> " + pulses + " " + nb_error + " " + euclidean_distance + " " + proportion_distance + " " + quantized_times;
		}
	}
	

	
	boolean is_better (int p1, int p2) {
		return u_d_h.indexOf(p1) < u_d_h.indexOf(p2);
	}
	
	int quant_test1 (Quanta q1, Quanta q2){
		if (q1.nb_error < q2.nb_error)
			return -1;
		else if (q1.nb_error > q2.nb_error)
			return 1;
		else if (is_better(q1.pulses,q2.pulses) && (q1.proportion_distance < dist_iota))
			return -1;
		else if (q1.proportion_distance < (q2.proportion_distance - proportions_iota ) )
			return -1;
		else return 1;
	}
	
	int quant_test2 (Quanta q1, Quanta q2){
		if (q1.nb_error < q2.nb_error)
			return -1;
		else if (q1.nb_error > q2.nb_error)
			return 1;
		else if (is_better(q1.pulses,q2.pulses) )
			return -1;
		else return 1;
	}
	
	List<Quanta> computeQuanta (double tmin, double tmax, int max_pulses, List<Double> atack_times) {
		List<Quanta> rep = new ArrayList<Quanta> ();
		List<Integer> needed_pulses = needed_pulses(max_pulses,atack_times, tmin,tmax);
		for (int item : needed_pulses) {;
			List<Double> quantized_times = new ArrayList<Double>();
			for (double time : atack_times){
				quantized_times.add(adjust_time_to_grid(time, item, tmin, tmax ));
			}
			Quanta quant = new Quanta();
			quant.pulses = item ;
			quant.nb_error = deletions(quantized_times);
			quant.euclidean_distance = distance (atack_times, quantized_times);
			quant.proportion_distance = proportion_distance (atack_times, quantized_times, tmin, tmax);
			quant.quantized_times = quantized_times;
			rep.add(quant);
		}
		return rep;
	}
	
	int deletions (List<Double> quantized_times) {
		List<Double> copy = ST.xTodx(quantized_times);
		int rep = 0;
		for (double item : copy)
			if (item == 0) rep++;
		return rep;
	}
	
	double  adjust_time_to_grid (double time, int pulses, double tmin, double tmax) {
		return tmin + ((tmax - tmin) * (1.0 / pulses) * Math.round( ((time - tmin) * pulses) / (tmax - tmin) ));
	}
	
	List<Integer> needed_pulses (int nb_pulse_max, List<Double> atack_times, double tmin, double tmax) {
		if (ST.emptyList(atack_times))
			return ST.createList(1, 1);
		else {
			List<Integer> rep = new ArrayList<Integer>();
			int min_pulses = minimum_pulses(atack_times, tmin, tmax);
			for (int item = min_pulses; item < nb_pulse_max ; item++)
				rep.add(item);
			if (ST.emptyList(rep))
				return ST.createList(1,  (nb_pulse_max - 1));
			else return rep;
		}
	}
	// End Quanta =========================
	
	List<Quanta> scoreApprox (List<Double> times, double tmin, double sec_sur_black) {
		List<Quanta> rep = new ArrayList<Quanta>();
		int nb_pulse_max = Math.min(maximum_pulses, max_division);
		List<Quanta> option1 = computeQuanta(tmin, tmin+sec_sur_black, nb_pulse_max, times);
		List<Quanta> option2 = computeQuanta(tmin, tmin+sec_sur_black, nb_pulse_max, times);
		option1.sort((q1, q2) -> quant_test1 (q1,q2));
		option2.sort((q1, q2) -> quant_test2 (q1,q2));
		for (int i = 0; i < option1.size(); i++) {
			if ((option1.get(i).euclidean_distance * 1.2 * (distance_weight - 1)) < 
					(option2.get(i).euclidean_distance * distance_weight))
				rep.add(option1.get(i));
			else rep.add(option2.get(i));
		}
		return rep;
	}
	
	
			                                                                 
	RTree  beat_structure (Quanta quants, boolean slur_p, double from, double to) {
		List<Double> atimes = new ArrayList<Double> ();
		atimes.addAll(quants.quantized_times);
		double last_atimes;
		if (! ST.emptyList (atimes)) {
			last_atimes = atimes.get(atimes.size() - 1);
			if (! (last_atimes == to ))
				atimes.add(to);
			if (atimes.get(0)!=from)
				atimes.add(0, from);
			}
		List<Double> durs = new ArrayList<Double> ();
		for (double item : ST.xTodx(atimes))
			if (item != 0.0) durs.add(item);
		double min_dur = (atimes.get(atimes.size() - 1) - atimes.get(0)) / quants.pulses;
		List<RTree>  rep = new ArrayList<RTree>  ();
		if (! ST.emptyList (durs)) {
			for (double item : durs)
				if (item != 0.0)
					rep.add(new RTree (Math.round (item / min_dur), slur_p));
		}
		return new RTree (1, rep);
	}
			    
	boolean forbidden_structure (RTree thestruct){
		List<RTree> struct = thestruct.getProporsitons();
		int division = 0;
		for (RTree item : struct)
			division =  division + (int) item.prop;
		if (division > max_division)
			return true;
		else {
			return forbidden_rythmic_divisions.contains(division);	
		}
	}
			  
	Quanta less_bad_quanta (List<Quanta> q_structures,  List<Double> times, double from, double dur) {
		Quanta rep = null;
		for (Quanta item : q_structures)
			if (1 == item.nb_error)
				rep = item;
		if (rep != null)
			return try_eliminating_one (rep, times, from, dur);
			else return rep;
	}
			 

	Quanta try_eliminating_one (Quanta q_structure, List<Double>  times, double from, double dur) {
		Quanta rep = null;
		for (Quanta current_atimes : scoreApprox(get_rid_of_duplicates(q_structure.quantized_times, times), from, dur)){
			if (! forbidden_structure(beat_structure(current_atimes, false, from, from + dur)))
				return current_atimes;
		}
		return rep;
	}
			 

	List<Double> get_rid_of_duplicates (List<Double> x, List<Double> times) {
		if (x.size() <= 1)
			return times;
		else { 
				List<Double> rep = get_rid_of_duplicates(x.subList(1, x.size()-1),times.subList(1, times.size()-1) ); 
				if (x.get(0) == x.get(1)) {
					keep_unquantized_statistics (x.get(1), x.get(0));
					return rep;
				}
				else {
					rep.add(0, times.get(0));
					return rep;
				}
			}
		}
					
			                                                                 
	Quanta testQuantizeConstraints(List<Double> list, double tmin, double beat_dur, boolean prev_slur) {
		List<Double> rep = new ArrayList<Double>();
		List<Quanta> q_structures = scoreApprox(list, tmin, beat_dur);
		for (Quanta current_atimes : q_structures)
			if (! forbidden_structure (beat_structure (current_atimes, prev_slur, tmin , (tmin + beat_dur)))) 
				if (current_atimes.nb_error > 0)
	                return less_bad_quanta (ST.createList (1, current_atimes), list, tmin, beat_dur);
	            else return current_atimes;
		return less_bad_quanta(q_structures, list, tmin, beat_dur);
	}
	
	void keep_unquantized_statistics (double atime, double previous) {
		unquantized_notes = unquantized_notes + 1;
		if (atime != -1) {
			GraceInfo grace = new GraceInfo();
			grace.prev = previous;
			grace.cur = atime - previous;
			global_grace_notes.add(grace );
		}
	}
			  
	
	protected class Rep_getOptimalTimeSection {
		Quanta a_section = null;
		boolean slur_p = false;
		boolean prev_slur_p = false;
	}
	
	/* "Given a beat duration span and an initial time (tmin), quantizes the
	attack times in list. Beat duration
	is beat-dur. Beat's onset time is tmin. Beat's end time is 'to'. If
	prev-slur is on, the first
	onset time of this beat should be slurred with the last one of the previous
	beat. Prev-slur may change in
	this function.If  Slur? is on, last onset of this beat should be linked
	with first of next beat (i.e. slur?
	becomes prev-slur in the next call)."*/
	Rep_getOptimalTimeSection  getOptimalTimeSection
		(List<Double> list, double to, double beat_dur, double tmin, boolean prev_slur){
		Rep_getOptimalTimeSection rep = new Rep_getOptimalTimeSection();
		
		Quanta atimes = null;
		Double last_list = null;
		Double end_surplus = null;
		List<Double> qlist = null;
		List<Double> head = null;
		List<Double> partition = null;
		boolean lagging_count = false;
		boolean slur_p = false;
		if (! ST.emptyList(list)){
			atimes = testQuantizeConstraints (list, tmin, beat_dur, prev_slur);
			last_list = ST.lastElement(list);
		}
		if (atimes != null)
			qlist = atimes.quantized_times;
		if (qlist != null)
			lagging_count =  tmin > 0 && ! (tmin == qlist.get(0));
		if (accum_error_p() && ! lagging_count ){
			if (list.size() > 0)
				keep_unquantized_statistics (list.get(0), tmin - getAccumError());
			else
				keep_unquantized_statistics (tmin, tmin - getAccumError());
			resetError();
		}
		if (qlist != null) {
			if (lagging_count){
				head = qlist;
				head.add(0, tmin);
			}
		else {
			prev_slur = false;
			head = qlist;
			}
		}
		if (head != null) {
			end_surplus = to - ST.lastElement(head);
			if (end_surplus > 0.0001) {
				resetError();
				slur_p = true;
				head.add(to);
				partition = head;
			}
			else{
				if (to > last_list) 
					resetError();
				partition = head;
			}
				
		}
		
		if (partition == null) {
			if (  ! ST.emptyList(list)  && atimes == null ) {
				keep_unquantized_statistics(to,list.get(list.size() - 1));
				if (list.size() > 2)
					keep_unquantized_statistics(to,list.get(list.size() - 2));
				else
					keep_unquantized_statistics(-1,-1);
				prev_slur = false;
				if (! (last_list == to || last_list == tmin || list.size() > 0))
					setError(to, last_list);
				List<Double> temp = ST.createList(1, tmin);
				temp.add(to);
				rep.a_section = testQuantizeConstraints(temp, tmin, beat_dur, prev_slur);
				rep.slur_p = true;
				rep.prev_slur_p= false;	
				
			}
			else {
				List<Double> temp = ST.createList(1, tmin);
				temp.add(to);
				rep.a_section = testQuantizeConstraints(temp, tmin, beat_dur, prev_slur);
				rep.slur_p = true;
				rep.prev_slur_p= prev_slur;	
			}
		}
		else {
			atimes.quantized_times = partition;
			rep.a_section = atimes;
			rep.slur_p = slur_p;
			rep.prev_slur_p= prev_slur;	
		}
		return rep;
	}
	
	List<Double> get_list_section (List<Double> list, double from, double to) {
		List<Double> rep = new ArrayList<Double>();
		double epsilon = -0.001;
		for (double item : list) {
			if ( item - to >= epsilon)
				break;
			else if ( item - from >= epsilon)
					rep.add(item);
		}
		return rep;
	}
		
	//"Finds the beat list structure corresponding to the quantized durations"
	RTree  search_rythm (Quanta a_section, double tmin, boolean prev_slur){
		return beat_structure (a_section, prev_slur, tmin, ST.lastElement(a_section.quantized_times));
	}
	
	RTree simplify (RTree  beats){
		if (beats.getProporsitons().size() == 1)
			return new RTree(1, beats.cont_p);
		else return beats;
	}
			 
	protected class Rep_getRhytms {
		List<RTree> beats = null;
		List<Double> atimes = null;
		boolean slur_p = false;
		double current_time = 0;
		double beat_dur = 0;
	}
	
	List<RTree> compound_beats (List<RTree> beat_list){
		/*List<RTree> rep  = new ArrayList<RTree>();
		for (RTree item : beat_list) {
			if (item.prop == null)
				item.prop = 1;
			rep.add(item);
		}*/
		return beat_list;
	}
			
	Rep_getRhytms getRhytms (List<Double> notes, double tempo , Fraction sign,
			double start_time, boolean old_slur_p, List<Integer> forbid ) {

		System.out.println("getRhytms ");
		System.out.println("notes " + notes);
		System.out.println("tempo " + tempo);
		System.out.println("sign " + sign);
		System.out.println("start_time " + start_time);
		System.out.println("old_slur_p " + old_slur_p);
		System.out.println("forbid " + forbid);
		
		Rep_getRhytms rep = new Rep_getRhytms();
		long measure_dur = Math.round (240000 * sign.num * (1 / (tempo * sign.denum)));
		List<Double> atimes = notes;
		long beat_dur = Math.round (measure_dur / sign.num);
		int nb_pulse_max = 16;
		double tMin = start_time;
		double fromDur = tMin;
		double toDur = beat_dur + tMin; 
		double measure_end = measure_dur + start_time; 
		List<RTree> beat_rythm_forms = new ArrayList<RTree>();
		int i = 0;
		int max_division = 8; //a faire
		double minimum_quant_dur = 60 / tempo / max_division;
		while ((toDur - fromDur) >= minimum_quant_dur && !ST.emptyList (atimes)) {
			List<Double> partition = get_list_section(notes, fromDur, toDur);
			atimes = atimes.subList(partition.size(), atimes.size());

			//System.out.println("part et atimes "+ partition + " " + atimes );
			Rep_getOptimalTimeSection opt;
			opt = new Rep_getOptimalTimeSection();
			opt = getOptimalTimeSection(partition, toDur, beat_dur, fromDur, old_slur_p);
			//System.out.println("a section "+ opt.a_section );
			if (opt.a_section != null) {
				RTree chosen_rythm = simplify (search_rythm (opt.a_section, fromDur, opt.prev_slur_p ));
				//if (! ST.emptyList(chosen_rythm.getProporsitons())){
					beat_rythm_forms.add(0, chosen_rythm);
					tMin = ST.lastElement(opt.a_section.quantized_times);
				//}
				//else {
					//beat_rythm_forms = null;
					//lancer une exception
				//}
			}
			old_slur_p =  opt.slur_p;
			fromDur = toDur;
			toDur = Math.min(toDur + beat_dur, measure_end);
			i++;
		}
		//System.out.println("beat_rythm_forms "+ beat_rythm_forms );
		//System.out.println("atimes "+ atimes );
		if (ST.emptyList (atimes)) {
			//System.out.println("atimes et old_slur "+ atimes + " " + old_slur_p);
			if (old_slur_p){
				RTree last_beat = beat_rythm_forms.get(0);
				if (last_beat.getProporsitons() != null) {
					RTree last = ST.lastElement(last_beat.getProporsitons());
					if (ST.emptyList(last.getProporsitons()))
						last.prop =  last.prop * -1;
				}
				old_slur_p = false;
				}
				while((measure_end - toDur) >= minimum_quant_dur) {
					beat_rythm_forms.add(0, new RTree(-1, false));
					toDur =  toDur + beat_dur;
				}
			}
		System.out.println("beat_rythm_forms "+ beat_rythm_forms );
		Collections.reverse(beat_rythm_forms);
		rep.beats = compound_beats (beat_rythm_forms);
		rep.atimes = atimes;
		rep.slur_p = old_slur_p;
		rep.current_time = toDur;
		rep.beat_dur = beat_dur;
		return rep;
	}
	/////////	
	class RepPutSilence {
		List<RTree>  beats_with_silences;
		List<Double> modifs; 
		boolean new_silence;
		
	}


	RepPutSilence put_in_silences(List<RTree> beats, List<Double >durs, boolean old_silence) {
		RepPutSilence rep = new RepPutSilence();
		
		return rep;
	}
	
	RTree make_a_measure (List<RTree> beat, long unit ) {
		long num =0;
		for (RTree rt :  beat) {
			num = num + Math.abs(rt.prop);
		}
		return new RTree ( new Fraction(num,unit), beat);
	}
	
	/////////
	List<RTree> doQuantify (List<Integer> durs, List<Double> tempi, List<Fraction> measures,
			List<List<Integer>> max_div, List<List<Integer>> forbid, List<List<Integer>> preci, int offset) {
		//Fileds
		unquantized_notes = 0;
		//
		if (offset != 0)
			durs.add(0, offset * -1 * get_beat_duration(tempi.get(0), measures.get(0).num));
		List<Double> positif_durs = new ArrayList<Double> ();
		durs.forEach((item) -> positif_durs.add((double) Math.abs (item)));
		List<Double> atimes = ST.dxTox(0, positif_durs);
		//Defaults
		Fraction def_measure = ST.lastElement(measures);
		double def_tempo = ST.lastElement(tempi);
		double cur_tempo = def_tempo;
		List<Integer> def_forbid = ST.lastElement(forbid);
		List<Integer> def_max = ST.lastElement(max_div);
		List<Integer> def_precis = ST.lastElement(preci);
		int measure_number = 0;
		double current_time = 0;
		boolean slur_fl =false;
		boolean old_silence =false;
		long cur_unit;
		List<RTree> result = new ArrayList<RTree>();
		
		while (! ST.emptyList (atimes)){
			/*if (measure_number < max_div.size())
				max_division = max_div.get(measure_number);
			else max_division = def_max;
			if (measure_number < preci.size())
				distance_weight = preci.get(measure_number);
			else distance_weight = def_precis;*/
			cur_tempo = def_tempo;
			cur_unit = def_measure.getNumerator();
			Rep_getRhytms rep = getRhytms(atimes, def_tempo, def_measure, current_time, slur_fl, def_forbid);
			atimes = rep.atimes;
			slur_fl = rep.slur_p;
			current_time = rep.current_time;
			//System.out.println("rep gr "+ rep.beats + " " + atimes + " " + slur_fl + " " + current_time );
			if (! ST.emptyList (rep.beats)) {
				System.out.println(rep.beats );
				result.add(make_a_measure(rep.beats, cur_unit));
			}
			else  atimes = null;
		}
		System.out.println("result "+ result);
		return result;
	}
	
	List<RTree> doQuantify (List<Integer> durs, double tempi, Fraction measures, Integer max_div) {
		
		return doQuantify(durs, ST.createList(1, tempi), ST.createList(1, measures),
				ST.createList(1, ST.createList(1, max_div)), ST.createList(1, ST.createList(0, 0)), 
				ST.createList(1, ST.createList(1, 50)), 0);
	}

	public static void main(String[] args) {
		Quantify kant = new Quantify();
		List<Integer> durs = new ArrayList<Integer> ();
		durs.add(1000);durs.add(-1000);durs.add(2000);durs.add(1000);
		durs.add(333);durs.add(-334);durs.add(333);;durs.add(2000);
		System.out.println("rep " + kant.doQuantify(durs, (float) 60, new Fraction(4,4), 8)) ;	
	}

}
