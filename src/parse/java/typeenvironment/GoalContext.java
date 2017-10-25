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

    public boolean add(ClassContext cd)  {
        return classes.add(cd);
    }

    boolean find() {
        return false;
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
}