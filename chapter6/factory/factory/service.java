package factory;
public interface Service extends com.inprise.vbroker.CORBA.Object {
  public java.lang.String getName();
  public org.omg.CORBA.Any performService(
    org.omg.CORBA.Any a
  ) throws
    factory.ServiceError;
}
