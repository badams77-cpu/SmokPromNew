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

    @GetMapping(value = "/webfonts/{file}.eot",  produces ="application/vnd.ms-fontobject")
    @ResponseBody
    public Resource serveEotContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/webfonts/"+file+".eot");
    }

    @GetMapping(value = "/webfonts/{file}.ttf",  produces ="application/x-font-ttf")
    @ResponseBody
    public Resource serveTTFContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/webfonts/"+file+".ttf");
    }

    @GetMapping(value = "/webfonts/{file}.woff",  produces ="application/x-font-woff")
    @ResponseBody
    public Resource serveWOFFContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/webfonts/"+file+".woff");
    }

    @GetMapping(value = "/webfonts/{file}.woff2",  produces ="font/woff2")
    @ResponseBody
    public Resource serveWOFF2Content(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/webfonts/"+file+".woff2");
    }

    @GetMapping(value = "/js/{file}",  produces ="text/javascript")
    @ResponseBody
    public Resource serveJSContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/js/"+file);
    }

    @GetMapping(value = "/fonts/{file}.w0ff2",  produces = "font/woff2")
    @ResponseBody
    public Resource serveFontWoff2Content(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/fonts/"+file+".ttf");
    }

    @GetMapping(value = "/fonts/{file}.ttf",  produces = "application/x-font-ttf")
    @ResponseBody
    public Resource serveFontContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/fonts/"+file+".ttf");
    }

    @GetMapping(value = "/css/{file}",  produces ="text/css")
    @ResponseBody
    public Resource serveCSSContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/"+file);
    }

    @GetMapping(value = "/css/slick-slider/{file}",  produces ="text/css")
    @ResponseBody
    public Resource serveSlickCSSContent(@PathVariable String file) {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/slick-slider/"+file);
    }

    @GetMapping(value = "/css/slick-slider/fonts/slick.eot",  produces ="application/vnd.ms-fontobject")
    @ResponseBody
    public Resource serveSlickEotCSSContent() {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/slick-slider/fonts/slide.eot");
    }

    @GetMapping(value = "/css/slick-slider/fonts/slick.svg",  produces ="image/svg+xml")
    @ResponseBody
    public Resource serveSlickSvgCSSContent() {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/slick-slider/fonts/slick.svg");
    }

    @GetMapping(value = "/css/slick-slider/fonts/slick.ttf",  produces ="application/x-font-ttf")
    @ResponseBody
    public Resource serveSlickTTFCSSContent() {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/slick-slider/css/fonts/slick.ttf");
    }

    @GetMapping(value = "/css/slick-slider/fonts/slick.woff",  produces ="application/x-font-woff")
    @ResponseBody
    public Resource serveSlickWoffCSSContent() {
        // Load and return your static HTML file
        return resourceLoader.getResource("classpath:/static/css/slick-slider/css/font/slick.woff");
    }
}

