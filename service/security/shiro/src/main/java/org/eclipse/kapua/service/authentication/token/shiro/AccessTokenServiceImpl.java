/*******************************************************************************
 * Copyright (c) 2011, 2018 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *******************************************************************************/
package org.eclipse.kapua.service.authentication.token.shiro;

import org.eclipse.kapua.KapuaEntityNotFoundException;
import org.eclipse.kapua.KapuaException;
import org.eclipse.kapua.commons.model.query.predicate.AttributePredicateImpl;
import org.eclipse.kapua.commons.service.internal.AbstractKapuaService;
import org.eclipse.kapua.commons.util.ArgumentValidator;
import org.eclipse.kapua.event.ServiceEvent;
import org.eclipse.kapua.locator.KapuaLocator;
import org.eclipse.kapua.locator.KapuaProvider;
import org.eclipse.kapua.model.domain.Actions;
import org.eclipse.kapua.model.id.KapuaId;
import org.eclipse.kapua.model.query.KapuaQuery;
import org.eclipse.kapua.service.authentication.token.AccessToken;
import org.eclipse.kapua.service.authentication.token.AccessTokenCreator;
import org.eclipse.kapua.service.authentication.token.AccessTokenListResult;
import org.eclipse.kapua.service.authentication.token.AccessTokenPredicates;
import org.eclipse.kapua.service.authentication.token.AccessTokenQuery;
import org.eclipse.kapua.service.authentication.token.AccessTokenService;
import org.eclipse.kapua.service.authorization.AuthorizationService;
import org.eclipse.kapua.service.authorization.permission.PermissionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * {@link AccessTokenService} implementation.
 *
 * @since 1.0.0
 */
