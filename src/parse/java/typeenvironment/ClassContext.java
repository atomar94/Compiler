package typeenvironment;

import java.util.*;

/** The typechecker for F.
m*/
public class ClassContext extends Context{
    Context parent;
    List<MethodContext> methods;
    List<IdentifierContext> identifiers;
    
    public ClassContext(String class_name) {
        super(class_name);
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

    public boolean add(MethodContext c) {
        return methods.add(c);
    }

    public boolean add(IdentifierContext c) {
        return identifiers.add(c);
    }

    public boolean find(String name) {
        if (this.name == name) {
            return true;
        }
        for (MethodContext m : methods) {
            if (m.find(name))
                return true;
        }
        for (IdentifierContext i : identifiers) {
            if (i.find(name))
                return true;
        }
        if (parent != null) {
            return parent.find(name);
        }
        else {
            return false;
        }
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