package com.infoworks.lab.services.definition;

import com.infoworks.lab.domain.models.Email;

public interface iEmailSender {
    int sendHtmlMessage(Email email);
}
