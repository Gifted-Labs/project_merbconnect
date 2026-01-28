package com.merbsconnect.events.service;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.SponsorApplicationRequest;
import com.merbsconnect.events.dto.request.VolunteerApplicationRequest;

public interface ApplicationService {
    MessageResponse applyToVolunteer(VolunteerApplicationRequest request);

    MessageResponse applyToSponsor(SponsorApplicationRequest request);
}
