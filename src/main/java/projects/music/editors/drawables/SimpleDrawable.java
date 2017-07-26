package projects.music.editors.drawables;

import gui.FX;
import gui.renders.I_Render;
import gui.CanvasFX;
import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Rectangle;

import projects.music.classes.abstracts.extras.I_Extra;
import projects.music.classes.interfaces.I_MusicalObject;
import projects.music.editors.MusChars;
import projects.music.editors.MusicalPanel;
import projects.music.editors.MusicalParams;
import projects.music.editors.SpacedPacket;
import javafx.scene.paint.Color;

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
		
		public List<I_ExtraDrawable> extras = new ArrayList<I_ExtraDrawable> ();


		//public Fraction bigchord = null;
		//extras

		public static final int onesecond = 3;
		public static final double rhtymicfactor = 1.5;


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
		public void drawObject(I_Render g, Rectangle rect,
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

		@Override
		public void drawContainersObjects (I_Render g, Rectangle rect,
				List<I_Drawable> selection, double deltax) {
		}
		
		public void drawStem (I_Render g, double x, double y, int size, int beamsnumber, double strsize, boolean up_p, double stemsize) {
			double total;
			int i = 0;
			if (up_p) {
				if (beamsnumber == 0) {
					total = stemsize;
					FX.omDrawLine (g, x, y+size/8, x, y+size/8 - total);
				}
				else {
					total = stemsize + (beamsnumber*size/4);
					FX.omDrawLine (g, x, y+size/8, x, y - stemsize - ((beamsnumber-1)*size/4) + size/8);
				}
				while  (i < beamsnumber) {
					FX.omDrawString (g, x, y - stemsize - ((i-1)*size/4) + size/8, MusChars.beam_up); //"A"
					i++;
				}
				setRectangle(x(), y - total, w(), total + size/4);
			}
			else {
				if (beamsnumber == 0) {
					total = stemsize;
					FX.omDrawLine (g, x-strsize, y+size/8, x-strsize, y + total);
				}
				else {
					total = stemsize + (beamsnumber *size/4);
					FX.omDrawLine (g, x-strsize, y+size/8, x-strsize, y + total);
				}
				while  (i < beamsnumber) {
					FX.omDrawString (g, x-strsize, y + stemsize + (i *size/4), MusChars.beam_dwn); 
					i++;
				}
				setRectangle(x(), y , w(), total);
			}
		}
		
		public double getStemSize (int beamsnumber, int size) {
			double stemsize = (size/8) * 7;
			if (beamsnumber == 0)
				return stemsize;
			else return stemsize + size/8 + (beamsnumber*size/4);
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
			return Math.max(size/4,   size * Math.pow (rhtymicfactor , Math.log(ms / 1000.0) / Math.log(2.0)));
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
		public void computeCX(SpacedPacket pack, int size) {
		}
		
		/*@Override
		public void translateCX(double max, SpacedPacket pack) {
			double deltax = max - getCX();
			if (deltax != 0) {
				setCX(max);
				recx = recx + deltax;
				pack.start = Math.min (pack.start, recx); 
				pack.space = Math.max (pack.space, (recx - pack.start) + rech); 
			}
			
		}*/
		
		@Override
		public void consTimeSpaceCurve (int size, double x, int zoom) {}

		@Override
		public void collectTemporalObjects(List<SpacedPacket> timelist) {}
		
		////////////////////BEAMS GROUP
		public void drawBeams (I_Render g, boolean up_p, int size, double deltax, 
				List <I_FigureDrawable> atomes, double ybarpos, double interBeamSpace) {
			int len = atomes.size();
			I_FigureDrawable cur;
			I_FigureDrawable next;
			I_FigureDrawable prev;
			for (int i = 0; i < len; i++) {
				int  shared;
				int propres;
				cur = (I_FigureDrawable) atomes.get(i);
				if (i+1 == len ) next = null; else next = (I_FigureDrawable) atomes.get(i+1);
				if (i == 0 ) prev = null; else prev = (I_FigureDrawable) atomes.get(i-1);
				if (i == 0 || cur.firstOfChildren()) {
					if (next == null) shared = 0;
					else	shared = getSharedWithNext(cur,next);
					propres = cur.getBeamsNum() - shared;
					if (next != null)
						drawLongBeams (g, up_p, cur, shared, next.getCX() - cur.getCX(),size, deltax, ybarpos, interBeamSpace) ;
					drawCourtBeams (g, up_p, cur, propres, size, shared, deltax, false, ybarpos, interBeamSpace);
				} 
				else if (i+1 == len ) {
					if (prev != null)
						shared = getSharedWithNext(prev,cur);
					else shared = 0;
					propres = cur.getBeamsNum() - shared;
					drawCourtBeams (g, up_p, cur, propres, size, shared, deltax, true, ybarpos, interBeamSpace);
				}
				else {
					if (cur.lastOfChildren() || next.firstOfChildren()) {
						if (prev != null)
							shared = getSharedWithNext(prev,cur);
						else shared = 0;
						propres = cur.getBeamsNum() - shared;
						drawCourtBeams (g, up_p, cur, propres, size, shared, deltax, true, ybarpos, interBeamSpace);
						shared = Math.min(1,Math.min(next.getBeamsNum(),cur.getBeamsNum()));
						drawLongBeams (g, up_p, cur, shared,  next.getCX() - cur.getCX(),size, deltax, ybarpos, interBeamSpace);
					}
					else {
						if (next != null)
							shared = getSharedWithNext(cur,next);
						else shared = 0;
						propres = cur.getBeamsNum() - shared;
						drawLongBeams (g, up_p, cur, shared,  next.getCX() - cur.getCX(),size, deltax, ybarpos, interBeamSpace);
						if (next.getBeamsNum() <= prev.getBeamsNum() && ! prev.lastOfChildren())
							drawCourtBeams (g, up_p, cur, propres, size, shared, deltax, true, ybarpos, interBeamSpace);
						else drawCourtBeams (g, up_p, cur, propres, size, shared, deltax, false, ybarpos, interBeamSpace);
					}
				}
			}
		}

	    public void drawBeam (I_Render g, double x, double y, double w, double h) {
	    	FX.omFillRect(g, x, y, w, h);
	    }
	    
	    public void drawCourtBeams (I_Render g, boolean up_p, I_FigureDrawable cur, int n, int size, 
	    		int shared, double deltax, boolean right, double ybarpos, double interBeamSpace) {
	    	double ygroup;
	    	double xpos;
	    	if (up_p) {
	    		ygroup = ybarpos + (shared * interBeamSpace);
	    		xpos = cur.getCX()  + deltax; //+ cur.getHeadSize(size) + deltax;
	    	}
	    	else {
	    		ygroup = ybarpos  - (shared * interBeamSpace);
	    		xpos = cur.getCX() + deltax;
	    	}
	    	if (right) xpos = xpos - size*1/4;
	    	for (int i = 0; i < n; i++) {
	    	if (up_p)
	    		drawBeam(g, xpos, ygroup+(i*interBeamSpace), size*1/4, size*1/8);
	    	else 
	    		drawBeam(g, xpos, ygroup-(i*interBeamSpace), size*1/4, size*1/8);
	    	}
	    }
	    
	    public void drawLongBeams (I_Render g, boolean up_p , I_FigureDrawable cur, int n, double sizex, 
	    		int size, double deltax, double ybarpos, double interBeamSpace ) {
	    	double xpos;
	    	if (up_p) 
	    		xpos = cur.getCX() + cur.getHeadSize(size) + deltax;
	    	else 
	    		xpos = cur.getCX() + deltax;
	    	for (int i = 0; i < n; i++) {
	    		if (up_p) 
	    			drawBeam(g, xpos, ybarpos + (i*interBeamSpace), sizex, size*1/8);
	    		else 
	    			drawBeam(g, xpos, ybarpos - (i*interBeamSpace), sizex, size*1/8);
	    	}
	   }
	    
	    public int getSharedWithNext (I_FigureDrawable prev, I_FigureDrawable cur) {
			if (prev.lastOfChildren() || cur.firstOfChildren()) 
				return Math.min(1,Math.min(prev.getBeamsNum(),  cur.getBeamsNum()));
			else
				return Math.min(prev.getBeamsNum(), cur.getBeamsNum());
	    }	
	    
	    ///////
	    
	    public I_Drawable nextBrother () {
	    	if (getFather() == null)
	    		return null;
	    	else {
	    		List<I_Drawable> childs =  getFather().getInside();
	    		int len = childs.size();
	    		for (int i = 0; i < len; i++) {
	    			if (this == childs.get(i))
	    				if (i < len-1)
	    					return childs.get(i+1);
	    				else
	    					return null;
	    		}
	    		return null;
	    	}
	    }

	    public I_Drawable prevBrother () {
	    	if (getFather() == null)
	    		return null;
	    	else {
	    		List<I_Drawable> childs =  getFather().getInside();
	    		int len = childs.size();
	    		for (int i = 0; i < len; i++) {
	    			if (this == childs.get(i))
	    				if (i > 0)
	    					return childs.get(i-1);
	    				else
	    					return null;
	    		}
	    		return null;
	    	}
	    }
	    
	    
	    //////////Extras///////
	    public void makeGraphicExtras () {
	    		for (I_Extra item : ref.getExtras()) {
	    			extras.add(item.makeDrawable(item, this));
	    		}                          
	    }
	    
	    public void drawExtras (I_Render g, double deltax) {
    		for (I_ExtraDrawable item : extras) {
    			item.drawExtra(g, deltax);
    		}                          
    }

}


