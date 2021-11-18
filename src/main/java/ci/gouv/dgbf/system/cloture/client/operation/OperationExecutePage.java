package ci.gouv.dgbf.system.cloture.client.operation;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.rest.ResponseHelper;
import org.cyk.utility.service.client.SpecificServiceGetter;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationDto;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationService;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class OperationExecutePage extends AbstractPageContainerManagedImpl implements Serializable {

	private Operation operation;
	@Inject private SpecificServiceGetter specificServiceGetter;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		String identifier = WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER);
		if(StringHelper.isNotBlank(identifier)) {
			Response response = specificServiceGetter.get(Operation.class).getByIdentifier(identifier, List.of(OperationDto.JSON_IDENTIFIER,OperationDto.JSON_CODE
					,OperationDto.JSON_NAME,OperationDto.JSON_EXECUTION_BEGIN_DATE_STRING,OperationDto.JSON_EXECUTION_END_DATE_STRING,OperationDto.JSON_EXECUTION_STATUS
					,OperationDto.JSON_PROCEDURE_NAME,OperationDto.JSON_START_DATE_STRING,OperationDto.JSON_TRIGGER));
			operation = ResponseHelper.getEntityFromJson(Operation.class, response);
		}	
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Exécution de l'opération : "+(operation == null ? "" : operation.getName());
	}
	
	public void execute() {
		OperationService operationService = (OperationService) specificServiceGetter.get(Operation.class);
		try {
			operationService.execute(operation.getIdentifier(), SessionHelper.getUserName(), Boolean.FALSE);
			Redirector.getInstance().redirect(new Redirector.Arguments().setOutcome(OperationListPage.OUTCOME));			
		} catch (WebApplicationException exception) {
			String message = ResponseHelper.getEntity(String.class, exception.getResponse());
			__inject__(MessageRenderer.class).render(message,Severity.ERROR,List.of(RenderType.INLINE, RenderType.DIALOG));
		}
	}
	
	/**/
	
	public static final String OUTCOME = "operationExecuteView";
}