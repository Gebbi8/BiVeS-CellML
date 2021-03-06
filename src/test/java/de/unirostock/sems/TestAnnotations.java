/**
 * 
 */
package de.unirostock.sems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.binfalse.bflog.LOGGER;
import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.ds.Patch;
import de.unirostock.sems.xmlutils.ds.DocumentNode;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.DocumentTools;
import de.unirostock.sems.xmlutils.tools.XmlTools;


/**
 * @author Martin Scharm
 *
 */
@RunWith(JUnit4.class)
public class TestAnnotations
{
	
	/** The test file for annotations. */
	private static final File		ANNOT_TEST_FILE	= new File ("test/annotation/mini.cellml");


	/**
	 * obtain the default cellml document
	 * @return the cellml document stored in the test file
	 */
	private static CellMLDocument  getValidTestModel ()
	{
		CellMLValidator val = new CellMLValidator ();
		if (!val.validate (ANNOT_TEST_FILE))
		{
			LOGGER.error (val.getError (), "annotation test case is not valid");
			fail ("annotation test case is not valid: " + val.getError ().toString ());
		}
		return val.getDocument ();
	}
	
	/**
	 * obtain the cellml document encoded in s.
	 *
	 * @param s the xml code of the model
	 * @return the model
	 */
	private static CellMLDocument getModel (String s)
	{
		CellMLValidator val = new CellMLValidator ();
		if (!val.validate (s))
		{
			LOGGER.error (val.getError (), "annotation test case is not valid");
			val.getError ().printStackTrace ();
			fail ("annotation test case is not valid: " + val.getError ().toString ());
		}
		return val.getDocument ();
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testCellmlSpec ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc2.getModel ().getDocumentNode ().setAttribute ("name", "hodgkin_huxley_model_excerpt2");
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replaceAll ("http://www.cellml.org/cellml/1.1", "http://www.cellml.org/cellml/1.0"));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, true, true, false, false, false, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeMath ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("<ci>i_L</ci>", "<cn> 10.0 </cn>");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 2, 2, 0, 0, false, false, true, false, false, false, false, false, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeAnnotation ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace (">hudgkin", ">hodgkin");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, false, false, false, false, false, true, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeTextualAnnotation ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("rocks", "sucks");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, false, false, false, false, false, false, true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testInsMath ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc1.getModel ().getComponent ("membrane").getDocumentNode ().rmChild (doc1.getModel ().getComponent ("membrane").getMath ().get (0).getDocumentNode ());
			doc1 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc1.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

			//System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 25, 0, 0, 0, false, true, true, false, false, false, false, false, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeReactionReversibility ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("reversible=\"no\"", "reversible=\"yes\"");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

			//System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, true, true, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeReaction ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("reactant", "product");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, true, false, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testDelReaction ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc2.getModel ().getComponent ("mySecondStupidComponent").getDocumentNode ().rmChild (doc2.getModel ().getComponent ("mySecondStupidComponent").getReactions ().get (0).getDocumentNode ());
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

