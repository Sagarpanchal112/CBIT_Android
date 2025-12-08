package com.tfb.cbit.activities;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.AnySpinerTicketSelectionAdapter;
import com.tfb.cbit.adapter.AnyTicketSelectionAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityAnyTimeSpinningBinding;
import com.tfb.cbit.event.UpdateUpcomingContestEvent;
import com.tfb.cbit.fragments.AnyTimeGameFragment;
import com.tfb.cbit.fragments.AnyTimeSpinningGameFragment;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.interfaces.OnItemLongClickListener;
import com.tfb.cbit.models.AnyTimeGameContestList;
import com.tfb.cbit.models.AnyTimeSpinningCatList;
import com.tfb.cbit.models.anytimegame.AnyTimeGameResponse;
import com.tfb.cbit.models.anytimegame.Content;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.CountDown;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.CustPagerTransformer;
import com.tfb.cbit.views.PagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AnyTimeSpinningActivity extends FragmentActivity {
    private static final String TAG = "AnyTimeSpinningActivity";
    private CountDown remainingTime = null;
    private Context context;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    ArrayList<String> iamgesList;
    private NewApiCall newApiCall;
    private SessionUtil sessionUtil;
    private List<Ticket> ticketList = new ArrayList<>();
    private String contestId = "";
    public int no_of_players = 0, pending = 0, played = 0, max_winner = 0, max_winner_per = 0;

    private List<Content> anyticketList = new ArrayList<>();
    public PagerSlidingTabStrip pagerTabStri;
    public static ViewPager viewPager;
    public ArrayList<AnyTimeSpinningGameFragment> fragments = new ArrayList<>();
    public ArrayList<AnyTimeSpinningCatList.Lst> anyTimeGameContestListArrayList = new ArrayList<>();

    public QuestionAdapter questionAdapter;
    private ActivityAnyTimeSpinningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnyTimeSpinningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        newApiCall = new NewApiCall();
        sessionUtil = new SessionUtil(context);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        //  toolbar_title.setText(bundle.getString(CONTEST_NAME, ""));
        pagerTabStri = (PagerSlidingTabStrip) findViewById(R.id.pager_header);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));
        //getAnyTimeGameContestList();
        spinningItemCategory();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        long mill = Utils.convertMillSeconds("2021-03-18 21:40:00", newdate);
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        remainingTime = new CountDown(mill, 1000) {
            @Override
            public void onTick(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setupSlot();
                            // FadinAnimaiton(rv_I);
                            //   FadinAnimaiton(rv_IV);
                            //  FadinAnimaiton(rv_VII);
                            //  FadinAnimaiton(rv_X);
                            // FadinAnimaiton(rv_XIII);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFinish() {
            }
        };
        remainingTime.start();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pagerTabStri.currentPosition = position;
                pagerTabStri.currentPositionOffset = positionOffset;

                pagerTabStri.scrollToChild(position, (int) (positionOffset * pagerTabStri.tabsContainer.getChildAt(position).getWidth()));

                pagerTabStri.invalidate();

                if (pagerTabStri.delegatePageListener != null) {
                    pagerTabStri.delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (pagerTabStri.delegatePageListener != null) {
                    //   currentOpenedFragment = position;
                    ///  Utils.Log("TAG", "Page no In listener--> " + position);

                    pagerTabStri.delegatePageListener.onPageSelected(position);

                }
                binding.tvGameName.setText(anyTimeGameContestListArrayList.get(position).getName());
                binding.toolbarTitle.setText("Spinning machine");

                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "You have moved to first game", Toast.LENGTH_LONG).show();
                } else if (anyTimeGameContestListArrayList.size() - 1 == position) {
                    Toast.makeText(getApplicationContext(), "You have moved to last game", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    pagerTabStri.scrollToChild(pagerTabStri.pager.getCurrentItem(), 0);
                }

                if (pagerTabStri.delegatePageListener != null) {
                    pagerTabStri.delegatePageListener.onPageScrollStateChanged(state);
                }
            }
        });

        binding.imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Pos = viewPager.getCurrentItem();
                if (Pos > 0) {
                    viewPager.setCurrentItem(Pos - 1);
                }
            }
        });
        binding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Pos = viewPager.getCurrentItem();
                if (Pos < anyTimeGameContestListArrayList.size()) {
                    viewPager.setCurrentItem(Pos + 1);
                }
            }
        });

    }

    String game_type;

    private void getAnyTimeGameContestList() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        String request = "";
        try {
            jsonObject.put("game_type", game_type);
            jsonObject.put("isSpinningMachine", "0");
            request = jsonObject.toString();
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes(StandardCharsets.UTF_8);
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient
                .getInstance()
                .getAnyTimeGameContestList(sessionUtil.getToken(), sessionUtil.getId(), request);
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                PrintLog.e(TAG, "getTicketDetails success :=> " + responseData);
                //  rvTicketSelection.showRecycler();
                Gson gson = new Gson();
                AnyTimeGameContestList anyTimeGameResponse = gson.fromJson(responseData, AnyTimeGameContestList.class);
                // anyTimeGameContestListArrayList.addAll(anyTimeGameResponse.getContent());

                Log.i("Size", "==>" + anyTimeGameContestListArrayList.size());

                for (int i = 0; i < anyTimeGameContestListArrayList.size(); i++) {
                    fragments.add(new AnyTimeSpinningGameFragment().newInstance(i));
                }
                questionAdapter = new QuestionAdapter(getSupportFragmentManager(), fragments);
                viewPager.setAdapter(questionAdapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
                viewPager.setPageMargin(pageMargin);
                pagerTabStri.setViewPager(viewPager);
                questionAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void spinningItemCategory() {
        Call<ResponseBody> call = APIClient
                .getInstance()
                .spinningItemCategory(sessionUtil.getToken(), sessionUtil.getId());
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                PrintLog.e(TAG, "getTicketDetails success :=> " + responseData);
                Gson gson = new Gson();
                AnyTimeSpinningCatList anyTimeGameResponse = gson.fromJson(responseData, AnyTimeSpinningCatList.class);
                anyTimeGameContestListArrayList.addAll(anyTimeGameResponse.getContent().getLst());
                Collections.shuffle(anyTimeGameContestListArrayList);
                binding.tvGameName.setText(anyTimeGameContestListArrayList.get(0).getName());

                Log.i("Size", "==>" + anyTimeGameContestListArrayList.size());

                for (int i = 0; i < anyTimeGameContestListArrayList.size(); i++) {
                    fragments.add(new AnyTimeSpinningGameFragment().newInstance(i));
                }
                questionAdapter = new QuestionAdapter(getSupportFragmentManager(), fragments);
                viewPager.setAdapter(questionAdapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                        .getDisplayMetrics());
                viewPager.setPageMargin(pageMargin);
                pagerTabStri.setViewPager(viewPager);
                questionAdapter.notifyDataSetChanged();
                //  rvTicketSelection.showRecycler();
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    @Subscribe
    public void onUpdateUpcomingContestEvent(UpdateUpcomingContestEvent updateUpcomingContestEvent) {
    }

    public class QuestionAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public ArrayList<AnyTimeSpinningGameFragment> mFragments;

        public QuestionAdapter(FragmentManager fm, ArrayList<AnyTimeSpinningGameFragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;

        }

        @Override
        public Fragment getItem(int position) {
            // EvalQuestionFragment fragment = new EvalQuestionFragment();
            return new AnyTimeSpinningGameFragment(anyTimeGameContestListArrayList.get(position), position + 1, AnyTimeSpinningActivity.this);
        }


        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.valueOf(position + 1);
        }


    }

    @Override
    protected void onDestroy() {


        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Log.isLoggable("TAG", Log.VERBOSE)) {
            Log.v("TAG", "Starting and binding service");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();
    }


    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    public void setupSlot() {

        //slot3By5();
    }


    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = Environment.getExternalStorageDirectory() + "/.cbit/";
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(iamgesList.get(new Random().nextInt(iamgesList.size())));
        }

        rv.setLayoutManager(new LinearLayoutManager(context));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }


}