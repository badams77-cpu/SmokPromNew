package com.smokpromotion.SmokProm.form;

import com.urcompliant.config.Md5PasswordEncoder;
import com.urcompliant.domain.entity.DE_User;
import com.urcompliant.domain.service.DS_UserService;
import com.urcompliant.service.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ChangePasswordFormValidator implements Validator {

    public static final String PASSWORD_FORM_CURRENT_DOES_NOT_MATCH = "section.changePasswordForm.current.doesNotMatch";
    public static final String APROBLEM = "m.EncounteredAProblem";
    private DS_UserService dsUserService;
    private PasswordPolicyService passwordPolicyService;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public ChangePasswordFormValidator(
            DS_UserService dsUserService,
            PasswordPolicyService passwordPolicyService) {

        this.dsUserService = dsUserService;
        this.passwordPolicyService = passwordPolicyService;

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

        /*
        current
         */

        // will cause an error if the current is empty
        genericValidation(errors, "current", form.getCurrent(), form.isPolicy());

        // Check that current matches the users current password.
        // Only do if a value has been passed for current. If this is empty, the above validation will have an error for that case
        Optional<DE_User> optUser = dsUserService.getById(form.getPortal(), form.getUserId());

        if (!form.getCurrent().isEmpty()) {
            if (optUser.isPresent()) {
                DE_User user = optUser.get();
                switch (user.getSecVNEnum()) {
                    case MD5:
                        Md5PasswordEncoder encoder = new Md5PasswordEncoder();
                        if (!encoder.encode(form.getCurrent()).equals(user.getUserpw())) {
                            errors.rejectValue("current", PASSWORD_FORM_CURRENT_DOES_NOT_MATCH);
                        }
                        break;
                    case BCRYPT:
                        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                        if (!bCryptPasswordEncoder.matches(form.getCurrent(), user.getUserpw())) {
                            errors.rejectValue("current", PASSWORD_FORM_CURRENT_DOES_NOT_MATCH);
                        }
                        break;
                    default:
                        errors.rejectValue(null, APROBLEM);
                }

            } else {
                errors.rejectValue(null, APROBLEM);
            }
        }

        /*
        neww
         */

        genericValidation(errors, "neww", form.getNeww(), form.isPolicy());

        // Ensure that the new password does not match the existing password.

        if (optUser.isPresent()) {

            DE_User user = optUser.get();
            switch(user.getSecVNEnum()) {
                case MD5:
                    Md5PasswordEncoder encoder = new Md5PasswordEncoder();
                    if (encoder.encode(form.getNeww()).equals(user.getUserpw())) {
                        errors.rejectValue("neww", "section.changePasswordForm.neww.sameAsCurrent");
                    }
                    break;
                case BCRYPT:
                    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
                    if (bCryptPasswordEncoder.matches(form.getNeww(),user.getUserpw())) {
                        errors.rejectValue("neww", "section.changePasswordForm.neww.sameAsCurrent");
                    }
                    break;
                default:
                    errors.rejectValue(null, APROBLEM);
            }

        }

        /*
        repeat
         */

        genericValidation(errors, "repeat", form.getRepeat(), form.isPolicy());

        // Check that repeat matches neww.

        if (!form.getNeww().equals(form.getRepeat())) {
            errors.rejectValue("repeat", "section.changePasswordForm.repeat.doesNotMatch");
        }

        if (form.isPolicy() && !form.getNeww().isEmpty()) {
            if (!passwordPolicyService.passwordValidByPolicy(form.getNeww())) {
                errors.rejectValue("neww", "section.changePasswordForm.neww.inadequate");
            }
        }

    }

    private boolean genericValidation(Errors errors, String field, String value, boolean isPolicy) {

        boolean valid = true;

        if (value.isEmpty()) {
            errors.rejectValue(field, "section.changePasswordForm." + field + ".empty");
            valid = false;
        } else if (!isPolicy && value.length() < 6) {
            // if the pwd policy is in place, that will include a length check also
            errors.rejectValue(field, "section.changePasswordForm.tooShort");
            valid = false;
        } else if (value.length() > 100) {
            errors.rejectValue(field, "section.changePasswordForm.tooLong");
            valid = false;
        }

        return valid;

    }

}
