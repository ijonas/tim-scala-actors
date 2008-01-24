package samples

import java.util.concurrent._
import scala.actors.Actor
import scala.actors.Actor._

// Messages
case object Tick
case class AddItem(desc: String)

// Main runner
object ClusteredActorsSample {
  def main(args: Array[String]) {
    val cart = new Cart //Cart()
    cart.start
    cart.ping
    for (i <- 1 to 100) {
      java.lang.Thread.sleep(2000L)
      cart ! AddItem("Item " + i)      
    }
  }
}

/**
 * Cart actor, responds to:
 * <p>-- Tick - prints out the list of current items
 * <p>-- AddItem - adds an item to the list of current items
 */
/*
class Cart extends Actor {
  def act() = loop(Nil)

  def loop(items: List[String]) {
    react {
      case Tick => 
        println("Items: " + items)
        loop(items)
      case AddItem(item) => 
        loop(item :: items)
    }
  }

  def ping = {
    ActorPing.scheduleAtFixedRate(this, Tick, 0L, 5000L)
  }
}
*/
/**
 * Cart companion object is a factory who's sole instance in a Terracotta root: Cart.instance.
 * <p>Usage: val cart = Cart()
 */
object Cart {
  def apply(): Cart = {
    if (instance == null) instance = new Cart
    instance.start; instance
  }
  private[this] var instance: Cart = null
}

 
  // same as above but state in a 'var' instead of passing it on recursively
  class Cart extends Actor {
   var items: List[String] = Nil

   def ping = {
     ActorPing.scheduleAtFixedRate(this, Tick, 0L, 5000L)
   }

   def act() {
     loop {
       react {
         case Tick => 
           println("Items: " + items)
         case AddItem(item) => 
           items = item :: items
       }
     }
   }
 }


// =============================================
/**
 * Pings an actor every X seconds.
 * 
 * Code based on code from the ActorPing class in the /lift/ repository (http://liftweb.net).
 * Copyright: 
 *
 * (c) 2007 WorldWide Conferencing, LLC
 * Distributed under an Apache License
 * http://www.apache.org/licenses/LICENSE-2.0
 */
object ActorPing { 

  def scheduleAtFixedRate(to: Actor, msg: Any, initialDelay: Long, period: Long): ScheduledFuture = {
    val cmd = new Runnable { def run { to ! msg } }
    service.scheduleAtFixedRate(cmd, initialDelay, period, TimeUnit.MILLISECONDS)
  }
  
  private val service = Executors.newSingleThreadScheduledExecutor(threadFactory)  
  
  private object threadFactory extends ThreadFactory {
    val threadFactory = Executors.defaultThreadFactory()
    def newThread(r: Runnable) : Thread = {
      val d: Thread = threadFactory.newThread(r)
      d setName "ActorPing"
      d setDaemon true
      d
    }
  }
}
