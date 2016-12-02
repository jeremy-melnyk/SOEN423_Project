package ServerInterfaceIDL;

/** 
 * Helper class for : ServerIDL
 *  
 * @author OpenORB Compiler
 */ 
public class ServerIDLHelper
{
    /**
     * Insert ServerIDL into an any
     * @param a an any
     * @param t ServerIDL value
     */
    public static void insert(org.omg.CORBA.Any a, ServerInterfaceIDL.ServerIDL t)
    {
        a.insert_Object(t , type());
    }

    /**
     * Extract ServerIDL from an any
     *
     * @param a an any
     * @return the extracted ServerIDL value
     */
    public static ServerInterfaceIDL.ServerIDL extract( org.omg.CORBA.Any a )
    {
        if ( !a.type().equivalent( type() ) )
        {
            throw new org.omg.CORBA.MARSHAL();
        }
        try
        {
            return ServerInterfaceIDL.ServerIDLHelper.narrow( a.extract_Object() );
        }
        catch ( final org.omg.CORBA.BAD_PARAM e )
        {
            throw new org.omg.CORBA.MARSHAL(e.getMessage());
        }
    }

    //
    // Internal TypeCode value
    //
    private static org.omg.CORBA.TypeCode _tc = null;

    /**
     * Return the ServerIDL TypeCode
     * @return a TypeCode
     */
    public static org.omg.CORBA.TypeCode type()
    {
        if (_tc == null) {
            org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init();
            _tc = orb.create_interface_tc( id(), "ServerIDL" );
        }
        return _tc;
    }

    /**
     * Return the ServerIDL IDL ID
     * @return an ID
     */
    public static String id()
    {
        return _id;
    }

    private final static String _id = "IDL:ServerInterfaceIDL/ServerIDL:1.0";

    /**
     * Read ServerIDL from a marshalled stream
     * @param istream the input stream
     * @return the readed ServerIDL value
     */
    public static ServerInterfaceIDL.ServerIDL read(org.omg.CORBA.portable.InputStream istream)
    {
        return(ServerInterfaceIDL.ServerIDL)istream.read_Object(ServerInterfaceIDL._ServerIDLStub.class);
    }

    /**
     * Write ServerIDL into a marshalled stream
     * @param ostream the output stream
     * @param value ServerIDL value
     */
    public static void write(org.omg.CORBA.portable.OutputStream ostream, ServerInterfaceIDL.ServerIDL value)
    {
        ostream.write_Object((org.omg.CORBA.portable.ObjectImpl)value);
    }

    /**
     * Narrow CORBA::Object to ServerIDL
     * @param obj the CORBA Object
     * @return ServerIDL Object
     */
    public static ServerIDL narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof ServerIDL)
            return (ServerIDL)obj;

        if (obj._is_a(id()))
        {
            _ServerIDLStub stub = new _ServerIDLStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
            return stub;
        }

        throw new org.omg.CORBA.BAD_PARAM();
    }

    /**
     * Unchecked Narrow CORBA::Object to ServerIDL
     * @param obj the CORBA Object
     * @return ServerIDL Object
     */
    public static ServerIDL unchecked_narrow(org.omg.CORBA.Object obj)
    {
        if (obj == null)
            return null;
        if (obj instanceof ServerIDL)
            return (ServerIDL)obj;

        _ServerIDLStub stub = new _ServerIDLStub();
        stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate());
        return stub;

    }

}
