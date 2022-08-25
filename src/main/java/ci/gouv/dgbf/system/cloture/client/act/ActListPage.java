package ci.gouv.dgbf.system.cloture.client.act;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.AbstractMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.ContextMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.cloture.client.act.ActFilterController.UsedFor;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.ActDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ActListPage extends AbstractEntityListPageContainerManagedImpl<Act> implements Serializable {

	private ActFilterController filterController;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ActFilterController();
		String usedForName = WebController.getInstance().getRequestParameter(ParameterName.stringify(UsedFor.class));
		if(StringHelper.isNotBlank(usedForName))
			filterController.setUsedFor(UsedFor.valueOf(usedForName));
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue(ci.gouv.dgbf.system.cloture.server.api.persistence.Act.NAME_PLURAL);
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(ActFilterController.class,filterController,ActListPage.class,this);
		//dataTable.setHeaderToolbarLeftCommands(null);
		//dataTable.setRecordMenu(null);
		//dataTable.setRecordCommands(null);
		//dataTable.setMenuColumn(null);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		AbstractPageContainerManagedImpl page = (AbstractPageContainerManagedImpl) MapHelper.readByKey(arguments, ActListPage.class);
		ActFilterController filterController = (ActFilterController) MapHelper.readByKey(arguments, ActFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (ActFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new ActFilterController());		
		filterController.setPage(page);
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Act.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);
		
		if(ActFilterController.UsedFor.ADD_TO_OPERATION.equals(filterController.getUsedFor()) || ActFilterController.UsedFor.REMOVE_FROM_OPERATION.equals(filterController.getUsedFor())) {
			MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_SELECTION_MODE, "multiple");
		}
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("50");
		
		filterController.setDataTable(dataTable);
		
		if(filterController.getOperationInitial() != null && page != null && !Boolean.TRUE.equals(page.getIsRenderTypeDialog())) {
			Map<String, List<String>> parameters = filterController.asMap();
			UserInterfaceAction addOrRemoveActCommandUserInterfaceAction = (UserInterfaceAction) MapHelper.readByKey(arguments, ADD_OR_REMOVE_ACT_COMMAND_USER_INTERFACE_ACTION);
			addHeaderAddOrRemoveActCommand(dataTable, Boolean.TRUE, parameters,addOrRemoveActCommandUserInterfaceAction);
			addHeaderAddOrRemoveActCommand(dataTable, Boolean.FALSE, parameters,addOrRemoveActCommandUserInterfaceAction);
			//dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD___OUTCOME__,OUTCOME,MenuItem.FIELD___PARAMETERS__,parameters
			//		, MenuItem.FIELD_VALUE,"Ajouter",MenuItem.FIELD_ICON,"fa fa-plus",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.OPEN_VIEW_IN_DIALOG);
		}
		
		/*
		addHeaderAddOrRemoveActCommand(dataTable, Boolean.TRUE);
		addHeaderAddOrRemoveActCommand(dataTable, Boolean.FALSE);
		*/
		/*
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD_VALUE,"Déverouiller les actes filtrés",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,MenuItem.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE,MenuItem.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES
				,List.of(RenderType.GROWL),MenuItem.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {				
				if(StringHelper.isBlank(((LazyDataModel)dataTable.getValue()).getFilterController().getCodes()))
					throw new RuntimeException("Filtrer les actes à déverouiller");
				Collection<String> codes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(((LazyDataModel)dataTable.getValue()).getFilterController().getCodes(),","));
				Response response = DependencyInjection.inject(ActController.class).unlockByIdentifiers(codes,Boolean.TRUE);
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		*/
		/*
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Vérouiller", "fa fa-lock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				DependencyInjection.inject(ActController.class).lock((Act)action.readArgument());
				return null; 
			}
		});
		*/
		/*dataTable.addRecordMenuItemByArgumentsExecuteFunction("Dévérouiller", "fa fa-unlock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				DependencyInjection.inject(ActController.class).unlock(Boolean.FALSE,(Act)action.readArgument());
				return null;
			}
		});*/
		//dataTable.addRecordMenuItemByArgumentsOpenViewInDialog(ActActLocksListPage.OUTCOME, MenuItem.FIELD_VALUE,"Verrous",MenuItem.FIELD_ICON,"fa fa-eye");
		dataTable.setEntityIdentifierParameterName(Parameters.ACT_IDENTIFIER);
		
		return dataTable;
	}
	
	public static void addHeaderAddOrRemoveActCommand(DataTable dataTable,Boolean add,Map<String, List<String>> parameters,UserInterfaceAction userInterfaceAction) {
		Map<String, List<String>> lParameters = parameters == null ? new HashMap<>() : new HashMap<>(parameters);
		lParameters.put(Parameters.ACT_ADDED_TO_SPECIFIED_OPERATION, List.of((add ? Boolean.FALSE : Boolean.TRUE).toString()));
		lParameters.put(ParameterName.stringify(UsedFor.class), List.of(add ? UsedFor.ADD_TO_OPERATION.name() : UsedFor.REMOVE_FROM_OPERATION.name()));
		String label = (Boolean.TRUE.equals(add) ? "Ajouter" : "Retirer");
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD___OUTCOME__,OUTCOME,MenuItem.FIELD___PARAMETERS__,lParameters
				, MenuItem.FIELD_VALUE,label,MenuItem.FIELD_ICON,"fa fa-"+(Boolean.TRUE.equals(add) ? "plus" : "minus"),MenuItem.FIELD_USER_INTERFACE_ACTION,ValueHelper.defaultToIfNull(userInterfaceAction,UserInterfaceAction.OPEN_VIEW_IN_DIALOG));
		/*
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD_VALUE,(Boolean.TRUE.equals(add) ? "Ajouter" : "Retirer")+" les actes filtrés",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,MenuItem.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE,MenuItem.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES
				,List.of(RenderType.GROWL),MenuItem.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				ActFilterController filterController = ((LazyDataModel)dataTable.getValue()).getFilterController();
				Filter.Dto filter = ActFilterController.instantiateFilter(filterController, Boolean.FALSE);
				OperationController controller = DependencyInjection.inject(OperationController.class);
				Boolean existingIgnorable = Boolean.TRUE;
				Response response = Boolean.TRUE.equals(add) ? controller.addActByFilter(filterController.getOperation(), filter, existingIgnorable) : controller.removeActByFilter(filterController.getOperation(), filter, existingIgnorable);
				return ResponseHelper.getEntity(String.class, response);
			}
		});
		*/
	}
	
	public static DataTable buildDataTable(Object...objects) {
		return buildDataTable(ArrayHelper.isEmpty(objects) ? null : MapHelper.instantiate(objects));
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		private ActFilterController filterController;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(Act.FIELD_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Code");
				map.put(Column.FIELD_WIDTH, "130");
			}else if(Act.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Libelle");
			}else if(Act.FIELD_NUMBER_OF_LOCKS_ENABLED.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Nombre de verrous");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(Act.FIELD_TYPE_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Type");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(Act.FIELD_LOCKED_AS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Verrouillé");
				map.put(Column.FIELD_WIDTH, "80");
			}else if(Act.FIELD_LOCKED_REASONS.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Motif");
				map.put(Column.FIELD_WIDTH, "400");
			}
			return map;
		}
		
		@Override
		public String getStyleClassByRecordByColumn(Object record, Integer recordIndex, Column column,Integer columnIndex) {
			if(record instanceof Act) {
				Act act = (Act) record;
				if(columnIndex != null && columnIndex == 0) {
					//if(ActOperationType.VERROUILLAGE.equals(act.getOperationType()))
					//	return "cyk-background-highlight";
					if(NumberHelper.isGreaterThanZero(act.getNumberOfLocksEnabled()))
						return "cyk-background-highlight";
				}
			}
			return super.getStyleClassByRecordByColumn(record, recordIndex, column, columnIndex);
		}
		
		@Override
		public Class<? extends AbstractMenu> getRecordMenuClass(AbstractCollection collection) {
			return ContextMenu.class;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<Act> implements Serializable {
		
		private ActFilterController filterController;
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,
				int firstTupleIndex, int numberOfTuples) {
			return List.of(ActDto.JSON_IDENTIFIER,ActDto.JSONS_CODE_NAME_TYPE_AS_STRING,ActDto.JSON_NUMBER_OF_LOCKS_ENABLED,ActDto.JSON_LOCKED_REASONS);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return ActFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
	}
	
	public static final String OUTCOME = "actListView";
	public static final String ADD_OR_REMOVE_ACT_COMMAND_USER_INTERFACE_ACTION = "addOrRemoveActCommandUserInterfaceAction";
}