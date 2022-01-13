package com.yespustak.yespustakapp.utils.shakeAnim;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by danishmchowdhry on 30/10/17.
 */

public class ShakeAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
        );
    }
}
