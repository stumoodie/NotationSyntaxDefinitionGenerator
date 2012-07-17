package org.pathwayeditor.codegenerator;

import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pathwayeditor.businessobjects.drawingprimitives.IModel;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.Colour;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.LineStyle;
import org.pathwayeditor.businessobjects.drawingprimitives.attributes.Version;
import org.pathwayeditor.businessobjects.drawingprimitives.properties.IPropertyDefinition;
import org.pathwayeditor.businessobjects.notationsubsystem.INotation;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationAutolayoutService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationConversionService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationExportService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationImportService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationPluginService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSubsystem;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationSyntaxService;
import org.pathwayeditor.businessobjects.notationsubsystem.INotationValidationService;
import org.pathwayeditor.businessobjects.typedefn.IObjectType;
import org.pathwayeditor.businessobjects.typedefn.IObjectTypeParentingRules;
import org.pathwayeditor.businessobjects.typedefn.IRootObjectType;
import org.pathwayeditor.businessobjects.typedefn.IShapeAttributeDefaults;
import org.pathwayeditor.businessobjects.typedefn.IShapeObjectType;
import org.pathwayeditor.figure.geometry.Dimension;
import org.pathwayeditor.figure.rendering.GenericFont;
import org.pathwayeditor.figure.rendering.IFont;
import org.pathwayeditor.notationsubsystem.toolkit.definition.GeneralNotation;
import org.pathwayeditor.notationsubsystem.toolkit.definition.IntegerPropertyDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.PlainTextPropertyDefinition;
import org.pathwayeditor.notationsubsystem.toolkit.definition.RootObjectType;

public class SimpleNotationSyntaxServiceTest {
	private static final String TEST_NOT_QUALIFIED_NAME = "org.pathwayeditor.codegenerator.Simple";
	private static final String TEST_NOT_DISP_NAME = "Simple Notation";
	private static final String TEST_NOT_DESCN = "Simple Notation Description";
	private static final Version TEST_NOT_VERSION = new Version(1, 0, 1);
	private static final String EXPECTED_ROOT_NAME = RootObjectType.DEFAULT_NAME;
	private static final String EXPECTED_ROOT_DESCN = RootObjectType.DEFAULT_DESCN;
	private static final int INVALID_UID = 192761727;
	private static final int NUM_SHAPE_OTS = 2;
	private static final int NUM_LINK_OTS = 1;
	private static final int NUM_OTS = 7;
	private static final int NUM_ANCHOR_NODE_OTS = 1;
	private static final String EXPECTED_SHAPE_NAME = "Compartment";
	private static final String EXPECTED_SHAPE_DESCN = "Functional compartment";
	private static final EnumSet<IShapeObjectType.EditableShapeAttributes> EXPECTED_SHAPE_EDITABLE_ATTS = EnumSet.allOf(IShapeObjectType.EditableShapeAttributes.class);
	private static final Colour EXPECTED_SHAPE_FILL = Colour.WHITE;
	private static final Colour EXPECTED_SHAPE_LINE_COL = Colour.BLACK;
	private static final Colour EXPECTED_SHAPE_FONT_COL = Colour.RED;
	private static final GenericFont EXPECTED_SHAPE_FONT = new GenericFont(12.0, EnumSet.of(IFont.Style.NORMAL));
	private static final LineStyle EXPECTED_SHAPE_LINE_STYLE = LineStyle.SOLID;
	private static final double EXPECTED_SHAPE_LINE_WIDTH = 2.0;
	private static final String EXPECTED_SHAPE_FIG_DEFN = "rect";
	private static final Dimension EXPECTED_SHAPE_SIZE = new Dimension(200, 200);
	private static final double DOUBLE_CMP_THRES = 0.000004;
	private static final String EXPECTED_SHAPE_PROP_NAME = "Cardinality";
	private static final String EXPECTED_SHAPE_PROP_NAME2 = "Name";
	private INotationSyntaxService testInstance;
	private static INotation TEST_NOTATION = new GeneralNotation(TEST_NOT_QUALIFIED_NAME, TEST_NOT_DISP_NAME, TEST_NOT_DESCN, TEST_NOT_VERSION);
	private INotationSubsystem expectedNotationSubsystem;
	
