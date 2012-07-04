/**

*/
grammar ContextTree;

options {
	output=AST;
	ASTLabelType=CommonTree;
	language=Java;
}

tokens{
	SIZE;
}


@header{
package org.pathwayeditor.codegenerator.gen;
}
@lexer::header{
package org.pathwayeditor.codegenerator.gen;
}

@members{
	}

notation_spec :	notation_id properties+ rmo shape+ anchor_node* links* parenting_defn* EOF
	 -> ^(notation_id properties+ rmo shape+ anchor_node* links* parenting_defn*)
	;

notation_id returns[String id]
	:	NOTATION PACKAGE_NAME  '(' name description version ')'
		 -> ^(NOTATION  PACKAGE_NAME  name description version)
	;

version	:	VERSION '=' VERSION_NUM
		-> VERSION_NUM
	;


properties
	:	PROPERTY ID '(' name description type  VISUALISABLE? ')'
		-> ^(PROPERTY ID name description type VISUALISABLE?)
	;

shape	: 	SHAPE ID '(' name description? node_figure_defn node_size fill_colour line_style line_width line_colour prop_ref* ')'
		 -> ^(SHAPE ID name description? node_figure_defn node_size fill_colour line_style line_width line_colour prop_ref*)
	;

prop_ref:	ID '=' (n=number|b=bool_val|t=TEXT)
		-> ^(ID $n? $b? $t?)
	;

fill_colour
	:	FCOLOUR '='  color
	-> ^(FCOLOUR color)
	;

node_figure_defn
	:	DEFN '=' TEXT
	-> ^(DEFN TEXT)
	;

node_size
	:	SIZE_KWD '=' '[' number ',' number ']'
		-> ^(SIZE_KWD number number)
	;

anchor_node
	: 	ANCHOR_NODE ID '(' name description? node_figure_defn node_size fill_colour line_style line_width line_colour ')'
		 -> ^(ANCHOR_NODE ID name description? node_figure_defn node_size fill_colour line_style line_width line_colour)
	;

links	:	LINK ID '(' name description? line_style line_width line_colour port+ valid_ends+ prop_ref* ')'
		-> ^(LINK ID name description? line_style line_width line_colour port+ valid_ends+ prop_ref*) 
	;
	
port	: 	(s=SRC_TERM|s=TGT_TERM) '=' ARROWHEADSTYLE '(' offset node_size ')'
		-> ^($s  ARROWHEADSTYLE offset node_size)
	;

offset	:	OFFSET '=' INT
		-> ^(OFFSET INT)
	;


valid_ends
	:	ID bracketed_list
	-> ^(ID bracketed_list)
	;

rmo	:	ROOT '(' ')'
		 -> ROOT
	;

/**
 * Defines parent/child relationship in the context. Conatinment describe rules to identify 
 * valid child for a parent and valid parent for a child. List in a square brackets define 
 * set of vaild children for enclosing shapes. List could be empty, which means that the shape 
 * is always a leaf in the parenting tree.  
 * It is able to use NOT operator to define a lis by excluding invalid children from all shapes defined in the context.
 * For example: contains [], contains [Macromolecule, Compound], contains ![Process]
 */
parenting_defn
	: ID '^' bracketed_list
		-> ^(ID bracketed_list) 
	;

bracketed_list	:	'[' ID (',' ID)* ']' -> ID+
	|	'[' '*' ']' -> '*'
	;


line_colour
	:	LCOLOUR '=' color
	-> ^(LCOLOUR color)
	;

line_width
	:	LWIDTH '=' number
	-> ^(LWIDTH number)
	;

line_style
	:	LSTYLE '=' LINE_STYLE
	-> ^(LSTYLE LINE_STYLE)
	;
		
color	:	HEXNUMBER
	;


name 	:	NAME '=' TEXT
		-> ^(NAME TEXT)
	; //Name should not be single world  

description 
	:	DESCR '='  TEXT
		-> ^(DESCR TEXT)
	;
//Type of property
type 	:	TYPE '=' (TEXT_PROP|REAL_PROP|BOOL_PROP|INT_PROP)//;TYPE
	-> ^(TYPE TEXT_PROP? REAL_PROP? BOOL_PROP? INT_PROP?)
	;

number	:	DOUBLE
	|	INT
	;

bool_val:	TRUE
	|	FALSE
	;

NOTATION
	:	'notation'
	;
	
VERSION	:	'version'
	;
	
ANCHOR_NODE
	:	'anchorNode'
	;
	
SHAPE	:	'shape'
	;

LINK	:	'link'
	;

ROOT	:	'root'
	;

SIZE_KWD:	'size'
	;

SRC_TERM:	'sterm'
	;
	
TGT_TERM:	'tterm'
	;

DEFN
	:	'defn'
	;

FCOLOUR
	:	'fcolor'
	;

LSTYLE
	:	'lstyle'
	;

LWIDTH
	:	'lwidth'
	;

LCOLOUR
	:	'lcolor'
	;

NAME
	:	'name'
	;

DESCR
	:	'descr'
	;

TYPE
	:	'type'
	;

TEXT_PROP
	:	'text'
	;

REAL_PROP
	:	'real'
	;

BOOL_PROP
	:	'boolean'
	;

INT_PROP
	:	'integer'
	;

OFFSET
	:	'offset'
	;

ARROWHEADSTYLE
	:	'DIAMOND'|'ARROW'|'DOUBLE_ARROW'|'TRIANGLE'|'EMPTY_DIAMOND'|'BAR'|'DOUBLE_BAR'|'EMPTY_CIRCLE'|'NONE'|'SQUARE'|'EMPTY_SQUARE'|'EMPTY_TRIANGLE'|'TRIANGLE_BAR'
	;
	
LINE_STYLE 
	:	'SOLID' | 'DASHED' | 'DASH_DOT' | 'DASH_DOT_DOT' | 'DOT'
	;

	
VISUALISABLE 
	:	'visualisable';

PROPERTY
	:	'property'
	;

TRUE	:	'true'
	;
	
FALSE	:	'false'
	;
	
fragment
DIGIT 	:	'0'..'9';

fragment
HEXDIGIT 	:	DIGIT|'A'..'F'|'a'..'f';

fragment
ASCII_LETTERS :	'A'..'Z'|'a'..'z'|DIGIT|'_';

ID 	:	('A'..'Z'|'a'..'z') ASCII_LETTERS*;

DOUBLE 	:	 DIGIT+ ('.'  DIGIT+)|'.'  DIGIT+;

INT 	:	 DIGIT+
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
		|	'u' HEXDIGIT HEXDIGIT HEXDIGIT HEXDIGIT
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

TEXT 	:	'"' LITERAL_CHAR+ '"'
	;

HEXNUMBER
 	:	'#' HEXDIGIT+
 	;


SL_COMMENT
 	:	'//'
 	 	(	~('\r'|'\n')*
		)
		'\r'? '\n'
		{$channel=HIDDEN;}
	;

ML_COMMENT
	:	'/*' .* '*/' {$channel=HIDDEN;}
	;


WS	:	(	' '
		|	'\t'
		|	'\r'? '\n'
		)+
		{$channel=HIDDEN;}
	;

PACKAGE_NAME
	:	ID('.' ID)*
	;

VERSION_NUM	
	:	DIGIT+ '.' DIGIT+ '.' DIGIT+
	;


