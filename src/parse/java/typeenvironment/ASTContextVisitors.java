package visitor;

import syntaxtree.*;
import typeenvironment.*;

public class ASTContextVisitors extends GJDepthFirst<String, Context> {

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

        // we don't need to worry about this because VarDeclarations append to the context for us!
        if(mc.f14.accept(this, mainMethodContext) == "ERROR") {
            return "ERROR";
        }
        if(mc.f15.accept(this, mainMethodContext) == "ERROR") {
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

        // vardeclarations add themselves to context.
        if(md.f7.accept(this, mContext) == "ERROR") {
            context.typeFailed();
            return "ERROR";
        }
        // statements
        if(md.f8.accept(this, mContext) == "ERROR") {
            context.typeFailed();
            return "ERROR";
        }
        // expression
        String returntype = md.f10.accept(this, mContext);
        if(returntype == "ERROR" || returntype != methodtype) {
            context.typeFailed();
            return "ERROR";
        }

        return "";
    }

    // return this type, which is int.
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
     * f0 -> "!"
     * f1 -> Expression()
     */
    public String visit(NotExpression ne, Context context) {
        String exprType = ne.f1.accept(this, context);
        if (exprType != "boolean") {
            context.typeFailed();
            return "ERROR";
        }
        return "boolean";
    }

    /**
     * Grammar production:
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression ae, Context context) {
        String id = ae.f1.accept(this, context);
        return context.find(id);
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

    /**
     * Grammar production:
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public String visit(FormalParameterList fpl, Context context) {
        // FormalParameter will add itself to the context.
        fpl.f0.accept(this, context);
        fpl.f1.accept(this, context);

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

    /**
     * Grammar production:
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    public String visit(Expression e, Context context) {
        return e.f0.accept(this, context);
    }

    /**
     * Grammar production:
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    // return the type of the PrimaryExpression.
    public String visit(PrimaryExpression pe, Context context) {
        String ret = pe.f0.accept(this, context);
        if (ret == "ERROR") {
            context.typeFailed();
            return "ERROR";
        }

        // if this is an identifier get its type from name.
        if (pe.f0.choice instanceof Identifier) {
            ret = context.find(ret);
            if (ret == "ERROR") {
                context.typeFailed();
                return ret;
            }
        }

        return ret;
    }

    public String visit(NodeChoice nc, Context context) {
        return nc.choice.accept(this, context);
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public String visit(AndExpression ae, Context context) {
        String type1 = ae.f0.accept(this, context);
        String type2 = ae.f2.accept(this, context);

        if (type1 == "boolean" && type2 == "boolean") {
            return "boolean";
        }
        else {
            context.typeFailed();
            return "ERROR";
        }
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression ce, Context context) {
        String T1 = ce.f0.accept(this, context);
        String T2 = ce.f2.accept(this, context);

        if (T1 == "int" && T2 == "int") {
            return "boolean";
        }
        context.typeFailed();
        return "ERROR";
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression pe, Context context) {
        String T1 = pe.f0.accept(this, context);
        String T2 = pe.f2.accept(this, context);

        if (T1 == "int" && T2 == "int") {
            return "int";
        }
        context.typeFailed();
        return "ERROR";
    }


    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression pe, Context context) {
        String T1 = pe.f0.accept(this, context);
        String T2 = pe.f2.accept(this, context);

        if (T1 == "int" && T2 == "int") {
            return "int";
        }
        context.typeFailed();
        return "ERROR";
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression pe, Context context) {
        String T1 = pe.f0.accept(this, context);
        String T2 = pe.f2.accept(this, context);

        if (T1 == "int" && T2 == "int") {
            return "int";
        }
        context.typeFailed();
        return "ERROR";
    }

    /**
     * Grammar production:
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement as, Context context) {
        String identifier_type = context.find(as.f0.accept(this, context));
        String expression_type = as.f2.accept(this, context);

        if (identifier_type == "ERROR" || expression_type == "ERROR") {
            context.typeFailed();
            return "ERROR";
        }
        // statement type checked but statements dont have a type themselves.
        else if (identifier_type == expression_type) {
            return "";
        }
        else {
            context.typeFailed();
            return "ERROR";
        }
    }
}