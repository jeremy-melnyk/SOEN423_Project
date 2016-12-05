package tam_replica.ServerInterfaceIDL;

/**
 * Holder class for : ServerIDL
 * 
 * @author OpenORB Compiler
 */
final public class ServerIDLHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal ServerIDL value
     */
    public tam_replica.ServerInterfaceIDL.ServerIDL value;

    /**
     * Default constructor
     */
    public ServerIDLHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public ServerIDLHolder(tam_replica.ServerInterfaceIDL.ServerIDL initial)
    {
        value = initial;
    }

    /**
     * Read ServerIDL from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = ServerIDLHelper.read(istream);
    }

    /**
     * Write ServerIDL into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        ServerIDLHelper.write(ostream,value);
    }

    /**
     * Return the ServerIDL TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return ServerIDLHelper.type();
    }

}
