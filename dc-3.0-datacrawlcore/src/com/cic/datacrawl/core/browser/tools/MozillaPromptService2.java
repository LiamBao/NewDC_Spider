package com.cic.datacrawl.core.browser.tools;

import org.apache.log4j.Logger;
import org.mozilla.interfaces.nsIAuthInformation;
import org.mozilla.interfaces.nsIAuthPromptCallback;
import org.mozilla.interfaces.nsICancelable;
import org.mozilla.interfaces.nsIChannel;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIPromptService;
import org.mozilla.interfaces.nsIPromptService2;
import org.mozilla.interfaces.nsISupports;

public class MozillaPromptService2 implements nsIPromptService2 {

  static Logger LOG = Logger.getLogger(MozillaPromptService2.class);

  @Override
  public nsICancelable asyncPromptAuth(nsIDOMWindow aParent,
      nsIChannel aChannel, nsIAuthPromptCallback aCallback,
      nsISupports aContext, long level, nsIAuthInformation authInfo,
      String checkboxLabel, boolean[] checkValue) {
    if (LOG.isDebugEnabled())LOG.debug("asyncPromptAuth: checkboxLabel=" + checkboxLabel);
    return null;
  }

  @Override
  public boolean promptAuth(nsIDOMWindow aParent, nsIChannel aChannel,
      long level, nsIAuthInformation authInfo, String checkboxLabel,
      boolean[] checkValue) {
    if (LOG.isDebugEnabled())LOG.debug("promptAuth: checkboxLabel=" + checkboxLabel);
    return false;
  }

  @Override
  public void alert(nsIDOMWindow aParent, String aDialogTitle, String aText) {
    if (LOG.isTraceEnabled())LOG.trace("alert: aText=" + aText);
  }

  @Override
  public void alertCheck(nsIDOMWindow aParent, String aDialogTitle,
      String aText, String aCheckMsg, boolean[] aCheckState) {
    if (LOG.isTraceEnabled())LOG.trace("alertCheck: aText=" + aText);
  }

  @Override
  public boolean confirm(nsIDOMWindow aParent, String aDialogTitle, String aText) {
    if (LOG.isTraceEnabled())LOG.trace("confirm: aText=" + aText);
    return false;
  }

  @Override
  public boolean confirmCheck(nsIDOMWindow aParent, String aDialogTitle,
      String aText, String aCheckMsg, boolean[] aCheckState) {
    if (LOG.isTraceEnabled())LOG.trace("confirmCheck: aText=" + aText);
    return false;
  }

  @Override
  public int confirmEx(nsIDOMWindow aParent, String aDialogTitle, String aText,
      long aButtonFlags, String aButton0Title, String aButton1Title,
      String aButton2Title, String aCheckMsg, boolean[] aCheckState) {
    if (LOG.isTraceEnabled())LOG.trace("confirmEx: aText=" + aText);
    return 0;
  }

  @Override
  public boolean prompt(nsIDOMWindow aParent, String aDialogTitle,
      String aText, String[] aValue, String aCheckMsg, boolean[] aCheckState) {
    if (LOG.isTraceEnabled())LOG.trace("prompt: aText=" + aText);
    return false;
  }

  @Override
  public boolean promptPassword(nsIDOMWindow aParent, String aDialogTitle,
      String aText, String[] aPassword, String aCheckMsg, boolean[] aCheckState) {
    if (LOG.isTraceEnabled())LOG.trace("promptPassword: aText=" + aText);
    return false;
  }

  @Override
  public boolean promptUsernameAndPassword(nsIDOMWindow aParent,
      String aDialogTitle, String aText, String[] aUsername,
      String[] aPassword, String aCheckMsg, boolean[] aCheckState) {
 if (LOG.isTraceEnabled())LOG.trace("promptUsernameAndPassword: aText=" + aText);
    return false;
  }

  @Override
  public boolean select(nsIDOMWindow aParent, String aDialogTitle,
      String aText, long aCount, String[] aSelectList, int[] aOutSelection) {
    if (LOG.isTraceEnabled())LOG.trace("select: aText=" + aText);
    return false;
  }

  @Override
  public nsISupports queryInterface(String uuid) {
    if (uuid.equals (nsIPromptService2.NS_IPROMPTSERVICE2_IID) ||
        uuid.equals (nsIPromptService.NS_IPROMPTSERVICE_IID) ||
        uuid.equals (nsISupports.NS_ISUPPORTS_IID))
      return this;
    return null;
  }

}
