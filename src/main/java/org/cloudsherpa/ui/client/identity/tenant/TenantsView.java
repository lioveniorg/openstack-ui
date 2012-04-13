package org.cloudsherpa.ui.client.identity.tenant;

import java.util.List;

import org.cloudsherpa.admin.client.Administration;
import org.openstack.model.identity.Tenant;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

public class TenantsView extends Composite {

	private static Binder uiBinder = GWT.create(Binder.class);

	interface Binder extends UiBinder<Widget, TenantsView> {
	}
	
	public interface Presenter {
		void onCreate();
		void onDelete();
		void onRefresh();
	}
	
	@UiField Button create;
	@UiField Button delete;
	@UiField Button refresh;
	
	@UiField TenantDetails details;
	
	@UiField(provided = true) DataGrid<Tenant> grid;
	
	MultiSelectionModel<Tenant> selectionModel;
	
	DefaultSelectionEventManager<Tenant> selectionEventManager = DefaultSelectionEventManager.createCheckboxManager(0);
	
	AsyncDataProvider<Tenant> asyncDataProvider = new AsyncDataProvider<Tenant>() {

		@Override
		protected void onRangeChanged(HasData<Tenant> display) {
			
			final Range range = display.getVisibleRange();
			
			Administration.CLOUD.listTenants(range.getStart(), range.getLength(), new AsyncCallback<List<Tenant>>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					
				}

				@Override
				public void onSuccess(List<Tenant> result) {
					updateRowData(range.getStart(), result);
					updateRowCount(range.getLength(), true);
					
				}
			});
			
		}

	};
	
	Presenter presenter;

	public TenantsView() {
		createGrid();
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@UiHandler("create")
	void onCreateClick(ClickEvent event) {
		presenter.onCreate();
	}
	
	@UiHandler("delete")
	void onDeleteClick(ClickEvent event) {
		presenter.onDelete();
	}
	
	@UiHandler("refresh")
	void onRefreshClick(ClickEvent event) {
		//grid.setVisibleRangeAndClearData(grid.getVisibleRange(), true);
		RangeChangeEvent.fire(grid, grid.getVisibleRange());
		presenter.onRefresh();
	}
	
	private void update() {
		switch (selectionModel.getSelectedSet().size()) {
		case 0:
			delete.setEnabled(false);
			break;
		case 1:
			delete.setEnabled(true);
			break;
		default:
			delete.setEnabled(true);
			break;
		}
	}
	
	private void createGrid() {
		grid = new DataGrid<Tenant>();
		selectionModel = new MultiSelectionModel<Tenant>();
		Column<Tenant, Boolean> checkboxColumn = new Column<Tenant, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(Tenant object) {
				return selectionModel.isSelected(object);
			}
		};
		grid.setColumnWidth(checkboxColumn, "40px");
		grid.addColumn(checkboxColumn, "");
		TextColumn<Tenant> nameColumn = new TextColumn<Tenant>() {
			@Override
			public String getValue(Tenant object) {
				return object.getName();
			}
		};
		grid.setColumnWidth(nameColumn, "120px");
		grid.addColumn(nameColumn, "Name");
		TextColumn<Tenant> descriptionColumn = new TextColumn<Tenant>() {
			@Override
			public String getValue(Tenant object) {
				return object.getName();
			}
		};
		grid.setColumnWidth(descriptionColumn, "120px");
		grid.addColumn(descriptionColumn, "Description");
		TextColumn<Tenant> enabledColumn = new TextColumn<Tenant>() {
			@Override
			public String getValue(Tenant object) {
				return object.isEnabled() ? "ENABLED" : "DISABLED";
			}
		};
		grid.setColumnWidth(enabledColumn, "120px");
		grid.addColumn(enabledColumn, "STATUS");
		ButtonCell previewButton = new ButtonCell();
		Column<Tenant,String> preview = new Column<Tenant,String>(previewButton) {
		  public String getValue(Tenant object) {
		    return "Preview";
		  }
		};
		preview.setFieldUpdater(new FieldUpdater<Tenant, String>() {
		  @Override
		  public void update(int index, Tenant server, String value) {
			  onPreview(server);
		  }
		});
		grid.setColumnWidth(preview, "100px");
		grid.addColumn(preview);
		grid.setSelectionModel(selectionModel, selectionEventManager);
		selectionModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				update();
				
			}
		});
		asyncDataProvider.addDataDisplay(grid); 
	}
	
	private void onPreview(Tenant tenant) {
		details.id.setText(tenant.getId());
		details.name.setText(tenant.getName());
		details.description.setText(tenant.getDescription());
		details.enabled.setText(String.valueOf(tenant.isEnabled()));
	}
	
}


