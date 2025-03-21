package com.example.gateway.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <get session="13617162" />
 */
public class GetDto {

    @JacksonXmlProperty(isAttribute = true)
    private String session;

    public String getSession() { return session; }

}
