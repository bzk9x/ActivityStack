package com.bzk9x.activitystack;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.animation.ValueAnimator;
import android.graphics.drawable.LayerDrawable;

public class MainActivity extends AppCompatActivity {
    private Vibrator vibrator;
    private AnimatorSet scaleDown;
    private Intent showStack;
    private Handler handler;
    private static final int CROSS_FADE_DURATION = 350;
    private LinearLayout body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        body = findViewById(R.id.body);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        showStack = new Intent();
        handler = new Handler(Looper.getMainLooper());

        ObjectAnimator scale_down_activity_x = ObjectAnimator.ofFloat(body, "scaleX", 1f, 0.9f);
        ObjectAnimator scale_down_activity_y = ObjectAnimator.ofFloat(body, "scaleY", 1f, 0.95f);

        scaleDown = new AnimatorSet();
        scaleDown.playTogether(scale_down_activity_x, scale_down_activity_y);
        scaleDown.setDuration(350);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button.setOnClickListener(view -> {
            scaleDown.start();

            Drawable currentBackground = body.getBackground();
            Drawable stackDrawable = ContextCompat.getDrawable(this, R.drawable.stack);

            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{
                    stackDrawable,
                    currentBackground
            });
            body.setBackground(layerDrawable);

            ValueAnimator crossfade = ValueAnimator.ofFloat(0f, 1f);
            crossfade.setDuration(CROSS_FADE_DURATION);
            crossfade.addUpdateListener(animation -> {
                float alpha = (float) animation.getAnimatedValue();
                if (currentBackground != null) {
                    currentBackground.setAlpha((int) ((1 - alpha) * 255));
                }
                if (stackDrawable != null) {
                    stackDrawable.setAlpha((int) (alpha * 255));
                }
            });
            crossfade.start();

            handler.postDelayed(() -> {
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE));
                    }
                }
            }, 350);

            showStack.setClass(getApplicationContext(), StackActivity.class);
            startActivity(showStack);
            overridePendingTransition(R.anim.slide_in_bottom, 0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (scaleDown != null) {
            scaleDown.cancel();
        }

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(body, "scaleX", body.getScaleX(), 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(body, "scaleY", body.getScaleY(), 1f);

        int startColor = Color.TRANSPARENT;
        Drawable background = body.getBackground();
        if (background instanceof ColorDrawable) {
            startColor = ((ColorDrawable) background).getColor();
        }

        ValueAnimator colorAnimation = ValueAnimator.ofObject(
                new ArgbEvaluator(),
                startColor,
                Color.WHITE
        );
        colorAnimation.addUpdateListener(animator ->
                body.setBackgroundColor((int) animator.getAnimatedValue())
        );

        AnimatorSet resetAnimations = new AnimatorSet();
        resetAnimations.playTogether(scaleX, scaleY, colorAnimation);
        resetAnimations.setDuration(350);
        resetAnimations.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scaleDown != null) {
            scaleDown.cancel();
            scaleDown = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }
}