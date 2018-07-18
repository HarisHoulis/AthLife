package com.example.xoulis.xaris.athLife;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

public class H {

    public static SpannableStringBuilder makeTextViewClickable(String url, String name,
                                                               final Context context) {
        // Create the Uri for the intent
        Uri webPage = Uri.parse(url);

        // Create the intent for the web browser
        final Intent intent = new Intent(Intent.ACTION_VIEW, webPage);

        // Create a SpannableStringBuilder and a ClickableSpan
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(name);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                context.startActivity(intent);
            }
        };

        // Control behaviour of the SpannableStringBuilder
        spannableStringBuilder.setSpan(span, 0, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Underline it
        spannableStringBuilder.setSpan(new UnderlineSpan(), 0,
                spannableStringBuilder.length(), 0);

        return spannableStringBuilder;
    }

    public static String buildStaticMapUrl(double lat, double lng) {
        String placeCoordinates = lat + "," + lng;
        Uri builtUri = Uri.parse(C.GOOGLE_STATIC_MAPS_BASE_URL)
                .buildUpon()
                .appendQueryParameter("center", placeCoordinates)
                .appendQueryParameter("zoom", "15")
                .appendQueryParameter("size", "500x300")
                .appendQueryParameter("markers", "color:red|" + placeCoordinates)
                .appendQueryParameter("key", C.GOOGLE_STATIC_MAPS_API_KEY)
                .build();

        return builtUri.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void showAlertDialog(final Context context, String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text)
                .setTitle(title)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((AppCompatActivity) context).finish();
                    }
                })
                .show();
    }

    public static void showSnackBar(final View view, int messageStringRes) {
        Snackbar.make(view, messageStringRes, Snackbar.LENGTH_SHORT).show();
    }
}
