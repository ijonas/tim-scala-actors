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
  //       <actor scope="custom">foo.bar.MyActor</actor>
  //     </actors>
  //   </jee-application>
  //   <!-- 
  //      Roots, Includes, Locks etc.
  //   -->
  // </scala>
  //
  public static final List<ActorConfiguration> ACTOR_CONFIGURATIONS;
  static {
    CLUSTERED_ACTORS_CONFIG_FILENAME = System.getProperty(
        CLUSTERED_SCALA_ACTORS_CONFIG_JVM_PROPERTY_NAME, DEFAULT_CLUSTERED_SCALA_ACTORS_CONFIG_FILENAME);
    if ((new File(CLUSTERED_ACTORS_CONFIG_FILENAME)).exists()) {
      System.out.println("Parsing scala actors config file: " + CLUSTERED_ACTORS_CONFIG_FILENAME);
      ACTOR_CONFIGURATIONS = readActorsFromConfigFile();
      for (Iterator<ActorConfiguration> it = ACTOR_CONFIGURATIONS.iterator(); it.hasNext();) {
        ActorConfiguration conf = it.next();
        System.out.println("Configuring clustering for Scala Actor [" + conf.getClassName() + "] with scope [" + conf.getScope() + "]");        
      }
    } else {
      ACTOR_CONFIGURATIONS = Collections.emptyList();
    }
  }

  public void deploy(final AspectModuleDeployer deployer) {
    AspectDefinitionBuilder instanceScopeBuilder = deployer.newAspectBuilder(
        "org.terracotta.modules.scala_actors_2_6_1.InstanceScopeActorProtocol", DeploymentModel.PER_JVM, null);
    AspectDefinitionBuilder classScopeBuilder = deployer.newAspectBuilder(
        "org.terracotta.modules.scala_actors_2_6_1.ClassScopeActorProtocol", DeploymentModel.PER_JVM, null);
    for (Iterator<ActorConfiguration> it = ACTOR_CONFIGURATIONS.iterator(); it.hasNext();) {
      ActorConfiguration conf = it.next();      
      if (conf.getScope().equals(ActorConfiguration.CUSTOM_SCOPE)) {
        continue;
      } else if (conf.getScope().equals(ActorConfiguration.CLASS_SCOPE)) {
        classScopeBuilder.addAdvice("around", "call(" + conf.getClassName() + ".new(..))", "newActor(StaticJoinPoint jp)");                      
      } else if (conf.getScope().equals(ActorConfiguration.INSTANCE_SCOPE)) {
        instanceScopeBuilder.addAdvice("around", "call(" + conf.getClassName() + ".new(..))", "newActor(StaticJoinPoint jp)");                               
      } else {
        System.out.println("Scala Actor scope unknown [" + conf.getScope() + "], needs to be one of [instance (default), class or custom]");
      }
    }
  }

  private static List<ActorConfiguration> readActorsFromConfigFile() {
    List<ActorConfiguration> actors = new ArrayList<ActorConfiguration>();
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(CLUSTERED_ACTORS_CONFIG_FILENAME));
      String line;
      while ((line = reader.readLine()) != null) {
        line.trim();
        if (line.startsWith("#") || line.equals("")) continue; // allow comments and blank lines
        int colon = line.indexOf(':');
        if (colon == -1) { // default -> instance scope
          actors.add(new ActorConfiguration(line.trim(), ActorConfiguration.INSTANCE_SCOPE));          
        } else {
          String actor = line.substring(0, colon);
          String scope = line.substring(colon + 1, line.length());
          actors.add(new ActorConfiguration(actor.trim(), scope.trim()));
        }
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

