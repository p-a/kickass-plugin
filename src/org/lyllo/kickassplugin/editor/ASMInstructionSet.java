/*
 Kick Assembler plugin - An Eclipse plugin for convenient Kick Assembling
 Copyright (c) 2012 - P-a Backstrom <pa.backstrom@gmail.com>
 
 Based on ASMPlugin - http://sourceforge.net/projects/asmplugin/
 Copyright (c) 2006 - Andy Reek, D. Mitte
 
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/ 
package org.lyllo.kickassplugin.editor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.Status;
import org.lyllo.kickassplugin.Activator;
import org.lyllo.kickassplugin.Constants;
import org.lyllo.kickassplugin.Messages;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Loads an manages the instructions, which are uses in the editor.
 * 
 * @author Daniel Mitte
 * @since 24.02.2006
 */
public final class ASMInstructionSet {
  private static HashMap<String, String> instructionMap = null;

  private static HashMap<String, String> segmentMap = null;

  private static HashMap<String, String> functionMap = null;

  private static HashMap<String, String> constantMap = null;

  private static HashMap<String, String> classMap = null;

  private static String[][] sortedInstructionArray = null;

  private static String[][] sortedSegmentArray = null;
  
  private static String[][] sortedFunctionArray = null;

  private static String[][] sortedConstantArray = null;

  private static String[][] sortedClassArray = null;

  /**
   * Must not be instantiated.
   */
  private ASMInstructionSet() {
    // Must not be instantiated.
  }

  /**
   * Returns all instructions.
   * 
   * @return The instructions.
   */
  public static HashMap<String, String> getInstructions() {
    if (instructionMap == null) {
      loadXMLData();
    }

    return instructionMap;
  }

  /**
   * Returns all segments.
   * 
   * @return The segments.
   */
  public static HashMap<String, String> getSegments() {
    if (segmentMap == null) {
      loadXMLData();
    }

    return segmentMap;
  }
  
  public static HashMap<String, String> getFunctions() {
	    if (functionMap == null) {
	      loadXMLData();
	    }

	    return functionMap;
	  }

  public static HashMap<String, String> getClasses() {
	    if (classMap == null) {
	      loadXMLData();
	    }

	    return classMap;
	  }
  
  public static HashMap<String, String> getConstants() {
	    if (constantMap == null) {
	      loadXMLData();
	    }

	    return constantMap;
	  }
  
  /**
   * Returns the Array with the instructions.
   * 
   * @return The Instructions.
   */
  public static String[][] getInstructionArray() {
    if (sortedInstructionArray == null) {
      loadXMLData();
    }

    return sortedInstructionArray;
  }

  /**
   * Returns the Array with the segments.
   * 
   * @return The segments.
   */
  public static String[][] getSegmentArray() {
    if (sortedSegmentArray == null) {
      loadXMLData();
    }

    return sortedSegmentArray;
  }
  
  public static String[][] getFunctionArray() {
	    if (sortedFunctionArray == null) {
	      loadXMLData();
	    }

	    return sortedFunctionArray;
	  }
  public static String[][] getConstantArray() {
	  if (sortedConstantArray == null) {
		  loadXMLData();
	  }
	  
	  return sortedConstantArray;
  }
  public static String[][] getClassArray() {
	  if (sortedClassArray == null) {
		  loadXMLData();
	  }
	  
	  return sortedClassArray;
  }

  /**
   * Loads the instructions.
   */
  private static void loadXMLData() {
     instructionMap = clearMap(instructionMap);
     segmentMap = clearMap(segmentMap);
     functionMap = clearMap(functionMap);
     constantMap = clearMap(constantMap);
     classMap = clearMap(classMap);
    
    
    String xmlfile = Activator.getFilePathFromPlugin("asm_instruction_set.xml");

    try {
      SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
      parser.parse(new File(xmlfile), new DefaultHandler() {
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
          if (qName.equals("instruction")) {
            instructionMap.put(attributes.getValue("command"), attributes.getValue("description"));
          } else if (qName.equals("segment")) {
            segmentMap.put(attributes.getValue("field"), attributes.getValue("description"));
          } else if (qName.equals("function")) {
        	  functionMap.put(attributes.getValue("field"), attributes.getValue("description"));
          } else if (qName.equals("constant")) {
        	  constantMap.put(attributes.getValue("field"), attributes.getValue("description"));
          } else if (qName.equals("class")) {
        	  classMap.put(attributes.getValue("field"), attributes.getValue("description"));
          }
        }
      });
    } catch (Exception e) {
      Activator.getDefault().getLog().log(
                                          new Status(Status.ERROR, Constants.PLUGIN_ID, Status.OK,
                                                     Messages.LOAD_ASMISET_ERROR, e));
    }

    sortedInstructionArray = new String[instructionMap.size()][3];
    sortedSegmentArray = new String[segmentMap.size()][3];
    sortedFunctionArray = new String[functionMap.size()][3];
    sortedConstantArray = new String[constantMap.size()][3];
    sortedClassArray = new String[classMap.size()][3];

	makeSortedArray(instructionMap, sortedInstructionArray);
	makeSortedArray(segmentMap, sortedSegmentArray);
	makeSortedArray(functionMap, sortedFunctionArray);
	makeSortedArray(constantMap, sortedConstantArray);
	makeSortedArray(classMap, sortedClassArray);
    
  }

private static void makeSortedArray(HashMap<String, String> map, String[][]array) {
	Vector<String> sortVector;
	sortVector = new Vector<String>(map.keySet());
    Collections.sort(sortVector);
    int pos = 0;

    for (String element : sortVector) {
      array[pos][0] = new String(element);
      array[pos][1] = new String(element.toLowerCase());
      array[pos][2] = new String((String) map.get(element));
      pos++;
    }
}

private static HashMap<String, String> clearMap(HashMap<String, String> map) {
	if (map == null) {
      map = new HashMap<String, String>();
    } else {
      map.clear();
    }
	return map;
}
}
