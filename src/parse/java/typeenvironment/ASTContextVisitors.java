package visitor;

import syntaxtree.*;
import typeenvironment.*;

public class ASTContextVisitors extends GJDepthFirst<Context, Context> {

    public void ASTContextVisitors() {
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
    public Context visit(MainClass mc, Context context) {
        System.out.println("In ASTContextVisitors::visit(MainClass)");
        // get classname, create a ClassContext, and add it to parent context.
        String classname = mc.f1.accept(this, context).toString();
        MainClassContext cContext = new MainClassContext(classname);
        context.add(cContext);

        // main method is not tokenized like other methods so we need to add it
        // to the context manually.
        MethodContext mainMethodContext = new MethodContext(mc.f6.toString());
        cContext.add(mainMethodContext);

        // get parameter name, create IdentifierContext, and add it to the main method context.
        String parametername = mc.f11.accept(this, mainMethodContext).toString();
        IdentifierContext param = new IdentifierContext(parametername);
        mainMethodContext.add(param);

        // we don't need to worry about this because VarDeclarations append to the context for us!
        mc.f14.accept(this, mainMethodContext);
        mc.f15.accept(this, mainMethodContext);

        return null;
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
    public Context visit(ClassDeclaration cd, Context context) {
        System.out.println("In ASTContextVisitors::visit(ClassDeclaration)");
        String classname = cd.f1.accept(this, context).toString();
        ClassContext cContext = new ClassContext(classname);
        context.add(cContext);

        // dont care about retval because we add things to context inside of here.
        cd.f3.accept(this, cContext);
        cd.f4.accept(this, cContext);

        return null;
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
    public Context visit(MethodDeclaration md, Context context) {
        System.out.println("In ASTContextVisitors::visit(MethodDeclaration)");
        //System.out.println(md.f2.f0.getClass().getName());

        // we dont need to go into the f0 is a NodeToken. We don't need to go into it, we get get
        // the string from here.
        String methodname = md.f2.f0.toString();
        MethodContext mContext = new MethodContext(methodname);
        context.add(mContext);

        md.f7.accept(this, mContext);

        // we dont do anything else becuase we only care about parsing the
        // declarations.
        return null;
    }

    /**
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public Context visit(FormalParameterList fpl, Context context) {
        System.out.println("In ASTContextVisitors::visit(FormalParameterList)");
        //get the name of the formal parameter and create an IdentifierContext.
        String firstParameterName = fpl.f0.f1.accept(this, context).toString();
        IdentifierContext fpContext = new IdentifierContext(firstParameterName);
        context.add(fpContext);

        // This might cause issues if there are no additional parameters (causing the param list to not be init.)
        for (int i = 0; i < fpl.f1.size(); i++) {
            fpl.f1.elementAt(i).accept(this, context);
        }

        return null;
    }


    // we add nothing to the context because we aren't instantiating anything here.
    public Context visit(Identifier id, Context context) {
        System.out.print("In ASTContextVisitors::visit(Identifier)");
        IdentifierContext idContext = new IdentifierContext(id.f0.toString());
        System.out.println(" : Got Identifier " + id.f0.toString());
        return idContext;
    }

    /**
     * Grammar production:
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    // return null because we add things to context.
    public Context visit(VarDeclaration cd, Context context) {
        System.out.println("In ASTContextVisitors::visit(VarDeclaration)");
        // get the name of the identifier and add it to the context.
        String idname = cd.f1.accept(this, context).toString();
        IdentifierContext var = new IdentifierContext(idname);
        context.add(var);

        return null;
    }

    public Context visit(Node n, Context context) {
        System.out.println("In ASTContextVisitors::visit(Node)");
        return null;
    }

}