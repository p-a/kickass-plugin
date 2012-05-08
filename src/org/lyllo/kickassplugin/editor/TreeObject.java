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

import java.util.ArrayList;

import org.lyllo.kickassplugin.Constants;


/**
 * An default TreeObject.
 * 
 * @author Andy Reek
 * @since 13.02.2006
 */
public class TreeObject {

  private Object parent = null;

  private Object data = null;

  private ArrayList<Object> children = new ArrayList<Object>();

  private String name;

  private int type = Constants.TREEOBJECT_TYPE_NULL;

  /**
   * Construct an TreeObject with a given name.
   * 
   * @param name The name.
   */
  public TreeObject(String name) {
    this.name = name;
  }

  /**
   * Construct an TreeObject with a given name.
   * 
   * @param name The name.
   * @param type A type of a TreeObject.
   */
  public TreeObject(String name, int type) {
    this.name = name;
    this.type = type;
  }

  /**
   * Returns the parent object of the TreeObject.
   * 
   * @return The parent.
   */
  public Object getParent() {
    return parent;
  }

  /**
   * Set a new parent.
   * 
   * @param parent The new parent.
   */
  public void setParent(Object parent) {
    this.parent = parent;
  }

  /**
   * Get all Children of the TreeObject.
   * 
   * @return The children.
   */
  public Object[] getChildren() {
    return children.toArray(new Object[0]);
  }

  /**
   * Set new Children.
   * 
   * @param children The new children.
   */
  public void setChildren(Object[] children) {
    this.children.clear();

    if (children != null) {
      for (Object child : children) {
        if (child instanceof TreeObject) {
          this.children.add(child);
        }
      }
    }
  }

  /**
   * Gets a child at the given position.
   * 
   * @param position The position.
   * 
   * @return The child or null if not available.
   */
  public Object getChild(int position) {
    return children.get(position);
  }

  /**
   * Adds a new Child.
   * 
   * @param child The new child.
   */
  public void addChild(Object child) {
    if (child instanceof TreeObject) {
      children.add(child);
    }
  }

  /**
   * Removes a given child.
   * 
   * @param child The child to be removed.
   */
  public void removeChild(Object child) {
    children.remove(child);
  }

  /**
   * Removes a child a t the given position.
   * 
   * @param position The position.
   */
  public void removeChild(int position) {
    children.remove(position);
  }

  /**
   * Gets the data from the child.
   * 
   * @return The data.
   */
  public Object getData() {
    return data;
  }

  /**
   * Sets data of the TreeObejct.
   * 
   * @param data The data.
   */
  public void setData(Object data) {
    this.data = data;
  }

  /**
   * {@inheritDoc}
   */
  public String toString() {
    return name;
  }

  /**
   * Gets the type of the TreeObject
   * 
   * @return The type
   */
  public int getType() {
    return type;
  }

  /**
   * Sets a new type.
   * 
   * @param type The new type.
   */
  public void setType(int type) {
    this.type = type;
  }

  /**
   * {@inheritDoc}
   */
  public boolean equals(Object treeobj) {
    if (!(treeobj instanceof TreeObject)) {
      return false;
    }

    return this.name.equalsIgnoreCase(treeobj.toString());
  }
}
