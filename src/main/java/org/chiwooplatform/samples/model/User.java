package org.chiwooplatform.samples.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class User {
  private Integer id;
  private String username;
  private String firstname;
  private String lastname;
  private String password;
  private String email;
  private String countryCode;
  private String zipCode;
  private String address;
  private String cellphone;
  private String birthdate;
  private String gender;
}
