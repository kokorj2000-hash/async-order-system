package com.aops.notification_service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long orderId;
    private Long userId;
    private String message;

}
