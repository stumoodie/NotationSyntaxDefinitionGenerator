tree grammar NotationGenTree;

options {
	tokenVocab=NotationGen;
	ASTLabelType=CommonTree;
	output=template;
	language=Java;
}


@header{
package org.pathwayeditor.codegenerator.gen;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

}

@members{
  private String qualifiedName;


  public String getQualifiedName(){
    return this.qualifiedName;
  }
}

notation_spec
	:	n=notation_id p+=properties* root s+=shape+ a+=anchor_node* l+=links* h+=parenting_defn*
	-> syntaxServiceClass(notation_id={$n.st}, prop_defns={$p}, shapes={$s}, links={$l}, anchor_nodes={$a}, parenting={$h})
	;
	
notation_id returns[String major, String minor, String patch, String shortName, String packageName]
@after{
  this.qualifiedName = $p.text;
}
	:	^(NOTATION  p=PACKAGE_NAME  ^(NAME n=TEXT) ^(DESCR d=TEXT) v=VERSION_NUM)
	{
	  Scanner s = new Scanner($v.text).useDelimiter("\\.");
	  $major = s.next();
	  $minor = s.next();
	  $patch = s.next();
	  Pattern pat = Pattern.compile("(?:(.*)\\b\\.)?\\b(\\w+)$");
	  Matcher mat = pat.matcher($p.text);
	  if(mat.matches()){
	    $packageName = mat.group(1);
	    $shortName = mat.group(2);
	  }
	 }
	-> notation_id(pn={$packageName}, qn={$p.text}, short_name={$shortName}, name={$n.text}, description={$d.text}, major={$major}, minor={$minor}, patch={$patch})
	;
	
properties
	:	^(PROPERTY ID (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE TEXT_PROP) v=VISUALISABLE?)
	-> define_text_prop(id={$ID.text}, name={$t.text}, descr={$d.text}, visualisable={$v.text})
	|	^(PROPERTY ID (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE REAL_PROP) v=VISUALISABLE?)
	-> define_real_prop(id={$ID.text}, name={$t.text}, descr={$d.text}, visualisable={$v.text})
	|	^(PROPERTY ID (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE INT_PROP) v=VISUALISABLE?)
	-> define_int_prop(id={$ID.text}, name={$t.text}, descr={$d.text}, visualisable={$v.text})
	|	^(PROPERTY ID (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE BOOL_PROP) v=VISUALISABLE?)
	-> define_bool_prop(id={$ID.text}, name={$t.text}, descr={$d.text}, visualisable={$v.text})
	;

root	:	^(ROOT ID)
	;

shape	:	^(SHAPE ID ^(NAME t=TEXT) (^(DESCR d=TEXT))? nd=node_defn fd=font_defn ld=line_defn pr+=prop_ref*)
	->	shape(id={$ID.text}, name={$t.text}, descr={$d.text}, node_defn={$nd.st}, line_defn={$ld.st}, font_defn={$fd.st}, prop_refs={$pr})
	;
	
prop_ref:	^(ID (v=number -> prop_ref(id={$ID.text}, val={$v.st})|b=bool_val -> prop_ref(id={$ID.text}, val={$b.st})|l=string_literal -> prop_ref(id={$ID.text}, val={$l.st})) )
	;
	
node_defn
	:	^(NODE nfd=node_figure_defn sz=node_size c=colour)
	-> node_defn(fig_defn={$nfd.textVal}, node_size={$sz.st}, colour={$c.st})
	;
	
string_literal
	:	TEXT
	-> string_lit(val={$TEXT.text})
	;
	
node_figure_defn returns[String textVal]
	:	^(DEFN TEXT)
	{  $textVal = $TEXT.text; }
	;
	
node_size
	:	^(SIZE_KWD w=number h=number)
		-> node_size(w={$w.st}, h={$h.st})
	;

font_defn
	:	^(FONT c=colour fs=font_style fp=font_pitch)
		-> font_defn(colour={$c.st}, style={$fs.st}, pitch={$fp.st})
	;

font_style
	:	^(STYLE ID)
		-> font_style(id={$ID.text})
	;

font_pitch
	:	^(PITCH n=number)
		-> font_pitch(id={$n.st})
	;

line_defn
	:	^(LINE ls=line_style lw=line_width c=colour)
		-> line_defn(style={$ls.st}, width={$lw.st}, colour={$c.st})
	;

anchor_node
	:	^(ANCHOR_NODE ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? nd=node_defn fd=font_defn ld=line_defn)
	->	anchor_node(id={$ID.text}, name={$t.text}, descr={$d.text}, node_defn={$nd.st}, font_defn={$fd.st}, line_defn={$ld.st})
	;
	
links	:	^(LINK ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? ld=line_defn fd=font_defn p+=port+ e+=valid_ends+ pr+=prop_ref*)
	->	link(id={$ID.text}, name={$t.text}, descr={$d.text}, line_defn={$ld.st}, font_defn={$fd.st}, ports={$p}, prop_refs={$pr}, connections={$e})
	;
	
port	:	^(s=SRC_TERM a=ID o=offset n=node_size)
	->	port(type={$s.text}, arrow_style={$a.text}, gap={$o.st}, size={$n.st})
	|	^(t=TGT_TERM a=ID o=offset n=node_size)
	->	port(type={$t.text}, arrow_style={$a.text}, gap={$o.st}, size={$n.st})
	;

offset	:	^(OFFSET INT)
	->number(val={$INT.text})
	;
	
valid_ends
	:	^(s=ID t+=ID+)
	->	define_connection(src={$s.text}, tgts={$t})
	;
	
parenting_defn
	:	^(p=ID c+=ID+)
	->	define_hierarchy(parent={$p.text}, children={$c})
	;

line_width
	:	^(WIDTH number)
	-> {$number.st}
	;

line_style
	:	^(STYLE ID)
	-> line_style(style={$ID.text})
	;
		
colour	:	^(COLOUR HEXNUMBER)
	-> colour(hex_num={$HEXNUMBER.text})
	;

number
	:	DOUBLE
		-> number(val={$DOUBLE.text})
	|	INT
		-> number(val={$INT.text})
	;

bool_val:	TRUE
	|	FALSE
	;
