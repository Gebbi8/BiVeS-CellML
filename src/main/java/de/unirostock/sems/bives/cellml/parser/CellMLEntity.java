/**
 * 
 */
package de.unirostock.sems.bives.cellml.parser;

import java.util.Vector;

import de.unirostock.sems.bives.ds.RDF;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeNode;


/**
 * @author Martin Scharm
 *
 */
public class CellMLEntity
{
	protected CellMLModel model;
	private Vector<RDF> rdfDescription;
	// might be null
	private DocumentNode node;
	
	public CellMLEntity (DocumentNode node, CellMLModel model)
	{
		this.model = model;
		this.node = node;
		rdfDescription = new Vector<RDF> ();

		if (model != null)
			model.mapNode (node, this);
		
		if (node != null)
		{
			Vector<TreeNode> kids= node.getChildrenWithTag ("rdf:RDF");
			for (TreeNode kid : kids)
			{
				if (kid.getType () != TreeNode.DOC_NODE)
					continue;
				rdfDescription.add (new RDF ((DocumentNode) kid));
			}
		}
	}
	
	public DocumentNode getDocumentNode ()
	{
		return node;
	}
}
