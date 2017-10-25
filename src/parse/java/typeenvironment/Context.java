package typeenvironment;

/** The typechecker for F.
m*/
public abstract class Context {
    String name;

    public Context(String name) {
        this.name = name;
    }

    public String toString() {
      return name;
    }

    public boolean find(String name) {
      return false;
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