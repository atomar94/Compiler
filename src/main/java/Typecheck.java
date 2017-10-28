import syntaxtree.*;

import visitor.*;

import java.util.*;

import typeenvironment.*;

/** The typechecker for F.
m*/
public class Typecheck extends GJDepthFirst<Type, Map<String, Type>>{

  public static void main (String [] args) {
    /*
    try {
      Goal goal = new FParser(System.in).Goal();

      HashMap<String, Type> a = new HashMap<>();

      Type t = goal.accept(new Typecheck(), a);
      // Don't do this either!!!!
      System.out.println(t);

    } catch (ParseException e) {
      e.printStackTrace();
      System.exit(-1);
    } catch (TypeException e) {
      // Don't DO THIS!
      System.out.println("...");
      e.printStackTrace();
      System.exit(-1);
    }
    */
        new MiniJavaParser(System.in);
        Node parse_tree = null;
        try {
            parse_tree = MiniJavaParser.Goal();
        } catch (Exception e) {
            System.out.println("Parse Error");
        }
        GoalContext c = new GoalContext();

        // Grab all the identifiers and populate the GoalContext with them,
        ASTIdentifierVisitor ast_identifiers = new ASTIdentifierVisitor();
        try {
            parse_tree.accept(ast_identifiers, c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verify that our identifiers are distinct.
        // TODO verify we have no inheriance cycles.
        if (c.distinct()) {
            System.out.println("Declarations are distinct.");
        } else {
            System.out.println("Declarations not distinct");
        }
        if (c.validSuperClasses() && c.acyclicSuperClasses()) {
          System.out.println("Superclasses exist and are acyclic");
        }
        else {
          System.out.println("Superclasses malformed.");
        }
        // debug printouts
        /*
        ClassContext mcc = c.getMainClassContext();
        System.out.println("Main Class: " + mcc.toString());
        for (MethodContext mc : mcc.getMethodContext()) {
            System.out.println("\tMethod: " + mc.toString() + " (" + mc.toType() + ")");

            for (IdentifierContext ic : mc.getIdentifierContext()) {
                System.out.println("\t\tIdentifier: " + ic.toString() + " (" + ic.toType() + ")");
            }
        }
        for (IdentifierContext ic : mcc.getIdentifierContext()) {
            System.out.println("\tIdentifier: " + ic.toString() + " (" + ic.toType() + ")");
        }        

        for (ClassContext cc : c.getClassContext()) {
            System.out.println("Class: " + cc.toString() + " (" + cc.toType() + ")");

            for (MethodContext mc : cc.getMethodContext()) {
                System.out.println("\tMethod: " + mc.toString() + " (" + mc.toType() + ")");

                for (IdentifierContext ic : mc.getIdentifierContext()) {
                    System.out.println("\t\tIdentifier: " + ic.toString() + " (" + ic.toType() + ")");
                }
          }
          for (IdentifierContext ic : cc.getIdentifierContext()) {
              System.out.println("\tIdentifier: " + ic.toString() + " (" + ic.toType() + ")");
          }
        }
        */
        // Use the context that is full of indentifiers and run over the
        // tree again to check 
        ASTContextVisitors ast_typechecker = new ASTContextVisitors();
        try {
            parse_tree.accept(ast_typechecker, c);
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Another Error");
        }
        if (c.isTypeFailed()) {
            System.out.println("Type Check failed");
        }
        else {
            System.out.println("Type Check succeeded");
        }

        //System.out.println("Program parsed successfully");
  }
}

class TypeException extends RuntimeException {
  public TypeException (String msg) {
    super(msg);
  }
}