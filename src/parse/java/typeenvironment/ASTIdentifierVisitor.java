package visitor;

import syntaxtree.*;
import typeenvironment.*;


/*
 * This visitor traverses the AST and finds all the identifiers, methods, classes.
 * and populates a context with them.
 */
public class ASTIdentifierVisitor extends GJDepthFirst<String, Context> {

    public void ASTIdentifierVisitor() {
        //nothing
    }

    //that should be a GoalContext.
    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"   
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass mc, Context context) {
        // get classname, create a ClassContext, and add it to parent context.
        String classname = mc.f1.accept(this, context).toString();
        MainClassContext cContext = new MainClassContext(classname);
        context.add(cContext);

        // main method is not tokenized like other methods so we need to add it
        // to the context manually.
        MethodContext mainMethodContext = new MethodContext(mc.f6.toString(), "void");
        cContext.add(mainMethodContext);

        // get parameter name, create IdentifierContext, and add it to the main method context.
        String parametername = mc.f11.accept(this, mainMethodContext).toString();
        IdentifierContext param = new IdentifierContext(parametername, "String");
        mainMethodContext.add(param);

        // parse var declarations.
        if(mc.f14.accept(this, mainMethodContext) == "ERROR") {
            return "ERROR";
        }

        return "";
    }

    /**
     * Grammar production:
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration cd, Context context) {
        String classname = cd.f1.accept(this, context).toString();
        ClassContext cContext = new ClassContext(classname);
        context.add(cContext);

        // dont care about retval because we add things to context inside of here.
        if(cd.f3.accept(this, cContext) == "ERROR") {
            return "ERROR";
        }
        if(cd.f4.accept(this, cContext) == "ERROR") {
            return "ERROR";
        }

        return "";
    }

    /**
     * Grammar production:
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration md, Context context) {
        String methodtype = md.f1.accept(this, context);
        String methodname = md.f2.accept(this, context);
        MethodContext mContext = new MethodContext(methodname, methodtype);
        context.add(mContext);

        // add formalParameters to the MethodContext.
        md.f4.accept(this, mContext);

        // vardeclarations add themselves to context.
        if(md.f7.accept(this, mContext) == "ERROR") {
            return "ERROR";
        }

        return "";
    }

    /**
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public String visit(FormalParameterList fpl, Context context) {
        System.out.println("FormalParameterList called");
        // FormalParameter will add itself to the context.
        fpl.f0.accept(this, context);
        // NodeListOptional
        fpl.f1.accept(this, context);

        return "";
    }

    /*
     * NodeListOptional has a list of nodes in a vector.
     *  we want to visit every node in this vector.
     */
    public String visit(NodeListOptional nlo, Context context) {
        for (int i = 0; i < nlo.size(); i++) {
            nlo.elementAt(i).accept(this, context);
        }
        return "";
    }

    /**
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     */
    // This adds to the context. we return "" because a FP does not have a type.
    // the thing it declared does but the statement itself does not.
    public String visit(FormalParameter fp, Context context) {
        System.out.println("FormalParameter called");
        String type = fp.f0.accept(this, context);
        String name = fp.f1.accept(this, context);
        IdentifierContext id = new IdentifierContext(name, type);
        context.add(id);

        return "";
    }

    /**
     * Grammar production:
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterRest fpr, Context context) {
        // formalParameter adds itself to context.
        fpr.f1.accept(this, context);

        // return "" because a FPR does not have a type itself, it only declares
        // identifiers with types and adds them to context.
        return "";
    }


    // return the name of the identifier.
    public String visit(Identifier id, Context context) {
        return id.f0.toString();
    }

    /**
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    // return "" because we add things to context.
    public String visit(VarDeclaration cd, Context context) {
        String type = cd.f0.accept(this, context);
        String idname = cd.f1.accept(this, context);

        IdentifierContext var = new IdentifierContext(idname, type);
        context.add(var);

        return "";
    }

    // we need to know the types of the identifiers so we can
    // add it to the context correctly.
    public String visit(IntegerType t, Context context) {
        return "int";
    }

    public String visit(BooleanType t, Context context) {
        return "boolean";
    }

    public String visit(ArrayType a, Context context) {
        return "int[]";
    }

    public String visit(IntegerLiteral il, Context context) {
        return "int";
    }

    public String visit(FalseLiteral fl, Context context) {
        return "boolean";
    }

    public String visit(TrueLiteral tl, Context context) {
        return "boolean";
    }

    public String visit(ThisExpression te, Context context) {
        return context.find("this");
    }

    public String visit(ArrayAllocationExpression aae, Context context) {
        return "int[]";
    }

    /**
     * Grammar production:
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type t, Context context) {
        return t.f0.accept(this, context);
    }
}