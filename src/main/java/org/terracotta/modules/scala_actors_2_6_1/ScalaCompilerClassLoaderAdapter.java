/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package org.terracotta.modules.scala_actors_2_6_1;

import com.tc.asm.MethodAdapter;
import com.tc.asm.Opcodes;
import com.tc.asm.ClassAdapter;
import com.tc.asm.ClassVisitor;
import com.tc.asm.MethodVisitor;
import com.tc.object.bytecode.ClassAdapterFactory;

/**
 * Custom adapter used to replace <code>URLClassLoader</code> implementation in Scala's <code>ObjectRunner$</code>
 * to a Terracotta-enabled loader. .
 */
public class ScalaCompilerClassLoaderAdapter extends ClassAdapter implements Opcodes, ClassAdapterFactory {

  public ScalaCompilerClassLoaderAdapter() {
    super(null);
  }

  public ScalaCompilerClassLoaderAdapter(ClassVisitor visitor, ClassLoader loader) {
    super(visitor);
  }

  public ClassAdapter create(ClassVisitor visitor, ClassLoader loader) {
    return new ScalaCompilerClassLoaderAdapter(visitor, loader);
  }
 
  public MethodVisitor visitMethod(int access, String methodName, String description, String signature, String[] exceptions) {
    MethodVisitor oldBody = super.visitMethod(access, methodName, description, signature, exceptions);
    if(!"makeClassLoader".equals(methodName) || !"(Lscala/List;)Ljava/net/URLClassLoader;".equals(description)) {
      return oldBody;
    }
    
    MethodVisitor newBody = new MethodAdapter(oldBody) {
      
      @Override
      public void visitTypeInsn(int opcode, String desc) {
        if (opcode == Opcodes.NEW && desc.equals("java/net/URLClassLoader")) {
          super.visitTypeInsn(opcode, "org/terracotta/modules/scala_actors_2_6_1/ScalaClassLoader");
        } else {
          super.visitTypeInsn(opcode, desc);
        }
      }
      
      @Override
      public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/net/URLClassLoader") && desc.equals("([Ljava/net/URL;Ljava/lang/ClassLoader;)V")) {
          super.visitMethodInsn(opcode, "org/terracotta/modules/scala_actors_2_6_1/ScalaClassLoader", name, desc);          
        } else {
          super.visitMethodInsn(opcode, owner, name, desc);
        }
      }
    };
    return newBody;
  }
}