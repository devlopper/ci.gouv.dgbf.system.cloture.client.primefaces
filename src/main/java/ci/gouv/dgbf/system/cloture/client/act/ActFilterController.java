package ci.gouv.dgbf.system.cloture.client.act;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class ActFilterController extends AbstractFilterController implements Serializable {

	@Override
	protected void buildInputs() {
		
	}
	
	@Override
	protected AbstractInput<?> buildInput(String arg0, Object arg1) {
		return null;
	}
	
	public String generateWindowTitleValue(String prefix) {
		Collection<String> strings = new ArrayList<>();
		strings.add(prefix);
		
		return StringHelper.concatenate(strings, " | ");
	}
	
	public Collection<String> generateColumnsNames() {
		Collection<String> columnsFieldsNames = new ArrayList<>();
		columnsFieldsNames.add(Act.FIELD_CODE);
		columnsFieldsNames.add(Act.FIELD_NAME);
		columnsFieldsNames.add(Act.FIELD_OPERATION_TYPE);
		columnsFieldsNames.add(Act.FIELD_TRIGGER);
		columnsFieldsNames.add(Act.FIELD_OPERATION_DATE_STRING);
		return columnsFieldsNames;
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,ActFilterController controller,Boolean initial) {
		return null;
	}
	
	public static Filter.Dto instantiateFilter(ActFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
}