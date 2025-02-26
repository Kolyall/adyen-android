/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by arman on 18/9/2019.
 */

package com.adyen.checkout.bcmc;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.adyen.checkout.base.ComponentAvailableCallback;
import com.adyen.checkout.base.PaymentComponentProvider;
import com.adyen.checkout.base.component.lifecycle.ComponentViewModelFactory;
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod;
import com.adyen.checkout.core.util.StringUtil;

public class BcmcComponentProvider implements PaymentComponentProvider<BcmcComponent, BcmcConfiguration> {

    @NonNull
    @Override
    public BcmcComponent get(@NonNull FragmentActivity activity, @NonNull PaymentMethod paymentMethod, @NonNull BcmcConfiguration configuration) {
        final ComponentViewModelFactory factory = new ComponentViewModelFactory(paymentMethod, configuration);
        return ViewModelProviders.of(activity, factory).get(BcmcComponent.class);
    }

    @NonNull
    @Override
    public BcmcComponent get(@NonNull Fragment fragment, @NonNull PaymentMethod paymentMethod, @NonNull BcmcConfiguration configuration) {
        final ComponentViewModelFactory factory = new ComponentViewModelFactory(paymentMethod, configuration);
        return ViewModelProviders.of(fragment, factory).get(BcmcComponent.class);
    }

    @Override
    public void isAvailable(
            @NonNull Application applicationContext,
            @NonNull PaymentMethod paymentMethod,
            @NonNull BcmcConfiguration configuration,
            @NonNull ComponentAvailableCallback<BcmcConfiguration> callback) {

        // TODO: 2019-07-23 Check if pubkey meets regex requirements
        final boolean isPubKeyAvailable = StringUtil.hasContent(configuration.getPublicKey());
        callback.onAvailabilityResult(isPubKeyAvailable, paymentMethod, configuration);
    }
}
