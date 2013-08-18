package me.artspb.gwt.keepass.client.ui;

import com.github.gwtbootstrap.datetimepicker.client.ui.DateTimeBox;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import java.util.Date;

/**
 * @author Artem Khvastunov
 */
public class ReadOnlyDateTimeBox extends DateTimeBox {
    
    public ReadOnlyDateTimeBox(final Date content) {
        setValue(content);
        setReadOnly(true);
        setAutoClose(true);
        addValueChangeHandler(new ValueChangeHandler<Date>() {
            public void onValueChange(ValueChangeEvent<Date> event) {
                setValue(content);
            }
        });
    }
}
