package com.company.demo.app;

import io.jmix.core.querycondition.Condition;
import io.jmix.core.querycondition.LogicalCondition;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.querycondition.PropertyCondition.Operation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleSqlConditionProcessor {

    public record Result(String where, Object[] params) {
    }

    private record OpAndParam(String op, Object param) {
    }

    @Nullable
    public static Result process(Condition root) {
        List<Object> paramsList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        if (root instanceof LogicalCondition rootCondition) {
            for (Iterator<Condition> it = rootCondition.getConditions().iterator(); it.hasNext(); ) {
                Condition c = it.next();
                if (c instanceof PropertyCondition pc) {
                    OpAndParam opAndParam = toSqlOpAndParam(pc);
                    if (opAndParam.param != null) {
                        sb.append("(").append(pc.getProperty());
                        sb.append(" ").append(opAndParam.op).append(" ");
                        sb.append("?)");
                        paramsList.add(opAndParam.param);
                    }
                }
                if (it.hasNext())
                    sb.append(" ").append(rootCondition.getType());
            }
        }
        if (sb.length() > 0)
            return new Result(sb.toString(), paramsList.toArray(new Object[0]));
        else
            return null;
    }

    private static OpAndParam toSqlOpAndParam(PropertyCondition pc) {
        return switch (pc.getOperation()) {
            case Operation.EQUAL -> new OpAndParam("=", pc.getParameterValue());
            case Operation.NOT_EQUAL -> new OpAndParam("<>", pc.getParameterValue());
            case Operation.GREATER -> new OpAndParam(">", pc.getParameterValue());
            case Operation.GREATER_OR_EQUAL -> new OpAndParam(">=", pc.getParameterValue());
            case Operation.LESS -> new OpAndParam("<", pc.getParameterValue());
            case Operation.LESS_OR_EQUAL -> new OpAndParam("<=", pc.getParameterValue());
            case Operation.CONTAINS -> new OpAndParam("like", pc.getParameterValue() == null ? null : "%" + pc.getParameterValue() + "%");
            case Operation.NOT_CONTAINS -> new OpAndParam("not like", pc.getParameterValue() == null ? null : "%" + pc.getParameterValue() + "%");
            case Operation.STARTS_WITH -> new OpAndParam("not like", pc.getParameterValue() == null ? null : "%" + pc.getParameterValue());
            case Operation.ENDS_WITH -> new OpAndParam("not like", pc.getParameterValue() == null ? null : pc.getParameterValue() + "%");
            default -> throw new UnsupportedOperationException("Operation '" + pc.getOperation() + "' is not supported");
        };
    }
}
