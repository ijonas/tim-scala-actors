/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

public class ActorConfiguration {
  public static final String INSTANCE_SCOPE = "instance";
  public static final String CLASS_SCOPE = "class";
  public static final String CUSTOM_SCOPE = "custom";

  private String m_className;
  private String m_scope = INSTANCE_SCOPE;
  
  public ActorConfiguration(String className, String scope) {
    m_className = className;
    m_scope = scope;
  }
  
  public String getClassName() {
    return m_className;
  }
  
  public String getScope() {
    return m_scope;
  }
  
  public String toString() {
    return m_className + " " + m_scope;
  }
}