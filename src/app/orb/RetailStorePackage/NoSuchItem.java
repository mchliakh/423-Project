package app.orb.RetailStorePackage;


/**
* a2/orb/RetailStorePackage/NoSuchItem.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/Misha/workspace/423/drs.idl
* Sunday, November 4, 2012 8:25:00 PM EST
*/

public final class NoSuchItem extends org.omg.CORBA.UserException
{

  public NoSuchItem ()
  {
    super(NoSuchItemHelper.id());
  } // ctor


  public NoSuchItem (String $reason)
  {
    super(NoSuchItemHelper.id() + "  " + $reason);
  } // ctor

} // class NoSuchItem
