package com.smokpromotion.SmokProm.controller.portal;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class StaticContentController {

    private final ResourceLoader resourceLoader;

    public StaticContentController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @GetMapping(value = "/images/{file}",  produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public Resource serveStaticContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/images/"+file);
    }

    @GetMapping(value = "/js/{file}",  produces ="text/javascript")
    @ResponseBody
    public Resource serveJSContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/js/"+file);
    }
}

