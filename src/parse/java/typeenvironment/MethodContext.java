package typeenvironment;

import typeenvironment.*;
import java.util.*;

/** The typechecker for F.
m*/
public class MethodContext extends Context {
    Context parent;
    List<IdentifierContext> identifiers;
    
    public MethodContext(String method_name) {
        super(method_name);
        //this.name = method_name;
        parent = null;
        this.identifiers = new ArrayList<IdentifierContext>();
    }

    public List<IdentifierContext> getIdentifierContext() {
        return identifiers;
    }

    public boolean add(IdentifierContext c) {
        //if methods.contains(c) return false because duplicate
        return identifiers.add(c);
    }

    public boolean find(String name) {
        if (this.name == name)
            return true;
        for (IdentifierContext c : identifiers){
            if (c.find(name))
                return true;
        }
        if (this.parent != null) {
            return this.parent.find(name);
        }
        else {
            return false;
        }
    }

    // recursively verify this method.
    public boolean distinct() {
        for (int i = 0; i < identifiers.size(); i++) {
            for (int j = i + 1; j < identifiers.size(); j++) {
                if (identifiers.get(i).toString() == identifiers.get(j).toString()) {
                    return false;
                }
            }
        }
        return true;
    }
}
