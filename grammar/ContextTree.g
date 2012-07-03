/**

*/
grammar ContextTree;

options {output=AST;
	ASTLabelType=CommonTree;
}

tokens {
	VALUE;
	VLIST;
	PROP_LIST;
	SHAPE_LIST;
	LINK_LIST;
	LINK_STX;
	USER_PROP;
	PACKAGE;
	PREDEFINED;
}

@header{
package org.pathwayeditor.codegenerator.gen;
}
@lexer::header{
package org.pathwayeditor.codegenerator.gen;
}

@members{
	private ArrayList<String> shapeList=new ArrayList<String>();
	private ArrayList<String> linkList=new ArrayList<String>();
	StringBuffer packageName=new StringBuffer();
	
	public String getPackageName(){
	  return packageName.toString();
	}
	
	public List<String> getShapeList(){
	  return new ArrayList<String>(this.shapeList);
	}
	
	public List<String> getLinkList(){
	  return new ArrayList<String>(this.linkList);
	}
	
	}

context	returns[String name] :	ML_COMMENT? contextDef {$name=$contextDef.id;} properties+ rmo shapes+  links+ EOF -> ^(contextDef rmo ^(PROP_LIST properties+) ^(SHAPE_LIST shapes+) ^(LINK_LIST links+)  )
	;

contextDef returns[String id]
	:	'context' (Package '.' {packageName.append($Package.text).append(".");})* elemDesc {$id=$elemDesc.id;packageName.append($id.toLowerCase());}version RightBracket -> ^(elemDesc (^(PACKAGE Package*))? version)
	;

version	: 'version'^ equals! Version 
	;



//links	: (ML_COMMENT?)! 'link'! elemDesc^ (lineProp|userProp|endDef|port)+ RightBracket!  
links	: (ML_COMMENT?) 'link' ID {linkList.add($ID.text);} LeftBracket name description? (lineProp|userProp|endDef|port)+ RightBracket -> ^(ID name description? lineProp+ port+ (^(USER_PROP userProp+))? ^(LINK_STX endDef+)) 
	;
	
properties
	: (ML_COMMENT?)! Property elemDesc propDef RightBracket (equals  propValue)?  -> ^(elemDesc propDef (^(VALUE propValue))?)
	;


port	: 'sport'^ portDef
	| 'tport'^ portDef
	;

portDef	:	equals ArrowheadStyle SIZE? LeftBracket (offset|shapeDef|userProp)+ RightBracket -> ^(ArrowheadStyle SIZE? offset? shapeDef+ (^(USER_PROP userProp+))?)
	;
/**
 * Set offset between shape boundaty and link visual end. 
 * by default offset is 0
 */
offset	:	'offset'^ equals! INT
	;

/**
 * Basic description of any element in the grammar. It consists of ID '(' name and optional definition
 */
elemDesc returns[String id]
	:	ID {$id=$ID.text;} LeftBracket name description? -> ^(ID name description?)
	;

/**
 * Definition of link end decorators together with connectivity validation
 */
//endDef	:	'source' equals ID 'target' equals listCSV -> ^(LINK_STX ^(ID listCSV))
endDef	:	'source'! equals! ID^ 'target'! equals! listCSV
	;

//shapes	: 	ML_COMMENT? 'shape' elemDesc (shapeDef|userProp|containment)+ RightBracket
shapes	: 	ML_COMMENT? 'shape' ID {shapeList.add($ID.text);} LeftBracket name description? (shapeDef|userProp|containment)+ RightBracket -> ^(ID name description? shapeDef+ (^(USER_PROP userProp+))? containment)
	;
rmo	:	ML_COMMENT? 'RMO' LeftBracket (userProp|containment)+ RightBracket -> ^('RMO' (^(USER_PROP userProp+))? containment)
	;

