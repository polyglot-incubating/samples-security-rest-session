package org.chiwooplatform.security.support.web;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import org.chiwooplatform.context.support.DateUtils;
import org.chiwooplatform.security.authentication.RestAuthenticationToken;
import org.chiwooplatform.security.authentication.UserAuthentication;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private RequestCache requestCache = new HttpSessionRequestCache();

  private final ObjectMapper objectMapper;

  public RestAuthenticationSuccessHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private String getJson(final Authentication authentication) {
    if (authentication instanceof RestAuthenticationToken) {
      RestAuthenticationToken restAuthentication = (RestAuthenticationToken) authentication;
      final Long expires = restAuthentication.getExpires();
      final String expiresValue = DateUtils.getFormattedString(expires);
      final String principal = restAuthentication.getName();
      final String token = restAuthentication.getToken();
      // UserProfile user = (UserProfile)restAuthentication.getDetails();
      UserAuthentication auth = new UserAuthentication(principal, token, expiresValue);
      StringWriter writer = new StringWriter();
      try {
        this.objectMapper.writeValue(writer, auth);
      } catch (Exception e) {
        logger.error(e.getMessage());
        return null;
      }
      String json = writer.toString();
      return json;
    }
    return null;

  }

  protected void handle(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    // Authentication aa = SecurityContextHolder.getContext().getAuthentication();
    // logger.info("xxxxxxxxxxxxxxxxx isAuthenticated(): " + aa.isAuthenticated());
    // logger.info("xxxxxxxxxxxxxxxxx getAuthorities(): " + aa.getAuthorities());
    String json = getJson(authentication);
    SavedRequest savedRequest = requestCache.getRequest(request, response);
    if (savedRequest == null) {
      clearAuthenticationAttributes(request);
      if (!StringUtils.isEmpty(json)) {
        response.getOutputStream().print(json);
      }
      return;
    }
    String targetUrlParam = getTargetUrlParameter();
    if (isAlwaysUseDefaultTargetUrl()
        || (targetUrlParam != null && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
      requestCache.removeRequest(request, response);
      clearAuthenticationAttributes(request);
      if (!StringUtils.isEmpty(json)) {
        response.getOutputStream().print(json);
      }
      return;
    }
    clearAuthenticationAttributes(request);
    if (!StringUtils.isEmpty(json)) {
      response.getOutputStream().print(json);
    }
  }

}
