package pdm;
final public class PartData {
  public java.lang.String name;
  public pdm.AttributeData[] attributes;
  public pdm.PartData[] components;
  public PartData() {
  }
  public PartData(
    java.lang.String name,
    pdm.AttributeData[] attributes,
    pdm.PartData[] components
  ) {
    this.name = name;
    this.attributes = attributes;
    this.components = components;
  }
}
