package com.charis.choulis.athLife.binding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.charis.choulis.athLife.GlideApp;
import com.charis.choulis.athLife.H;
import com.charis.choulis.athLife.R;
import com.charis.choulis.athLife.data.models.Place;
import com.charis.choulis.athLife.ui.fragments.DetailsFragment;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HelperBindingAdapter {

    // Used for both events and places
    @BindingAdapter({"imgUrl", "isEvent"})
    public static void loadImage(ImageView view, String imgUrl, boolean isEvent) {
        int defaultImageResId;
        if (isEvent) {
            defaultImageResId = R.drawable.default_event_image;
        } else {
            defaultImageResId = R.drawable.default_place_image;
        }
        GlideApp.with(view.getContext())
                .load(imgUrl)
                .fallback(defaultImageResId)
                .error(defaultImageResId)
                .apply(new RequestOptions().transform(new RoundedCorners(40)))
                .into(view);
    }

    @BindingAdapter({"statusBarAndLogo", "handler", "isEvent"})
    public static void setLogoAndStatusBar(ImageView view, String imageUrl, final DetailsFragment handler, boolean isEvent) {
        int defaultImageResId;
        if (isEvent) {
            defaultImageResId = R.drawable.default_event_image;
        } else {
            defaultImageResId = R.drawable.default_place_image;
        }

        final Context context = view.getContext();
        GlideApp.with(context)
                .asBitmap()
                .load(imageUrl)
                .fallback(defaultImageResId)
                .error(defaultImageResId)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        handler.startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette palette) {
                                    final int darkMuted = palette.getDarkMutedColor(context.getResources().getColor(R.color.colorPrimaryDark));
                                    final Activity activity = (Activity) ((ContextThemeWrapper) context).getBaseContext();
                                    activity.runOnUiThread(new Runnable() {
                                        @SuppressLint("NewApi")
                                        @Override
                                        public void run() {
                                            activity.getWindow().setStatusBarColor(darkMuted);
                                            handler.startPostponedEnterTransition();
                                        }
                                    });
                                }

                            });
                        }
                        return false;
                    }
                })
                .into(view);
    }

    @BindingAdapter("eventAddress")
    public static void setEventAddress(TextView view, String address) {
        if (TextUtils.isEmpty(address)) {
            view.setText(R.string.default_event_address);
        } else {
            view.setText(address);
        }
    }

    @BindingAdapter("eventFormattedDate")
    public static void setEventFormattedDate(TextView view, String dateString) {
        if (TextUtils.isEmpty(dateString)) return;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date formattedDate = sdf.parse(dateString);
            sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());
            view.setText(sdf.format(formattedDate));
        } catch (ParseException e) {
            Log.i("DATE_FORMATTING_ERROR", e.getMessage());
        }
    }

    @BindingAdapter("eventDescription")
    public static void setEventDescription(ExpandableTextView view, String description) {
        String tempDescription;
        if (TextUtils.isEmpty(description)) {
            tempDescription = view.getContext().getString(R.string.default_event_description);
        } else {
            tempDescription = description;
        }
        view.setText(tempDescription);
    }

    @BindingAdapter("placeWebAddress")
    public static void setPlaceWebAddress(TextView view, Place place) {
        if (place == null) return;
        SpannableStringBuilder ssb = H.makeTextViewClickable(place.getUrl(), place.getName(), view.getContext());
        view.setText(ssb);
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
