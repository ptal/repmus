package projects.music.classes.abstracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javafx.geometry.Point2D;
import kernel.tools.Fraction;
import kernel.tools.ParseException;
import kernel.tools.ParserListener;
import kernel.tools.ST;
import kernel.tools.generated.grammar1Lexer;
import kernel.tools.generated.grammar1Parser;
import projects.music.classes.interfaces.I_RT;
import projects.music.editors.MusChars;
import projects.music.editors.drawables.Figure;

public class Strie_MO extends MusicalObject implements I_RT {

	double tempo = 60;
	Fraction qdur = new Fraction (1);
	Fraction qoffset = new Fraction (0);

	public Fraction rtdur = new Fraction(1);
	List<RTree> rtproplist = null;
	public RTree tree;

	@Override
	public double getTempo() {
		return tempo;
	}
	@Override
	public void setTempo (double thetempo) {
		tempo = thetempo;
		setOffset(n2ms(qoffset, getTempo()));
		setDuration(n2ms(qdur, getTempo()));
	}
	@Override
	public Fraction getRTdur() {
		return rtdur;
	}
	@Override
	public void setRTdur(Fraction thertdur) {
		rtdur = thertdur;
	}
	@Override
	public List<RTree> getRTproplist() {
		return rtproplist;
	}
	@Override
	public void setRTproplist(List<RTree> theproplist) {
		rtproplist = theproplist;

	}
	@Override
	public Fraction getQDurs() {
		return qdur;
	}
	@Override
	public void setQDurs(Fraction dur) {
		qdur = dur;
		setDuration(n2ms(qdur, getTempo()));

	}
	@Override
	public Fraction getQoffset() {
		return qoffset;
	}
	@Override
	public void setQoffset(Fraction offset) {
		qoffset = offset;
		setOffset(n2ms(qoffset, getTempo()));
	}

	public Fraction getOnsetTotal () {
		if (getFather() == null)
			return qoffset ;
		else
			return Fraction.addition(qoffset , ((Strie_MO) getFather()).getOnsetTotal() );
	}

	@Override
	public long getOnsetMS () {
		return (long) n2ms(getOnsetTotal(), tempo);
	}

	///////////////////////////////////TOOLS////////////////////////////////
	///////////////////////////////////TOOLS////////////////////////////////
	///////////////////////////////////TOOLS////////////////////////////////


	//trouve la puissance de 2 apres  n
	public static long afterPower2 (long n) {
		long exp = 0;
		while (n >= Math.pow(2, exp)) {
			exp++;
		}
		return  Math.round(Math.pow(2, exp));
	}

	//trouve la puissance de 2 avant  n
		public static long beforePower2 (long n) {
			long exp = 0;
			while (n >= Math.pow(2, exp)) {
				exp++;
			}
			return  Math.round(Math.pow(2, exp-1));
		}

		// nombre de beams pour un ration
		public int getBeamsNum(Fraction val){
			long bef = beforePower2(val.getNumerator());
			if (bef == val.getNumerator()) return getStrictBeams (val);
			else if (bef * 3/2 == val.getNumerator())
				return getStrictBeams (new Fraction( bef , val.getDenominator()));
			else if (bef * 7/4 == val.getNumerator())
				return getStrictBeams (new Fraction( bef , val.getDenominator()));
			else return 0;
		}

		public int  getStrictBeams (Fraction val) {
			if (val.equals(Fraction.ONE))
			return -1;
			else if (val.equals(Fraction.f1_2) || val.equals(Fraction.f1_4)) return 0;
			else if (val.equals(Fraction.f1_8)) return 1;
			else if (val.equals(Fraction.f1_16)) return 2;
			else if (val.equals(Fraction.f1_32)) return 3;
			else if (val.equals(Fraction.f1_64)) return 4;
			else if (val.equals(Fraction.f1_128)) return 5;
			else if (val.equals(Fraction.f1_256)) return 6;
			else if (val.equals(Fraction.f1_512)) return 7;
			else if (val.isBinaire ())
				return (int) Math.round((Math.log(val.getDenominator())/Math.log(2)) - 2);
			else
				return 8 ; // tres petit
			}

	//transforme une duree en rondes en ms par rapport a un tempo donnee
	public static long n2ms (Fraction n, double tempo) {return (long) Math.round(n.value() * 4 * (60000/tempo)); };

	//Trouve le valeur symbolic pour un denominateur
	public static long findBeatSymbol (long den) {
		long exp = 0;
		if (den == 5 || den == 6 || den == 7) return 4;
		if (den == 13 || den == 14) return 8;
		while (den >= Math.pow(2, exp)) {
			exp++;
		}
		if (Math.abs(den - Math.pow(2, exp-1)) <= Math.abs(den - Math.pow(2, exp)))
				return Math.round(Math.pow(2, exp-1));
		else
			return Math.round(Math.pow(2, exp));
	}


	//decompose un integer en durees avec figures pour faire la liason
	public static List<Long> decomposeDur (long dur) {
		Long [] simples = new Long [] {(long) 1, (long)2, (long)3, (long)4, (long)6, (long)7, (long)8, (long)12,
				(long)14, (long)16, (long)24, (long)28, (long)32, (long)48, (long)56, (long)64};
		List<Long> fixes = Arrays.asList(simples);
		List<Long> rep = new ArrayList<Long>();
		if (dur == 0) return rep;
		if (fixes.contains(dur))
			rep.add(dur);
		else {
			long inf = beforePower2(dur);
			if (inf == dur || (inf * 1.5) == dur || (inf * 1.75) == dur)
				rep.add(dur);
			else if (dur > (inf * 1.5))  {
				long temp = (long) Math.floor(inf * 1.5);
				rep.add(temp);
				rep.addAll(decomposeDur(dur - temp));
			}
			else {
				rep.add(inf);
				rep.addAll(decomposeDur(dur - inf));
			}
		}
		return rep;
	}

	//decompose un ratio < 1
		public static List<Figure> decomposeFrac (Fraction dur, boolean rest_p) {
			List<Figure> rep = new ArrayList<Figure>();
			long p = dur.getNumerator();
			long q = dur.getDenominator();
			if (p == 0 || q == 0) return rep;
			for (long item : decomposeDur (p))
				rep.add( new Figure (item , false, rest_p, q) );
			return rep;
		}



	///////////////////////////////////////////////
	///SUPER IMPORTANT
	///////////////////////////////////////////////
	// une liste de symbols pour noter une duree avec la ronde comme unite
	public static List<Figure> dur2symbols (Fraction dur)  {
		boolean rest_p = dur.value() < 0;
		List<Figure> rep = new ArrayList<Figure> () ;
		long p = Math.abs(dur.getNumerator());
		long q = dur.getDenominator();
		long b = ST.mod ((int) p, (int) q);
		long a = (long) Math.floor(p/q);
		if (p == 0 || q == 0)
				return rep;
		else if (a == 0)
			return decomposeFrac(new Fraction (b,q), rest_p);
		else {
			for (long item : decomposeDur (a))
				rep.add( new Figure (item, true, rest_p) );
			rep.addAll(decomposeFrac(new Fraction (b,q), rest_p));
			return rep;
		}
	}

}
