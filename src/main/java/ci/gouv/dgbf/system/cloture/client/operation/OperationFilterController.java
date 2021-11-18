package ci.gouv.dgbf.system.cloture.client.operation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.cyk.utility.__kernel__.string.StringHelper;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.AbstractFilterController;
import org.cyk.utility.client.controller.web.jsf.primefaces.model.input.AbstractInput;
import org.cyk.utility.persistence.query.Filter;

import ci.gouv.dgbf.system.cloture.server.client.rest.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter @Setter @Accessors(chain=true)
public class OperationFilterController extends AbstractFilterController implements Serializable {

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
		columnsFieldsNames.add(Operation.FIELD_CODE);
		columnsFieldsNames.add(Operation.FIELD_NAME);
		columnsFieldsNames.add(Operation.FIELD_START_DATE_STRING);
		columnsFieldsNames.add(Operation.FIELD_EXECUTION_BEGIN_DATE_STRING);
		columnsFieldsNames.add(Operation.FIELD_EXECUTION_END_DATE_STRING);
		columnsFieldsNames.add(Operation.FIELD_TRIGGER);
		columnsFieldsNames.add(Operation.FIELD_EXECUTION_STATUS);
		return columnsFieldsNames;
	}
	
	/**/
	
	public static Filter.Dto populateFilter(Filter.Dto filter,OperationFilterController controller,Boolean initial) {
		return null;
	}
	
	public static Filter.Dto instantiateFilter(OperationFilterController controller,Boolean initial) {
		return populateFilter(new Filter.Dto(), controller,initial);
	}
}