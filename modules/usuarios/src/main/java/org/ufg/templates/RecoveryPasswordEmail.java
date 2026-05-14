package org.ufg.templates;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

public class RecoveryPasswordEmail {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance recovery(String code);
    }
}
