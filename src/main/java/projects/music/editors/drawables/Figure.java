package projects.music.editors.drawables;

import gui.renders.I_Render;

import java.util.List;
import gui.CanvasFX;
import com.sun.javafx.geom.Rectangle;

import kernel.tools.Fraction;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_MusicalObject;
import projects.music.classes.music.RChord.RChordDrawable;
import projects.music.classes.music.RNote.RNoteDrawable;
import projects.music.editors.MusChars;
import projects.music.editors.SpacedPacket;

public class Figure implements I_FigureDrawable{
	public boolean big_p; //plus grand que 1

	public Fraction realDur; //subdivision ou multiplication
	public long longTimes;
	
	public boolean rest_p;
	public String head;
	public int points;
	public int beamsnum;
	public boolean stem_p = false;
	public long denom = 1; //a mettre sur le group
	public boolean inGroup = false;
	
	double strSize = 7.176000118255615;
	double spaceRelPosX = 0;
	
	public SimpleDrawable ref = null;

	public Figure (long thedur, boolean big, boolean rest) {
		big_p = big;
		rest_p = rest;
		realDur = new Fraction (thedur); 
		setHeadAndPoints(thedur, 1);
	}

	public Figure (long thedur, boolean big, boolean rest, long thedenom) {
		big_p = big;
		rest_p = rest;
		denom = thedenom;
		realDur = new Fraction (thedur, thedenom); 
		long symb_denom = Strie_MO.findBeatSymbol(denom);
		setHeadAndPoints(thedur, symb_denom);
	}
	
	public double getStrSize (int size) {
		return  strSize * (size/24) ;
	}
	

	@Override
	public int getBeamsNum() {
		return beamsnum;
	}

	@Override
	public double getHeadSize(int size) {
		return strSize * (size/24) ;
	}
	
	public boolean hasDenom () {
		Fraction frac = new Fraction (1, denom);
		return ! (denom == 1 || frac.isBinaire()) ;
	}
	

	public long getDurMS (double tempo) {
		return Strie_MO.n2ms(realDur, tempo);
	}
		
	public String toString ( ){
		return "head " + head +" big_p " + big_p + " denom : " + denom + " beamsnum : " + beamsnum + " points : " + points + "\n";
	}

	public void setHeadAndPoints(long dur, long symb_denom){
		Fraction thedur = new Fraction (dur, symb_denom);
		long bef = Strie_MO.beforePower2(thedur.getNumerator());
		if (bef == thedur.getNumerator()){
			head = noteStrictChar (thedur);
			points = 0;
		}
		else if (bef * 3/2 == thedur.getNumerator()) {
			head = noteStrictChar (new Fraction( bef , thedur.getDenominator()));
			points = 1;
		}
		else if (bef * 7/4 == thedur.getNumerator()) {
			head = noteStrictChar (new Fraction( bef , thedur.getDenominator()));
			points = 2;
		}
	}