			//System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 8, 0, 0, false, true, false, false, false, false, true, true, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeHierarchy ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			((DocumentNode) doc2.getTreeDocument ().getNodeByPath ("/model[1]/group[1]/component_ref[1]")).rmChild ((DocumentNode) doc2.getTreeDocument ().getNodeByPath ("/model[1]/group[1]/component_ref[1]/component_ref[3]"));
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 2, 0, 0, false, false, false, false, false, false, false, false, false, false, true, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeConnection1 ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("variable_2=\"i_Na\"", "variable_2=\"i_Pa\"");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, false, false, false, true, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeConnection2 ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			((DocumentNode) doc2.getModel ().getDocumentNode ().getChildrenWithTag ("connection").get (0)).rmChild (((DocumentNode) ((DocumentNode) doc2.getModel ().getDocumentNode ().getChildrenWithTag ("connection").get (0)).getChildrenWithTag ("map_variables").get (1)));
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 3, 0, 0, false, false, false, false, false, false, false, false, false, true, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testDelConnection ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc2.getModel ().getDocumentNode ().rmChild (((DocumentNode) doc2.getModel ().getDocumentNode ().getChildrenWithTag ("connection").get (0)));
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 10, 0, 1, false, false, false, false, false, false, false, false, false, true, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeUnitNameInComponent ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("name=\"someUnit\"", "name=\"someUnit2\"");
//			System.out.println (d);
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, true, false, false, true, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeUnitName ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			String d = XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())).replace ("name=\"unusedUnit\"", "name=\"unusedUnit2\"");
			doc2 = getModel (d);
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

			//System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, true, false, false, true, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
		
		
		
		/**
		 * 
		 */
		@Test
		public void testChangeUnitsInComponents ()
		{
			try
			{
				CellMLDocument doc1 = getValidTestModel ();
				CellMLDocument doc2 = getValidTestModel ();
				((DocumentNode) doc2.getModel ().getComponent ("myThirdStupidComponent").getUnit ("someUnit").getDocumentNode ().getChildren ().get (0)).setAttribute ("multiplier", "5");
				doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
				
				
				CellMLDiff differ = new CellMLDiff (doc1, doc2);
				differ.mapTrees ();
				checkDiff (differ);

//				System.out.println (differ.getDiff ());
				simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, false, false, true, false, false, false, false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
			}
		}
	
	
	/**
	 * 
	 */
	@Test
	public void  testChangeUnits ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			((DocumentNode) doc2.getModel ().getUnits ().getUnit ("inch", null).getDocumentNode ().getChildren ().get (0)).setAttribute ("multiplier", "5");
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, false, false, false, true, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testDelUnitsInComponents ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc2.getModel ().getComponent ("myThirdStupidComponent").getDocumentNode ().rmChild (doc2.getModel ().getComponent ("myThirdStupidComponent").getUnit ("someUnit").getDocumentNode ());
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 6, 0, 0, false, true, false, false, false, true, false, false, true, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testDelUnits ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			doc2.getModel ().getDocumentNode ().rmChild (doc2.getModel ().getUnits ().getUnit ("unusedUnit", null).getDocumentNode ());
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 6, 0, 0, false, false, false, false, false, true, false, false, true, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * Simple check annotations.
	 *
	 * @param differ the differ
	 * @param ins the ins
	 * @param del the del
	 * @param up the up
	 * @param mov the mov
	 * @param changeVariableDef the change variable def
	 * @param changeComponentDef the change component def
	 * @param changeMathModel the change math model
	 * @param changeSpec the change spec
	 * @param changeModelName the change model name
	 * @param changeEntityIdentifier the change entity identifier
	 * @param changeReactionNetwork the change reaction network
	 * @param changeReactionReversibility the change reaction reversibility
	 * @param changeUnits the change units
	 * @param changeVariableConnection the change variable connection
	 * @param changeComponentHierarchy the change component hierarchy
	 * @param changeAnnotation the change annotation
	 * @param changeTextualDescription the change textual description
	 * @throws XmlDocumentParseException the xml document parse exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JDOMException the jDOM exception
	 */
	private void simpleCheckAnnotations (
		CellMLDiff differ,
		int ins, int del, int up, int mov,
		boolean changeVariableDef, boolean changeComponentDef, boolean changeMathModel,
		boolean changeSpec, boolean changeModelName, boolean changeEntityIdentifier,
		boolean changeReactionNetwork, boolean changeReactionReversibility,
		boolean changeUnits, boolean changeVariableConnection, boolean changeComponentHierarchy,
		boolean changeAnnotation, boolean changeTextualDescription
		) throws XmlDocumentParseException, IOException, JDOMException
	{
		// stolen from my logger :)
		StackTraceElement ste =  Thread.currentThread().getStackTrace()[2];
		String pre = ste.getClassName () + "@" + ste.getLineNumber() + ": ";

		Patch patch = differ.getPatch ();
		String diff = differ.getDiff ();
		Document patchDoc = patch.getDocument (false);
		TreeDocument myPatchDoc = new TreeDocument (patchDoc, null);
		String annotations = patch.getAnnotationDocumentXml ();
		TreeDocument annotationsDoc = new TreeDocument (XmlTools.readDocument (annotations), null);
		
		assertNotNull (pre + "diff shouldn't be null", diff);
		assertEquals (pre + "expected exactly " + (ins + del + up + mov) + " changes", 5 + ins + del + up + mov, myPatchDoc.getNumNodes ());
		assertEquals (pre + "expected exactly " + del + " del", del, patch.getNumDeletes ());
		assertEquals (pre + "expected exactly " + ins + " ins", ins, patch.getNumInserts ());
		assertEquals (pre + "expected exactly " + up + " up", up, patch.getNumUpdates ());
		assertEquals (pre + "expected exactly " + mov + " mov", mov, patch.getNumMoves ());
		assertTrue (pre + "there should be some annotation, even if there weren't any changes", annotationsDoc.getNumNodes () > 5);
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#VariableSetup", changeVariableDef, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#VariableSetup"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ComponentDefinition", changeComponentDef, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ComponentDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#MathematicalModelDefinition", changeMathModel, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#MathematicalModelDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ChangedSpecification", changeSpec, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ChangedSpecification"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ModelName", changeModelName, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ModelName"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#EntityIdentifier", changeEntityIdentifier, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#EntityIdentifier"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ReactionNetworkDefinition", changeReactionNetwork, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ReactionNetworkDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ReversibilityDefinition", changeReactionReversibility, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ReversibilityDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#UnitDefinition", changeUnits, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#UnitDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#VariableConnectionDefinition", changeVariableConnection, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#VariableConnectionDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#HierarchyDefinition", changeComponentHierarchy, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#HierarchyDefinition"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#ModelAnnotation", changeAnnotation, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#ModelAnnotation"));
		assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#TextualDescription", changeTextualDescription, annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#TextualDescription"));
//	assertEquals (pre + "occurence of http://purl.uni-rostock.de/comodi/comodi#", , annotations.contains ("http://purl.uni-rostock.de/comodi/comodi#"));
	}
	
	
	/**
	 * 
	 */
	@Test
	public void  testVariableDel ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			// update model -> remove a variable
			doc2.getModel ().getComponent ("myStupidComponent").getDocumentNode ().rmChild (doc2.getModel ().getComponent ("myStupidComponent").getVariable ("myStupidVariable").getDocumentNode ());
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 5, 0, 0, true, true, false, false, false, true, false, false, false, false, false, false, false);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testVariableConcDiffers ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			// update model -> change name of a variable
			doc2.getModel ().getComponent ("membrane").getVariable ("V").getDocumentNode ().setAttribute ("initial_value", "1");
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, true, false, false, false, false, false, false, false, false, false, false, false, false);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testComponentNameDiffers ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			// update model -> change name of a variable
			doc2.getModel ().getComponent ("myStupidComponent").getDocumentNode ().setAttribute ("name", "myNonStupidComponent");
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, true, false, false, false, false, false, false, false);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	
	
	/**
	 * 
	 */
	@Test
	public void  testVariableNameDiffers ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			// update model -> change name of a variable
			doc2.getModel ().getComponent ("myStupidComponent").getVariable ("myStupidVariable").getDocumentNode ().setAttribute ("name", "myNonStupidVariable");
			doc2 = getModel (XmlTools.prettyPrintDocument (DocumentTools.getDoc (doc2.getTreeDocument ())));
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);

