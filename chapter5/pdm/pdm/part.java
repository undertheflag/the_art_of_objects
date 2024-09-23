package pdm;
final public class Part {
  public java.lang.String name;
  public pdm.Attribute[] attributes;
  public pdm.Part[] components;
  public Part() {
  }
  public Part(
    java.lang.String name,
    pdm.Attribute[] attributes,
    pdm.Part[] components
  ) {
    this.name = name;
    this.attributes = attributes;
    this.components = components;
  }
}
