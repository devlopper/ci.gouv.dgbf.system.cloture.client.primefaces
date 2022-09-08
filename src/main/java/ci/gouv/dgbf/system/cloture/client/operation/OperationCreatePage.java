package ci.gouv.dgbf.system.cloture.client.operation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import org.cyk.utility.__kernel__.enumeration.Action;
import org.cyk.utility.__kernel__.map.MapHelper;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.Input;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoice;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOne;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputChoiceOneCombo;
import org.cyk.utility.__kernel__.object.__static__.controller.annotation.InputText;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.client.controller.web.jsf.primefaces.data.Form;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.command.CommandButton;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInputChoice;
import org.cyk.utility.client.controller.web.jsf.primefaces.page.AbstractEntityEditPageContainerManagedImpl;
import org.cyk.utility.rest.ResponseHelper;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationController;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationType;
import ci.gouv.dgbf.system.cloture.server.client.rest.OperationTypeController;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class OperationCreatePage extends AbstractEntityEditPageContainerManagedImpl<OperationCreatePage.Data> implements Serializable{

	//private String createdOperationIdentifier;
	
	@Override
	protected Form __buildForm__() {		
		return buildForm(Form.FIELD_CONTAINER,this);
	}
	
	@Override
	protected String __getWindowTitleValue__() {
		return "Création "+ci.gouv.dgbf.system.cloture.server.api.persistence.Operation.NAME;
	}
	
	public static Form buildForm(Map<Object,Object> map) {
		if(map == null)
			map = new HashMap<>();
		MapHelper.writeByKeyDoNotOverride(map, Form.FIELD_ENTITY_CLASS, Data.class);
		MapHelper.writeByKeyDoNotOverride(map,Form.FIELD_ACTION, Action.CREATE);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_CONTROLLER_ENTITY_INJECTABLE, Boolean.FALSE);
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_INPUTS_FIELDS_NAMES, List.of(Data.FIELD_TYPE,Data.FIELD_REASON,Data.FIELD_CODE,Data.FIELD_NAME));
		MapHelper.writeByKeyDoNotOverride(map,Form.ConfiguratorImpl.FIELD_LISTENER, new FormConfiguratorListenerImpl());		
		MapHelper.writeByKeyDoNotOverride(map,Form.FIELD_LISTENER, new FormListenerImpl());
		Form form = Form.build(map);
		return form;
	}
	
	public static Form buildForm(Object...arguments) {
		return buildForm(MapHelper.instantiate(arguments));
	}
	
	public static class FormListenerImpl extends Form.Listener.AbstractImpl implements Serializable {
		@Override
		public void act(Form form) { 
			Data data = (Data) form.getEntity();
			Response response = __inject__(OperationController.class).create(new Operation().setType(data.getType()).setCode(data.getCode()).setName(data.getName()).setReason(data.getReason()));
			form.setRequest(ResponseHelper.getHeader(response, Parameters.OPERATION_IDENTIFIER));
			//createdOperationIdentifier = ResponseHelper.getHeader(response, ResponseHelper.HEADER_ENTITY_IDENTIFIER_SYSTEM);
		}
		
		@Override
		public void redirect(Form form, Object request) {
			AbstractPageContainerManagedImpl page = (AbstractPageContainerManagedImpl) form.getContainer();
			if(page != null && !Boolean.TRUE.equals(page.getIsRenderTypeDialog()))
				Redirector.getInstance().redirect(new Redirector.Arguments().outcome(OperationReadPage.OUTCOME).addParameter(Parameters.OPERATION_IDENTIFIER, form.getRequest()));
			else
				super.redirect(form, request);
		}
	}
	
	public static class FormConfiguratorListenerImpl extends Form.ConfiguratorImpl.Listener.AbstractImpl implements Serializable {
		
		@Override
		public Map<Object, Object> getInputArguments(Form form, String fieldName) {
			Map<Object, Object> map = super.getInputArguments(form, fieldName);
			if(Data.FIELD_CODE.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Code");
			}else if(Data.FIELD_NAME.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Libellé");
			}else if(Data.FIELD_REASON.equals(fieldName)) {
				map.put(AbstractInput.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE, "Motif");
			}else if(Data.FIELD_TYPE.equals(fieldName)) {
				map.put(AbstractInputChoice.AbstractConfiguratorImpl.FIELD_OUTPUT_LABEL_VALUE,"Type");
				Collection<OperationType> choices = __inject__(OperationTypeController.class).get();
				map.put(AbstractInputChoice.FIELD_CHOICES,choices);
			}
			return map;
		}
		
		@Override
		public Map<Object, Object> getCommandButtonArguments(Form form, Collection<AbstractInput<?>> inputs) {
			Map<Object, Object> map = super.getCommandButtonArguments(form, inputs);
			map.put(CommandButton.FIELD_VALUE, "Créer");
			return map;
		}
	}
	
	@Getter @Setter
	public static class Data {
		@NotNull @Input @InputChoice @InputChoiceOne @InputChoiceOneCombo private OperationType type;
		@Input @InputText private String code;
		@Input @InputText private String name;
		@NotNull @Input @InputText private String reason;
		
		public static final String FIELD_TYPE = "type";
		public static final String FIELD_CODE = "code";
		public static final String FIELD_NAME = "name";
		public static final String FIELD_REASON = "reason";
	}
	
	public static final String OUTCOME = "operationCreateView";
}