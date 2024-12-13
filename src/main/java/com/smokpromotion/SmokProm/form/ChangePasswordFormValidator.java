package com.smokpromotion.SmokProm.form;

import com.smokpromotion.SmokProm.config.Md5PasswordEncoder;
import com.smokpromotion.SmokProm.domain.entity.S_User;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;

import com.smokpromotion.SmokProm.util.SecVnEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ChangePasswordFormValidator implements Validator {

    public static final String PASSWORD_FORM_CURRENT_DOES_NOT_MATCH = "Passwords Do not match";
    public static final String APROBLEM = "m.EncounteredAProblem";
    private REP_UserService dsUserService;
    private PasswordPolicyService passwordPolicyService;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

    @Autowired
    public ChangePasswordFormValidator(
            REP_UserService dsUserService,
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
    public void validate(Object target, Errors errors)  {

        ChangePasswordForm form = (ChangePasswordForm) target;

        /*
        current
         */

        // will cause an error if the current is empty
        genericValidation(errors, "current", form.getCurrent(), form.isPolicy());

        // Check that current matches the users current password.
        // Only do if a value has been passed for current. If this is empty, the above validation will have an error for that case
        S_User user = null;
        try {
            user = dsUserService.getById(form.getUserId());
        } catch (Exception e){}
        if (!form.getCurrent().isEmpty()) {
                switch (SecVnEnum.getFromCode(user.getSecVn())) {
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


        /*
        neww
         */

        genericValidation(errors, "neww", form.getNeww(), form.isPolicy());

        // Ensure that the new password does not match the existing password.





        /*
        repeat
         */

        genericValidation(errors, "repeat", form.getRepeat(), form.isPolicy());

        // Check that repeat matches neww.

        if (!form.getNeww().equals(form.getRepeat())) {
            errors.rejectValue("repeat", PASSWORD_FORM_CURRENT_DOES_NOT_MATCH);
        }

        if (form.isPolicy() && !form.getNeww().isEmpty()) {
            if (!passwordPolicyService.passwordValidByPolicy(form.getNeww())) {
                errors.rejectValue("neww", "Password not strong enough");
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
