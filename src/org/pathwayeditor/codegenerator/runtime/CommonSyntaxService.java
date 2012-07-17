package org.pathwayeditor.codegenerator.runtime;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.pathwayeditor.businessobjects.drawingprimitives.attributes.Colour;
import org.pathwayeditor.businessobjects.drawingprimitives.properties.IPropertyDefinition;
import org.pathwayeditor.businessobjects.notationsubsystem.INotation;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSubsystem;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSyntaxService;
import org.pathwayeditor.businessobjects.typedefn.IAnchorNodeObjectType;
import org.pathwayeditor.businessobjects.typedefn.ILabelObjectType;
import org.pathwayeditor.businessobjects.typedefn.ILinkObjectType;
import org.pathwayeditor.businessobjects.typedefn.IObjectType;
import org.pathwayeditor.businessobjects.typedefn.IRootObjectType;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.AnchorNodeObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.LabelObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.LinkObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.RootObjectType;
import org.pathwayeditor.notationsubsystem.toolkit.definition.ShapeObjectType;


public abstract class CommonSyntaxService implements INotationSyntaxService {	
	private final INotationSubsystem subsystem;
	private final SortedMap<Integer, IShapeObjectType> shapeOts;
	private final SortedMap<Integer, ILinkObjectType> linkOts;
	private final SortedMap<Integer, IAnchorNodeObjectType> anchorNodeOts;
	private final SortedMap<Integer, ILabelObjectType> labelOts;
	private final SortedMap<IPropertyDefinition, ILabelObjectType> visualiableProperties;
	private RootObjectType rootObjectType;

	protected CommonSyntaxService(INotationSubsystem subsystem){
		this.subsystem = subsystem;
		this.shapeOts = new TreeMap<Integer, IShapeObjectType>();
		this.linkOts = new TreeMap<Integer, ILinkObjectType>();
		this.labelOts = new TreeMap<Integer, ILabelObjectType>();
		this.anchorNodeOts = new TreeMap<Integer, IAnchorNodeObjectType>();
		this.visualiableProperties = new TreeMap<IPropertyDefinition, ILabelObjectType>(); 
	}
	
