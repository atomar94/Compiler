package typeenvironment;

import java.util.*;

public class GoalContext extends Context {
    List<ClassContext> classes;
    ClassContext mc;

    public GoalContext() {
        super("", "");
        mc = null;
        classes = new ArrayList<ClassContext>();
    }


    public boolean add(MainClassContext mc) {
        if (this.mc == null) {
            System.out.println("Adding MainClassContext to GoalContext");
            this.mc = mc;
            this.mc.setParent(this);
            return true;
        }
        // we already got one of these! Throw an error or something.
        return false;
    }

    // see if an identifier exists within this context.
    // Return null on fail.
    public Context getChildContext(String name) {
        for (ClassContext c : this.classes) {
            if (c.toString() == name) {
                return c;
            }
        }
        if (this.name == "main") {
            return mc;
        }
        return null;
    }


    // check that all of our classes type check.
    public boolean isTypeFailed() {
        if (mc.isTypeFailed()) {
            System.out.println("Main class failed to typecheck.");
            return true;
        }
        for (ClassContext c : classes) {
            if (c.isTypeFailed()) {
                return true;
            }
        }
        return false;
    }

    public ClassContext getMainClassContext() {
        return mc;
    }

    // search all classes and if one matches then get the method context from it.
    public MethodContext getClassMethodContext(String classname, String methodname) {
        for (ClassContext c : classes) {
            for (MethodContext m : c.methods) {
                if (m.toString() == methodname) {
                    System.out.println("Found " + classname + "." + methodname);
                    return m;
                }
            }
        }
        for (MethodContext m : mc.methods) {
            if (m.toString() == name) {
                System.out.println("Found " + classname + "." + methodname);
                return m;
            }
        }
        System.out.println("Could not find " + classname + "." + methodname);
        return null;
    }


    public List<ClassContext> getClassContext() {
        return classes;
    }

    public boolean add(ClassContext cd)  {
        cd.setParent(this);
        return classes.add(cd);
    }

    // search all the classes we've found.
    public String find(String name) {
        System.out.println("Searching GoalContext");
        for (ClassContext c : classes) {
            if(c.toString() == name) {
                System.out.println("\tFound class " + name);
                return c.toString();
            }
        }
        // search MainClass too
        if (mc.toString() == name) {
            System.out.println("\tFound class " + name);
            return mc.toString();
        }
        System.out.println("\tNo class " + name + " found.");
        return "ERROR";
    }

    // recursively verify this goal.
    public boolean distinct() {
        if (mc == null) {
            return false;
        }
        if (!mc.distinct()) {
            return false;
        }

        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                if (classes.get(i).toString() == classes.get(j).toString()) {
                    return false;
                }
                else if (classes.get(i).toString() == mc.toString()) {
                    return false;
                }
            }
            if (!classes.get(i).distinct()) {
                return false;
            }
        }
        return true;
    }

    // check for cycles
    public boolean acyclicSuperClasses() {

        // the key is the child and the value is the parent.
        HashMap<String, String> hm = new HashMap<String, String>();

        // populate our hash map.
        for (ClassContext c : classes) {
            if (!c.getSuperClass().equals("")) {
                hm.put(c.toString(), c.getSuperClass());
            }
        }
        for (ClassContext c : classes) {
            ArrayList<String> visited = new ArrayList<String>();
            String current_node = c.toString();

            // if the HM has an inheritance value for this class
            // find it and mark it visited and then visit it's parent.
            while (hm.containsKey(current_node)) {
                current_node = hm.get(current_node);

                // if we've already visited this node then we 
                // have a cycle.
                if (visited.contains(current_node)) {
                    return false;
                }
                visited.add(current_node);
            }
        }
        return true;

    }

    // do all superclasses exist
    public boolean validSuperClasses() {
        for (ClassContext c : classes) {
            // if this class has a super class
            if (c.getSuperClass() != "") {
                // if this super class doesn't exist fail
                if (! this.find(c.getSuperClass()).equals(c.getSuperClass())) {
                    return false;
                }
            }
        }
        return true;
    }
}