/**
 * 
 */
package de.unirostock.sems.bives.cellml.parser;

import java.util.Vector;


/**
 * @author Martin Scharm
 *
 */
public class CellMLHierarchyNode
{
	private CellMLComponent component;

	private CellMLHierarchyNode parent;
	private Vector<CellMLHierarchyNode> children;
	
	public CellMLHierarchyNode (CellMLComponent component)
	{
		this.component = component;
		children = new Vector<CellMLHierarchyNode> ();
	}
	
	public CellMLComponent getComponent ()
	{
		return component;
	}
	
	public void setParent (CellMLHierarchyNode parent)
	{
		this.parent = parent;
	}
	
	public CellMLHierarchyNode getParent ()
	{
		return parent;
	}
	
	public void addChild (CellMLHierarchyNode child)
	{
		this.children.add (child);
	}
	
	public Vector<CellMLHierarchyNode> getChildren ()
	{
		return children;
	}
	
	public String toString ()
	{
		String r = "[hirarchy of " + component.getName ();
		if (parent != null)
			r += " p:" + parent.component.getName ();
		for (CellMLHierarchyNode c : children)
			r += " c:" + c.component.getName ();
		return r + "]";
	}
}
