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
        //if methods.contains(c) return false because duplicate
        return methods.add(c);
    }

    public boolean add(IdentifierContext c) {
        //if methods.contains(c) return false because duplicate
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
}