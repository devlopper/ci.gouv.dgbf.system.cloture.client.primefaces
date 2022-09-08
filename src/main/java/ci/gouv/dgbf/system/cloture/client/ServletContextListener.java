package ci.gouv.dgbf.system.cloture.client;

import java.io.Serializable;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;

import org.cyk.user.interface_.theme.web.jsf.primefaces.atlantis.ThemeManager;
import org.cyk.user.interface_.theme.web.jsf.primefaces.atlantis.dgbf.DesktopDefault;
import org.cyk.utility.__kernel__.DependencyInjection;
import org.cyk.utility.client.controller.component.menu.MenuBuilderMapInstantiator;
import org.cyk.utility.client.deployment.AbstractServletContextListener;

import ci.gouv.dgbf.system.cloture.client.operation.OperationListPage;

@WebListener
public class ServletContextListener extends AbstractServletContextListener implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void __initialize__(ServletContext context) {
		super.__initialize__(context);
		DependencyInjection.setQualifierClassTo(ci.gouv.dgbf.system.cloture.server.api.System.class, MenuBuilderMapInstantiator.class/*,EntitySaver.class*/);
		
		DesktopDefault.initialize(null,null);
		org.cyk.utility.security.keycloak.client.ApplicationScopeLifeCycleListener.enable(context, "/keycloak/*","/private/*");
		ThemeManager.INDEX_VIEW_OUTCOME.set(OperationListPage.OUTCOME);
		/*
		DesktopDefault.MENU_IDENTIFIER = ConfigurationHelper.getValueAsString(VariableName.USER_INTERFACE_THEME_MENU_IDENTIFIER);
		DesktopDefault.DYNAMIC_MENU = ConfigurationHelper.is(VariableName.USER_INTERFACE_THEME_MENU_IS_DYNAMIC);
		DesktopDefault.IS_SHOW_USER_MENU = DesktopDefault.DYNAMIC_MENU;
		if(DesktopDefault.DYNAMIC_MENU) {
			
		}else {
			DesktopDefault.SYSTEM_LINK = "#";
		}
		*/
		//ClientRequestFilterImpl.LOGGABLE = Boolean.TRUE;
		//ClientRequestFilterImpl.LOG_LEVEL = Level.INFO;
		
		//org.cyk.utility.service.client.ClientRequestFilter.LOG_LEVEL = java.util.logging.Level.INFO;
	}	
}