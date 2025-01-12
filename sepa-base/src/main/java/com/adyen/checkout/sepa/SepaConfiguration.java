/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 22/8/2019.
 */

package com.adyen.checkout.sepa;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.adyen.checkout.base.component.BaseConfiguration;
import com.adyen.checkout.base.component.BaseConfigurationBuilder;
import com.adyen.checkout.core.api.Environment;
import com.adyen.checkout.core.code.Lint;

import java.util.Locale;

public class SepaConfiguration extends BaseConfiguration {

    public static final Parcelable.Creator<SepaConfiguration> CREATOR = new Parcelable.Creator<SepaConfiguration>() {
        public SepaConfiguration createFromParcel(@NonNull Parcel in) {
            return new SepaConfiguration(in);
        }

        public SepaConfiguration[] newArray(int size) {
            return new SepaConfiguration[size];
        }
    };

    @SuppressWarnings(Lint.SYNTHETIC)
    SepaConfiguration(@NonNull Locale shopperLocale,
            @NonNull Environment environment) {
        super(shopperLocale, environment);
    }

    SepaConfiguration(@NonNull Parcel in) {
        super(in);
    }

    public static final class Builder extends BaseConfigurationBuilder<SepaConfiguration> {

        public Builder(@NonNull Context context) {
            super(context);
        }

        public Builder(@NonNull Locale shopperLocale, @NonNull Environment environment) {
            super(shopperLocale, environment);
        }

        @NonNull
        @Override
        public SepaConfiguration build() {
            return new SepaConfiguration(mBuilderShopperLocale, mBuilderEnvironment);
        }
    }
}
