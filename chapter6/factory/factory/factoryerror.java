package factory;
final public class FactoryError extends org.omg.CORBA.UserException {
  public java.lang.String message;
  public FactoryError() {
  }
  public FactoryError(
    java.lang.String message
  ) {
    this.message = message;
  }
}
