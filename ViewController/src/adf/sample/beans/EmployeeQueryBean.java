package adf.sample.beans;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.view.rich.event.QueryEvent;

import oracle.binding.AttributeBinding;
import oracle.binding.OperationBinding;


public class EmployeeQueryBean {
    public EmployeeQueryBean() {
    }

    public void onEmployeeQuery(QueryEvent queryEvent) {
        //default EL string created when dragging the table
        //to the JSF page
        //#{bindings.allEmployeesQuery.processQuery}

        BindingContext bctx = BindingContext.getCurrent();
        DCBindingContainer bindings = (DCBindingContainer) bctx.getCurrentBindingsEntry();

        //access the method bindings to set the bind variables on the ViewCriteria
        OperationBinding rangeStartOperationBinding = bindings.getOperationBinding("setHireDateRangeStart");
        OperationBinding rangeEndOperationBinding = bindings.getOperationBinding("setHireDateRangeEnd");

        // get the start date and end date from the temporary valiables
        AttributeBinding attr = (AttributeBinding) bindings.getControlBinding("startDate1");
        oracle.jbo.domain.Date sd = (oracle.jbo.domain.Date) attr.getInputValue();
        attr = (AttributeBinding) bindings.getControlBinding("endDate1");
        oracle.jbo.domain.Date ed = (oracle.jbo.domain.Date) attr.getInputValue();

        //set the start and end date of the range to search
        rangeStartOperationBinding.getParamsMap().put("value", sd);
        rangeEndOperationBinding.getParamsMap().put("value", ed);

        //set bind variable on the business service
        rangeStartOperationBinding.execute();
        rangeEndOperationBinding.execute();

        invokeMethodExpression("#{bindings.allEmployeesQuery.processQuery}", Object.class, QueryEvent.class, queryEvent);
    }


    // The code below should be in a Utility clas

    public Object invokeMethodExpression(String expr, Class returnType, Class[] argTypes, Object[] args) {

        FacesContext fc = FacesContext.getCurrentInstance();
        ELContext elctx = fc.getELContext();
        ExpressionFactory elFactory = fc.getApplication().getExpressionFactory();
        MethodExpression methodExpr = elFactory.createMethodExpression(elctx, expr, returnType, argTypes);
        return methodExpr.invoke(elctx, args);
    }

    public Object invokeMethodExpression(String expr, Class returnType, Class argType, Object argument) {
        return invokeMethodExpression(expr, returnType, new Class[] { argType }, new Object[] { argument });
    }
}

