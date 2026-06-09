package com.rey.template.util;

import com.rey.template.dto.Status;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * ZK converter: maps a Status enum value to a CSS badge class.
 * Usage in ZUL:  sclass="@load(row.status, converter='com.rey.template.util.StatusBadgeConverter')"
 */
public class StatusBadgeConverter implements Converter<String, Status, Component> {

    @Override
    public String coerceToUi(Status status, Component component, BindContext ctx) {
        if (status == null) return "status-badge";
        return switch (status) {
            case SUCCESS -> "status-badge badge-success";
            case PENDING -> "status-badge badge-pending";
            case FAILED  -> "status-badge badge-failed";
        };
    }

    @Override
    public Status coerceToBean(String s, Component component, BindContext ctx) {
        return null; // read-only converter
    }
}
