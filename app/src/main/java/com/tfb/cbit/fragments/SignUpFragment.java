package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.AboutUsActivity;
import com.tfb.cbit.activities.OTPVerificationActivity;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentSignUpBinding;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.statecity.StateCityDetailsModel;
import com.tfb.cbit.models.statecity.StatecityModel;
import com.tfb.cbit.models.wallet_transfer_otp.OTPModel;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class SignUpFragment extends Fragment {
    private static final String TAG = "SignUpFragment";

    private NewApiCall newApiCall;
    private Context context;
    private SessionUtil sessionUtil;
    private boolean isUserNameTaken = false;
    //FB
    private String socialid = "", socialtype = "";
    String code = "", mobile = "";
     StateAdapter stateAdapter;
    List<StateCityDetailsModel> Statecitylist = new ArrayList<>();

    CityAdapter cityAdapter;
    List<StateCityDetailsModel> Allcitylist = new ArrayList<>();
    List<StateCityDetailsModel> citylist = new ArrayList<>();

    int CITYID, STATEID;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String code, String mobile) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        bundle.putString("mobile", mobile);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SignUpFragment newInstance(Bundle bundle) {
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            code = getArguments().getString("code", "");
            mobile = getArguments().getString("mobile", "");

        }
     /*   OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.d("debug", "User:" + userId);
                OneSignalID = userId;
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);

            }
        });*/
    }
    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_sign_up, container, false));
        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);

        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString("fname", ""))) {
            binding.edtPassword.setVisibility(View.GONE);
            binding. edtFName.setText(getArguments().getString("fname", ""));
            binding. edtLName.setText(getArguments().getString("lname", ""));
            binding. edtEmail.setText(getArguments().getString("email", ""));
            socialid = getArguments().getString("socialid", "");
            socialtype = getArguments().getString("socialtype", "");
            binding. edtReferralCode.setText(CBit.referealCode);
        }
        binding. edtMobile.setText(mobile);
        binding.  edtReferralCode.setText(CBit.referealCode);
        binding. edtPassword.setText("12345678");
        getStateCity();
        /*edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(edtUserName.getText().toString().trim().length()>4) {
                        checkUsername();
                    }
                }
            }
        });*/
        binding. spnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding. spnCity.setSelection(0);
                if (position != 0) {
                    STATEID = Statecitylist.get(position - 1).getStateID();
                    citylist.clear();
                    for (int i = 0; i < Allcitylist.size(); i++) {
                        if (Statecitylist.get(position - 1).getStateID() == Allcitylist.get(i).getStateID()) {
                            citylist.add(Allcitylist.get(i));
                        }
                    }
                    cityAdapter.notifyDataSetChanged();

                } else {
                    citylist.clear();
                    cityAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                citylist.clear();
                cityAdapter.notifyDataSetChanged();

            }
        });
        binding. spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    CITYID = citylist.get(position - 1).getCityID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        customTextView( binding.tvAgree);

        binding.linearLogin.setOnClickListener(view1 -> {
            linearLoginClick();
        });
        binding.btnRegister.setOnClickListener(view1 -> {
            btnRegisterClick();
        });
        binding.ivBack.setOnClickListener(view1 -> {
            getActivity().onBackPressed();
        });


    }
    protected void linearLoginClick() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                    .commit();
        }
    }

    protected void btnRegisterClick() {
        if (isValidForm()) {
            checkUsername();
        }
    }
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "I agree to the ");
        spanTxt.append("Terms&Conditions");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.termscond));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.terms_link));
                startActivity(intent);
            }
        }, spanTxt.length() - "Term of services".length(), spanTxt.length(), 0);
        spanTxt.append(" and");
        spanTxt.append(" Privacy Policy");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.privacypolicy));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.privacy_link));
                startActivity(intent);
            }
        }, spanTxt.length() - " Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }



    private boolean isValidForm() {
        if (!MyValidator.isBlankETError(context,  binding.edtFName, "Enter Firstname", 1, 100)) {
            return false;
        }/* else if (!MyValidator.isBlankETError(context, edtMName, "Enter Middlename", 1, 100)) {
            return false;
        }*/ else if (!MyValidator.isBlankETError(context,  binding.edtLName, "Enter Surname", 1, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context,  binding.edtEmail, "Enter Email", 1, 100)) {
            return false;
        } else if (!MyValidator.isValidEmail(context, "Enter Valid Email",  binding.edtEmail)) {
            return false;
        } else if (Statecitylist.size() == 0 ||  binding.spnState.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select State", Toast.LENGTH_SHORT).show();
            return false;
        } /*else if (citylist.size() == 0 || spnCity.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please Select City", Toast.LENGTH_SHORT).show();
            return false;
        }*/ else if (!MyValidator.isBlankETError(context, binding. edtUserName, "Enter UserName", 4, 10)) {
            return false;
        } else if (socialid.isEmpty() && !MyValidator.isBlankETError(context,  binding.edtPassword, "Enter Password", 8, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context,  binding.edtMobile, "Enter Mobile", 10, 10)) {
            return false;
        } else if (! binding.chkAgree.isChecked()) {
            Utils.showToast(context, "Check to Agree Terms & Condition and Privacy Policy");
            return false;
        } else if (! binding.chkAge.isChecked()) {
            binding. tvAge.setTextColor(getActivity().getResources().getColor(R.color.color_red));
            //    Utils.showToast(context, "Check to Agree Terms & Condition and Privacy Policy");
            return false;
        } else if (! binding.chkState.isChecked()) {
            binding.  tvState.setTextColor(getActivity().getResources().getColor(R.color.color_red));
            binding. tvAge.setTextColor(getActivity().getResources().getColor(R.color.white));
            //    Utils.showToast(context, "Check to Agree Terms & Condition and Privacy Policy");
            return false;
        }/*else if(isUserNameTaken){
            edtUserName.setError("username already taken");
            edtUserName.requestFocus();
            return false;
        }*/ else {
            binding. tvState.setTextColor(getActivity().getResources().getColor(R.color.white));
            binding. tvAge.setTextColor(getActivity().getResources().getColor(R.color.white));
            return true;
        }
    }


    private void getStateCity() {
        System.err.println("getToken " + sessionUtil.getToken());
        System.err.println("getId " + sessionUtil.getId());
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getStateCity(sessionUtil.getToken(), sessionUtil.getId());

        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success>>>: " + responseData);
                Gson gson = new Gson();
                StatecityModel statecitymodel = gson.fromJson(responseData, StatecityModel.class);
                if (statecitymodel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Statecitylist.addAll(statecitymodel.getContent().getStateList());
                    Allcitylist.addAll(statecitymodel.getContent().getCityList());
                }
                stateAdapter = new StateAdapter(getActivity(), Statecitylist);
                binding.spnState.setAdapter(stateAdapter);

                cityAdapter = new CityAdapter(getActivity(), citylist);
                binding. spnCity.setAdapter(cityAdapter);

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void getOtp() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("mobile_no", binding. edtMobile.getText().toString().trim());
            jsonObject.put("email",  binding.edtEmail.getText().toString().trim());
            jsonObject.put("ReferralCode",  binding.edtReferralCode.getText().toString().trim());
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().sendOtpAuth(request);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                OTPModel otpModel = gson.fromJson(responseData, OTPModel.class);
                if (otpModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fname", binding. edtFName.getText().toString().trim());
                    bundle.putString("mname", binding. edtMName.getText().toString().trim());
                    bundle.putString("lname",  binding.edtLName.getText().toString().trim());
                    bundle.putString("email",  binding.edtEmail.getText().toString().trim());
                    bundle.putString("userName", binding. edtUserName.getText().toString().trim());
                    bundle.putString("password",  binding.edtPassword.getText().toString().trim());
                    bundle.putString("ReferralCode",  binding.edtReferralCode.getText().toString().trim());
                    bundle.putString("mobile_no",  binding.edtMobile.getText().toString().trim());
                    bundle.putString("deviceId", sessionUtil.getFcmtoken());
                    bundle.putString("deviceType", "android");
                    bundle.putString("socialid", socialid);
                    bundle.putInt("StateId", STATEID);
                    bundle.putInt("CityId", CITYID);
                    bundle.putString("socialtype", socialtype);
                    bundle.putString("otpId", String.valueOf(otpModel.getContent().getOtpId()));
                    bundle.putString("otp", String.valueOf(otpModel.getContent().getOtp()));
                    bundle.putString(OTPVerificationActivity.SCREEN_TYPE, "register");
                    Intent intent = new Intent(context, OTPVerificationActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Utils.showToast(context, otpModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    private void checkUsername() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("userName",  binding.edtUserName.getText().toString().trim());
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
            System.err.println("username request " + request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().checkUserName(request);
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    getOtp();
                } else if (commonRes.getStatus() == Utils.StandardStatusCodes.NO_DATA_FOUND) {
                    isUserNameTaken = true;
                    binding. edtUserName.requestFocus();
                    binding. edtUserName.setError(commonRes.getMessage());
                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                isUserNameTaken = true;
            }
        });
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

    }

    class StateAdapter extends BaseAdapter {
        List<StateCityDetailsModel> stateList;
        LayoutInflater inflter;
        Context context;

        public StateAdapter(Context context, List<StateCityDetailsModel> stateLists) {
            this.context = context;
            this.stateList = stateLists;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return stateList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return stateList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflter.inflate(R.layout.custom_spinner_items, null);
            TextView names = convertView.findViewById(R.id.tv_spinner);
            if (position == 0) {
                names.setText("Select State");
                names.setTextColor(Color.GRAY);
            } else {
                StateCityDetailsModel categoryModel = stateList.get(position - 1);
                names.setText(categoryModel.getStateName());
                names.setTextColor(Color.BLACK);
            }
            return convertView;
        }


    }

    class CityAdapter extends BaseAdapter {
        List<StateCityDetailsModel> cityList;
        LayoutInflater inflter;
        Context context;

        public CityAdapter(Context context, List<StateCityDetailsModel> cityLists) {
            this.context = context;
            this.cityList = cityLists;
            inflter = (LayoutInflater.from(context));
        }

        @Override
        public int getCount() {
            return cityList.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflter.inflate(R.layout.custom_spinner_items, null);
            TextView names = convertView.findViewById(R.id.tv_spinner);
            if (position == 0) {
                names.setText("Select City");
                names.setTextColor(Color.GRAY);
            } else {
                StateCityDetailsModel categoryModel = cityList.get(position - 1);
                names.setText(categoryModel.getCityName());
                names.setTextColor(Color.BLACK);
            }
            return convertView;
        }


    }
}
