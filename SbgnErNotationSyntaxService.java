package org.pathwayeditor.notations.sbgner.services;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

import org.pathwayeditor.businessobjects.drawingprimitives.attributes.ConnectionRouter;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.LineStyle;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.LinkEndDecoratorShape;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.PrimitiveShapeType;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.RGB;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.Size;
import org.pathwayeditor.businessobjects.drawingprimitives.properties.IPropertyDefinition;
import org.pathwayeditor.businessobjects.notationsubsystem.INotation;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSubsystem;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSyntaxService;
import org.pathwayeditor.businessobjects.typedefn.ILinkObjectType;
import org.pathwayeditor.businessobjects.typedefn.IObjectType;
import org.pathwayeditor.businessobjects.typedefn.IRootObjectType;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType;
import org.pathwayeditor.businessobjects.typedefn.ILinkObjectType.LinkEditableAttributes;
import org.pathwayeditor.businessobjects.typedefn.ILinkTerminusDefinition.LinkTermEditableAttributes;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType.EditableShapeAttributes;
import org.pathwayeditor.notationsubsystem.toolkit.definition.FormattedTextPropertyDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.LinkObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.LinkTerminusDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.NumberPropertyDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.PlainTextPropertyDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.RootObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.ShapeObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.TextPropertyDefinition;

public class SbgnErNotationSyntaxService implements INotationSyntaxService {
  private static final int NUM_ROOT_OTS = 1;
  private final INotation context;
  private final Map <Integer, IShapeObjectType> shapes = new HashMap<Integer, IShapeObjectType>(); 
  private final Map <Integer, ILinkObjectType> links = new HashMap<Integer, ILinkObjectType>();
//  private final Set <IPropertyDefinition> propSet=new HashSet<IPropertyDefinition>();
  
  private RootObjectType rmo;
  //shapes
  private ShapeObjectType State;
  private ShapeObjectType LocState;
  private ShapeObjectType ExState;
  private ShapeObjectType UnitOfInf;
  private ShapeObjectType Entity;
  private ShapeObjectType Interaction;
  private ShapeObjectType Outcome;
  private ShapeObjectType Perturbation;
  private ShapeObjectType Observable;
  private ShapeObjectType Submap;
  private ShapeObjectType Interface;
  private ShapeObjectType Tag;
  private ShapeObjectType AndGate;
  private ShapeObjectType OrGate;
  private ShapeObjectType NotGate;
  private ShapeObjectType DelayGate;
  private ShapeObjectType Decision;
  private ShapeObjectType Acceptor;

  //links
  private LinkObjectType Modulation;
  private LinkObjectType Stimulation;
  private LinkObjectType Catalysis;
  private LinkObjectType Inhibition;
  private LinkObjectType AbsInhibition;
  private LinkObjectType NecessaryStimulation;
  private LinkObjectType LogicArc;
  private LinkObjectType InteractionArc;
  private LinkObjectType AssignmenArc;
  private LinkObjectType VarArc;
  private LinkObjectType EquivalenceArc;

  
  
  private INotationSubsystem serviceProvider;

