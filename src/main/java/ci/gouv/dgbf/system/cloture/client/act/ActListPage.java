package ci.gouv.dgbf.system.cloture.client.act;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.number.NumberHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.value.ValueHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractCollection;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.AbstractDataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.Column;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.collection.DataTable;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.AbstractMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.ContextMenu;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.menu.MenuItem;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityListPageContainerManagedImpl;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.SpecificServiceGetter;
import org.primefaces.model.SortOrder;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.ActDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import ci.gouv.dgbf.system.cloture.server.client.rest.ActController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class ActListPage extends AbstractEntityListPageContainerManagedImpl<Act> implements Serializable {

	private ActFilterController filterController;
	@Inject private SpecificServiceGetter specificServiceGetter;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new ActFilterController();   
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue("Actes de dépenses");
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(ActFilterController.class,filterController);
		//dataTable.setHeaderToolbarLeftCommands(null);
		//dataTable.setRecordMenu(null);
		//dataTable.setRecordCommands(null);
		//dataTable.setMenuColumn(null);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		ActFilterController filterController = (ActFilterController) MapHelper.readByKey(arguments, ActFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (ActFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new ActFilterController());		
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
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("20");
		
		dataTable.addHeaderToolbarLeftCommandsByArguments(MenuItem.FIELD_VALUE,"Déverouiller les actes suivants",MenuItem.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,MenuItem.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE,MenuItem.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES
				,List.of(RenderType.GROWL),MenuItem.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {				
				if(StringHelper.isBlank(((LazyDataModel)dataTable.getValue()).getFilterController().getCodes()))
					throw new RuntimeException("Filtrer les actes à déverouiller");
				Collection<String> codes = CollectionHelper.listOf(Boolean.TRUE, StringUtils.split(((LazyDataModel)dataTable.getValue()).getFilterController().getCodes(),","));
				DependencyInjection.inject(ActController.class).unlockByIdentifiers(codes);
				return null;
			}
		});
		
		/*
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Vérouiller", "fa fa-lock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				DependencyInjection.inject(ActController.class).lock((Act)action.readArgument());
				return null; 
			}
		});
		*/
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Dévérouiller", "fa fa-unlock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				DependencyInjection.inject(ActController.class).unlock((Act)action.readArgument());
				return null;
			}
		});
		dataTable.addRecordMenuItemByArgumentsOpenViewInDialog(ActActLocksListPage.OUTCOME, MenuItem.FIELD_VALUE,"Verrous",MenuItem.FIELD_ICON,"fa fa-eye");
		dataTable.setEntityIdentifierParameterName(Parameters.ACT_IDENTIFIER);
		
		return dataTable;
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
				map.put(Column.FIELD_WIDTH, "110");
			}else if(Act.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Libelle");
			}else if(Act.FIELD_NUMBER_OF_LOCKS_ENABLED.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Nombre de verrous");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(Act.FIELD_OPERATION_TYPE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Opération");
				map.put(Column.FIELD_WIDTH, "130");
			}else if(Act.FIELD_TRIGGER.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Déclencheur");
				map.put(Column.FIELD_WIDTH, "150");
			}else if(Act.FIELD_OPERATION_DATE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exécutée le");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Act.FIELD_STATUS_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Statut");
				map.put(Column.FIELD_WIDTH, "500");
			}else if(Act.FIELD_LATEST_OPERATION_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Dernière opération");
				map.put(Column.FIELD_WIDTH, "300");
			}else if(Act.FIELD_TYPE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Type");
				map.put(Column.FIELD_WIDTH, "100");
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
			return List.of(ActDto.JSON_IDENTIFIER,ActDto.JSONS_CODE_NAME_TYPE_STRING_NUMBER_OF_LOCKS_ENABLED_STATUS_STRING_LATEST_OPERATION,ActDto.JSON_NUMBER_OF_LOCKS_ENABLED,ActDto.JSON_LOCKED_REASONS);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return ActFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
	}
	
	public static final String OUTCOME = "actListView";
}