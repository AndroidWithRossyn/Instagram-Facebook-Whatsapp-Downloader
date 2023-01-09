package com.allmy.allstatusdownloader.Auth;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class AQuery extends AbstractAQuery<AQuery> {
    public AQuery(Activity activity) {
        super(activity);
    }

    public AQuery(View view) {
        super(view);
    }

    public AQuery(Context context) {
        super(context);
    }

    public AQuery(Activity activity, View view) {
        super(activity, view);
    }
}
