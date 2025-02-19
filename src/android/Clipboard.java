package com.verso.cordova.clipboard;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.text.Html;

public class Clipboard extends CordovaPlugin {

    private static final String actionCopy = "copy";
    private static final String actionPaste = "paste";
    private static final String actionClear = "clear";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        ClipboardManager clipboard = (ClipboardManager) cordova.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (action.equals(actionCopy)) {
            try {
                String text = args.getString(0);
                /**
                * Changed on 30 JAN 2018 by Mayank & Karanveer
                * It will now support Html content with links copy and paste
                */
                //ClipData clip = ClipData.newPlainText("Text", text);
                String plainText = Html.fromHtml(text).toString();
                ClipData clip = ClipData.newHtmlText("html", plainText, text);
                clipboard.setPrimaryClip(clip);
                callbackContext.success(text);

                return true;
            } catch (JSONException e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.toString()));
            }
        } else if (action.equals(actionPaste)) {
            /**
            * Changed on 30 JAN 2018 by Mayank & Karanveer
            * It will now support Html content with links copy and paste
            */            
            //if (!clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            if (!clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.NO_RESULT));
            }

            try {
                String text = "";
                
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null) {
                    ClipData.Item item = clip.getItemAt(0);
                    text = item.getText().toString();
                }
                callbackContext.success(text);

                return true;
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.toString()));
            }
        } else if (action.equals(actionClear)) {
            try {
                ClipData clip = ClipData.newPlainText("", "");
                clipboard.setPrimaryClip(clip);

                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK));

                return true;
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, e.toString()));
            }
        }

        return false;
    }
}


