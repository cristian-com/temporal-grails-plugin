package grails.plugin.temporal.compiler;

import groovy.transform.CompileStatic;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@CompileStatic
@GroovyASTTransformation
public class ActivityStubInjector extends AbstractASTTransformation {

    private static final ClassNode ACTIVITY_FACTORY = ClassHelper.make(ActivityStubFactoryBridge.class);

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        init(nodes, source);
        if (!(nodes[1] instanceof FieldNode)) {
            throw new GroovyBugError("Somehow the " + ActivityStubInjector.class.getName() +
                    " AST transformation is being applied to something that is not a field.");
        }

        FieldNode field = (FieldNode) nodes[1];
        //AnnotationNode annotation = (AnnotationNode) nodes[0];

        // Only if the
        if (!field.hasInitialExpression()) {
            ClassNode type = field.getType();
            field.setInitialValueExpression(createInitializationExpression(type));
        }
    }

    private StaticMethodCallExpression createInitializationExpression(ClassNode activityType) {
        return GeneralUtils.callX(ACTIVITY_FACTORY,
                "get",
                GeneralUtils.classX(activityType));
    }

}
