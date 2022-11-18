package ci.gouv.dgbf.system.cloture.client.imputation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.__kernel__.collection.CollectionHelper;
import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.identifier.resource.ParameterName;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.__kernel__.user.interface_.UserInterfaceAction;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.converter.ObjectConverter;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractAction;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoiceOne;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AutoComplete;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.InputText;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.SelectOneCombo;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.layout.Cell;
import org.cyk.utility.persistence.query.Filter;
import org.cyk.utility.rest.ResponseHelper;
import org.cyk.utility.service.client.Controller;
import org.cyk.utility.service.client.SpecificController;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.api.service.ExerciseDto;
import ci.gouv.dgbf.system.cloture.server.client.rest.Activity;
import ci.gouv.dgbf.system.cloture.server.client.rest.ActivityController;
import ci.gouv.dgbf.system.cloture.server.client.rest.Exercise;
import ci.gouv.dgbf.system.cloture.server.client.rest.ExerciseController;
import ci.gouv.dgbf.system.cloture.server.client.rest.Imputation;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationController;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ImputationFilterController extends AbstractFilterController implements Serializable {

	private SelectOneCombo exerciseSelectOne,operationSelectOne,addedToSelectedOperationSelectOne;
	private InputText codesInputText;
	private InputText searchInputText;
	
	protected AutoComplete activityAutoComplete;
	
	protected Exercise exerciseInitial;
	protected Activity activityInitial;
	
	private Operation operationInitial;
	private String codesInitial;
	private String searchInitial;
	private Boolean addedToSelectedOperationInitial;
	
	private CommandButton addToOperationCommandButton,removeFromOperationCommandButton;
	
	private UsedFor usedFor;
	
	public ImputationFilterController(Boolean initializable) {
		if(Boolean.TRUE.equals(initializable))
			initialize();
		isLayoutContainerCollapsed = Boolean.FALSE;
	}
	
	public ImputationFilterController() {
		this(Boolean.TRUE);
	}
	
	@Override
	public AbstractFilterController initialize() {
		if(exerciseInitial == null)
			exerciseInitial = __inject__(ExerciseController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.EXERCISE_IDENTIFIER)
				,new Controller.GetArguments().projections(ExerciseDto.JSON_IDENTIFIER,ExerciseDto.JSON_YEAR));
		//if(typeInitial == null)
		//	typeInitial = __inject__(ActTypeController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.ACT_TYPE_IDENTIFIER)
		//		,new Controller.GetArguments().projections(ActTypeDto.JSON_IDENTIFIER,ActTypeDto.JSON_NAME));
		
		//if(operationInitial == null)
		//	if(StringHelper.isNotBlank(WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER)))
		//		operationInitial = __inject__(OperationController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER)
		//			,new Controller.GetArguments().projections(ActTypeDto.JSON_IDENTIFIER,ActTypeDto.JSON_NAME));
			/*
			operationInitial = __inject__(OperationController.class).getByIdentifierOrDefaultIfIdentifierIsBlank(WebController.getInstance().getRequestParameter(Parameters.OPERATION_IDENTIFIER)
				,new Controller.GetArguments().projections(ActTypeDto.JSON_IDENTIFIER,ActTypeDto.JSON_NAME));
			*/
		codesInitial = WebController.getInstance().getRequestParameter(buildParameterName(FIELD_CODES_INPUT_TEXT));
		searchInitial = WebController.getInstance().getRequestParameter(buildParameterName(FIELD_SEARCH_INPUT_TEXT));
		return super.initialize();
	}
	
	@Override
	public void __addInputsByBasedOnFieldsNames__() {
		super.__addInputsByBasedOnFieldsNames__();
		addInputSelectOneByBaseFieldName("exercise");
		addInputSelectOneByBaseFieldName("operation");
		addInputSelectOneByBaseFieldName("addedToSelectedOperation");
		addInputAutoCompleteByBaseFieldName("activity");
		/*
		addInputSelectOneByBaseFieldName("operation");
		addInputSelectOneByBaseFieldName("addedToSelectedOperation");
		addInputTextByBaseFieldName("codes");
		addInputTextByBaseFieldName("search");*/
	}

	@Override
	protected Object __getControllerByInputFieldName__(String fieldName) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName))
			return __inject__(ExerciseController.class);
		if(FIELD_ACTIVITY_AUTO_COMPLETE.equals(fieldName))
			return __inject__(ActivityController.class);
		if(FIELD_OPERATION_SELECT_ONE.equals(fieldName))
			return __inject__(OperationController.class);
		return super.__getControllerByInputFieldName__(fieldName);
	}
	
	@Override
	protected Object getByIdentifier(Object controller, String fieldName, String identifier) {
		return ((SpecificController<?>)controller).getByIdentifier(identifier);
	}
	
	protected void selectByValueSystemIdentifier() {
		super.selectByValueSystemIdentifier();
		if(exerciseSelectOne != null)
			exerciseSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
		if(operationSelectOne != null)
			operationSelectOne.selectFirstChoiceIfValueIsNullElseSelectByValueSystemIdentifier();
	}
	
	@Override
	protected void enableValueChangeListeners() {
		super.enableValueChangeListeners();
		activityAutoComplete.enableAjaxItemSelect();
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName))
			return buildExerciseSelectOne((Exercise) value);
		if(FIELD_ACTIVITY_AUTO_COMPLETE.equals(fieldName))
			return buildActivityAutoComplete((Activity) value);
		if(FIELD_OPERATION_SELECT_ONE.equals(fieldName))
			return buildInputOperationSelectOne((Operation) value);
		if(FIELD_CODES_INPUT_TEXT.equals(fieldName))
			return buildInputCodes((String)value);
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName))
			return buildInputSearch((String)value);
		if(FIELD_ADDED_TO_SELECTED_OPERATION_SELECT_ONE.equals(fieldName))
			return buildInputAddedToSelectedOperation((Boolean) value);
		return null;
	}
	
	private SelectOneCombo buildExerciseSelectOne(Exercise exercise) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,exercise,SelectOneCombo.FIELD_CHOICE_CLASS,Exercise.class,SelectOneCombo.FIELD_LISTENER
				,new SelectOneCombo.Listener.AbstractImpl<Exercise>() {
			@Override
			protected Collection<Exercise> __computeChoices__(AbstractInputChoice<Exercise> input,Class<?> entityClass) {
				Collection<Exercise> choices = __inject__(ExerciseController.class).get(new Controller.GetArguments().projections(ExerciseDto.JSON_IDENTIFIER,ExerciseDto.JSON_YEAR));
				return choices;
			}
			@Override
			public Object getChoiceLabel(AbstractInputChoice<Exercise> input, Exercise choice) {
				if(choice == null)
					return super.getChoiceLabel(input, choice);
				return choice.getYear();
			}
			@Override
			public void select(AbstractInputChoiceOne input, Exercise exercise) {
				super.select(input, exercise);
				
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.Exercise.NAME,SelectOneCombo.FIELD_CONVERTER,new ObjectConverter());
		input.updateChoices();
		return input;
	}
	
	private AutoComplete buildActivityAutoComplete(Activity activity) {
		AutoComplete input = AutoComplete.build(AutoComplete.FIELD_ENTITY_CLASS,Activity.class,AutoComplete.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_BUILDABLE,Boolean.FALSE
				,AutoComplete.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.Activity.NAME,AutoComplete.FIELD_VALUE,activity
				,AutoComplete.FIELD_LISTENER,new AutoComplete.Listener.AbstractImpl<Activity>() {
			@Override
			public Collection<Activity> complete(AutoComplete autoComplete) {
				return __inject__(ActivityController.class).get(new Controller.GetArguments().setFirstTupleIndex(0).setNumberOfTuples(autoComplete.getMaxResults()).setFilter(autoComplete.get__filter__()).setCountable(Boolean.TRUE));
			}
			
			@Override
			public Filter.Dto instantiateFilter(AutoComplete autoComplete) {
				return new Filter.Dto().addField(Parameters.SEARCH, autoComplete.get__queryString__());
			}
		});
		input.enableAjaxItemSelect();
		return input;
	}
	
	private SelectOneCombo buildInputOperationSelectOne(Operation operation) {
		SelectOneCombo input = SelectOneCombo.build(SelectOneCombo.FIELD_VALUE,operation,SelectOneCombo.FIELD_CHOICE_CLASS,Operation.class
				,SelectOneCombo.FIELD_LISTENER,new SelectOneCombo.Listener.AbstractImpl<Operation>() {
			@Override
			protected Collection<Operation> __computeChoices__(AbstractInputChoice<Operation> input, Class<?> entityClass) {
				Collection<Operation> choices = __inject__(OperationController.class).get();
				CollectionHelper.addNullAtFirstIfSizeGreaterThanZero(choices);
				return choices;
			}
		},SelectOneCombo.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.Operation.NAME);
		input.updateChoices();
		return input;
	} 
	
	private InputText buildInputCodes(String codes) {
		InputText input = InputText.build(SelectOneCombo.FIELD_VALUE,codes,InputText.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.Act.CODES_LABEL);
		return input;
	}
	
	private InputText buildInputSearch(String search) {
		InputText input = InputText.build(SelectOneCombo.FIELD_VALUE,search,InputText.ConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,ci.gouv.dgbf.system.cloture.server.api.persistence.Act.SEARCH_LABEL);
		input.setPlaceholder(ci.gouv.dgbf.system.cloture.server.api.persistence.Act.SEARCH_PLACEHOLDER);
		return input;
	}
	
	private SelectOneCombo buildInputAddedToSelectedOperation(Boolean value) {
		return SelectOneCombo.buildUnknownYesNoOnly(value, "Ajoutés à l'opération sélectionnée");
	}
	
	@Override
	protected String buildParameterName(String fieldName, AbstractInput<?> input) {
		if(FIELD_EXERCISE_SELECT_ONE.equals(fieldName) || (input != null && input == exerciseSelectOne))
			return Parameters.EXERCISE_IDENTIFIER;
		if(FIELD_ACTIVITY_AUTO_COMPLETE.equals(fieldName) || (input != null && input == activityAutoComplete))
			return Parameters.ACTIVITY_IDENTIFIER;
		if(FIELD_OPERATION_SELECT_ONE.equals(fieldName) || (input != null && input == operationSelectOne))
			return Parameters.OPERATION_IDENTIFIER;
		if(FIELD_CODES_INPUT_TEXT.equals(fieldName) || (input != null && input == codesInputText))
			return Parameters.ACTS_CODES;
		if(FIELD_SEARCH_INPUT_TEXT.equals(fieldName) || (input != null && input == searchInputText))
			return Parameters.SEARCH;
		if(FIELD_ADDED_TO_SELECTED_OPERATION_SELECT_ONE.equals(fieldName) ||(input != null && input == addedToSelectedOperationSelectOne))
			return Parameters.ADDED_TO_SPECIFIED_OPERATION;
		return super.buildParameterName(fieldName, input);
	}
	
	@Override
	protected Collection<Map<Object, Object>> buildLayoutCells() {
		Collection<Map<Object, Object>> cellsMaps = new ArrayList<>();
		if(exerciseSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,exerciseSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,exerciseSelectOne,Cell.FIELD_WIDTH,1));
		}
		
		if(activityAutoComplete != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activityAutoComplete.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,activityAutoComplete,Cell.FIELD_WIDTH,9));
		}
		
		if(operationSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,operationSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,operationSelectOne,Cell.FIELD_WIDTH,9));
		}
		
		if(addedToSelectedOperationSelectOne != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,addedToSelectedOperationSelectOne.getOutputLabel(),Cell.FIELD_WIDTH,2));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,addedToSelectedOperationSelectOne,Cell.FIELD_WIDTH,1));
		}
		/*
		if(codesInputText != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,codesInputText.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,codesInputText,Cell.FIELD_WIDTH,11));
		}
		
		if(searchInputText != null) {
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,searchInputText.getOutputLabel(),Cell.FIELD_WIDTH,1));
			cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,searchInputText,Cell.FIELD_WIDTH,11));
		}
		*/
		Integer width = 12;
		if(UsedFor.ADD_TO_OPERATION.equals(usedFor) || UsedFor.REMOVE_FROM_OPERATION.equals(usedFor)) {
			if(UsedFor.ADD_TO_OPERATION.equals(usedFor))
				cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,buildAddToOrRemoveFromOperationCommandButton(this, Boolean.TRUE, Boolean.TRUE),Cell.FIELD_WIDTH,8));
			else if(UsedFor.REMOVE_FROM_OPERATION.equals(usedFor))
				cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,buildAddToOrRemoveFromOperationCommandButton(this, Boolean.FALSE, Boolean.TRUE),Cell.FIELD_WIDTH,8));
			width = 4;
			isInitialValue = Boolean.FALSE;
		}
		cellsMaps.add(MapHelper.instantiate(Cell.FIELD_CONTROL,filterCommandButton,Cell.FIELD_WIDTH,width));
		
		return cellsMaps;
	}
	
	@Override
	protected void buildFilterCommandButton() {
		if(page != null && Boolean.TRUE.equals( ((AbstractPageContainerManagedImpl)page).getIsRenderTypeDialog()))
			getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(WebController.REQUEST_PARAMETER_NAME_WINDOW_RENDER_TYPE, WebController.REQUEST_PARAMETER_VALUE_WINDOW_RENDER_TYPE_DIALOG);
		if(usedFor != null)
			getOnSelectRedirectorArguments(Boolean.TRUE).addParameter(ParameterName.stringify(UsedFor.class),usedFor.name());
		super.buildFilterCommandButton();
		filterCommandButton.addStyleClasses("cyk-float-right");
	}
	
	public static CommandButton buildAddToOrRemoveFromOperationCommandButton(ImputationFilterController filterController,Boolean add,Boolean existingIgnorable) {
		return CommandButton.build(CommandButton.FIELD_VALUE,(Boolean.TRUE.equals(add) ? "Ajouter" : "Retirer"),CommandButton.FIELD_ICON,"fa fa-"+(Boolean.TRUE.equals(add) ? "plus" : "minus")
				,CommandButton.FIELD_USER_INTERFACE_ACTION,UserInterfaceAction.EXECUTE_FUNCTION
				,CommandButton.ConfiguratorImpl.FIELD_CONFIRMABLE,Boolean.TRUE,CommandButton.ConfiguratorImpl.FIELD_RUNNER_ARGUMENTS_SUCCESS_MESSAGE_ARGUMENTS_RENDER_TYPES
				,List.of(org.cyk.utility.__kernel__.user.interface_.message.RenderType.GROWL),CommandButton.FIELD_LISTENER,new AbstractAction.Listener.AbstractImpl() {
			@Override
			protected Object __runExecuteFunction__(AbstractAction action) {
				Filter.Dto filter = ImputationFilterController.instantiateFilter(filterController, Boolean.FALSE);
				OperationController controller = DependencyInjection.inject(OperationController.class);
				Response response;
				Collection<Imputation> imputations = CollectionHelper.cast(Imputation.class,filterController.getCollection().getSelectionAsCollection());
				if(CollectionHelper.isEmpty(imputations)) {
					if(add)
						response = controller.addImputationByFilter(filterController.getOperation(), filter, existingIgnorable);
					else
						response = controller.removeImputationByFilter(filterController.getOperation(), filter, existingIgnorable);
				}else {
					if(add)
						response = controller.addImputation(filterController.getOperation(), imputations, existingIgnorable);
					else
						response = controller.removeImputation(filterController.getOperation(), imputations, existingIgnorable);
				}
				return ResponseHelper.getEntity(String.class, response);	
			}
		},CommandButton.FIELD_STYLE_CLASS,"cyk-float-left");
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(exerciseInitial != null)
			strings.add(String.format("%s : %s",ci.gouv.dgbf.system.cloture.server.api.persistence.Exercise.NAME, exerciseInitial.toString()));
		if(activityInitial != null)
			strings.add(String.format("%s : %s",ci.gouv.dgbf.system.cloture.server.api.persistence.Activity.NAME, activityInitial.toString()));
		if(operationInitial != null) {
			strings.add(String.format("%s : %s",ci.gouv.dgbf.system.cloture.server.api.persistence.Operation.NAME, operationInitial.toString()));
			if(addedToSelectedOperationInitial != null)
				strings.add(String.format("%s : %s","Ajouté", addedToSelectedOperationInitial ? "Oui" : "Non"));		
		}
		if(StringHelper.isNotBlank(codesInitial))
			strings.add(String.format("%s : %s",ci.gouv.dgbf.system.cloture.server.api.persistence.Act.CODES_LABEL, codesInitial));
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		if(exerciseInitial == null)
			columnsFieldsNames.add(Imputation.FIELD_EXERCISE_AS_STRING);
		if(activityInitial == null)
			columnsFieldsNames.add(Imputation.FIELD_ACTIVITY_AS_STRING);
		columnsFieldsNames.add(Imputation.FIELD_ECONOMIC_NATURE_AS_STRING);
		return columnsFieldsNames;
	}
	
	public Exercise getExercise() {
		return (Exercise)AbstractInput.getValue(exerciseSelectOne);
	}
	
	public Activity getActivity() {
		return (Activity)AbstractInput.getValue(activityAutoComplete);
	}
	
	public Operation getOperation() {
		return (Operation)AbstractInput.getValue(operationSelectOne);
	}
	
	public String getSearch() {
		return (String)AbstractInput.getValue(searchInputText);
	}
	
	public String getCodes() {
		return (String)AbstractInput.getValue(codesInputText);
	}
	
	public Boolean getAddedToSelectedOperation() {
		return (Boolean)AbstractInput.getValue(addedToSelectedOperationSelectOne);
	}
	/*
	@Override
	public Map<String, List<String>> asMap() {
		Map<String, List<String>> map = super.asMap();
		if(usedFor != null) {
			if(map == null)
				map = new HashMap<>();
			map.put(ParameterName.stringify(UsedFor.class), List.of(usedFor.name()));
		}
		return map;
	}
	*/
	/*
	@Override
	public Map<String, List<String>> asMap() {
		Map<String, List<String>> map = new HashMap<>();
		if(typeInitial != null)
			map.put(Parameters.ACT_TYPE_IDENTIFIER, List.of(typeInitial.getIdentifier()));	
		if(operationInitial != null)
			map.put(Parameters.OPERATION_IDENTIFIER, List.of(operationInitial.getIdentifier()));	
		if(searchInitial != null && StringHelper.isNotBlank(searchInitial))
			map.put(Parameters.SEARCH, List.of(searchInitial));
		return map;
	}
	*/
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ImputationFilterController controller,Boolean initial) {
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.EXERCISE_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.exerciseInitial : controller.getExercise()), filter);
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.ACTIVITY_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.activityInitial : controller.getActivity()), filter);
		
		String operationIdentifier = (String) FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.operationInitial : controller.getOperation());
		if(StringHelper.isNotBlank(operationIdentifier)) {
			filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.OPERATION_IDENTIFIER, operationIdentifier, filter);
			filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.ADDED_TO_SPECIFIED_OPERATION,Boolean.TRUE.equals(initial) ? controller.addedToSelectedOperationInitial : controller.getAddedToSelectedOperation(), filter);
		}
		/*
		String codesAsString = Boolean.TRUE.equals(initial) ? controller.codesInitial : controller.getCodes();
		if(StringHelper.isNotBlank(codesAsString)) {
			String[] codesArray = StringUtils.split(codesAsString, ",");
			Collection<String> codes = CollectionHelper.listOf(Boolean.TRUE,codesArray);
			if(CollectionHelper.isNotEmpty(codes))
				codes = codes.stream().filter(code -> StringHelper.isNotBlank(code.trim())).map(code -> code.trim()).collect(Collectors.toList());
			if(CollectionHelper.isNotEmpty(codes))
				filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.ACTS_CODES, codes, filter);
		}		
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.SEARCH, Boolean.TRUE.equals(initial) ? controller.searchInitial : controller.getSearch(), filter);
		*/
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(ImputationFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static Filter.Dto instantiateFilter(ImputationFilterController controller) {
		return instantiateFilter(controller, controller.getIsInitialValue());
	}
	
	public static final String FIELD_EXERCISE_SELECT_ONE = "exerciseSelectOne";
	public static final String FIELD_ACTIVITY_AUTO_COMPLETE = "activityAutoComplete";
	public static final String FIELD_SEARCH_INPUT_TEXT = "searchInputText";
	public static final String FIELD_CODES_INPUT_TEXT = "codesInputText";
	public static final String FIELD_OPERATION_SELECT_ONE = "operationSelectOne";
	public static final String FIELD_ADDED_TO_SELECTED_OPERATION_SELECT_ONE = "addedToSelectedOperationSelectOne";
	
	public static enum UsedFor {
		ADD_TO_OPERATION
		,REMOVE_FROM_OPERATION
	}
}