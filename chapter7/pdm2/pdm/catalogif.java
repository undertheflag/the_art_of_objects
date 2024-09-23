package pdm;
public interface CatalogIF extends com.inprise.vbroker.CORBA.Object {
  public java.lang.String getName();
  public int getPartCount();
  public pdm.PartIF[] getParts();
  public pdm.PartData[] getPartDatas();
  public pdm.PartIF getPart(
    java.lang.String partName
  ) throws
    pdm.PdmError;
  public pdm.PartData getPartData(
    java.lang.String partName
  ) throws
    pdm.PdmError;
  public boolean contains(
    pdm.PartIF part
  );
  public void setName(
    java.lang.String name
  );
  public pdm.PartIF addPart(
    java.lang.String partName,
    pdm.AttributeData[] attributes,
    pdm.PartData[] components
  ) throws
    pdm.PdmError;
  public void removePartByName(
    java.lang.String partName
  ) throws
    pdm.PdmError;
  public void removePart(
    pdm.PartIF partIF
  ) throws
    pdm.PdmError;
  public void removeAllParts();
}
