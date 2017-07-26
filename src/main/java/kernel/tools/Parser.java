package kernel.tools;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import projects.music.classes.abstracts.RTree;
import projects.music.classes.abstracts.extras.Extra;
import projects.music.classes.abstracts.extras.I_Extra;
import projects.music.classes.music.RChord;
import projects.music.editors.StaffSystem;
import kernel.tools.generated.grammar1Lexer;
import kernel.tools.generated.grammar1Parser;

public class Parser {
	
	public static Object str2Object (String str, Class<?> clazz, Type gpType) throws ParseException {
		Object rep;
		Class<?> theclass = clazz;
		rep = parseString(str);
		System.out.print ("el rep " + rep);
		if (theclass == long.class) rep = new Long ((int) rep);
		else if (theclass == Fraction.class && rep .getClass() != Fraction.class)
			rep = new Fraction((int) rep);
		else if (theclass == double.class)
			rep = new Double(rep.toString());
		else if (theclass == RTree.class)
			rep = RTreefromLlist ((List<Object>) rep, true);
		else if (theclass == List.class) {
			Class<?> finalclass = ParserListener.getFinalClass((ParameterizedType) gpType);
			int profondeur = ParserListener.getProfondeur((ParameterizedType) gpType, 1);
			if (profondeur == 1)
				rep = ParserListener.castList1( (List<Object>) rep, finalclass );
			else
				rep = ParserListener.castList2( (List<List<Object>>) rep, finalclass );
		}
		return rep;
	}
		
	public static Object [] getInputsDef (String str, Constructor met) throws ParseException {
		String [] inputs = str.split(";");
		Class<?>[] types  = met.getParameterTypes();
		int len = types.length;
		Type[] gpType = met.getGenericParameterTypes();
		Object [] rep = new Object[len];
		for (int i = 0; i < len; i++) {
			Class<?> theclass = types[i];
			rep [i] = parseString(inputs[i]);
			if (theclass == long.class)
				rep[i] = new Long ((int) rep [i]);
			else if (theclass == RTree.class)
				rep[i] = RTreefromLlist ((List<Object>) rep[i], true);
			else if (theclass == Fraction.class && rep[i].getClass() != Fraction.class)
				rep[i] = new Fraction((int) rep[i]);
			else if (theclass == double.class)
				rep[i] = new Double(rep[i].toString());
			else if (theclass == List.class) {
				Class<?> finalclass = ParserListener.getFinalClass((ParameterizedType) gpType[i]);
				int profondeur = ParserListener.getProfondeur((ParameterizedType) gpType[i], 1);
				if (profondeur == 1)
					rep[i] = ParserListener.castList1( (List<Object>) rep[i], finalclass );
				else
					rep[i] = ParserListener.castList2( (List<List<Object>>) rep[i], finalclass );
			}
		}
		return rep;
	}
	
		//"Chord ((6000), (100), (0), (1000), (1))"
		//"RChord ((6000), (80), (0), 1/8, (1))"
		//"Note (6000, 100, 100, 1))"
		// "NCercle (12, ((1 5 7)))"
		// "ChordSeq ( ((6000)), (0), ((1000)), ((80)), ((0)), ((1)), 100)"

	// "(4/4 ( 1 1 [text "hola"] -1))"
	// "Voice ( ( (4/4 ( 1 2 -1))) , ( RChord ((6000), (80), (0), 1/8, (1))) , 60)"
	
	public static Object parseString (String arg) throws ParseException  {
		try {
			ANTLRInputStream in = new ANTLRInputStream(arg);
			// flux de caractères -> analyseur lexical
			grammar1Lexer lexer = new grammar1Lexer(in);
			// analyseur lexical -> flux de tokens
			CommonTokenStream tokens =	new CommonTokenStream(lexer);
			// flux tokens -> analyseur syntaxique
			grammar1Parser parser =	new grammar1Parser(tokens);
			// démarage de l'analyse syntaxique
			grammar1Parser.ExprContext tree = parser.expr();	
			// parcours de l'arbre syntaxique et appels du Listener
			ParseTreeWalker walker = new ParseTreeWalker();
			ParserListener extractor = new ParserListener();
			walker.walk(extractor, tree);	
			return tree.rep;
		} catch (Exception e) {
			System.out.print ("e " + e + " " + e.getMessage());
			throw e;
		}
	}

	
	public static RTree RTreefromLlist (List<Object> list, boolean start) {
		RTree rep = null;
		List<RTree> propor = new ArrayList<RTree> ();
		List<Object> plist = (List<Object>) list.get(1);
		for (Object item : plist) {
			if (item.getClass().getName().equals("java.util.ArrayList")) {
				if ( ((ArrayList) item).size() > 0) {
					if (((ArrayList) item).get(0) instanceof Extra) {
						List<I_Extra> extralist = new ArrayList<I_Extra> ();
						for (Object elem : ((ArrayList) item)) {
							extralist.add((I_Extra) elem);
						}
						propor.get(propor.size()-1).extras = extralist;
					}
					else
						propor.add(RTreefromLlist((List<Object>)  item, false));
				}
			}
			else if (item.getClass().getName().equals("java.lang.Integer"))
				propor.add(new RTree (new Long ((int) item), false));
			else if (item.getClass().getName().equals("java.lang.Float"))
				propor.add(new RTree (Math.round((float) item), true));
		}
		if (start) {
			if (list.get(0).getClass().getName().equals("kernel.tools.Fraction"))
				rep = new RTree ((Fraction) list.get(0), propor);
			else
				rep = new RTree (new Fraction ((int) list.get(0)), propor);
		}
		else rep = new RTree (new Long ((int) list.get(0)), propor);
		return rep;
	}
	
	public static void main(String[] args) throws ParseException  {
		try {
			ANTLRInputStream in = new ANTLRInputStream("(4/4 ( 1 1 [-text \"hola\"] -1))");
			// flux de caractères -> analyseur lexical
			grammar1Lexer lexer = new grammar1Lexer(in);
			// analyseur lexical -> flux de tokens
			CommonTokenStream tokens =	new CommonTokenStream(lexer);
			// flux tokens -> analyseur syntaxique
			grammar1Parser parser =	new grammar1Parser(tokens);
			// démarage de l'analyse syntaxique
			grammar1Parser.RtContext rep = parser.rt();	
			// parcours de l'arbre syntaxique et appels du Listener
			ParseTreeWalker walker = new ParseTreeWalker();
			ParserListener extractor = new ParserListener();
			walker.walk(extractor, rep);
		} catch (Exception e) {
			System.out.println("Uy error : " + e);
		}
	}
	
	public static String getString (Object obj) {
		if (obj.getClass().getName().equals("java.util.ArrayList")) 
			return getString((List<?>) obj);
		else
			return obj.toString();
	}
	
	public static <T> String getString (List<T> obj) {
		String rep = "(";
		for (T item : obj)
			rep =  rep + getString(item) + " ";
		rep = rep + ")";
		return rep;
	}
		
}
