package ci.gouv.dgbf.system.cloture.client.operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;
import org.cyk.utility.service.client.SpecificController;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationTypeDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationType;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationTypeController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class OperationFilterController extends AbstractFilterController implements Serializable {

	protected SelectOneCombo typeSelectOne;
	
	protected OperationType typeInitial;
	
	public OperationFilterController(Boolean initializable) {
		if(Boolean.TRUE.equals(initializable))
			initialize();
		isLayoutContainerCollapsed = Boolean.FALSE;
	}
	
	public OperationFilterController() {
		this(Boolean.TRUE);
	}
	
	@Override
	public AbstractFilterController initialize() {
		if(typeInitial == null)
			typeInitial = __inject__(OperationTypeController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.OPERATION_TYPE_IDENTIFIER)
				,new Controller.GetArguments().projections(OperationTypeDto.JSON_IDENTIFIER,OperationTypeDto.JSON_NAME));
		return super.initialize();
	}
	
	@Override
	public void __addInputsByBasedOnFieldsNames__() {
		super.__addInputsByBasedOnFieldsNames__();
		addInputSelectOneByBaseFieldName("type");
	}

	@Override
	protected Object __getControllerByInputFieldName__(String fieldName) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName))
			return __inject__(OperationTypeController.class);
		return super.__getControllerByInputFieldName__(fieldName);
	}
	
	@Override
	protected Object getByIdentifier(Object controller, String fieldName, String identifier) {
		return ((SpecificController<?>)controller).getByIdentifier(identifier);
	}
	
	protected void selectByValueSystemIdentifier() {
		super.selectByValueSystemIdentifier();
		if(typeSelectOne != null)
			typeSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName))
			return buildTypeSelectOne((OperationType) value);
		return null;
	}
	
	private SelectOneCombo buildTypeSelectOne(OperationType type) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,type,SelectOneCombo.FIELD_CHOICE_CLASS,OperationType.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<OperationType>() {
			@Override
			protected Collection<OperationType> __computeChoices__(AbstractInputChoice<OperationType> input,Class<?> entityClass) {
				Collection<OperationType> choices = __inject__(OperationTypeController.class).get(new Controller.GetArguments().projections(OperationTypeDto.JSON_IDENTIFIER,OperationTypeDto.JSON_NAME));
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.OperationType.NAME);
		input.updateChoices();
		return input;
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName) || (input != null && input == typeSelectOne))
			return Parameters.OPERATION_TYPE_IDENTIFIER;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		buildLayoutCells(cellsMaps);
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,12));	
		return cellsMaps;
	}
	
	protected void buildLayoutCells(Collection<Map<Object, Object>> cellsMaps) {
		if(typeSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,typeSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,typeSelectOne,Cell.FIELD_WIDTH,11));
		}
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = generateWindowTitleValues(prefix);
		return StringHelper.concatenate(strings, " | ");
	}
	
	protected Collection<String> generateWindowTitleValues(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(typeInitial != null)
			strings.add(String.format("%s : %s", ci.gouv.dgbf.system.cloture.server.api.persistence.OperationType.NAME,typeInitial.toString()));
		return strings;
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(typeInitial == null)
			columnsFieldsNames.add(Operation.FIELD_TYPE_AS_STRING);
		columnsFieldsNames.addAll(List.of(Operation.FIELD_CODE,Operation.FIELD_NAME,Operation.FIELD_REASON));
		return columnsFieldsNames;
	}
	
	public OperationType getType() {
		return (OperationType)AbstractInput.getValue(typeSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,OperationFilterController controller,Boolean initial) {
		//filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.OPERATION_TYPE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.typeInitial : controller.getType()), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(OperationFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_TYPE_SELECT_ONE = "typeSelectOne";
	//public static final String FIELD_ACTIVITY_AUTO_COMPLETE = "activityAutoComplete";
}