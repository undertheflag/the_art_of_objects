//*************************************************************************
/**
 * CatalogImpl.java - implementation for the CatalogIF interface in pdm.idl
 *
 *   Copyright (C) 1998-2000 	Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import pdm.*;  // from pdm.idl

/** 
 * Implementation for CatalogIF.  It uses Catalog from the persistent layer as
 * a servant class.
 */
class CatalogImpl implements CatalogIFOperations {

  /** the servant persistent object */
  private Catalog catalog;

  /**
   * Constructs a CatalogImpl object from a persistent catalog object.
   *
   * @param     catalog   the persistent object
   */
  public CatalogImpl(Catalog catalog) { 
    this.catalog = catalog;
  }

  /**
   * Returns the persistent object.
   *
   * @return     catalog   the persistent object
   */
  public Catalog getCatalog() { 
    return catalog;
  }

  /** 
   * Returns the name of the catalog. 
   *
   * @return the name of the catalog
   */
  public String getName() {
    String name = null;

    // Commands below may involve transaction.  We put them in
    // a synchronized block to make threads cooperating with 
    // each other.
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	name = catalog.getName();
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return name;
  }

  /** Returns the number of parts in the catalog. */
  public int getPartCount() {
    int count = 0;
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	count = catalog.getPartCount();

        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return count;
  }

  /** Returns an array of part interfaces. */
  public PartIF[] getParts() {
    PartIF[] partIFs = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part[] parts = catalog.getParts();
	partIFs = new PartIF[parts.length];
	for (int i=0; i<parts.length; i++) {
  	  partIFs[i] = LayerMediator.getPartIF(parts[i]);
        }
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return partIFs;
  }

  /** 
   * Returns an array of PartData.
   * This is a deep get.
   */
  public PartData[] getPartDatas() {
    PartData[] pDatas = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();

	Part[] parts = catalog.getParts();
	pDatas = new PartData[parts.length];

	for (int i=0; i<parts.length; i++) {
  	  pDatas[i] = PartImpl.toPartData(parts[i]);
        }

        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return pDatas;
  }

  /**
   * Returns the PartIF object with the input name.
   *
   * @param partName 	Name of the part
   * @return PartIF object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public PartIF getPart(String partName) throws PdmError {
    PartIF partIF = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part part = catalog.getPart(partName);
	partIF = LayerMediator.getPartIF(part);
        DBManager.commitTrx();
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return partIF;
  }

  /**
   * Returns the PartData object with the input name.
   * This is a deep get.
   *
   * @param partName 	Name of the part
   * @return PartData object.  Null if none matched.
   *
   * @exception PdmError never thrown
   */
  public PartData getPartData(String partName) throws PdmError {
    PartData partData = null;

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part part = catalog.getPart(partName);
	if (part == null) return null;

	partData = PartImpl.toPartData(part);

        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return partData;
  }

  /**
   * Returns whether the catalog contains the input part.
   *
   * @param part 	the part interface object
   * @return true if the catalog contains the input part.  False otherwise.
   */
  public boolean contains(PartIF partIF) {
    boolean result = false;
    
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginReadTrx();
	Part part = LayerMediator.getPart(partIF);
	result = catalog.contains(part);
	DBManager.commitTrx();
	
      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
    return result;
  }

  /** 
   * Sets the name of the catalog. 
   *
   * @param name	the name of the catalog
   */
  public void setName(String name) {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	catalog.setName(name);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }

  /**
   * Adds a new Part using the input data, 
   * which include PartData and AttributeData arrays.
   * <P>
   * We use the recursive operation in PartImpl to do
   * the job (createPart).
   * <P>
   * <I>This is a deep operation if the input arrays are not empty.</I>
   *
   * @param name	Name of the part
   * @param attributes	Attributes of part as AttributeData[]
   * @param components  Components of part as PartData[]
   * @return the new PartIF object
   *
   * @exception PdmError if there is transaction error
   */
  public PartIF addPart(String name, AttributeData[] attributes, 
  			PartData[] components)  throws PdmError {
    PartIF partIF = null;

    if (attributes == null) attributes = new AttributeData[0];
    if (components == null) components = new PartData[0];

    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();

	// use the static operation in PartImpl
	Part part = PartImpl.createPart(name, attributes, components);

	partIF = LayerMediator.getPartIF(part);
	catalog.addPart(part);

        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
    return partIF;
  }

  /**
   * Remove the Part with the input name form the catalog.
   *
   * @param partName  The name of the part to be removed.
   * @exception PdmError if the part is not found or transaction error occurs
   */
  public void removePartByName(String partName) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	catalog.removePart(partName);
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the input part from the catalog.
   *
   * @param part  The part interface object
   * @exception PdmError if the part is not found or transaction error occurs
   */
  public void removePart(PartIF partIF) throws PdmError {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	try {
  	  catalog.removePart(LayerMediator.getPart(partIF));
	} catch (Exception e1) {
	  throw new PdmError(e1.getMessage());
	}
        DBManager.commitTrx();

      } catch (Exception e) {
        DBManager.handleTrxError(e);
	throw new PdmError(e.getMessage());
      }
    }
  }

  /**
   * Remove the all the parts.
   */
  public void removeAllParts() {
    synchronized (DBManager.TOKEN) {
      try {
        DBManager.beginUpdateTrx();
	catalog.removeAllParts();
        DBManager.commitTrx();

      } catch (Exception e) {

        DBManager.handleTrxError(e);
	show(e.getMessage());
      }
    }
  }


  /** 
   * Shows the input string.
   *
   * @s the string to be shown
   */
  public static void show(String s) { 
    System.out.println(s);
  }

}

