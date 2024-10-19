package com.smokpromotion.SmokProm.config.portal;


import com.smokpromotion.SmokProm.config.common.SubscriptionAccessManager;
import com.smokpromotion.SmokProm.domain.repo.REP_UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;

import org.springframework.http.server.reactive.ServerHttpRequest;

import reactor.core.publisher.Mono;


@Profile("!test")
public class MajoranaAccessDecisionManager extends BaseAccessDecisionManager {
    @Autowired private REP_UserService userService;
    @Autowired private SubscriptionAccessManager subscriptionAccess;

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAccessDecisionManager.class);


    @Override
    protected boolean checkAccessForPath(ServerHttpRequest request,
                                         // PortalSecurityPrinciple principle,
                                         Authentication authentication, String path) throws Exception {
        return subscriptionAccess.isUserAllowedToPath(request.getQueryParams(), authentication, path);
    }
}

