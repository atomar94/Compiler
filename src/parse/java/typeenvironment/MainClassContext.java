package typeenvironment;

import java.util.*;


// this is the exact same implementation as ClassContext but
// we need to differentiate it so we can use polymorphism
// to parse and store it.
public class MainClassContext extends ClassContext{

    public MainClassContext(String classname) {
        super(classname);
    }
}