//			System.out.println (differ.getDiff ());
			simpleCheckAnnotations (differ, 0, 0, 1, 0, false, false, false, false, false, true, false, false, false, false, false, false, false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}
	
	/**
	 * 
	 */
	@Test
	public void  testModelsEqual ()
	{
		try
		{
			CellMLDocument doc1 = getValidTestModel ();
			CellMLDocument doc2 = getValidTestModel ();
			
			CellMLDiff differ = new CellMLDiff (doc1, doc2);
			differ.mapTrees ();
			checkDiff (differ);
			
			Patch patch = differ.getPatch ();
			
			String diff = differ.getDiff ();
			Document patchDoc = patch.getDocument (false);
			TreeDocument myPatchDoc = new TreeDocument (patchDoc, null);

			simpleCheckAnnotations (differ, 0, 0, 0, 0, false, false, false, false, false, false, false, false, false, false, false, false, false);
			assertNotNull ("diff shouldn't be null", diff);
			assertEquals ("didn't expect any changes", 5, myPatchDoc.getNumNodes ());
			assertTrue ("didn't expect any changes but some annotations", 5 < new TreeDocument (patch.getDocument (true), null).getNumNodes ());
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ("unexpected exception while diffing cellml models: " + e.getMessage ());
		}
	}

	/**
	 * @param diff
	 * @throws ParserConfigurationException
	 */
	public void checkDiff (Diff diff) throws ParserConfigurationException
	{
		try 
		{
			String reactionsGraphMl = diff.getReactionsGraphML ();
			String reactionsDot = diff.getReactionsDotGraph ();
			String reactionsJson = diff.getReactionsJsonGraph ();
			assertTrue ("reactionsGraphMl shouldn't be null", reactionsGraphMl == null || reactionsGraphMl.length () > 10);
			assertTrue ("reactionsDot shouldn't be null", reactionsDot == null || reactionsDot.length () > 10);
			assertTrue ("reactionsJson shouldn't be null", reactionsJson == null || reactionsJson.length () > 10);
	
			String hierarchyGraphml = diff.getHierarchyGraphML ();
			String hierarchyDot = diff.getHierarchyGraphML ();
			String hierarchyJson = diff.getHierarchyGraphML ();
			assertTrue ("hierarchyGraphml shouldn't be null", hierarchyGraphml == null || hierarchyGraphml.length () > 10);
			assertTrue ("hierarchyDot shouldn't be null", hierarchyDot == null || hierarchyDot.length () > 10);
			assertTrue ("hierarchyJson shouldn't be null", hierarchyJson == null || hierarchyJson.length () > 10);
	
			String html = diff.getHTMLReport ();
			String md = diff.getMarkDownReport ();
			String rst = diff.getReStructuredTextReport ();
			assertNotNull ("html shouldn't be null", html);
			assertNotNull ("md shouldn't be null", md);
			assertNotNull ("rst shouldn't be null", rst);
		}
		catch (Exception e)
		{
			fail ("unexpected exception " + e);
		}
	}
	
	
}
