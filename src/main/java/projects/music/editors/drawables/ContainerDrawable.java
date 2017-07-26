package projects.music.editors.drawables;

import gui.FX;
import gui.renders.I_Render;
import gui.CanvasFX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import kernel.tools.ParseException;
import kernel.tools.ParserListener;
import kernel.tools.ST;
import kernel.tools.generated.grammar1Lexer;
import kernel.tools.generated.grammar1Parser;

import com.sun.javafx.geom.Rectangle;

import projects.music.classes.abstracts.Compose_L_MO;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;

public class ContainerDrawable extends SimpleDrawable{

		public List<I_Drawable> inside = new ArrayList<I_Drawable>();		
		
		public List<SpacedPacket> timespacedlist;
		
		public List<I_Drawable> getInside() {
			return inside;
		}
		
		public class Pair {
		    private Long l;
		    private Double r;
		    public Pair(Long l, Double r){
		        this.l = l;
		        this.r = r;
		    }
		    public Long getTime(){ return l; }
		    public Double getPixel(){ return r; }
		    public String toString ( ) {return "( " + l + " , " + r + " )";}
		}
		
		public static List <Pair> bpftime;
		
		//On va dessiner en deux pas
		//d'abord on fait dans le temps avec drawObject
		//apres on fera par hierarchie avec drawContainersObjects
		@Override
		public void drawObject(I_Render g,  Rectangle rect,
				 List<I_Drawable> selection, double packStart, 
				double deltax, double deltay) {
			if (isRoot_p ()) {
				for (SpacedPacket packed : timespacedlist) {
					for (I_Drawable obj : packed.objectlist ) {
						obj.drawObject (g,  rect, selection, packed.start, deltax, deltay);
					}
				}
			}
			drawContainersObjects(g,  rect, selection, deltax);
			drawExtras(g, deltax);

		/*	System.out.println("bpftime " + bpftime);
			int size = params.fontsize.get();
			Font thefont = params.getFont("normal2.3Size");
			Font oldfont = FX.omGetFont(g);
			FX.omSetFont(g, thefont);
			for (int i = 0; i < 20000; i = i+ 1000) {
				double x = deltax + time2pixel(i, size);
				FX.omDrawLine(g, x, 0, x, panel.h());

				FX.omDrawString(g, x, 50, Math.round(x-deltax)+"");
				FX.omDrawString(g, x, 60, Math.round(interpole(i))+"");
				FX.omDrawString(g, x, 70, i+"");
			}
			FX.omSetFont(g, oldfont);*/
		}
		

		public void drawContainersObjects (I_Render g, Rectangle rect,
				 List<I_Drawable> selection, double deltax) {
				for (I_Drawable obj : getInside()) {
					obj.drawContainersObjects(g, rect, selection, deltax);
				}
				collectRectangle();
		}
		
		/*public void drawSelection(GraphicsContext g, List<MusicalObject> selection, int size){
			if  (selection.contains(ref))
				drawRectSelection(g);
			for (SimpleDrawable note : getInside()) {
				note.drawSelection(g,  selection,  size);
			}
		}
		*/
		
		public void collectRectangle () {
			double x = Integer.MAX_VALUE;
			double y = Integer.MAX_VALUE;
			double w = 0;
			double h = 0;
			for (I_Drawable obj : getInside()) {
				x = Math.min(x, obj.x());
				y = Math.min(y, obj.y());
				w = Math.max(w, obj.x() + obj.w());
				h = Math.max(h, obj.y() + obj.h());
				}
			setRectangle( x, y,  w-x, h-y);
		}
	
	
		///////////SPACING//////////
		
		public static Comparator<SpacedPacket> packetordered = new Comparator<SpacedPacket>() {

			public int compare(SpacedPacket p1, SpacedPacket p2) {
			   long t1 = p1.time;
			   long t2 = p2.time;
			   int rep;
			   if (t1 > t2) rep = 1;
			   else if (t2 > t1) rep = -1;
			   else rep =0;
			   return  rep;
		   }};
			
		  
		@Override
		public void collectTemporalObjects(List<SpacedPacket> timelist) {
			for (I_Drawable obj : getInside()) {
				obj.collectTemporalObjects(timelist);
			}
		}
		
		
		public void makeSpaceObjectList () {
			timespacedlist = new ArrayList<SpacedPacket>();
			collectTemporalObjects(timespacedlist);
			Collections.sort(timespacedlist, packetordered);
			groupTemporalObjects ();
		}
		
