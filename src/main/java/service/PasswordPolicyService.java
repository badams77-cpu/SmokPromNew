package service;

import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import com.urcompliant.config.portal.PortalSecurityPrinciple;
import com.urcompliant.domain.PortalEnum;
import com.urcompliant.domain.entity.DE_User;
import com.urcompliant.domain.service.DS_UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordPolicyService {

    public static final int DAYS_EXPIRE = 90;
    public static final int LENGTH = 9;
    public static final String SPECIAL_CHARS = "!@#$%\\^&*\\-_=+;:,.";

    public static final int REASON_EXPIRED = 1;
    public static final int REASON_INADEQUATE = 2;

    private REP_UserService userService;

    @Autowired
    public PasswordPolicyService(REP_UserService userService) {
        this.userService = userService;
    }

    public boolean userNeedsNewPassword( int userid, String password) {

        boolean userNeedsToPassword = false;

        Optional<S_User> optUser = userService.getById( userid);

        if (optUser.isPresent()) {
            DE_User user = optUser.get();
            if (user.getPassword_policy() == 1 && !passwordValidByPolicy(password)) {
                userNeedsToPassword = true;
            } else if (passwordExpired(user.getPwd_change())) {
                userNeedsToPassword = true;
            }
        }

        return userNeedsToPassword;

    }

    public int userNeedsNewPasswordReason(PortalEnum legacyMpcPortalEnum, int userid, String password) {

        int reason = 0;

        Optional<DE_User> optUser = userService.getById(legacyMpcPortalEnum, userid);

        if (optUser.isPresent()) {
            DE_User user = optUser.get();
            if (user.getPassword_policy() == 1 && !passwordValidByPolicy(password)) {
                reason = REASON_INADEQUATE;
            } else if (passwordExpired(user.getPwd_change())) {
                reason = REASON_EXPIRED;
            }
        }

        return reason;

    }

    private boolean passwordExpired(LocalDate pwdChange) {

        boolean expired = false;

        LocalDate expiredDate = pwdChange.plusDays(DAYS_EXPIRE);

        if (expiredDate.isBefore(LocalDate.now())) {
            expired = true;
        }

        return expired;

    }

    public boolean passwordValidByPolicy(String password) {

        boolean valid = false;

        Pattern lowercasePattern = Pattern.compile("[a-z]");
        Matcher lowercaseMatcher = lowercasePattern.matcher(password);

        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Matcher uppercaseMatcher = uppercasePattern.matcher(password);

        Pattern numberPattern = Pattern.compile("[0-9]");
        Matcher numberMatcher = numberPattern.matcher(password);

        Pattern specialPattern = Pattern.compile("[" + SPECIAL_CHARS + "]");
        Matcher specialMatcher = specialPattern.matcher(password);

        if (password != null && password.length() >= LENGTH && numberMatcher.find() &&
                lowercaseMatcher.find() && uppercaseMatcher.find() && specialMatcher.find()) {
            valid = true;
        }

        return valid;

    }

    public boolean showAdminPage() {

        boolean showAdminPage = false;

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof PortalSecurityPrinciple) {

            PortalSecurityPrinciple principle =
                    (PortalSecurityPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principle.getPortal().equals(PortalEnum.AWS) && (
                    principle.getPracticeGroupId() == 81 && principle.getPracticeRoleIds().contains(252))) {

                // $sessionRole["PracticeRoleName"] == "DentalCorp Admin Access"
                showAdminPage = true;

            }

        }

        return showAdminPage;
    }

    public  int getDaysExpire() {
        return DAYS_EXPIRE;
    }
}
