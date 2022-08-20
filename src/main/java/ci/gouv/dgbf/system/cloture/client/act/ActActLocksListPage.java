package ci.gouv.dgbf.system.cloture.client.act;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.cloture.server.api.service.ActDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import ci.gouv.dgbf.system.cloture.server.client.rest.ActController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActActLocksListPage extends AbstractPageContainerManagedImpl implements Serializable{

	private Act act;
	private DataTable locksDataTable;
	private ActReadController readController;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		Controller.GetArguments arguments = new Controller.GetArguments();
		//arguments.projections(ActDto.JSON_IDENTIFIER,ActDto.JSONS_CODE_NAME_TYPE_STRING_NUMBER_OF_LOCKS_ENABLED_STATUS_STRING_LATEST_OPERATION,ActDto.JSON_NUMBER_OF_LOCKS_ENABLED
		//		,ActDto.JSON_LOCKED_REASONS);
		readController = new ActReadController();
		readController.setAct(__inject__(ActController.class).getByIdentifier(WebController.getInstance().getRequestParameter(ParameterName.ENTITY_IDENTIFIER),arguments));
		readController.setLocksDataTable(ActLockListPage.buildDataTable(ActLockFilterController.class,new ActLockFilterController().setActInitial(readController.getAct())));
		readController.initialize();
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Liste des verrous";
	}
	
	public static final String OUTCOME = "actActLocksListView";
}