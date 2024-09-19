package com.smokpromotion.SmokProm.controller.admin;


import com.smokpromotion.SmokProm.config.common.SimpleUserLogger;
import com.smokpromotion.SmokProm.domain.dto.EmailLanguage;
import com.smokpromotion.SmokProm.domain.entity.DE_EmailTemplate;
import com.smokpromotion.SmokProm.domain.repo.DR_EmailTemplate;
import com.smokpromotion.SmokProm.form.EmailTemplateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@Profile(value={"smok_admin"})
@RequestMapping(value="/email-templates")
public class EmailTemplateController extends AdminBaseController {
    private static final int MAX_BODY_LENGTH = 131071;
    private static final int MAX_NAME_LENGTH = 255;
    private static final int MAX_SUBJECT_LENGTH = 255;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailTemplateController.class);

    private final MessageSource messageSource;
    private final DR_EmailTemplate drEmailTemplate;
    private final EmailTemplateValidator validator;

    @Autowired
    public EmailTemplateController(DR_EmailTemplate drEmailTemplate, MessageSource messageSource, SimpleUserLogger simpleUserLogger, EmailTemplateValidator validator) {
        this.drEmailTemplate = drEmailTemplate;
        this.messageSource = messageSource;
        this.validator = validator;
    }

    @RequestMapping(path = "/list")
    public String listTemplates( HttpServletRequest request, Authentication authentication, Model model) throws Exception {
        checkAuthentication(authentication);
        List<DE_EmailTemplate> templateList = drEmailTemplate.getAll();
        model.addAttribute("templates", templateList);
        model.addAttribute( "hasItems", !templateList.isEmpty());
        model.addAttribute("templateForm", new DE_EmailTemplate());
        model.addAttribute("languages", EmailLanguage.values());
        return getBase()+"private/email_templates";
    }


    @RequestMapping(path="/edit/{id}", method= RequestMethod.GET)
    public String editById(HttpServletRequest request, @PathVariable(name="id", required=true) int id, Authentication authentication, Model model) throws Exception {
        checkAuthentication(authentication);
        Optional<DE_EmailTemplate> template = drEmailTemplate.getById(id);
        if (!template.isPresent()){
            LOGGER.error("editById: Template "+id+" not present");
            return "redirect:/email-templates/list";
        }
        model.addAttribute("templateForm", template.orElse(new DE_EmailTemplate()));
        model.addAttribute("languages", EmailLanguage.values());
        return getBase()+"private/edit_email_template";
    }

    @RequestMapping(path="/edit/{id}", method= RequestMethod.POST)
    public String postEdit(
            HttpServletRequest request,
            @PathVariable(name="id", required=true) int id,
            Authentication authentication,
            @ModelAttribute("templateForm") DE_EmailTemplate template,
            BindingResult bindingResult,
            Model model) throws Exception {
        checkAuthentication(authentication);
        validateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("templateForm",template);
            return getBase()+"private/edit_email_template";
        }
        Optional<DE_EmailTemplate> originalTemplateOpt = drEmailTemplate.getById(id);
        if (!originalTemplateOpt.isPresent()){
            LOGGER.error("postEdit: Template "+id+" not present");
            return "redirect:/email-templates/list";
        }
        DE_EmailTemplate originalTemplate = originalTemplateOpt.get();
        originalTemplate.setName(template.getName());
        originalTemplate.setTemplateBody(template.getTemplateBody());
        originalTemplate.setLanguage(template.getLanguage());
        originalTemplate.setSubject(template.getSubject());
        try {
            drEmailTemplate.update( originalTemplate);
        } catch (DuplicateKeyException e) {
            try {
                bindingResult.rejectValue("name", "Name and language already used", "Name and language already used");
                List<DE_EmailTemplate> templateList = drEmailTemplate.getAll();
                model.addAttribute("languages", EmailLanguage.values());
                model.addAttribute("templateForm", template);
                return getBase() + "private/edit_email_template";
            } catch (Exception fl) {
                LOGGER.warn("Error on duplicate", fl);
            }
        } catch (Exception e) {
            LOGGER.warn("Error sql",e);
        }
        return "redirect:/email-templates/list";
    }

    @RequestMapping(path="/add", method= RequestMethod.POST)
    public String add(
            HttpServletRequest request,
            Authentication authentication,
            @ModelAttribute("templateForm") DE_EmailTemplate template,
            BindingResult bindingResult,
            Model model) throws Exception {

        checkAuthentication(authentication);
        validateTemplate(template, bindingResult);
        if (bindingResult.hasErrors()){
            List<DE_EmailTemplate> templateList = drEmailTemplate.getAll();
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("templates", templateList);
            model.addAttribute( "hasItems", !templateList.isEmpty());
            model.addAttribute("templateForm",template);
            return getBase()+"private/email_templates";
        }
        try {
            drEmailTemplate.create( template);
        } catch (DuplicateKeyException e){
            bindingResult.rejectValue("name", "Name and language already used","Name and language already used");
            List<DE_EmailTemplate> templateList = drEmailTemplate.getAll();
            model.addAttribute("languages", EmailLanguage.values());
            model.addAttribute("templates", templateList);
            model.addAttribute( "hasItems", !templateList.isEmpty());
            model.addAttribute("templateForm",template);
            return getBase()+"private/email_templates";
        }
        return "redirect:/email-templates/list";
    }

    private void validateTemplate(DE_EmailTemplate template, BindingResult bindingResult){
        if (template.getName().isEmpty()){
            bindingResult.rejectValue("name","Name is Required","Name is Required");
        }
        if (template.getName().length()>=MAX_NAME_LENGTH){
            bindingResult.rejectValue("name","Name is too long","Name is too long");
        }
        if (template.getTemplateBody().isEmpty()){
            bindingResult.rejectValue("templateBody","Body is Required","Body is Required");
        }
        if (template.getTemplateBody().length()>= MAX_BODY_LENGTH){
            bindingResult.rejectValue("templateBody","Body is too long","Body is too long");
        }
        for(String errors : validator.validate(template.getTemplateBody())){
            bindingResult.rejectValue("templateBody",errors,errors);
        }
        Optional<EmailLanguage> lang = Arrays.stream(EmailLanguage.values()).filter( x->x.getValue().equals(template.getLanguage())).findFirst();
        if (!lang.isPresent()){
            bindingResult.rejectValue("language","please select a language", "please select a language");
        }
        if (template.getSubject().isEmpty()){
            bindingResult.rejectValue("subject","Subject is Required","Subject is Required");
        }
        if (template.getSubject().length()>=MAX_SUBJECT_LENGTH){
            bindingResult.rejectValue("subject","Subject is too long","Subject is too long");
        }
    }



}
