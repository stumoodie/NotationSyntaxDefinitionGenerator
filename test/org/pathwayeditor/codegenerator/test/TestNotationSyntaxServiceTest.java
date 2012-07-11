package org.pathwayeditor.codegenerator.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.LineStyle;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.LinkEndDecoratorShape;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.RGB;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.Version;
import org.pathwayeditor.businessobjects.drawingprimitives.properties.IPropertyDefinition;
import org.pathwayeditor.businessobjects.notationsubsystem.INotation;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSubsystem;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSyntaxService;
import org.pathwayeditor.businessobjects.typedefn.ILabelAttributeDefaults;
import org.pathwayeditor.businessobjects.typedefn.ILinkAttributeDefaults;
import org.pathwayeditor.businessobjects.typedefn.ILinkConnectionRules;
import org.pathwayeditor.businessobjects.typedefn.ILinkObjectType;
import org.pathwayeditor.businessobjects.typedefn.ILinkObjectType.LinkEditableAttributes;
import org.pathwayeditor.businessobjects.typedefn.ILinkTerminusDefaults;
import org.pathwayeditor.businessobjects.typedefn.ILinkTerminusDefinition;
import org.pathwayeditor.businessobjects.typedefn.ILinkTerminusDefinition.LinkTermEditableAttributes;
import org.pathwayeditor.businessobjects.typedefn.IObjectType;
import org.pathwayeditor.businessobjects.typedefn.IObjectTypeParentingRules;
import org.pathwayeditor.businessobjects.typedefn.IPropertyDefinitionContainer;
import org.pathwayeditor.businessobjects.typedefn.IRootObjectType;
import org.pathwayeditor.businessobjects.typedefn.IShapeAttributeDefaults;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType.EditableShapeAttributes;
import org.pathwayeditor.codegenerator.SimpleNotationSyntaxService;
import org.pathwayeditor.figure.geometry.Dimension;

public class TestNotationSyntaxServiceTest {
	private enum ExpectedTypes { TYPE_NAME, TYPE_DESCRIPTION, TYPE_EDITABLE, DEFAULT_SHAPE_TYPE, DEFAULT_NAME, DEFAULT_DESCN, DEFAULT_DETAILED_DESCN, DEFAULT_FILL_COLOUR,
		DEFAULT_LINE_COLOUR, DEFAULT_LINE_STYLE, DEFAULT_LINE_WIDTH, DEFAULT_SIZE, DEFAULT_URL, DEFAULT_ROUTER_TYPE, PROP_DEFNS, PROP_VALUE, PROP_EDITABLE, PROP_VISUALISABLE,
		LABEL_FILL_COLOUR, LABEL_LINE_COLOUR, LABEL_LINE_STYLE, LABEL_LINE_WIDTH, LABEL_SIZE, PARENTING_TABLE, CONNECTION_TABLE, SOURCE_TERM, TARGET_TERM, DEFAULT_TERM_TYPE,
		DEFAULT_GAP, DEFAULT_TERM_SIZE, DEFAULT_END_TYPE, DEFAULT_TERM_COLOUR, DEFAULT_END_SIZE };

	private static final String EXPECTED_NOTATION_NAME = "Test";
	private static final String EXPECTED_NOTATION_DESCN = "Test Notation";
	private static final String EXPECTED_NOTATION_QUALIFIED_NAME = "org.pathwayeditor.codegenerator.Test";
	private static final Version EXPECTED_NOTATION_VERSION = new Version(1, 2, 0);
	private static final int EXPECTED_NUM_SHAPE_TYPES = 2;
	private static final int EXPECTED_NUM_LINK_TYPES = 1;
	private INotationSyntaxService testInstance;
	private INotationSubsystem mockSubsystem;
	private INotation mockNotation;
	
