package org.chiwooplatform.samples.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;

/**
 * Created by seonbo.shim on 2017-07-06.
 */
@JsonRootName("simpleCredentials")
@Data
public class SimpleCredentials {

    private String username;

    private String password;
}
