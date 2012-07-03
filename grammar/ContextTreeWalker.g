tree grammar ContextTreeWalker;

options {
	tokenVocab=ContextTree;
	ASTLabelType=CommonTree;
	output=template;
}

scope LineScope{
	String lineWidth;
	String lineStyle;
	String lineColour;
	boolean isLW; //is line width editable
	boolean isLS; //is line style editable
	boolean isLC; //is line color editable
//shape part
	String shapeType;
	String shapeSize;
	String shapeColour;
	boolean isST;
	boolean isSS;
	boolean isSC;
}

@header{
package org.pathwayeditor.codegenerator.gen;

import org.antlr.stringtemplate.*;
}

@members{
	List<String> shapeList=new ArrayList<String>();
	List<String> linkList=new ArrayList<String>();
}

context[ List<String> slist, List<String> llist, String packageName]
scope {String objName;} 	
@init{
	this.shapeList=slist;
	this.linkList=llist;
}	
	:	^(ID name description? (^(PACKAGE pack+=Package+))? version rmo ^(PROP_LIST prop+=globalProperties[""]+) ^(SHAPE_LIST sh+=shapes+) ^(LINK_LIST li+=links+)  ) ->
	contexClass(id={$ID.text},pack={packageName.replaceAll("\\.","/")},pn={packageName},name={$name.st},desc={$description.st},ver={$version.text.replace("version=","").replaceAll("\\.","_")}, rmo={$rmo.st},slist={shapeList},llist={linkList},
		prop={$prop},sh={$sh},li={$li})
	;
//.replace("version=","")

version	: 	^('version' Version) -> version(ver={$Version.text})
	;



links	
scope LineScope;
@init{
	$LineScope::lineWidth="1";
	$LineScope::lineStyle="SOLID";
	$LineScope::lineColour="new int[]{0,0,0}";
	$LineScope::isLW=true;//is line width mutable
	$LineScope::isLS=true;//is line style mutable
	$LineScope::isLC=true;//is line colour mutable
	$LineScope::shapeType="RECTANGLE";
	$LineScope::shapeSize="0,0";
	$LineScope::shapeColour="new int[]{255,255,255}";
	$LineScope::isST=true;//is Shape Type mutable
	$LineScope::isSS=true;//is Shape Size mutable
	$LineScope::isSC=true;//is Shape Colour mutable
}
	:	^(ID{$context::objName=$ID.text;} name description? lineProp+ p+=port+ (^(USER_PROP up+=userProp[$ID.text]+))? ^(LINK_STX e+=endDef[$ID.text]+)) 
		-> defineLink(id={$ID.text},name={$name.st},desc={$description.st},up={$up},port={$p},ends={$e},
		lw={$LineScope::lineWidth},ls={$LineScope::lineStyle},lc={$LineScope::lineColour},ilw={$LineScope::isLW},ils={$LineScope::isLS},
		ilc={$LineScope::isLC},ist={$LineScope::isST},iss={$LineScope::isSS},isc={$LineScope::isSC})
	;
	
//properties[boolean isGlobal,String objName]
//scope{ 	boolean global;
//	boolean isEdit;
//	boolean isVis;
//	String val;
//}
//@init{	$properties::global=$isGlobal;
//	$properties::isEdit=true;
//	$properties::isVis=false;
//}
//	:	{ $properties::global}? =>	^(ID name description? propDef (^(VALUE propValue))?) 
//			-> globalProperty(id={$ID.text},name={$name.st},desc={$description.st},def={$propDef.st},val={$propValue.text},ise={$properties::isEdit},isv={$properties::isVis})
//	|	 {!$properties::global}? => ^(ID name {System.out.println("local property"+$name.text+"\t"+$context::objName);} description? propDef ^(VALUE propValue)) 
//			-> localProperty(obj={$objName},id={$ID.text},name={$name.st},desc={$description.st},def={$propDef.st},val={$propValue.text},ise={$properties::isEdit},isv={$properties::isVis})
//	;

globalProperties[String objName]
scope{ 	
	boolean isEdit;
	boolean isVis;
	String val;
}
@init{	
	$globalProperties::isEdit=true;
	$globalProperties::isVis=false;
}
	:	^(ID name description? globalPropDef (^(VALUE propValue))?) 
			-> globalProperty(id={$ID.text},name={$name.st},desc={$description.st},def={$globalPropDef.st},val={$propValue.text},ise={$globalProperties::isEdit},isv={$globalProperties::isVis})
	;

