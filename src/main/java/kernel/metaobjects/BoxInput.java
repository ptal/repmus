package kernel.metaobjects;

import gui.FX;

import java.io.Serializable;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import kernel.annotations.StringParser;
import kernel.tools.ParseException;
import kernel.tools.Parser;


public class BoxInput implements Serializable{

	public String name;
	public int index;
	transient public Class<?> type;
	transient public Type ptype;
	public Object val;
	
	public BoxInput (String thename, int theindex, Class<?> thetype, Type theptype, Object defval) {
		name = thename;
		index = theindex;
		ptype = theptype;
		type = thetype;
		val = defval;
	}
	
	public void changeVal (String newval) {
		Object rep;
		try {
			rep = Parser.str2Object (newval,type,ptype);
			if (rep != null)
				val  = rep;
		} catch (ParseException e) {
			FX.omMessageDialog("Ouups c'est pas une bonne entrée");
		} 
	}
}