/**
 * Defines parent/child relationship in the context. Conatinment describe rules to identify 
 * valid child for a parent and valid parent for a child. List in a square brackets define 
 * set of vaild children for enclosing shapes. List could be empty, which means that the shape 
 * is always a leaf in the parenting tree.  
 * It is able to use NOT operator to define a lis by excluding invalid children from all shapes defined in the context.
 * For example: contains [], contains [Macromolecule, Compound], contains ![Process]
 */
containment
	: SHAPE_STX^ listCSV
	| SHAPE_STX^ LeftSqBracket!  RightSqBracket! //default empty list
	;
/**
 * Comma separated list of object IDs. Allow to define list directly or via exclusion list.
 */
listCSV	:	LeftSqBracket ID (',' ID)* RightSqBracket -> ^(VLIST ID+)
	|	'!' LeftSqBracket ID (',' ID)* RightSqBracket  -> ^('!' ^(VLIST ID+))
	|	LeftSqBracket '*' RightSqBracket -> ^(VLIST '*')
//	|	'!' LeftSqBracket '*' RightSqBracket  -> ^('!' ^(VLIST '*'))
	;


propDef	: type  PROPMODIFIER*
	
	;

userProp
	:	ID modifyProp? equals propValue -> ^(ID modifyProp? ^(VALUE propValue))
	|	PredefinedProperty modifyProp? equals propValue -> ^(PREDEFINED ^(PredefinedProperty  modifyProp? ^(VALUE propValue)))
	|	properties
	;
	
modifyProp
	:	 LeftBracket! PROPMODIFIER(','! PROPMODIFIER)* RightBracket!
	;
	
shapeDef:	'stype'^ modifyProp? equals! ShapeType
	|	'size'^  modifyProp? equals! SIZE
	|	'fcolor'^ modifyProp? equals!  color
	|	lineProp
	;
	
//Definition of visual properties like fill type, color etc.
lineProp
	:  	'lstyle'^ modifyProp? equals! LineStyle
	|	'lwidth'^ modifyProp? equals! INT
	|	'lcolor'^ modifyProp? equals! color
//	|	'transpacency'^ modifyProp? equals! Transparency
	;

equals	:	WS? EqualSign WS?
	;

/**
 * Color definition. Could be triplet of integers of hex value:
 * [255,255,255]
 * #FFFFFF
 */	
color:		RGB
	|	HEXRGB
	;
	
name 	:	'name'^ equals! Text
	; //Name should not be single world  

description 
	:	'descr'^ equals!  Text
	;
//Type of property
type 	:	'type'^ equals! ('simple'|'rich'|'number'|'size'|'rgb')//;TYPE
	;

propValue 
	:	Text
	|	EMPTY_STR
	|	html
	|	DOUBLE
	|	SIZE
	|	color;
	
html 	:	'"<' '>"';


/** Set of predefined object properties in EPE CA
*/	
PredefinedProperty
	:	'Url' | 'Description' | 'DetailedDescription' | 'Name'
	;
	
ArrowheadStyle
	:	'DIAMOND'|'ARROW'|'DOUBLE_ARROW'|'TRIANGLE'|'EMPTY_DIAMOND'|'BAR'|'DOUBLE_BAR'|'EMPTY_CIRCLE'|'NONE'|'SQUARE'|'EMPTY_SQUARE'|'EMPTY_TRIANGLE'|'TRIANGLE_BAR'
	;
	
Transparency 
	:	'NORMAL ' | 'TWENTYFIVE_PERCENT' | 'FIFTY_PERCENT' | 'SEVENTYFIVE_PERCENT'	
	;
	
LineStyle 
	:	'SOLID' | 'DASHED' | 'DASH_DOT' | 'DASH_DOT_DOT' | 'DOT'
	;

