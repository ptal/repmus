package projects.music.editors;

import gui.FX;
import gui.renders.I_Render;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import kernel.tools.ParseException;
import kernel.tools.ParserListener;
import kernel.tools.ST;
import kernel.tools.generated.grammar1Lexer;
import kernel.tools.generated.grammar1Parser;


public  class StaffSystem {
	
	
	////////////////////////////////////////////
	
	public static class Key {

		String key = MusChars.key_g;
		int keyline = 4;
		Rectangle rect;
		
		Key (String theKey, int theKeyline){
			key = theKey;
			keyline = theKeyline;
		}
		
		public void draw (I_Render g, double x, double y, double dent){
			if (key.equals(MusChars.key_f))
					FX.omDrawString(g, x+dent , y+ dent, key);
			else if (key.equals(MusChars.key_g))
				 	FX.omDrawString(g, x+dent , y+ 3*dent, key); 
			else if (key.equals(MusChars.key_c))
			 	FX.omDrawString(g, x+dent , y+ (5 - keyline)*dent, key); 
			else if (key.equals("p")){
				FX.omFillRect(g, x+dent , y+dent, dent/2, 2*dent);
				FX.omFillRect(g, x+dent+dent , y+dent, dent/2, 2*dent);
			}

		}
	}

	////////////////////////////////////////////
	 public static class SimpleStaff {
	   Key theKey;
	   int numlines;
	   Point2D range;
	   String strStaff;
	   double pixelStartY = 0; 
	   double pixelStartX = 0;
	   double pixelEndY = 0;
	   int do4SpacesFromTop;
	   int interline = 0; // en dents
	   
	   double startY = 0;
	   
	   
	   SimpleStaff (String staff){
	    	strStaff = staff;
			theKey = keyFromString();
			range = rangeFromString();
			numlines = linesFromString();
			do4SpacesFromTop = getSpaceFromTop();
		}
	   
	   public double getTopPixel (int size) {
			return startY * size/4;
		}
	   
	   public double getBottomPixel (int size) {
			return (startY + 4) * size/4 ;
	   }
	    
	    public  Key keyFromString (){
			Key rep;
			switch (strStaff) { 
			case "F": 	rep = new StaffSystem.Key(MusChars.key_f, 4); break; 
			case "U1": 	rep = new StaffSystem.Key(MusChars.key_c, 1); break;
			case "U2": 	rep = new StaffSystem.Key(MusChars.key_c, 2); break;
			case "U3": 	rep = new StaffSystem.Key(MusChars.key_c, 3); break;
			case "U4": 	rep = new StaffSystem.Key(MusChars.key_c, 4); break;
			case "G": rep = new StaffSystem.Key(MusChars.key_g, 4); break; 
			case "F2": 	rep =new StaffSystem.Key(MusChars.key_f, 4); break; 
			case "G2": rep = new StaffSystem.Key(MusChars.key_g, 4); break;
			case "P": 	rep = new StaffSystem.Key("p", 4); break; 
			case "L": rep = new StaffSystem.Key("r", 0); break; 
			case "empty": rep = new StaffSystem.Key("r", 0); break;
			default: rep = new StaffSystem.Key("r", 0);
			}
			return  rep;
	   }
	 
	 public  int linesFromString (){
		 	int rep = 5;
			switch (strStaff) { 
				case "L": rep = 1; break;
				case "empty": rep = 0; break;
				default: rep = 5;
			}
			return rep;
		   }
	 
	 public  Point2D rangeFromString (){
		 Point2D rep;
		 switch (strStaff) { 
			case "F": 	rep = new Point2D (43,57); break; 
			case "G": rep = new Point2D (64,77); break; 
			case "F2": rep = new Point2D (19,33); break;
			case "G2": rep = new Point2D (88,101); break;
			case "P" : 	rep = new Point2D (64,77); break; 
			case "U1" : 	rep = new Point2D (60,74); break; 
			case "U2" : 	rep = new Point2D (57,71); break; 
			case "U3" : 	rep = new Point2D (54,67); break; 
			case "U4" : 	rep = new Point2D (50,64); break; 
			default: rep = new Point2D (0 ,0);
			}
		 return rep;
	}
	 
