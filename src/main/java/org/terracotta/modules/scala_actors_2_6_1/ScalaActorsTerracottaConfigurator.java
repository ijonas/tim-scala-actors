/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.tc.object.config.ConfigLockLevel;
import com.tc.object.config.LockDefinition;

import org.terracotta.modules.configuration.TerracottaConfiguratorModule;

import java.util.Iterator;

public final class ScalaActorsTerracottaConfigurator extends TerracottaConfiguratorModule {
  // TODO: use TC logger instead of STDOUT when the prefix (com.tc) restriction has been removed
  //private static final TCLogger LOGGER = TCLogging.getLogger(ScalaActorsTerracottaConfigurator.class);
  
  private static final String SCALA_ACTORS_BUNDLE_NAME = "org.terracotta.modules.clustered-scala-actors-2.6.1";
  private static final String SCALA_COMPILER_MAIN_CLASS = "scala.tools.nsc.ObjectRunner$";
  private static final String SCALA_INTERPRETER_MAIN_CLASS = "scala.tools.nsc.InterpreterLoop";
  
  protected final void addInstrumentation(final BundleContext context) {  
    super.addInstrumentation(context);
    
    Bundle bundle = getExportedBundle(context, SCALA_ACTORS_BUNDLE_NAME);
    if (null == bundle) {
      throw new RuntimeException("Couldn't find bundle with symbolic name '" + SCALA_ACTORS_BUNDLE_NAME + 
      "' during the instrumentation configuration of the bundle '" + context.getBundle().getSymbolicName() + "'.");
    }
  
    configHelper.addCustomAdapter(SCALA_COMPILER_MAIN_CLASS, new ScalaCompilerClassLoaderAdapter());
    configHelper.addCustomAdapter(SCALA_INTERPRETER_MAIN_CLASS, new ScalaInterpreterClassLoaderAdapter());
    
    addExportedBundleClass(bundle, "org.terracotta.modules.scala_actors_2_6_1.ScalaClassLoader");
    addExportedBundleClass(bundle, "org.terracotta.modules.scala_actors_2_6_1.InstanceScopeActorProtocol");
    addExportedBundleClass(bundle, "org.terracotta.modules.scala_actors_2_6_1.ClassScopeActorProtocol");
    addExportedBundleClass(bundle, "org.terracotta.modules.scala_actors_2_6_1.ScalaAspectModule");
    addExportedBundleClass(bundle, "org.terracotta.modules.scala_actors_2_6_1.ActorConfiguration");

    for (Iterator<ActorConfiguration> it = ScalaAspectModule.ACTOR_CONFIGURATIONS.iterator(); it.hasNext();) {
      configureActor(it.next().getClassName());
    }
    
    // TODO: not ideal to pass in empty string, e.g. 'match-all', but we don't know in whiche packages
    // the customer's classes that are using the actors resides
    configHelper.addAspectModule("", "org.terracotta.modules.scala_actors_2_6_1.ScalaAspectModule");      
  }

  private void configureActor(String actor) {
    // == define generic locking for actor ==
    LockDefinition lockDefinition = configHelper.createLockDefinition("actors_continuation_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    String lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$continuation_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_continuation", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$continuation()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_received_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$received_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_received", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$received()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_session1_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$session1_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_session1", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$session1()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_sessions_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$sessions_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_sessions", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$sessions()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_mailbox_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$mailbox_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_mailbox", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$mailbox()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_isSuspended_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$isSuspended_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_isSuspended", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$isSuspended()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_rc_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$rc_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_rc", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$rc()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_isWaiting_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$isWaiting_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_isWaiting", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$isWaiting()").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    
    lockDefinition = configHelper.createLockDefinition("actors_timeoutPending_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$timeoutPending_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_timeoutPending", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$timeoutPending()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_waitingFor_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$waitingFor_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_waitingFor", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$waitingFor()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_waitingForNone_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$waitingForNone_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_waitingForNone", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".scala$actors$Actor$$waitingForNone()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_trapExit_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".trapExit_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_trapExit", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".trapExit()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_exitReason_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".exitReason_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_exitReason", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".exitReason()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_exiting_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".exiting_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_exiting", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".exiting()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_shouldExit_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".shouldExit_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_shouldExit", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".shouldExit()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_isDetached_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".isDetached_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_isDetached", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".isDetached()").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_kill_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".kill_$eq(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    lockDefinition = configHelper.createLockDefinition("actors_kill", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append(".kill()").toString();
    configHelper.addLock(lockPattern, lockDefinition);
    
    lockDefinition = configHelper.createLockDefinition("actors_anon_apply", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = new StringBuilder("* ").append(actor).append("*.apply(..)").toString();
    configHelper.addLock(lockPattern, lockDefinition);

    // == define locking for message queue ==
    lockDefinition = configHelper.createLockDefinition("actors_message_queue_append", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.append(..)";
    configHelper.addLock(lockPattern, lockDefinition);    
    
    lockDefinition = configHelper.createLockDefinition("actors_message_queue_extract_first", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.extractFirst(..)";
    configHelper.addLock(lockPattern, lockDefinition);

    lockDefinition = configHelper.createLockDefinition("actors_message_queue_last_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.last_$eq(..)";
    configHelper.addLock(lockPattern, lockDefinition); 
    lockDefinition = configHelper.createLockDefinition("actors_message_queue_last", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.last()";
    configHelper.addLock(lockPattern, lockDefinition); 
    
    lockDefinition = configHelper.createLockDefinition("actors_message_queue_first_$eq", ConfigLockLevel.WRITE);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.first_$eq(..)";
    configHelper.addLock(lockPattern, lockDefinition); 
    lockDefinition = configHelper.createLockDefinition("actors_message_queue_first", ConfigLockLevel.READ);
    lockDefinition.commit();
    lockPattern = "* scala.actors.MessageQueue.first()";
    configHelper.addLock(lockPattern, lockDefinition); 
    
    // add generic includes
    configHelper.addIncludePattern(actor);
    configHelper.addIncludePattern("scala.tools.nsc.Interpreter*");
    configHelper.addIncludePattern("scala.actors.ActorProxy");
    configHelper.addIncludePattern("scala.actors.Actor$");
    configHelper.addIncludePattern("scala.actors.Actor$class");
    configHelper.addIncludePattern("scala.actors.Actor$$anon$*");
    configHelper.addIncludePattern("scala.actors.Actor$$anonfun$*");
    configHelper.addIncludePattern("scala.actors.MessageQueue");
    configHelper.addIncludePattern("scala.actors.MessageQueueElement");
    configHelper.addIncludePattern("scala.actors.OutputChannel");
    configHelper.addIncludePattern("scala.actors.TIMEOUT$");
    configHelper.addIncludePattern("scala.runtime.ObjectRef");
    configHelper.addIncludePattern("scala.Symbol");
    configHelper.addIncludePattern("scala.Option");
    configHelper.addIncludePattern("scala.Some");
    configHelper.addIncludePattern("scala.None$");

    /*    
  <distributed-methods>
    <method-expression>* *..*.$bang(..)</method-expression>
    <method-expression>* *..*.react(..)</method-expression>
  </distributed-methods>
     */
  }
}
