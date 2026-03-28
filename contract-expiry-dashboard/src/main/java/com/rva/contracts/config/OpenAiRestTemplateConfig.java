package com.rva.contracts.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiRestTemplateConfig {

  @Bean
  @Qualifier("openAiRestTemplate")
  public RestTemplate openAiRestTemplate(RestTemplateBuilder builder, OpenAiProperties props) {
    return builder
        .setConnectTimeout(props.getConnectTimeout())
        .setReadTimeout(props.getReadTimeout())
        .build();
  }
}
