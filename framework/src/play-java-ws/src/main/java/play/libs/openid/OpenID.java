/*
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package play.libs.openid;

import java.util.Map;
import java.util.HashMap;

import play.libs.Scala;
import scala.runtime.AbstractFunction1;
import scala.collection.JavaConversions;

import play.api.libs.concurrent.Promise;

import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Request;

import play.core.Invoker;

/**
 * provides support for OpenID
 */
public class OpenID {

    /**
     * Retrieve the URL where the user should be redirected to start the OpenID authentication process
     */
    public static F.Promise<String> redirectURL(String openID, String callbackURL) {
        return redirectURL(openID, callbackURL, null, null, null);
    }

    /**
     * Retrieve the URL where the user should be redirected to start the OpenID authentication process
     */
    public static F.Promise<String> redirectURL(String openID, String callbackURL, Map<String, String> axRequired) {
        return redirectURL(openID, callbackURL, axRequired, null, null);
    }

    /**
     * Retrieve the URL where the user should be redirected to start the OpenID authentication process
     */
    public static F.Promise<String> redirectURL(String openID,
            String callbackURL,
            Map<String, String> axRequired,
            Map<String, String> axOptional) {
        return redirectURL(openID, callbackURL, axRequired, axOptional, null);
    }

    /**
     * Retrieve the URL where the user should be redirected to start the OpenID authentication process
     */
    public static F.Promise<String> redirectURL(String openID,
            String callbackURL,
            Map<String, String> axRequired,
            Map<String, String> axOptional,
            String realm) {
        if (axRequired == null) axRequired = new HashMap<String, String>();
        if (axOptional == null) axOptional = new HashMap<String, String>();
        return F.Promise.wrap(play.api.libs.openid.OpenID.redirectURL(openID,
                                                                      callbackURL,
                                                                      JavaConversions.mapAsScalaMap(axRequired).toSeq(),
                                                                      JavaConversions.mapAsScalaMap(axOptional).toSeq(),
                                                                      Scala.Option(realm)));
    }

    /**
     * Check the identity of the user from the current request, that should be the callback from the OpenID server
     */
    public static F.Promise<UserInfo> verifiedId() {
        Request request = Http.Context.current().request();
        scala.concurrent.Future<UserInfo> scalaPromise = play.api.libs.openid.OpenID.verifiedId(request.queryString()).map(
                new AbstractFunction1<play.api.libs.openid.UserInfo, UserInfo>() {
                    @Override
                    public UserInfo apply(play.api.libs.openid.UserInfo scalaUserInfo) {
                        return new UserInfo(scalaUserInfo.id(), JavaConversions.mapAsJavaMap(scalaUserInfo.attributes()));
                    }
                },Invoker.executionContext());
        return F.Promise.wrap(scalaPromise);
    }

    public static class UserInfo {
        public String id;
        public Map<String, String> attributes;
        public UserInfo(String id) {
            this.id = id;
            this.attributes = new HashMap<String, String>();
        }
        public UserInfo(String id, Map<String, String> attributes) {
            this.id = id;
            this.attributes = attributes;
        }
    }

}
