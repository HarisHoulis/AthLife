package com.charis.choulis.athLife.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.charis.choulis.athLife.C;
import com.charis.choulis.athLife.R;
import com.google.firebase.auth.FirebaseAuth;

public class AppWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.xoulis.xaris.athLife.widget.AppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String PREFS_SUFFIX_SHOWEVENTS = "_showEvents";
    private static final String PREFS_SUFFIX_CATEGORY = "_category";

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private Spinner categoriesSpinner;
    private Button addWidgetButton;
    private ArrayAdapter<CharSequence> spinnerAdapter;
    private TextView categoryTextView;

    private boolean showEvents = true;
    private String category = C.EVENT_DEFAULT_CATEGORY;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null && !auth.getCurrentUser().isAnonymous()) {
                createWidget();
            }
            finish();
        }
    };

    public AppWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.app_widget_configure);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && !auth.getCurrentUser().isAnonymous()) {
            setupUiForNonNullUser();
        } else {
            setupUiForNullUser();
        }

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private void setupUiForNullUser() {
        addWidgetButton = findViewById(R.id.add_widget_button);
        addWidgetButton.setText(getString(R.string.ok));
        addWidgetButton.setOnClickListener(mOnClickListener);
        TextView errorTextView = findViewById(R.id.widget_activity_error_textView);
        LinearLayout linearLayout = findViewById(R.id.widget_activity_non_null_user_ll);
        linearLayout.setVisibility(View.GONE);
        addWidgetButton.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.VISIBLE);
    }

    private void setupUiForNonNullUser() {
        RadioGroup radioGroup = findViewById(R.id.widget_activity_radioGroup);
        categoriesSpinner = findViewById(R.id.widget_activity_spinner);
        categoryTextView = findViewById(R.id.widget_activity_category_textView);
        addWidgetButton = findViewById(R.id.add_widget_button);

        addWidgetButton.setOnClickListener(mOnClickListener);
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = spinnerAdapter.getItem(i).toString();
                addWidgetButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedButton = radioGroup.findViewById(i);
                String source = (String) checkedButton.getText();
                showEvents = source.equals(getString(R.string.events_label));
                showCategoriesSpinner();
            }
        });
    }

    private void showCategoriesSpinner() {
        int categoriesArraysResId = showEvents ? R.array.events_categories_array : R.array.places_categories_array;
        spinnerAdapter = ArrayAdapter.createFromResource(
                this, categoriesArraysResId, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoriesSpinner.setAdapter(spinnerAdapter);
        categoriesSpinner.setSelection(0, false);
        categoryTextView.setVisibility(View.VISIBLE);
        categoriesSpinner.setVisibility(View.VISIBLE);
    }

    private void createWidget() {
        final Context context = AppWidgetConfigureActivity.this;
        saveSourcePref(context, mAppWidgetId, category, showEvents);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, AppWidget.class));
        AppWidget.updateAppWidgets(context, appWidgetManager, showEvents, category, appWidgetIds);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveSourcePref(Context context, int appWidgetId, String category, boolean showEvents) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_CATEGORY, category);
        prefs.putBoolean(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_SHOWEVENTS, showEvents);
        prefs.apply();
    }

    // Read if events are currently being displayed
    static boolean loadShowEventsPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_SHOWEVENTS, true);
    }

    // Read the category of the source currently being shown
    static String loadCategoryPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_CATEGORY, C.EVENT_DEFAULT_CATEGORY);
    }

    static void deletePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_SHOWEVENTS);
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREFS_SUFFIX_CATEGORY);
        prefs.apply();
    }
}

