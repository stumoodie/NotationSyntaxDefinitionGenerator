package org.pathwayeditor.codegenerator;


public class CodeGeneratorUtils {
	// protected HashSet<?>
	public static int[] getRGB(String hex) {
		hex = hex.replace("#", "");
		int r = Integer.parseInt(hex.substring(0, 2), 16);
		int g = Integer.parseInt(hex.substring(2, 4), 16);
		int b = Integer.parseInt(hex.substring(4), 16);
		return new int[] { r, g, b };
	}

	public static int[] getVersion(String ver) {
		String[] l = ver.split("\\.");
		int majorVersion = Integer.parseInt(l[0]);
		int minorVersion = Integer.parseInt(l[1]);
		int patchVersion = Integer.parseInt(l[2]);

		return new int[] { majorVersion, minorVersion, patchVersion };
	}
/*	
	public static TextPropertyDefinition reassignVal(TextPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		TextPropertyDefinition newP=new TextPropertyDefinition(prop.getName(),val,(prop.isEditable()&isEdit),(prop.isVisualisable() | isVis));
		return newP;
	}
	
	public static FormattedTextPropertyDefinition reassignVal(FormattedTextPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		FormattedTextPropertyDefinition newP=new FormattedTextPropertyDefinition(prop.getName(),val,(prop.isEditable()&isEdit),(prop.isVisualisable() | isVis));
		return newP;
	}
	
	public static NumberPropertyDefinition reassignVal(NumberPropertyDefinition prop,String val,boolean isEdit,boolean isVis){
		NumberPropertyDefinition newP=new NumberPropertyDefinition(prop.getName(),val,(prop.isEditable()&isEdit),(prop.isVisualisable() | isVis));
		return newP;
	}
*/	
}

/*
 * $Log$
 * Revision 1.2  2008/04/22 23:04:25  asorokin
 * Working CA syntax service provider generator
 *
 * Revision 1.1  2008/04/21 07:48:14  asorokin
 * ContextAdapter initial commit
 *
 */