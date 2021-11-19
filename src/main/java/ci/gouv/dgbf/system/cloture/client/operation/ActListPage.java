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
		
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Vérouiller", "fa fa-lock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				System.out.println("ActListPage.buildDataTable(...).new AbstractImpl() {...}.__runExecuteFunction__() V");
				return null; 
			}
		});
		dataTable.addRecordMenuItemByArgumentsExecuteFunction("Dévérouiller", "fa fa-unlock", new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				System.out.println("ActListPage.buildDataTable(...).new AbstractImpl() {...}.__runExecuteFunction__() D");
				return null;
			}
		});
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
				map.put(Column.FIELD_WIDTH, "40");
			}else if(Act.FIELD_NAME.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Libelle");
			}else if(Act.FIELD_OPERATION_TYPE.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Opération");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Act.FIELD_TRIGGER.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Déclencheur");
				map.put(Column.FIELD_WIDTH, "120");
			}else if(Act.FIELD_OPERATION_DATE_STRING.equals(fieldName)) {
				map.put(Column.FIELD_HEADER_TEXT, "Exécutée le");
				map.put(Column.FIELD_WIDTH, "120");
			}
			return map;
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
			return List.of(ActDto.JSON_IDENTIFIER,ActDto.JSON_CODE,ActDto.JSON_NAME,ActDto.JSON_OPERATION_TYPE
					,ActDto.JSON_TRIGGER,ActDto.JSON_OPERATION_DATE_STRING);
		}
		
		@Override
		protected Filter.Dto getFilter(Map<String, Object> filters, LinkedHashMap<String, SortOrder> sortOrders,int firstTupleIndex, int numberOfTuples) {
			return ActFilterController.instantiateFilter(filterController, Boolean.TRUE);
		}
	}
	
	public static final String OUTCOME = "actListView";
}