package projects.music.editors.drawables;

import gui.FXCanvas;
import gui.renders.I_Render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import projects.music.classes.abstracts.Compose_L_MO;
import projects.music.editors.MusicalParams;
import projects.music.editors.Scale;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;

public class ContainerDrawable extends SimpleDrawable{

		public List<I_Drawable> inside = new ArrayList<I_Drawable>();		
		
		public List<SpacedPacket> timespacedlistS;
		public List<SpacedPacket> timespacedlistL;
		
		public List<I_Drawable> getInside() {
			return inside;
		}
		
		//On va dessiner en deux pas
		//d'abord on fait dans le temps avec drawObject
		//apres on fera par hierarchie avec drawContainersObjects
		@Override
		public void drawObject(I_Render g, FXCanvas panel, Rectangle rect,
				 List<I_Drawable> selection, double packStart, 
				double deltax, double deltay) {
			int size = params.fontsize.get();
			if (isRoot_p ()) {
				for (SpacedPacket packed : timespacedlistS) {
					for (I_Drawable obj : packed.objectlist ){
						obj.drawObject (g, panel, rect, selection, packed.start, deltax, deltay);
					}
				}
				for (SpacedPacket packed : timespacedlistL) {
					for (I_Drawable obj : packed.objectlist ){
						obj.drawObject (g, panel, rect, selection, packed.start, deltax, deltay);
					}
				}
			}
			drawContainersObjects(g,  panel,  rect, selection, deltax);
		}
		

		public void drawContainersObjects (I_Render g, FXCanvas panel, Rectangle rect,
				 List<I_Drawable> selection, double deltax) {
			for (I_Drawable obj : getInside()) {
				obj.drawContainersObjects(g,  panel,  rect, selection, deltax);
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
		public void collectTemporalObjectsS(List<SpacedPacket> timelist) {
			for (I_Drawable obj : getInside()) {
				((SimpleDrawable) obj).collectTemporalObjectsS(timelist);
			}
		}
		
		@Override
		public void collectTemporalObjectsL(List<SpacedPacket> timelist) {
			for (I_Drawable obj : getInside()) {
				((SimpleDrawable) obj).collectTemporalObjectsL(timelist);
			}
		}
		
		public void makeSpaceObjectList () {
			timespacedlistS = new ArrayList<SpacedPacket>();
			timespacedlistL = new ArrayList<SpacedPacket>();
			collectTemporalObjectsS(timespacedlistS);
			collectTemporalObjectsL(timespacedlistL);
			Collections.sort(timespacedlistS, packetordered);
			Collections.sort(timespacedlistL, packetordered);
			groupTemporalObjects ();
		}
		
		private void groupTemporalObjects () {
			List<SpacedPacket> rep = new ArrayList<SpacedPacket>();
			SpacedPacket curpacket = null;
			for (SpacedPacket packet : timespacedlistS) {
				 if (curpacket == null)
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time);
				 else if (packet.time == curpacket.time)
					 curpacket.objectlist.add(packet.objectlist.get(0));
				 else {
					 rep.add(curpacket);
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time);
				 }
			}
			if (curpacket != null) rep.add(curpacket);
			timespacedlistS = rep;
			List<SpacedPacket> rep1 = new ArrayList<SpacedPacket>();
			curpacket = null;
			for (SpacedPacket packet : timespacedlistL) {
				 if (curpacket == null)
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time);
				 else if (packet.time == curpacket.time)
					 curpacket.objectlist.add(packet.objectlist.get(0));
				 else {
					 rep1.add(curpacket);
					 curpacket = new SpacedPacket(packet.objectlist.get(0), packet.time);
				 }
			}
			if (curpacket != null) rep1.add(curpacket);
			timespacedlistL = rep1;
		}
		
		/////////
		public void consTimeSpaceCurve (int size, double x, int zoom) {
			long lasttime = 0;
			double lastspace = x;
			SpacedPacket packed;
			double newspace = 0;
			int lens = timespacedlistS.size();
			int lenl = timespacedlistL.size();
			if (lens == 0) {
				for (int i = 0; i < lenl; i++) {
					packed = timespacedlistL.get(i);
					packed.start = x + ms2pixel(packed.time, size);
					if (i+1 < lenl)
						newspace = ryhtm2pixels(timespacedlistL.get(i+1).time - packed.time, size) ;
					else 
						newspace = size;
					packed.space = newspace;
					packed.spacePacket(size);
					lasttime = packed.time;
					lastspace = lastspace + packed.space;
				}
			}
			else {
				for (int i = 0; i < lens; i++) {
					packed = timespacedlistS.get(i);
					if (i+1 < lens)
						newspace = ryhtm2pixels(timespacedlistS.get(i+1).time - packed.time, size) ;
					else 
						newspace = size;
					packed.start = lastspace;
					packed.space = newspace; // pour l'instant sinon la valeur elle doit sortir apres spacePacket
					packed.spacePacket(size);
					lasttime = packed.time;
					lastspace = lastspace + packed.space;
				}
				//prendre tous le lisses et les interpoler
			}
		}
		
	
			
}