/*
Pour utiliser une grammaire ANTLR 4:

- installer ANTLR4 SDK depuis Help -> Marketplace...

- vérifiez que dans Widnow -> Preferences -> ANTLR4 -> Tools,
"Generate parse tree listener" est bien sélectionné.

- cliquer sur le fichier .g4 avec : click droit -> Run As -> Generate ANTLR Recognizer
(un fichier target/generated-sources/antlr4/ILPMgrammar1BaseListener est généré)

- ajouter le chemin target/generated-sources/antl4  dans les source du projet
avec cllic droit sur antlr4 -> Build Path -> Use as Source Folder

- ajouter antlr-4.4complete.jar

*/

grammar grammar1;

 // Expressions
expr returns [Object rep]
	: classname=IDENT '(' args+=expr? (',' args+=expr)* ')' # Builder
    | '(' elems+=expr* ')' # List
    | 't' # ConstTrue
    | 'nil' # ConstFalse
    | intConst=integerR # ConstInteger
    | floatConst=floatR # ConstFloat
    | stringConst=STRING # ConstString
    | ratio=ratioR # ConstRatio
    ;
//Rhythmic tree
rt returns [Object rep]

// RT
    : '(' dur=ratioR '(' prop+=prt* ')' ')' # RTree
    ;

prt returns [Object rep]

 // PRT
    : '(' propor=integerR '(' propo+=prt* ')' ')' # PRTree
	| intConst=integerR # PRTInteger
    | floatConst=floatR # PRTFloat
    ;

integerR returns [Object rep]
// integer
    : intval=INT # PositifInt
	| '-' intval=INT # NegatifInt
    ;

floatR returns [Object rep]
// integer
    : floatval=FLOAT # PositifFloat
	| '-' floatval=FLOAT # NegatifFloat
    ;

ratioR returns [Object rep]
// ration
    : intNum=integerR '/' intDen=INT# Ratio
	| intConst=integerR # DurInteger
    ;



system returns [Object rep]
//staff
	: '[' lines+=line+ ']' # StaffLines
	;

line returns [Object rep]
//staff
	: simple=IDENT # StaffSimple
	| group= '{' lines+=line+ '}'   # StaffGroup
	;



// Règles lexicales.


// Identificateurs
IDENT : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Constantes entières
INT : [0-9]+ ;

// Constantes flottantes
FLOAT : [0-9]* '.' [0-9]* ;

// Constantes chaînes de caractères
STRING : '"' (ESC | ~["\\])*  '"';
ESC : '\\' [\\nrt"];


// Espaces
SPACE : [ \t\r\n]+ -> skip;
