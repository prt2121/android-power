package com.funsociety.openissues;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Bernat on 27/03/2015.
 */
public class AccountAuthenticatorService extends Service {

    private static AccountAuthenticatorImpl accountAuthenticator;

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;

        if (intent.getAction().equals(AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
            ret = getAuthenticator().getIBinder();
        }

        return ret;
    }

    private AccountAuthenticatorImpl getAuthenticator() {
        if (accountAuthenticator == null) {
            accountAuthenticator = new AccountAuthenticatorImpl(this);
        }
        return accountAuthenticator;
    }

    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {

        private Context context;

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
            return null;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] strings, Bundle bundle) throws NetworkErrorException {
            Bundle reply = new Bundle();
            Intent intent = new Intent(context, WebLoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(WebLoginActivity.ADDING_FROM_ACCOUNTS, true);
            intent.putExtra(WebLoginActivity.ARG_ACCOUNT_TYPE, accountType);
            intent.putExtra(WebLoginActivity.ARG_AUTH_TYPE, authTokenType);
            reply.putParcelable(AccountManager.KEY_INTENT, intent);
            return reply;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle bundle) throws NetworkErrorException {
            Bundle reply = new Bundle();
            Intent intent = new Intent(context, WebLoginActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(WebLoginActivity.ADDING_FROM_ACCOUNTS, true);
            intent.putExtra(WebLoginActivity.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(WebLoginActivity.ARG_AUTH_TYPE, authTokenType);
            reply.putParcelable(AccountManager.KEY_INTENT, intent);

            return reply;
        }

        @Override
        public String getAuthTokenLabel(String s) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
            return null;
        }
    }
}
