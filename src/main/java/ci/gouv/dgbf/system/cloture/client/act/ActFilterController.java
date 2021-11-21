package ci.gouv.dgbf.system.cloture.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.InputText;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.cloture.server.api.persistence.ActOperationType;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ActFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo operationTypeSelectOne;
	private InputText searchInputText;
	
	private ActOperationType operationTypeInitial;
	private String searchInitial;
	
	public ActFilterController() {
		initialize();
	}
	
	@Override
	public AbstractFilterController initialize() {
		String operationTypeInitialString = WebController.getInstance().getRequestParameter(buildParameterName(FIELD_OPERATION_TYPE_SELECT_ONE));
		if(StringHelper.isNotBlank(operationTypeInitialString))
			operationTypeInitial = ActOperationType.valueOf(operationTypeInitialString);
		searchInitial = WebController.getInstance().getRequestParameter(buildParameterName(FIELD_SEARCH_INPUT_TEXT));
		return this;
	}
	
	@Override
	protected void buildInputs() {
		buildInputSelectOne(FIELD_OPERATION_TYPE_SELECT_ONE, ActOperationType.class);
		buildInputText(FIELD_SEARCH_INPUT_TEXT);
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_OPERATION_TYPE_SELECT_ONE.equals(fieldName))
			return buildInputActOperationTypeSelectOne((ActOperationType) value);
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName))
			return buildInputSearch((String)value);
		return null;
	}
	
	private SelectOneCombo buildInputActOperationTypeSelectOne(ActOperationType operationType) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,operationType,SelectOneCombo.FIELD_CHOICE_CLASS,ActOperationType.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<ActOperationType>() {
			
			@Override
			protected Collection<ActOperationType> __computeChoices__(AbstractInputChoice<ActOperationType> input, Class<?> entityClass) {
				Collection<ActOperationType> choices = CollectionHelper.listOf(ActOperationType.values());
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Opération");
		return input;
	} 
	
	private InputText buildInputSearch(String search) {
		InputText input = InputText.build(SelectOneCombo.FIELD_VALUE,search,InputText.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Recherche");
		input.setPlaceholder(StringHelper.concatenate(List.of("Numéro","Libellé"), " | "));
		return input;
	}
	
	@Override
	protected Object getInputSelectOneInitialValue(String fieldName, Class<?> klass) {
		if(FIELD_OPERATION_TYPE_SELECT_ONE.equals(fieldName))
			return operationTypeInitial;
		return super.getInputSelectOneInitialValue(fieldName, klass);
	}
	
	@Override
	protected String getInputTextInitialValue(String fieldName) {
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName))
			return searchInitial;
		return super.getInputTextInitialValue(fieldName);
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_OPERATION_TYPE_SELECT_ONE.equals(fieldName) || input == operationTypeSelectOne)
			return Parameters.ACT_OPERATION_TYPE;
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName) || input == searchInputText)
			return Parameters.SEARCH;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected String buildParameterName(String fieldName) {
		if(FIELD_OPERATION_TYPE_SELECT_ONE.equals(fieldName))
			return Parameters.ACT_OPERATION_TYPE;
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName))
			return Parameters.SEARCH;
		return super.buildParameterName(fieldName);
	}
	
	@Override
	protected String buildParameterName(AbstractInput<?> input) {
		if(input == operationTypeSelectOne)
			return Parameters.ACT_OPERATION_TYPE;
		if(input == searchInputText)
			return Parameters.SEARCH;
		return super.buildParameterName(input);
	}
	
	@Override
	protected String buildParameterValue(AbstractInput<?> input) {
		if(input == operationTypeSelectOne)
			return StringHelper.get(AbstractInput.getValue(input));
		return super.buildParameterValue(input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(operationTypeSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,operationTypeSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,operationTypeSelectOne,Cell.FIELD_WIDTH,10));
		}
		
		if(searchInputText != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,searchInputText.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,searchInputText,Cell.FIELD_WIDTH,10));
		}
		
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,12));	
		return cellsMaps;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(operationTypeInitial != null)
			strings.add(String.format("Opération : %s", operationTypeInitial.name()));
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.add(Act.FIELD_CODE);
		columnsFieldsNames.add(Act.FIELD_NAME);
		if(operationTypeInitial == null)
			columnsFieldsNames.add(Act.FIELD_OPERATION_TYPE);
		columnsFieldsNames.add(Act.FIELD_OPERATION_DATE_STRING);
		columnsFieldsNames.add(Act.FIELD_TRIGGER);		
		return columnsFieldsNames;
	}
	
	public ActOperationType getOperationType() {
		return (ActOperationType)AbstractInput.getValue(operationTypeSelectOne);
	}
	
	public String getSearch() {
		return (String)AbstractInput.getValue(searchInputText);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ActFilterController controller,Boolean initial) {
		ActOperationType operationType = Boolean.TRUE.equals(initial) ? controller.operationTypeInitial : controller.getOperationType();
		if(operationType != null)
			filter = Filter.Dto.addFieldIfValueNotNull(Parameters.ACT_OPERATION_TYPE,operationType.name() , filter);
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.SEARCH, Boolean.TRUE.equals(initial) ? controller.searchInitial : controller.getSearch(), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(ActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_OPERATION_TYPE_SELECT_ONE = "operationTypeSelectOne";
	public static final String FIELD_SEARCH_INPUT_TEXT = "searchInputText";
}