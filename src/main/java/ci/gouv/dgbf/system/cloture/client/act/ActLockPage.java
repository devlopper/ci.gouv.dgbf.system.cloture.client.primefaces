package ci.gouv.dgbf.system.cloture.client.act;

import java.io.Serializable;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;

import org.cyk.utility.__kernel__.session.SessionHelper;
import org.cyk.utility.__kernel__.user.interface_.message.MessageRenderer;
import org.cyk.utility.__kernel__.user.interface_.message.RenderType;
import org.cyk.utility.__kernel__.user.interface_.message.Severity;
import org.cyk.utility.client.controller.web.jsf.Redirector;
import org.cyk.utility.client.controller.web.jsf.primefaces.AbstractPageContainerManagedImpl;
import org.cyk.utility.rest.ResponseHelper;
import org.cyk.utility.service.client.SpecificServiceGetter;

import ci.gouv.dgbf.system.cloture.client.operation.OperationListPage;
import ci.gouv.dgbf.system.cloture.server.api.service.ActService;
import ci.gouv.dgbf.system.cloture.server.client.rest.Act;
import lombok.Getter;
import lombok.Setter;

@Named @ViewScoped @Getter @Setter
public class ActLockPage extends AbstractPageContainerManagedImpl implements Serializable {

	@Inject private SpecificServiceGetter specificServiceGetter;

	@Override
	protected String __getWindowTitleValue__() {
		return "Verouillage d'acte";
	}
	
	public void lock() {
		ActService service = (ActService) specificServiceGetter.get(Act.class);
		try {
			service.lock(List.of(), SessionHelper.getUserName());
			Redirector.getInstance().redirect(new Redirector.Arguments().setOutcome(OperationListPage.OUTCOME));			
		} catch (WebApplicationException exception) {
			String message = ResponseHelper.getEntity(String.class, exception.getResponse());
			__inject__(MessageRenderer.class).render(message,Severity.ERROR,List.of(RenderType.INLINE, RenderType.DIALOG));
		}
	}
}