	@Before
	public void setUp() throws Exception {
		expectedNotationSubsystem = new StubNotationSubsystem();
		this.testInstance = new SimpleNotationSyntaxService(expectedNotationSubsystem);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetNotation() {
		assertEquals("correct notation", TEST_NOTATION, this.testInstance.getNotation());
	}

	@Test
	public void testGetNotationSubsystem() {
		assertEquals("Correct NS", this.expectedNotationSubsystem, this.testInstance.getNotationSubsystem());
	}

	@Test
	public void testShapeTypeIterator() {
		assertIterator(this.testInstance.shapeTypeIterator(), SimpleNotationSyntaxService.OT_Compartment, SimpleNotationSyntaxService.OT_Process);
	}

	@Test
	public void testLinkTypeIterator() {
		assertIterator(this.testInstance.linkTypeIterator(), SimpleNotationSyntaxService.OT_Consume);
	}

	@Test
	public void testObjectTypeIterator() {
		assertIterator(this.testInstance.objectTypeIterator(), SimpleNotationSyntaxService.OT_rootNode, SimpleNotationSyntaxService.OT_Compartment, SimpleNotationSyntaxService.OT_Process,
				SimpleNotationSyntaxService.OT_Consume, SimpleNotationSyntaxService.OT_Outcome, SimpleNotationSyntaxService.OT_generalLab, SimpleNotationSyntaxService.OT_ecLabel);
	}

	@Test
	public void testGetRootObjectType() {
		IRootObjectType expectedRootObjectType = this.testInstance.getRootObjectType(); 
		assertEquals("expected idx", SimpleNotationSyntaxService.OT_rootNode, expectedRootObjectType.getUniqueId());
		assertEquals("expected name", EXPECTED_ROOT_NAME, expectedRootObjectType.getName());
		assertEquals("expected descn", EXPECTED_ROOT_DESCN, expectedRootObjectType.getDescription());
		assertParentingRules(expectedRootObjectType.getParentingRules(), SimpleNotationSyntaxService.OT_Compartment, SimpleNotationSyntaxService.OT_Process);
	}

	@Test
	public void testContainsShapeObjectType() {
		assertTrue("contains ot", this.testInstance.containsShapeObjectType(SimpleNotationSyntaxService.OT_Compartment));
		assertTrue("contains ot", this.testInstance.containsShapeObjectType(SimpleNotationSyntaxService.OT_Process));
		assertFalse("not contain ot", this.testInstance.containsShapeObjectType(SimpleNotationSyntaxService.OT_generalLab));
	}

	@Test
	public void testGetShapeObjectType() {
		assertNotNull("exists", this.testInstance.getShapeObjectType(SimpleNotationSyntaxService.OT_Compartment));
		assertNotNull("exists", this.testInstance.getShapeObjectType(SimpleNotationSyntaxService.OT_Process));
		assertNull("not exists", this.testInstance.getShapeObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertNull("not exists", this.testInstance.getShapeObjectType(INVALID_UID));
	}

	@Test
	public void testGetShapeObjectValidateValuesType() {
		IShapeObjectType actualOt = this.testInstance.getShapeObjectType(SimpleNotationSyntaxService.OT_Compartment);
		assertEquals("expected name", EXPECTED_SHAPE_NAME, actualOt.getName());
		assertEquals("expected descn", EXPECTED_SHAPE_DESCN, actualOt.getDescription());
		assertEquals("expected name", EXPECTED_SHAPE_EDITABLE_ATTS, actualOt.getEditableAttributes());
		assertParentingRules(actualOt.getParentingRules(), SimpleNotationSyntaxService.OT_Process);
		IShapeAttributeDefaults otDefaults = actualOt.getDefaultAttributes();
		assertEquals("expected fill", EXPECTED_SHAPE_FILL, otDefaults.getFillColour());
		assertEquals("expected line col", EXPECTED_SHAPE_LINE_COL, otDefaults.getLineColour());
		assertEquals("expected font col", EXPECTED_SHAPE_FONT_COL, otDefaults.getFontColour());
		assertEquals("expected font", EXPECTED_SHAPE_FONT, otDefaults.getFont());
		assertEquals("expected line style", EXPECTED_SHAPE_LINE_STYLE, otDefaults.getLineStyle());
		assertEquals("expected line width", EXPECTED_SHAPE_LINE_WIDTH, otDefaults.getLineWidth(), DOUBLE_CMP_THRES);
		assertEquals("expected fig defn", EXPECTED_SHAPE_FIG_DEFN, otDefaults.getShapeDefinition());
		assertEquals("expected size", EXPECTED_SHAPE_SIZE, otDefaults.getSize());
		assertTrue("has prop", otDefaults.containsPropertyDefinition(EXPECTED_SHAPE_PROP_NAME));
		IPropertyDefinition expectedPropDefn = otDefaults.getPropertyDefinition(EXPECTED_SHAPE_PROP_NAME);
		assertNotNull("expected prop defn", expectedPropDefn);
		assertFalse("not visualisable", this.testInstance.isVisualisableProperty(expectedPropDefn));
		assertTrue("has prop", otDefaults.containsPropertyDefinition(EXPECTED_SHAPE_PROP_NAME2));
		IPropertyDefinition expectedPropDefn2 = otDefaults.getPropertyDefinition(EXPECTED_SHAPE_PROP_NAME2);
		assertNotNull("expected prop defn", expectedPropDefn2);
		assertTrue("is visualisable", this.testInstance.isVisualisableProperty(expectedPropDefn2));
	}

	@Test
	public void testContainsLinkObjectType() {
		assertTrue("contains ot", this.testInstance.containsLinkObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertFalse("not contain ot", this.testInstance.containsLinkObjectType(SimpleNotationSyntaxService.OT_generalLab));
	}

	@Test
	public void testGetLinkObjectType() {
		assertNotNull("exists", this.testInstance.getLinkObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertNull("not exists", this.testInstance.getLinkObjectType(SimpleNotationSyntaxService.OT_ecLabel));
	}

	@Test
	public void testContainsObjectType() {
		assertTrue("contains ot", this.testInstance.containsObjectType(SimpleNotationSyntaxService.OT_rootNode));
		assertTrue("contains ot", this.testInstance.containsObjectType(SimpleNotationSyntaxService.OT_Compartment));
		assertTrue("contains ot", this.testInstance.containsObjectType(SimpleNotationSyntaxService.OT_Outcome));
		assertTrue("contains ot", this.testInstance.containsObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertTrue("contains ot", this.testInstance.containsObjectType(SimpleNotationSyntaxService.OT_generalLab));
		assertFalse("not contain ot", this.testInstance.containsObjectType(INVALID_UID));
	}

	@Test
	public void testGetObjectType() {
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_rootNode));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_Compartment));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_ecLabel));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_generalLab));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_Outcome));
		assertNotNull("exists", this.testInstance.getObjectType(SimpleNotationSyntaxService.OT_Process));
		assertNull("not exists", this.testInstance.getObjectType(INVALID_UID));
	}

	@Test
	public void testGetLabelObjectType() {
		assertNotNull("exists", this.testInstance.getLabelObjectType(SimpleNotationSyntaxService.OT_ecLabel));
		assertNotNull("exists", this.testInstance.getLabelObjectType(SimpleNotationSyntaxService.OT_generalLab));
		assertNull("not exists", this.testInstance.getLabelObjectType(SimpleNotationSyntaxService.OT_Consume));
	}

	@Test
	public void testGetLabelObjectTypeByProperty() {
		IPropertyDefinition expectedProp = new PlainTextPropertyDefinition("EC", "--.--.--.--");
		assertNotNull("expected", this.testInstance.getLabelObjectTypeByProperty(expectedProp));
		assertEquals("expected prop", SimpleNotationSyntaxService.OT_ecLabel, this.testInstance.getLabelObjectTypeByProperty(expectedProp).getUniqueId());
	}

	@Test
	public void testIsVisualisableProperty() {
		IPropertyDefinition expectedProp = new PlainTextPropertyDefinition("EC", "--.--.--.--");
		assertTrue("is visualisable", this.testInstance.isVisualisableProperty(expectedProp));
		IPropertyDefinition expectedStoichProp = new IntegerPropertyDefinition("Stoich", 2);
		assertFalse("noit visualisable", this.testInstance.isVisualisableProperty(expectedStoichProp));
		assertFalse("not visualisable", this.testInstance.isVisualisableProperty(null));
	}

	@Test
	public void testNumShapeObjectTypes() {
		assertEquals("expected num", NUM_SHAPE_OTS, this.testInstance.numShapeObjectTypes());
	}

	@Test
	public void testNumLinkObjectTypes() {
		assertEquals("expected num", NUM_LINK_OTS, this.testInstance.numLinkObjectTypes());
	}

	@Test
	public void testNumObjectTypes() {
		assertEquals("expected num", NUM_OTS, this.testInstance.numObjectTypes());
	}

	@Test
	public void testAnchorNodeTypeIterator() {
		assertIterator(this.testInstance.anchorNodeTypeIterator(), SimpleNotationSyntaxService.OT_Outcome);
	}

	@Test
	public void testNumAnchorNodeTypes() {
		assertEquals("expected num", NUM_ANCHOR_NODE_OTS, this.testInstance.numAnchorNodeTypes());
	}

	@Test
	public void testGetAnchorNodeObjectType() {
		assertNotNull("exists", this.testInstance.getAnchorNodeObjectType(SimpleNotationSyntaxService.OT_Outcome));
		assertNull("not exists", this.testInstance.getAnchorNodeObjectType(SimpleNotationSyntaxService.OT_Consume));
		assertNull("not exists", this.testInstance.getAnchorNodeObjectType(INVALID_UID));
	}

	@Test
	public void testContainsAnchorNodeObjectType() {
		assertTrue("contains ot", this.testInstance.containsAnchorNodeObjectType(SimpleNotationSyntaxService.OT_Outcome));
		assertFalse("not contain ot", this.testInstance.containsAnchorNodeObjectType(SimpleNotationSyntaxService.OT_generalLab));
	}

	private <T extends IObjectType> void assertIterator(Iterator<T> testIter, int ... expectedResults){
		int cnt = 0;
		while(testIter.hasNext()){
			IObjectType expectedOt = testInstance.getObjectType(expectedResults[cnt]);
			assertNotNull("expected ot extsts", expectedOt);
			assertEquals("Expected value", expectedOt, testIter.next());
			cnt++;
		}
		assertEquals("same size", expectedResults.length, cnt);
	}
	
	private void assertParentingRules(IObjectTypeParentingRules testIter, int ... expectedResults){
		int cnt = 0;
		Iterator<IObjectType> otIter = this.testInstance.objectTypeIterator();
		while(otIter.hasNext()){
			IObjectType otype = otIter.next();
			if(cnt < expectedResults.length && otype.equals(this.testInstance.getObjectType(expectedResults[cnt]))){
				assertTrue("Expected children", testIter.isValidChild(otype));
				cnt++;
			}
			else{
				assertFalse("No Expected children", testIter.isValidChild(otype));
			}
		}
	}
	
	private class StubNotationSubsystem implements INotationSubsystem {

		@Override
		public void registerModel(IModel modelToRegister) {
			
		}

		@Override
		public void unregisterModel(IModel modelToRegister) {
			
		}

		@Override
		public INotation getNotation() {
			return TEST_NOTATION;
		}

		@Override
		public boolean isFallback() {
			return false;
		}

		@Override
		public INotationSyntaxService getSyntaxService() {
			return testInstance;
		}

		@Override
		public Set<INotationExportService> getExportServices() {
			return null;
		}

		@Override
		public Set<INotationImportService> getImportServices() {
			return null;
		}

		@Override
		public INotationAutolayoutService getAutolayoutService() {
			return null;
		}

		@Override
		public INotationValidationService getValidationService() {
			return null;
		}

		@Override
		public Set<INotationPluginService> getPluginServices() {
			return null;
		}

		@Override
		public Set<INotationConversionService> getConversionServices() {
			return null;
		}
	
	};
}
