package android.ext;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/* JADX INFO: loaded from: classes.dex */
public class DefensiveURLSpan extends URLSpan {
    public DefensiveURLSpan(String url) {
        super(url);
    }

    public DefensiveURLSpan(Parcel src) {
        super(src);
    }

    @Override // android.text.style.URLSpan, android.text.style.ClickableSpan
    public void onClick(View widget) {
        Throwable e = null;
        try {
            try {
                super.onClick(widget);
            } catch (ActivityNotFoundException e2) {
                Log.w("failed call activity", e2);
                return;
            }
        } catch (AndroidRuntimeException ex) {
            e = ex;
        } catch (WindowManager.BadTokenException ex2) {
            e = ex2;
        } catch (SecurityException ex3) {
            e = ex3;
        }
        if (e != null) {
            try {
                Log.w("failed onClick on URLSpan: " + getURL(), e);
                Uri uri = Uri.parse(getURL());
                Context context = widget.getContext();
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                intent.putExtra("com.android.browser.application_id", Tools.getPackageName());
                intent.setFlags(AddressItem.FLAG_VALUE_NOT_EQUAL);
                try {
                    context.startActivity(intent);
                } catch (Throwable th) {
                    Log.w("failed2 onClick on URLSpan: " + getURL(), e);
                }
            } catch (SecurityException ex4) {
                Log.w("failed onClick on URLSpan: " + getURL(), ex4);
                return;
            }
        }
        Alert.dismissAll();
    }

    public static void fixTextView(TextView tv) {
        fixTextView(tv.getText());
    }

    public static void fixTextView(CharSequence text) {
        if (text instanceof SpannableString) {
            SpannableString current = (SpannableString) text;
            URLSpan[] spans = (URLSpan[]) current.getSpans(0, current.length(), URLSpan.class);
            for (URLSpan span : spans) {
                int start = current.getSpanStart(span);
                int end = current.getSpanEnd(span);
                current.removeSpan(span);
                current.setSpan(new DefensiveURLSpan(span.getURL()), start, end, 0);
            }
        }
    }
}
