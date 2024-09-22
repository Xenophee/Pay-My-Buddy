package com.paymybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;


/**
 * Configuration class for web-related beans.
 */
@Configuration
public class WebConfig {

    /**
     * Bean definition for HiddenHttpMethodFilter.
     * This filter allows for HTTP methods such as PUT and DELETE to be used in forms.
     *
     * @return a new instance of HiddenHttpMethodFilter
     */
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}

