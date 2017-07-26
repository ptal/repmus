package projects.music.editors;

import java.util.ArrayList;
import java.util.List;

import kernel.tools.ST;
import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;

//A set of objects to draw at the same time
public class SpacedPacket {
	public List<I_Drawable> objectlist =new ArrayList<I_Drawable>();;
	public long time = 0;
	public double start = 0; // start pixel
	public double space = 0; // w en pixel
	//public double cx = 0; // absolut en pixel
	public double noTimeDeltaLeft = 0;
	public double noTimeDeltaRight = 0;

	public double deltal = 0;
	public double deltar = 0;
	public boolean strie_p = false;
		
	public SpacedPacket (I_Drawable obj, long at, double pos, boolean strie) {
		objectlist.add(obj);
		time = at;
		start = pos;
		strie_p = strie;
	}
	
	public SpacedPacket (I_Drawable obj, long at, boolean strie) {
		objectlist.add(obj);
		time = at;
		strie_p = strie;
	}

	public void spacePacket(int size) {
		for (I_Drawable item : objectlist) 
			 item.computeCX(this, size);
		for (I_Drawable item : objectlist) {
			item.setCX(start+noTimeDeltaLeft+deltal);
		}
		space = space + noTimeDeltaLeft + deltal + deltar + noTimeDeltaRight;
	}
	
	public void updatePacket(double ntdl, double ntdr, double item_dl, double item_dr) {
		noTimeDeltaLeft = Math.max (noTimeDeltaLeft, ntdl);
		noTimeDeltaRight = Math.max (noTimeDeltaRight, ntdr);
		deltal = Math.max (deltal, item_dl);
		deltar = Math.max (deltar, item_dr);
	}
	
	public double getCX ( ){
		return start+noTimeDeltaLeft+deltal;
	}
	
	public String toString ( ){
		return "time : " + time + " start " + start + " space : " + space + " objs : " + objectlist;
	}
	
}


