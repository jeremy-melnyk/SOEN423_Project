package tam_replica.ServerInterfaceIDL;

/**
 * Interface definition: ServerIDL.
 * 
 * @author OpenORB Compiler
 */
public class _ServerIDLStub extends org.omg.CORBA.portable.ObjectImpl
        implements ServerIDL
{
    static final String[] _ids_list =
    {
        "IDL:ServerInterfaceIDL/ServerIDL:1.0"
    };

    public String[] _ids()
    {
     return _ids_list;
    }

    private final static Class _opsClass = tam_replica.ServerInterfaceIDL.ServerIDLOperations.class;

    /**
     * Operation bookFlight
     */
    public String bookFlight(String firstName, String lastName, String Address, String phone, String destination, String date, String flightclass)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("bookFlight",true);
                    _output.write_string(firstName);
                    _output.write_string(lastName);
                    _output.write_string(Address);
                    _output.write_string(phone);
                    _output.write_string(destination);
                    _output.write_string(date);
                    _output.write_string(flightclass);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("bookFlight",_opsClass);
                if (_so == null)
                   continue;
                tam_replica.ServerInterfaceIDL.ServerIDLOperations _self = (tam_replica.ServerInterfaceIDL.ServerIDLOperations) _so.servant;
                try
                {
                    return _self.bookFlight( firstName,  lastName,  Address,  phone,  destination,  date,  flightclass);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation getBookedFlightCount
     */
    public String getBookedFlightCount(String recordtype)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("getBookedFlightCount",true);
                    _output.write_string(recordtype);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("getBookedFlightCount",_opsClass);
                if (_so == null)
                   continue;
                tam_replica.ServerInterfaceIDL.ServerIDLOperations _self = (tam_replica.ServerInterfaceIDL.ServerIDLOperations) _so.servant;
                try
                {
                    return _self.getBookedFlightCount( recordtype);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation editFlightRecord
     */
    public String editFlightRecord(String recordID, String fieldname, String newValue)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("editFlightRecord",true);
                    _output.write_string(recordID);
                    _output.write_string(fieldname);
                    _output.write_string(newValue);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("editFlightRecord",_opsClass);
                if (_so == null)
                   continue;
                tam_replica.ServerInterfaceIDL.ServerIDLOperations _self = (tam_replica.ServerInterfaceIDL.ServerIDLOperations) _so.servant;
                try
                {
                    return _self.editFlightRecord( recordID,  fieldname,  newValue);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

    /**
     * Operation transferReservation
     */
    public String transferReservation(String PassengerID, String currentcity, String newcity)
    {
        while(true)
        {
            if (!this._is_local())
            {
                org.omg.CORBA.portable.InputStream _input = null;
                try
                {
                    org.omg.CORBA.portable.OutputStream _output = this._request("transferReservation",true);
                    _output.write_string(PassengerID);
                    _output.write_string(currentcity);
                    _output.write_string(newcity);
                    _input = this._invoke(_output);
                    String _arg_ret = _input.read_string();
                    return _arg_ret;
                }
                catch(org.omg.CORBA.portable.RemarshalException _exception)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _exception)
                {
                    String _exception_id = _exception.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: "+ _exception_id);
                }
                finally
                {
                    this._releaseReply(_input);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("transferReservation",_opsClass);
                if (_so == null)
                   continue;
                tam_replica.ServerInterfaceIDL.ServerIDLOperations _self = (tam_replica.ServerInterfaceIDL.ServerIDLOperations) _so.servant;
                try
                {
                    return _self.transferReservation( PassengerID,  currentcity,  newcity);
                }
                finally
                {
                    _servant_postinvoke(_so);
                }
            }
        }
    }

}
