package com.tfb.cbit.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.activities.ForgotPasswordActivity;
import com.tfb.cbit.activities.HomeActivity;
import com.tfb.cbit.activities.LoginSignUpActivity;
import com.tfb.cbit.activities.TutorialActivity;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.FragmentAboutBinding;
import com.tfb.cbit.databinding.FragmentLoginBinding;
import com.tfb.cbit.models.login_register.LoginRegisterModel;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private NewApiCall newApiCall;
    private Context context;
    DecimalFormat format = new DecimalFormat("0.##");
    CallbackManager callbackManager;
    private SessionUtil sessionUtil;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
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

    }

    private FragmentLoginBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.bind(LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_login, container, false));
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionUtil = new SessionUtil(context);

        newApiCall = new NewApiCall();

        callbackManager = CallbackManager.Factory.create();
        facebookLoginManagerCallback();

        binding.ivFacebook.setOnClickListener(view1 -> {
            ivFacebookClick();
        });
        binding.btnLogin.setOnClickListener(view1 -> {
            btnLoginClick();
        });
        binding.tvForgotPass.setOnClickListener(view1 -> {
            tvForgotPassClick();
        });
        binding.linearRegister.setOnClickListener(view1 -> {
            linearRegisterClick();
        });
        binding.linearloginwithmob.setOnClickListener(view1 -> {
            linearClick();
        });
        binding.tvHowtoPlay.setOnClickListener(view1 -> {
            tvHowtoPlayClick();
        });
    }

    private void facebookLoginManagerCallback() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        Log.d(TAG, "loginresult: " + loginResult.toString());
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        fbSignIn(object, loginResult.getAccessToken().getToken());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Utils.showToast(context, "Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("TAG", exception.getMessage() + "");
                    }
                });
    }

    // getting detail of facebook user profile
    private void fbSignIn(JSONObject user, String accessToken) {
        //System.out.println("FB Login : " + user);
        String facebookfirstName = user.optString("first_name");
        String facebooklastname = user.optString("last_name");
        String facebookemail = user.optString("email");
        //  JSONObject picObj = user.optJSONObject("picture");
        // JSONObject data = picObj.optJSONObject("data");
        // facebookpicture = data.optString("url");
        //Log.d(TAG,"FB Pic "+facebookpicture);
        String facebookid = user.optString("id");
        PrintLog.e("TAG", "Facebook " + facebookid);
        if (LoginManager.getInstance() != null)
            LoginManager.getInstance().logOut();

        // String fullName = facebookfirstName + " " +facebooklastname;
        getSocialLogin(facebookid, facebookfirstName, facebooklastname, facebookemail, "Facebook");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void ivFacebookClick() {
        if (Utils.isNetworkAvailable(context)) {
            LoginManager.getInstance().logInWithReadPermissions(this,
                    Arrays.asList("public_profile", "email"));
        } else {
            Utils.showToast(context, "No Internet Available");
        }
    }

    protected void btnLoginClick() {
        if (isValidForm()) {
            getSignIn();
        }
    }

    protected void tvForgotPassClick() {
        startActivity(new Intent(context, ForgotPasswordActivity.class));
    }

    protected void linearRegisterClick() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, SignUpFragment.newInstance("", ""))
                    .commit();
        }
    }

    protected void linearClick() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
