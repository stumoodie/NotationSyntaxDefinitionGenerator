tree grammar ContextTreeWalker;

options {
	tokenVocab=ContextTree;
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
	:	n=notation_id p+=properties* ROOT s+=shape+ a+=anchor_node* l+=links* h+=parenting_defn*
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
	
shape	:	^(SHAPE ID ^(NAME t=TEXT) (^(DESCR d=TEXT))? nfd=node_figure_defn sz=node_size fc=fill_colour ls=line_style lw=line_width lc=line_colour pr+=prop_ref*)
	->	shape(id={$ID.text}, name={$t.text}, descr={$d.text}, fig_defn={$nfd.textVal}, node_size={$sz.st}, fill_colour={$fc.st}, 
			line_style={$ls.st}, line_width={$lw.st}, line_colour={$lc.st}, prop_refs={$pr})
	;
	
prop_ref:	^(ID (v=number -> prop_ref(id={$ID.text}, val={$v.st})|b=bool_val -> prop_ref(id={$ID.text}, val={$b.st})|l=string_literal -> prop_ref(id={$ID.text}, val={$l.st})) )
	;
	
string_literal
	:	TEXT
	-> string_lit(val={$TEXT.text})
	;
	
fill_colour
	:	^(FCOLOUR color)
	->	{$color.st}
	;
	
node_figure_defn returns[String textVal]
	:	^(DEFN TEXT)
	{  $textVal = $TEXT.text; }
	;
	
node_size
	:	^(SIZE_KWD w=number h=number)
		-> node_size(w={$w.st}, h={$h.st})
	;
	
anchor_node
	:	^(ANCHOR_NODE ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? nfd=node_figure_defn sz=node_size fc=fill_colour ls=line_style lw=line_width lc=line_colour)
	->	anchor_node(id={$ID.text}, name={$t.text}, descr={$d.text}, fig_defn={$nfd.textVal}, node_size={$sz.st}, fill_colour={$fc.st}, 
			line_style={$ls.st}, line_width={$lw.st}, line_colour={$lc.st})
	;
	
links	:	^(LINK ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? ls=line_style lw=line_width lc=line_colour p+=port+ e+=valid_ends+ pr+=prop_ref*)
	->	link(id={$ID.text}, name={$t.text}, descr={$d.text}, line_style={$ls.st}, line_width={$lw.st}, line_colour={$lc.st}, ports={$p}, prop_refs={$pr}, connections={$e})
	;
	
port	:	^(s=SRC_TERM a=ARROWHEADSTYLE o=offset n=node_size)
	->	port(type={$s.text}, arrow_style={$a.text}, gap={$o.st}, size={$n.st})
	|	^(t=TGT_TERM a=ARROWHEADSTYLE o=offset n=node_size)
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

line_colour
	:	^(LCOLOUR color)
	->	{$color.st}
	;

line_width
	:	^(LWIDTH number)
	-> {$number.st}
	;

line_style
	:	^(LSTYLE LINE_STYLE)
	-> line_style(style={$LINE_STYLE.text})
	;
		
color	:	HEXNUMBER
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
