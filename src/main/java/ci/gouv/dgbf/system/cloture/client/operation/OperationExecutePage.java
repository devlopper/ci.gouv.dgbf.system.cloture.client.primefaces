package ci.gouv.dgbf.system.cloture.client.operation;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.service.client.Controller.GetArguments;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class OperationExecutePage extends AbstractPageContainerManagedImpl implements Serializable {

	private Operation operation;
	@Inject private OperationController controller;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		String identifier = WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER);
		if(StringHelper.isNotBlank(identifier)) {
			operation = controller.getByIdentifier(identifier, new GetArguments().projections(OperationDto.JSON_IDENTIFIER,OperationDto.JSON_CODE
					,OperationDto.JSON_NAME,OperationDto.JSON_EXECUTION_BEGIN_DATE_STRING,OperationDto.JSON_EXECUTION_END_DATE_STRING,OperationDto.JSON_EXECUTION_STATUS
					,OperationDto.JSON_PROCEDURE_NAME,OperationDto.JSON_START_DATE_STRING,OperationDto.JSON_TRIGGER));
			// ResponseHelper.getEntityFromJson(Operation.class, response);
		}	
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Exécution de l'opération : "+(operation == null ? "" : operation.getName());
	}
	
	public void execute() {
		try {
			controller.execute(operation, Boolean.FALSE);
			Redirector.getInstance().redirect(new Redirector.Arguments().setOutcome(OperationListPage.OUTCOME));			
		} catch (Exception exception) {
			__inject__(MessageRenderer.class).render(exception.getMessage(),Severity.ERROR,List.of(RenderType.INLINE, RenderType.DIALOG));
		}
	}
	
	/**/
	
	public static final String OUTCOME = "operationExecuteView";
}