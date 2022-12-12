package de.matul.lepton_sim;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Expander {

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
    public void expand(String[] args) throws IOException, TemplateException {

        Properties p = new Properties();
        p.load(new FileReader(args[1]));

        Template temp = cfg.getTemplate(p.get("template") + ".template");

        /* Create a data-model */
        Map<Object, Object> root = new HashMap<>(p);
        root.put("toInt", (TemplateMethodModelEx) arguments -> Integer.parseInt(arguments.get(0).toString()));
        root.put("toList", (TemplateMethodModelEx) arguments -> Arrays.asList(arguments.get(0).toString().split(" *, *")));

        /* Merge data-model with template */
        Writer out = new OutputStreamWriter(System.out);
        temp.process(root, out);

        // Note: Depending on what `out` is, you may need to call `out.close()`.
        // This is usually the case for file output, but not for servlet output.
    }
}
