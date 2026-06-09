package com.rey.template.util;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.zk.ui.Component;

/**
 * ZK converter: shows "Add Report" when id is null, else "Edit Report".
 * Usage in ZUL:  title="@load(vm.formReport.id, converter='com.rey.template.util.FormTitleConverter')"
 */
public class FormTitleConverter implements Converter<String, Long, Component> {

    @Override
    public String coerceToUi(Long id, Component component, BindContext ctx) {
        return id == null ? "Add Report" : "Edit Report";
    }

    @Override
    public Long coerceToBean(String s, Component component, BindContext ctx) {
        return null; // read-only
    }
}
