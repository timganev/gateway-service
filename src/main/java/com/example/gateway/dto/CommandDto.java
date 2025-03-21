package com.example.gateway.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Represents a single <command id="..."> element.
 * Inside it, we may have <enter> or <get> child nodes.
 */
@JacksonXmlRootElement(localName = "command")
public class CommandDto {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    private EnterDto enter;
    private GetDto get;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EnterDto getEnter() {
        return enter;
    }

    public void setEnter(EnterDto enter) {
        this.enter = enter;
    }

    public GetDto getGet() {
        return get;
    }

    public void setGet(GetDto get) {
        this.get = get;
    }
}
