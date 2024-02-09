package de.matul.lepton_sim;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModelEx;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class NetlistCompiler {

    private static final Configuration cfg;

    static {
        /* Create and adjust the configuration singleton */
        cfg = new Configuration(Configuration.VERSION_2_3_29);
        try {
            cfg.setDirectoryForTemplateLoading(new File("templates"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
    }

    public String expand(String arg) throws IOException, TemplateException {

        Properties p = new Properties();
        p.load(new FileReader(arg));

        Template temp = cfg.getTemplate(p.get("template") + ".template");

        /* Create a data-model */
        Map<Object, Object> root = new HashMap<>(p);
        root.put("toInt", (TemplateMethodModelEx) arguments -> Integer.parseInt(arguments.get(0).toString()));
        root.put("toList", (TemplateMethodModelEx) this::toList);
        root.put("toPair", (TemplateMethodModelEx) arguments -> new Pair(arguments));

        /* Merge data-model with template */
        Writer out = new StringWriter();
        temp.process(root, out);
        // Note: Depending on what `out` is, you may need to call `out.close()`.
        // This is usually the case for file output, but not for servlet output.

        return out.toString();
    }

    private List<String> toList(List<?> arguments) {
        if (arguments == null || arguments.size() != 1) {
            throw new RuntimeException("Expected exactly one argument");
        }
        if (arguments.get(0) == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(arguments.get(0).toString().split(" *, *"));
    }

    public static final class Pair {
        public final String from;

        public final String to;

        Pair(List s) {
            String[] parts = s.get(0).toString().split(" *-> *", 2);
            this.from = parts[0];
            if (parts.length > 1) {
                this.to = parts[1];
            } else if (s.size() > 1) {
                this.to = s.get(1).toString();
            } else {
                this.to = "";
            }
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }
}
