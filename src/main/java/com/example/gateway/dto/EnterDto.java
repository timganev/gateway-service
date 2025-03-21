package com.example.gateway.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <enter session="13617162">
 *   <timestamp>1586335186721</timestamp>
 *   <player>238485</player>
 * </enter>
 */
public class EnterDto {

    @JacksonXmlProperty(isAttribute = true)
    private String session;

    private long timestamp;
    private String player;

    public String getSession() { return session; }

    public long getTimestamp() { return timestamp; }

    public String getPlayer() { return player; }

}