@KapuaProvider
public class AccessTokenServiceImpl extends AbstractKapuaService implements AccessTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessTokenServiceImpl.class);

    private static final KapuaLocator LOCATOR = KapuaLocator.getInstance();

    private static final AuthorizationService AUTHORIZATION_SERVICE = LOCATOR.getService(AuthorizationService.class);
    private static final PermissionFactory PERMISSION_FACTORY = LOCATOR.getFactory(PermissionFactory.class);

    /**
     * Constructor
     */
    public AccessTokenServiceImpl() {
        super(AccessTokenEntityManagerFactory.getInstance());
    }

    @Override
    public AccessToken create(AccessTokenCreator accessTokenCreator) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(accessTokenCreator, "accessTokenCreator");
        ArgumentValidator.notNull(accessTokenCreator.getScopeId(), "accessTokenCreator.scopeId");
        ArgumentValidator.notNull(accessTokenCreator.getTokenId(), "accessTokenCreator.tokenId");
        ArgumentValidator.notNull(accessTokenCreator.getUserId(), "accessTokenCreator.userId");
        ArgumentValidator.notNull(accessTokenCreator.getExpiresOn(), "accessTokenCreator.expiresOn");

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.write, accessTokenCreator.getScopeId()));

        //
        // Do create
        return entityManagerSession.onTransactedInsert(em -> AccessTokenDAO.create(em, accessTokenCreator));
    }

    @Override
    public AccessToken update(AccessToken accessToken) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(accessToken, "accessToken");
        ArgumentValidator.notNull(accessToken.getId(), "accessToken.id");
        ArgumentValidator.notNull(accessToken.getScopeId(), "accessToken.scopeId");
        ArgumentValidator.notNull(accessToken.getUserId(), "accessToken.userId");
        ArgumentValidator.notNull(accessToken.getExpiresOn(), "accessToken.expiresOn");

        //
        // Check access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.write, accessToken.getScopeId()));

        //
        // Check existence
        if (find(accessToken.getScopeId(), accessToken.getId()) == null) {
            throw new KapuaEntityNotFoundException(AccessToken.TYPE, accessToken.getId());
        }

        //
        // Do update
        return entityManagerSession.onTransactedResult(em -> AccessTokenDAO.update(em, accessToken));
    }

    @Override
    public AccessToken find(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        //
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(accessTokenId, "accessTokenId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, scopeId));

        //
        // Do find
        return entityManagerSession.onResult(em -> AccessTokenDAO.find(em, accessTokenId));
    }

    @Override
    public AccessTokenListResult query(KapuaQuery<AccessToken> query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do query
        return entityManagerSession.onResult(em -> AccessTokenDAO.query(em, query));
    }

    @Override
    public long count(KapuaQuery<AccessToken> query) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(query, "query");
        ArgumentValidator.notNull(query.getScopeId(), "query.scopeId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, query.getScopeId()));

        //
        // Do count
        return entityManagerSession.onResult(em -> AccessTokenDAO.count(em, query));
    }

    @Override
    public void delete(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(accessTokenId, "accessTokenId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.delete, scopeId));

        //
        // Check existence
        if (find(scopeId, accessTokenId) == null) {
            throw new KapuaEntityNotFoundException(AccessToken.TYPE, accessTokenId);
        }

        //
        // Do delete
        entityManagerSession.onTransactedAction(em -> AccessTokenDAO.delete(em, accessTokenId));
    }

    @Override
    public AccessTokenListResult findByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(userId, "userId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, scopeId));

        //
        // Build query
        AccessTokenQuery query = new AccessTokenQueryImpl(scopeId);
        query.setPredicate(new AttributePredicateImpl<>(AccessTokenPredicates.USER_ID, userId));

        //
        // Do query
        return query(query);
    }

    @Override
    public AccessToken findByTokenId(String tokenId) throws KapuaException {
        //
        // Argument Validation
        ArgumentValidator.notNull(tokenId, "tokenId");

        //
        // Do find
        AccessToken accessToken = entityManagerSession.onResult(em -> AccessTokenDAO.findByTokenId(em, tokenId));

        //
        // Check Access
        if (accessToken != null) {
            AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, accessToken.getScopeId()));
        }

        return accessToken;
    }

    @Override
    public void invalidate(KapuaId scopeId, KapuaId accessTokenId) throws KapuaException {
        //
        // Validation of the fields
        ArgumentValidator.notNull(scopeId, "scopeId");
        ArgumentValidator.notNull(accessTokenId, "accessTokenId");

        //
        // Check Access
        AUTHORIZATION_SERVICE.checkPermission(PERMISSION_FACTORY.newPermission(ACCESS_TOKEN_DOMAIN, Actions.read, scopeId));

        //
        // Do find
        entityManagerSession.onTransactedResult(em -> {
            AccessToken accessToken = AccessTokenDAO.find(em, accessTokenId);
            accessToken.setInvalidatedOn(new Date());

            return AccessTokenDAO.update(em, accessToken);
        });
    }

    //@ListenServiceEvent(fromAddress="account")
    //@ListenServiceEvent(fromAddress="user")
    public void onKapuaEvent(ServiceEvent kapuaEvent) throws KapuaException {
        if (kapuaEvent == null) {
            //service bus error. Throw some exception?
        }

        LOGGER.info("AccessTokenService: received kapua event from {}, operation {}", kapuaEvent.getService(), kapuaEvent.getOperation());
        if ("user".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessTokenByUserId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        } else if ("account".equals(kapuaEvent.getService()) && "delete".equals(kapuaEvent.getOperation())) {
            deleteAccessTokenByAccountId(kapuaEvent.getScopeId(), kapuaEvent.getEntityId());
        }
    }

    private void deleteAccessTokenByUserId(KapuaId scopeId, KapuaId userId) throws KapuaException {

        AccessTokenQuery query = new AccessTokenQueryImpl(scopeId);
        query.setPredicate(new AttributePredicateImpl<>(AccessTokenPredicates.USER_ID, userId));

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }

    private void deleteAccessTokenByAccountId(KapuaId scopeId, KapuaId accountId) throws KapuaException {

        AccessTokenQuery query = new AccessTokenQueryImpl(accountId);

        AccessTokenListResult accessTokensToDelete = query(query);

        for (AccessToken at : accessTokensToDelete.getItems()) {
            delete(at.getScopeId(), at.getId());
        }
    }
}