		private void groupTemporalObjects () {
			List<SpacedPacket> rep = new ArrayList<SpacedPacket>();
			SpacedPacket curpacket = null;
			for (SpacedPacket packet : timespacedlist) {
				 if (curpacket == null) {
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time, false);
					 if (packet.strie_p) curpacket.strie_p = true;
				 }
				 else if (packet.time == curpacket.time) {
					 curpacket.objectlist.add(packet.objectlist.get(0));
				 	 if (packet.strie_p) curpacket.strie_p = true;
				 }
				 else {
					 rep.add(curpacket);
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time, false);
					 if (packet.strie_p) curpacket.strie_p = true;
				 }
			}
			if (curpacket != null) rep.add(curpacket);
			timespacedlist = rep;
		}
		
		/////////
		public void consTimeSpaceCurve (int size, double x, int zoom) {
			long lasttime = 0;
			double lastspace = x;
			SpacedPacket packed;
			double newspace = 0;
			int len = timespacedlist.size();
			List<SpacedPacket> Stimespacedlist = new ArrayList<SpacedPacket> ();
			List<SpacedPacket> Ltimespacedlist = new ArrayList<SpacedPacket> ();
			for (SpacedPacket item : timespacedlist)  
				if (item.strie_p) Stimespacedlist.add(item);
				else Ltimespacedlist.add(item);
			int lens = Stimespacedlist.size();
			int lenl = Ltimespacedlist.size();
			for (int i = 0; i < lens; i++) {
				packed = Stimespacedlist.get(i);
				if (i+1 < lens) {
					newspace = ryhtm2pixels(Stimespacedlist.get(i+1).time - packed.time, size) ;
					System.out.println ("newspace" + newspace + "1333 " + ryhtm2pixels(1333, size));
				}
				else 
					newspace = size;
				packed.start = lastspace;
				packed.space = newspace;
				packed.spacePacket(size);
				//lasttime = packed.time;
				lastspace = lastspace + packed.space;
			}
			makeBpfTime(Stimespacedlist);
			for (int i = 0; i < lenl; i++) {
				packed = Ltimespacedlist.get(i);
				packed.start = time2pixel(packed.time, size);
				packed.space = size/2;
				packed.spacePacket(size);
			}
			if (Stimespacedlist.size() > 0) {
				lastspace = x;
				for (int i = 0; i < len; i++) {
					packed = timespacedlist.get(i);
					packed.start = lastspace;
					lastspace = lastspace + packed.space;
				}
			}
			makeBpfTime(timespacedlist);
		}
		
		public void makeBpfTime (List<SpacedPacket> list) {
	        bpftime = new ArrayList <Pair> ();
	        for (SpacedPacket item : list) {
	        	bpftime.add(new Pair(item.time, item.getCX()));
	        }
	    }
		
		public static double time2pixel (long timeMS, int size) {
			double rep;
			if (bpftime.size() > 0) {
				Pair last = ST.lastElement(bpftime);
				long maxtime = last.getTime();
				long mintime = bpftime.get(0).getTime();
				if (timeMS > maxtime) {
					rep = interpole(maxtime) + ((timeMS - maxtime)/ size);
				}
				else if (timeMS < mintime) {
					rep = interpole(mintime) + ((mintime - timeMS ) / size);
				}
				else {
				 rep = interpole(timeMS);
				}
			} else rep = timeMS / size;
			return rep;
		}
		
		public static double interpole (long t) {
			int len = bpftime.size();
			long x1 = -1;
			long x2 = -1;
			double y1 = -1;
			double y2 = -1;
			for (int i = 0; i<len; i++) {
				Pair curpoint = bpftime.get(i);
				if (t == curpoint.getTime())
					return curpoint.getPixel();
				else if (curpoint.getTime() > t) {
							x1 = curpoint.getTime();
							y1 = curpoint.getPixel();
							if ( i+1 < len) {
								x2 =  bpftime.get(i+1).getTime();
								y2 =  bpftime.get(i+1).getPixel();
							}
							break;
						}
				}
			return  ST.linearInterpol(x1*1.0, x2*1.0, y1, y2, t);
		}
		
		public static long interpole1 (double pixel) {
			int len = bpftime.size();
			long x1 = -1;
			long x2 = -1;
			double y1 = -1;
			double y2 = -1;
			for (int i = 0; i<len; i ++) {
				Pair curpoint = bpftime.get(i);
				if (pixel == curpoint.getPixel())
					return curpoint.getTime();
				if (curpoint.getPixel() > pixel) {
					y1 = curpoint.getPixel();
					x1 = curpoint.getTime();
					if ( i+1 < len) {
						x2 =  bpftime.get(i+1).getTime();
						y2 =  bpftime.get(i+1).getPixel();
					}
					break;
				}
			}
			return (long)  ST.linearInterpol( y1, y2, x1*1.0, x2*1.0, pixel);
		}
		
		public static long pixel2time (double x, int size) {
			long rep;
			if (bpftime.size() > 0) {
				Pair last = ST.lastElement(bpftime);
				double maxpix = last.getPixel();
				double minpix = bpftime.get(0).getTime();
				if (x > maxpix) {
					rep = (long) (interpole1(maxpix) + ((x - maxpix)*size));
				}
				else if (x < minpix) {
					rep = (long) Math.max(0, interpole1(minpix) - ((minpix - x ) * size));
				}
				else {
				 rep = interpole1(minpix);
				}
			} else rep = (long) (x * size);
			return rep;
		}
				
}