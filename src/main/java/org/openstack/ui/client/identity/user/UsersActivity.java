package org.openstack.ui.client.identity.user;

import org.openstack.admin.client.Administration;
import org.openstack.model.identity.UserForCreate;
import org.openstack.ui.client.compute.common.AdministrationPlace;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class UsersActivity extends AbstractActivity implements UsersView.Presenter, CreateUser.Listener {
	
	public static final UsersView VIEW = new UsersView();

	private AdministrationPlace place;

	public UsersActivity(AdministrationPlace place) {
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		VIEW.setPresenter(this);
		panel.setWidget(VIEW);
	}

	@Override
	public void onCreate() {
		CreateUser widget = new CreateUser(this);
		Administration.MODAL.setWidget(widget);
		Administration.MODAL.center();
		
	}

	@Override
	public void onDelete() {
		
		
	}

	@Override
	public void onRefresh() {
		
	}

	@Override
	public void onSave(UserForCreate tenant) {
		Administration.MODAL.hide();
		VIEW.refresh();
		
	}
	
}