userProperties[String objName]
scope{ 	boolean isEdit;
	boolean isVis;
	String val;
}
@init{	
	$userProperties::isEdit=true;
	$userProperties::isVis=false;
}
	:	^(ID name {System.out.println("local property"+$name.text+"\t"+$context::objName);} description? userPropDef ^(VALUE propValue)) 
			-> localProperty(obj={$objName},id={$ID.text},name={$name.st},desc={$description.st},def={$userPropDef.st},val={$propValue.text},ise={$userProperties::isEdit},isv={$userProperties::isVis})
	;


port	
scope{
	String pType;
}
	: ^('sport' {$port::pType="sport";} portDef) -> port(def={$portDef.st})
	| ^('tport' {$port::pType="tport";} portDef) -> port(def={$portDef.st})
	;

portDef	
scope LineScope;
@init{
	$LineScope::lineWidth="0";
	$LineScope::lineStyle="SOLID";
	$LineScope::lineColour="new int[]{0,0,0}";
	$LineScope::isLW=true;//is line width mutable
	$LineScope::isLS=true;//is line style mutable
	$LineScope::isLC=true;//is line colour mutable
	$LineScope::shapeType="RECTANGLE";
	$LineScope::shapeSize="0,0";
	$LineScope::shapeColour="new int[]{255,255,255}";
	$LineScope::isST=true;//is Shape Type mutable
	$LineScope::isSS=true;//is Shape Size mutable
	$LineScope::isSC=true;//is Shape Colour mutable
}
	:	^(ArrowheadStyle SIZE? offset? sd+=shapeDef[$port::pType]+ (^(USER_PROP up+=userProp[$port::pType]+))?) 
	-> portDef(pt={$port::pType},ast={$ArrowheadStyle.text},asz={($SIZE.text==null)?"8,8":$SIZE.text.replace("[","").replace("]","")},
		ofs={$offset.st},st={$LineScope::shapeType},ss={$LineScope::shapeSize},sc={$LineScope::shapeColour},
		lw={$LineScope::lineWidth},ls={$LineScope::lineStyle},lc={$LineScope::lineColour},up={$up},ilw={$LineScope::isLW},ils={$LineScope::isLS},
		ilc={$LineScope::isLC},ist={$LineScope::isST},iss={$LineScope::isSS},isc={$LineScope::isSC})
	;

/**
 * Set offset between shape boundary and link visual end. 
 * by default offset is 0
 */
offset	:	^('offset' INT) -> offset(pt={$port::pType},v={$INT.text})
	;


/**
 * Definition of link end decorators together with connectivity validation
 */
endDef[String objName]	:	^(ID listCSV) -> endDef(id={$objName},src={$ID.text},list={$listCSV.st})
	;

shapes	
scope LineScope;
@init{
	$LineScope::lineWidth="1";
	$LineScope::lineStyle="SOLID";
	$LineScope::lineColour="new int[]{0,0,0}";
	$LineScope::isLW=true;//is line width mutable
	$LineScope::isLS=true;//is line style mutable
	$LineScope::isLC=true;//is line colour mutable
	$LineScope::shapeType="RECTANGLE";
	$LineScope::shapeSize="10,10";
	$LineScope::shapeColour="new int[]{255,255,255}";
	$LineScope::isST=true;//is Shape Type mutable
	$LineScope::isSS=true;//is Shape Size mutable
	$LineScope::isSC=true;//is Shape Colour mutable
}
	:
	^(ID{$context::objName=$ID.text;}  name description? sd+=shapeDef[$ID.text]+ (^(USER_PROP up+=userProp[$ID.text]+))? containment[$ID.text]) 
		-> defineShape(id={$ID.text},name={$name.st},desc={$description.st},sd={$sd},up={$up},cont={$containment.st},
		lw={$LineScope::lineWidth},ls={$LineScope::lineStyle},lc={$LineScope::lineColour},ilw={$LineScope::isLW},ils={$LineScope::isLS},
		ilc={$LineScope::isLC},ist={$LineScope::isST},iss={$LineScope::isSS},isc={$LineScope::isSC})
	;

rmo	:	^('RMO'{$context::objName="rmo";} (^(USER_PROP up+=userProp["rmo"]+))? containment["rmo"]) -> rmo(up={$up},cont={$containment.st})
	;
