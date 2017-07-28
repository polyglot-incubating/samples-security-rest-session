package org.chiwooplatform.samples.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserToken {
    private String token;
    private Long expires;
}
