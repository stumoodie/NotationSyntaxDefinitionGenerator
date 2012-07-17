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
	:	n=notation_id r=root labs+=label* p+=properties* s+=shape+ a+=anchor_node* l+=links* h+=parenting_defn*
	-> syntaxServiceClass(notation_id={$n.st}, root={$r.st}, prop_defns={$p}, labels={$labs}, shapes={$s}, links={$l}, anchor_nodes={$a}, parenting={$h})
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
	:	^(PROPERTY id=ID (^(LABEL l=ID))? (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE TEXT_PROP) iv=prop_init_val ) 
	-> define_text_prop(id={$id.text}, label_id={$l.text}, name={$t.text}, descr={$d.text}, value={$iv.st})
	|	^(PROPERTY id=ID (^(LABEL l=ID))? (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE REAL_PROP) iv=prop_init_val )
	-> define_real_prop(id={$id.text}, label_id={$l.text}, name={$t.text}, descr={$d.text}, value={$iv.st})
	|	^(PROPERTY id=ID (^(LABEL l=ID))? (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE INT_PROP) iv=prop_init_val )
	-> define_int_prop(id={$id.text}, label_id={$l.text}, name={$t.text}, descr={$d.text}, value={$iv.st})
	|	^(PROPERTY id=ID (^(LABEL l=ID))? (^(NAME t=TEXT)) (^(DESCR d=TEXT)) ^(TYPE BOOL_PROP) iv=prop_init_val )
	-> define_bool_prop(id={$id.text}, label_id={$l.text}, name={$t.text}, descr={$d.text}, value={$iv.st})
	;

prop_init_val
	:	^('=' (number -> {$number.st} |bool_val -> {$bool_val.st}|string_literal -> {$string_literal.st}) )
	;

root	:	^(ROOT ID)
	-> root(id={$ID.text})
	;

label	:	^(LABEL ID ^(NAME t=TEXT) (^(DESCR d=TEXT))? f=format? nd=node_defn fd=font_defn ld=line_defn)
	->	label(id={$ID.text}, format={$f.st}, name={$t.text}, descr={$d.text}, node_defn={$nd.st}, line_defn={$ld.st}, font_defn={$fd.st})
	;

format	:	^(FORMAT t=TEXT)
	->  format(text={$t.text})
	;

shape	:	^(SHAPE ID ^(NAME t=TEXT) (^(DESCR d=TEXT))? nd=node_defn fd=font_defn ld=line_defn pd=prop_defn?)
	->	shape(id={$ID.text}, name={$t.text}, descr={$d.text}, node_defn={$nd.st}, line_defn={$ld.st}, font_defn={$fd.st}, prop_defn={$pd.st})
	;
	
prop_defn
	:	^(PROPERTY pr+=ID+)
	->	prop_defn(prop_refs={$pr})
	;	

node_defn
@init {CommonTree t = (CommonTree)input.LT(1);}
    	:  ^(NODE nfd=node_figure_defn sz=node_size c=colour)
    	-> {t.hasAncestor(LABEL)}? label_node_defn(fig_defn={$nfd.textVal}, node_size={$sz.st}, colour={$c.st})
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
	-> font_style(style={$ID.text})
	;

font_pitch
	:	^(PITCH n=number)
	-> font_pitch(pitch={$n.st})
	;

line_defn
	:	^(LINE ls=line_style lw=line_width c=colour)
	-> line_defn(style={$ls.st}, width={$lw.st}, colour={$c.st})
	;

anchor_node
	:	^(ANCHOR_NODE ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? nd=node_defn fd=font_defn ld=line_defn)
	->	anchor_node(id={$ID.text}, name={$t.text}, descr={$d.text}, node_defn={$nd.st}, font_defn={$fd.st}, line_defn={$ld.st})
	;
	
links	:	^(LINK ID (^(NAME t=TEXT)) (^(DESCR d=TEXT))? ld=line_defn p+=port+ pd=prop_defn? e+=valid_ends+ )
	->	link(id={$ID.text}, name={$t.text}, descr={$d.text}, line_defn={$ld.st}, ports={$p}, prop_defn={$pd.st}, connections={$e})
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
