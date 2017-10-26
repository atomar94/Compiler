package typeenvironment;

import java.util.*;

/** The typechecker for F.
m*/
public class ClassContext extends Context{
    Context parent;
    List<MethodContext> methods;
    List<IdentifierContext> identifiers;
    
    public ClassContext(String class_name) {
        // the name of this class is the type!
        super(class_name, class_name);
        parent = null;
        this.methods = new ArrayList<MethodContext>();
        this.identifiers = new ArrayList<IdentifierContext>();
    }

    public List<MethodContext> getMethodContext() {
        return methods;
    }

    public List<IdentifierContext> getIdentifierContext() {
        return identifiers;
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
                    System.out.println("Method " + m.toString() + " fail");
                    return true;
                }
            }
        }
        return false;
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