ShapeType
	: ('RECTANGLE' | 'ELLIPSE' | 'ROUNDED_RECTANGLE' | 'OCTAGON' | 'IRREGULAR_OCTAGON' | 'LH_PARALLELOGRAM' | 'RH_PARALLELOGRAM' | 'TRIANGLE' | 'HEXAGON' | 'UP_CHEVRON' | 'DOWN_CHEVRON' | 'EMPTY_SET' | 'ARC' | 'LH_SIGN_ARROW' | 'RH_SIGN_ARROW' | 'SECTOR' | 'MULTIMER' | 'CONCENTRIC_CIRCLES' | 'ELLIPSE_MULTIMER' | 'IRREGULAR_ROUNDED_RECTANGLE' | 'XSHAPE' | 'BOTTOM_ROUNDED_RECTANGLE' | 'SAUSAGE');



SHAPE_STX
	:	'contains'
	;
	
PROPMODIFIER 
	:	('visualisable'|'read-only');

	
fragment
DIGITS 	:	'0'..'9';

fragment
HEXDIGITS 	:	DIGITS|'A'..'F';

fragment
ASCII_LETTERS :	'A'..'Z'|'a'..'z'|DIGITS|'_';

ID 	:	'A'..'Z' ASCII_LETTERS*;

DOUBLE 	:	 '"'DIGITS+ ('.'  DIGITS+)|'.'  DIGITS+'"';

INT 	:	 '1'..'9' DIGITS* | '0';
//	|	'0';

fragment
SpecSymbols 
	:	'\u0021' |
       '\u0023'..'\u002f' |
       '\u003a'..'\u003b' | '\u003d'|
       '\u003f'..'\u0040' |
       '\u005b'..'\u005e'
	;

fragment
LITERAL_CHAR
	:	ESC
	|	~('"'|'\\')
	;

fragment
ESC	:	'\\'
		(	'n'
		|	'r'
		|	't'
		|	'b'
		|	'f'
		|	'"'
		|	'\''
		|	'\\'
		|	'>'
		|	'u' HEXDIGITS HEXDIGITS HEXDIGITS HEXDIGITS
		|	. // unknown, leave as it is
		)
	;

fragment
Letter
    :  '\u005f' |
       '\u0041'..'\u005a' |
       '\u0061'..'\u007a' |
       '\u00c0'..'\u00d6' |
       '\u00d8'..'\u00f6' |
       '\u00f8'..'\u00ff' |
       '\u0100'..'\u1fff' |
       '\u3040'..'\u318f' |
       '\u3300'..'\u337f' |
       '\u3400'..'\u3d2d' |
       '\u4e00'..'\u9fff' |
       '\uf900'..'\ufaff'
    ;
//NAME 	:	'"' Letter (Letter|' ' )* '"';

Text 	:	'"' LITERAL_CHAR+ '"';


//TYPE 	:	('simple'|'rich'|'number'|'size'|'rgb');
	
SIZE 	:	LeftSqBracket DIGITS+ ',' DIGITS+ RightSqBracket;

RGB 	:	LeftSqBracket ColorDef','ColorDef','ColorDef RightSqBracket;

fragment
ColorDef 
	:	'25' ('0'..'6')
	|	('2') ('0'..'4') DIGITS
	|	('0'..'1') DIGITS DIGITS
	|	DIGITS DIGITS
	|	DIGITS
	; 

HEXRGB 	:	'#' HEXDIGITS HEXDIGITS HEXDIGITS HEXDIGITS HEXDIGITS HEXDIGITS;



Property
	:	'property'
	;


EqualSign
	:	'='
	;


RightBracket
	:	')'
	;

LeftBracket
	:	'('
	;

LeftSqBracket
	:	'['
	;

RightSqBracket
	:	']'
	;

SL_COMMENT
 	:	'//'
 	 	(	~('\r'|'\n')*
		)
		'\r'? '\n'
		{$channel=HIDDEN;}
	;

ML_COMMENT
	:	'/*' {$channel=HIDDEN;} .* '*/'
	;


WS	:	(	' '
		|	'\t'
		|	'\r'? '\n'
		)+
		{$channel=HIDDEN;}
	;

Package	:	'a'..'z' ASCII_LETTERS*
	;

Version	:	DIGITS+ '.' DIGITS+ '.' DIGITS+
	;

EMPTY_STR
	:	'"''"'
	;

