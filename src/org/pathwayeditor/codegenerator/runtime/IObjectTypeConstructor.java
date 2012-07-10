package org.pathwayeditor.codegenerator.runtime;

import org.pathwayeditor.businessobjects.typedefn.IObjectType;

public interface IObjectTypeConstructor<T extends IObjectType> {

	T create();

}
