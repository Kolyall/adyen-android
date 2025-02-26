/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 13/5/2019.
 */

package com.adyen.checkout.base.component;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.adyen.checkout.base.ActionComponent;
import com.adyen.checkout.base.ActionComponentData;
import com.adyen.checkout.base.ComponentError;
import com.adyen.checkout.base.model.payments.response.Action;
import com.adyen.checkout.core.exeption.CheckoutException;
import com.adyen.checkout.core.exeption.ComponentException;
import com.adyen.checkout.core.log.LogUtil;
import com.adyen.checkout.core.log.Logger;
import com.adyen.checkout.core.util.StringUtil;

import org.json.JSONObject;

import java.util.List;

public abstract class BaseActionComponent extends AndroidViewModel implements ActionComponent {
    private static final String TAG = LogUtil.getTag();

    private static final String PAYMENT_DATA_KEY = "payment_data";

    private final MutableLiveData<ActionComponentData> mResultLiveData = new MutableLiveData<>();

    private final MutableLiveData<ComponentError> mErrorMutableLiveData = new MutableLiveData<>();

    private String mPaymentData;

    public BaseActionComponent(@NonNull Application application) {
        super(application);
    }

    @Override
    public boolean canHandleAction(@NonNull Action action) {
        return getSupportedActionTypes().contains(action.getType());
    }

    @NonNull
    protected abstract List<String> getSupportedActionTypes();

    @Override
    public void handleAction(@NonNull Activity activity, @NonNull Action action) {
        if (!canHandleAction(action)) {
            notifyException(new ComponentException("Action type not supported by this component - " + action.getType()));
            return;
        }

        mPaymentData = action.getPaymentData();
        try {
            handleActionInternal(activity, action);
        } catch (ComponentException e) {
            notifyException(e);
        }
    }

    @Override
    public void observe(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<ActionComponentData> observer) {
        mResultLiveData.observe(lifecycleOwner, observer);
    }

    @Override
    public void observeErrors(@NonNull LifecycleOwner lifecycleOwner, @NonNull Observer<ComponentError> observer) {
        mErrorMutableLiveData.observe(lifecycleOwner, observer);
    }

    /**
     * Call this method to save the current data of the Component to the Bundle from {@link Activity#onSaveInstanceState(Bundle)}.
     *
     * @param bundle The bundle to save the sate into.
     */
    public void saveState(@Nullable Bundle bundle) {
        if (bundle != null && StringUtil.hasContent(mPaymentData)) {
            if (bundle.containsKey(PAYMENT_DATA_KEY)) {
                Logger.d(TAG, "bundle already has paymentData, overriding");
            }
            bundle.putString(PAYMENT_DATA_KEY, mPaymentData);
        }
    }

    /**
     * Call this method to restore the current data of the Component from the savedInstanceState Bundle from {@link Activity#onCreate(Bundle)}}.
     *
     * @param bundle The bundle to restore the sate from.
     */
    public void restoreState(@Nullable Bundle bundle) {
        if (bundle != null && bundle.containsKey(PAYMENT_DATA_KEY) && !StringUtil.hasContent(mPaymentData)) {
            mPaymentData = bundle.getString(PAYMENT_DATA_KEY);
        }
    }

    protected abstract void handleActionInternal(@NonNull Activity activity, @NonNull Action action) throws ComponentException;

    protected void notifyDetails(@NonNull JSONObject details) throws ComponentException {
        final ActionComponentData actionComponentData = new ActionComponentData();
        actionComponentData.setDetails(details);
        actionComponentData.setPaymentData(mPaymentData);

        mResultLiveData.setValue(actionComponentData);
    }

    protected void notifyException(@NonNull CheckoutException e) {
        mErrorMutableLiveData.setValue(new ComponentError(e));
    }
}
