package typeenvironment;

import java.util.*;

/** The typechecker for F.
m*/
public abstract class Context {
    String name;
    String type;
    Context parent;
    boolean typeCheckFailed;

    public Context(String name, String type) {
        this.name = name;
        this.type = type;
        this.parent = null;
        typeCheckFailed = false;
    }

    public Context getChildContext(String name) {
        return null;
    }

    // we need this to be polymorphic because we don't know the type of
    // context we have so we need to be able to call it on any context
    // object. The default behavior is do nothing.
    public void addParameterType(String type) {
        return;
    }

    // we need this to be polymorphic because we don't know the type of
    // context we have so we need to be able to call it on any context
    // object. The default behavior is do nothing.
    public ArrayList<String> getParameterTypes() {
        return null;
    }


    public void typeFailed() {
        typeCheckFailed = true;
    }

    public boolean isTypeFailed() {
        return typeCheckFailed;
    }

    public void setParent(Context parent) {
        this.parent = parent;
    }

    public MethodContext getClassMethodContext(String classname, String methodname) {
        return null;
    }

    public String toString() {
      return name;
    }

    public String toType() {
        return type;
    }

    // return the type of the identifier
    public String find(String name) {
      return "ERROR";
    }

    public boolean add(Context c) {
        System.out.println("Context::add(Context)");
        return false;
    }

    public boolean add(ClassContext cd) {
        System.out.println("Context::add(ClassContext)");
        return false;
    }

    public boolean add(MainClassContext cd) {
        System.out.println("Context::add(MainClassContext)");
        return false;
    }

    public boolean add(MethodContext c) {
        System.out.println("Context::add(MethodContext)");
        return false;
    }

    public boolean add(IdentifierContext c) {
        System.out.println("Context::add(IdentifierContext)");
        return false;
    }

    public boolean distinct() {
        return false;
    }
}