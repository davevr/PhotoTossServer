package com.eweware.phototoss.api;

import com.eweware.phototoss.core.UserRecord;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import com.googlecode.objectify.Key;
import org.apache.commons.codec.binary.Base64;


import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public  class Authenticator {
    private static Authenticator instance = null;
    public static final String USERID = "userid";
    private final static int ITERATIONS = 1000;
    private static final String TWO_WAY_CRYPT_METHOD = "PBEWithMD5AndDES";
    private static final char[] MASTER_PASSWORD = "23&-*/F43v02!s_83jJ@=a".toCharArray();
    private static final byte[] MASTER_SALT = {
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
            (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12,
    };



    protected Authenticator() {
        // Exists only to defeat instantiation.
    }
    public static Authenticator getInstance() {
        if(instance == null) {
            instance = new Authenticator();
        }
        return instance;
    }

    public static String[] createSaltedPassword(final String password)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {

        final byte[] bSalt = new byte[8];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(bSalt);

        final byte[] bDigest = getHash(ITERATIONS, password, bSalt);

        return new String[]{new String(Base64.encodeBase64(bDigest)), new String(Base64.encodeBase64(bSalt))};
    }

    public static boolean authenticate(String digest, String salt, final String password) throws Exception {
        final byte[] proposedDigest = getHash(ITERATIONS, password, Base64.decodeBase64(salt));

        return Arrays.equals(proposedDigest, Base64.decodeBase64(digest));
    }



    public static Boolean AuthenticateUser(HttpSession session, String username, String password)
    {
        if (UserIsLoggedIn(session)) {
            Logout(session);
        }

        UserRecord newUser = ofy().load().type(UserRecord.class).filter("username =", username).first().now();

        try {

            if ((newUser != null) && authenticate(newUser.passwordhash, newUser.passwordsalt, password)) {
                // we are in
                session.setAttribute(USERID, newUser.id);
                newUser.signedOn = true;
                newUser.lastActiveDate = new Date();
                ofy().save().entity(newUser);
                return true;
            } else {
                // passwords did not match
                return false;
            }
        }
        catch (Exception exp)
        {
            // authenticate failed
            return false;
        }
    }

    public static UserRecord CreateAndAuthenticateUser(HttpSession session, String username, String password)  throws Exception {
        if (UserIsLoggedIn(session)) {
            Logout(session);
        }

        UserRecord newUser = null;

        newUser = ofy().load().type(UserRecord.class).filter("username =", username).first().now();

        if (newUser != null) {
            // username exists
            return null;
        } else {
            // user does not exist - create

            newUser = new UserRecord();
            newUser.username = username;
            String[]    saltAndHash = createSaltedPassword(password);
            newUser.passwordhash = saltAndHash[0];
            newUser.passwordsalt = saltAndHash[1];
            newUser.signedOn = true;
            newUser.lastActiveDate = new Date();
            newUser.creationDate = new Date();

            ofy().save().entity(newUser).now();
            session.setAttribute(USERID, newUser.id);

            return newUser;

        }
    }

    public static long CurrentUserId(HttpSession session) {
        Object theId = session.getAttribute(USERID);
        if (theId != null)
            return (long)theId;
        else
            return 0;
    }

    public static UserRecord CurrentUser(HttpSession session) {
        Object theId = session.getAttribute(USERID);
        if (theId != null) {
            UserRecord newUser = ofy().load().key(Key.create(UserRecord.class, (long)theId)).now();
            return newUser;
        }
        else
            return null;
    }

    public static Boolean UserIsLoggedIn(HttpSession session) {
        return session.getAttribute(USERID) != null;
    }


    public static Boolean Logout(HttpSession session)
    {
        if (session.getAttribute(USERID) != null) {
            // TODO:  update user's online status
            long userId = CurrentUserId(session);
            session.removeAttribute(USERID);
            UserRecord newUser = ofy().load().key(Key.create(UserRecord.class, userId)).now();
            newUser.signedOn = false;
            newUser.lastActiveDate = new Date();
            ofy().save().entity(newUser);
            return true;
        }
        else
            return false;

    }

    public static byte[] getHash(final int iterationNb, final String password, final byte[] salt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < iterationNb; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
    }

}
