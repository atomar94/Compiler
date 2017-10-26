package typeenvironment;

import typeenvironment.*;
import java.util.*;

/** The typechecker for F.
m*/
public class MethodContext extends Context {
    List<IdentifierContext> identifiers;

    public MethodContext(String method_name, String type) {
        super(method_name, type);
        this.identifiers = new ArrayList<IdentifierContext>();
    }

    public List<IdentifierContext> getIdentifierContext() {
        return identifiers;
    }

    public boolean add(IdentifierContext c) {
        c.setParent(this);
        return identifiers.add(c);
    }

    public String find(String name) {
        if (this.name == name)
            return type;

        for (IdentifierContext c : identifiers){
            String ret = c.find(name);
            if (ret != "ERROR")
                return ret;
        }
        // if our parent exists (it should) go search it.
        if (this.parent != null) {
            return this.parent.find(name);
        }
        return "ERROR";
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