/**
 * Defines parent/child relationship in the context. Conatinment describe rules to identify 
 * valid child for a parent and valid parent for a child. List in a square brackets define 
 * set of vaild children for enclosing shapes. List could be empty, which means that the shape 
 * is always a leaf in the parenting tree.  
 * It is able to use NOT operator to define a lis by excluding invalid children from all shapes defined in the context.
 * For example: contains [], contains [Macromolecule, Compound], contains ![Process]
 */
containment[String objName]
	: ^(SHAPE_STX listCSV) -> containsList(shape={$objName},list={$listCSV.st})
	| SHAPE_STX -> containsNone(shape={$objName})
	;

/**
 * Comma separated list of object IDs. Allow to define list directly or via exclusion list.
 */
listCSV	:	^(VLIST l+=ID+) -> addList(list={$l})
	|	^('!' ^(VLIST l+=ID+)) -> addAllExcept(main={shapeList},remove={$l})
	|	^(VLIST '*') -> addList(list={shapeList})
//	|	^('!' ^(VLIST '*')) -> addNone()
	;


userPropDef	: type  modifyProp? {if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $userProperties::isEdit=false;if($modifyProp.text!=null && $modifyProp.text.contains("visualisable")) $userProperties::isVis=true;}  -> {$type.st}
	
	;

globalPropDef	: type  modifyProp? {if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $globalProperties::isEdit=false;if($modifyProp.text!=null && $modifyProp.text.contains("visualisable")) $globalProperties::isVis=true;}  -> {$type.st}
	
	;

userProp[String objName]
	:	^(ID modifyProp? ^(VALUE propValue)) -> assignVal(obj={$objName},id={$ID.text},val={$propValue.text},ise={!($modifyProp.text!=null && $modifyProp.text.contains("read-only"))},isv={$modifyProp.text!=null && $modifyProp.text.contains("visualisable")})
	|	^(PREDEFINED ^(PredefinedProperty  modifyProp? ^(VALUE propValue))) -> assignPredef(obj={$objName},id={$PredefinedProperty.text},val={$propValue.text},ise={!($modifyProp.text!=null && $modifyProp.text.contains("read-only"))},isv={$modifyProp.text!=null && $modifyProp.text.contains("visualisable")})
	|	userProperties[$objName] -> {$userProperties.st}
	;
	
modifyProp
	:	PROPMODIFIER+ 
	;
	
shapeDef[String objName]
	:	^('stype' modifyProp?   ShapeType) {if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isST=false;$LineScope::shapeType=$ShapeType.text;} 
		-> setShapeType(id={$objName},type={$ShapeType.text})
	|	^('size'  modifyProp? {if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isSS=false;}  SIZE) {$LineScope::shapeSize=$SIZE.text.replace("[","").replace("]","");}  
		-> setSize(id={$objName},size={$SIZE.text.replace("[","").replace("]","")})
	|	^('fcolor' modifyProp? color)  {if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isSC=false;$LineScope::shapeColour=$color.st.toString();} 
		-> setFillProperty(id={$objName},colour={$color.st})
	|	lineProp
	;
	
/*Definition of visual properties like fill type, color etc.
*/
lineProp
	:  	^('lstyle'  modifyProp? LineStyle) {$LineScope::lineStyle=$LineStyle.text; if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isLS=false;}
	|	^('lwidth' modifyProp? INT)  {$LineScope::lineWidth=$INT.text; if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isLW=false;}
	|	^('lcolor' modifyProp? color)  {$LineScope::lineColour=$color.st.toString(); if($modifyProp.text!=null && $modifyProp.text.contains("read-only")) $LineScope::isLC=false;}
//	|	^('transpacency' modifyProp? Transparency)
	;

/**
 * Color definition. Could be triplet of integers of hex value:
 * [255,255,255]
 * #FFFFFF
 */
color:		RGB 
		-> rgbT(rgb={$RGB.text.replace("[","").replace("]","")})
	|	HEXRGB -> hexT(rgb={$HEXRGB.text})
	;

	
name 	:	^('name' Text) -> setName(name={$Text.text})
	; //Name does not require to be single world  

description 
	:	^('descr' Text) -> setDescription(descr={$Text.text})
	;
/**Type of property
*/
type 	:	^('type' ('simple' -> simpleType()
		|'rich' -> richType()
		|'number' -> numberType()
		|'size'
		|'rgb')
		)
	;


propValue 
	:	Text
	|	html
	|	DOUBLE
	|	SIZE
	|	color;
	
html 	:	'"<' '>"';

