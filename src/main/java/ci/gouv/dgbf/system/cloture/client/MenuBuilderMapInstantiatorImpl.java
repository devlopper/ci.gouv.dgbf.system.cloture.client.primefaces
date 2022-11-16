package ci.gouv.dgbf.system.cloture.client;

import java.io.Serializable;
import java.security.Principal;

import org.cyk.utility.__kernel__.icon.Icon;
import org.cyk.utility.client.controller.component.menu.MenuBuilder;
import org.cyk.utility.client.controller.component.menu.MenuItemBuilder;

import ci.gouv.dgbf.system.cloture.client.act.ActListPage;
import ci.gouv.dgbf.system.cloture.client.imputation.ImputationListPage;
import ci.gouv.dgbf.system.cloture.client.operation.OperationListPage;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Act;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Imputation;
import ci.gouv.dgbf.system.cloture.server.api.persistence.Operation;

@ci.gouv.dgbf.system.cloture.server.api.System
public class MenuBuilderMapInstantiatorImpl extends org.cyk.utility.client.controller.component.menu.AbstractMenuBuilderMapInstantiatorImpl implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void __instantiateSessionMenuBuilderItems__(Object key, MenuBuilder sessionMenuBuilder, Object request,Principal principal) {
		sessionMenuBuilder.addItems(
				__inject__(MenuItemBuilder.class).setCommandableName("Cloture").setCommandableIcon(Icon.SUITCASE)
				.addChild(
						/*__inject__(MenuItemBuilder.class).setCommandableName("Opérations").setCommandableNavigationIdentifier("operationListView").setCommandableIcon(Icon.LIST)
						,*/__inject__(MenuItemBuilder.class).setCommandableName(Operation.NAME_PLURAL).setCommandableNavigationIdentifier(OperationListPage.OUTCOME).setCommandableIcon(Icon.BUILDING)
						,__inject__(MenuItemBuilder.class).setCommandableName(Act.NAME_PLURAL).setCommandableNavigationIdentifier(ActListPage.OUTCOME).setCommandableIcon(Icon.FILE)
						,__inject__(MenuItemBuilder.class).setCommandableName(Imputation.NAME_PLURAL).setCommandableNavigationIdentifier(ImputationListPage.OUTCOME).setCommandableIcon(Icon.FILE)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Finex").setCommandableNavigationIdentifier("activityCostUnitFundingEditAdjustmentsFinexView").setCommandableIcon(Icon.PENCIL)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Lignes").setCommandableNavigationIdentifier("activityCostUnitFundingListView").setCommandableIcon(Icon.LIST)
						//,__inject__(MenuItemBuilder.class).setCommandableName("Incohérences").setCommandableNavigationIdentifier("activityCostUnitFundingDashboardListWhereAvailablePaymentCreditIsNotEnoughView").setCommandableIcon(Icon.EYE)
						)
				);
	}	
}
