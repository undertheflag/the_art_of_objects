package factory;
public interface ServiceFactory extends com.inprise.vbroker.CORBA.Object {
  public factory.Service create();
  public factory.Service find(
    java.lang.String name
  ) throws
    factory.FactoryError;
  public void remove(
    java.lang.String serviceName
  ) throws
    factory.FactoryError;
  public java.lang.String showAll();
}
