package com.yespustak.yespustakapp.utils.shakeAnim;

/**
 * Created by danishmchowdhry on 30/10/17.
 */

public enum Techniques {

    Shake(ShakeAnimator.class);

    private Class animatorClazz;

    private Techniques(Class clazz) {
        animatorClazz = clazz;
    }

    public BaseViewAnimator getAnimator() {
        try {
            return (BaseViewAnimator) animatorClazz.newInstance();
        } catch (Exception e) {
            throw new Error("Can not init animatorClazz instance");
        }
    }
}
