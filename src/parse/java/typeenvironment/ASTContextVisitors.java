package visitor;

import syntaxtree.*;
import typeenvironment.*;


/*
 * This visitor traverses the AST and TypeChecks. We assume that
 * the context passed in already has been populated with the variable,
 * method, and class declarations.
 */
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
        // get the mainClass context, then the MainMethod context.
        String classname = mc.f1.accept(this, context).toString();
        Context mainClassContext = context.getChildContext(classname);
        if (mainClassContext == null) {
            return "ERROR";
        }
        Context mainMethodContext = mainClassContext.getChildContext("main");
        if (mainMethodContext == null) {
            return "ERROR";
        }

        // use the mainmethod context to type check the statements within main method.
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

        // this should be a class context.
        Context cContext = context.getChildContext(classname);

        // if we cant find this class then we have failed the TypeChecker,
        if (cContext == null) {
            context.typeFailed();
            return "ERROR";
        }

        if(cd.f4.accept(this, cContext) == "ERROR") {
            context.typeFailed();
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

        // this should be a method context.
        Context mContext = context.getChildContext(methodname);

        // this cant fail because we must have added this method to context
        // before but lets check anyway because we are good coders who use
        // defensive coding practices.
        if (mContext == null) {
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
            System.out.println("NotExpression failed");
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

    // return the name of the identifier.
    public String visit(Identifier id, Context context) {
        return id.f0.toString();
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
    public String visit(IfStatement is, Context context) {
        String conditionType = is.f2.accept(this, context);
        if (conditionType != "boolean") {
            System.out.println("IfStatement failed.");
            context.typeFailed();
        }
        is.f4.accept(this, context);
        is.f6.accept(this, context);
        return "";
    }

    /**
     * Grammar production:
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement ws, Context context) {
        String conditionType = ws.f2.accept(this, context);
        if (conditionType != "boolean") {
            System.out.println("WhileStatement failed.");
            context.typeFailed();
        }
        ws.f4.accept(this, context);
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
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend ms, Context context) {
        String primaryExpressionType = ms.f0.accept(this, context);
        String methodName = ms.f2.accept(this, context);

        MethodContext primaryExpressionMethodContext = context.getClassMethodContext(primaryExpressionType, methodName);
        if (primaryExpressionMethodContext == null) {
            return "ERROR";
        }
        return primaryExpressionMethodContext.toType();
        // TODO methods need to be overloadable.
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
            System.out.println("PrimaryExpression failed");
            context.typeFailed();
            return "ERROR";
        }

        // if this is an identifier get its type from name.
        if (pe.f0.choice instanceof Identifier) {
            ret = context.find(ret);
            if (ret == "ERROR") {
                System.out.println("PrimaryExpression failed");
                context.typeFailed();
                return ret;
            }
        }

        return ret;
    }

    /**
     * Grammar production:
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup al, Context context) {
        String arrayType = al.f0.accept(this, context);
        String indexType = al.f2.accept(this, context);

        if (arrayType != "int[]" || indexType != "int") {
            System.out.println("ArrayLookup failed with array:" + 
                arrayType + " and index:" + indexType);
            context.typeFailed();
            return null;
        }
        return "int";
    }

    /**
     * Grammar production:
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement ps, Context context) {
        String exprType = ps.f2.accept(this, context);
        if (exprType == "int") {
            return "";
        }
        System.out.println("PrintStatement failed.");
        context.typeFailed();
        return null;
    }

    /**
     * Grammar production:
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression be, Context context) {
        return be.f1.accept(this, context);
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
            System.out.println("AndExpression failed");            
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
        System.out.println("CompareExpression failed");
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
        System.out.println("PlusExpression failed");
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
        System.out.println("Minus failed");
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
        System.out.println("TimesExpression failed");
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
            System.out.println("AssignmentStatement failed.");
            context.typeFailed();
            return "ERROR";
        }
        // statement type checked but statements dont have a type themselves.
        else if (identifier_type == expression_type) {
            return "";
        }
        else {
            System.out.println("AssignmentStatement failed. LHS is " +
                identifier_type + " and RHS is " + expression_type + 
                " within context " + context.toString());
            context.typeFailed();
            return "ERROR";
        }
    }
}