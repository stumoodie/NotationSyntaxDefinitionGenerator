/**

*/
grammar NotationGen;

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

notation_spec :	notation_id properties* rmo label* shape+ anchor_node* links* parenting_defn* EOF
	 -> notation_id rmo label* properties* shape+ anchor_node* links* parenting_defn*
	;

notation_id
	:	NOTATION PACKAGE_NAME  '(' name description version ')'
		 -> ^(NOTATION  PACKAGE_NAME  name description version)
	;

version	:	VERSION '=' VERSION_NUM
		-> VERSION_NUM
	;


properties
	:	PROPERTY ID label_ref?  '(' name description type ')' prop_init_val
		-> ^(PROPERTY ID label_ref? name description type prop_init_val)
	;

prop_init_val
	:	'=' (number -> ^('=' number)|bool_val -> ^('=' bool_val) |TEXT -> ^('=' TEXT) )
	;

label	:	LABEL ID '(' name description? node_defn font_defn line_defn ')'
		-> ^(LABEL ID name description? node_defn font_defn line_defn)
	;

shape	: 	SHAPE ID '(' name description? node_defn font_defn line_defn prop_defn? ')'
		 -> ^(SHAPE ID name description? node_defn font_defn line_defn prop_defn?)
	;

node_defn
	:	NODE '('  node_figure_defn node_size colour ')'
	-> ^(NODE node_figure_defn node_size colour)
	;

line_defn
	:	LINE '(' line_style line_width colour ')'
	-> ^(LINE line_style line_width colour)
	;
	
font_defn
	:	FONT '(' colour font_style font_pitch ')'
	-> ^(FONT colour font_style font_pitch)
	;
	
font_style
	:	STYLE '=' ID
	-> ^(STYLE ID)
	;

font_pitch
	:	PITCH '=' number
	-> ^(PITCH number)
	;

prop_defn
	:	PROPERTY '(' ID+ ')'
	-> ^(PROPERTY ID+)
	;

label_ref
	:	'{' ID '}'
	->	^(LABEL ID)
	;

colour
	:	COLOUR '='  color
	-> ^(COLOUR color)
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
	: 	ANCHOR_NODE ID '(' name description? node_defn font_defn line_defn ')'
		 -> ^(ANCHOR_NODE ID name description? node_defn font_defn line_defn)
	;

links	:	LINK ID '(' name description? line_defn port+ prop_defn?  valid_ends+ ')'
		-> ^(LINK ID name description? line_defn port+ prop_defn? valid_ends+) 
	;
	
port	: 	(s=SRC_TERM|s=TGT_TERM) '(' ID offset node_size ')'
		-> ^($s ID offset node_size)
	;

offset	:	OFFSET '=' INT
		-> ^(OFFSET INT)
	;


valid_ends
	:	ID bracketed_list
	-> ^(ID bracketed_list)
	;

rmo	:	ROOT ID '(' ')'
		 -> ^(ROOT ID)
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
	;


line_width
	:	WIDTH '=' number
	-> ^(WIDTH number)
	;

line_style
	:	STYLE '=' ID
	-> ^(STYLE ID)
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

LABEL	:	'label'
	;

LINK	:	'link'
	;

ROOT	:	'root'
	;

SIZE_KWD:	'size'
	;

SRC_TERM:	'source'
	;
	
TGT_TERM:	'target'
	;

DEFN
	:	'defn'
	;

COLOUR
	:	'colour'
	;

STYLE
	:	'style'
	;

WIDTH
	:	'width'
	;

NAME
	:	'name'
	;

NODE	:	'node'
	;

LINE	:	'line'
	;

FONT	:	'font'
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
	
PROPERTY
	:	'property'
	;

PITCH	:	'pitch'
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


