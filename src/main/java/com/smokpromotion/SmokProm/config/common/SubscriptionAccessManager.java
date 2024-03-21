package com.smokpromotion.SmokProm.config.common;

import com.smokpromotion.SmokProm.config.admin.AdminSecurityPrinciple;
import com.smokpromotion.SmokProm.config.portal.PortalSecurityPrinciple;
import com.smokpromotion.SmokProm.domain.entity.DE_ApplicationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.*;


@Service
@Lazy
public class SubscriptionAccessManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionAccessManager.class);

    private static final String CHECK_MPC1_ROLES_ONLY = "CHECK_MPC1_ROLES_ONLY";
    private static final String CHECK_MPC2_ROLES_ONLY = "CHECK_MPC2_ROLES_ONLY";
    private static final String CHECK_MPC1_AND_MPC2_ROLES = "CHECK_MPC1_AND_MPC2_ROLES";
    private static final String CHECK_MPC2_MODULES_ONLY = "CHECK_MPC2_MODULES_ONLY";

    public static final String DENTIST_PORTAL_APP_FEATURE_URLS[] = {"/mpcportal/dentist-portal-dashboard"};

    private final String MKT_PAGE_URL = "/marketing";
    private final String MKT_PAGE_PATH_PARAM = "path";
    private final String MKT_PAGE_ROOT_PATH = "/marketing_pages";

    private final String LOCKED_DOWNPAGES[] = {"/landing-page","/notifications" };


    private final boolean checkMPC1;
    private final boolean checkMPC2;
    private final boolean checkModules;
    private final boolean useMenuApi;

    @Autowired
    public SubscriptionAccessManager(
            @Value("${MPC_ACCESS_CONTROL:null}") String accessSetting,
            @Value("${MPC_MENU_API:false}") boolean useMenuApi){
        this.useMenuApi = useMenuApi;
        if (accessSetting==null){
            checkMPC1=true;
            checkMPC2=false;
            checkModules=false;
            LOGGER.warn("constructor: NO MPC_ACCESS_CONTROL setting use MPC1 access only");
        } else {
            switch (accessSetting){
                case CHECK_MPC1_ROLES_ONLY:
                    checkMPC1=true;
                    checkMPC2=false;
                    checkModules = false;
                    LOGGER.info("constructor: using only MPC1 access");
                    break;
                case CHECK_MPC2_ROLES_ONLY:
                    checkMPC2=true;
                    checkMPC1=false;
                    checkModules = false;
                    LOGGER.info("constructor: using only MPC2 access");
                    break;
                case CHECK_MPC1_AND_MPC2_ROLES:
                    checkMPC1=true;
                    checkMPC2=true;
                    checkModules = false;
                    LOGGER.info("constructor: using both MPC1 and MPC2 access");
                    break;
                case CHECK_MPC2_MODULES_ONLY:
                    checkMPC1=false;
                    checkMPC2=false;
                    checkModules=true;
                    LOGGER.info("constructor: using CHECK MODULES");
                    break;
                default:
                    checkMPC1=true;
                    checkMPC2=false;
                    checkModules = false;
                    LOGGER.warn("constructor: NO MPC_ACCESS_CONTROL setting use MPC1 access only");
                    break;
            }
        }

    }

    public boolean usesMPC1AccessControl(){
        return checkMPC1;
    }

    public boolean usesMPC2OrModulesAccessControl(){
        return checkMPC2 || checkModules;
    }

    public boolean isUserAllowedToPathMV(MultiValueMap<String, String[]> requestParams,
                                       Authentication auth, String path) {
        return true;
    }

    public boolean isUserAllowedToPath(MultiValueMap<String, String> requestParams,
                                       Authentication auth, String path) {
        return true;
    }

        //        boolean ret = false;

        // PortalSecurityPrinciple principle = (PortalSecurityPrinciple)auth.getPrincipal();

        // First allow marketing pages

  /*      if (path.equals(MKT_PAGE_URL)
                && requestParams != null
                && requestParams.containsKey(MKT_PAGE_PATH_PARAM)
                && requestParams.get(MKT_PAGE_PATH_PARAM).length == 1) {

            // URL path is a marketing page - allow if relevant to this user's subscription level

            // URC-2738 - getSubscriptionType call causing null pointer
            //            String mktPagePath = requestParams.get(MKT_PAGE_PATH_PARAM)[0];
            //            String subsType = principle.getSubscriptionType().toLowerCase();
            //            String mktPageSubsLevelRoot = String.format("%s/%s/", MKT_PAGE_ROOT_PATH, subsType);

            //            This line seem to be blocking valid marketing pages x 1`1`
            //            if (mktPagePath.startsWith(mktPageSubsLevelRoot)) {
            // allow users with this subscription level to see the marketing pages relevant to them
            return true;
            //            }
        }
*/
        //       if (dsNotification.isLockedDown(principle.getPracticeGroupCode())){
        //    return Arrays.stream(LOCKED_DOWNPAGES).anyMatch(p->path.startsWith(p));
        //}

