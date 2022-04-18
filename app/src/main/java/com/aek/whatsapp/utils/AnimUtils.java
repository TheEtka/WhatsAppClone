package com.aek.whatsapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnimUtils {

    public static void showFabAddEstado(int positionTab,
                                        FloatingActionButton floatingActionButton,
                                        Context context) {

        floatingActionButton.animate().translationY(positionTab == 2 || positionTab == 3 ?
                PixelsDPUtils.convertPixelsToDp(-58, context) :
                PixelsDPUtils.convertPixelsToDp(0, context)).setDuration(150);

    }

    public static void showSearchView(SearchView searchView) {
        searchView.setVisibility(View.VISIBLE);

        Animator animator = ViewAnimationUtils.createCircularReveal(searchView,
                (searchView.getRight() + searchView.getLeft() / 2),
                (searchView.getTop() + searchView.getBottom() / 2),
                0f,
                searchView.getWidth());

        animator.setDuration(350);
        animator.start();
    }

    public static void hideSearchView(SearchView searchView, AppBarLayout appBarLayout) {
        Animator animator = ViewAnimationUtils.createCircularReveal(searchView,
                (searchView.getRight() + searchView.getLeft() / 2),
                (searchView.getTop() + searchView.getBottom() / 2),
                searchView.getWidth(),
                0f);

        animator.setDuration(300);
        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                searchView.setVisibility(View.INVISIBLE);
                appBarLayout.setExpanded(true,true);
                animation.removeAllListeners();
            }
        });
    }

    public static void scaleFab(FloatingActionButton actionButton) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                actionButton.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100);
            }
        },450);
    }

    public static void animateBtnTakePhoto(ImageButton button) {
        button.animate().scaleX(0.8f).scaleY(0.8f).setDuration(50).withEndAction(new Runnable() {
            @Override
            public void run() {
                button.animate().scaleX(1.0f).scaleY(1.0f);
            }
        });
    }

    public static void rotateSwitchCameraButton(ImageButton button) {
        button.animate().rotationY(180).scaleX(1.3f).scaleY(1.3f)
                .setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                button.setRotationY(0);
                button.animate().scaleX(1.0f).scaleY(1.0f);
            }
        });
    }

    public static void changeSwitchFlashButtonIcon(ImageButton button, int img,
                                                   Context context) {

        button.animate().scaleY(0.5f).scaleX(0.5f).alpha(0f).setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.setImageDrawable(context.getDrawable(img));
                        button.animate().scaleY(1.0f).scaleX(1.0f).alpha(1f).setDuration(100);
                    }
                });

    }

    public static void scaleView(View view, int duration, float scale) {
        view.animate().scaleX(scale).scaleY(scale).setDuration(duration);
    }

    public static void showView(View view, int duration, float alpha) {
        view.animate().setDuration(duration).alpha(alpha);
    }

    public static void changeIconButtonSendMessage(int resIcon, ImageView imageView) {
        imageView.animate().scaleY(0.8f).scaleX(0.8f).alpha(0f).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(resIcon);
                imageView.animate().scaleY(1f).scaleX(1f).alpha(1f).setDuration(150);
            }
        });
    }

    public static void animateIconsChat(ImageButton button1, ImageButton button2, int value, Context context) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(button1, "translationX", PixelsDPUtils.convertPixelsToDp(value, context)),
                ObjectAnimator.ofFloat(button2, "translationX", PixelsDPUtils.convertPixelsToDp(value, context))
        );
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    public static void showViewAdjuntarArchivos(View viewAdjuntarArchivos, boolean isInvisible, Context context) {

        int x = viewAdjuntarArchivos.getRight();
        int y = viewAdjuntarArchivos.getBottom();

        x -= (PixelsDPUtils.convertPixelsToDp(112,context) + PixelsDPUtils.convertPixelsToDp(6,context));

        int hypot = (int) Math.hypot(viewAdjuntarArchivos.getWidth(), viewAdjuntarArchivos.getHeight());

        Animator anim;

        if (isInvisible) {
            anim = ViewAnimationUtils.createCircularReveal(viewAdjuntarArchivos, x, y, 0, hypot);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    viewAdjuntarArchivos.setVisibility(View.VISIBLE);
                }
            });

        } else {
            anim = ViewAnimationUtils.createCircularReveal(viewAdjuntarArchivos, x, y, hypot, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    viewAdjuntarArchivos.setVisibility(View.GONE);
                }
            });
        }

        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();

    }

}
