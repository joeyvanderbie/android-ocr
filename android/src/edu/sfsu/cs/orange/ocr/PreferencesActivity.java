/*
 * Copyright (C) 2008 ZXing authors
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.sfsu.cs.orange.ocr;

import edu.sfsu.cs.orange.ocr.language.LanguageCodeHelper;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Class to handle preferences that are saved across sessions of the app. Shows
 * a hierarchy of preferences to the user, organized into sections. These
 * preferences are displayed in the options menu that is shown when the user
 * presses the MENU button.
 */
public class PreferencesActivity extends PreferenceActivity implements
  OnSharedPreferenceChangeListener {
  
  // Preference keys not carried over from ZXing project
  public static final String KEY_SOURCE_LANGUAGE_PREFERENCE = "sourceLanguageCodeOcrPref";
  public static final String KEY_TARGET_LANGUAGE_PREFERENCE = "targetLanguageCodeTranslationPref";
  public static final String KEY_TOGGLE_TRANSLATION = "preference_translation_toggle_translation";
  public static final String KEY_CONTINUOUS_PREVIEW = "preference_capture_continuous";
  public static final String KEY_PAGE_SEGMENTATION_MODE = "preference_page_segmentation_mode";
  public static final String KEY_OCR_ENGINE_MODE = "preference_ocr_engine_mode";
  public static final String KEY_CHARACTER_BLACKLIST = "preference_character_blacklist";
  public static final String KEY_CHARACTER_WHITELIST = "preference_character_whitelist";
  //public static final String KEY_TOGGLE_LIGHT = "preference_toggle_light";
  public static final String KEY_TRANSLATOR = "preference_translator";
  
  // Preference keys carried over from ZXing project
  public static final String KEY_HELP_VERSION_SHOWN = "preferences_help_version_shown";
  public static final String KEY_NOT_OUR_RESULTS_SHOWN = "preferences_not_our_results_shown";
  public static final String KEY_REVERSE_IMAGE = "preferences_reverse_image";
  public static final String KEY_PLAY_BEEP = "preferences_play_beep";
  public static final String KEY_VIBRATE = "preferences_vibrate";

  public static final String CAPTURE_MODE_CONTINUOUS = "Continuous";
  public static final String CAPTURE_MODE_SINGLE_SHOT = "Single shot";

  public static final String TRANSLATOR_BING = "Bing Translator";
  public static final String TRANSLATOR_GOOGLE = "Google Translate";

  
  private ListPreference listPreferenceTranslator;
  private ListPreference listPreferenceSourceLanguage;
  private ListPreference listPreferenceTargetLanguage;
  private ListPreference listPreferencePageSegmentationMode;
  private ListPreference listPreferenceOcrEngineMode;
  private EditTextPreference editTextPreferenceCharacterBlacklist;
  private EditTextPreference editTextPreferenceCharacterWhitelist;
  private SharedPreferences sharedPreferences;

  /**
   * Set the default preference values.
   * 
   * @param Bundle
   *            savedInstanceState the current Activity's state, as passed by
   *            Android
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    listPreferenceTranslator = (ListPreference) getPreferenceScreen().findPreference(KEY_TRANSLATOR);
    listPreferenceSourceLanguage = (ListPreference) getPreferenceScreen().findPreference(KEY_SOURCE_LANGUAGE_PREFERENCE);
    listPreferenceTargetLanguage = (ListPreference) getPreferenceScreen().findPreference(KEY_TARGET_LANGUAGE_PREFERENCE);
    listPreferencePageSegmentationMode = (ListPreference) getPreferenceScreen().findPreference(KEY_PAGE_SEGMENTATION_MODE);
    listPreferenceOcrEngineMode = (ListPreference) getPreferenceScreen().findPreference(KEY_OCR_ENGINE_MODE);
    editTextPreferenceCharacterBlacklist = (EditTextPreference) getPreferenceScreen().findPreference(KEY_CHARACTER_BLACKLIST);
    editTextPreferenceCharacterWhitelist = (EditTextPreference) getPreferenceScreen().findPreference(KEY_CHARACTER_WHITELIST);
    
    // Create the entries/entryvalues for the translation target language list.
    initTranslationTargetList();
  }

  /**
   * Interface definition for a callback to be invoked when a shared
   * preference is changed. Sets summary text for the app's preferences. Summary text values show the
   * current settings for the values.
   * 
   * @param sharedPreferences
   *            the Android.content.SharedPreferences that received the change
   * @param key
   *            the key of the preference that was changed, added, or removed
   */
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    
    // Update preference summary values to show current preferences
    if (key.equals(KEY_TRANSLATOR)) {
      listPreferenceTranslator.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_TRANSLATOR));
    } else if(key.equals(KEY_SOURCE_LANGUAGE_PREFERENCE)) {
      listPreferenceSourceLanguage.setSummary(LanguageCodeHelper.getLanguageName(getBaseContext(), sharedPreferences.getString(key, CaptureActivity.DEFAULT_SOURCE_LANGUAGE_CODE)));
    } else if (key.equals(KEY_TARGET_LANGUAGE_PREFERENCE)) {
      listPreferenceTargetLanguage.setSummary(LanguageCodeHelper.getTranslationLanguageName(this, sharedPreferences.getString(key, CaptureActivity.DEFAULT_TARGET_LANGUAGE_CODE)));
    } else if (key.equals(KEY_PAGE_SEGMENTATION_MODE)) {
      listPreferencePageSegmentationMode.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_PAGE_SEGMENTATION_MODE));
    } else if (key.equals(KEY_OCR_ENGINE_MODE)) {
      listPreferenceOcrEngineMode.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_OCR_ENGINE_MODE_SUMMARY_TEXT));
    } else if (key.equals(KEY_CHARACTER_BLACKLIST)) {
      editTextPreferenceCharacterBlacklist.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_CHARACTER_BLACKLIST));
    } else if (key.equals(KEY_CHARACTER_WHITELIST)) {
      editTextPreferenceCharacterWhitelist.setSummary(sharedPreferences.getString(key, CaptureActivity.DEFAULT_CHARACTER_WHITELIST));
    }
    
    // Update the languages available for translation based on the current translator selected.
    if (key.equals(KEY_TRANSLATOR)) {
      initTranslationTargetList();
    }
    
  }

  /**
   * Sets the list of available languages and the current target language for translation. Called
   * when the key for the current translator is changed.
   */
  void initTranslationTargetList() {
    // Update the list of available languages for the currently-chosen translation API.
    if (sharedPreferences.getString(KEY_TRANSLATOR, CaptureActivity.DEFAULT_TRANSLATOR).equals(TRANSLATOR_BING)) {
      listPreferenceTargetLanguage.setEntries(R.array.translationtargetlanguagenames_microsoft);
      listPreferenceTargetLanguage.setEntryValues(R.array.translationtargetiso6391_microsoft);
    } else if (sharedPreferences.getString(KEY_TRANSLATOR, CaptureActivity.DEFAULT_TRANSLATOR).equals(TRANSLATOR_GOOGLE)) {
      listPreferenceTargetLanguage.setEntries(R.array.translationtargetlanguagenames_google);
      listPreferenceTargetLanguage.setEntryValues(R.array.translationtargetiso6391_google);
    }
    
    // Set the prference for the equivalent language for switching from Google to Bing, or Bing to Google.
    // If that language is not supported by that translator, switch it to English.
    String currentLanguageCode = sharedPreferences.getString(KEY_TARGET_LANGUAGE_PREFERENCE, 
        CaptureActivity.DEFAULT_TARGET_LANGUAGE_CODE);
    boolean supported = LanguageCodeHelper.isSupported(getBaseContext(), 
        sharedPreferences.getString(KEY_TRANSLATOR, CaptureActivity.DEFAULT_TRANSLATOR), 
        currentLanguageCode);
    
    Log.d("PreferencesActivity", "Checking if language code " + sharedPreferences.getString(KEY_TARGET_LANGUAGE_PREFERENCE, CaptureActivity.DEFAULT_TARGET_LANGUAGE_CODE)
        + " is supported");
    
    // If the language was not found on the list, set an appropriate new value.
    String newLanguageCode = "";
    if (!supported) {
      // Default to English
      newLanguageCode = LanguageCodeHelper.mapLanguageCode(CaptureActivity.DEFAULT_SOURCE_LANGUAGE_CODE);
      
      // Hack to check for Chinese codes that do not match between Google and Bing
      if (currentLanguageCode.equals("zh-CHS")) {
        newLanguageCode = "zh-CN";
      } else if (currentLanguageCode.equals("zh-CHT")) {
        newLanguageCode = "zh-TW";
      } else if (currentLanguageCode.equals("zh-CN")) {
        newLanguageCode = "zh-CHS";
      } else if (currentLanguageCode.equals("zh-TW")) {
        newLanguageCode = "zh-CHT";
      } else if (currentLanguageCode.equals("zh")) {
        newLanguageCode = "zh-CHS";
      }
      
      // Hack to accomodate different codes for Hebrew--Google uses 'iw' but Bing uses 'he'
      if (currentLanguageCode.equals("he")) {
        newLanguageCode = "iw";
      } else if (currentLanguageCode.equals("iw")) {
        newLanguageCode = "he";
      }
      
      // Set the target language preference
      sharedPreferences.edit().putString(PreferencesActivity.KEY_TARGET_LANGUAGE_PREFERENCE, 
          newLanguageCode).commit();
      
      // Also set the selected radio button in the target language list
      listPreferenceTargetLanguage.setValue(LanguageCodeHelper.getLanguageName(getBaseContext(),
          newLanguageCode));
    }
  }
  
  /**
   * Sets up initial preference summary text
   * values and registers the OnSharedPreferenceChangeListener.
   */
  @Override
  protected void onResume() {
    super.onResume();

    // Set up the initial summary values
    listPreferenceTranslator.setSummary(sharedPreferences.getString(KEY_TRANSLATOR, CaptureActivity.DEFAULT_TRANSLATOR));
    listPreferenceSourceLanguage.setSummary(LanguageCodeHelper.getLanguageName(this, sharedPreferences.getString(KEY_SOURCE_LANGUAGE_PREFERENCE, CaptureActivity.DEFAULT_SOURCE_LANGUAGE_CODE)));
    listPreferenceTargetLanguage.setSummary(LanguageCodeHelper.getTranslationLanguageName(this, sharedPreferences.getString(KEY_TARGET_LANGUAGE_PREFERENCE, CaptureActivity.DEFAULT_TARGET_LANGUAGE_CODE)));
    listPreferencePageSegmentationMode.setSummary(sharedPreferences.getString(KEY_PAGE_SEGMENTATION_MODE, CaptureActivity.DEFAULT_PAGE_SEGMENTATION_MODE));
    listPreferenceOcrEngineMode.setSummary(sharedPreferences.getString(KEY_OCR_ENGINE_MODE, CaptureActivity.DEFAULT_OCR_ENGINE_MODE_SUMMARY_TEXT));
    editTextPreferenceCharacterBlacklist.setSummary(sharedPreferences.getString(KEY_CHARACTER_BLACKLIST, CaptureActivity.DEFAULT_CHARACTER_BLACKLIST));
    editTextPreferenceCharacterWhitelist.setSummary(sharedPreferences.getString(KEY_CHARACTER_WHITELIST, CaptureActivity.DEFAULT_CHARACTER_WHITELIST));
    
    // Set up a listener whenever a key changes
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  /**
   * Called when Activity is about to lose focus. Unregisters the
   * OnSharedPreferenceChangeListener.
   */
  @Override
  protected void onPause() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }
}