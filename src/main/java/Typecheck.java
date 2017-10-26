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
        ASTContextVisitors parse_tree_visitor = new ASTContextVisitors();

        try {
            parse_tree.accept(parse_tree_visitor, c);
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("Another Error");
        }

        if (c.distinct()) {
            System.out.println("Verified distinct declarations");
        } else {
            System.out.println("Declarations not distinct");
        }
        if (c.isTypeFailed()) {
            System.out.println("Type Check failed");
        }
        else {
            System.out.println("Type Check succeeded");
        }

        // debug printouts
        ClassContext mcc = c.getMainClassContext();
        System.out.println("Main Class: " + mcc.toString());
        for (MethodContext mc : mcc.getMethodContext()) {
            System.out.println("\tMethod: " + mc.toString());

            for (IdentifierContext ic : mc.getIdentifierContext()) {
                System.out.println("\t\tIdentifier: " + ic.toString());
            }
        }
        for (IdentifierContext ic : mcc.getIdentifierContext()) {
            System.out.println("\tIdentifier: " + ic.toString());
        }        

        for (ClassContext cc : c.getClassContext()) {
            System.out.println("Class: " + cc.toString());

            for (MethodContext mc : cc.getMethodContext()) {
                System.out.println("\tMethod: " + mc.toString());

                for (IdentifierContext ic : mc.getIdentifierContext()) {
                    System.out.println("\t\tIdentifier: " + ic.toString());
                }
          }
          for (IdentifierContext ic : cc.getIdentifierContext()) {
              System.out.println("\tIdentifier: " + ic.toString());
          }

        }

        //System.out.println("Program parsed successfully");
  }
/**
 * Grammar production:
 * f0 -> "if"
 * f1 -> "("
 * f2 -> Expression()
 * f3 -> ")"
 * f4 -> Statement()
 * f5 -> "else"
 * f6 -> Statement()
 */
