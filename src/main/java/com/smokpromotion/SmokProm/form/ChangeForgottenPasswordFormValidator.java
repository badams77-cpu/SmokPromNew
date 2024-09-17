package com.smokpromotion.SmokProm.form;

import com.urcompliant.domain.entity.DE_DentistPortalUser;
import com.urcompliant.domain.entity.DE_User;
import com.urcompliant.domain.repository.DR_DentistPortalUser;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.service.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ChangeForgottenPasswordFormValidator implements Validator {

    private DS_UserService dsUserService;
    private DR_DentistPortalUser drDentistPortalUser;
    private PasswordPolicyService passwordPolicyService;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public ChangeForgottenPasswordFormValidator(
            DS_UserService dsUserService,
            PasswordPolicyService passwordPolicyService,
            DR_DentistPortalUser drDentistPortalUser) {

        this.dsUserService = dsUserService;
        this.passwordPolicyService = passwordPolicyService;
        this.drDentistPortalUser = drDentistPortalUser;

    }

    // ---------------------------------------------------------------------------------------------
    // Public Methods - Overrides
    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean supports(Class<?> clazz) {
        return UserForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ChangePasswordForm form = (ChangePasswordForm) target;



        boolean valid = genericValidation(errors, "current", form.getCurrent());

        // Check that current matches the users current password.
        Optional<DE_User> optUser = dsUserService.getById(form.getPortal(), form.getUserId());

        genericValidation(errors, "neww", form.getNeww());

        genericValidation(errors, "repeat", form.getRepeat());

        // Check that repeat matches neww.

        if (!form.getNeww().equals(form.getRepeat())) {
            errors.rejectValue("repeat", "section.changePasswordForm.repeat.doesNotMatch");
        }

        if (form.isPolicy()) {
            if (!passwordPolicyService.passwordValidByPolicy(form.getNeww())) {
                errors.rejectValue("neww", "section.changePasswordForm.neww.inadequate");
            }
        }

    }

    public void validateDentistPortal(Object target, Errors errors) {

        ChangePasswordForm form = (ChangePasswordForm) target;



        boolean valid = genericValidation(errors, "current", form.getCurrent());

        // Check that current matches the users current password.
        Optional<DE_DentistPortalUser> optUser = drDentistPortalUser.getById(form.getPortal(), form.getUserId(),false);

        genericValidation(errors, "neww", form.getNeww());

        genericValidation(errors, "repeat", form.getRepeat());

        // Check that repeat matches neww.

        if (!form.getNeww().equals(form.getRepeat())) {
            errors.rejectValue("repeat", "section.changePasswordForm.repeat.doesNotMatch");
        }

        if (form.isPolicy()) {
            if (!passwordPolicyService.passwordValidByPolicy(form.getNeww())) {
                errors.rejectValue("neww", "section.changePasswordForm.neww.inadequate");
            }
        }

    }

    private boolean genericValidation(Errors errors, String field, String value) {

        boolean valid = true;

        if (value.isEmpty()) {
            errors.rejectValue(field, "section.changePasswordForm." + field + ".empty");
            valid = false;
        } else if (value.length() < 6) {
            errors.rejectValue(field, "section.changePasswordForm.tooShort");
            valid = false;
        } else if (value.length() > 100) {
            errors.rejectValue(field, "section.changePasswordForm.tooLong");
            valid = false;
        }

        return valid;

    }

}