	 public int getDoFromTop() {
	 	return do4SpacesFromTop;
	 }
	 
	 public  int getSpaceFromTop (){
		 int rep;
		 switch (strStaff) { 
			case "F": 	rep = -2; break; 
			case "G": rep = 10; break; 
			case "F2": rep = -16; break;
			case "G2": rep = 24; break;
			case "P" : 	rep = 10; break;
			case "U1" : rep = 8; break; 
			case "U2" : rep = 6; break; 
			case "U3" : rep = 4; break; 
			case "U4" : rep = 2; break; 
			default: rep = 0;
			}
		 return rep;
	}
	    
	public int getHeight (){
	    	int rep;
			 switch (strStaff) { 
				case "F" : 	rep = 7; break;
				case "U1" : 	rep = 7; break; 
				case "U2" : 	rep = 7; break; 
				case "U3" : 	rep = 7; break; 
				case "U4" : 	rep = 7; break; 
				case "G": rep = 6; break; 
				case "F2": rep = 4; break;
				case "G2": rep = 7; break;
				default: rep = 4;
				}
			 return rep;
	}
	    
	public void setInitEndY (double y0){
		startY = y0;
	}
	
	public void draw(I_Render g, double x, double y, double w, double dent, 
			boolean tempo, boolean selected_p) {
	    	double posy = y;
			 for (int i = 0; i < numlines ; i++) {
				 FX.omDrawLine (g, x , posy, x+w, posy);
				 posy = posy + dent;
			 }
			 theKey.draw(g,x,y,dent);
			 pixelStartX = x;
			 pixelStartY = y;
			 pixelEndY = posy - dent;
	}
	    
	    public  boolean ClickInKey  (int x, int y, int size){
	    	Rectangle rect = new Rectangle ((int) pixelStartX , 
	    									(int) getTopPixel(size), 
	    									size, size);
			return rect.contains(x,y);
		 }
	    
	    public String toString ( ) {
	    	return strStaff;
		}
	}
	
	////////////////////////////////////////////

	public static class MultipleStaff  implements I_StaffLine {
		boolean selected = false;
		List<SimpleStaff> staffs = new ArrayList<SimpleStaff>();;
		Point2D range;
		String staffname;
		int interspace = 4; //in dents
		   
	public MultipleStaff (String name) {
		staffname = name;
		List<String> theStaffs = new ArrayList<String>();
		List<Integer> interlines = new ArrayList<Integer>();
		switch (name) { 
			case "F": theStaffs.add("F"); break; 
			case "G": theStaffs.add("G"); break; 
			case "F2": 	theStaffs.add("F2"); break; 
			case "G2": theStaffs.add("G2"); break;
			case "GF": theStaffs.add("G"); theStaffs.add("F"); interlines.add(2); break; 
			case "GG": theStaffs.add("G2"); theStaffs.add("G");  interlines.add(3); break;
			case "FF": 	theStaffs.add("F"); theStaffs.add("F2");  interlines.add(3);  break; 
			case "GFF": theStaffs.add("G");
						theStaffs.add("F");
						theStaffs.add("F2"); interlines.add(2); interlines.add(3); break; 
			case "GGF": theStaffs.add("G2");
						theStaffs.add("G");
						theStaffs.add("F");  interlines.add(3); interlines.add(2); break; 
			case "GGFF": theStaffs.add("G2");
						theStaffs.add("G");
						theStaffs.add("F");
						theStaffs.add("F2"); 
						interlines.add(3); interlines.add(2); 
						interlines.add(3); break;
			case "U1": theStaffs.add("U1"); break;
			case "U2": theStaffs.add("U2"); break;
			case "U3": theStaffs.add("U3"); break;
			case "U4": theStaffs.add("U4"); break;
			case "P":  theStaffs.add("P"); break; 
			case "L": theStaffs.add("L"); break;
			case "empty": theStaffs.add("empty"); break;
			default: theStaffs.add("F");
			}
			double min = 256;
			double max = 0;
			int i = 1;
			int len = theStaffs.size();
			for (String staff : theStaffs) {
				SimpleStaff theStaff = new SimpleStaff(staff);
				staffs.add(theStaff);
				if (i < len)
					theStaff.interline = interlines.get(i-1);
				i++;
				min = Math.min(min, theStaff.range.getX());
				max = Math.max(max, theStaff.range.getY());
			}
			range = new Point2D(max, min);
		}

