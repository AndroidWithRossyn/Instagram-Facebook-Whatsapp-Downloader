package com.allmy.allstatusdownloader.Others;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.allmy.allstatusdownloader.Model.FavPrefSetter;
import com.google.gson.Gson;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavSharedPreference {
    public static final String FAVORITES = "code_Favorite";
    public static final String PREFS_NAME = "POCKTCODE_APP";
    int position;

    public void saveFavorites(Context context, List<FavPrefSetter> list) {
        for (int i = 0; i < list.size(); i++) {
            FavPrefSetter favPrefSetter = (FavPrefSetter) list.get(i);
            String profile_picture = favPrefSetter.getProfile_picture();
            String userID = favPrefSetter.getUserID();
            String userName = favPrefSetter.getUserName();
            String userFullName = favPrefSetter.getUserFullName();
            PrintStream printStream = System.out;
            StringBuilder sb = new StringBuilder();
            sb.append("FULLNAME : ");
            sb.append(userFullName);
            printStream.println(sb.toString());
            PrintStream printStream2 = System.out;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("url : ");
            sb2.append(profile_picture);
            printStream2.println(sb2.toString());
            PrintStream printStream3 = System.out;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("USER_ID : ");
            sb3.append(userID);
            printStream3.println(sb3.toString());
            PrintStream printStream4 = System.out;
            StringBuilder sb4 = new StringBuilder();
            sb4.append("USER_NAME : ");
            sb4.append(userName);
            printStream4.println(sb4.toString());
        }
        Editor edit = context.getSharedPreferences("POCKTCODE_APP", 0).edit();
        edit.putString("code_Favorite", new Gson().toJson((Object) list));
        edit.commit();
    }

    public void addFavorite(Context context, FavPrefSetter favPrefSetter) {
        ArrayList favorites = getFavorites(context);
        if (favorites == null) {
            favorites = new ArrayList();
        }
        favorites.add(favPrefSetter);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, FavPrefSetter favPrefSetter, int i) {
        ArrayList favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(i);
        }
        saveFavorites(context, favorites);
    }

    public ArrayList<FavPrefSetter> getFavorites(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("POCKTCODE_APP", 0);
        String str = "code_Favorite";
        if (!sharedPreferences.contains(str)) {
            return null;
        }
        return new ArrayList<>(Arrays.asList((FavPrefSetter[]) new Gson().fromJson(sharedPreferences.getString(str, null), FavPrefSetter[].class)));
    }
}
