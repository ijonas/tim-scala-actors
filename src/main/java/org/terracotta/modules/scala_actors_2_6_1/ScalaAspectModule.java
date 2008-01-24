/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import com.tc.aspectwerkz.DeploymentModel;
import com.tc.aspectwerkz.definition.deployer.AspectModule;
import com.tc.aspectwerkz.definition.deployer.AspectModuleDeployer;
import com.tc.aspectwerkz.definition.deployer.AspectDefinitionBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Manages deployment of AspectWerkz aspects used to cluster Scala Actors.
 */
public class ScalaAspectModule implements AspectModule {
  private static final String CLUSTERED_SCALA_ACTORS_CONFIG_JVM_PROPERTY_NAME = "clustered.scala.actors.config";
  private static final String DEFAULT_CLUSTERED_SCALA_ACTORS_CONFIG_FILENAME = "clustered-scala-actors.conf";
  private static final String CLUSTERED_ACTORS_CONFIG_FILENAME; 
  
  // Read in all actor class names from actor config file
  //
  // TODO: should be part of the tc-config.xml but need to be added to schema
  // Something like:
  // 
  // <scala> 
  //   <jee-application name="myWebApp">
  //     <actors> 
  //       <actor>foo.bar.MyActor</actor>
  //     </actors>
  //   </jee-application>
  //   <!-- 
  //      Roots, Includes, Locks etc.
  //   -->
  // </scala>
  //
  public static final List<String> ACTOR_CLASSNAMES;
  static {
    CLUSTERED_ACTORS_CONFIG_FILENAME = System.getProperty(
        CLUSTERED_SCALA_ACTORS_CONFIG_JVM_PROPERTY_NAME, DEFAULT_CLUSTERED_SCALA_ACTORS_CONFIG_FILENAME);
    if ((new File(CLUSTERED_ACTORS_CONFIG_FILENAME)).exists()) {
      System.out.println("Parsing scala actors config file: " + CLUSTERED_ACTORS_CONFIG_FILENAME);
      ACTOR_CLASSNAMES = readActorsFromConfigFile();
    } else {
      ACTOR_CLASSNAMES = Collections.EMPTY_LIST;
    }
  }

  public void deploy(final AspectModuleDeployer deployer) {
    AspectDefinitionBuilder builder = deployer.newAspectBuilder(
        "org.terracotta.modules.scala_actors_2_6_1.ScalaActorsProtocol", DeploymentModel.PER_JVM, null);
    for (Iterator<String> it = ACTOR_CLASSNAMES.iterator(); it.hasNext();) {
      String actor = it.next();
      builder.addAdvice("around", "call(" + actor + ".new(..))", "newActor(StaticJoinPoint jp)");      
    }
  }

  private static List<String> readActorsFromConfigFile() {
    List<String> actors = new ArrayList<String>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(CLUSTERED_ACTORS_CONFIG_FILENAME));
      String actor;
      while ((actor = reader.readLine()) != null) {
        actor.trim();
        if (actor.startsWith("#") || actor.equals("")) continue; // allow comments and blank lines
        actors.add(actor);
      }
    } catch (IOException e) {
      System.err.println("Error when trying to read the scala actors config file: " + e.toString());
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    return actors;
  }
}