	@Before
	public void setUp() throws Exception {
		this.mockSubsystem = EasyMock.createMock("mockSubsystem", INotationSubsystem.class);
		this.mockNotation = EasyMock.createMock("mockNotation", INotation.class);
		EasyMock.expect(this.mockNotation.getDescription()).andReturn(EXPECTED_NOTATION_DESCN);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(this.mockNotation.getDisplayName()).andReturn(EXPECTED_NOTATION_NAME);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(this.mockNotation.getQualifiedName()).andReturn(EXPECTED_NOTATION_QUALIFIED_NAME);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(this.mockNotation.getVersion()).andReturn(EXPECTED_NOTATION_VERSION);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.expect(this.mockSubsystem.getNotation()).andReturn(this.mockNotation);
		EasyMock.expectLastCall().anyTimes();
		EasyMock.replay(this.mockNotation);
		EasyMock.replay(this.mockSubsystem);
		this.testInstance = new SimpleNotationSyntaxService(this.mockSubsystem);
	}

	@Test
	public void testCorrectStatistics(){
		assertEquals("Num shape types", EXPECTED_NUM_SHAPE_TYPES, this.testInstance.numShapeObjectTypes());
		assertEquals("Num link types", EXPECTED_NUM_LINK_TYPES, this.testInstance.numLinkObjectTypes());
	}
	
