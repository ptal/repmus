package kernel.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import projects.music.classes.abstracts.RTree;
import projects.music.editors.I_StaffLine;
import projects.music.editors.StaffSystem;
import projects.music.editors.StaffSystem.MultipleStaff;
import projects.music.editors.StaffSystem.StaffGroup;
import kernel.annotations.Ombuilder;
import kernel.tools.generated.grammar1Listener;
import kernel.tools.generated.grammar1Parser;
import kernel.tools.generated.grammar1Parser.BuilderContext;
import kernel.tools.generated.grammar1Parser.ConstFalseContext;
import kernel.tools.generated.grammar1Parser.ConstFloatContext;
import kernel.tools.generated.grammar1Parser.ConstIntegerContext;
import kernel.tools.generated.grammar1Parser.ConstRatioContext;
import kernel.tools.generated.grammar1Parser.ConstStringContext;
import kernel.tools.generated.grammar1Parser.ConstTrueContext;
import kernel.tools.generated.grammar1Parser.DurIntegerContext;
import kernel.tools.generated.grammar1Parser.ExprContext;
import kernel.tools.generated.grammar1Parser.ExtraGraceContext;
import kernel.tools.generated.grammar1Parser.ExtraListContext;
import kernel.tools.generated.grammar1Parser.ExtraTextContext;
import kernel.tools.generated.grammar1Parser.LineContext;
import kernel.tools.generated.grammar1Parser.ListContext;
import kernel.tools.generated.grammar1Parser.NegatifFloatContext;
import kernel.tools.generated.grammar1Parser.NegatifIntContext;
import kernel.tools.generated.grammar1Parser.PRTFloatContext;
import kernel.tools.generated.grammar1Parser.PRTIntegerContext;
import kernel.tools.generated.grammar1Parser.PRTreeContext;
import kernel.tools.generated.grammar1Parser.PositifFloatContext;
import kernel.tools.generated.grammar1Parser.PositifIntContext;
import kernel.tools.generated.grammar1Parser.RTreeContext;
import kernel.tools.generated.grammar1Parser.RatioContext;
import kernel.tools.generated.grammar1Parser.StaffGroupContext;
import kernel.tools.generated.grammar1Parser.StaffLinesContext;
import kernel.tools.generated.grammar1Parser.StaffSimpleContext;
import kernel.tools.generated.grammar1Parser.SystemContext;
import projects.music.classes.abstracts.extras.Extra;
import projects.music.classes.abstracts.extras.Extra.ExtraText;
import projects.music.classes.abstracts.extras.I_Extra;


public class ParserListener implements grammar1Listener{
	List<String> packages = Arrays.asList(new String [] {"projects.music.classes.music", "projects.mathtools.classes"});

	@Override
	public void enterEveryRule(ParserRuleContext arg0) {}

	@Override
	public void exitEveryRule(ParserRuleContext arg0) {}

	@Override
	public void visitErrorNode(ErrorNode arg0) {}

	@Override
	public void visitTerminal(TerminalNode arg0) {}