	public void setSelected (boolean val) {
			selected = val;
	}

	public boolean getSelected () {
		return selected;
	}

	public Point2D getRange () {
		return range;
	}

	public double getTopPixel (int size) {
		return staffs.get(0).getTopPixel(size);
	}

	public double getBottomPixel (int size) {
		return staffs.get(staffs.size()-1).getBottomPixel(size);
	}

	// il faut changer et dire que le start est le DO 6000
	public int getMidicFromY (int size, double y) {
			   int dent = size/4;
			   double oneDemiTonePixel = (dent * 3.5) / 12; 
			   double doPixels = getTopPixel(size) + (getDoFromTop() * (dent / 2));
			   double deltaPixels = doPixels - y;
			   int Octaves = (int) Math.floor (deltaPixels / (dent * 3.5) );
			   double octavePixel =  (doPixels + ( (5 - Octaves) * dent * 3.5));
			   double tonesPixels = octavePixel - (y + getTopPixel(size)) ;
			   int demiTones = (int) Math.round ( tonesPixels / oneDemiTonePixel);
			   int [] tonearray =  {0, 1, 2, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 11}; 
			   return (Octaves * 1200) + (tonearray[demiTones] * 100); 
	}

	public int getDemiDentsFromTop (int midi, Scale scale, int index) {
			   int doPosition = getDoFromTop(); 
			   int octaves = (int) Math.floor (midi / 12);
			   int octavePosition =  doPosition + ((5 - octaves) * 7);
			   return octavePosition - scale.getLines()[index]; 
	}

	public int DemiDents (int midic, Scale scale){
				int cents = ST.mod (midic , 1200);
				int index = ST.mod (Math.round (cents / scale.getApprox()), scale.getLines().length);
				int midi = Math.round(midic / 100);
				return getDemiDentsFromTop ( midi, scale, index);
	}

	public  int getDoFromTop (){
			   return staffs.get(0).getDoFromTop();
	}

	public int getHeight (){
			   int rep = 0;
			   for (SimpleStaff staff : staffs) {
				   rep =  rep + 4 + staff.interline;
				}
			   return rep;
	}

	public void setInitEndY (double y){
			   for (SimpleStaff staff : staffs) {
				   staff.setInitEndY(y);
				   y =  y + staff.getHeight();
				}
	}

	public double getMidiCenter (){
			   return range.getX() +  ((range.getY() - range.getX()) / 2);
	}

	public int getPosY (Scale scale, int midic){
				int cents = ST.mod (midic , 1200);
				int index = ST.mod (Math.round (cents / scale.getApprox()), scale.getLines().length);
				int midi = Math.round(midic / 100);
				return this.getDemiDentsFromTop ( midi, scale, index);
	}

	public void draw(I_Render g, double x, double y, double w, double dent, boolean tempo, boolean selected_p) {
			   double posy = y;
			   FX.omSetColorStroke(g, staffcolor);
				if (selected) FX.omSetColorStroke(g, selectedcolor);
				 for (SimpleStaff staff : staffs) {
					 staff.draw(g,x,posy, w, dent, tempo, selected_p || selected);
					 posy = posy + (dent * (4 + staff.interline));
				 }

				 FX.omDrawLine (g, staffs.get(0).pixelStartX , staffs.get(0).pixelStartY, 
						 staffs.get(0).pixelStartX, staffs.get(staffs.size() -1).pixelEndY);
				 FX.omDrawLine (g, staffs.get(0).pixelStartX + w , staffs.get(0).pixelStartY, 
						 staffs.get(0).pixelStartX + w, staffs.get(staffs.size() -1).pixelEndY);
				 if (selected) FX.omSetColorStroke(g, selectedcolor);
	}

	public  MultipleStaff ClickInKey  (double x, double y, int size){
				 boolean rep = false;
				 MultipleStaff repons =null;
				 for (SimpleStaff staff : staffs) {
					 if (rep == false) {
						 rep = staff.ClickInKey((int) x,(int) y, size);
					 }
				 }
				 if (rep) repons = this;
				 return repons;
			 }
		   
