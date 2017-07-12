package com.crazyktv.wcf.android;

public class Util {
    public final static String LIST_TYPE = "list_type";
    public final static String LIST_ARGUMENT = "list_arg";
    public final static String BACK_STACK_MAIN = "main";
    public final static String BACK_STACK_SINGER = "singer";
    public final static String BACK_STACK_SINGER_LIST = "singer_list";
    public final static String BACK_STACK_LANG = "lang";
    public final static String BACK_STACK_WORD = "word";
    public final static String BACK_STACK_FAVORITE = "favorite";
    public final static String LIST_SONG_ROW = "50";

    static int LangResolve(String s){
        int i = 0;
        if(s.equals("國語")){
            i = R.string.lr_chinese;
        }else if (s.equals("台語")){
            i = R.string.lr_taiwanese;
        }else if (s.equals("粵語")){
            i = R.string.lr_cantonese;
        }else if (s.equals("英語")){
            i = R.string.lr_english;
        }else if (s.equals("日語")){
            i = R.string.lr_japanese;
        }else if (s.equals("客語")){
            i = R.string.lr_hakka;
        }else if (s.equals("韓語")){
            i = R.string.lr_korean;
        }else if (s.equals("其它")){
            i = R.string.lr_other;
        }else if (s.equals("兒歌")){
            i = R.string.lr_children;
        }else if (s.equals("原住民語")){
            i = R.string.lr_aborigine;
        }else {
            i = R.string.lr_none;
        }

        return i;
    }
    static int SingerResolve(String s){
        int j;
        switch (Integer.parseInt(s)){
            case 0:
                j = R.string.sr_male;
                break;
            case 1:
                j = R.string.sr_female;
                break;
            case 2:
                j = R.string.sr_group;
                break;
            case 4:
                j = R.string.sr_male;
            case 5:
                j = R.string.sr_female;
            case 6:
                j = R.string.sr_group;
            default:
                j = R.string.sr_other;
        }

        return j;
    }
    public static String getLang(int i){
        String lang = "";
        switch (i){
            case R.string.menu_lang_chinese:
                lang = "%E5%9C%8B%E8%AA%9E";
                break;
            case R.string.menu_lang_taiwanese:
                lang = "%E5%8F%B0%E8%AA%9E";
                break;
            case R.string.menu_lang_cantonese:
                lang = "%E7%B2%B5%E8%AA%9E";
                break;
            case R.string.menu_lang_hakka:
                lang = "%E5%AE%A2%E8%AA%9E";
                break;
            case R.string.menu_lang_english:
                lang = "%E8%8B%B1%E8%AA%9E";
                break;
            case R.string.menu_lang_japanese:
                lang = "%E6%97%A5%E8%AA%9E";
                break;
            case R.string.menu_lang_korea:
                lang = "%E9%9F%93%E8%AA%9E";
                break;
            case R.string.menu_lang_children:
                lang = "%E5%85%92%E6%AD%8C";
                break;
            case R.string.menu_lang_other:
                lang = "%E5%85%B6%E5%AE%83";
                break;
            case R.string.menu_lang_aborigine:
                lang = "%E5%8E%9F%E4%BD%8F%E6%B0%91%E8%AA%9E";
                break;
        }
        return lang;
    }

}
