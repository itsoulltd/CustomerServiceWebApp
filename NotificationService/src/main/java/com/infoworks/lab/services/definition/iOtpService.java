package com.infoworks.lab.services.definition;

import com.infoworks.lab.domain.models.Otp;

public interface iOtpService {
    boolean verify(Otp otp, String forKey);
    Otp storeOtp(String forKey);
}