	protected static Colour getColour(String hex) {
		hex = hex.replace("#", "");
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4, 6), 16);
		int a = Integer.parseInt(hex.substring(6), 16);
		return new Colour(r, g, b, a);
	}

	protected abstract void defineParenting();
	
	protected final void initialise(IObjectTypeConstructor<RootObjectType> rootConstructor, List<IObjectTypeConstructor<ShapeObjectType>> shapeConstructors,
			List<IObjectTypeConstructor<AnchorNodeObjectType>> anchorNodeConstructors, List<IObjectTypeConstructor<LabelObjectType>> labelConstructors,
			List<IObjectTypeConstructor<LinkObjectType>> linkConstructors, List<IPropertyDefinitionConstructor> propConstructors){
		this.rootObjectType = rootConstructor.create();
		// labels must got first because properties created later use them is visualisable.
		for(IObjectTypeConstructor<LabelObjectType> labelConstr : labelConstructors){
			assignObjectType(this.labelOts, labelConstr.create());
		}
		for(IPropertyDefinitionConstructor propConst : propConstructors){
			propConst.create();
		}
		for(IObjectTypeConstructor<ShapeObjectType> shapeConstr : shapeConstructors){
			assignObjectType(this.shapeOts, shapeConstr.create());
		}
		for(IObjectTypeConstructor<AnchorNodeObjectType> shapeConstr : anchorNodeConstructors){
			assignObjectType(this.anchorNodeOts, shapeConstr.create());
		}
		for(IObjectTypeConstructor<LinkObjectType> linkConstr : linkConstructors){
			assignObjectType(this.linkOts, linkConstr.create());
		}
		defineParenting();
	}

	protected final IPropertyDefinition  defineVisProp(IPropertyDefinition propDefn, int labelObjectTypeId){
		ILabelObjectType labelOt =  getLabelObjectType(labelObjectTypeId);
		this.visualiableProperties.put(propDefn, labelOt);
		return propDefn;
	}
	
	private static <T extends IObjectType>  void assignObjectType(SortedMap<Integer, T> otMap, T objectType){
		otMap.put(objectType.getUniqueId(), objectType);
	}
	
	@Override
	public INotation getNotation() {
		return this.subsystem.getNotation();
	}

	@Override
	public INotationSubsystem getNotationSubsystem() {
		return this.subsystem;
	}

	@Override
	public Iterator<IShapeObjectType> shapeTypeIterator() {
		return this.shapeOts.values().iterator();
	}

	@Override
	public Iterator<ILinkObjectType> linkTypeIterator() {
		return this.linkOts.values().iterator();
	}

	@Override
	public Iterator<IObjectType> objectTypeIterator() {
		List<IObjectType> retVal = new LinkedList<IObjectType>();
		retVal.add(rootObjectType);
		retVal.addAll(this.shapeOts.values());
		retVal.addAll(this.linkOts.values());
		retVal.addAll(this.anchorNodeOts.values());
		retVal.addAll(this.labelOts.values());
		return retVal.iterator();
	}

	@Override
	public IRootObjectType getRootObjectType() {
		return this.rootObjectType;
	}

	@Override
	public boolean containsShapeObjectType(int uniqueId) {
		return this.shapeOts.containsKey(uniqueId);
	}

	@Override
	public IShapeObjectType getShapeObjectType(int uniqueId) {
		return this.shapeOts.get(uniqueId);
	}

	@Override
	public boolean containsLinkObjectType(int uniqueId) {
		return this.linkOts.containsKey(uniqueId);
	}

	@Override
	public ILinkObjectType getLinkObjectType(int uniqueId) {
		return this.linkOts.get(uniqueId);
	}

	@Override
	public boolean containsObjectType(int uniqueId) {
		boolean retVal = uniqueId == this.rootObjectType.getUniqueId() || this.shapeOts.containsKey(uniqueId)
				|| this.anchorNodeOts.containsKey(uniqueId) || this.linkOts.containsKey(uniqueId)
				|| this.labelOts.containsKey(uniqueId);
		return retVal;
	}

	@Override
	public IObjectType getObjectType(int uniqueId) {
		IObjectType retVal = null;
		if(uniqueId == this.rootObjectType.getUniqueId()){
			retVal = this.rootObjectType;
		}
		else if(this.shapeOts.containsKey(uniqueId)){
			retVal = this.shapeOts.get(uniqueId);
		}
		else if(this.labelOts.containsKey(uniqueId)){
			retVal = this.labelOts.get(uniqueId);
		}
		else if(this.anchorNodeOts.containsKey(uniqueId)){
			retVal = this.anchorNodeOts.get(uniqueId);
		}
		else{
			retVal = this.linkOts.get(uniqueId);
		}
		return retVal;
	}

	@Override
	public ILabelObjectType getLabelObjectType(int uniqueId) {
		return this.labelOts.get(uniqueId);
	}

	@Override
	public ILabelObjectType getLabelObjectTypeByProperty(IPropertyDefinition propDefn) {
		return this.visualiableProperties.get(propDefn);
	}

	@Override
	public boolean isVisualisableProperty(IPropertyDefinition propDefn) {
		boolean retVal = false;
		if(propDefn != null){
			retVal = this.visualiableProperties.containsKey(propDefn); 
		}
		return retVal;
	}

	@Override
	public int numShapeObjectTypes() {
		return this.shapeOts.size();
	}
	
	@Override
	public int numLabelObjectTypes(){
		return this.labelOts.size();
	}

	@Override
	public int numLinkObjectTypes() {
		return this.linkOts.size();
	}

	@Override
	public int numObjectTypes() {
		return this.shapeOts.size() + this.labelOts.size() + this.anchorNodeOts.size() + this.linkOts.size() + 1;
	}

//	private <T extends IObjectType> T findObjectTypeByName(
//			Collection<? extends T> otSet, String name) {
//		T retVal = null;
//		for (T val : otSet) {
//			if (val.getName().equals(name)) {
//				retVal = val;
//				break;
//			}
//		}
//		return retVal;
//	}

//	@Override
//	public IShapeObjectType findShapeObjectTypeByName(String name) {
//		return findObjectTypeByName(this.shapeOts.values(), name);
//	}
//
//	@Override
//	public ILinkObjectType findLinkObjectTypeByName(String name) {
//		return findObjectTypeByName(this.linkOts.values(), name);
//	}

	@Override
	public Iterator<IAnchorNodeObjectType> anchorNodeTypeIterator() {
		return this.anchorNodeOts.values().iterator();
	}

	@Override
	public int numAnchorNodeTypes() {
		return this.anchorNodeOts.size();
	}

	@Override
	public IAnchorNodeObjectType getAnchorNodeObjectType(int uniqueId) {
		return this.anchorNodeOts.get(uniqueId);
	}

	@Override
	public boolean containsAnchorNodeObjectType(int uniqueId) {
		return this.anchorNodeOts.containsKey(uniqueId);
	}
	
}
