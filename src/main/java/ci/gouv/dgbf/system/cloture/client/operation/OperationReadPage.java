package ci.gouv.dgbf.system.cloture.client.operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Layout;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.TabMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.TabMenu.Tab;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.output.OutputText;
import org.cyk.utility.service.client.Controller;

import ci.gouv.dgbf.system.cloture.client.act.ActFilterController;
import ci.gouv.dgbf.system.cloture.client.act.ActListPage;
import ci.gouv.dgbf.system.cloture.client.imputation.ImputationFilterController;
import ci.gouv.dgbf.system.cloture.client.imputation.ImputationListPage;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Act;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class OperationReadPage extends AbstractPageContainerManagedImpl implements Serializable {

	private Operation operation;
	private OperationReadController readController;
	private TabMenu tabMenu;
	private Layout layout;
	
	@Override
	protected void __listenPostConstruct__() {
		super.__listenPostConstruct__();
		operation = __inject__(OperationController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER)
				,new Controller.GetArguments().projections(OperationDto.JSON_IDENTIFIER,OperationDto.JSONS_STRINGS,OperationDto.JSONS_STATUSES,OperationDto.JSON___AUDIT__));
		if(operation == null)
			return;
		Collection<Map<Object,Object>> cellsMaps = new ArrayList<>();
		buildTabMenu(cellsMaps);
		buildTab(cellsMaps);
		buildLayout(cellsMaps);
	}
	
	private void buildTabMenu(Collection<Map<Object,Object>> cellsMaps) {		
		tabMenu = TabMenu.build(TabMenu.ConfiguratorImpl.FIELD_ITEMS_OUTCOME,OUTCOME,TabMenu.ConfiguratorImpl.FIELD_TABS,TABS,TabMenu.ConfiguratorImpl.FIELD_TAB_MENU_ITEM_BUILDER,new TabMenu.Tab.MenuItemBuilder.AbstractImpl() {
			@Override
			protected void process(TabMenu tabMenu, Tab tab, MenuItem item) {
				super.process(tabMenu, tab, item);
				
			}
		});
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,tabMenu,Cell.FIELD_WIDTH,12));
	}
	
	private void buildTab(Collection<Map<Object,Object>> cellsMaps) {
		if(tabMenu.getSelected().getParameterValue().equals(TAB_SUMMARY))
			buildTabSummary(cellsMaps);
		else if(tabMenu.getSelected().getParameterValue().equals(TAB_ACTS))
			buildTabActs(cellsMaps);
	}
	
	private void buildTabSummary(Collection<Map<Object,Object>> cellsMaps) {
		OperationReadController readController = new OperationReadController(operation);
		readController.initialize();
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,readController.getLayout(),Cell.FIELD_WIDTH,12));
		
		if(Boolean.TRUE.equals(operation.getCreated()))
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,CommandButton.build(CommandButton.FIELD_VALUE,"Exécuter",CommandButton.FIELD_ICON,"fa fa-gear"
					,CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
					,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_NULLABLE,Boolean.TRUE
					,CommandButton.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE
					,CommandButton.FIELD_LISTENER
					,new AbstractAction.Listener.AbstractImpl() {
				@Override
				protected Object __runExecuteFunction__(AbstractAction action) {
					__inject__(OperationController.class).startExecution(operation);
					__inject__(Redirector.class).redirect(OUTCOME, Map.of(Parameters.OPERATION_IDENTIFIER,List.of(operation.getIdentifier())));
					return null;
				}
			}),Cell.FIELD_WIDTH,12));
		
		ActFilterController actsFilterController = new ActFilterController();
		actsFilterController.setOperationInitial(operation);
		actsFilterController.setAddedToSelectedOperationInitial(Boolean.TRUE);
		actsFilterController.setRenderType(AbstractFilterController.RenderType.NONE);
		DataTable actsDataTable = ActListPage.buildDataTable(ActFilterController.class,actsFilterController,ActListPage.class,this/*,ActListPage.ADD_OR_REMOVE_ACT_COMMAND_USER_INTERFACE_ACTION,UserInterfaceAction.OPEN_VIEW_IN_DIALOG*/);
		actsDataTable.setTitle(OutputText.buildFromValue(ci.gouv.dgbf.system.cloture.server.api.persistence.Act.NAME_PLURAL));
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,actsDataTable,Cell.FIELD_WIDTH,12));
		
		ImputationFilterController imputationFilterController = new ImputationFilterController();
		imputationFilterController.setOperationInitial(operation);
		imputationFilterController.setAddedToSelectedOperationInitial(Boolean.TRUE);
		imputationFilterController.setRenderType(AbstractFilterController.RenderType.NONE);
		DataTable imputationsDataTable = ImputationListPage.buildDataTable(ImputationFilterController.class,imputationFilterController,ImputationListPage.class,this/*,ActListPage.ADD_OR_REMOVE_ACT_COMMAND_USER_INTERFACE_ACTION,UserInterfaceAction.OPEN_VIEW_IN_DIALOG*/);
		imputationsDataTable.setTitle(OutputText.buildFromValue(ci.gouv.dgbf.system.cloture.server.api.persistence.Imputation.NAME_PLURAL));
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,imputationsDataTable,Cell.FIELD_WIDTH,12));
	}
	
	private void buildTabActs(Collection<Map<Object,Object>> cellsMaps) {
		/*regulatoryActFilterController = new RegulatoryActFilterController();
		regulatoryActFilterController.setOperationInitial(legislativeAct);
		//HttpServletRequest request = __inject__(HttpServletRequest.class);
		//if(!request.getParameterMap().keySet().contains(Parameters.REGULATORY_ACT_INCLUDED))
		//	regulatoryActFilterController.setIncludedInitial(Boolean.TRUE);
		regulatoryActFilterController.setReadOnlyByFieldsNames(RegulatoryActFilterController.FIELD_LEGISLATIVE_ACT_SELECT_ONE);
		regulatoryActFilterController.getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(TabMenu.Tab.PARAMETER_NAME, TAB_REGULATORY_ACTS);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,RegulatoryActListPage.buildDataTable(RegulatoryActFilterController.class,regulatoryActFilterController,RegulatoryActListPage.OUTCOME,OUTCOME)));
		*/
	}
	
	private void buildLayout(Collection<Map<Object,Object>> cellsMaps) {
		layout = Layout.build(Layout.FIELD_CELL_WIDTH_UNIT,Cell.WidthUnit.FLEX,Layout.ConfiguratorImpl.FIELD_CELLS_MAPS,cellsMaps);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		if(operation == null)
			return String.format("Aucune %s trouvée",ci.gouv.dgbf.system.cloture.server.api.persistence.Operation.NAME);
		return ci.gouv.dgbf.system.cloture.server.api.persistence.Operation.NAME+" : "+operation.getName();
	}
	
	public static final String TAB_SUMMARY = "summary";	
	public static final String TAB_ACTS = "actes";
	public static final List<TabMenu.Tab> TABS = List.of(
		new TabMenu.Tab("Récapitulatif",TAB_SUMMARY)
		//,new TabMenu.Tab(ci.gouv.dgbf.system.cloture.server.api.persistence.Act.NAME_PLURAL,TAB_ACTS)
	);

	public static final String OUTCOME = "operationReadView";
}