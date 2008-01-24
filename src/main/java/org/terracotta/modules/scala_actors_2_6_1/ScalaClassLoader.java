/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import com.tc.object.bytecode.hook.impl.ClassProcessorHelper;
import com.tc.object.loaders.NamedClassLoader;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class ScalaClassLoader extends URLClassLoader implements NamedClassLoader {  
  private final String m_name = "SCALA_CLASS_LOADER";
  
  public ScalaClassLoader(URL[] urls) {
    this(urls, ClassLoader.getSystemClassLoader());
  }

  public ScalaClassLoader(URL[] urls, ClassLoader parent) {
    this(urls, parent, null);
  }

  public ScalaClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
    super(urls, parent, factory);
    ClassProcessorHelper.registerGlobalLoader(this);
  }

  public String __tc_getClassLoaderName() {
    return m_name;
  }

  public void __tc_setClassLoaderName(String name) {
    throw new UnsupportedOperationException("can't set class loader name");
  }
}
