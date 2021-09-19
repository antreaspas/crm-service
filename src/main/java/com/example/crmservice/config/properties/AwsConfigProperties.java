package com.example.crmservice.config.properties;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "aws")
@Value
@ConstructorBinding
public class AwsConfigProperties {
    @NotBlank
    String region;
    @NotBlank
    String accessKeyId;
    @NotBlank
    String secretAccessKey;
    @NotBlank
    String bucketName;
    @NotNull
    @Min(1)
    Integer presignedUrlExpiryInMinutes;
    String endpoint;
}
