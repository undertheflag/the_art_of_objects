package pdm;
final public class PdmError extends org.omg.CORBA.UserException {
  public java.lang.String message;
  public PdmError() {
  }
  public PdmError(
    java.lang.String message
  ) {
    this.message = message;
  }
}
