package app.orb.RetailStorePackage;


/**
* a2/orb/RetailStorePackage/InsufficientQuantity.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/Misha/workspace/423/drs.idl
* Sunday, November 4, 2012 8:25:00 PM EST
*/

public final class InsufficientQuantity extends org.omg.CORBA.UserException
{

  public InsufficientQuantity ()
  {
    super(InsufficientQuantityHelper.id());
  } // ctor


  public InsufficientQuantity (String $reason)
  {
    super(InsufficientQuantityHelper.id() + "  " + $reason);
  } // ctor

} // class InsufficientQuantity