		   public  void offSelected  (){
				 selected = false;
			 }
		   
		   public String toString ( ) {
			return staffname + " ";
			}

		@Override
		public int getInterspace() {
			return interspace;
		}
	}
	
	////////////////////////////////////////////
	public static class StaffGroup implements I_StaffLine {
		
		List<I_StaffLine> staffLines = new ArrayList<I_StaffLine>();
		boolean selected = false;
		
		public StaffGroup (List<I_StaffLine> lines) {
			staffLines = lines;
		}
		
		public List<MultipleStaff> getStaffs () {
			List<MultipleStaff> rep = new ArrayList<MultipleStaff> ();
			for (I_StaffLine line : staffLines) 
				if (line instanceof MultipleStaff)
					rep.add((MultipleStaff) line);
				else
					rep.addAll(((StaffGroup) line).getStaffs());
			return rep;
		}
		
		public double getTopPixel (int size) {
			return getStaffs().get(0).getTopPixel(size);
		}
		
		public double getBottomPixel (int size) {
			List<MultipleStaff> staffs = getStaffs();
			return staffs.get(staffs.size()-1).getBottomPixel(size);
		}
		
		public int getHeight (){
			   int rep = 0;
			   for (I_StaffLine staff : staffLines) {
				   rep =  rep + staff.getHeight();
				}
			   return rep;
		}
		
		public void setInitEndY (double y){
			   for (I_StaffLine staff : staffLines) {
				   staff.setInitEndY(y);
				   y =  y + staff.getHeight() + staff.getInterspace();
				}
		}
		
		public int getInterspace () {
			List<MultipleStaff> staffs = getStaffs();
			return staffs.get(staffs.size()-1).getInterspace();
		}
		
		public void draw(I_Render g, double x, double y, double w, double dent, boolean tempo, boolean selected_p) {
			double posy = y;
			 FX.omSetColorStroke(g, staffcolor);
			 if (selected) FX.omSetColorStroke(g, selectedcolor);
			 for (I_StaffLine staff : staffLines) {
				 staff.draw(g,x,posy, w, dent, tempo, selected_p || selected);
				 posy = posy + dent * (staff.getHeight() + staff.getInterspace());
			 }
			 FX.omOpenBrace(g, x - dent - 1, 
					 getTopPixel((int) dent*4), 
					 dent, 
					 getBottomPixel((int) dent*4) - getTopPixel((int) dent*4));
			 FX.omSetColorStroke(g, staffcolor);
		 }
		
		 public  MultipleStaff ClickInKey  (double x, double y, int size){
			 MultipleStaff rep = null;
			 for (I_StaffLine staff : staffLines) {
				 if (rep == null){
					 rep = staff.ClickInKey(x,y, size);
				 }
			 }
			 return rep;
		 }
		 
		 public  void offSelected  (){
			 selected = false;
			 for (I_StaffLine staff : staffLines) {
				 staff.offSelected();
			 }
		 }
		 
		public String toString ( ) {
			String rep = "{ ";
			for (I_StaffLine staff : staffLines) {
				 rep =  rep + staff;
			 }
			rep = rep + " }" ;
		return rep;
		}
	}
	
	////////////////////////////////////////////
	List<I_StaffLine> staffLines = new ArrayList<I_StaffLine>();
	boolean selected = false;
	static Color staffcolor = new Color (0,0,0,1);
	static Color selectedcolor = new Color (0,0,0,0.5);
	double xmarge = 1;
	double ymarge = 2;
	
	public StaffSystem (List<I_StaffLine> lines) {
		staffLines = lines;
	}
	
	public  StaffSystem (String arg)  {
		StaffSystem copy = getStaffSystem(arg);
		staffLines = copy.getStaffLines();
	}
	
	public double getXmarge () {
		 return xmarge;
	}
	
	public double getYmarge () {
		 return xmarge;
	}
	
	public List<I_StaffLine> getStaffLines () {
		return staffLines;
	}
	
