package factory;
final public class ServiceError extends org.omg.CORBA.UserException {
  public java.lang.String message;
  public ServiceError() {
  }
  public ServiceError(
    java.lang.String message
  ) {
    this.message = message;
  }
}