	public String noteStrictChar (Fraction val) {
		if (rest_p) {
			double value = Math.abs(val.value());
			if (value > 4) {
				longTimes = (long) value / 4;
				return "" + ((long) value);
				}
				else if (val.equals(Fraction.FOUR)) {strSize=4.1519999504089355; return MusChars.rest_4;}
				else if (val.equals(Fraction.TWO))	{strSize=4.1519999504089355; return MusChars.rest_2;}
				else if (val.equals(Fraction.ONE))  {strSize=7.271999835968018; return MusChars.rest_1;}
				else if (val.equals(Fraction.f1_2)) {strSize=7.271999835968018; return MusChars.rest_1_2;}
				else if (val.equals(Fraction.f1_4)) {strSize=7.679999828338623;  return MusChars.rest_1_4;}
				else if (val.equals(Fraction.f1_8)) {strSize=7.464000225067139; beamsnum = 1; return MusChars.rest_1_8;}
				else if (val.equals(Fraction.f1_16)) {strSize=7.464000225067139; beamsnum = 2; return MusChars.rest_1_16;}
				else if (val.equals(Fraction.f1_32)) {strSize=10.392000198364258; beamsnum = 3; return MusChars.rest_1_32;}
				else if (val.equals(Fraction.f1_64)) {strSize=14.928000450134277; beamsnum = 4; return MusChars.rest_1_64;}
				else if (val.equals(Fraction.f1_128)) {strSize=14.928000450134277; beamsnum = 5; return MusChars.rest_1_128;}
				else {strSize=14.928000450134277; return MusChars.rest_1_128;}
			}
		else if (val.value() >= 8) {
			longTimes = (long) val.value() / 8;
			strSize=15.64799976348877;
			return MusChars.head_8;
			}
			else if (val.equals(Fraction.FOUR)) {strSize=12.64799976348877; return MusChars.head_4;}
			else if (val.equals(Fraction.TWO)) {strSize=12.64799976348877; return MusChars.head_2;}
			else if (val.equals(Fraction.ONE)) {strSize=9.9600000381469735; return MusChars.head_1;}
			else if (val.equals(Fraction.f1_2)) {strSize=7.176000118255615; stem_p = true; return MusChars.head_1_2;}
			else if (val.equals(Fraction.f1_4)) {strSize=7.176000118255615; stem_p = true;  return MusChars.head_1_4;}
			else if (val.equals(Fraction.f1_8)) {strSize=7.176000118255615; stem_p = true; beamsnum = 1; return MusChars.head_1_4;}
			else if (val.equals(Fraction.f1_16)) {strSize=7.176000118255615; stem_p = true; beamsnum = 2; return MusChars.head_1_4;}
			else if (val.equals(Fraction.f1_32)) {strSize=7.176000118255615; stem_p = true; beamsnum = 3; return MusChars.head_1_4;}
			else if (val.equals(Fraction.f1_64)) {strSize=7.176000118255615; stem_p = true; beamsnum = 4; return MusChars.head_1_4;}
			else if (val.equals(Fraction.f1_128)) {strSize=7.176000118255615; stem_p = true; beamsnum = 5; return MusChars.head_1_4;}
			else {strSize=7.176000118255615; stem_p = true; beamsnum = 6; return MusChars.head_1_4;}
		}
	
	@Override
	public void computeCX(SpacedPacket pack, int size) {
		if (ref instanceof RNoteDrawable) {
			RNoteDrawable thenote = (RNoteDrawable) ref;
			if (thenote.domain == null) {
				double x0 = pack.start;
				double strsize = getStrSize(size);

				double deltal = (thenote.deltaAlt * strsize * 3) / 10;
				double deltar = strsize + (thenote.deltaHead * strsize);

				pack.updatePacket(0, 0, deltal, deltar);
				thenote.setRectangle(thenote.x(), thenote.y(), deltal+deltar - x0 + strsize, thenote.h());
			}
		}
		else if (ref instanceof RChordDrawable) {
			RChordDrawable thechord = (RChordDrawable) ref;
			double x0 = pack.start;
			double deltal = 0;
			double deltar = 0;
			double strsize = getStrSize(size);
			for (I_Drawable note : thechord.getInside()) {
				RNoteDrawable thenote = (RNoteDrawable) note;
				deltal = Math.max( deltal , (thenote.deltaAlt * strsize * 3) / 10 );
				deltar = Math.max( deltar ,  (thenote.deltaHead * strsize));
			}
			pack.updatePacket(0, 0, deltal, deltar + strsize);
			thechord.setRectangle(thechord.x(), thechord.y(), deltal + deltar - x0 + strsize, thechord.h());
		}
		
	}

	@Override
	public double getCX() {
		return spaceRelPosX;
	}

	@Override
	public void setCX(double cx) {
		spaceRelPosX = cx;	
	}


	@Override
	public void drawObject(I_Render g,  Rectangle rect,
			List<I_Drawable> selection, double x0, double deltax, double deltay) {
		// TODO Auto-generated method stub
	}

	@Override
	public void consTimeSpaceCurve(int size, double x, int zoom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public I_Drawable getClickedObject(double x, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawContainersObjects(I_Render g,
			Rectangle rect, List<I_Drawable> selection, double deltax) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double h() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double w() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double x() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double y() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<I_Drawable> getInside() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFather(ContainerDrawable thefather) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public I_MusicalObject getRef() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void collectTemporalObjects(List<SpacedPacket> timelist) {}

	@Override
	public boolean firstOfChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean lastOfChildren() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public double getStemSize (int beamsnumber, int size) {
		double stemsize = (size/8) * 7;
		if (beamsnumber == 0)
			return stemsize;
		else return stemsize + (beamsnumber*size/4);
	}

	@Override
	public double getUpYpos(int size) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getDwnYpos(int size) {
		// TODO Auto-generated method stub
		return 0;
	}

}


