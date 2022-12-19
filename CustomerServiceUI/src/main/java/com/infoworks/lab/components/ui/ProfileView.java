package com.infoworks.lab.components.ui;

import com.infoworks.lab.domain.repository.WebSocketRepository;
import com.infoworks.lab.layouts.RootAppLayout;
import com.infoworks.lab.layouts.RoutePath;
import com.infoworks.lab.rest.models.Message;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import java.util.Objects;
import java.util.logging.Logger;

@Route(value = RoutePath.PROFILE_VIEW, layout = RootAppLayout.class)
public class ProfileView extends Composite<Div> {

    private static Logger LOG = Logger.getLogger(ProfileView.class.getSimpleName());
    private Span span;

    public ProfileView() {
        span = new Span("Profile");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        LOG.info("onAttach");
        getContent().add(span);
        subscribeToSocket();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        unsubscribeToSocket();
        LOG.info("onDetach");
        super.onDetach(detachEvent);
    }

    private void unsubscribeToSocket() {
        Object obj = UI.getCurrent().getSession().getAttribute("web_socket");
        if (Objects.isNull(obj)) return;
        ((WebSocketRepository) obj).getSocket().unsubscribe("/topic/event");
        LOG.info("unsubscribeToSocket");
    }

    private void subscribeToSocket() {
        Object obj = UI.getCurrent().getSession().getAttribute("web_socket");
        if (Objects.isNull(obj)) return;
        ((WebSocketRepository) obj).getSocket().subscribe("/topic/event", Message.class, (msg) -> {
            if (msg != null){
                //Update In UI-Thread:
                if (getUI().isPresent()) {
                    getUI().get().access(() -> {
                        span.removeAll();
                        span.add("Received: " + msg.getPayload());
                    });
                }
            }
        });
        LOG.info("subscribeToSocket");
    }
}