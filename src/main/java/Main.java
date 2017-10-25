import syntaxtree.*;
import visitor.*;
import typeenvironment.*;

public class Main { 
    
    public static void main (String [] args) {
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
            System.out.println("Another Error");
        }

        System.out.println("Program parsed successfully");
    }

}
