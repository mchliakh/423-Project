package app.orb;

/**
* a2/orb/RetailStoreHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/Misha/workspace/423/drs.idl
* Sunday, November 4, 2012 8:25:00 PM EST
*/

public final class RetailStoreHolder implements org.omg.CORBA.portable.Streamable
{
  public app.orb.RetailStore value = null;

  public RetailStoreHolder ()
  {
  }

  public RetailStoreHolder (app.orb.RetailStore initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = app.orb.RetailStoreHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    app.orb.RetailStoreHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return app.orb.RetailStoreHelper.type ();
  }

}