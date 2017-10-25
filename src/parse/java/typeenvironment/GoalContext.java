package typeenvironment;

import java.util.*;

public class GoalContext extends Context {
    List<ClassContext> classes;
    ClassContext mc;

    public GoalContext() {
        super("");
        mc = null;
        classes = new ArrayList<ClassContext>();
    }


    public boolean add(MainClassContext mc) {
        if (this.mc == null) {
            this.mc = mc;
            return true;
        }
        // we already got one of these! Throw an error or something.
        return false;
    }

    public ClassContext getMainClassContext() {
        return mc;
    }


    public List<ClassContext> getClassContext() {
        return classes;
    }

    public boolean add(ClassContext cd) {
        // if cd in classes then fail
        classes.add(cd); //classes.add is an ArrayList method.
        return true;
    }

    boolean find() {
        return false;
    }
}