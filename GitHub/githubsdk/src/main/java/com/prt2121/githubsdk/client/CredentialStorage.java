package com.prt2121.githubsdk.client;

import android.content.Context;
import android.content.SharedPreferences;
import com.securepreferences.SecurePreferences;

public class CredentialStorage {

  private static final String USER_NAME = CredentialStorage.class.getSimpleName() + ".USER_NAME";
  private static final String USER_TOKEN = CredentialStorage.class.getSimpleName() + ".USER_TOKEN";
  private static final String USER_SCOPES =
      CredentialStorage.class.getSimpleName() + ".USER_SCOPES";
  private static final String USER_SCOPES_NO_ASK =
      CredentialStorage.class.getSimpleName() + ".USER_SCOPES_NO_ASK";
  private final SharedPreferences.Editor editor;
  private final SharedPreferences preferences;

  public CredentialStorage(Context context, String accessToken) {
    preferences = new SecurePreferences(context, accessToken, "secure.xml");
    editor = preferences.edit();
    editor.putString(USER_TOKEN, accessToken);
    editor.apply();
  }

  public void storeToken(String accessToken) {
    editor.putString(USER_TOKEN, accessToken);
    editor.apply();
  }

  public String token() {
    return preferences.getString(USER_TOKEN, null);
  }

  public void storeScopes(String scopes) {
    editor.putString(USER_SCOPES, scopes);
    editor.commit();
  }

  public String scopes() {
    return preferences.getString(USER_SCOPES, null);
  }

  public void saveScopeNoAsk(boolean scopesNoAsk) {
    editor.putBoolean(USER_SCOPES_NO_ASK, scopesNoAsk);
    editor.commit();
  }

  public Boolean scopeNoAsk() {
    return preferences.getBoolean(USER_SCOPES_NO_ASK, false);
  }

  public void clear() {
    editor.clear();
    editor.commit();
  }

  public void storeUsername(String name) {
    editor.putString(USER_NAME, name);
    editor.apply();
  }

  public String getUserName() {
    return preferences.getString(USER_NAME, null);
  }
}
