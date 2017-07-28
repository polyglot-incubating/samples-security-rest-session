package org.chiwooplatform.samples.support;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

public class DefaultCorsConfiguration implements CorsConfigurationSource {

    private final String[] EXPOSED_RESPONSE_HEADERS = new String[] { "Authorization",
            "Location", "Proxy-Location", "x-requested-with" };

    @Override
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader(request.getHeader("Origin"));
        for (String header : EXPOSED_RESPONSE_HEADERS) {
            configuration.addAllowedHeader(header);
            configuration.addExposedHeader(header);
        }
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("*");
        configuration.setAllowCredentials(true);
        return configuration;
    }
}