/*
  public AllType visit(IfStatement ifs, Map<String, Type> a) {
    // we know the grammar is correct but we want to type check.
    // IfStatements do not have a type so we just want to make sure
    // every subcomponent type checks.
    ifs.f2.accept(this, a);
    ifs.f4.accept(this, a);
    ifs.f6.accept(this, a);

    // if statements don't have a type themselves, so we can return null here.
    // this either returns null (success) or throws error (fail)
    return null;
  }


  // Primary Expressions
   

  // this is an IntegerLiteral, nothing more to check.
  public AllType visit(IntegerLiteral il, Map<String, Type> a) {
    return new AllType("int");
  }

  //this is an integer type. nothing left to do.
  public AllType visit(IntegerType it, Map<String, Type> a) {
    return new AllType("int");
  }

  //this is an boolean type. nothing left to do.
  public AllType visit(TrueLiteral tl, Map<String, Type> a) {
    return new AllType("boolean");
  }

    //this is an boolean type. nothing left to do.
  public AllType visit(FalseLiteral fl, Map<String, Type> a) {
    return new AllType("boolean");
  }

  public AllType visit(Identifier i, Map<String, Type> a){
    String id = i.f0.toString();
    Type t = a.get(id);
    if (t == null) {
      throw new TypeException("Could not find " + id + " in A.");
    }
    else
      return new AllType(t.f0.toString());
  }

  
   // Grammar production:
   // f0 -> "class"
   // f1 -> Identifier()
   // f2 -> "{"
   // f3 -> "public"
   // f4 -> "static"
   // f5 -> "void"
   // f6 -> "main"
   // f7 -> "("
   // f8 -> "String"
   // f9 -> "["
   // f10 -> "]"
   // f11 -> Identifier()
   // f12 -> ")"
   // f13 -> "{"
   // f14 -> ( VarDeclaration() )*
   // f15 -> ( Statement() )*
   // f16 -> "}"
   // f17 -> "}"
   
  // TODO implement this. we need to check to see if the class is declared inside map.
  public Type visit(MainClass mc, Map<String, Type> a) {
      n.f1.accept(this, argu); //ident
      n.f11.accept(this, argu); // ident
      n.f14.accept(this, argu); // vardeclaration
      n.f15.accept(this, argu); // statements
  }



 // Grammar production:
 // f0 -> MainClass()
 // f1 -> ( TypeDeclaration() )*
 // f2 -> <EOF>
  public Type visit(Goal g, Map<String, Type> a) {
    return g.f0.accept(this, a);
  }

  public Type visit(Const c,  Map<String, Type> a) {
    return Type.N;
  }

  public Type visit(Prim p, Map<String, Type> a ){
    return p.f0.accept(this, a);
  }

  public Type visit(Paren p, Map<String, Type> a) {
    return p.f1.accept(this, a);
  }

  public Type visit(Expr e, Map<String, Type> a) {
    Type t = null;
    for (Node n: e.f0.nodes) {
      if (t == null) {
        t = n.accept(this, a);
      } else {
        t = t.apply(n.accept(this, a));
      }
    }
    return t;
  }

  public Type visit(Abs abs, Map<String, Type> a) {
    final Map<String, Type> anew = new HashMap(a);
    final Set<String> ids = new HashSet<String>();
    final ArrayList<Type> types = new ArrayList<Type>();

    abs.f1.accept(new DepthFirstVisitor () {
        public void visit(Arg arg) {
          String id = arg.f0.f0.toString();

          final ArrayList<Type> argTypes = new ArrayList<Type>();
          // a: A -> B -> C
          arg.f2.accept(new DepthFirstVisitor () {
            public void visit (Ident i) {
              argTypes.add(new ConstType(i.f0.toString()));
            }
          });

          Type type = Type.fromList(argTypes);

          if (ids.contains(id)) {
            throw new TypeException("" + id + " occurring more than once in function");
          } else {
            ids.add(id);
            anew.put(id, type);
            types.add(type);
          }
        }
    });

    types.add(abs.f3.accept(this, anew));

    return Type.fromList(types);
  }
}

class Type {
  static final Type N = new ConstType("N");

  public Type apply(Type t2) {
    if (this instanceof ArrType) {
      ArrType arr = (ArrType) this;
      // arr = (N -> N) -> N
      // t2 = N -> N
      // -> N
      if (arr.from.equals(t2)) {
        return arr.to;
      } else {
        throw new TypeException("Couldn't match " + t2 + " with " + arr.from  + "in " + this);
      }
    } else {
      throw new TypeException("Can't apply " + t2 + " to " + this);
    }
  }

  public static Type fromList(List<Type> types) {
    switch (types.size()) {
    case 0:
      throw new TypeException("Could not create type from nothing");
    case 1:
      return types.get(0);
    default:
      return new ArrType(types.get(0), Type.fromList(types.subList(1, types.size())));
    }
  }
}

class ConstType extends Type {
  final String name;

  public ConstType (String name) {
    this.name = name;
  }

  public boolean equals(Object o) {
    if (o != null && o instanceof ConstType) {
      return name.equals(((ConstType)o).name);
    } else {
      return false;
    }
  }

  public String toString () {
    return name;
  }
}

// subclass of Type that just holds a string.
class AllType extends Type {
  final String name;

  public AllType (String name) {
    this.name = name;
  }

  public boolean equals(Object o) {
    if (o != null && o instanceof AllType) {
      return name.equals(((AllType)o).name);
    } else {
      return false;
    }
  }

  public String toString () {
    return name;
  }
}


// A Type class for BooleanType (Node)
class BoolType extends Type {

  public boolean equals(Object o) {
    if (o != null && o instanceof BoolType) {
        return true;
    } else {
        return false;
    }
  }

  public boolean toString () {
    return "boolean";
  }
}


// A Type class for IntegerType (Node)
class IntType extends Type {
  final String name;

  public IntType (String name) {
    this.name = name;
  }

  public boolean equals(Object o) {
    if (o != null && o instanceof IntType) {
      return name.equals(((IntType)o).name);
    } else {
      return false;
    }
  }

  public String toString () {
    return name;
  }
}

// from -> to
// [ a: T, b: T. a]: T -> T -> T === T -> ( T -> T)
//                  (T -> T) -> T =/= T -> T ->T
// [ f: N -> N. f 1]: (N -> N) -> N
class ArrType extends Type {
  final Type from;
  final Type to;

  public ArrType(Type from, Type to) {
    this.from = from;
    this.to = to;
  }

  public boolean equals(Object o) {
    if (o != null && o instanceof ArrType) {
      ArrType t = (ArrType) o;
      return from.equals(t.from) && to.equals(t.to);
    } else {
      return false;
    }
  }

  public String toString () {
    if (from instanceof ArrType )
      return "(" + from.toString() + ") -> " + to.toString();
    else
      return from.toString() + " -> " + to.toString();
  }
  */
}

class TypeException extends RuntimeException {
  public TypeException (String msg) {
    super(msg);
  }
}