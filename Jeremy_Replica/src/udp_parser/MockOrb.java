package udp_parser;

import java.applet.Applet;
import java.util.Properties;

import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.portable.OutputStream;

public class MockOrb extends ORB {

	@Override
	public TypeCode create_alias_tc(String arg0, String arg1, TypeCode arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Any create_any() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_array_tc(int arg0, TypeCode arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContextList create_context_list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_enum_tc(String arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Environment create_environment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExceptionList create_exception_list() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_exception_tc(String arg0, String arg1, StructMember[] arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_interface_tc(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NVList create_list(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamedValue create_named_value(String arg0, Any arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream create_output_stream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_recursive_sequence_tc(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_sequence_tc(int arg0, TypeCode arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_string_tc(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_struct_tc(String arg0, String arg1, StructMember[] arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_union_tc(String arg0, String arg1, TypeCode arg2, UnionMember[] arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode create_wstring_tc(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context get_default_context() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Request get_next_response() throws WrongTransaction {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeCode get_primitive_tc(TCKind arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] list_initial_services() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String object_to_string(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean poll_next_response() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object resolve_initial_references(String arg0) throws InvalidName {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void send_multiple_requests_deferred(Request[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void send_multiple_requests_oneway(Request[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void set_parameters(String[] arg0, Properties arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void set_parameters(Applet arg0, Properties arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object string_to_object(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