//        if (principle.isRegularUser()) {
//
//
//            /** TEMPORARY CODE, ONCE WE MIGRATED THE OLD MPC1 STRUCTURE, THIS BLOCK WILL BE DISABLED */
//            // check whether the user is able to access the path using the MPC1 roles logic
//            ret = checkModules && checkPathModulesLogic( path, principle, LocalDate.now());
//            if (!ret) {
//                ret = checkMPC1 && checkPathMpc1Logic(principle, path);
//            }
//            if (!ret){
//
//                ret = checkMPC2 && checkAccessMPC2(path, principle);
//            }
//
//        } else {
//            // check whether the user is able to access the path based on the role group
//            ret = checkModules && checkPathModulesLogic( path, principle, LocalDate.now());
//
//            if (!checkModules) {
//                // original code always checked checkMPC2Access for lite users, so do this if not using modules
//                ret = checkAccessMPC2(path, principle);
//            }
//
//        }
//        return ret;
//    }

    public boolean isCheckMPC1() {
        return checkMPC1;
    }

    public boolean isCheckMPC2() {
        return checkMPC2;
    }

    public boolean isCheckModules() {
        return checkModules;
    }

    public boolean checkPathModulesLogic(String path, PortalSecurityPrinciple principle, LocalDate when) {
        boolean ret;

//        List<Integer> idRoleGroupsModule = drPracticeGroupModule.findIdRoleGroupsByPracticeGroupAtDate(principle.getPortal(), principle.getPracticeGroupId(), when);
//        List<Integer> idRolesSubscription = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroupsModule);

//        if (!principle.getOrganization().getPortalRoleGroup().equals("")){
//            return checkAccessBySubscriptionRoleGroup(path, principle, idRolesSubscription);
//        }

        // URC-1642 - subscription exempt users should be able to view all
        // Except if they have a custom role group set that applies to them

        if (obtainUserCustomRoleGroups(principle).isEmpty() && isSubscriptionExempt(principle)) {
            return true;
        }

        // URC-2656 - ensure that Tier1 groups cannot access Dentist Portal Dashboard features
//        if (isTier1ForbiddenDentistPortalAccess(principle, path)) {
//            LOGGER.warn("checkPathModulesLogic: practiceGroupId " + principle.getPracticeGroupId() + ", user: " + principle.getEmail() + " group is tier1, denying access to Dentist Portal dashboard: " + path);
//            return false;
//        }



        // Otherwise, check user's roles
        List<Integer> idRoleGroups = obtainUserCustomRoleGroups(principle);
//        if (idRoleGroups.isEmpty()){
//            idRoleGroups = idRoleGroupsModule;
//        }
   /*     List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroups);
        // Here we disallow custom application features from modules
        ret = subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(), path, idRoles, false,true, principle.isCustomerRelationsValid());
        if (!ret && isTrackingGroup(principle, idRoleGroups)){
            // Here we also disallow custom only application features from subscription group
            return subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(), path, idRolesSubscription, false, true, principle.isCustomerRelationsValid());
        }
        if (ret && !isSubscriptionExempt(principle)){
            // Disallow access from custom role group, being given, when subscription level doesn't enable it
            // is subscription except ignore this step

            // Here we allow custom only application features from subscription group
            ret=  subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(), path, idRolesSubscription, false, false, principle.isCustomerRelationsValid());
            if (!ret){
                LOGGER.info("Path "+path+" for user "+principle.getEmail()+
                        " was permitted by custom role, but denied by modules access, active module =( "+
                        drPracticeGroupModule.findModuleNamesByPracticeGroupIdAtDate(principle.getPortal(), principle.getPracticeGroupId(), when)+
                        "), denying access");
            }
        }*/
        return true;
    }

    /**
     * Return the application feature URLs that are enabled
     * @param principle the PortalSecurityPrinciple to check access for
     * @param asOfDate the date to check access for
     * @return list of application feature URLs that the principal can access
     */
    public Set<String> getAppFeatureURLsEnabled(PortalSecurityPrinciple principle, LocalDate asOfDate) {

        return new HashSet<>();
        //        return this.getAppFeaturesEnabled(principle, asOfDate).stream()
//                .map(af -> af.getUrl())
//                .collect(Collectors.toSet());
    }

    /**
     * Return all application features that the given principal is able to access
     * @param principle the PortalSecurityPrinciple to check access for
     * @param asOfDate the date to check access for
     * @return list of DE_ApplicationFeature elements that the principal can access
     */