	public List<MultipleStaff> getStaffs () {
		List<MultipleStaff> rep = new ArrayList<MultipleStaff> ();
		for (I_StaffLine line : staffLines) 
			if (line instanceof MultipleStaff)
				rep.add((MultipleStaff) line);
			else
				rep.addAll(((StaffGroup) line).getStaffs());
		return rep;
	}
	
	public void setMarges (double x, double y) {
		 xmarge = x;
		 ymarge = y;
		 int y0 = (int) y*4;
		 for (I_StaffLine group : staffLines) {
			 group.setInitEndY(y0);
			 y0 = y0 + group.getHeight() + group.getInterspace();
		}
	}
	
	public double getTopPixel (int size) {
		return getStaffs().get(0).getTopPixel(size);
	}
	
	public double getBottomPixel (int size) {
		List<MultipleStaff> staffs = getStaffs();
		return staffs.get(staffs.size()-1).getBottomPixel(size);
	}
	
	public int getHeight (){
		   int rep = 0;
		   for (I_StaffLine group : staffLines) {
			   rep =  rep + group.getHeight();
			}
		   return rep;
	}
	 
	public void draw(I_Render g, double x, double y, double w, double dent, boolean tempo, boolean selected_p, int size) {
		 FX.omSetColorStroke(g, staffcolor);
		 if (selected) FX.omSetColorStroke(g, selectedcolor);
		 double posy = y + ymarge*size;
		 double posx = x + xmarge*size;
		 for (I_StaffLine line : staffLines) {
			 line.draw(g, posx, posy, w, dent, tempo, selected);
			 posy = posy + dent * (line.getHeight() + line.getInterspace());
		 }
		 FX.omSetColorStroke(g, staffcolor);
	 }
	 
	 public  MultipleStaff ClickInKey  (double x, double y, int size) {
		 MultipleStaff rep = null;
		 for (I_StaffLine group : staffLines) {
			 if (rep == null){
				 rep = group.ClickInKey(x,y, size);
			 }
		 }
		 return rep;
	 }
	 
	 public  void offSelected  (){
		 selected = false;
		 for (I_StaffLine group : staffLines) {
			 group.offSelected();
		 }
	 }
	 
	public String toString ( ) {
		String rep = "[ ";
		for (I_StaffLine group : staffLines) {
			 rep =  rep + group;
		 }
		rep = rep + " ]" ;
	return rep;
	}
	 
	 /////
	
	public static StaffSystem getStaffSystem (String name) {
		try {
			return parseStaff (name);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static StaffSystem parseStaff (String arg) throws ParseException  {
		try {
			ANTLRInputStream in = new ANTLRInputStream(arg);
			grammar1Lexer lexer = new grammar1Lexer(in);
			CommonTokenStream tokens =	new CommonTokenStream(lexer);
			grammar1Parser parser =	new grammar1Parser(tokens);
			grammar1Parser.SystemContext system = parser.system();
			ParseTreeWalker walker = new ParseTreeWalker();
			ParserListener extractor = new ParserListener();
			walker.walk(extractor, system);	
			return  (StaffSystem) system.rep;
		} catch (Exception e) {
			System.out.print ("e " + e + " " + e.getMessage());
			throw e;
		}
	}
	 
	 public static int setAuxLines (int midic, MultipleStaff staff){
			int midi = Math.round(midic / 100);
			int rep = -1;
			int top = (int) staff.getRange().getX();
			int bottom = (int) staff.getRange().getY();
			if (midi > top)
				rep = 0;
			else if (midi < bottom) 
				rep = 1;
			return rep;
	}
	 
	 public static void drawAuxLines (I_Render g, int auxlines, int size, double xPos, double yPos, MultipleStaff staff, double headsize) {
			if (auxlines != -1) {
				int dent = size/4;
				if (auxlines == 0) { //up
					double top = staff.getTopPixel(size) - dent;
					while (top >= yPos) {
						FX.omDrawLine(g, xPos - (size/8) , top, xPos+ headsize+ size/8 , top);
						top = top - dent;
					}
				}
				else {
					double botton = staff.getBottomPixel(size) + dent;
					while (botton <= yPos) {
						FX.omDrawLine(g, xPos - (size/8) , botton , xPos+ headsize+ size/8  , botton);
						botton = botton + dent;
					}	
				}
			}
		}
	 
}



