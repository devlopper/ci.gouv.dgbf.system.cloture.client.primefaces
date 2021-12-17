package ci.gouv.dgbf.system.cloture.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.field.FieldHelper;
import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.WebController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.cloture.server.api.persistence.Parameters;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import ci.gouv.dgbf.system.cloture.server.client.rest.ActController;
import ci.gouv.dgbf.system.cloture.server.client.rest.ActLock;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ActLockFilterController extends AbstractFilterController implements Serializable {

	private Act actInitial;
	
	public ActLockFilterController() {
		initialize();
	}
	
	@Override
	public AbstractFilterController initialize() {
		if(actInitial == null)
			actInitial = __inject__(ActController.class).getByIdentifier(WebController.getInstance().getRequestParameter(Parameters.ACT_IDENTIFIER));
		return this;
	}
	
	@Override
	protected void buildInputs() {
		
	}
	
	@Override
	protected AbstractInput<?> buildInput(String fieldName, Object value) {
		return null;
	}
	
	@Override
	protected String buildParameterName(String fieldName) {
		if(FIELD_ACT_AUTO_COMPLETE.equals(fieldName))
			return Parameters.ACT_IDENTIFIER;
		return super.buildParameterName(fieldName);
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		if(actInitial != null)
			strings.add(String.format("Acte : %s", actInitial));
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.add(ActLock.FIELD_REASON);
		columnsFieldsNames.add(ActLock.FIELD_ENABLED_STRING); 
		columnsFieldsNames.add(ActLock.FIELD_BEGIN_DATE);
		columnsFieldsNames.add(ActLock.FIELD_END_DATE);		
		columnsFieldsNames.add(ActLock.FIELD_LATEST_OPERATION);
		return columnsFieldsNames;
	}
	
	public Act getAct() {
		return null;
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ActLockFilterController controller,Boolean initial) {
		filter = Filter.Dto.addFieldIfValueNotBlank(Parameters.ACT_IDENTIFIER, FieldHelper.readSystemIdentifier(Boolean.TRUE.equals(initial) ? controller.actInitial : controller.getAct()), filter);
		return filter;
	}
	
	public static Filter.Dto instantiateFilter(ActLockFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
	
	public static final String FIELD_ACT_AUTO_COMPLETE = "actAutoComplete";
}