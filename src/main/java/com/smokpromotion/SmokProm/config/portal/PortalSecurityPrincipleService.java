package com.smokpromotion.SmokProm.config.portal;

import com.smokpromotion.SmokProm.domain.entity.S_User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortalSecurityPrincipleService {

    final String UNKNOWN_TIER = "Unknown";

    public static final String RODERICKS_GROUP_CODE= "Rodericks";
    private static final String GENESIS_GROUP_CODE = "GenesisDental";
    public static final String UNITED_DENTAL_GROUP_CODE = "UnitedDental";
    private static final String PORTMAN_GROUP_CODE = "Portman";

    private static final int NUMBER_OF_PRACTICES_FOR_QUICK_NHS_SETTINGS = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalSecurityPrincipleService.class);


    //private SubscriptionAccessManager subscriptionAccessManager;

    // -----------------------------------------------------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------------------------------------------------

//    @Autowired
    public PortalSecurityPrincipleService(
    ) {


    }

    // -----------------------------------------------------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------------------------------------------------

    public PortalSecurityPrinciple create(S_User legacyUser, String password) {
        PortalSecurityPrinciple authUser = new PortalSecurityPrinciple(
                legacyUser.getId(),
                legacyUser.getUuid(),
                legacyUser.getUsername()
        );


        authUser.setAdminLogged(true);

        /*
        if (legacyUser.isAdminuser()) {
            authUser.setUserLevel(2);
            authUser.setUserleveldesc("administrator");
        } else {
            authUser.setUserLevel(1);
            authUser.setUserleveldesc("standard");
        }

        authUser.setAdminUser(legacyUser.isAdminuser());
        authUser.setOrganization(legacyUser.getOrganization());
        authUser.setNoPII(legacyUser.isNoPII());
        */
//        authUser.setFirstname(legacyUser.getFirstname());
//        authUser.setLastname(legacyUser.getLastname());

        // LanguageSettingEnum lang = authUser.getUserLanguage();
        // Optional<DE_PracticeGroup> group = legacyGroupService.getById(legacyMajoranaPortalEnum, authUser.getPracticeGroupId());

        //if (lang==null){
        //    lang=  group.get().getLanguageSetting();
        //}
        //if (lang!=null) { LocaleContextHolder.setLocale(lang.getLocale()); }
        //authUser.setUserLanguage(lang);
        //authUser.setUserTypeId(legacyUser.getUserid());
        //authUser.setCurrencySymbol(legacyUser.getCurrencySymbolForLanguage());

        //authUser.setPasswordPolicy(legacyUser.getPassword_policy() == 1);
        // Previously we used the user subscription level, since URC-1503 this only comes from the group
        //  authUser.setSubscriptionType(legacyUser.getSubscription_type());

        authUser.setLastLogin(legacyUser.getLastVisit());
/*        if (authUser.isPasswordPolicy()) {
            // Check if the user needs to change their password upon logging in (which is happening now).
            authUser.setUserNeedsToSetPasswordOnLogin(
                    passwordPolicyService.userNeedsNewPassword(legacyMajoranaPortalEnum, legacyUser.getUserid(), password));
            authUser.setUserNeedsToSetPasswordOnLoginReason(
                    passwordPolicyService.userNeedsNewPasswordReason(legacyMajoranaPortalEnum, legacyUser.getUserid(), password)
            );
        }

 */
//        authUser.setSchedulingEnabled(schedulerService.isSchedulingPermitted(authUser));
//        authUser.setSubscriptionExempt(legacyUser.isSubscriptionExempt());
//        authUser = addPracticeGroupDetails(legacyMajoranaPortalEnum, legacyUser, authUser);


        if (authUser != null) {





            if (authUser != null) {

/*                authUser.setManageUsers(legacyUser.isManageUsers() && userHasAllPracticesInGroup(legacyMajoranaPortalEnum, authUser));

                if (authUser.isClientAccess()){ // URC-2479 client access all to have manage users
                    authUser.setManageUsers(true);
                }

                setUserAppFeatureCodesAndPersonas(legacyMajoranaPortalEnum, legacyUser.getPracticeGroupID(), legacyUser.getUserid(), authUser);

                authUser = addUserPracticeRoles(legacyMajoranaPortalEnum, legacyUser, authUser);

                LicenseInfo licenseInfo = dentistPortalLicenseService.getLicenseInfo(legacyMajoranaPortalEnum, legacyUser.getPracticeGroupID(), LocalDate.now().plusDays(1) );
                authUser.setHasDentistPortalModule(licenseInfo.isHasModule());

                DE_Module tierMod = getTierModule(legacyMajoranaPortalEnum, legacyUser.getPracticeGroupID());

                authUser.setTier(getTier(tierMod));
                authUser.setFreeTrialEnd(tierMod != null ? tierMod.getFreeTrialEndDate() : null);
*/
            }

        }

        // still used for the manage users, manage contracts screens
        if (authUser==null){ return null; }

        // Determine admin access permissions (e.g., "users").
//        boolean hasAccessToAllPractices = userHasAllPracticesInGroup(legacyMajoranaPortalEnum, authUser);

//        authUser.setUseMenuApi(subscriptionAccessManager.isUseMenuApi());

/*        if (subscriptionAccessManager.isCheckMajorana1()) {
            if (!authUser.isLiteUser()) {
                if (legacyGrantService.check_access_level(authUser, "users")) {
                    if (hasAccessToAllPractices) {
                        authUser.getAdminAccess().add("users");
                    } else {
                        LOGGER.warn("create: practiceGroupId: " + authUser.getPracticeGroupId() + ", userid " + authUser.getId() + " has Majorana1 manage users, but not access to all practices - manage users forbidden");
                    }
                }
            }

            if (legacyGrantService.check_access_level(authUser, "contracts")) {
                authUser.getAdminAccess().add("contracts");
            }
        }

 */
 /*
        if (subscriptionAccessManager.isCheckMajorana2()){
            if (!authUser.isLiteUser()) {
                if(legacyUser.isManageUsers()) {
                    if (hasAccessToAllPractices) {
                        authUser.getAdminAccess().add("users");
                    } else {
                        LOGGER.warn("create: practiceGroupId: " + authUser.getPracticeGroupId() + ", userid " + authUser.getId() + " has Majorana2 manage users, but not access to all practices - manage users forbidden");
                    }
                }
            }

            if (subscriptionAccessManager.checkAccessMajorana2("/manage-contracts", authUser)){
                authUser.getAdminAccess().add("contracts");
            }
        }
*/

        /*
        if (subscriptionAccessManager.isCheckModules()){
            if(legacyUser.isManageUsers()) {
                if (hasAccessToAllPractices) {
                    authUser.getAdminAccess().add("users");
                } else {
                    LOGGER.warn("create: practiceGroupId: " + authUser.getPracticeGroupId() + ", userid " + authUser.getId() + " has Majorana2 manage users, but not access to all practices - manage users forbidden");
                }
            }

            if (subscriptionAccessManager.checkPathModulesLogic("/manage-contracts", authUser, LocalDate.now())){
                authUser.getAdminAccess().add("contracts");
            }
        }
        */


//        if (authUser.getAdminAccess().contains("users")) {
//            LOGGER.warn("create: practiceGroupId: " + authUser.getPracticeGroupId() + ", userid " + authUser.getId() + " granted manage users access - practice group admin");
//        }

        /*

        // Set NHS Practice field
        if (authUser.getPracticeIds().size()>NUMBER_OF_PRACTICES_FOR_QUICK_NHS_SETTINGS && isKnownCOTSandUDAGroupCode(authUser.getPracticeGroupCode())){
            authUser.setAnyPracticeHasNHSCOTs(true);
            authUser.setAnyPracticeUsesUDAs(true);
            authUser.setAnyPracticeHasNonUDANHS(isKnownCOTSandNHSScotNIGroupCode(authUser.getPracticeGroupCode()));
        } else {
            PracticeNHSData nhsData = getPracticeNHSData(authUser);
            if (nhsData!=null) {
                authUser.setAnyPracticeHasNHSCOTs(nhsData.anyPracticeHasNHSCOTs());
                authUser.setAnyPracticeUsesUDAs(nhsData.anyPracticeUsesUDAs());
                authUser.setAnyPracticeHasNonUDANHS(nhsData.anyPracticeHasNonUDANHS());
            }
        }


         */
        return authUser;

    }

    // Unused - DE_AdminUser should not be used by this service
    //    public PortalSecurityPrinciple create(DE_AdminUser legacyUser, String password) {
    //
    //        PortalSecurityPrinciple authUser = new PortalSecurityPrinciple(
    //                legacyUser.getUserid(),
    //                legacyUser.getUsername(),
    //                0,
    //                true);
    //
    //        PortalEnum legacyMajoranaPortalEnum =
    //               PortalEnum.AWS;
    //
    //        authUser.setAdminLogged(true);
    //
    //            authUser.setUserLevel(2);
    //            authUser.setUserleveldesc("administrator");
    //
    //
    //        authUser.setFirstname(legacyUser.getFirstname());
    //        authUser.setLastname(legacyUser.getLastname());
    //        authUser.setUserLanguage(legacyUser.getLanguageSetting());
    //        authUser.setUserTypeId(legacyUser.getUserid());
    //        authUser.setCurrencySymbol("£");
    //        authUser.setPasswordPolicy(true);
    //        authUser.setSubscriptionType("regular");
    //        authUser.setLastLogin(legacyUser.getLastvisit());
    //        if (authUser.isPasswordPolicy()) {
    //            // Check if the user needs to change their password upon logging in (which is happening now).
    //            authUser.setUserNeedsToSetPasswordOnLogin(
    //                    passwordPolicyService.userNeedsNewPassword(legacyMajoranaPortalEnum, legacyUser.getUserid(), password));
    //            authUser.setUserNeedsToSetPasswordOnLoginReason(
    //                    passwordPolicyService.userNeedsNewPasswordReason(legacyMajoranaPortalEnum, legacyUser.getUserid(), password)
    //            );
    //        }
    //
    //
    //
    //
    //        // still used for the manage users, manage contracts screens
    //        if (authUser != null) {
    //
    //            // Determine admin access permissions (e.g., "users").
    //
    //                    authUser.getAdminAccess().add("users");
    //
    //                authUser.getAdminAccess().add("contracts");
    //        }
    //
    //        return authUser;
    //
    //    }

    /*
    public void setUserAppFeatureCodesAndPersonas(PortalEnum legacyMajoranaPortalEnum, int practiceGroupId, int userId, PortalSecurityPrinciple authUser) {
        Set<AppFeaturePersona> personas = drUserApplicationFeature.getByUser(legacyMajoranaPortalEnum, practiceGroupId, userId, false).stream()
                .map(x -> ((DE_UserApplicationFeature) x).getAppFeaturePersona()).collect(Collectors.toSet());

        if (authUser.isManageUsers() || (authUser.isClientAccess() && authUser.isSoeOrUrc())) {
            personas.addAll(AppFeaturePersona.getForPracticeGroupAdminDefault());
        }

        authUser.setAllowedFeaturePersonas(personas.stream().collect(Collectors.toMap(x->x, x -> Boolean.TRUE)));
        Set<AppFeatureCode> codes = new HashSet<>();
        for(AppFeaturePersona persona : personas){
            if (persona!=null) { codes.addAll(persona.getAppFeatureCodes()); }
        }
        authUser.setAllowedFeatureCodes(codes.stream().collect(Collectors.toMap(x->x, x -> Boolean.TRUE)));
    }
    */


    // -----------------------------------------------------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------------------------------------------------














}
