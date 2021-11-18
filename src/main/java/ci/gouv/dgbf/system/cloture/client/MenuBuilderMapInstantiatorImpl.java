package ci.gouv.dgbf.system.cloture.client;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;

@ci.gouv.dgbf.system.cloture.server.api.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		sessionMenuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setCommandableName("Cloture").setCommandableIcon(Icon.SUITCASE)
				.addChild(
						__inject__(MenuItemBuilder.class).setCommandableName("Opérations").setCommandableNavigationIdentifier("operationListView").setCommandableIcon(Icon.LIST)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Saisir les resources").setCommandableNavigationIdentifier("activityRevenueCostUnitEditAdjustmentsView").setCommandableIcon(Icon.BANK)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Finex").setCommandableNavigationIdentifier("activityCostUnitFundingEditAdjustmentsFinexView").setCommandableIcon(Icon.PENCIL)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Lignes").setCommandableNavigationIdentifier("activityCostUnitFundingListView").setCommandableIcon(Icon.LIST)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Incohérences").setCommandableNavigationIdentifier("activityCostUnitFundingDashboardListWhereAvailablePaymentCreditIsNotEnoughView").setCommandableIcon(Icon.EYE)
						)
				);
	}	
}
