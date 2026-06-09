package com.rey.template.util;

import com.rey.template.dto.Status;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * ZK converter: maps a Status enum value to an icon class.
 * Usage in ZUL:  <span sclass="@load(row.status, converter='com.rey.template.util.StatusIconConverter')"/>
 */
public class StatusIconConverter implements Converter<String, Status, Component> {

    @Override
    public String coerceToUi(Status status, Component component, BindContext ctx) {
        if (status == null) return "";
        return switch (status) {
            case SUCCESS -> "z-icon-check";
            case PENDING -> "z-icon-clock-o";
            case FAILED  -> "z-icon-exclamation-circle";
        };
    }

    @Override
    public Status coerceToBean(String s, Component component, BindContext ctx) {
        return null; // read-only converter
    }
}
