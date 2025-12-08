package com.tfb.cbit.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.SearchAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivitySearchGroupBinding;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.MyJTicket.MyJTcktModel;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SearchGroupActivity extends AppCompatActivity {


    public PrivateGroupResponse.Content product;
    public ArrayList<PrivateGroupResponse.Content> productArrayList = new ArrayList<>();
    public SearchAdapter productAdapter;
    public TextInputLayout ip_usernames;
    private Context context;
    private SessionUtil sessionUtil;
    private List<PrivateGroupResponse.Content> contentList = new ArrayList<>();

    private ActivitySearchGroupBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerCategory.setLayoutManager(layoutManager);
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        binding.recyclerCategory.setLayoutAnimation(controller);
        productAdapter = new SearchAdapter(this, productArrayList);
        binding.recyclerCategory.setAdapter(productAdapter);
        productAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                hitJoinGroupApi(position);
            }
        });
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSearch(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.txtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.edtSearch.getText().toString().isEmpty()) {
                    ip_usernames.setError("plese enter item name");

                } else {
                    getSearch(binding.edtSearch.getText().toString());
                }

            }
        });
    }

    public String request = "";

    private void hitJoinGroupApi(int position) {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("group_id", contentList.get(position).getId());
            request = jsonObject.toString();
            Log.i("send request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ResponseBody> call = APIClient.getInstance().requestToJoinPrivateGroup(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                onBackPressed();
            }

            @Override
            public void failure(String responseData) {
            }
        });
    }

    private void getSearch(CharSequence s) {
        Call<ResponseBody> call = APIClient.getInstance().allPrivateGroup(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                PrivateGroupResponse nm = gson.fromJson(responseData, PrivateGroupResponse.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    contentList.clear();
                    contentList.addAll(nm.getContent());
                    productAdapter.addAll(nm.getContent());
                }

                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }
}