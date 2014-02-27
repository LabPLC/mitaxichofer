package codigo.labplc.mx.mitaxichofer.trip.location;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import codigo.labplc.mx.mitaxichofer.R;

/**
 * 
 * @author zace3d
 * 
 */
public class AnimationFactory {
	
	/**
	 * Create and show a slide down animation
	 * 
	 * @param context
	 * @param view
	 */
	public static void slideDown(Context context, final View view) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_down);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
	}
	
	/**
	 * Create and show a slide up animation
	 * 
	 * @param context
	 * @param view
	 */
	public static void slideUp(Context context, final View view) {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.slide_up);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.VISIBLE);
			}
		});
	}
	
	/**
	 * Create and show a fade in animation
	 * 
	 * @param context
	 * @param view
	 */
	public static void fadeIn(Context context, final View view) {
		Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.VISIBLE);
			}
		});
	}
	
	/**
	 * Create and show a fade out animation
	 * @param context
	 * @param view
	 */
	public static void fadeOut(Context context, final View view) {
		Animation anim = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
		view.startAnimation(anim);
		anim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.setVisibility(View.GONE);
			}
		});
	}
}