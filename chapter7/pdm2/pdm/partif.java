package pdm;
public interface PartIF extends com.inprise.vbroker.CORBA.Object {
  public java.lang.String getName();
  public void setName(
    java.lang.String name
  );
  public int getComponentCount();
  public pdm.PartIF[] getComponents();
  public pdm.PartData[] getComponentDatas();
  public pdm.PartIF getComponent(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public pdm.PartData getComponentData(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public boolean containsComponent(
    pdm.PartIF component
  );
  public pdm.PartIF addComponent(
    java.lang.String name,
    pdm.AttributeData[] attributes,
    pdm.PartData[] components
  ) throws
    pdm.PdmError;
  public void removeComponentByName(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public void removeComponent(
    pdm.PartIF component
  ) throws
    pdm.PdmError;
  public void removeAllComponents();
  public int getAttributeCount();
  public pdm.AttributeIF[] getAttributes();
  public pdm.AttributeData[] getAttributeDatas();
  public pdm.AttributeIF getAttribute(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public pdm.AttributeData getAttributeData(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public boolean containsAttribute(
    pdm.AttributeIF attr
  );
  public pdm.AttributeIF addAttribute(
    java.lang.String name,
    double value,
    java.lang.String unit
  ) throws
    pdm.PdmError;
  public void removeAttributeByName(
    java.lang.String name
  ) throws
    pdm.PdmError;
  public void removeAttribute(
    pdm.AttributeIF attributeIF
  ) throws
    pdm.PdmError;
  public void removeAllAttributes();
}
