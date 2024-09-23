//*************************************************************************
/*
 * LayerMediator.java - a utility to mediate objects between two layers
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 *
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

import java.util.Hashtable;

/**
 * This class provides static methods to convert interface objects to
 * persistent objects, and vice versa.
 */
public class LayerMediator {

  /** 
   * A hashtable for storing (persistent_object, interface_object) pairs.
   * The key is persistent_object.
   * <P>
   * For now the interface_objects are not deleted.  Since more than one
   * clients may be using the objects, a good strategy is needed to 
   * manage the life cycle of these objects.
   */
   private static Hashtable objectPairs = new Hashtable();

  /**
   * Returns the persistent object linked to the interface object.
   *
   * @param     catalogIF  the interface (TIE) object
   * @return    the associated persistent object
   */
  public static Catalog getCatalog(CatalogIF catalogIF) {
    if (catalogIF == null) return null;
    CatalogImpl catalogImpl = 
      (CatalogImpl) ((_tie_CatalogIF) catalogIF)._delegate();
    return catalogImpl.getCatalog();
  }

  /**
   * Returns an the interface object for the persistent object.
   * It creates the interface object if not found in the pool.
   *
   * @param     catalog  the persistent object
   * @return    a CatalogIF object
   */
  public static CatalogIF getCatalogIF(Catalog catalog) throws Exception {
    if (catalog == null) return null;

    // try to find it
    CatalogIF catalogIF = (CatalogIF) objectPairs.get(catalog);

    if (catalogIF == null) {  // then create it
      catalogIF = new _tie_CatalogIF(new CatalogImpl(catalog));
      objectPairs.put(catalog, catalogIF);
    }
    return catalogIF;
  }


  /**
   * Returns the persistent object linked to the interface object.
   *
   * @param     partIF  the interface (TIE) object
   * @return    the associated persistent object
   */
  public static Part getPart(PartIF partIF) {
    if (partIF == null) return null;
    PartImpl partImpl = 
      (PartImpl) ((_tie_PartIF) partIF)._delegate();
    return partImpl.getPart();
  }

  /**
   * Returns an the interface object for the persistent object.
   * It creates the interface object if not found in the pool.
   *
   * @param     part  the persistent object
   * @return    a PartIF object
   */
  public static PartIF getPartIF(Part part) throws Exception {
    if (part == null) return null;

    // try to find it
    PartIF partIF = (PartIF) objectPairs.get(part);

    if (partIF == null) {  // then create it
      partIF = new _tie_PartIF(new PartImpl(part));
      objectPairs.put(part, partIF);
    }
    return partIF;
  }


  /**
   * Returns the persistent object linked to the interface object.
   *
   * @param     attributeIF  the interface (TIE) object
   * @return    the associated persistent object
   */
  public static Attribute getAttribute(AttributeIF attributeIF) {
    if (attributeIF == null) return null;
    AttributeImpl attributeImpl = 
      (AttributeImpl) ((_tie_AttributeIF) attributeIF)._delegate();
    return attributeImpl.getAttribute();
  }

  /**
   * Returns an the interface object for the persistent object.
   * It creates the interface object if not found in the pool.
   *
   * @param     attribute  the persistent object
   * @return    an AttributeIF object
   */
  public static AttributeIF getAttributeIF(Attribute attribute)
  	throws Exception {
    if (attribute == null) return null;

    // try to find it
    AttributeIF attributeIF = (AttributeIF) objectPairs.get(attribute);

    if (attributeIF == null) {  // then create it
      attributeIF = new _tie_AttributeIF(new AttributeImpl(attribute));
      objectPairs.put(attribute, attributeIF);
    }
    return attributeIF;
  }

}
