package projects.music.editors.drawables;

import gui.FX;
import gui.FXCanvas;
import gui.renders.I_Render;

import java.util.List;

import com.sun.javafx.geom.Rectangle;

import projects.music.classes.abstracts.MusicalObject;
import projects.music.classes.abstracts.Strie_MO;
import projects.music.classes.interfaces.I_MusicalObject;
import projects.music.classes.music.Chord;
import projects.music.classes.music.ChordSeq;
import projects.music.classes.music.Chord.ChordDrawable;
import projects.music.classes.music.Group.GroupDrawable;
import projects.music.editors.MusChars;
import projects.music.editors.MusicalParams;
import projects.music.editors.SpacedPacket;
import projects.music.editors.StaffSystem;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import kernel.tools.Fraction;
import kernel.tools.ST;

public class SimpleDrawable implements I_Drawable {
		public I_MusicalObject ref;
		public ContainerDrawable father;
		double recx;
		double recy;
		double recw;
		double rech;
		public double centerX;
		public double centerY;
		public boolean selected;
		public boolean editor_root = false;
		
		public MusicalParams params;


		//public Fraction bigchord = null;
		//extras

		public static final int onesecond = 3;
		public static final double rhtymicfactor = 1.2;//1.5;


		public void setRectangle (double x, double y, double w, double h) {
			recx = x ;
			recy = y;
			recw = w;
			rech = h;
		}

		public void drawRectSelection(I_Render g) {
			Color color = FX.omGetColorFill(g);
			FX.omSetColorFill(g, new Color(0.2, 0.2, 0.8, 0.3));
			FX.omFillRect(g, x(), y(), w(), h());
			FX.omSetColorFill(g, color);
		}
		
		public void drawRectSelection1(I_Render g, Color color1) {
			Color color = FX.omGetColorFill(g);
			FX.omSetColorFill(g, color1);
			FX.omFillRect(g, x(), y(), w(), h());
			FX.omSetColorFill(g, color);
		}

		public boolean getSelected() {
			return selected;
		}

		public void setSelected(boolean sel) {
			 selected = sel;
		}

		public ContainerDrawable getFather() {
			return father;
		}

		public void setFather (ContainerDrawable thefather) {
			father = thefather;
		}


		@Override
		public I_Drawable getClickedObject(double x, double y) {
			// TODO Auto-generated method stub
			return null;
		}


		@Override
		public void drawObject(I_Render g, FXCanvas panel, Rectangle rect,
				List<I_Drawable> selection, double x0, double deltax, double deltay) {

		}


		@Override
		public double h() {
			return rech;
		}

		@Override
		public double w() {
			return recw;
		}

		@Override
		public double x() {
			return recx;
		}

		@Override
		public double y() {
			return recy;
		}


		@Override
		public List<I_Drawable> getInside() {
			return null;
		}
		
		@Override
		public I_MusicalObject getRef() {
			return ref;
		}


		/////////////////////DRAW
		public void drawSelection(I_Render g, List<I_MusicalObject> selection, int size){
			if  (selection.contains(this))
				drawRectSelection(g);
		}

		public void drawContainersObjects (I_Render g, FXCanvas panel, Rectangle rect,
				List<I_Drawable> selection, double deltax) {
		}
		
		public void drawStem (I_Render g, double x, double y, int size, int beamsnumber, double strsize, boolean up_p, double stemsize) {
			double total;
			int i = 0;
			if (up_p) {
				if (beamsnumber == 0) {
					total = stemsize + size/4;
					FX.omDrawLine (g, x, y+size/8, x, y - total);
				}
				else {
					total = stemsize + (beamsnumber*size/4);
					FX.omDrawLine (g, x, y+size/8, x, y - total);
				}
				while  (i < beamsnumber) {
					FX.omDrawString (g, x, y - stemsize - (i*size/4), MusChars.beam_up); //"A"
					i++;
				}
				setRectangle(x(), y - total, w(), total + size/4);
			}
			else {
				if (beamsnumber == 0) {
					total = stemsize + size/2;
					FX.omDrawLine (g, x-strsize, y+size/8, x-strsize, y + total);
				}
				else {
					total = stemsize + ((beamsnumber + 1)*size/4);
					FX.omDrawLine (g, x-strsize, y+size/8, x-strsize, y + total);
				}
				while  (i < beamsnumber) {
					FX.omDrawString (g, x-strsize, y + stemsize + ((i+1) *size/4), MusChars.beam_dwn); 
					i++;
				}
				setRectangle(x(), y , w(), total);
			}
		}

		public boolean  firstOfChildren () {
			if (getFather() == null) return false;
			return this == getFather().getInside().get(0);
		}
		
		public boolean  lastOfChildren () {
			if (getFather() == null) return false;
			List<I_Drawable> children = getFather().getInside();
			int len = children.size();
			return this == children.get(len - 1);
		}
		
		public boolean isRoot_p () {
			return this.getFather() == null;
		}
		
		////////////////////
		////////////////////SPACING
		////////////////////
		
		public double  ms2pixel (long ms, int size) {
			return Math.round((onesecond * size * ms) / 1000);
		}

		public double ryhtm2pixels (long ms, int size) {
			return Math.max(size * 0.5,   size * Math.pow (rhtymicfactor , Math.log(ms/1000.0)/Math.log(2)));
		}


		public void collectRectangle () {
		}


		@Override
		public double getCX() {
			return centerX;
		}

		@Override
		public void setCX(double cx) {
			centerX = cx;
		}

		@Override
		public double computeCX(SpacedPacket pack, int size) {
			setCX(pack.start);
			return pack.start;
		}
		
		@Override
		public void translateCX(double max, SpacedPacket pack) {
			double deltax = max - getCX();
			if (deltax != 0) {
				setCX(max);
				recx = recx + deltax;
				pack.start = Math.min (pack.start, recx); 
				pack.space = Math.max (pack.space, (recx - pack.start) + rech); 
			}
			
		}
		
		@Override
		public void consTimeSpaceCurve (int size, double x, int zoom) {
		}

		@Override
		public void collectTemporalObjectsS(List<SpacedPacket> timelist) {
		}

		@Override
		public void collectTemporalObjectsL(List<SpacedPacket> timelist) {
		}


}


