package typeenvironment;

/** The typechecker for F.
m*/
public class IdentifierContext extends Context {
    
    public IdentifierContext(String id_name) {
        super(id_name);
    }

    public boolean find(String name) {
        if (this.name == name)
            return true;
        else
            return false;
    }

    // base case for recursive verification.
    public boolean distinct() {
        return true;
    }
}
