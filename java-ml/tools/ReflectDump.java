// $Id$
// Copyright (C) 2000 Greg J. Badros <gjb@cs.washington.edu>
//
// @$CLASSPATH xml-writer.jar xerces.jar

import java.lang.reflect.*;
import java.io.*;
import com.megginson.sax.XMLWriter;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class ReflectDump {
  public static void Dump(XMLWriter w, Class c) 
    throws SAXException{
    AttributesImpl atts = new AttributesImpl();
    w.startElement("java-class-file");
    String className = c.getName();
    atts.addAttribute("","name","","",className);
    int m = c.getModifiers();
    if (Modifier.isPublic(m)) {
      atts.addAttribute("","visibility","","","public");
    } else if (Modifier.isPrivate(m)) {
      atts.addAttribute("","visibility","","","private");
    } else if (Modifier.isProtected(m)) {
      atts.addAttribute("","visibility","","","protected");
    }
    if (Modifier.isStatic(m)) {
      atts.addAttribute("","static","","","true");
    }
    if (Modifier.isFinal(m)) {
      atts.addAttribute("","final","","","true");
    }
    if (Modifier.isAbstract(m)) {
      atts.addAttribute("","abstract","","","true");
    }
    if (Modifier.isSynchronized(m)) {
      atts.addAttribute("","synchronized","","","true");
    }
    String classOrInterface = "class";
    if (c.isInterface()) {
      classOrInterface = "interface";
    }
    w.startElement("",classOrInterface,"", atts);
    atts.clear();
    Class superclass = c.getSuperclass();
    if (null != superclass) {
      String superclassName = superclass.getName();
      atts.addAttribute("","name","","",superclassName);
      w.startElement("","superclass","", atts);
      atts.clear();
      w.endElement("superclass");
    }
    Class[] interfaces = c.getInterfaces();
    for (int i=0; i < interfaces.length; ++i) {
      String interfaceName = interfaces[i].getName();
      atts.addAttribute("","interface","","",interfaceName);
      w.startElement("","implements","", atts);
      atts.clear();
      w.endElement("implements");
    }
    Field[] fields = c.getDeclaredFields();
    for (int i=0; i < fields.length; ++i) {
      Dump(w,fields[i]);
    }
    w.endElement("class");
    w.endElement("java-class-file");
    w.endDocument();
  }

  public static void Dump(XMLWriter w, Field f) 
    throws SAXException {
    // GREGB:FIXME:: need to handle visibility, etc.
    AttributesImpl atts = new AttributesImpl();
    atts.addAttribute("","name","","",f.getName());
    w.startElement("","field","",atts);
    atts.clear();
    Class type = f.getType();
    atts.addAttribute("","name","","",type.getName());
    w.startElement("","type","",atts);
    atts.clear();
    w.endElement("type");
    w.endElement("field");
  }
  


  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage:\n" +
                         "java ReflectDump CLASS-NAME\n");
      return;
    }
    try {
      XMLWriter w = new XMLWriter();
      w.startDocument();
      Dump(w,Class.forName(args[0]));
    }
    catch (ClassNotFoundException e) {
      System.err.println("Could not find class: " + e);
    }
    catch (SAXException e) {
      System.err.println("Caught SAXException: " + e);
    }
  }
}
