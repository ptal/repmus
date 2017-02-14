package projects.music.editors;

import java.util.ArrayList;
import java.util.List;

import projects.music.editors.drawables.I_Drawable;
import projects.music.editors.drawables.SimpleDrawable;

//A set of objects to draw at the same time
public class SpacedPacket {
	public List<I_Drawable> objectlist =new ArrayList<I_Drawable>();;
	public long time = 0;
	public double start = 0; // start pixel
	public double space = 0; // w en pixel
	public double noTimeDeltaLeft = 0;
	public double noTimeDeltaRight = 0;
		
	public SpacedPacket (I_Drawable obj, long at, double pos) {
		objectlist.add(obj);
		time = at;
		start = pos;
	}
	
	public SpacedPacket (I_Drawable obj, long at) {
		objectlist.add(obj);
		time = at;
	}

	public void spacePacket(int size) {
		double maxCX = this.start;
		for (I_Drawable item : objectlist) {
			maxCX = Math.max (maxCX, item.computeCX(this, size)); // moveCX et calcule le rectangle del packet
			// il faudra faire item.setCX(packed.space);
		}
		//for (I_Drawable item : objectlist) {
		//	item.translateCX(maxCX, this);
		//}
		// a la fin je dois changer la valeur de packet space
	}
	
	public String toString ( ){
		return "time : " + time + " start " + start + " space : " + space + " objs : " + objectlist;
	}
	
}

/*
clefSpace
measureSpace
signatureSpace
alterationSpace
headsSpace
graceSpace
*/

/*
 breakLine
 breakPage
 changeClef
 changeSystem
 */

/*
 newMultiSystem
 finMultiSystem
 */

