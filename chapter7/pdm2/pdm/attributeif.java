package pdm;
public interface AttributeIF extends com.inprise.vbroker.CORBA.Object {
  public java.lang.String getName();
  public double getValue();
  public java.lang.String getUnit();
  public void setName(
    java.lang.String name
  );
  public void setValue(
    double value
  );
  public void setUnit(
    java.lang.String unit
  );
}
