/**
 * 
 */
package de.unirostock.sems.bives.cellml.parser;

import java.util.HashMap;
import java.util.Vector;

import de.unirostock.sems.bives.cellml.exception.BivesCellMLParseException;
import de.unirostock.sems.bives.ds.MathML;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.exception.BivesLogicalException;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;
import de.unirostock.sems.xmlutils.exception.XmlDocumentConsistencyException;


/**
 * @author Martin Scharm
 *
 */
public class CellMLComponent
extends CellMLEntity
{
	
	// Each <component> must have a name attribute, the value of which is a unique identifier for the component amongst all other components within the current model.
	private String name;
	
	// A modeller can define a set of units to use within the component
	private CellMLUnitDictionary units;
	
	// A component may contain any number of <variable> elements, which define variables that may be mathematically related in the equation blocks contained in the component.
	private HashMap<String, CellMLVariable> variables;
	
	// A component may contain <reaction> elements, which are used to provide chemical and biochemical context for the equations describing a reaction. It is recommended that only one <reaction> element appear in any <component> element.
	private Vector<CellMLReaction> reactions;
	
	// A component may contain a set of mathematical relationships between the variables declared in this component.
	private Vector<MathML> math;
	
	/*public CellMLComponent copy (CellMLModel model, String newName) throws BivesConsistencyException, BivesCellMLParseException, BivesLogicalException
	{
		DocumentNode node = getDocumentNode ().extract ();
		node.setAttribute ("name", newName);
		CellMLComponent cpy = new CellMLComponent (model, node);
		return cpy;
	}*/
	
	public CellMLComponent (CellMLModel model, DocumentNode node) throws BivesCellMLParseException, BivesLogicalException, BivesDocumentConsistencyException
	{
		super (node, model);
		
		units = model.getUnits ();
		math = new Vector<MathML> ();
		variables = new HashMap<String, CellMLVariable> ();
		reactions = new Vector<CellMLReaction> ();

		name = node.getAttribute ("name");
		if (name == null || name.length () < 1)
			throw new BivesCellMLParseException ("component doesn't have a name.");
		
		Vector<TreeNode> kids = node.getChildrenWithTag ("units");
		Vector<String> problems = new Vector<String> ();
		boolean nextRound = true;
		while (nextRound && kids.size () > 0)
		{
			nextRound = false;
			problems.clear ();
			for (int i = kids.size () - 1; i >= 0; i--)
			{
				TreeNode kid = kids.elementAt (i);
				if (kid.getType () != TreeNode.DOC_NODE)
					continue;
				try
				{
					units.addUnit (this, new CellMLUserUnit (model, units, this, (DocumentNode) kid));
				}
				catch (BivesDocumentConsistencyException ex)
				{
					problems.add (ex.getMessage ());
					continue;
				}
				kids.remove (i);
				nextRound = true;
			}
		}
		if (kids.size () != 0)
			throw new BivesDocumentConsistencyException ("inconsistencies for "+kids.size ()+" units in component "+name+", problems: " + problems);
		
		kids = node.getChildrenWithTag ("variable");
		while (nextRound && kids.size () > 0)
		{
			nextRound = false;
			problems.clear ();
			for (int i = kids.size () - 1; i >= 0; i--)
			{
				TreeNode kid = kids.elementAt (i);
				if (kid.getType () != TreeNode.DOC_NODE)
					continue;
				try
				{
					CellMLVariable var = new CellMLVariable (model, this, (DocumentNode) kid);
					if (variables.get (var.getName ()) != null)
						throw new BivesDocumentConsistencyException ("variable name is not unique: " + var.getName ());
					variables.put (var.getName (), var);
				}
				catch (BivesDocumentConsistencyException ex)
				{
					problems.add (ex.getMessage ());
					continue;
				}
				kids.remove (i);
				nextRound = true;
			}
		}
		if (kids.size () != 0)
			throw new BivesDocumentConsistencyException ("inconsistencies for "+kids.size ()+" variables in component "+name+", problems: " + problems);
		
		kids = node.getChildrenWithTag ("reaction");
		for (TreeNode kid : kids)
		{
			reactions.add (new CellMLReaction (model, this, (DocumentNode) kid));
		}
		
		kids = node.getChildrenWithTag ("math");
		for (TreeNode kid : kids)
		{
			math.add (new MathML ((DocumentNode) kid));
		}
	}
	
	public CellMLVariable getVariable (String name) throws BivesDocumentConsistencyException
	{
		CellMLVariable var = variables.get (name);
		if (var == null)
			throw new BivesDocumentConsistencyException ("unknown variable: " + name + " in component " + this.name);
		return var;
	}
	
	public CellMLUnit getUnit (String name)// throws BivesConsistencyException
	{
		return units.getUnit (name, this);
	}
	
	public String getName ()
	{
		return name;
	}
	
	public void setName (String name)
	{
		this.name = name;
		getDocumentNode ().setAttribute ("name", name);
	}
	
	public void debug (String prefix)
	{
		System.out.println (prefix + "comp: " + name);
		for (CellMLVariable v : variables.values ())
			v.debug (prefix + "  ");
	}
	
	public void unconnect ()
	{
		for (CellMLVariable var : variables.values ())
			var.unconnect ();
	}

	public Vector<CellMLUserUnit> getDependencies (Vector<CellMLUserUnit> vector)
	{
		for (CellMLVariable var : variables.values ())
		{
			var.getDependencies (vector);
		}
		
		return vector;
	}
	
	public Vector<CellMLReaction> getReactions ()
	{
		return reactions;
	}
	
	public HashMap<String, CellMLVariable> getVariables ()
	{
		return variables;
	}
	
	public Vector<MathML> getMath ()
	{
		return math;
	}
}
