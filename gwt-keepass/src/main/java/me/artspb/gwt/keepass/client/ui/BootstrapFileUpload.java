package me.artspb.gwt.keepass.client.ui;

import com.github.gwtbootstrap.client.ui.FileUpload;
import org.vectomatic.file.FileUploadExt;

/**
 * @author Artem Khvastunov
 */
public class BootstrapFileUpload extends FileUploadExt {

    public BootstrapFileUpload() {
        super(new FileUpload().getElement(), false);
        setStyleName("fileInput");
    }
}
