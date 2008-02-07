/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import com.tc.aspectwerkz.joinpoint.StaticJoinPoint;
import com.tc.object.bytecode.Manager;
import com.tc.object.bytecode.ManagerUtil;

import java.util.HashMap;
import java.util.Map;

public class ClassScopeActorProtocol {
  
  // Create a Terracotta root that holds all actors
  private static final Map<String, Object> ACTORS; 
  static {
    ManagerUtil.beginLock("SCALA_ACTORS_ROOT_CLASS_SCOPE", Manager.LOCK_TYPE_WRITE);    
    try {
      ACTORS = (Map<String, Object>)ManagerUtil.lookupOrCreateRoot("SCALA_ACTORS_CLASS_SCOPE", new HashMap<String, Object>());
    } finally {
      ManagerUtil.commitLock("SCALA_ACTORS_ROOT_CLASS_SCOPE");      
    }   
  }

  /**
   * Advice that maintains the singleton constraint.
   */
  public Object newActor(final StaticJoinPoint jp) throws Throwable {
    ManagerUtil.monitorEnter(ACTORS, Manager.LOCK_TYPE_WRITE);     
    try {
      final String id = jp.getCalleeClass().getName().trim();
      if (ACTORS.containsKey(id)) {
          return ACTORS.get(id);
        } else {          
          final Object actor = jp.proceed();
          ACTORS.put(id, actor);
          return actor;
        }
    } finally {
      ManagerUtil.monitorExit(ACTORS);     
    }      
  }
}