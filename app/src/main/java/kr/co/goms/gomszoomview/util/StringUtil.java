package kr.co.goms.gomszoomview.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isBlank(String str) {
		return (str == null || str.trim().length() == 0);
	}

	public static boolean isEmpty(CharSequence str) {
		return (str == null || str.length() == 0);
	}

	public static String booleanToString(boolean b){
		return b ? "true" : "false";
	}

	public static boolean stringToBoolean(String b){
		if(b == null){
			return false;
		}
		return "true".equalsIgnoreCase(b) ? true : false;
	}

	//float to int
	public static int floatToInt(float floatValue){
		return (int)floatValue;
	}
	//Int to String
	public static String intToString(Integer intValue){
		return String.valueOf(intValue);
	}
	//Long to String
	public static String longToString(Long longValue){
		return String.valueOf(longValue);
	}
    //Double to String
	public static String doubleToString(Double doubleValue){
		return String.valueOf(doubleValue);
	}
	//Float to String
	public static String floatToString(Float floatValue){
		return String.valueOf(floatValue);
	}

	/*
	 * String to Int
	 */
	public static int stringToInt(String stringValue){

		if (!TextUtils.isEmpty(stringValue)) {
			try {
				return Integer.parseInt(stringValue);
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		int defaultValue = 0;
		return defaultValue ;
	}

	/*
	 * String to Long
	 */
	public static Long stringToLong(String stringValue){
		return Long.parseLong(stringValue);
	}

	/*
	 * String to Float
	 */
	public static Float stringToFloat(String stringValue){
		return Float.parseFloat(stringValue);
	}


	/*
	 * String to double
	 */
	public static double stringToDouble(String stringValue){
		return Double.parseDouble(stringValue);
	}

	public static String stringToUTF8(String src){
		String tmp = null;
		try {
			tmp = new String(src.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tmp;
	}

	public static String stringToURLEncoder(String src){
		try {
			return URLEncoder.encode(src, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	public static String parameters2String(ArrayList<String> parameters) {
		StringBuffer stringBuffer = new StringBuffer("?");
		for (String parameter : parameters) {
			if (stringBuffer.length() > 1) {
				stringBuffer.append("&");
			}
			stringBuffer.append(parameter);
		}
		return stringBuffer.toString();
	}

	public static boolean isNull(String string) {
		if (string == null || string.length() == 0 || string.equals(""))
			return true;
		else
			return false;
	}

	public static boolean isNotNull(String string) {
		if (string != null && string.length() > 0 && !string.equals(""))
			return true;
		else
			return false;
	}

	public static String list2String(ArrayList<String> list) {
		if (list == null || list.size() == 0) {
			return "";
		} else {
			String string = "";
			for (String s : list) {
				string = string + s + ",";
			}
			return string.substring(0, string.length() - 1);
		}
	}

	public static ArrayList<String> string2List(String string){
		if (string == null || string.length() == 0 || string.equals("")) {
			return null;
		}
		else {
			ArrayList<String> list = new ArrayList<String>();
			String [] temp  =  string.split(",");
			for (int i = 0; i < temp.length; i++) {
				list.add(temp[i]);
			}
			return list;
		}
	}

	public static ArrayList<String> stringDelimiter2List(String string, String delimiter){
		if (string == null || string.length() == 0 || string.equals("")) {
			return null;
		}
		else {
			ArrayList<String> list = new ArrayList<String>();
			String [] temp  =  string.split(delimiter);
			for (int i = 0; i < temp.length; i++) {
				list.add(temp[i]);
			}
			return list;
		}
	}

	public static String getAddCommaScoreString(String stringScore){
		StringBuffer _score = new StringBuffer(stringScore);

		int j = 1;

		for (int i = 0; i < _score.length(); i++) {
			if(0 < _score.length()-(3*(j)+i)){
				_score.insert(_score.length()-(3*(j)+i),",");
				j++;
			}
			else
				break;
		}

		return _score.toString();
	}


	public static String jsonObject2String(JSONObject jsonObj){
		String string = jsonObj.toString();
		return string;
	}


	public static JSONObject string2JsonObject(String jsonString){
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObj;
	}

	public static CharSequence textToHtml(String text){
		/*
		 * <string name="addting_title03_1"><![CDATA[""<font color="#9ee91d">%1$s</font>"" 님들의 <br>짱짱한 프로필을 입력해 볼까요?]]></string>
		 */
		CharSequence result = Html.fromHtml(text);
		return result;
	}

	public static CharSequence stringHtml(Context context, int resId, String text){
		/*
		 * <string name="addting_title03_1"><![CDATA[""<font color="#9ee91d">%1$s</font>"" 님들의 <br>짱짱한 프로필을 입력해 볼까요?]]></string>
		 */
		String tmp = context.getString(resId);
		String title = String.format(tmp, text);
		CharSequence result = Html.fromHtml(title);
		return result;
	}

	public static CharSequence stringHtml(Context context, int resId, String text01, String text02){
		/*
		 * <string name="addting_title03_1"><![CDATA[""<font color="#9ee91d">%1$s</font>"" 님들의 <br>짱짱한 프로필을 입력해 볼까요?]]></string>
		 */
		String tmp = context.getString(resId);
		String title = String.format(tmp, text01, text02);
		CharSequence result = Html.fromHtml(title);
		return result;
	}

	public static boolean isEditTextEmpty(EditText mInput){
		String str = mInput.getText().toString().trim();
		if(str.length() == 0)
			return true;
		else
			return false;
	}

	public static boolean isTextViewEmpty(TextView mInput){
		String str = mInput.getText().toString().trim();
		if(str.length() == 0)
			return true;
		else
			return false;
	}

	public static boolean isEditTextEmpty(String mInput){
		String str = mInput.trim();
		if(str.length() == 0)
			return true;
		else
			return false;
	}

	public static String convertCharsequenceToString(CharSequence mInput){
		String str = mInput.toString();
		return str;
	}

	public static CharSequence convertStringToCharsequence(String mInput){
		CharSequence str = mInput;
		return str;
	}

	public static int getResourceId(String pDrawableName){
		int resourceId=Resources.getSystem().getIdentifier(pDrawableName, "string", "android");
		return resourceId;
	}

	public static String convertResIdToString(Context context, int resId){
		String tempString = context.getString(resId);
		return tempString;
	}

	/*
	 * Param string : /storage/sdcard0/GomsLayerCamera/goms_layer_37_3_21_37.5475429_127.051254_KR_20140216222512.jpg
	 * Param delimiter : /  마지막 자리수 찾는 구분자
	 */
	public static String getFindStringAndEnd(String string, String delimiter)
	{
		int startInt = string.lastIndexOf(delimiter)+1;
		String photoName = string.substring(startInt, string.length());
		return photoName;
	}

	/*
	 * Param string : "http://www.goms.co.kr"
	 */
	public static Uri convertStringToUri(String string)
	{
		Uri uri = Uri.parse(string);
		return (Uri) uri;
	}

	// ------------------------------------------------------------- replacement

	/**
	 * 특정 글자를 특정 문자열로 바꿔줍니다.
	 *
	 * 예를 들어, 특정 글자를 escape 처리하고자 할 때 이용할 수 있다.<br>
	 * <blockquote><pre>
	 * pstmt = con.prepareStatement(...);
	 * pstmt.setString(1, SringUtil.replace("bjkim's home", '\'', "''"));
	 * </pre></blockquote>
	 *
	 * @param source 	원본 문자열이다.
	 * @param ch		바꾸고자 하는 글자이다.
	 * @param replace	대치하고자 하는 문자열이다.
	 *
	 * @see #replace(String source, char ch, String replace, int max)
	 */
	public static String replace(String source, char ch, String replace)
	{
		return replace(source, ch, replace, -1);
	}

	/**
	 * 특정 글자를 특정 문자열로 바꿔줍니다.
	 *
	 * @param source 	원본 문자열이다.
	 * @param ch		바꾸고자 하는 글자이다.
	 * @param replace	대치하고자 하는 문자열이다.
	 * @param max		몇번 바꿀 것인지를 나타낸다. <code>-1</code>이면
	 *					모두 바꾼다
	 *
	 * @see #replace(String source, String original, String replace, int max)
	 */
	public static String replace(
			String source,
			char ch,
			String replace,
			int max)
	{
		return replace(source, ch + "", replace, max);
	}

	/**
	 * 특정 문자열을 특정 문자열로 바꿔줍니다.
	 *
	 * <blockquote><pre>
	 * String str = StringUtil.replace("Java \r\n is \r\n Wonderful",
	 *								   "\r\n", "<BR>");
	 * </pre></blockquote>
	 *
	 * @param source 	원본 문자열이다.
	 * @param original	바꾸고자 하는 문자열입니다.
	 * @param replace	대치하고자 하는 문자열이다.
	 */
	public static String replace(
			String source,
			String original,
			String replace)
	{
		return replace(source, original, replace, -1);
	}

	/**
	 * 특정 문자열을 특정 문자열로 바꿔줍니다. 문자열이 null이라면 null를 리턴한다.
	 *
	 * @param source 	원본 문자열이다.
	 * @param original	바꾸고자 하는 문자열입니다.
	 * @param replace	대치하고자 하는 문자열이다.
	 * @param max		몇번 바꿀것인지를 나타낸다. <code>-1</code>이면
	 *					모두 바꾼다.
	 */
	public static String replace(
			String source,
			String original,
			String replace,
			int max)
	{
		if (null == source)
			return null;
		int nextPos = 0; // 다음 position
		int currentPos = 0; // 현재 position
		int len = original.length();
		StringBuffer result = new StringBuffer(source.length());
		while ((nextPos = source.indexOf(original, currentPos)) != -1)
		{
			result.append(source.substring(currentPos, nextPos));
			result.append(replace);
			currentPos = nextPos + len;
			if (--max == 0)
			{ // 바꿀 횟수를 줄어준다
				break;
			}
		}
		if (currentPos < source.length())
		{
			result.append(source.substring(currentPos));
		}
		return result.toString();
	}

	/**
	 * Character.toTitleCase()를 이용하여 대문자 한다
	 */
	public static String toTitleCase(String str, int len)
	{
		if (null == str)
			return null;
		int strLen = str.length();
		int index = 0;
		StringBuffer sb = new StringBuffer(str.length());
		while ((index < len) && (index < strLen))
		{
			sb.append(Character.toTitleCase(str.charAt(index)));
			++index;
		}
		if (index < strLen)
		{
			sb.append(str.substring(index));
		}
		return sb.toString();
	}

	/**
	 * 첫 글자만 소문자로 바꾸어 준다. 입력 String이 null이라면
	 * null를 리턴한다.
	 *
	 * <blockquote><pre>
	 * String str = StringUtil.toFirstLowerCase("Java"); // result --> "java"
	 * </pre></blockquote>
	 */
	public static String toFirstLowerCase(String str)
	{
		return (
				null == str
						? null
						: str.substring(0, 1).toLowerCase() + str.substring(1));
	}

	/**
	 * String을 공백으로 분리시켜 문자열을 리턴한다.
	 *
	 * <blockquote><pre>
	 * String[] strs = StringUtil.split("Java is wonderful");
	 * // strs[0] = "Java"; strs[1] = "is"; strs[2] = "wonderful"
	 * </pre></blockquote>
	 *
	 * #see split(String str, String delim)
	 */
	public static String[] split(String str)
	{
		return split(str, " ", -1);
	}

	/**
	 * String을 특정 글자로 분리하여 문자열 배열을 리턴한다.
	 *
	 * #see split(String str, char delim, int max)
	 */
	public static String[] split(String str, char delim)
	{
		return split(str, delim + "", -1);
	}

	/**
	 * String을 특정 글자로 분리하여 문자열 배열을 리턴한다.
	 *
	 * @see #split(String str, String delim, int max)
	 */
	public static String[] split(String str, char delim, int max)
	{
		return split(str, delim + "", max);
	}

	/**
	 * String을 특정 문자열로 분리시켜 문자열 배열을 리턴한다.
	 *
	 * <blockquote><pre>
	 * String[] strs = StringUtil.split("a=b&b=c&c=d", "&");
	 * // strs[0] = "a=b"; strs[1] = "b=c"; strs[2] = "c=d"
	 * </pre></blockquote>
	 *
	 * @see #split(String str, String delim, int max)
	 */
	public static String[] split(String str, String delim)
	{
		return split(str, delim, -1);
	}

	/**
	 * String을 특정 문자열로 특정 횟수만큼 분리해서 문자열을 리턴한다.
	 *
	 * @param str		분리하고자 하는 문자열을 나타낸다.
	 * @param delim		분리자를 나타낸다.
	 * @param max		분리 횟수를 나타낸다. <code>-1</code>는
	 *					전체를 분리한다.
	 */
	public static String[] split(String str, String delim, int max)
	{
		int nextPos = 0;
		int currentPos = 0;
		int len = delim.length();
		List list = new ArrayList();
		while ((nextPos = str.indexOf(delim, currentPos)) != -1)
		{
			list.add(str.substring(currentPos, nextPos));
			currentPos = nextPos + len;
			if (--max == 0)
			{ // 분리 횟수를 줄어준다
				break;
			}
		}
		if (currentPos < str.length())
		{
			list.add(str.substring(currentPos));
		}
		return (String[]) list.toArray(new String[0]);
	}

	/**
	 * 특정 길이만큼 자른후 ...을  삽입한다.
	 * @param src
	 */
	public static String omit(String src, int len)
	{
		if(src==null || src.length()-1<len)
		{
			return src;
		}
		else
		{
			return src.substring(0,len)+"...";
		}
	}

	/**
	 * Trim text to a maximum size
	 *
	 * @param text The text
	 * @param p The paint
	 * @param maxSize The maximum size
	 *
	 * @return The text
	 */
	public static String trimText(String text, Paint p, int maxSize) {
		final int textSize = (int)p.measureText(text);
		if (textSize > maxSize) {
			final int chars = p.breakText(text, true, maxSize - 12, null);
			text = text.substring(0, chars);
			text += "...";
		}

		return text;
	}

	public static String cutFirstLen(String src, int len)
	{
		if(src==null || src.length()-1<len)
		{
			return src;
		}
		else
		{
			return src.substring(0,len);
		}
	}

	/** 5자리~13자리까지 */
	public static boolean checkIsInvitationCodeNumber(String src){
		String regexStr = "\\d{5,13}";
		Pattern pattern = Pattern.compile(regexStr);
		Matcher matcher = pattern.matcher(src);
		boolean isNumber = matcher.matches();
		return isNumber;
	}

	public static <T> boolean checkArrayContainsData(final T[] array, final T v) {
		if (v == null) {
			for (final T e : array)
				if (e == null)
					return true;
		} else {
			for (final T e : array)
				if (e == v || v.equals(e))
					return true;
		}

		return false;
	}

	public static String convertTagShap(String tmp){
		tmp = StringUtil.replace(tmp, ",", "#");
		tmp = StringUtil.replace(tmp, "·", "#");
		tmp = StringUtil.replace(tmp, "#", " #");
		StringBuilder sb = new StringBuilder("#");
		sb.append(tmp);
		return sb.toString();
	}

	public static String convertColorIntToString(int color)
	{
		String strColor = String.format("#%06X", 0xFFFFFF & color);
		return strColor;
	}

	public static String removeLastChar(String str) {
		return str.substring(0,str.length()-1);
	}

	public static String convertMapToArray(Map map, String delim) {
		TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>( map );
		Iterator<Integer> treeMapIter = treeMap.keySet().iterator();
		StringBuffer stringBuffer = new StringBuffer();

		while( treeMapIter.hasNext()) {	//오름 차순

			Integer key = treeMapIter.next();
			Integer value = treeMap.get( key );

			stringBuffer.append(value);
			stringBuffer.append(",");

		}

		String tmp = StringUtil.removeLastChar(stringBuffer.toString());
		return tmp;
	}

	/**
	 * @param email
	 * @return
	 * if (!validateEmail(username)) {
	usernameWrapper.setError("Not a valid email address!");
	}
	 */
	public static boolean isValidateEmail(String email) {
		String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;
		Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	//*****************************************************************

	/**
	 * if(newPassword.getText().toString().length()<8 &&!isValidPassword(newPassword.getText().toString())){
	 System.out.println("Not Valid");

	 }else{

	 System.out.println("Valid");
	 }
	 * @param password
	 * @return
	 */
	public static boolean isValidPassword(final String password) {
		GomsLog.d("StringUtil", "password : " + password);
		Pattern pattern;
		Matcher matcher;
		//final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\\\S+$).{4,}$";	//특수문자포함
		//final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=[\\S]+$).{4,}$";								//숫자 + 영문 조합
		final String PASSWORD_PATTERN = "^[A-Za-z0-9]*.{4,}$";								//숫자 혹은 영문

		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		return matcher.matches();
	}

	/**
	 *
	 * @param arr
	 * @param targetValue
     * @return
     */
	public static boolean isContains(String[] arr, String targetValue){
		return Arrays.asList(arr).contains(targetValue);
	}
}
