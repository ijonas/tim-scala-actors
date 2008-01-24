/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import com.tc.aspectwerkz.joinpoint.StaticJoinPoint;
import com.tc.object.bytecode.Manager;
import com.tc.object.bytecode.ManagerUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ScalaActorsProtocol {
  
  // Create a Terracotta root that holds all actors
  private static final Map<String, Object> ACTORS; 
  static {
    ManagerUtil.beginLock("SCALA_ACTORS_ROOT", Manager.LOCK_TYPE_WRITE);    
    try {
      ACTORS = (Map<String, Object>)ManagerUtil.lookupOrCreateRoot("SCALA_ACTORS", new HashMap<String, Object>());
    } finally {
      ManagerUtil.commitLock("SCALA_ACTORS_ROOT");      
    }   
  }

  /**
   * Advice that maintains the singleton constraint.
   */
  public Object newActor(final StaticJoinPoint jp) throws Throwable {
    ManagerUtil.monitorEnter(ACTORS, Manager.LOCK_TYPE_WRITE);     
    try {
      final String id = calculateId(jp);
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
  
  /**
   * Calculate an ID based on context and location of the instantiation in the code.
   */
  private String calculateId(final StaticJoinPoint jp) { 
    StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter(sw);
	pw.println(jp.getCalleeClass().getName());
	new Throwable().printStackTrace(pw);
    return getDigest(sw.toString());
  }

  private String getDigest(String s) {
    try {
	  s = s.replaceAll(System.getProperty("line.separator"), "\n");
	  MessageDigest digest = MessageDigest.getInstance("MD5");
	  digest.update(s.getBytes("ASCII"));
	  byte[] b = digest.digest();
	      
	  StringBuilder sb = new StringBuilder();
	  String hex = "0123456789ABCDEF";
	  for (int i = 0; i < b.length; i++) {
	    int n = b[i];
	    sb.append(hex.charAt((n & 0xF) >> 4)).append(hex.charAt(n & 0xF));
	  }
	  return sb.toString();
    } catch (NoSuchAlgorithmException e) {
	  // should never happen
	  throw new RuntimeException(e.getMessage());
	} catch (UnsupportedEncodingException e) {
	  // should never happen
	  throw new RuntimeException(e.getMessage());
	}
  }
}