	@Override
	public void enterBuilder(BuilderContext ctx) {}
	@Override
	public void exitBuilder(BuilderContext ctx) {
		//classname=IDENT '(' args+=expr? (',' args+=expr)* ')' # Builder
		//Note (int themidic, int thevel, long dur, int thechan)
		List<Object> rep = new ArrayList<Object>();
		Object [] args = new Object [ctx.args.size()];
		int i = 0;
		for (ExprContext e : ctx.args) {
			args[i] =e.rep;
			i++;
		}
		String cname = ctx.classname.getText();
		Class<?> clazz = null;
		for (String pkg : packages) {
			    String fullyQualified = pkg + "." + cname;
			    try {
			    	clazz = Class.forName(fullyQualified);
			    	break;
			    } catch (ClassNotFoundException e) {
			        clazz = null;
			    }
			}
		if (clazz != null) {
			try {
				for (Constructor<?> c : clazz.getConstructors ()) {
					Ombuilder annot = (Ombuilder) c.getAnnotation(kernel.annotations.Ombuilder.class);
					if (annot != null){
						Class<?> [] argTypes = c.getParameterTypes();
						Type[] gpType = c.getGenericParameterTypes();
						Object [] finalArgs = convert (argTypes, gpType, args);
						if (finalArgs != null) {
							ctx.rep = c.newInstance(finalArgs);
							break;
						}
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}		
	}

	
	public static <K> List<K> castList1 (List<Object> thelist, Class<K> clazz) {
		List<K> rep = new ArrayList<K>();
		for (Object e : thelist) {
			if (clazz.getName().equals("java.lang.Long"))
				rep.add((K) new Long ((int) e));
			else if (clazz.getName().equals("projects.music.classes.abstracts.RTree"))
				rep.add((K) Parser.RTreefromLlist((List<Object>) e, true ));
			else
				rep.add((K) e);
		}
		return rep;
	}
	
	public static <K> List<List<K>> castList2 (List<List<Object>> thelist, Class<K> clazz) {
		List<List<K>> rep1 = new ArrayList<List<K>>();
		for (List<Object> e1 : thelist) {
			List<K> rep = new ArrayList<K>();
			for (Object e : e1) {
				if (clazz.getName().equals("java.lang.Long"))
					rep.add((K) new Long ((int) e));
				else if (clazz.getName().equals("java.lang.Integer"))
					rep.add((K) new Integer ((int) e));
				else if (clazz.getName().equals("projects.music.classes.abstracts.RTree"))
					rep.add((K) Parser.RTreefromLlist((List<Object>) e, true ));
				else
					rep.add((K) e);
			}
			rep1.add(rep);
		}
		return rep1;
	}
	
	public static Class<?> getFinalClass (ParameterizedType clazz) {
		Type [] types = clazz.getActualTypeArguments();
		if (types[0] instanceof ParameterizedType) 
			return getFinalClass ((ParameterizedType) types[0]);
		else
			return (Class<?>) types[0];
	}
	
	public static int getProfondeur (ParameterizedType clazz, int n) {
		Type [] types = clazz.getActualTypeArguments();
		if (types[0] instanceof ParameterizedType) 
			return getProfondeur ((ParameterizedType) types[0], n+1);
		else
			return n;
	}
		
	public Object [] convert (Class<?> [] argtypes, Type[] gpType, Object [] args) {
		if (args.length != argtypes.length)
			return null;
		else {
			Object [] rep = new Object [args.length];
			for (int i = 0; i < args.length; i++){
				Object newobj = null;
				if (argtypes[i].getName().equals("long"))
					newobj = new Long ((int) args[i]);
				else if (argtypes[i].getName().equals("int"))
					newobj = Integer.parseInt(args[i].toString());
				else if (argtypes[i].getName().equals("projects.music.classes.abstracts.RTree"))
					newobj = Parser.RTreefromLlist( (List<Object>) args[i], true);
				else if (argtypes[i].getName().equals("java.util.List")) {
					Class<?> finalclass = getFinalClass((ParameterizedType) gpType[i]);
					int profondeur = getProfondeur((ParameterizedType) gpType[i], 1);
					if (profondeur == 1)
						newobj = castList1( (List<Object>) args[i], finalclass );
					else
						newobj = castList2( (List<List<Object>>) args[i], finalclass );
				}
				else
					newobj = args[i];
				if (newobj != null)
					rep[i] = newobj;
				else
					return null;
			}
			return rep;
		}
	}

	@Override
	public void enterConstFloat(ConstFloatContext ctx) {}
	@Override
	public void exitConstFloat(ConstFloatContext ctx) {
		ctx.rep =  ctx.floatConst.rep;
	}


	@Override
	public void enterConstFalse(ConstFalseContext ctx) {}
	@Override
	public void exitConstFalse(ConstFalseContext ctx) {
		ctx.rep =  false;
	}

	@Override
	public void enterConstTrue(ConstTrueContext ctx) {}
	@Override
	public void exitConstTrue(ConstTrueContext ctx) {
		ctx.rep =  true;
	}

	@Override
	public void enterConstInteger(ConstIntegerContext ctx) { }
	@Override
	public void exitConstInteger(ConstIntegerContext ctx) {
		ctx.rep =  ctx.intConst.rep;
	}
	
	@Override
	public void enterConstRatio(ConstRatioContext ctx) {}
	@Override
	public void exitConstRatio(ConstRatioContext ctx) {
		ctx.rep =  ctx.ratio.rep;
	}

	//////////////List

	@Override
	public void enterList(ListContext ctx) {}
	@Override
	public void exitList(ListContext ctx) {
		List<Object> rep = new ArrayList<Object>();
		for (ExprContext e : ctx.elems) {
			rep.add((Object) e.rep);
		}
		ctx.rep = rep;
	}

	//////////////RTree

	@Override
	public void enterRTree(RTreeContext ctx) {}
	@Override
	public void exitRTree(RTreeContext ctx) {
		List<RTree> rep = new ArrayList<RTree>();
		for (grammar1Parser.PrtContext e : ctx.prop) {
			rep.add((RTree) e.rep);
		}
		ctx.rep = new RTree((Fraction) ctx.dur.rep, rep);
		
	}
	
	@Override
	public void enterPRTree(PRTreeContext ctx) {}
	@Override
	public void exitPRTree(PRTreeContext ctx) {
		List<RTree> rep = new ArrayList<RTree>();
		for (grammar1Parser.PrtContext e : ctx.propo) {
			rep.add((RTree) e.rep);
		}
		ctx.rep = new RTree(new Long ((int) ctx.propor.rep), rep);
	}
	
	@Override
	public void enterPRTInteger(PRTIntegerContext ctx) {}
	@Override
	public void exitPRTInteger(PRTIntegerContext ctx) {
		RTree therep = new RTree(new Long((int) ctx.intConst.rep), false);
		List<I_Extra> extralist = new ArrayList<I_Extra>();
		for (grammar1Parser.ExtraRContext e : ctx.extras) {
			extralist.add((I_Extra) e.rep);
		}
		therep.extras= extralist;
		ctx.rep = therep;
	}
	
	@Override
	public void enterPRTFloat(PRTFloatContext ctx) {}
	@Override
	public void exitPRTFloat(PRTFloatContext ctx) {
		ctx.rep = new RTree((long) Math.round((float) ctx.floatConst.rep), true);
	}
	
	
	@Override
	public void enterRatio(RatioContext ctx) {}
	@Override
	public void exitRatio(RatioContext ctx) {
		ctx.rep = new Fraction ((Integer) ctx.intNum.rep, Integer.parseInt(ctx.intDen.getText()));
	}
	
	@Override
	public void enterDurInteger(DurIntegerContext ctx) {}
	@Override
	public void exitDurInteger(DurIntegerContext ctx) {
		ctx.rep = new Fraction ((Integer) ctx.intConst.rep);
	}

	///////////////////////////Constantes
	
	@Override
	public void enterConstString(ConstStringContext ctx) {}
	@Override
	public void exitConstString(ConstStringContext ctx) {
		ctx.rep = ctx.stringConst.getText();
	}

	@Override
	public void enterPositifInt(PositifIntContext ctx) {}
	@Override
	public void exitPositifInt(PositifIntContext ctx) {
		ctx.rep = Integer.parseInt(ctx.intval.getText());	
	}

	@Override
	public void enterPositifFloat(PositifFloatContext ctx) {}
	@Override
	public void exitPositifFloat(PositifFloatContext ctx) {
		ctx.rep = Float.parseFloat(ctx.floatval.getText());
	}

	@Override
	public void enterNegatifInt(NegatifIntContext ctx) {}
	@Override
	public void exitNegatifInt(NegatifIntContext ctx) {
		ctx.rep = Integer.parseInt(ctx.intval.getText()) * -1;
	}

	@Override
	public void enterNegatifFloat(NegatifFloatContext ctx) {}
	@Override
	public void exitNegatifFloat(NegatifFloatContext ctx) {
		ctx.rep = Float.parseFloat(ctx.floatval.getText()) * -1;
	}

	
	// POUR STAFFSYSTEM
	@Override
	public void enterStaffLines(StaffLinesContext ctx) {}
	@Override
	public void exitStaffLines(StaffLinesContext ctx) {
		List<I_StaffLine> staffLines = new ArrayList<I_StaffLine>();
		for (grammar1Parser.LineContext obj : ctx.lines) 
			staffLines.add ((I_StaffLine) obj.rep);
		ctx.rep = new StaffSystem (staffLines);
	}

	@Override
	public void enterStaffGroup(StaffGroupContext ctx) {}
	@Override
	public void exitStaffGroup(StaffGroupContext ctx) {
		List<I_StaffLine> staffLines = new ArrayList<I_StaffLine>();
		for (grammar1Parser.LineContext obj : ctx.lines) 
			staffLines.add((I_StaffLine) obj.rep);
		ctx.rep = new StaffGroup (staffLines);
	}

	@Override
	public void enterStaffSimple(StaffSimpleContext ctx) {}
	@Override
	public void exitStaffSimple(StaffSimpleContext ctx) {
		ctx.rep = new MultipleStaff (ctx.simple.getText());
	}

	@Override
	public void enterExtraText(ExtraTextContext ctx) {}
	@Override
	public void exitExtraText(ExtraTextContext ctx) {
		boolean dwn = (ctx.up != null);
		int dx,dy;
		if (ctx.dy != null)
			dy = Integer.parseInt(ctx.dy.getText());
		else
			dy = 0;
		if (ctx.dx != null)
			dx = Integer.parseInt(ctx.dx.getText());
		else
			dx = 0;
		ctx.rep = new ExtraText (ctx.text.getText(), dwn,dx, dy);
	}

	@Override
	public void enterExtraGrace(ExtraGraceContext ctx) {}
	@Override
	public void exitExtraGrace(ExtraGraceContext ctx) {
		// TODO Auto-generated method stub
	}

	@Override
	public void enterExtraList(ExtraListContext ctx) {}

	@Override
	public void exitExtraList(ExtraListContext ctx) {
		List<I_Extra> extralist = new ArrayList<I_Extra>();
		for (grammar1Parser.ExtraRContext e : ctx.extras) {
			extralist.add((I_Extra) e.rep);
		}
		ctx.rep = extralist;
	}
}
