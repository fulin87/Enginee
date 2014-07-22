
package com.engineer.webservice.client.ref;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


/**
 * This class was generated by the JAXWS SI.
 * JAX-WS RI 2.0_02-b08-fcs
 * Generated source version: 2.0
 * 
 */
@WebService(name = "Example", targetNamespace = "http://www.morly.com/wstest")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface Example {


    /**
     * 
     * @param userName
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "toSayHello")
    @WebResult(name = "returnWord", partName = "returnWord")
    public String toSayHello(
        @WebParam(name = "userName", partName = "userName")
        String userName);

    /**
     * 
     * @param person
     * @param arg1
     * @return
     *     returns com.engineer.webservice.client.ref.PersonArray
     * @throws HelloException_Exception
     */
    @WebMethod(action = "sayHello")
    @WebResult(name = "personList", partName = "personList")
    public PersonArray sayHello(
        @WebParam(name = "person", partName = "person")
        Person person,
        @WebParam(name = "arg1", partName = "arg1")
        String arg1)
        throws HelloException_Exception
    ;

}