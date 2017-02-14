/* *****************************************************************
 * ILP9 - Implantation d'un langage de programmation.
 * by Christian.Queinnec@paracamplus.com
 * See http://mooc.paracamplus.com/ilp9
 * GPL version 3
 ***************************************************************** */
package kernel.tools;

@SuppressWarnings("serial")
public class ParseException extends Exception {
	public ParseException (final Throwable cause) {
		super(cause);
	}

	public ParseException (final String message) {
		super(message);
	}
}
