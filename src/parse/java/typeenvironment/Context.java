package typeenvironment;

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

    public void typeFailed() {
        typeCheckFailed = true;
    }

    public boolean isTypeFailed() {
        return typeCheckFailed;
    }

    public void setParent(Context parent) {
        this.parent = parent;
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