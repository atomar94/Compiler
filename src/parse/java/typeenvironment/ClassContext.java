package typeenvironment;

import java.util.*;

/** The typechecker for F.
m*/
public class ClassContext extends Context{
    public List<MethodContext> methods;
    public List<IdentifierContext> identifiers;
    public String superClass;
    
    public ClassContext(String class_name) {
        // the name of this class is the type!
        super(class_name, class_name);
        this.parent = null; // parent context
        this.methods = new ArrayList<MethodContext>();
        this.identifiers = new ArrayList<IdentifierContext>();
        this.superClass = ""; // parent (inherited) class
    }

    public String getSuperClass() {
        return this.superClass;
    }

    // set the superclass for this class.
    public void setSuperClass(String superclass) {
        this.superClass = superclass;
    }

    public List<MethodContext> getMethodContext() {
        return methods;
    }

    public List<IdentifierContext> getIdentifierContext() {
        return identifiers;
    }

    public MethodContext getClassMethodContext(String classname, String methodname) {
        if (classname == this.toString()) {
            for (MethodContext m : methods) {
                if (m.toString() == methodname) {
                    return m;
                }
            }
        } else {
            return this.parent.getClassMethodContext(classname, methodname);
        }
        return null;
    }


    // class has a more complicated isTypeFailed because we want to 
    // check the child methods. we dont have to, but we should for
    // robustness.
    public boolean isTypeFailed() {
        if (typeCheckFailed) {
            return true;
        }
        else {
            // make sure the methods did fail and the error just
            // didnt end up propagating upwards.
            for (MethodContext m : methods) {
                if (m.isTypeFailed()) {
                    return true;
                }
            }
        }
        return false;
    }

    // see if an identifier exists within this context.
    // Return null on fail.
    public Context getChildContext(String name) {
        for (IdentifierContext c : this.identifiers) {
            if (c.toString() == name) {
                return c;
            }
        }
        for (MethodContext m : this.methods) {
            if (m.toString() == name) {
                return m;
            }
        }
        return null;
    }


    public boolean add(MethodContext c) {
        c.setParent(this);
        return methods.add(c);
    }

    public boolean add(IdentifierContext c) {
        c.setParent(this);
        return identifiers.add(c);
    }

    public String find(String name) {
        // if we are tryingto find "this" then return the name of 
        // this class.
        if (name == "this") {
            return this.name;
        }

        for (MethodContext m : methods) {
            if (name == m.toString()) {
                return m.toType();
            }
        }

        String ret = "";
        for (IdentifierContext i : identifiers) {
            ret = i.find(name);
            if (ret != "ERROR") {
                return ret;
            }
        }

        // if our parent exists (it should) go search it.
        if (this.parent != null) {
            return this.parent.find(name);
        }
        // couldnt find the identifier in this class context, and this is the
        // highest context there is (until we check inheritance)
        return "ERROR";
    }

    // recursively verify this class.
    public boolean distinct() {
        // check for duplicate identifiers
        for (int i = 0; i < identifiers.size(); i++) {
            for (int j = i + 1; j < identifiers.size(); j++) {
                if (identifiers.get(i).toString() == identifiers.get(j).toString()) {
                    return false;
                }
            }
            if(!identifiers.get(i).distinct()) {
                return false;
            }
        }
        // check for duplicate methods
        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                if (methods.get(i).toString() == methods.get(j).toString()) {
                    return false;
                }
            }
            if(!methods.get(i).distinct()) {
                return false;
            }
        }
        return true;
    }
}