//                    .replace(R.id.frameContainer, LoginWithMobileFragment.newInstance())
                    .replace(R.id.frameContainer, LoginFragment.newInstance())
                    .commit();
        }
    }

    protected void tvHowtoPlayClick() {
        startActivity(new Intent(context, TutorialActivity.class));
        //startActivity(new Intent(context, HowtoPlayActivity.class));
    }

    private boolean isValidForm() {
        return MyValidator.isBlankETError(context, binding.edtEmail, "Enter Email", 1, 100) &&
                MyValidator.isValidEmail(context, "Enter Valid Email", binding.edtEmail) &&
                MyValidator.isBlankETError(context, binding.edtPassword, "Enter Password", 8, 100);
    }

    private void getSignIn2() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("email", sessionUtil.getEmail());
            jsonObject.put("password", sessionUtil.getPass());
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());// OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            jsonObject.put("version", Utils.getVersionName(context));

            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSignIn: " + jsonObject.toString());
        Call<ResponseBody> call = APIClient.getInstance()
                .signIn(request);
        //.signIn(sessionUtil.getEmail(),sessionUtil.getPass(),sessionUtil.getFcmtoken(),"android");
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(), loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(), sessionUtil.getPass(), loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(), String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(), loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(), loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(), "", "", loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())), loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    startActivity(new Intent(context, HomeActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                } else {
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    startActivity(new Intent(context, LoginSignUpActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                }
            }

            @Override
            public void failure(String responseData) {
                String fcmToken = sessionUtil.getFcmtoken();
                sessionUtil.logOut();
                sessionUtil.setFCMToken(fcmToken);
                startActivity(new Intent(context, LoginSignUpActivity.class));
                getActivity().finish();
            }
        });

    }


    private void getSignIn() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("email", binding.edtEmail.getText().toString().trim());
            jsonObject.put("password", binding.edtPassword.getText().toString().trim());
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());// OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            jsonObject.put("version", Utils.getVersionName(context));

            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.d(TAG, "getSignIn: "+jsonObject.toString());
        Call<ResponseBody> call = APIClient.getInstance()
                .signIn(request);
        //.signIn(edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim(),sessionUtil.getFcmtoken(),"android");
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "Social Login success: " + responseData);
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(),
                            loginRegisterModel.getContest().getMiddelName(),
                            loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(),
                            binding.edtPassword.getText().toString().trim(),
                            loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(),
                            String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(),
                            loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(),
                            loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(), "", "",
                            loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())),
                            loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());

                    startActivity(new Intent(context, HomeActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                } else {
                    Utils.showToast(context, loginRegisterModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                if (!responseData.isEmpty()) {
                    Utils.showToast(context, responseData);
                }
            }
        });

    }


    private void getSocialLogin(final String social_Id, final String fname, final String lname, final String email, final String social_type) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("social_Id", social_Id);
            jsonObject.put("social_Type", social_type);
            jsonObject.put("deviceId", sessionUtil.getFcmtoken());
            jsonObject.put("deviceType", "android");
            jsonObject.put("OneSignalID", OneSignal.getDeviceState().getUserId());// OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            jsonObject.put("version", Utils.getVersionName(context));

            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance()
                .signIn(request);
        //.signIn(edtEmail.getText().toString().trim(),edtPassword.getText().toString().trim(),sessionUtil.getFcmtoken(),"android");
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.d(TAG, "logindata: " + responseData);
                Gson gson = new Gson();
                LoginRegisterModel loginRegisterModel = gson.fromJson(responseData, LoginRegisterModel.class);
                if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    SessionUtil sessionUtil = new SessionUtil(context);
                    sessionUtil.setData(loginRegisterModel.getContest().getToken(),
                            loginRegisterModel.getContest().getFirstName(), loginRegisterModel.getContest().getMiddelName(),
                            loginRegisterModel.getContest().getLastName(),
                            loginRegisterModel.getContest().getEmail(),
                            loginRegisterModel.getContest().getMobileNo(),
                            binding.edtPassword.getText().toString().trim(),
                            loginRegisterModel.getContest().getProfileImage(),
                            loginRegisterModel.getContest().getMyCode(),
                            String.valueOf(loginRegisterModel.getContest().getId()),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getPbAmount())),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getSbAmount())),
                            loginRegisterModel.getContest().getSetNotification(),
                            loginRegisterModel.getContest().getUserName(),
                            loginRegisterModel.getContest().getVerify_pan(),
                            loginRegisterModel.getContest().getVerify_bank(),
                            loginRegisterModel.getContest().getVerify_email(),
                            social_Id, social_type, loginRegisterModel.getContest().getReferralCode(),
                            String.valueOf(format.format(loginRegisterModel.getContest().getWallateDetails().getCcAmount())), loginRegisterModel.getContest().getWallateDetails().getWalletAuth(), false,
                            loginRegisterModel.getContest().getAutoPilot(),
                            loginRegisterModel.getContest().getIsRedeem());
                    startActivity(new Intent(context, HomeActivity.class));
                    if (getActivity() != null)
                        getActivity().finish();
                } else if (loginRegisterModel.getStatusCode() == Utils.StandardStatusCodes.NO_DATA_FOUND) {
                    Bundle bundle = new Bundle();
                    bundle.putString("fname", fname);
                    bundle.putString("lname", lname);
                    bundle.putString("email", email);
                    bundle.putString("socialid", social_Id);
                    bundle.putString("socialtype", social_type);
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameContainer, SignUpFragment.newInstance(bundle))
                                .commit();
                    }
                    //EventBus.getDefault().post(new FbRegister(social_Id,social_type,email,fname));
                } else {
                    Utils.showToast(context, loginRegisterModel.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {
                if (!responseData.isEmpty()) {
                    Utils.showToast(context, responseData);
                }
            }
        });

    }


    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }
}
