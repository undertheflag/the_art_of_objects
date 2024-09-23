package pdm;
public interface CatalogIF extends com.inprise.vbroker.CORBA.Object {
  public java.lang.String getName();
  public pdm.Part[] getParts();
  public int getPartCount();
  public pdm.Part getPart(
    java.lang.String partName
  ) throws
    pdm.PdmError;
  public boolean contains(
    pdm.Part part
  );
  public pdm.Part addPart(
    java.lang.String partName,
    pdm.Attribute[] attributes,
    pdm.Part[] components
  ) throws
    pdm.PdmError;
  public void removePartByName(
    java.lang.String partName
  ) throws
    pdm.PdmError;
  public void removePart(
    pdm.Part part
  ) throws
    pdm.PdmError;
  public void removeAllParts();
}
