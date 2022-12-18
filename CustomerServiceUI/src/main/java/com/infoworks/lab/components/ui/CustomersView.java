package com.infoworks.lab.components.ui;

import com.infoworks.lab.components.crud.Configurator;
import com.infoworks.lab.components.crud.Crud;
import com.infoworks.lab.components.crud.components.datasource.DefaultDataSource;
import com.infoworks.lab.components.crud.components.datasource.GridDataSource;
import com.infoworks.lab.components.crud.components.utils.EditorDisplayType;
import com.infoworks.lab.components.db.source.JsqlDataSource;
import com.infoworks.lab.components.presenters.CustomerEditor;
import com.infoworks.lab.components.rest.source.RestDataSource;
import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.executor.CustomerExecutor;
import com.infoworks.lab.domain.repository.WebSocketRepository;
import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.layouts.RootAppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.infoworks.lab.rest.models.Message;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Route(value = RoutePath.CUSTOMERS_CRUD_VIEW, layout = RootAppLayout.class)
public class CustomersView extends Composite<Div> {

    public CustomersView() {
        super();
    }

    private static Logger LOG = Logger.getLogger(CustomersView.class.getSimpleName());

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        System.out.println("onAttach");
        //
        if (getContent().getChildren().count() > 0){
            getContent().removeAll();
        }
        //Create DataSource:
        GridDataSource source = createDataSource(ExecutorType.REST);

        Configurator configurator = new Configurator(Customer.class)
                .setDisplayType(EditorDisplayType.EMBEDDED)
                .setDataSource(source)
                .setEditor(CustomerEditor.class)
                .setDialog(CustomerEditor.class)
                .setGridPageSize(8);

        Crud crud = new Crud(configurator);
        getContent().add(crud);
        subscribeToSocket();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        unsubscribeToSocket();
        super.onDetach(detachEvent);
    }

    private void unsubscribeToSocket() {
        Object obj = UI.getCurrent().getSession().getAttribute("web_socket");
        if (Objects.isNull(obj)) return;
        ((WebSocketRepository) obj).getSocket().unsubscribe("/user/queue/async/event");
    }

    private void subscribeToSocket() {
        Object obj = UI.getCurrent().getSession().getAttribute("web_socket");
        if (Objects.isNull(obj)) return;
        ((WebSocketRepository) obj).getSocket().subscribe("/user/queue/async/event", Message.class, (msg) -> {
            if (msg != null){
                Notification notification = Notification.show(msg.getPayload());
                notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            }
        });
    }

    private GridDataSource createDataSource(ExecutorType executorType){
        if(executorType == ExecutorType.REST) {
            //Fetching Data From WebService:
            JsqlDataSource source = JsqlDataSource.createDataSource(RestDataSource.class, executorType);
            source.setExecutor(new CustomerExecutor());
            return source;
        }else{
            //In-Memory DataSource:
            GridDataSource source = new DefaultDataSource();
            getCustomers().stream().forEach(customer -> source.save(customer));
            return source;
        }
    }

    private List<Customer> getCustomers() {
        List<Customer> personList = new ArrayList<>();
        personList.add(new Customer("Lucas", Gender.MALE, 68));
        personList.add(new Customer("Peter", Gender.MALE, 38));
        personList.add(new Customer("Jack", Gender.MALE, 28));
        personList.add(new Customer("Samuel", Gender.MALE, 53));
        return personList;
    }
}
