package ci.gouv.dgbf.system.cloture.client.operation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.cyk.utility.__kernel__.array.ArrayHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.value.ValueHelper;
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
import ci.gouv.dgbf.system.cloture.server.api.service.OperationDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Named @ViewScoped @Getter @Setter
public class OperationListPage extends AbstractEntityListPageContainerManagedImpl<Operation> implements Serializable {

	private OperationFilterController filterController;
	@Inject private SpecificServiceGetter specificServiceGetter;
	
	@Override
	protected void __listenBeforePostConstruct__() {
		super.__listenBeforePostConstruct__();
		filterController = new OperationFilterController();   
	}
	
	@Override
	protected String __getWindowTitleValue__() { 
		if(filterController == null)
			return super.__getWindowTitleValue__(); 
		return filterController.generateWindowTitleValue("Opérations de clôture");
	}
	
	@Override
	protected DataTable __buildDataTable__() {
		DataTable dataTable = buildDataTable(OperationFilterController.class,filterController);
		//dataTable.setHeaderToolbarLeftCommands(null);
		//dataTable.setRecordMenu(null);
		//dataTable.setRecordCommands(null);
		//dataTable.setMenuColumn(null);
		return dataTable;
	}
	
	public static DataTable buildDataTable(Map<Object,Object> arguments) {
		if(arguments == null) 
			arguments = new HashMap<>();
		OperationFilterController filterController = (OperationFilterController) MapHelper.readByKey(arguments, OperationFilterController.class);
		LazyDataModel lazyDataModel = (LazyDataModel) MapHelper.readByKey(arguments, DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL);
		if(lazyDataModel == null)
			arguments.put(DataTable.ConfiguratorImpl.FIELD_LAZY_DATA_MODEL,lazyDataModel = new LazyDataModel());
		if(lazyDataModel.getFilterController() == null)
			lazyDataModel.setFilterController(filterController);
		filterController = (OperationFilterController) lazyDataModel.getFilterController();
		if(filterController == null)
			lazyDataModel.setFilterController(filterController = new OperationFilterController());		
		filterController.build();
		
		String outcome = ValueHelper.defaultToIfBlank((String)MapHelper.readByKey(arguments,OUTCOME),OUTCOME);
		filterController.getOnSelectRedirectorArguments(Boolean.TRUE).outcome(outcome);
		
		DataTableListenerImpl dataTableListenerImpl = (DataTableListenerImpl) MapHelper.readByKey(arguments, DataTable.FIELD_LISTENER);
		if(dataTableListenerImpl == null)
			arguments.put(DataTable.FIELD_LISTENER, dataTableListenerImpl = new DataTableListenerImpl());
		dataTableListenerImpl.setFilterController(filterController);
		
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_LAZY, Boolean.TRUE);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.FIELD_ELEMENT_CLASS, Operation.class);
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_COLUMNS_FIELDS_NAMES, filterController.generateColumnsNames());
		MapHelper.writeByKeyDoNotOverride(arguments, DataTable.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE, Boolean.FALSE);
		
		DataTable dataTable = DataTable.build(arguments);
		dataTable.setFilterController(filterController);
		dataTable.setAreColumnsChoosable(Boolean.TRUE);      
		dataTable.getOrderNumberColumn().setWidth("20");
		
		dataTable.addRecordMenuItemByArgumentsNavigateToView(null,OperationExecutePage.OUTCOME, MenuItem.FIELD_VALUE,"Exécuter",MenuItem.FIELD_ICON,"fa fa-gear");
		dataTable.setEntityIdentifierParameterName(Parameters.OPERATION_IDENTIFIER);
		
		return dataTable;
	}
	
	public static DataTable buildDataTable(Object...objects) {
		return buildDataTable(ArrayHelper.isEmpty(objects) ? null : MapHelper.instantiate(objects));
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class DataTableListenerImpl extends DataTable.Listener.AbstractImpl implements Serializable {
		private OperationFilterController filterController;
		
		@Override
		public Map<Object, Object> getColumnArguments(AbstractDataTable dataTable, String fieldName) {
			Map<Object, Object> map = super.getColumnArguments(dataTable, fieldName);
			map.put(Column.ConfiguratorImpl.FIELD_EDITABLE, Boolean.FALSE);
			if(Operation.FIELD_CODE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Code");
				map.put(Column.FIELD_WIDTH, "40");
			}else if(Operation.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Libelle");
			}else if(Operation.FIELD_START_DATE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exécutable à partir du");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Operation.FIELD_EXECUTION_BEGIN_DATE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exécution démarrée le");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Operation.FIELD_EXECUTION_END_DATE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exécution terminée le");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Operation.FIELD_TRIGGER.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Déclencheur");
				map.put(Column.FIELD_WIDTH, "100");
			}else if(Operation.FIELD_EXECUTION_STATUS.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Status");
				map.put(Column.FIELD_WIDTH, "100");
			}
			return map;
		}
		
		@Override
		public Class<? extends AbstractMenu> getRecordMenuClass(AbstractCollection collection) {
			return ContextMenu.class;
		}
	}
	
	@Getter @Setter @Accessors(chain=true)
	public static class LazyDataModel extends org.cyk.utility.primefaces.collection.LazyDataModel<Operation> implements Serializable {
		
		private OperationFilterController filterController;
		
		@Override
		protected List<String> getProjections(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,
				int firstTupleIndex, int numberOfTuples) {
			return List.of(OperationDto.JSON_IDENTIFIER,OperationDto.JSON_CODE,OperationDto.JSON_NAME,OperationDto.JSON_START_DATE_STRING
					,OperationDto.JSON_EXECUTION_BEGIN_DATE_STRING,OperationDto.JSON_EXECUTION_END_DATE_STRING,OperationDto.JSON_TRIGGER
					,OperationDto.JSON_EXECUTION_STATUS);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return OperationFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
	}
	
	public static final String OUTCOME = "operationListView";
}