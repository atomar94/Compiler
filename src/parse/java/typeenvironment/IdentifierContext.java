package typeenvironment;

/** The typechecker for F.
m*/
public class IdentifierContext extends Context {
    
    public IdentifierContext(String id_name, String type) {
        super(id_name, type);
    }

    public String find(String name) {
        if (this.name == name)
            return type;
        else
            return "ERROR";
    }

    // base case for recursive verification.
    public boolean distinct() {
        return true;
    }
}
