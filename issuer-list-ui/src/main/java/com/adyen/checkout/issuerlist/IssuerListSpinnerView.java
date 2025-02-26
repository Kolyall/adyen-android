/*
 * Copyright (c) 2019 Adyen N.V.
 *
 * This file is open source and available under the MIT license. See the LICENSE file for more info.
 *
 * Created by caiof on 26/4/2019.
 */

package com.adyen.checkout.issuerlist;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.adyen.checkout.base.ComponentView;
import com.adyen.checkout.base.api.ImageLoader;
import com.adyen.checkout.core.log.LogUtil;
import com.adyen.checkout.core.log.Logger;
import com.adyen.checkout.issuerlist.ui.R;

import java.util.Collections;
import java.util.List;

public abstract class IssuerListSpinnerView<IssuerListComponentT extends IssuerListComponent> extends LinearLayout implements
        ComponentView<IssuerListComponentT>, AdapterView.OnItemSelectedListener {
    private static final String TAG = LogUtil.getTag();

    @Nullable
    protected IssuerListComponentT mComponent;

    private final AppCompatSpinner mIssuersSpinner;

    private final IssuerListInputData mIdealInputData = new IssuerListInputData();

    private IssuerListSpinnerAdapter mIssuersAdapter;

    public IssuerListSpinnerView(@NonNull Context context) {
        this(context, null);
    }

    public IssuerListSpinnerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // Regular View constructor
    @SuppressWarnings("JavadocMethod")
    public IssuerListSpinnerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(getContext()).inflate(R.layout.issuer_list_spinner_view, this, true);

        mIssuersSpinner = findViewById(R.id.spinner_issuers);
    }

    @CallSuper
    @Override
    public void attach(@NonNull IssuerListComponentT component, @NonNull LifecycleOwner lifecycleOwner) {
        mComponent = component;

        mIssuersSpinner.setOnItemSelectedListener(this);
        mIssuersAdapter = new IssuerListSpinnerAdapter(getContext(),
                Collections.<IssuerModel>emptyList(),
                ImageLoader.getInstance(getContext(), component.getConfiguration().getEnvironment()), component.getPaymentMethodType(),
                hideIssuersLogo());
        mIssuersSpinner.setAdapter(mIssuersAdapter);

        mComponent.getIssuersLiveData().observe(lifecycleOwner, createIssuersObserver());

        mComponent.sendAnalyticsEvent(getContext());
    }

    @Override
    public boolean isConfirmationRequired() {
        return true;
    }

    public boolean hideIssuersLogo() {
        return false;
    }

    void onIssuersChanged(@Nullable List<IssuerModel> issuerList) {
        mIssuersAdapter.updateIssuers(issuerList);
    }

    @Override
    public void onItemSelected(@NonNull AdapterView<?> parent, @NonNull View view, int position, long id) {
        Logger.d(TAG, "onItemSelected - " + mIssuersAdapter.getItem(position).getName());
        mIdealInputData.setSelectedIssuer(mIssuersAdapter.getItem(position));
        mComponent.inputDataChanged(mIdealInputData);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIssuersSpinner.setEnabled(enabled);
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    @Override
    public void onNothingSelected(@NonNull AdapterView<?> parent) {
        // nothing changed
    }

    private Observer<List<IssuerModel>> createIssuersObserver() {
        return new Observer<List<IssuerModel>>() {
            @Override
            public void onChanged(@Nullable List<IssuerModel> issuerList) {
                onIssuersChanged(issuerList);
            }
        };
    }

    @NonNull
    protected Observer<IssuerListOutputData> createOutputDataObserver() {
        return new Observer<IssuerListOutputData>() {
            @Override
            public void onChanged(@Nullable IssuerListOutputData idealOutputData) {
                // Component does not change selected IssuerModel, add validation UI here if that's needed
            }
        };
    }
}