	private static int[] getRGB(String hex) {
		hex = hex.replace("#", "");
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4), 16);
		return new int[] { r, g, b };
	}

	private static IPropertyDefinition reassignVal(IPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		if( prop instanceof TextPropertyDefinition) return reassignVal((TextPropertyDefinition) prop,val,isEdit,isVis);
		if( prop instanceof FormattedTextPropertyDefinition) return reassignVal((FormattedTextPropertyDefinition) prop,val,isEdit,isVis);
		if( prop instanceof NumberPropertyDefinition) return reassignVal((NumberPropertyDefinition) prop,val,isEdit,isVis);
		return prop;
	}
	
	private static PlainTextPropertyDefinition reassignVal(TextPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		PlainTextPropertyDefinition newP=new PlainTextPropertyDefinition(prop.getName(),val,(prop.isVisualisable() | isVis),(prop.isEditable()&isEdit));
  //  if(newP.isVisualisable())newP.setAppearance(prop.getAppearance());
		return newP;
	}
	
	private static FormattedTextPropertyDefinition reassignVal(FormattedTextPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		FormattedTextPropertyDefinition newP=new FormattedTextPropertyDefinition(prop.getName(),val,(prop.isVisualisable() | isVis),(prop.isEditable()&isEdit));
//		if(newP.isVisualisable())newP.setAppearance(prop.getAppearance());
		return newP;
	}
	
	private static NumberPropertyDefinition reassignVal(NumberPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		NumberPropertyDefinition newP=new NumberPropertyDefinition(prop.getName(), new BigDecimal(val),(prop.isVisualisable() | isVis),(prop.isEditable()&isEdit));
 //   if(newP.isVisualisable())newP.setAppearance(prop.getAppearance());
		return newP;
	}
	

	public SbgnErNotationSyntaxService(INotationSubsystem serviceProvider) {
		this.serviceProvider=serviceProvider;
		this.context = serviceProvider.getNotation();
		//"SBGN-ER"
		//"SBGN entity-relationship diagram language notation"
		//0_0_0
		createRMO();
	//shapes
	this.State= new ShapeObjectType(this,10, "State");
	createState();
	this.LocState= new ShapeObjectType(this,11, "LocState");
	createLocState();
	this.ExState= new ShapeObjectType(this,12, "ExState");
	createExState();
	this.UnitOfInf= new ShapeObjectType(this,13, "UnitOfInf");
	createUnitOfInf();
	this.Entity= new ShapeObjectType(this,14, "Entity");
	createEntity();
	this.Interaction= new ShapeObjectType(this,15, "Interaction");
	createInteraction();
	this.Outcome= new ShapeObjectType(this,16, "Outcome");
	createOutcome();
	this.Perturbation= new ShapeObjectType(this,17, "Perturbation");
	createPerturbation();
	this.Observable= new ShapeObjectType(this,18, "Observable");
	createObservable();
	this.Submap= new ShapeObjectType(this,19, "Submap");
	createSubmap();
	this.Interface= new ShapeObjectType(this,110, "Interface");
	createInterface();
	this.Tag= new ShapeObjectType(this,111, "Tag");
	createTag();
	this.AndGate= new ShapeObjectType(this,112, "AndGate");
	createAndGate();
	this.OrGate= new ShapeObjectType(this,113, "OrGate");
	createOrGate();
	this.NotGate= new ShapeObjectType(this,114, "NotGate");
	createNotGate();
	this.DelayGate= new ShapeObjectType(this,115, "DelayGate");
	createDelayGate();
	this.Decision= new ShapeObjectType(this,116, "Decision");
	createDecision();
	this.Acceptor= new ShapeObjectType(this,117, "Acceptor");
	createAcceptor();

		defineParentingRMO();
	//shapes parenting
		defineParentingState();
		defineParentingLocState();
		defineParentingExState();
		defineParentingUnitOfInf();
		defineParentingEntity();
		defineParentingInteraction();
		defineParentingOutcome();
		defineParentingPerturbation();
		defineParentingObservable();
		defineParentingSubmap();
		defineParentingInterface();
		defineParentingTag();
		defineParentingAndGate();
		defineParentingOrGate();
		defineParentingNotGate();
		defineParentingDelayGate();
		defineParentingDecision();
		defineParentingAcceptor();

	//links
	this.Modulation = new LinkObjectType(this, 20, "Modulation");
	createModulation();
	this.Stimulation = new LinkObjectType(this, 21, "Stimulation");
	createStimulation();
	this.Catalysis = new LinkObjectType(this, 22, "Catalysis");
	createCatalysis();
	this.Inhibition = new LinkObjectType(this, 23, "Inhibition");
	createInhibition();
	this.AbsInhibition = new LinkObjectType(this, 24, "AbsInhibition");
	createAbsInhibition();
	this.NecessaryStimulation = new LinkObjectType(this, 25, "NecessaryStimulation");
	createNecessaryStimulation();
	this.LogicArc = new LinkObjectType(this, 26, "LogicArc");
	createLogicArc();
	this.InteractionArc = new LinkObjectType(this, 27, "InteractionArc");
	createInteractionArc();
	this.AssignmenArc = new LinkObjectType(this, 28, "AssignmenArc");
	createAssignmenArc();
	this.VarArc = new LinkObjectType(this, 29, "VarArc");
	createVarArc();
	this.EquivalenceArc = new LinkObjectType(this, 210, "EquivalenceArc");
	createEquivalenceArc();

	//shape set
		this.shapes.put(this.State.getUniqueId(), this.State);
		this.shapes.put(this.LocState.getUniqueId(), this.LocState);
		this.shapes.put(this.ExState.getUniqueId(), this.ExState);
		this.shapes.put(this.UnitOfInf.getUniqueId(), this.UnitOfInf);
		this.shapes.put(this.Entity.getUniqueId(), this.Entity);
		this.shapes.put(this.Interaction.getUniqueId(), this.Interaction);
		this.shapes.put(this.Outcome.getUniqueId(), this.Outcome);
		this.shapes.put(this.Perturbation.getUniqueId(), this.Perturbation);
		this.shapes.put(this.Observable.getUniqueId(), this.Observable);
		this.shapes.put(this.Submap.getUniqueId(), this.Submap);
		this.shapes.put(this.Interface.getUniqueId(), this.Interface);
		this.shapes.put(this.Tag.getUniqueId(), this.Tag);
		this.shapes.put(this.AndGate.getUniqueId(), this.AndGate);
		this.shapes.put(this.OrGate.getUniqueId(), this.OrGate);
		this.shapes.put(this.NotGate.getUniqueId(), this.NotGate);
		this.shapes.put(this.DelayGate.getUniqueId(), this.DelayGate);
		this.shapes.put(this.Decision.getUniqueId(), this.Decision);
		this.shapes.put(this.Acceptor.getUniqueId(), this.Acceptor);

	//link set
		this.links.put(this.Modulation.getUniqueId(), this.Modulation);
		this.links.put(this.Stimulation.getUniqueId(), this.Stimulation);
		this.links.put(this.Catalysis.getUniqueId(), this.Catalysis);
		this.links.put(this.Inhibition.getUniqueId(), this.Inhibition);
		this.links.put(this.AbsInhibition.getUniqueId(), this.AbsInhibition);
		this.links.put(this.NecessaryStimulation.getUniqueId(), this.NecessaryStimulation);
		this.links.put(this.LogicArc.getUniqueId(), this.LogicArc);
		this.links.put(this.InteractionArc.getUniqueId(), this.InteractionArc);
		this.links.put(this.AssignmenArc.getUniqueId(), this.AssignmenArc);
		this.links.put(this.VarArc.getUniqueId(), this.VarArc);
		this.links.put(this.EquivalenceArc.getUniqueId(), this.EquivalenceArc);
		
	}

  public INotationSubsystem getNotationSubsystem() {
    return serviceProvider;
  }
  
  public INotation getNotation() {
    return this.context;
  }

  public Iterator<ILinkObjectType> linkTypeIterator() {
    return this.links.values().iterator();
  }

  public IRootObjectType getRootObjectType() {
    return this.rmo;
  }

  public Iterator<IShapeObjectType> shapeTypeIterator() {
    return this.shapes.values().iterator();
  }
  
  
  public Iterator<IObjectType> objectTypeIterator(){
    Set<IObjectType> retVal = new HashSet<IObjectType>(this.shapes.values());
    retVal.addAll(this.links.values());
    retVal.add(this.rmo);
    return retVal.iterator();
  }
  
  public boolean containsLinkObjectType(int uniqueId) {
    return this.links.containsKey(uniqueId);
  }

  public boolean containsObjectType(int uniqueId) {
    boolean retVal = this.links.containsKey(uniqueId);
    if(!retVal){
      retVal = this.shapes.containsKey(uniqueId);
    }
    if(!retVal){
      retVal = this.rmo.getUniqueId() == uniqueId;
    }
    return retVal;
  }

  public boolean containsShapeObjectType(int uniqueId) {
    return this.shapes.containsKey(uniqueId);
  }

  public ILinkObjectType getLinkObjectType(int uniqueId) {
    return this.links.get(uniqueId);
  }

  public IObjectType getObjectType(int uniqueId) {
    IObjectType retVal = this.links.get(uniqueId);
    if(retVal == null){
      retVal = this.shapes.get(uniqueId);
    }
    if(retVal == null){
      retVal = (this.rmo.getUniqueId() == uniqueId) ? this.rmo : null;
    }
    if(retVal == null){
      throw new IllegalArgumentException("The unique Id was not present in this notation subsystem");
    }
    return retVal;
  }

  public IShapeObjectType getShapeObjectType(int uniqueId) {
    return this.shapes.get(uniqueId);
  }

  private <T extends IObjectType> T findObjectTypeByName(Collection<? extends T> otSet, String name){
    T retVal = null;
    for(T val : otSet){
      if(val.getName().equals(name)){
        retVal = val;
        break;
      }
    }
    return retVal;
  }
  
  public ILinkObjectType findLinkObjectTypeByName(String name) {
    return findObjectTypeByName(this.links.values(), name);
  }

  public IShapeObjectType findShapeObjectTypeByName(String name) {
    return findObjectTypeByName(this.shapes.values(), name);
  }

  public int numLinkObjectTypes() {
    return this.links.size();
  }

  public int numShapeObjectTypes() {
    return this.shapes.size();
  }

  public int numObjectTypes(){
    return this.numLinkObjectTypes() + this.numShapeObjectTypes() + NUM_ROOT_OTS;
  }

		private void createRMO(){
			this.rmo= new RootObjectType(0, "Root Object", "ROOT_OBJECT", this);
		}
		private void defineParentingRMO(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.State, this.LocState, this.ExState, this.UnitOfInf, this.Entity, this.Interaction, this.Outcome, this.Perturbation, this.Observable, this.Submap, this.Interface, this.Tag, this.AndGate, this.OrGate, this.NotGate, this.DelayGate, this.Decision, this.Acceptor}));
			set.removeAll(Arrays.asList(new IShapeObjectType[]{this.LocState, this.UnitOfInf, this.ExState}));
			for (IShapeObjectType child : set) {
			  this.rmo.getParentingRules().addChild(child);
			}

		}

	private void createState(){
	this.State.setDescription("State variable");//ment to be TypeDescription rather
	//this.State.getDefaultAttributes().setName("State");
	this.State.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.State.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.State.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.State.getDefaultAttributes().setLineWidth(1);
	this.State.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.State.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.State.getDefaultAttributes().setShapeType(PrimitiveShapeType.SAUSAGE);		int[] s=new int[]{25,25};
			this.State.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.State.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.State.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.State.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.State.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.State.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.State.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.State.getDefaultAttributes().setLineColourEditable(true);
	this.State.setEditableAttributes(editableAttributes);
	this.State.getDefaultAttributes().setUrl("");
	IPropertyDefinition Label=reassignVal(getPropLabel(),"State variable",true,false);
	State.getDefaultAttributes().addPropertyDefinition(Label);
	  	
	 	this.State.getDefaultAttributes().setName(" ");
	 
	}

		private void defineParentingState(){
			this.State.getParentingRules().clear();
		}

		public ShapeObjectType getState(){
			return this.State;
		}
	private void createLocState(){
	this.LocState.setDescription("Location State variable");//ment to be TypeDescription rather
	//this.LocState.getDefaultAttributes().setName("LocationState");
	this.LocState.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.LocState.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.LocState.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.LocState.getDefaultAttributes().setLineWidth(1);
	this.LocState.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.LocState.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.LocState.getDefaultAttributes().setShapeType(PrimitiveShapeType.SAUSAGE);		int[] s=new int[]{25,25};
			this.LocState.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.LocState.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.LocState.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.LocState.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.LocState.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.LocState.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.LocState.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.LocState.getDefaultAttributes().setLineColourEditable(true);
	this.LocState.setEditableAttributes(editableAttributes);
	this.LocState.getDefaultAttributes().setUrl("");
	IPropertyDefinition Label=reassignVal(getPropLabel(),"State variable",true,false);
	LocState.getDefaultAttributes().addPropertyDefinition(Label);
	  	
	 	this.LocState.getDefaultAttributes().setName("L");
	 
	}

		private void defineParentingLocState(){
			this.LocState.getParentingRules().clear();
		}

		public ShapeObjectType getLocState(){
			return this.LocState;
		}
	private void createExState(){
	this.ExState.setDescription("Existence State variable");//ment to be TypeDescription rather
	//this.ExState.getDefaultAttributes().setName("ExistenceState");
	this.ExState.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.ExState.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.ExState.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.ExState.getDefaultAttributes().setLineWidth(1);
	this.ExState.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.ExState.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.ExState.getDefaultAttributes().setShapeType(PrimitiveShapeType.SAUSAGE);		int[] s=new int[]{25,25};
			this.ExState.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.ExState.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.ExState.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.ExState.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.ExState.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.ExState.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.ExState.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.ExState.getDefaultAttributes().setLineColourEditable(true);
	this.ExState.setEditableAttributes(editableAttributes);
	this.ExState.getDefaultAttributes().setUrl("");
	IPropertyDefinition Label=reassignVal(getPropLabel(),"Existence State variable",true,false);
	ExState.getDefaultAttributes().addPropertyDefinition(Label);
	  	
	 	this.ExState.getDefaultAttributes().setName("E");
	 
	}

		private void defineParentingExState(){
			this.ExState.getParentingRules().clear();
		}

		public ShapeObjectType getExState(){
			return this.ExState;
		}
	private void createUnitOfInf(){
	this.UnitOfInf.setDescription("Auxiliary information box");//ment to be TypeDescription rather
	//this.UnitOfInf.getDefaultAttributes().setName("UnitOfInf");
	this.UnitOfInf.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.UnitOfInf.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.UnitOfInf.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{1,1,1};
	this.UnitOfInf.getDefaultAttributes().setLineWidth(1);
	this.UnitOfInf.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.UnitOfInf.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.UnitOfInf.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);		int[] s=new int[]{65,25};
			this.UnitOfInf.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{240,247,255};
	this.UnitOfInf.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(false){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.UnitOfInf.getDefaultAttributes().setFillEditable(false);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.UnitOfInf.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.UnitOfInf.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.UnitOfInf.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.UnitOfInf.getDefaultAttributes().setLineWidthEditable(true);
	if(false){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.UnitOfInf.getDefaultAttributes().setLineColourEditable(false);
	this.UnitOfInf.setEditableAttributes(editableAttributes);
	this.UnitOfInf.getDefaultAttributes().setUrl("");
	IPropertyDefinition Label=reassignVal(getPropLabel(),"Unit of Information",true,false);
	UnitOfInf.getDefaultAttributes().addPropertyDefinition(Label);
	 
	}

		private void defineParentingUnitOfInf(){
			this.UnitOfInf.getParentingRules().clear();
		}

		public ShapeObjectType getUnitOfInf(){
			return this.UnitOfInf;
		}
	private void createEntity(){
	this.Entity.setDescription("Interacting entity");//ment to be TypeDescription rather
	//this.Entity.getDefaultAttributes().setName("Entity");
	this.Entity.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Entity.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Entity.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Entity.getDefaultAttributes().setLineWidth(1);
	this.Entity.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Entity.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Entity.getDefaultAttributes().setShapeType(PrimitiveShapeType.ROUNDED_RECTANGLE);		int[] s=new int[]{60,40};
			this.Entity.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Entity.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Entity.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Entity.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Entity.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Entity.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Entity.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Entity.getDefaultAttributes().setLineColourEditable(true);
	this.Entity.setEditableAttributes(editableAttributes);
	this.Entity.getDefaultAttributes().setUrl("");
	}

		private void defineParentingEntity(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.State, this.UnitOfInf, this.ExState, this.LocState}));
			for (IShapeObjectType child : set) {
			  this.Entity.getParentingRules().addChild(child);
			}

		}

		public ShapeObjectType getEntity(){
			return this.Entity;
		}
	private void createInteraction(){
	this.Interaction.setDescription("Interacting entity");//ment to be TypeDescription rather
	//this.Interaction.getDefaultAttributes().setName("Entity");
	this.Interaction.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Interaction.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Interaction.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Interaction.getDefaultAttributes().setLineWidth(1);
	this.Interaction.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Interaction.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Interaction.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{60,40};
			this.Interaction.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Interaction.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Interaction.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Interaction.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Interaction.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Interaction.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Interaction.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Interaction.getDefaultAttributes().setLineColourEditable(true);
	this.Interaction.setEditableAttributes(editableAttributes);
	this.Interaction.getDefaultAttributes().setUrl("");
	}

		private void defineParentingInteraction(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.Outcome}));
			for (IShapeObjectType child : set) {
			  this.Interaction.getParentingRules().addChild(child);
			}

		}

		public ShapeObjectType getInteraction(){
			return this.Interaction;
		}
	private void createOutcome(){
	this.Outcome.setDescription("Interaction outcome");//ment to be TypeDescription rather
	//this.Outcome.getDefaultAttributes().setName("Outcome");
	this.Outcome.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Outcome.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Outcome.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Outcome.getDefaultAttributes().setLineWidth(1);
	this.Outcome.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Outcome.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Outcome.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{10,10};
			this.Outcome.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{0,0,0};
	this.Outcome.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Outcome.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Outcome.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Outcome.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Outcome.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Outcome.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Outcome.getDefaultAttributes().setLineColourEditable(true);
	this.Outcome.setEditableAttributes(editableAttributes);
	this.Outcome.getDefaultAttributes().setUrl("");
	}

		private void defineParentingOutcome(){
			this.Outcome.getParentingRules().clear();
		}

		public ShapeObjectType getOutcome(){
			return this.Outcome;
		}
	private void createPerturbation(){
	this.Perturbation.setDescription("Perturbation");//ment to be TypeDescription rather
	//this.Perturbation.getDefaultAttributes().setName("Perturbation");
	this.Perturbation.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Perturbation.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Perturbation.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Perturbation.getDefaultAttributes().setLineWidth(1);
	this.Perturbation.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Perturbation.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Perturbation.getDefaultAttributes().setShapeType(PrimitiveShapeType.XSHAPE);		int[] s=new int[]{80,60};
			this.Perturbation.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Perturbation.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Perturbation.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Perturbation.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Perturbation.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Perturbation.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Perturbation.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Perturbation.getDefaultAttributes().setLineColourEditable(true);
	this.Perturbation.setEditableAttributes(editableAttributes);
	this.Perturbation.getDefaultAttributes().setUrl("");
	}

		private void defineParentingPerturbation(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.State, this.ExState}));
			for (IShapeObjectType child : set) {
			  this.Perturbation.getParentingRules().addChild(child);
			}

		}

		public ShapeObjectType getPerturbation(){
			return this.Perturbation;
		}
	private void createObservable(){
	this.Observable.setDescription("Observable");//ment to be TypeDescription rather
	//this.Observable.getDefaultAttributes().setName("Observable");
	this.Observable.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Observable.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Observable.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Observable.getDefaultAttributes().setLineWidth(1);
	this.Observable.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Observable.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Observable.getDefaultAttributes().setShapeType(PrimitiveShapeType.HEXAGON);		int[] s=new int[]{80,60};
			this.Observable.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Observable.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Observable.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Observable.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Observable.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Observable.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Observable.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Observable.getDefaultAttributes().setLineColourEditable(true);
	this.Observable.setEditableAttributes(editableAttributes);
	this.Observable.getDefaultAttributes().setUrl("");
	}

		private void defineParentingObservable(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.State, this.ExState}));
			for (IShapeObjectType child : set) {
			  this.Observable.getParentingRules().addChild(child);
			}

		}

		public ShapeObjectType getObservable(){
			return this.Observable;
		}
	private void createSubmap(){
	this.Submap.setDescription("collapsed part of diagram");//ment to be TypeDescription rather
	//this.Submap.getDefaultAttributes().setName("Submap");
	this.Submap.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Submap.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Submap.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Submap.getDefaultAttributes().setLineWidth(1);
	this.Submap.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Submap.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Submap.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);		int[] s=new int[]{120,120};
			this.Submap.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Submap.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Submap.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Submap.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Submap.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Submap.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Submap.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Submap.getDefaultAttributes().setLineColourEditable(true);
	this.Submap.setEditableAttributes(editableAttributes);
	this.Submap.getDefaultAttributes().setUrl("");
	}

		private void defineParentingSubmap(){
			HashSet<IShapeObjectType> set=new HashSet<IShapeObjectType>();
			set.addAll(Arrays.asList(new IShapeObjectType[]{this.Interface, this.UnitOfInf}));
			for (IShapeObjectType child : set) {
			  this.Submap.getParentingRules().addChild(child);
			}

		}

		public ShapeObjectType getSubmap(){
			return this.Submap;
		}
	private void createInterface(){
	this.Interface.setDescription("Interface to the submap");//ment to be TypeDescription rather
	//this.Interface.getDefaultAttributes().setName("Interface");
	this.Interface.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Interface.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Interface.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Interface.getDefaultAttributes().setLineWidth(1);
	this.Interface.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Interface.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Interface.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);		int[] s=new int[]{20,35};
			this.Interface.getDefaultAttributes().setSize(new Size(s[0],s[1]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Interface.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Interface.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Interface.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Interface.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Interface.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Interface.getDefaultAttributes().setLineColourEditable(true);
	this.Interface.setEditableAttributes(editableAttributes);
	this.Interface.getDefaultAttributes().setUrl("");
	}

		private void defineParentingInterface(){
			this.Interface.getParentingRules().clear();
		}

		public ShapeObjectType getInterface(){
			return this.Interface;
		}
	private void createTag(){
	this.Tag.setDescription("Mark node to be interface to submap");//ment to be TypeDescription rather
	//this.Tag.getDefaultAttributes().setName("Tag");
	this.Tag.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Tag.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Tag.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Tag.getDefaultAttributes().setLineWidth(1);
	this.Tag.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Tag.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Tag.getDefaultAttributes().setShapeType(PrimitiveShapeType.RH_SIGN_ARROW);		int[] s=new int[]{40,20};
			this.Tag.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{255,255,255};
	this.Tag.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Tag.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Tag.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Tag.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Tag.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Tag.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Tag.getDefaultAttributes().setLineColourEditable(true);
	this.Tag.setEditableAttributes(editableAttributes);
	this.Tag.getDefaultAttributes().setUrl("");
	}

		private void defineParentingTag(){
			this.Tag.getParentingRules().clear();
		}

		public ShapeObjectType getTag(){
			return this.Tag;
		}
	private void createAndGate(){
	this.AndGate.setDescription("AndGate");//ment to be TypeDescription rather
	//this.AndGate.getDefaultAttributes().setName("AND gate");
	this.AndGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.AndGate.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.AndGate.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.AndGate.getDefaultAttributes().setLineWidth(1);
	this.AndGate.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.AndGate.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.AndGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{40,40};
			this.AndGate.getDefaultAttributes().setSize(new Size(s[0],s[1]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.AndGate.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.AndGate.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.AndGate.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.AndGate.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.AndGate.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.AndGate.getDefaultAttributes().setLineColourEditable(true);
	this.AndGate.setEditableAttributes(editableAttributes);
	this.AndGate.getDefaultAttributes().setUrl("");
	 	
	 	this.AndGate.getDefaultAttributes().setName("AND");
	 
	}

		private void defineParentingAndGate(){
			this.AndGate.getParentingRules().clear();
		}

		public ShapeObjectType getAndGate(){
			return this.AndGate;
		}
	private void createOrGate(){
	this.OrGate.setDescription("OR logic Gate");//ment to be TypeDescription rather
	//this.OrGate.getDefaultAttributes().setName("OR gate");
	this.OrGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.OrGate.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.OrGate.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.OrGate.getDefaultAttributes().setLineWidth(1);
	this.OrGate.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.OrGate.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.OrGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{40,40};
			this.OrGate.getDefaultAttributes().setSize(new Size(s[0],s[1]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.OrGate.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.OrGate.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.OrGate.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.OrGate.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.OrGate.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.OrGate.getDefaultAttributes().setLineColourEditable(true);
	this.OrGate.setEditableAttributes(editableAttributes);
	this.OrGate.getDefaultAttributes().setUrl("");
	 	
	 	this.OrGate.getDefaultAttributes().setName("OR");
	 
	}

		private void defineParentingOrGate(){
			this.OrGate.getParentingRules().clear();
		}

		public ShapeObjectType getOrGate(){
			return this.OrGate;
		}
	private void createNotGate(){
	this.NotGate.setDescription("NOT logic Gate");//ment to be TypeDescription rather
	//this.NotGate.getDefaultAttributes().setName("NOT gate");
	this.NotGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.NotGate.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.NotGate.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.NotGate.getDefaultAttributes().setLineWidth(1);
	this.NotGate.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.NotGate.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.NotGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{40,40};
			this.NotGate.getDefaultAttributes().setSize(new Size(s[0],s[1]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.NotGate.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.NotGate.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.NotGate.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.NotGate.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.NotGate.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.NotGate.getDefaultAttributes().setLineColourEditable(true);
	this.NotGate.setEditableAttributes(editableAttributes);
	this.NotGate.getDefaultAttributes().setUrl("");
	 	
	 	this.NotGate.getDefaultAttributes().setName("NOT");
	 
	}

		private void defineParentingNotGate(){
			this.NotGate.getParentingRules().clear();
		}

		public ShapeObjectType getNotGate(){
			return this.NotGate;
		}
	private void createDelayGate(){
	this.DelayGate.setDescription("NOT logic Gate");//ment to be TypeDescription rather
	//this.DelayGate.getDefaultAttributes().setName("NOT gate");
	this.DelayGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.DelayGate.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.DelayGate.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.DelayGate.getDefaultAttributes().setLineWidth(1);
	this.DelayGate.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.DelayGate.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.DelayGate.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{40,40};
			this.DelayGate.getDefaultAttributes().setSize(new Size(s[0],s[1]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.DelayGate.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.DelayGate.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.DelayGate.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.DelayGate.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.DelayGate.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.DelayGate.getDefaultAttributes().setLineColourEditable(true);
	this.DelayGate.setEditableAttributes(editableAttributes);
	this.DelayGate.getDefaultAttributes().setUrl("");
	 	
	 	this.DelayGate.getDefaultAttributes().setName("&#964;");
	 
	}

		private void defineParentingDelayGate(){
			this.DelayGate.getParentingRules().clear();
		}

		public ShapeObjectType getDelayGate(){
			return this.DelayGate;
		}
	private void createDecision(){
	this.Decision.setDescription("agregator for several state assignments");//ment to be TypeDescription rather
	//this.Decision.getDefaultAttributes().setName("Decision");
	this.Decision.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Decision.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Decision.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Decision.getDefaultAttributes().setLineWidth(1);
	this.Decision.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Decision.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Decision.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{4,4};
			this.Decision.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{0,0,0};
	this.Decision.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Decision.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Decision.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Decision.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Decision.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Decision.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Decision.getDefaultAttributes().setLineColourEditable(true);
	this.Decision.setEditableAttributes(editableAttributes);
	this.Decision.getDefaultAttributes().setUrl("");
	}

		private void defineParentingDecision(){
			this.Decision.getParentingRules().clear();
		}

		public ShapeObjectType getDecision(){
			return this.Decision;
		}
	private void createAcceptor(){
	this.Acceptor.setDescription("acceptor of influence link");//ment to be TypeDescription rather
	//this.Acceptor.getDefaultAttributes().setName("Acceptor");
	this.Acceptor.getDefaultAttributes().setShapeType(PrimitiveShapeType.RECTANGLE);
	this.Acceptor.getDefaultAttributes().setFillColour(new RGB(255,255,255));
	this.Acceptor.getDefaultAttributes().setSize(new Size(20,20));
	int[] lc=new int[]{0,0,0};
	this.Acceptor.getDefaultAttributes().setLineWidth(1);
	this.Acceptor.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Acceptor.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Acceptor.getDefaultAttributes().setShapeType(PrimitiveShapeType.ELLIPSE);		int[] s=new int[]{4,4};
			this.Acceptor.getDefaultAttributes().setSize(new Size(s[0],s[1]));int[] c=new int[]{0,0,0};
	this.Acceptor.getDefaultAttributes().setFillColour(new RGB(c[0],c[1],c[2]));

	EnumSet<EditableShapeAttributes> editableAttributes = EnumSet.noneOf(EditableShapeAttributes.class);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.FILL_COLOUR);
	}
	//this.Acceptor.getDefaultAttributes().setFillEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_TYPE);
	}
	//this.Acceptor.setPrimitiveShapeTypeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.SHAPE_SIZE);
	}
	//this.Acceptor.setSizeEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_STYLE);
	}
	//this.Acceptor.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_WIDTH);
	}
	//this.Acceptor.getDefaultAttributes().setLineWidthEditable(true);
	if(true){
	    editableAttributes.add(EditableShapeAttributes.LINE_COLOUR);
	}
	//this.Acceptor.getDefaultAttributes().setLineColourEditable(true);
	this.Acceptor.setEditableAttributes(editableAttributes);
	this.Acceptor.getDefaultAttributes().setUrl("");
	}

		private void defineParentingAcceptor(){
			this.Acceptor.getParentingRules().clear();
		}

		public ShapeObjectType getAcceptor(){
			return this.Acceptor;
		}

	
	private void createModulation(){
	Set<IShapeObjectType> set=null;
	this.Modulation.setDescription("A modulation affects the flux of a process represented by the target transition.");
	int[] lc=new int[]{0,0,0};
	this.Modulation.getDefaultAttributes().setLineWidth(1);
	this.Modulation.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Modulation.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Modulation.getDefaultAttributes().setName("Modulation Link");
	this.Modulation.getDefaultAttributes().setDescription("");
	this.Modulation.getDefaultAttributes().setDetailedDescription("");
	this.Modulation.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.Modulation.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.Modulation.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.Modulation.getDefaultAttributes().setLineWidthEditable(true);
	this.Modulation.setEditableAttributes(editableAttributes);

	this.Modulation.getDefaultAttributes().setUrl("");
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	Modulation.getDefaultAttributes().addPropertyDefinition(Stoich);
	 IPropertyDefinition SBOTerm=reassignVal(getPropSBOTerm()," ",true,false);
	Modulation.getDefaultAttributes().addPropertyDefinition(SBOTerm);
	 
	//LinkEndDefinition sport=this.Modulation.getLinkSource();
	//LinkEndDefinition tport=this.Modulation.getLinkTarget();
	LinkTerminusDefinition sport=this.Modulation.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.Modulation.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.EMPTY_DIAMOND);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Modulation.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getModulation(){
		return this.Modulation;
	}
	private void createStimulation(){
	Set<IShapeObjectType> set=null;
	this.Stimulation.setDescription("A stimulation affects positively the flux of a process represented by the target transition.");
	int[] lc=new int[]{0,0,0};
	this.Stimulation.getDefaultAttributes().setLineWidth(1);
	this.Stimulation.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Stimulation.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Stimulation.getDefaultAttributes().setName("Stimulation Link");
	this.Stimulation.getDefaultAttributes().setDescription("");
	this.Stimulation.getDefaultAttributes().setDetailedDescription("");
	this.Stimulation.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.Stimulation.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.Stimulation.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.Stimulation.getDefaultAttributes().setLineWidthEditable(true);
	this.Stimulation.setEditableAttributes(editableAttributes);

	this.Stimulation.getDefaultAttributes().setUrl("");
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	Stimulation.getDefaultAttributes().addPropertyDefinition(Stoich);
	 IPropertyDefinition SBOTerm=reassignVal(getPropSBOTerm()," ",true,false);
	Stimulation.getDefaultAttributes().addPropertyDefinition(SBOTerm);
	 
	//LinkEndDefinition sport=this.Stimulation.getLinkSource();
	//LinkEndDefinition tport=this.Stimulation.getLinkTarget();
	LinkTerminusDefinition sport=this.Stimulation.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.Stimulation.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.EMPTY_TRIANGLE);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Stimulation.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getStimulation(){
		return this.Stimulation;
	}
	private void createCatalysis(){
	Set<IShapeObjectType> set=null;
	this.Catalysis.setDescription("A catalysis is a particular case of stimulation.");
	int[] lc=new int[]{0,0,0};
	this.Catalysis.getDefaultAttributes().setLineWidth(1);
	this.Catalysis.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Catalysis.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Catalysis.getDefaultAttributes().setName("Catalysis Link");
	this.Catalysis.getDefaultAttributes().setDescription("");
	this.Catalysis.getDefaultAttributes().setDetailedDescription("");
	this.Catalysis.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.Catalysis.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.Catalysis.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.Catalysis.getDefaultAttributes().setLineWidthEditable(true);
	this.Catalysis.setEditableAttributes(editableAttributes);

	this.Catalysis.getDefaultAttributes().setUrl("");
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	Catalysis.getDefaultAttributes().addPropertyDefinition(Stoich);
	 IPropertyDefinition SBOTerm=reassignVal(getPropSBOTerm()," ",true,false);
	Catalysis.getDefaultAttributes().addPropertyDefinition(SBOTerm);
	 
	//LinkEndDefinition sport=this.Catalysis.getLinkSource();
	//LinkEndDefinition tport=this.Catalysis.getLinkTarget();
	LinkTerminusDefinition sport=this.Catalysis.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.Catalysis.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)10);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.EMPTY_CIRCLE);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Catalysis.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getCatalysis(){
		return this.Catalysis;
	}
	private void createInhibition(){
	Set<IShapeObjectType> set=null;
	this.Inhibition.setDescription("An inhibition affects negatively the flux of a process represented by the target transition.");
	int[] lc=new int[]{0,0,0};
	this.Inhibition.getDefaultAttributes().setLineWidth(1);
	this.Inhibition.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.Inhibition.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.Inhibition.getDefaultAttributes().setName("Inhibition Link");
	this.Inhibition.getDefaultAttributes().setDescription("");
	this.Inhibition.getDefaultAttributes().setDetailedDescription("");
	this.Inhibition.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.Inhibition.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.Inhibition.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.Inhibition.getDefaultAttributes().setLineWidthEditable(true);
	this.Inhibition.setEditableAttributes(editableAttributes);

	this.Inhibition.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.Inhibition.getLinkSource();
	//LinkEndDefinition tport=this.Inhibition.getLinkTarget();
	LinkTerminusDefinition sport=this.Inhibition.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.Inhibition.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.BAR);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	tport.getDefaultAttributes().addPropertyDefinition(Stoich);
	 
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.Inhibition.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getInhibition(){
		return this.Inhibition;
	}
	private void createAbsInhibition(){
	Set<IShapeObjectType> set=null;
	this.AbsInhibition.setDescription("An absolute inhibition block the flux of a process represented by the target transition.");
	int[] lc=new int[]{0,0,0};
	this.AbsInhibition.getDefaultAttributes().setLineWidth(1);
	this.AbsInhibition.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.AbsInhibition.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.AbsInhibition.getDefaultAttributes().setName("Absolute Inhibition Link");
	this.AbsInhibition.getDefaultAttributes().setDescription("");
	this.AbsInhibition.getDefaultAttributes().setDetailedDescription("");
	this.AbsInhibition.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.AbsInhibition.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.AbsInhibition.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.AbsInhibition.getDefaultAttributes().setLineWidthEditable(true);
	this.AbsInhibition.setEditableAttributes(editableAttributes);

	this.AbsInhibition.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.AbsInhibition.getLinkSource();
	//LinkEndDefinition tport=this.AbsInhibition.getLinkTarget();
	LinkTerminusDefinition sport=this.AbsInhibition.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.AbsInhibition.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.DOUBLE_BAR);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	tport.getDefaultAttributes().addPropertyDefinition(Stoich);
	 
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.AbsInhibition.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getAbsInhibition(){
		return this.AbsInhibition;
	}
	private void createNecessaryStimulation(){
	Set<IShapeObjectType> set=null;
	this.NecessaryStimulation.setDescription("A Necessary Stimulation effect, or absolute stimulation, is a stimulation that is necessary for a process to take place.");
	int[] lc=new int[]{0,0,0};
	this.NecessaryStimulation.getDefaultAttributes().setLineWidth(1);
	this.NecessaryStimulation.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.NecessaryStimulation.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.NecessaryStimulation.getDefaultAttributes().setName("Necessary Stimulation Link");
	this.NecessaryStimulation.getDefaultAttributes().setDescription("");
	this.NecessaryStimulation.getDefaultAttributes().setDetailedDescription("");
	this.NecessaryStimulation.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.NecessaryStimulation.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.NecessaryStimulation.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.NecessaryStimulation.getDefaultAttributes().setLineWidthEditable(true);
	this.NecessaryStimulation.setEditableAttributes(editableAttributes);

	this.NecessaryStimulation.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.NecessaryStimulation.getLinkSource();
	//LinkEndDefinition tport=this.NecessaryStimulation.getLinkTarget();
	LinkTerminusDefinition sport=this.NecessaryStimulation.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.NecessaryStimulation.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.TRIANGLE_BAR);//, 5,5);
	tport.getDefaultAttributes().setEndSize(new Size(5,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);
	IPropertyDefinition Stoich=reassignVal(getPropStoich(),"1",true,false);
	tport.getDefaultAttributes().addPropertyDefinition(Stoich);
	 
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Acceptor, this.Interaction, this.Observable}));
	for (IShapeObjectType tgt : set) {
	  this.NecessaryStimulation.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getNecessaryStimulation(){
		return this.NecessaryStimulation;
	}
	private void createLogicArc(){
	Set<IShapeObjectType> set=null;
	this.LogicArc.setDescription("Logic arc is the arc used to represent the fact that an entity influences outcome of logic operator.");
	int[] lc=new int[]{0,0,0};
	this.LogicArc.getDefaultAttributes().setLineWidth(1);
	this.LogicArc.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.LogicArc.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.LogicArc.getDefaultAttributes().setName("Logic arc");
	this.LogicArc.getDefaultAttributes().setDescription("");
	this.LogicArc.getDefaultAttributes().setDetailedDescription("");
	this.LogicArc.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.LogicArc.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.LogicArc.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.LogicArc.getDefaultAttributes().setLineWidthEditable(true);
	this.LogicArc.setEditableAttributes(editableAttributes);

	this.LogicArc.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.LogicArc.getLinkSource();
	//LinkEndDefinition tport=this.LogicArc.getLinkTarget();
	LinkTerminusDefinition sport=this.LogicArc.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.LogicArc.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)0);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.TRIANGLE);//, 8,8);
	tport.getDefaultAttributes().setEndSize(new Size(8,8));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.Perturbation, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.AndGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.OrGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.NotGate, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.AndGate, this.OrGate, this.NotGate, this.DelayGate}));
	for (IShapeObjectType tgt : set) {
	  this.LogicArc.getLinkConnectionRules().addConnection(this.DelayGate, tgt);
	}

	}

	public LinkObjectType getLogicArc(){
		return this.LogicArc;
	}
	private void createInteractionArc(){
	Set<IShapeObjectType> set=null;
	this.InteractionArc.setDescription("Interaction arc is the arc.");
	int[] lc=new int[]{0,0,0};
	this.InteractionArc.getDefaultAttributes().setLineWidth(1);
	this.InteractionArc.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.InteractionArc.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.InteractionArc.getDefaultAttributes().setName("Interaction arc");
	this.InteractionArc.getDefaultAttributes().setDescription("");
	this.InteractionArc.getDefaultAttributes().setDetailedDescription("");
	this.InteractionArc.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.InteractionArc.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.InteractionArc.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.InteractionArc.getDefaultAttributes().setLineWidthEditable(true);
	this.InteractionArc.setEditableAttributes(editableAttributes);

	this.InteractionArc.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.InteractionArc.getLinkSource();
	//LinkEndDefinition tport=this.InteractionArc.getLinkTarget();
	LinkTerminusDefinition sport=this.InteractionArc.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.InteractionArc.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)0);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)0);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.ARROW);//, 7,5);
	tport.getDefaultAttributes().setEndSize(new Size(7,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Entity, this.Outcome}));
	for (IShapeObjectType tgt : set) {
	  this.InteractionArc.getLinkConnectionRules().addConnection(this.Interaction, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Entity, this.Outcome}));
	for (IShapeObjectType tgt : set) {
	  this.InteractionArc.getLinkConnectionRules().addConnection(this.Acceptor, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Entity, this.Outcome}));
	for (IShapeObjectType tgt : set) {
	  this.InteractionArc.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}

	}

	public LinkObjectType getInteractionArc(){
		return this.InteractionArc;
	}
	private void createAssignmenArc(){
	Set<IShapeObjectType> set=null;
	this.AssignmenArc.setDescription("Logic arc is the arc.");
	int[] lc=new int[]{0,0,0};
	this.AssignmenArc.getDefaultAttributes().setLineWidth(1);
	this.AssignmenArc.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.AssignmenArc.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.AssignmenArc.getDefaultAttributes().setName("Assignment arc");
	this.AssignmenArc.getDefaultAttributes().setDescription("");
	this.AssignmenArc.getDefaultAttributes().setDetailedDescription("");
	this.AssignmenArc.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.AssignmenArc.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.AssignmenArc.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.AssignmenArc.getDefaultAttributes().setLineWidthEditable(true);
	this.AssignmenArc.setEditableAttributes(editableAttributes);

	this.AssignmenArc.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.AssignmenArc.getLinkSource();
	//LinkEndDefinition tport=this.AssignmenArc.getLinkTarget();
	LinkTerminusDefinition sport=this.AssignmenArc.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.AssignmenArc.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)0);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.ARROW);//, 7,5);
	tport.getDefaultAttributes().setEndSize(new Size(7,5));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.State}));
	for (IShapeObjectType tgt : set) {
	  this.AssignmenArc.getLinkConnectionRules().addConnection(this.State, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.State}));
	for (IShapeObjectType tgt : set) {
	  this.AssignmenArc.getLinkConnectionRules().addConnection(this.Decision, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.State}));
	for (IShapeObjectType tgt : set) {
	  this.AssignmenArc.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.State}));
	for (IShapeObjectType tgt : set) {
	  this.AssignmenArc.getLinkConnectionRules().addConnection(this.Acceptor, tgt);
	}

	}

	public LinkObjectType getAssignmenArc(){
		return this.AssignmenArc;
	}
	private void createVarArc(){
	Set<IShapeObjectType> set=null;
	this.VarArc.setDescription("Arc is the arc connecting state value to decision point or state variable to outcome or two outcomes or outcome to decision point.");
	int[] lc=new int[]{0,0,0};
	this.VarArc.getDefaultAttributes().setLineWidth(1);
	this.VarArc.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.VarArc.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.VarArc.getDefaultAttributes().setName("Variable arc");
	this.VarArc.getDefaultAttributes().setDescription("");
	this.VarArc.getDefaultAttributes().setDetailedDescription("");
	this.VarArc.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.VarArc.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.VarArc.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.VarArc.getDefaultAttributes().setLineWidthEditable(true);
	this.VarArc.setEditableAttributes(editableAttributes);

	this.VarArc.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.VarArc.getLinkSource();
	//LinkEndDefinition tport=this.VarArc.getLinkTarget();
	LinkTerminusDefinition sport=this.VarArc.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.VarArc.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)0);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)0);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	tport.getDefaultAttributes().setEndSize(new Size(8,8));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Decision, this.Outcome, this.Acceptor}));
	for (IShapeObjectType tgt : set) {
	  this.VarArc.getLinkConnectionRules().addConnection(this.State, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Decision, this.Outcome, this.Acceptor}));
	for (IShapeObjectType tgt : set) {
	  this.VarArc.getLinkConnectionRules().addConnection(this.Outcome, tgt);
	}
	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Decision, this.Outcome, this.Acceptor}));
	for (IShapeObjectType tgt : set) {
	  this.VarArc.getLinkConnectionRules().addConnection(this.Acceptor, tgt);
	}

	}

	public LinkObjectType getVarArc(){
		return this.VarArc;
	}
	private void createEquivalenceArc(){
	Set<IShapeObjectType> set=null;
	this.EquivalenceArc.setDescription("Equivalence Arc is the arc used to represent the fact that all entities marked by the tag are equivalent.");
	int[] lc=new int[]{0,0,0};
	this.EquivalenceArc.getDefaultAttributes().setLineWidth(1);
	this.EquivalenceArc.getDefaultAttributes().setLineStyle(LineStyle.SOLID);
	this.EquivalenceArc.getDefaultAttributes().setLineColour(new RGB(lc[0],lc[1],lc[2]));
	this.EquivalenceArc.getDefaultAttributes().setName("Equivalence Arc");
	this.EquivalenceArc.getDefaultAttributes().setDescription("");
	this.EquivalenceArc.getDefaultAttributes().setDetailedDescription("");
	this.EquivalenceArc.getDefaultAttributes().setRouter(ConnectionRouter.SHORTEST_PATH);
	EnumSet<LinkEditableAttributes> editableAttributes = EnumSet.noneOf(LinkEditableAttributes.class);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.COLOUR);
	}
	//this.EquivalenceArc.getDefaultAttributes().setLineColourEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_STYLE);
	}
	//this.EquivalenceArc.getDefaultAttributes().setLineStyleEditable(true);
	if(true){
	  editableAttributes.add(LinkEditableAttributes.LINE_WIDTH);
	}
	//this.EquivalenceArc.getDefaultAttributes().setLineWidthEditable(true);
	this.EquivalenceArc.setEditableAttributes(editableAttributes);

	this.EquivalenceArc.getDefaultAttributes().setUrl("");
	//LinkEndDefinition sport=this.EquivalenceArc.getLinkSource();
	//LinkEndDefinition tport=this.EquivalenceArc.getLinkTarget();
	LinkTerminusDefinition sport=this.EquivalenceArc.getSourceTerminusDefinition();
	LinkTerminusDefinition tport=this.EquivalenceArc.getTargetTerminusDefinition();
	sport.getDefaultAttributes().setGap((short)5);
	sport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	sport.getDefaultAttributes().setEndSize(new Size(8,8));
	sport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	sport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] csport=new int[]{255,255,255};
	sport.getDefaultAttributes().setTermColour(new RGB(csport[0],csport[1],csport[2]));
	//sport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editablesportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//sport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editablesportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//sport.getDefaultAttributes().setColourEditable(true);
	sport.setEditableAttributes(editablesportAttributes);
	tport.getDefaultAttributes().setGap((short)5);
	tport.getDefaultAttributes().setEndDecoratorType(LinkEndDecoratorShape.NONE);//, 8,8);
	tport.getDefaultAttributes().setEndSize(new Size(8,8));
	tport.getDefaultAttributes().setTermDecoratorType(PrimitiveShapeType.RECTANGLE);
	tport.getDefaultAttributes().setTermSize(new Size(0,0));
	int[] ctport=new int[]{255,255,255};
	tport.getDefaultAttributes().setTermColour(new RGB(ctport[0],ctport[1],ctport[2]));
	//tport.getDefaultAttributes().setLineProperties(0, LineStyle.SOLID);
	EnumSet<LinkTermEditableAttributes> editabletportAttributes = EnumSet.of(LinkTermEditableAttributes.END_SIZE, LinkTermEditableAttributes.OFFSET,
	                  LinkTermEditableAttributes.TERM_DECORATOR_TYPE, LinkTermEditableAttributes.TERM_SIZE);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.END_DECORATOR_TYPE);
	}
	//tport.getDefaultAttributes().setShapeTypeEditable(true);
	if(true){
	  editabletportAttributes.add(LinkTermEditableAttributes.TERM_COLOUR);
	}
	//tport.getDefaultAttributes().setColourEditable(true);
	tport.setEditableAttributes(editabletportAttributes);

	set=new HashSet<IShapeObjectType>();
	set.addAll(Arrays.asList(new IShapeObjectType[]{this.Tag, this.Interface}));
	for (IShapeObjectType tgt : set) {
	  this.EquivalenceArc.getLinkConnectionRules().addConnection(this.Entity, tgt);
	}

	}

	public LinkObjectType getEquivalenceArc(){
		return this.EquivalenceArc;
	}
	

	private IPropertyDefinition getPropGOTerm(){
		IPropertyDefinition GOTerm=new PlainTextPropertyDefinition("GO term"," ",false,true);
		return GOTerm;
	}
	private IPropertyDefinition getPropSBOTerm(){
		IPropertyDefinition SBOTerm=new PlainTextPropertyDefinition("SBO term"," ",false,true);
		return SBOTerm;
	}
	private IPropertyDefinition getPropLabel(){
		IPropertyDefinition Label=new FormattedTextPropertyDefinition("Label"," ",true,true);
		return Label;
	}
	private IPropertyDefinition getPropStoich(){
		IPropertyDefinition Stoich=new PlainTextPropertyDefinition("STOICH"," ",true,true);
		return Stoich;
	}


}