/*    public List<DE_ApplicationFeature> getAppFeaturesEnabled(PortalSecurityPrinciple principle, LocalDate asOfDate) {
        return this.getAppFeaturesWithAccessInfo(principle, asOfDate).stream()
                .filter(DE_ApplicationFeature::isAccessEnabled)
                .collect(Collectors.toList());
    }

*/
    /**
     * Returns all application features, with an indication of whether the given principal can access them
     * @param principle the PortalSecurityPrinciple to check access for
     * @param asOfDate the date to check access for
     * @return list of DE_ApplicationFeature elements, with the isAccessEnabled flag indicating if the principal has access
     */
    public List<DE_ApplicationFeature> getAppFeaturesWithAccessInfo(PortalSecurityPrinciple principle, LocalDate asOfDate) {
////        MenuRolesRetValue forAccess = this.getRolesForAccess(principle, asOfDate);
       List<DE_ApplicationFeature> appFeatures = new LinkedList<>();
//
//    drApplicationFeature.findByRoles(principle.getPortal(), forAccess.getRoles());
//        List<DE_ApplicationFeature> appFeaturesSub = drApplicationFeature.findByRoles(principle.getPortal(), forAccess.getRoleSubscription());
//        boolean isRestricted = false;
//        if (!principle.getOrganization().getPortalRoleGroup().equals("")){
//            appFeaturesSub = appFeatures;
//            isRestricted = true;
//        }
//        Map<Integer, DE_ApplicationFeature> appFeatureSubsMap = appFeaturesSub.stream().collect(Collectors.toMap(DE_ApplicationFeature::getId, x->x));
//        boolean isTracking = !forAccess.getTrackingRoleGroups().isEmpty();
//        for(DE_ApplicationFeature appFeature : appFeatures){
//            if (isTier1ForbiddenDentistPortalAccess(principle, appFeature.getUrl())) {
//                // URC-2656 - prevent Tier1 groups accessing dentist portal app features
//                appFeature.setAccessEnabled(false);
//            } else if (!principle.isSubscriptionExempt() && appFeature.isAccessEnabled()) {
//                appFeature.setAccessEnabled(appFeatureSubsMap.get(appFeature.getId()).isAccessEnabled());
//            } else if (principle.isSubscriptionExempt()){
//                appFeature.setAccessEnabled(true);
//            } else if (isTracking && !appFeature.isAccessEnabled()){
//                appFeature.setAccessEnabled(appFeatureSubsMap.get(appFeature.getId()).isAccessEnabled());
//            }
//            if (appFeature.isCustomerRelations() && !principle.isCustomerRelationsValid() && !isRestricted){
//                appFeature.setAccessEnabled(false);
//            }
//        }
//
//        if (principle.isSubscriptionExempt()  && !isRestricted && !appFeatures.stream().anyMatch(DE_ApplicationFeature::isAccessEnabled)){
//            appFeatures = appFeaturesSub;
//        }
//
//        // URC-3988 Add a flag to state where the application feature is allowed by in subscription
//        for(DE_ApplicationFeature appFeature : appFeatures){
//            appFeature.setInSubscriptionRoleGroup(appFeatureSubsMap.getOrDefault(appFeature.getId(), new DE_ApplicationFeature()).isAccessEnabled() && (!appFeature.isCustomOnly() || appFeature.isAccessEnabled() ));
//        }
//        // URC-3957
        return appFeatures;
        // .stream().filter(x->!(x.isHideOnNoAccess() && !x.isAccessEnabled())).collect(Collectors.toList());
    }

    /**
     * Indicates if the current user has strictly less access to application features than the group subscription access
     * @param principle - user
     * @param asOfDate - as of date to use
     * @return true if there are application features accessible by the user's group subscription, which are not accessible to them
     */
    public boolean userHasLessThenSubscriptionAccess(PortalSecurityPrinciple principle, LocalDate asOfDate) {
//        if (!principle.getOrganization().getPortalRoleGroup().equals("")) {
//            // for mediholdings and other restricted organizations assume this is always true
//            /           return true;
//        }

        return getAppFeaturesWithAccessInfo(principle, asOfDate).stream()
                .filter(af -> !af.isCustomerRelations())
                .anyMatch(af -> af.isInSubscriptionRoleGroup() && !af.isAccessEnabled());
    }

    // Return
    // URC-2656 - make private to reduce scope for changes
   /* private MenuRolesRetValue getRolesForAccess(PortalSecurityPrinciple principle, LocalDate asOfDate){
        MenuRolesRetValue ret = new MenuRolesRetValue();
        ret.setCustomerRelationsAllowed(principle.isCustomerRelationsValid());
        if (checkModules){
            List<Integer> idRoleGroupsModule = drPracticeGroupModule.findIdRoleGroupsByPracticeGroupAtDate(principle.getPortal(), principle.getPracticeGroupId(), asOfDate);
            List<Integer> idRolesSubscription = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroupsModule);
            List<Integer> idRoleGroups = obtainUserCustomRoleGroups(principle);
            if (idRoleGroups.isEmpty()){
                idRoleGroups = idRoleGroupsModule;
            }


            List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroups);
            if (!principle.getOrganization().getPortalRoleGroup().equals("")) {
                List<Integer> idRoleGroups1 = new LinkedList<Integer>();
                DE_RoleGroup match = new DE_RoleGroup();
                match.setDescription(principle.getOrganization().getPortalRoleGroup());
                match.setRoleType(RoleType.SUBSCRIPTION_LEVEL);
                Optional<DE_RoleGroup> group = drRoleGroup.getByDescriptionAndType(principle.getPortal(), match);
                group.ifPresent(
                        gr -> idRoleGroups1.add(gr.getId()));
                if (!group.isPresent()) {
                    LOGGER.warn("checkMPC2Access: User: " + principle.getEmail() + " cannot find role subscription role group for " + match.getDescription() + " accessed denied");
                }
                idRoleGroups = idRoleGroups1;
                idRoles = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(), idRoleGroups);
            }
            ret.setRoles(idRoles);
            if (principle.isSubscriptionExempt() && principle.getOrganization().getPortalRoleGroup().equals("")) {
                idRoles.addAll(idRolesSubscription);
            }
            ret.setRoleSubscription(idRolesSubscription);
            List<Integer> trackingRoleGroups = new LinkedList<>();
            for(int roleGroupID : idRoleGroups){
                Optional<DE_RoleGroup> group = drRoleGroup.getById(principle.getPortal(), roleGroupID);
                if (group.isPresent() && group.get().isTrackSubscription()){
                    trackingRoleGroups.add(roleGroupID);
                }
            }
            ret.setTrackingRoleGroups(trackingRoleGroups);
        }*/
        // At present only supported for modules
        //return ret;
    //}

    public boolean checkAccessMPC2(String path, PortalSecurityPrinciple principle) {
        List<Integer> roleGroups = obtainIdRoleGroups(principle);
        return checkAccessBySubscriptionRoleGroup(path, principle, roleGroups);
    }

    public boolean checkAccessBySubscriptionRoleGroup(String path, PortalSecurityPrinciple principle, List<Integer> idRoleGroups) {
        boolean ret;

        // URC-1642 - subscription exempt users should be able to view all
        // Except if they have a custom role group set that applies to them
     /*   if (principle.getOrganization().getPortalRoleGroup().equals("") && obtainUserCustomRoleGroups(principle).isEmpty() && isSubscriptionExempt(principle)) {
            if (path.toLowerCase().startsWith("/cs-setup")) {
                // only ever accessible for users with customer relations valid
                return principle.isCustomerRelationsValid();
            }
            return true;
        }

        // Otherwise, check user's roles

        if (!principle.getOrganization().getPortalRoleGroup().equals("")){
            List<Integer> idRoleGroups1 = new LinkedList<Integer>();
            DE_RoleGroup match = new DE_RoleGroup();
            match.setDescription(principle.getOrganization().getPortalRoleGroup());
            match.setRoleType(RoleType.SUBSCRIPTION_LEVEL);
            Optional<DE_RoleGroup> group = drRoleGroup.getByDescriptionAndType(PortalEnum.AWS, match);
            group.ifPresent(
                    gr-> idRoleGroups1.add(gr.getId()));
            if (!group.isPresent()){
                LOGGER.warn("checkMPC2Access: User: "+principle.getEmail()+" cannot find role subscription role group for "+match.getDescription()+" accessed denied");
            }
            idRoleGroups=idRoleGroups1;
            List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsRegular(PortalEnum.AWS,idRoleGroups);
            // Here we allow custom only application features from subscription group
            ret = subscriptionLevel.checkAccessByPathAndRoles(PortalEnum.AWS, path, idRoles, false, false, principle.isCustomerRelationsValid());
*/            return true;
        }


  /*      List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroups);
        // Here we block custom only application features from subscription group
        ret = subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(), path, idRoles, false, true, principle.isCustomerRelationsValid());
        if (!ret && isTrackingGroup(principle, idRoleGroups)){
            return checkSubscriptionAccess(path, principle, true, principle.isCustomerRelationsValid());
        }
        if (ret && !isSubscriptionExempt(principle)){
            // Disallow access from custom role group, being given, when subscription level doesn't enable it
            // is subscription except ignore this step
            ret = checkSubscriptionAccess(path, principle,false, principle.isCustomerRelationsValid());
            if (!ret){
                LOGGER.info("Path "+path+" for user "+principle.getEmail()+
                        " was permitted by custom role, but denied by subscription level "+
                        principle.getSubscriptionType()+
                        ", denying access");
            }
        }*/
      //  return ret;


    // URC-1607 if any group the user access that has track subscription true, we allow all if a non custom feature is in the subscription access
    public boolean isTrackingGroup(PortalSecurityPrinciple principle, List<Integer> idRoleGroups){
        //List<DE_RoleGroup> groups = new LinkedList<>();
        //for(int groupId : idRoleGroups){
        //    drRoleGroup.getById(principle.getPortal(), groupId).ifPresent( group->groups.add(group));
        //}
        //return groups.stream().anyMatch( group->group.isTrackSubscription());
        return false;
    }

    private boolean checkSubscriptionAccess(String path, PortalSecurityPrinciple principle, boolean excludeCustomFeature, boolean isCustomerRelationFeatureAllowed) {
        boolean ret;
        int subscriptionGroup = obtainRoleGroupSubLevel(principle);
        List<Integer> subscriptionGroups = new LinkedList<>();
        subscriptionGroups.add(subscriptionGroup);
 //       List<Integer> idRolesSubscription = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),subscriptionGroups);
 //       // Here we allow custom only application features from subscription group
 //       ret = subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(), path, idRolesSubscription, false, excludeCustomFeature, isCustomerRelationFeatureAllowed);
//        return ret;
        return false;
    }

    private boolean isSubscriptionExempt(PortalSecurityPrinciple principle){
        //return principle.isSubscriptionExempt() && SubExemptDomains.isAllowed(principle.getEmail()) ;
        return false;
    }




    public boolean checkAdminAccess(String path, AdminSecurityPrinciple principle) {
        boolean ret;
        return true;
       /* if (principle.isManageAdminUsers()){ return true; }
        List<Integer> idRoleGroups = userRoleGroup.findIdRoleGroupsByIdAdminUser(PortalEnum.AWS, principle.getId());
        List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsAdmin(PortalEnum.AWS,idRoleGroups);
        // Here we allow custom only application features from subscription group
        ret = subscriptionLevel.checkAccessByPathAndRoles(PortalEnum.AWS, path, idRoles, true, false, principle.isCustomerRelationsValid());
        return ret;*/
    }

    public List<String> getValidPathsForAdmin(AdminSecurityPrinciple principle){
        //if (principle.isManageAdminUsers()){
            return new LinkedList<>();
        //}
     //   List<Integer> idRoleGroups = userRoleGroup.findIdRoleGroupsByIdAdminUser(PortalEnum.AWS, principle.getId());
     //   List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsAdmin(PortalEnum.AWS,idRoleGroups);
     //   return subscriptionLevel.getAllowedPaths(PortalEnum.AWS, idRoles, true, false, principle.isCustomerRelationsValid());
    }

    /**
     * returns true if the given request URL is accessible to this user based on the group's module access
     * @param principle - the user trying to access the request URL
     * @param requestURI - the request URL
     * @return true if the group has access to this URL, false otherwise
     */
    public boolean isAllowedByGroupModulesAccess(PortalSecurityPrinciple principle, String requestURI) {
        // URC-2656
        if (isTier1ForbiddenDentistPortalAccess(principle, requestURI)) {
            return false;
        }
        return true;
       // List<Integer> idRoleGroupsModule = drPracticeGroupModule.findIdRoleGroupsByPracticeGroupAtDate(principle.getPortal(), principle.getPracticeGroupId(),
       //         LocalDate.now());
       // List<Integer> idRoles = roles.findIdRolesByIdRoleGroupsRegular(principle.getPortal(),idRoleGroupsModule);
       // return subscriptionLevel.checkAccessByPathAndRoles(principle.getPortal(),requestURI, idRoles, false,false, true);
    }

    public List<Integer> getSubscriptionRoleGroupIds(PortalSecurityPrinciple principle, LocalDate date){
        return new LinkedList<>();
    /*    if (checkModules){
            return drPracticeGroupModule.findIdRoleGroupsByPracticeGroupAtDate(principle.getPortal(), principle.getPracticeGroupId(), LocalDate.now());
        } else {
            int subscriptionGroup = obtainRoleGroupSubLevel(principle);
            List<Integer> subscriptionGroups = new LinkedList<>();
            subscriptionGroups.add(subscriptionGroup);
            return subscriptionGroups;
        }

     */
    }


    private boolean checkPathMpc1Logic(PortalSecurityPrinciple principle, String path) {
        boolean ret = false;
/*
        if (path.toLowerCase().startsWith("/cs-setup")) {
            // only ever accessible for users with customer relations valid
            return principle.isCustomerRelationsValid();
        }

        if (path.toLowerCase().startsWith("/portal-roles") || path.toLowerCase().startsWith("/portal-role-groups")) {
            // URC-1921c - disabled setting of MPC1 roles for portal currently in MPC1 mode
            return false;
        }

        ret = subscriptionLevel.checkPathMpc1Logic(principle.getPortal(), principle.getId(), path);

 */
        return true;
    }


    private int obtainRoleGroupSubLevel(PortalSecurityPrinciple principle){
        return 1;
//        return  subscriptionLevel.findIdRoleGroupBySubscriptionCode(principle.getPortal(), principle.getSubscriptionType());
    }

    private List<Integer> obtainUserCustomRoleGroups(PortalSecurityPrinciple principle){
        return new  LinkedList<>();
        //        return userRoleGroup.findIdRoleGroupsByIdRegularUser(principle.getPortal(), principle.getId());
    }

    private List<Integer> obtainIdRoleGroups(PortalSecurityPrinciple principle) {
        List<Integer> roleGroups = obtainUserCustomRoleGroups(principle);
        if (roleGroups.isEmpty()) {
            roleGroups.add(obtainRoleGroupSubLevel(principle));
        }
        return roleGroups;
    }

    private boolean isPathDentistPortalAppFeature(String path) {
        return (path != null && Arrays.stream(DENTIST_PORTAL_APP_FEATURE_URLS).anyMatch(af -> path.startsWith(af)));
    }

    private boolean isTier1ForbiddenDentistPortalAccess(PortalSecurityPrinciple principle, String path) {

        return false;
        //        return !principle.isSubscriptionExempt() && principle.isGroupTier1() && isPathDentistPortalAppFeature(path);
    }

    public boolean isUseMenuApi(){ return useMenuApi; }

}
