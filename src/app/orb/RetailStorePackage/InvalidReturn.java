package app.orb.RetailStorePackage;


/**
* a2/orb/RetailStorePackage/InvalidReturn.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/Misha/workspace/423/drs.idl
* Sunday, November 4, 2012 8:25:00 PM EST
*/

public final class InvalidReturn extends org.omg.CORBA.UserException
{

  public InvalidReturn ()
  {
    super(InvalidReturnHelper.id());
  } // ctor


  public InvalidReturn (String $reason)
  {
    super(InvalidReturnHelper.id() + "  " + $reason);
  } // ctor

} // class InvalidReturn
