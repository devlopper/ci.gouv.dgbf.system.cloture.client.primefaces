package ci.gouv.dgbf.system.cloture.client.operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.service.client.Controller;
import org.cyk.utility.service.client.SpecificController;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationStatusDto;
import ci.gouv.dgbf.system.cloture.server.api.service.OperationTypeDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationStatus;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationStatusController;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationType;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationTypeController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class OperationFilterController extends AbstractFilterController implements Serializable {

	protected SelectOneCombo typeSelectOne,statusSelectOne;
	
	protected OperationType typeInitial;
	protected OperationStatus statusInitial;
	
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
		//if(typeInitial == null)
		//	typeInitial = __inject__(OperationTypeController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.OPERATION_TYPE_IDENTIFIER)
		//		,new Controller.GetArguments().projections(OperationTypeDto.JSON_IDENTIFIER,OperationTypeDto.JSON_NAME));
		return super.initialize();
	}
	
	@Override
	public void __addInputsByBasedOnFieldsNames__() {
		super.__addInputsByBasedOnFieldsNames__();
		addInputSelectOneByBaseFieldName("type");
		addInputSelectOneByBaseFieldName("status");
	}

	@Override
	protected Object __getControllerByInputFieldName__(String fieldName) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName))
			return __inject__(OperationTypeController.class);
		if(FIELD_STATUS_SELECT_ONE.equals(fieldName))
			return __inject__(OperationStatusController.class);
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
		if(statusSelectOne != null)
			statusSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName))
			return buildTypeSelectOne((OperationType) value);
		if(FIELD_STATUS_SELECT_ONE.equals(fieldName))
			return buildStatusSelectOne((OperationStatus) value);
		return null;
	}
	
	private SelectOneCombo buildTypeSelectOne(OperationType type) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,type,SelectOneCombo.FIELD_CHOICE_CLASS,OperationType.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<OperationType>() {
			@Override
			protected Collection<OperationType> __computeChoices__(AbstractInputChoice<OperationType> input,Class<?> entityClass) {
				Collection<OperationType> choices = __inject__(OperationTypeController.class).get(new Controller.GetArguments().projections(OperationTypeDto.JSON_IDENTIFIER,OperationTypeDto.JSON_NAME));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.OperationType.NAME);
		input.updateChoices();
		return input;
	}
	
	private SelectOneCombo buildStatusSelectOne(OperationStatus status) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,status,SelectOneCombo.FIELD_CHOICE_CLASS,OperationStatus.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<OperationStatus>() {
			@Override
			protected Collection<OperationStatus> __computeChoices__(AbstractInputChoice<OperationStatus> input,Class<?> entityClass) {
				Collection<OperationStatus> choices = __inject__(OperationStatusController.class).get(new Controller.GetArguments().projections(OperationStatusDto.JSON_IDENTIFIER,OperationStatusDto.JSON_NAME));
				CollectionHelper.addNullAtFirstIfSizeGreaterThanOne(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.OperationStatus.NAME);
		input.updateChoices();
		return input;
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_TYPE_SELECT_ONE.equals(fieldName) || (input != null && input == typeSelectOne))
			return Parameters.OPERATION_TYPE_IDENTIFIER;
		if(FIELD_STATUS_SELECT_ONE.equals(fieldName) || (input != null && input == statusSelectOne))
			return Parameters.OPERATION_STATUS_IDENTIFIER;
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
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,typeSelectOne,Cell.FIELD_WIDTH,5));
		}
		
		if(statusSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,statusSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,statusSelectOne,Cell.FIELD_WIDTH,5));
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
		if(statusInitial != null)
			strings.add(String.format("%s : %s", ci.gouv.dgbf.system.cloture.server.api.persistence.OperationStatus.NAME,statusInitial.toString()));
		return strings;
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(typeInitial == null)
			columnsFieldsNames.add(Operation.FIELD_TYPE_AS_STRING);
		columnsFieldsNames.addAll(List.of(Operation.FIELD_CODE,Operation.FIELD_NAME,Operation.FIELD_REASON,Operation.FIELD_NUMBER_OF_ACTS));
		if(statusInitial == null)
			columnsFieldsNames.add(Operation.FIELD_STATUS_AS_STRING);
		columnsFieldsNames.addAll(List.of(Operation.FIELD___AUDIT__));
		return columnsFieldsNames;
	}
	
	public OperationType getType() {
		return (OperationType)AbstractInput.getValue(typeSelectOne);
	}
	
	public OperationStatus getStatus() {
		return (OperationStatus)AbstractInput.getValue(statusSelectOne);
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,OperationFilterController controller,Boolean initial) {
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.OPERATION_TYPE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.typeInitial : controller.getType()), filter);
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.OPERATION_STATUS_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.statusInitial : controller.getStatus()), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(OperationFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_TYPE_SELECT_ONE = "typeSelectOne";
	public static final String FIELD_STATUS_SELECT_ONE = "statusSelectOne";
	//public static final String FIELD_ACTIVITY_AUTO_COMPLETE = "activityAutoComplete";
}