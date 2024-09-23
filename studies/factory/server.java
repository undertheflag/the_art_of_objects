//*************************************************************************
/**
 * Server.java - the factory server implementation
 *
 *   Copyright (C) 1998-2000    Yun-Tung Lau
 *   All Rights Reserved.  The contents of this file are proprietary to
 *   the above copyright holder.
 */
//*************************************************************************

import java.util.*;
import java.io.*;
import java.net.*;
import factory.*;  // from factory.idl

/** Implementation for Service interface.
 */
abstract class ServiceImpl implements ServiceOperations {

  private String mName;

  /** Constructor, which sets the name. */
  public ServiceImpl(String name) {
    mName = name;
  }

  public String getName() {
    return mName;
  }

}


/** Specific implementation for Service interface.
 */
class URLServiceImpl extends ServiceImpl {

  /** Constructor, which sets the name. */
  URLServiceImpl(String name) {
    super(name);
  }

  /** Implements interface operations. */
  public org.omg.CORBA.Any performService(org.omg.CORBA.Any a)
    throws ServiceError {

    String s = "";

    try {
      // connect to the url and read the content
      URL url = new URL(a.extract_string());
      URLConnection uc = url.openConnection();
      BufferedReader in = new BufferedReader(
        new InputStreamReader(uc.getInputStream()) );
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        s += "\n" + inputLine;
      }
      in.close();

    } catch (Exception e) {
      throw new ServiceError(" " + e);
    }

    // create a new any variable for return
    org.omg.CORBA.Any a1 = Server.orb.create_any();
    a1.insert_string(s);
    return a1;
  }

}


/** Generic implementation for ServiceFactory interface.  
 *  It manages the Service objects using a hashtable.
 */
abstract class ServiceFactoryImpl implements ServiceFactoryOperations {

  Dictionary mServices = new Hashtable();
  org.omg.CORBA.BOA mBoa; 

  public ServiceFactoryImpl(org.omg.CORBA.BOA boa) {
    mBoa = boa;
  }

  public Service find(String name) throws FactoryError {
    // lookup the Service in the Service dictionary
    Service service = (Service) mServices.get(name);
    if(service == null) {
      throw new FactoryError("No service for: " + name);
    }
    return service;
  }

  public void remove(String name) throws FactoryError {
    Service service = find(name);
    service = (Service) mServices.remove(name);
    System.out.println( " " + name + " remove.");
    mBoa.deactivate_obj(service);  // deactivate Corba object
  }

  public String showAll() {
    String s = "";

    for (Enumeration e = mServices.keys(); e.hasMoreElements(); ) {
      s += e.nextElement() + ", ";
    }
    return s;
  }

}


/** Specific implementation for URLService. 
 */
class URLServiceFactoryImpl extends ServiceFactoryImpl {

  public URLServiceFactoryImpl(org.omg.CORBA.BOA boa) {
    super(boa);
  }

 /** Creates a service with a unique service name, such as "URLService5".
  */
  public Service create() {

    int i = 1;
    String name0 = "URLService";
    String name = name0 + i;

    // lookup the service in the service dictionary
    Service service = (Service) mServices.get(name);

    // find a new name
    while (service != null) {
      i++;
      name = name0 + i;
      service = (Service) mServices.get(name);
    }

    ServiceImpl newService = new URLServiceImpl(name);

    // create the tie object for the service 
    service = new _tie_Service(newService);

    // save the service in the service dictionary
    mServices.put(name, service);

    return service;
  }

}


/** Implementation for Server main.
 */
public class Server {
  public static org.omg.CORBA.ORB orb;
  public static ServiceFactoryImpl serviceFactory;

  public static void main(String[] args) {
      orb = org.omg.CORBA.ORB.init(args,null);
      org.omg.CORBA.BOA boa = ((com.visigenic.vbroker.orb.ORB)orb).BOA_init();

      // create the service Factory 
      String serviceFactoryName = "URL Service Factory";
      serviceFactory = new URLServiceFactoryImpl(boa);
      ServiceFactory factory = new _tie_ServiceFactory(serviceFactory, serviceFactoryName);

      // export the object reference
      boa.obj_is_ready(factory);
      System.out.println(factory + " is ready.");

      // wait for requests
      boa.impl_is_ready();
  }
}