	@Test
	public void testRootMapObject(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "rootObjectType" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "No descn" },
				{
					ExpectedTypes.PARENTING_TABLE, new Object[][] {
							{"Compartment", true }, {"ROOT_OBJECT", false}, {"Compound", true}, {"Macromolecule", false}, {"Process", true}							
					}
				},
		};
		Map<ExpectedTypes, Object> expectations = new HashMap<ExpectedTypes, Object>();
		for(Object[] row : rules){
			ExpectedTypes type = (ExpectedTypes)row[0];
			expectations.put(type, row[1]);
		}
		IRootObjectType actualShapeObjectType = this.testInstance.getRootObjectType();
		validateRootObjectType(actualShapeObjectType, expectations);
		IObjectTypeParentingRules parentingRules = actualShapeObjectType.getParentingRules();
		validateParenting(parentingRules, (Object[][])expectations.get(ExpectedTypes.PARENTING_TABLE));
	}
	
	private void validateRootObjectType(IRootObjectType actualShapeObjectType, Map<ExpectedTypes, Object> expectations){
		assertEquals("expected syntax service", this.testInstance, actualShapeObjectType.getSyntaxService());
		assertEquals("expected type name", expectations.get(ExpectedTypes.TYPE_NAME), actualShapeObjectType.getName());
		assertEquals("expected type descn", expectations.get(ExpectedTypes.TYPE_DESCRIPTION), actualShapeObjectType.getDescription());
	}
	

	
	@Test
	public void testCompartmentType(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "Compartment" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "Functional compartment" },
				{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(EditableShapeAttributes.FILL_COLOUR, EditableShapeAttributes.LINE_COLOUR,
						EditableShapeAttributes.LINE_STYLE, EditableShapeAttributes.LINE_WIDTH, EditableShapeAttributes.SHAPE_SIZE,
						EditableShapeAttributes.SHAPE_TYPE) },
				{ ExpectedTypes.DEFAULT_SHAPE_TYPE, "rect" },
				{ ExpectedTypes.DEFAULT_NAME, "Compartment" },
				{ ExpectedTypes.DEFAULT_DESCN, "" },
				{ ExpectedTypes.DEFAULT_DETAILED_DESCN, "" },
				{ ExpectedTypes.DEFAULT_FILL_COLOUR, RGB.WHITE },
				{ ExpectedTypes.DEFAULT_LINE_COLOUR, RGB.BLACK },
				{ ExpectedTypes.DEFAULT_LINE_STYLE, LineStyle.SOLID },
				{ ExpectedTypes.DEFAULT_LINE_WIDTH, 1 },
				{ ExpectedTypes.DEFAULT_SIZE, new Dimension(200, 200) },
				{ ExpectedTypes.DEFAULT_URL, "http://www.proteinatlas.org" },
				{
					ExpectedTypes.PROP_DEFNS, new Object[][]{
							{ "GO term", new Object[][]{
									{ ExpectedTypes.PROP_VALUE, " "},
									{ ExpectedTypes.PROP_EDITABLE, true },
									{ ExpectedTypes.PROP_VISUALISABLE, false },
									{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
									{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
									{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
									{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
									{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
									},
							},
					}
				},
				{
					ExpectedTypes.PARENTING_TABLE, new Object[][] {
							{"Compartment", true }, {"ROOT_OBJECT", false}, {"Compound", true}, {"Macromolecule", true}, {"Process", true}							
					}
				},
		};
		validateShapeObjectType(rules);
	}
	
	@Test
	public void testProcessType(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "Process" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "chemical conversion of compounds" },
				{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(EditableShapeAttributes.FILL_COLOUR, EditableShapeAttributes.LINE_COLOUR,
						EditableShapeAttributes.LINE_STYLE, EditableShapeAttributes.LINE_WIDTH,	EditableShapeAttributes.SHAPE_TYPE) },
				{ ExpectedTypes.DEFAULT_SHAPE_TYPE, "rect" },
				{ ExpectedTypes.DEFAULT_NAME, "Reaction" },
				{ ExpectedTypes.DEFAULT_DESCN, "" },
				{ ExpectedTypes.DEFAULT_DETAILED_DESCN, "" },
				{ ExpectedTypes.DEFAULT_FILL_COLOUR, RGB.WHITE },
				{ ExpectedTypes.DEFAULT_LINE_COLOUR, RGB.BLACK },
				{ ExpectedTypes.DEFAULT_LINE_STYLE, LineStyle.SOLID },
				{ ExpectedTypes.DEFAULT_LINE_WIDTH, 1 },
				{ ExpectedTypes.DEFAULT_SIZE, new Dimension(10, 10) },
				{ ExpectedTypes.DEFAULT_URL, "" },
				{
					ExpectedTypes.PROP_DEFNS, new Object[][]{
							{ "EC", new Object[][]{
									{ ExpectedTypes.PROP_VALUE, "-.-.-.-"},
									{ ExpectedTypes.PROP_EDITABLE, true },
									{ ExpectedTypes.PROP_VISUALISABLE, true },
									{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
									{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
									{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
									{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
									{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
									},
							},
							{ "KineticLaw", new Object[][]{
									{ ExpectedTypes.PROP_VALUE, " "},
									{ ExpectedTypes.PROP_EDITABLE, true },
									{ ExpectedTypes.PROP_VISUALISABLE, false },
									{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
									{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
									{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
									{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
									{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
									},
							},
					}
				},
				{
					ExpectedTypes.PARENTING_TABLE, new Object[][] {
							{"Compartment", true }, {"ROOT_OBJECT", false}, {"Compound", true}, {"Macromolecule", true}, {"Process", true}							
					}
				},
		};
		validateShapeObjectType(rules);
	}
	
	@Test
	public void testMacromoleculeType(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "Macromolecule" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "polymer" },
				{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(EditableShapeAttributes.FILL_COLOUR, EditableShapeAttributes.LINE_COLOUR,
						EditableShapeAttributes.LINE_STYLE, EditableShapeAttributes.LINE_WIDTH,	EditableShapeAttributes.SHAPE_SIZE,
						EditableShapeAttributes.SHAPE_TYPE) },
				{ ExpectedTypes.DEFAULT_SHAPE_TYPE, "rect" },
				{ ExpectedTypes.DEFAULT_NAME, "Macromolecule" },
				{ ExpectedTypes.DEFAULT_DESCN, "" },
				{ ExpectedTypes.DEFAULT_DETAILED_DESCN, "" },
				{ ExpectedTypes.DEFAULT_FILL_COLOUR, RGB.WHITE },
				{ ExpectedTypes.DEFAULT_LINE_COLOUR, RGB.BLACK },
				{ ExpectedTypes.DEFAULT_LINE_STYLE, LineStyle.SOLID },
				{ ExpectedTypes.DEFAULT_LINE_WIDTH, 1 },
				{ ExpectedTypes.DEFAULT_SIZE, new Dimension(20, 20) },
				{ ExpectedTypes.DEFAULT_URL, "" },
				{
					ExpectedTypes.PROP_DEFNS, new Object[][]{
							{ "GO term", new Object[][]{
									{ ExpectedTypes.PROP_VALUE, " "},
									{ ExpectedTypes.PROP_EDITABLE, true },
									{ ExpectedTypes.PROP_VISUALISABLE, false },
									{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
									{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
									{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
									{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
									{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
									},
							},
					}
				},
				{
					ExpectedTypes.PARENTING_TABLE, new Object[][] {
							{"Compartment", false }, {"ROOT_OBJECT", false}, {"Compound", true}, {"Macromolecule", true}, {"Process", false}							
					}
				},
		};
		validateShapeObjectType(rules);
	}
	
	@Test
	public void testConsumeType(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "Consume" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "No descn" },
				{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(LinkEditableAttributes.COLOUR,
						LinkEditableAttributes.LINE_STYLE, LinkEditableAttributes.LINE_WIDTH) },
				{ ExpectedTypes.DEFAULT_NAME, "Consumption Link" },
				{ ExpectedTypes.DEFAULT_DESCN, "" },
				{ ExpectedTypes.DEFAULT_DETAILED_DESCN, "" },
				{ ExpectedTypes.DEFAULT_LINE_COLOUR, RGB.BLACK },
				{ ExpectedTypes.DEFAULT_LINE_STYLE, LineStyle.SOLID },
				{ ExpectedTypes.DEFAULT_LINE_WIDTH, 1 },
				{ ExpectedTypes.DEFAULT_URL, "" },
				{
					ExpectedTypes.PROP_DEFNS, new Object[][]{}
				},
				{
					ExpectedTypes.CONNECTION_TABLE, new String[][] {
							{"Compound", "Process" }, {"Macromolecule", "Process"}							
					}
				},
				{
					ExpectedTypes.SOURCE_TERM, new Object[][]{
							{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(
									LinkTermEditableAttributes.END_DECORATOR_TYPE, LinkTermEditableAttributes.END_SIZE,
									LinkTermEditableAttributes.OFFSET) },
							{ ExpectedTypes.DEFAULT_TERM_COLOUR, RGB.WHITE },
							{ ExpectedTypes.DEFAULT_TERM_SIZE, new Dimension(0, 0) },
							{ ExpectedTypes.DEFAULT_END_SIZE, new Dimension(8, 8) },
							{ ExpectedTypes.DEFAULT_GAP, Short.valueOf((short)0) },
							{ ExpectedTypes.DEFAULT_END_TYPE, LinkEndDecoratorShape.NONE },
							{ ExpectedTypes.DEFAULT_SIZE, new Dimension(20, 20) },
							{
								ExpectedTypes.PROP_DEFNS, new Object[][]{
										{ "ROLE", new Object[][]{
												{ ExpectedTypes.PROP_VALUE, "substrate" },
												{ ExpectedTypes.PROP_EDITABLE, true },
												{ ExpectedTypes.PROP_VISUALISABLE, true },
												{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
												{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
												{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
												{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
												{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
												},
										},
								}
							},
					}
				},
				{
					ExpectedTypes.TARGET_TERM, new Object[][]{
							{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(
									LinkTermEditableAttributes.END_DECORATOR_TYPE, LinkTermEditableAttributes.END_SIZE,
									LinkTermEditableAttributes.OFFSET) },
							{ ExpectedTypes.DEFAULT_TERM_COLOUR, RGB.WHITE },
							{ ExpectedTypes.DEFAULT_TERM_SIZE, new Dimension(0, 0) },
							{ ExpectedTypes.DEFAULT_END_SIZE, new Dimension(8, 8) },
							{ ExpectedTypes.DEFAULT_GAP, Short.valueOf((short)0) },
							{ ExpectedTypes.DEFAULT_END_TYPE, LinkEndDecoratorShape.NONE },
							{ ExpectedTypes.DEFAULT_SIZE, new Dimension(20, 20) },
							{
								ExpectedTypes.PROP_DEFNS, new Object[][]{
										{ "STOICH", new Object[][]{
												{ ExpectedTypes.PROP_VALUE, "1" },
												{ ExpectedTypes.PROP_EDITABLE, true },
												{ ExpectedTypes.PROP_VISUALISABLE, true },
												{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
												{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
												{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
												{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
												{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
												},
										},
								}
							},
					}
				},
		};
		validateLinkObjectType(rules);
	}
	
	@Test
	public void testProduceType(){
		Object rules[][] = {
				{ ExpectedTypes.TYPE_NAME, "Produce" },
				{ ExpectedTypes.TYPE_DESCRIPTION, "No descn" },
				{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(LinkEditableAttributes.COLOUR,
						LinkEditableAttributes.LINE_STYLE, LinkEditableAttributes.LINE_WIDTH) },
				{ ExpectedTypes.DEFAULT_NAME, "Production Link" },
				{ ExpectedTypes.DEFAULT_DESCN, "" },
				{ ExpectedTypes.DEFAULT_DETAILED_DESCN, "" },
				{ ExpectedTypes.DEFAULT_LINE_COLOUR, RGB.BLACK },
				{ ExpectedTypes.DEFAULT_LINE_STYLE, LineStyle.SOLID },
				{ ExpectedTypes.DEFAULT_LINE_WIDTH, 1 },
				{ ExpectedTypes.DEFAULT_URL, "" },
							{
					ExpectedTypes.PROP_DEFNS, new Object[][]{}
				},
				{
					ExpectedTypes.CONNECTION_TABLE, new String[][] {
							{ "Process", "Compound" }							
					}
				},
				{
					ExpectedTypes.SOURCE_TERM, new Object[][]{
							{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(
									LinkTermEditableAttributes.END_DECORATOR_TYPE, LinkTermEditableAttributes.END_SIZE,
									LinkTermEditableAttributes.OFFSET) },
							{ ExpectedTypes.DEFAULT_TERM_COLOUR, RGB.WHITE },
							{ ExpectedTypes.DEFAULT_TERM_SIZE, new Dimension(0, 0) },
							{ ExpectedTypes.DEFAULT_END_SIZE, new Dimension(8, 8) },
							{ ExpectedTypes.DEFAULT_GAP, Short.valueOf((short)2) },
							{ ExpectedTypes.DEFAULT_END_TYPE, LinkEndDecoratorShape.NONE },
							{
								ExpectedTypes.PROP_DEFNS, new Object[][]{
										{ "STOICH", new Object[][]{
												{ ExpectedTypes.PROP_VALUE, "1" },
												{ ExpectedTypes.PROP_EDITABLE, true },
												{ ExpectedTypes.PROP_VISUALISABLE, true },
												{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
												{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
												{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
												{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
												{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
												},
										},
								}
							},
					}
				},
				{
					ExpectedTypes.TARGET_TERM, new Object[][]{
							{ ExpectedTypes.TYPE_EDITABLE, EnumSet.of(
									LinkTermEditableAttributes.END_DECORATOR_TYPE, LinkTermEditableAttributes.END_SIZE,
									LinkTermEditableAttributes.OFFSET) },
							{ ExpectedTypes.DEFAULT_TERM_COLOUR, RGB.WHITE },
							{ ExpectedTypes.DEFAULT_TERM_SIZE, new Dimension(0, 0) },
							{ ExpectedTypes.DEFAULT_END_SIZE, new Dimension(8, 8) },
							{ ExpectedTypes.DEFAULT_GAP, Short.valueOf((short)0) },
							{ ExpectedTypes.DEFAULT_END_TYPE, LinkEndDecoratorShape.TRIANGLE },
							{ ExpectedTypes.DEFAULT_SIZE, new Dimension(20, 20) },
							{
								ExpectedTypes.PROP_DEFNS, new Object[][]{
										{ "ROLE", new Object[][]{
												{ ExpectedTypes.PROP_VALUE, "product" },
												{ ExpectedTypes.PROP_EDITABLE, true },
												{ ExpectedTypes.PROP_VISUALISABLE, true },
												{ ExpectedTypes.LABEL_FILL_COLOUR, RGB.WHITE },
												{ ExpectedTypes.LABEL_LINE_COLOUR, RGB.BLACK },
												{ ExpectedTypes.LABEL_LINE_STYLE, LineStyle.SOLID },
												{ ExpectedTypes.LABEL_LINE_WIDTH, 1 },
												{ ExpectedTypes.LABEL_SIZE, new Dimension(10, 10) },
												},
										},
								}
							},
					}
				},
		};
		validateLinkObjectType(rules);
	}
	
	private void validateLinkObjectType(Object[][] rules){
		Map<ExpectedTypes, Object> expectations = new HashMap<ExpectedTypes, Object>();
		for(Object[] row : rules){
			ExpectedTypes type = (ExpectedTypes)row[0];
			expectations.put(type, row[1]);
		}
		ILinkObjectType actualObjectType = this.testInstance.findLinkObjectTypeByName((String)expectations.get(ExpectedTypes.TYPE_NAME));
		validateLinkObjectType(actualObjectType, expectations);
		validateTerminusDefinition(actualObjectType, actualObjectType.getSourceTerminusDefinition(), createExpectationsMap((Object[][])expectations.get(ExpectedTypes.SOURCE_TERM)));
//		validateTerminusDefinition(actualObjectType, actualObjectType.getTargetTerminusDefinition(), createExpectationsMap((Object[][])expectations.get(ExpectedTypes.TARGET_TERM)));
		ILinkConnectionRules connectionRules = actualObjectType.getLinkConnectionRules();
		validateConnections(connectionRules, (String[][])expectations.get(ExpectedTypes.CONNECTION_TABLE));
	}
	
	private void validateLinkObjectType(ILinkObjectType actualObjectType, Map<ExpectedTypes, Object> expectations){
		ILinkAttributeDefaults actualAttribute = actualObjectType.getDefaultAttributes();
		assertEquals("expected syntax service", this.testInstance, actualObjectType.getSyntaxService());
		assertEquals("expected type name", expectations.get(ExpectedTypes.TYPE_NAME), actualObjectType.getName());
		assertEquals("expected type descn", expectations.get(ExpectedTypes.TYPE_DESCRIPTION), actualObjectType.getDescription());
		assertEquals("expected type editable", expectations.get(ExpectedTypes.TYPE_EDITABLE), actualObjectType.getEditableAttributes());
		assertEquals("expected line colour", expectations.get(ExpectedTypes.DEFAULT_LINE_COLOUR), actualAttribute.getLineColour());
		assertEquals("expected line style", expectations.get(ExpectedTypes.DEFAULT_LINE_STYLE), actualAttribute.getLineStyle());
		assertEquals("expected line width", expectations.get(ExpectedTypes.DEFAULT_LINE_WIDTH), actualAttribute.getLineWidth());
		assertNotNull("expected non-null type name", actualObjectType.getName());
		assertNotNull("expected non-null type descn", actualObjectType.getDescription());
		assertNotNull("expected non-null type editable", actualObjectType.getEditableAttributes());
		assertNotNull("expected non-null line colour", actualAttribute.getLineColour());
		assertNotNull("expected non-null line style", actualAttribute.getLineStyle());
		assertNotNull("expected non-null line width", actualAttribute.getLineWidth());
		validateProperties(actualAttribute, (Object[][])expectations.get(ExpectedTypes.PROP_DEFNS));
	}

	private static Map<ExpectedTypes, Object> createExpectationsMap(Object[][] rules){
		Map<ExpectedTypes, Object> expectations = new HashMap<ExpectedTypes, Object>();
		for(Object[] row : rules){
			ExpectedTypes type = (ExpectedTypes)row[0];
			expectations.put(type, row[1]);
		}
		return expectations;
	}
	
	private void validateTerminusDefinition(ILinkObjectType linkObjectType, ILinkTerminusDefinition actualTerminusDefn, Map<ExpectedTypes, Object> expectations){
		ILinkTerminusDefaults actualAttribute = actualTerminusDefn.getDefaultAttributes();
		assertEquals("expected syntax service", linkObjectType, actualTerminusDefn.getOwningObjectType());
		assertEquals("expected type editable", expectations.get(ExpectedTypes.TYPE_EDITABLE), actualTerminusDefn.getEditableAttributes());
		assertEquals("expected gap", expectations.get(ExpectedTypes.DEFAULT_GAP), actualAttribute.getGap());
		assertEquals("expected end type", expectations.get(ExpectedTypes.DEFAULT_END_TYPE), actualAttribute.getEndDecoratorType());
		assertEquals("expected end size", expectations.get(ExpectedTypes.DEFAULT_END_SIZE), actualAttribute.getEndSize());
	}
	
	private Map<IShapeObjectType, Set<IShapeObjectType>> createRuleExpectations(String[][] expectedRules){
		final Map<IShapeObjectType, Set<IShapeObjectType>> expectedRuleLookup = new HashMap<IShapeObjectType, Set<IShapeObjectType>>();
//		Iterator<IShapeObjectType> shapeOtIter = this.testInstance.shapeTypeIterator();
//		while(shapeOtIter.hasNext()){
//			IShapeObjectType shapeOt = shapeOtIter.next();
//			expectedRuleLookup.put(shapeOt, new HashSet<IShapeObjectType>());
//		}
		for(String row[] : expectedRules){
			IShapeObjectType source = this.testInstance.findShapeObjectTypeByName(row[0]);
			IShapeObjectType target = this.testInstance.findShapeObjectTypeByName(row[1]);
			assertNotNull("source object type found", source);
			assertNotNull("target object type found", target);
			if(!expectedRuleLookup.containsKey(source)){
				expectedRuleLookup.put(source, new HashSet<IShapeObjectType>());
			}
			expectedRuleLookup.get(source).add(target);
		}
		return expectedRuleLookup;
	}
	
	private void validateConnections(ILinkConnectionRules connectionRules, String[][] expectedRules){
		final Map<IShapeObjectType, Set<IShapeObjectType>> expectedRuleLookup = createRuleExpectations(expectedRules);
		Iterator<IShapeObjectType> sourceOtIter = this.testInstance.shapeTypeIterator();
		while(sourceOtIter.hasNext()){
			IShapeObjectType sourceOt = sourceOtIter.next();
			Iterator<IShapeObjectType> targetOtIter = this.testInstance.shapeTypeIterator();
			while(targetOtIter.hasNext()){
				IShapeObjectType targetOt = targetOtIter.next();
				assertEquals("expected connection source for: " + sourceOt.getName(),
						expectedRuleLookup.containsKey(sourceOt), connectionRules.isValidSource(sourceOt));
				boolean expectedResult = expectedRuleLookup.containsKey(sourceOt) ? expectedRuleLookup.get(sourceOt).contains(targetOt) : false; 
				assertEquals("expected connection rule for: " + sourceOt.getName() + " - " + targetOt.getName(),
						expectedResult, connectionRules.isValidTarget(sourceOt, targetOt));
			}
		}
	}
	
	private void validateShapeObjectType(Object[][] rules){
		Map<ExpectedTypes, Object> expectations = new HashMap<ExpectedTypes, Object>();
		for(Object[] row : rules){
			ExpectedTypes type = (ExpectedTypes)row[0];
			expectations.put(type, row[1]);
		}
		IShapeObjectType actualShapeObjectType = this.testInstance.findShapeObjectTypeByName((String)expectations.get(ExpectedTypes.TYPE_NAME));
		validateShapeObjectType(actualShapeObjectType, expectations);
		IObjectTypeParentingRules parentingRules = actualShapeObjectType.getParentingRules();
		validateParenting(parentingRules, (Object[][])expectations.get(ExpectedTypes.PARENTING_TABLE));
	}
	
	private void validateShapeObjectType(IShapeObjectType actualShapeObjectType, Map<ExpectedTypes, Object> expectations){
		IShapeAttributeDefaults actualAttribute = actualShapeObjectType.getDefaultAttributes();
		assertEquals("expected syntax service", this.testInstance, actualShapeObjectType.getSyntaxService());
		assertEquals("expected type name", expectations.get(ExpectedTypes.TYPE_NAME), actualShapeObjectType.getName());
		assertEquals("expected type descn", expectations.get(ExpectedTypes.TYPE_DESCRIPTION), actualShapeObjectType.getDescription());
		assertEquals("expected type editable", expectations.get(ExpectedTypes.TYPE_EDITABLE), actualShapeObjectType.getEditableAttributes());
		assertEquals("expected fill colour", expectations.get(ExpectedTypes.DEFAULT_FILL_COLOUR), actualAttribute.getFillColour());
		assertEquals("expected line colour", expectations.get(ExpectedTypes.DEFAULT_LINE_COLOUR), actualAttribute.getLineColour());
		assertEquals("expected line style", expectations.get(ExpectedTypes.DEFAULT_LINE_STYLE), actualAttribute.getLineStyle());
		assertEquals("expected line width", expectations.get(ExpectedTypes.DEFAULT_LINE_WIDTH), actualAttribute.getLineWidth());
		assertEquals("expected size", expectations.get(ExpectedTypes.DEFAULT_SIZE), actualAttribute.getSize());
		validateProperties(actualAttribute, (Object[][])expectations.get(ExpectedTypes.PROP_DEFNS));
	}
	
	private void validateProperties(IPropertyDefinitionContainer actualAttribute, Object[][] rules){
		assertEquals("expected num properties", rules.length, actualAttribute.numPropertyDefinitions());
		for(Object[]  outerRow : rules){
			String propName = (String)outerRow[0];
			Object[][] propertyRules = (Object[][])outerRow[1];
			Map<ExpectedTypes, Object> expectations = new HashMap<ExpectedTypes, Object>();
			for(Object[] row : propertyRules){
				ExpectedTypes type = (ExpectedTypes)row[0];
				expectations.put(type, row[1]);
			}
			IPropertyDefinition propDefn = actualAttribute.getPropertyDefinition(propName);
			assertEquals("expected value", expectations.get(ExpectedTypes.PROP_VALUE), propDefn.getDefaultValue());
			assertEquals("expected editable", expectations.get(ExpectedTypes.PROP_EDITABLE), propDefn.isEditable());
			assertEquals("expected visualisable", expectations.get(ExpectedTypes.PROP_VISUALISABLE), this.testInstance.isVisualisableProperty(propDefn));
			ILabelAttributeDefaults labelDefaults = this.testInstance.getLabelObjectTypeByProperty(propDefn).getDefaultAttributes();
			assertEquals("expected fill colour", expectations.get(ExpectedTypes.LABEL_FILL_COLOUR), labelDefaults.getFillColour());
			assertEquals("expected line colour", expectations.get(ExpectedTypes.LABEL_LINE_COLOUR), labelDefaults.getLineColour());
			assertEquals("expected line style", expectations.get(ExpectedTypes.LABEL_LINE_STYLE), labelDefaults.getLineStyle());
			assertEquals("expected line width", expectations.get(ExpectedTypes.LABEL_LINE_WIDTH), labelDefaults.getLineWidth());
			assertEquals("expected size", expectations.get(ExpectedTypes.LABEL_SIZE), labelDefaults.getMinimumSize());
		}
	}
	
	
	private void validateParenting(IObjectTypeParentingRules parentingRules, Object[][] expectedRules){
		final int nameIdx = 0;
		final int expectationIdx = 1;
		// ensure that all the parentable object types are being exampled  
		assertEquals("expected num of rules to validate", expectedRules.length, this.testInstance.numShapeObjectTypes()+1);
		for(int i = 0; i < expectedRules.length; i++){
			String expectedTypeName = (String)expectedRules[i][nameIdx];
			IObjectType ot = null;
			if(expectedTypeName.equals("ROOT_OBJECT")){
				ot = this.testInstance.getRootObjectType();
			}
			else{
				ot = this.testInstance.findShapeObjectTypeByName(expectedTypeName);
			}
			assertNotNull("object type found", ot);
			assertEquals("expected parenting rule for: " + expectedTypeName, expectedRules[i][expectationIdx], parentingRules.isValidChild(ot));
		}
	}
	
	
	@After
	public void tearDown() throws Exception {
		this.testInstance = null;
	}

}
