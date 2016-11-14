package com.EPDev.PromotionAppIcc;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by soluciones on 13-11-2016.
 */
public class ReplaceFonts {

    public static void replaceDefaultFonts(Context context,
                                           String nameOfFontBeingReplaced,
                                           String nameOfFontInAsset){

        Typeface customFontTypeFace = Typeface.createFromAsset(context.getAssets(), nameOfFontInAsset);
        replaceFont(nameOfFontBeingReplaced, customFontTypeFace);

    }

    private static void replaceFont(String nameOfFontBeingReplaced, Typeface customFontTypeFace) {
        try {
            Field myField = Typeface.class.getDeclaredField(nameOfFontBeingReplaced);
            myField.setAccessible(true);
            myField.set(null, customFontTypeFace);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


}
