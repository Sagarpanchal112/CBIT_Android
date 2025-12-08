package com.tfb.cbit.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.BricksAdapter;
import com.tfb.cbit.adapter.ViewFliperItemAdapter;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityEditGameBinding;
import com.tfb.cbit.fragments.SpinningItemFragment;
import com.tfb.cbit.models.CategoryImage;
import com.tfb.cbit.models.SpinningImagesModel;
import com.tfb.cbit.models.private_group.PrivateGroupResponse;
import com.tfb.cbit.utility.CountDown;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;
import com.tfb.cbit.views.CustPagerTransformer;
import com.tfb.cbit.views.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.tfb.cbit.activities.AnyTimeGameActivity.CONTEST_MaxRange;
import static com.tfb.cbit.activities.AnyTimeGameActivity.CONTEST_MinRange;

public class EditGameActivity extends AppCompatActivity {
    private String TAG = "EditGameActivity";

    private SessionUtil sessionUtil = null;
    private Context context;
    private List<PrivateGroupResponse.Content> myGroupList = new ArrayList<>();
    public GroupAdapter groupAdapter;

    private ArrayList<Integer> bricksItems = new ArrayList<>();
    private ArrayList<HashMap<String, Integer>> bricksColorModel = new ArrayList<>();
    private BricksAdapter bricksAdapter;
    private Handler handler = new Handler();
    private boolean isHandlerPost = false;
    int minrange, maxrange;
    public String contest_id = "", lock_style = "", game_mode = "", ans_min="1", ans_max="0";
    public int group_id, col=0, raw=0;
    public ArrayList<Slotes> slotesArrayList = new ArrayList<>();
    public Slotes slotes;
    private CountDown remainingTime = null;
    ArrayList<String> iamgesList;

    public PagerSlidingTabStrip pagerTabStri;
    public static ViewPager viewPager;
    //  ExamPagerAdapter examPagerAdapter;
    public QuestionAdapter questionAdapter;
    public ArrayList<SpinningItemFragment> fragments = new ArrayList<>();
    CategoryImage categoryImage;
    private ActivityEditGameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);
        slotes = new Slotes();
        contest_id = getIntent().getStringExtra("contest_id");
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        getGroupList();
        selectCategoryImages();
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        pagerTabStri = (PagerSlidingTabStrip) findViewById(R.id.pager_header);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setPageTransformer(false, new CustPagerTransformer(this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.tvGameName.setText(categoryImage.categoryArrayList.get(position).getName());
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
                binding.tvGameName.setText(categoryImage.categoryArrayList.get(position).getName());
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "You have moved to first game", Toast.LENGTH_LONG).show();
                } else if (categoryImage.categoryArrayList.size() - 1 == position) {
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
        binding. imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Pos = viewPager.getCurrentItem();
                if (Pos < categoryImage.categoryArrayList.size()) {
                    viewPager.setCurrentItem(Pos + 1);
                }
            }
        });
        binding.   spGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    group_id = myGroupList.get(position - 1).getId();

                }
                if (position == 4) {
                    Intent i = new Intent(EditGameActivity.this, CreateGroupActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding. rgGame.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("Classic Grid")) {
                    game_mode = "rdb";
                    binding.  rvBricks.setVisibility(View.VISIBLE);
                    inItBricks(8, 0, 10);
                    binding.  linGameType.setVisibility(View.VISIBLE);
                    //   linBrickes.setVisibility(View.GONE);
                    binding. linSpinning.setVisibility(View.GONE);

                } else if (checkedRadioButton.getText().equals("Spinning Machine")) {
                    game_mode = "spinning-machine";
                    binding. linGameType.setVisibility(View.GONE);
                    binding. rvBricks.setVisibility(View.GONE);
                    //  linBrickes.setVisibility(View.VISIBLE);
                    binding. linSpinning.setVisibility(View.VISIBLE);
                    selectedSlots(11);

                }

            }
        });
        binding. rgLockStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("Basic")) {
                    lock_style = "basic";

                } else if (checkedRadioButton.getText().equals("Paper Chit")) {
                    lock_style = "paper_chit";

                } else if (checkedRadioButton.getText().equals("Catch the Object")) {
                    lock_style = "catch_the_object";
                    openCatchObject();

                }

            }
        });
        binding. rgGameLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("Easy")) {
                    if (game_mode.equals("spinning-machine")) {
                        raw = 3;
                        col = 5;
                    } else {
                        raw = 2;
                        col = 4;
                    }
                } else if (checkedRadioButton.getText().equals("Moderate")) {
                    if (game_mode.equals("spinning-machine")) {
                        raw = 4;
                        col = 5;
                    } else {
                        raw = 4;
                        col = 4;
                    }
                } else if (checkedRadioButton.getText().equals("Pro")) {
                    if (game_mode.equals("spinning-machine")) {
                        raw = 5;
                        col = 5;
                    } else {
                        raw = 6;
                        col = 4;
                    }
                }

            }
        });
        binding. rgGameType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("Colour Colour")) {
                    slotes.setDisplay_Name("Red Win");
                    slotes.setStart_value("-100");
                    slotes.setEnd_value("-1");
                    slotesArrayList.add(slotes);
                    slotes = new Slotes();
                    slotes.setDisplay_Name("Draw");
                    slotes.setStart_value("0");
                    slotes.setEnd_value("0");
                    slotesArrayList.add(slotes);
                    slotes = new Slotes();
                    slotes.setDisplay_Name("Blue Win");
                    slotes.setStart_value("1");
                    slotes.setEnd_value("100");
                    slotesArrayList.add(slotes);
                    binding. linNumericSlot.setVisibility(View.GONE);
                    ans_min = "-100";
                    ans_max = "100";

                } else if (checkedRadioButton.getText().equals("Numeric Slot")) {
                    lock_style = "paper_chit";
                    binding. linNumericSlot.setVisibility(View.VISIBLE);

                }

            }
        });
        binding. rgNoSlot.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                if (checkedRadioButton.getText().equals("2 Slots")) {
                    selectedSlots(2);
                    ans_min = "0";
                    ans_max = "9";
                } else if (checkedRadioButton.getText().equals("3 Slots")) {
                    selectedSlots(3);

                    ans_min = "0";
                    ans_max = "9";
                } else if (checkedRadioButton.getText().equals("5 Slots")) {
                    selectedSlots(5);
                    ans_min = "0";
                    ans_max = "9";

                } else if (checkedRadioButton.getText().equals("10 Slots")) {
                    selectedSlots(10);
                    ans_min = "0";
                    ans_max = "9";

                }
            }
        });
        binding.  rvBricks.setLayoutManager(new GridLayoutManager(context, 4));
        bricksItems.clear();
        bricksColorModel.clear();
        bricksAdapter = new BricksAdapter(context, bricksItems, bricksColorModel);
        binding. rvBricks.setAdapter(bricksAdapter);
        minrange = bundle.getInt(CONTEST_MinRange, 0);
        maxrange = bundle.getInt(CONTEST_MaxRange, 0);
        inItBricks(8, 0, 100);
        handler.removeCallbacks(runnable);
        isHandlerPost = handler.post(runnable);
        binding. btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitApiForEditGame();
            }
        });
        getFromSdcard();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String newdate = sdf.format(date);
        long mill = Utils.convertMillSeconds("2021-02-28 21:40:00", newdate);
        try {
            setupSlot();
            FadinAnimaiton(binding.rvI);
            FadinAnimaiton(binding.rvIV);
            FadinAnimaiton(binding.rvVII);
            FadinAnimaiton(binding.rvX);
            FadinAnimaiton(binding.rvXIII);
        } catch (Exception e) {
            e.printStackTrace();
        }
        remainingTime = new CountDown(mill, 1000) {
            @Override
            public void onTick(final long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            setupSlot();
                            FadinAnimaiton(binding.rvI);
                            FadinAnimaiton(binding.rvIV);
                            FadinAnimaiton(binding.rvVII);
                            FadinAnimaiton(binding.rvX);
                            FadinAnimaiton(binding.rvXIII);
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
    }

    public void getFromSdcard() {
        iamgesList = new ArrayList<String>();// list of file paths
        File[] listFile;

        File file = new File(android.os.Environment.getExternalStorageDirectory(), ".cbit");

        if (file.isDirectory()) {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++) {
                iamgesList.add(listFile[i].getAbsolutePath());
            }
        }
    }

    public void setupSlot() {
        slot3By5();
    }

    public void slot3By5() {
        setUpRecyclr(binding.rvI, 0, 2);
        setUpRecyclr(binding.rvIV, 3, 5);
        setUpRecyclr(binding.rvVII, 6, 8);
        setUpRecyclr(binding.rvX, 9, 11);
        setUpRecyclr(binding.rvXIII, 12, 14);

    }

    private void selectCategoryImages() {
        Call<ResponseBody> call = APIClient.getInstance().selectCategoryImages(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);


                Gson gson = new Gson();
                SpinningImagesModel nm = gson.fromJson(responseData, SpinningImagesModel.class);
                categoryImage = new CategoryImage();

                Log.i("SpinningImagesModel", "==>" + nm.content.size());
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    CategoryImage.Category category = new CategoryImage.Category();
                    for (int j = 0; j < nm.content.size(); j++) {
                        if (categoryImage.categoryArrayList.size() > 0) {
                            int temp = 0;
                            for (int i = 0; i < categoryImage.categoryArrayList.size(); i++) {

                                if (categoryImage.categoryArrayList.get(i).getId() == nm.content.get(j).getCategoryID()) {
                                    //   Log.i("TAG", "ID : " + categoryImage.categoryArrayList.get(i).getId());
                                    categoryImage.categoryArrayList.get(i).spinningImagesModelArrayList.add(nm.content.get(j));
                                    break;
                                } else {
                                    temp = i;
                                    if (temp == categoryImage.categoryArrayList.size() - 1) {
                                        category = new CategoryImage.Category();
                                        category.setId(nm.content.get(j).getCategoryID());
                                        category.setName(nm.content.get(j).getName());
                                        category.spinningImagesModelArrayList.add(nm.content.get(j));
                                        categoryImage.categoryArrayList.add(category);
                                        break;
                                    }
                                }
                            }
                        } else {
                            category.setId(nm.content.get(j).getCategoryID());
                            category.setName(nm.content.get(j).getName());
                            category.spinningImagesModelArrayList.add(nm.content.get(j));
                            categoryImage.categoryArrayList.add(category);
                        }
                    }
                    String jObj = gson.toJson(categoryImage);
                    for (int i = 0; i < categoryImage.categoryArrayList.size(); i++) {
                        fragments.add(new SpinningItemFragment().newInstance(i));
                    }
                    questionAdapter = new QuestionAdapter(getSupportFragmentManager(), fragments);
                    viewPager.setAdapter(questionAdapter);
                    final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                            .getDisplayMetrics());
                    viewPager.setPageMargin(pageMargin);
                    pagerTabStri.setViewPager(viewPager);
                    questionAdapter.notifyDataSetChanged();

                    Log.i("categoryArrayList", "==>" + jObj);
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    public class QuestionAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        public ArrayList<SpinningItemFragment> mFragments;

        public QuestionAdapter(FragmentManager fm, ArrayList<SpinningItemFragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;

        }

        @Override
        public Fragment getItem(int position) {
            return new SpinningItemFragment(categoryImage.categoryArrayList.get(position).spinningImagesModelArrayList, position + 1, EditGameActivity.this);
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

    public void FadinAnimaiton(RecyclerView img) {
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        img.startAnimation(aniFade);
    }

    public void setUpRecyclr(RecyclerView rv, int startPos, int endPos) {
        ArrayList<String> bricksItems = new ArrayList<>();
        // this is dynamic image load from local doenloaded logic
        String SDCardPath = Environment.getExternalStorageDirectory() + "/.cbit/";
        for (int i = startPos; i <= endPos; i++) {
            bricksItems.add(iamgesList.get(new Random().nextInt(iamgesList.size())));
        }

        rv.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        ViewFliperItemAdapter ticketAdapter = new ViewFliperItemAdapter(context, bricksItems);
        rv.setAdapter(ticketAdapter);

    }


    public String request = "";
    public List<Integer> itemArray = new ArrayList<>();

    private void hitApiForEditGame() {
        JSONObject jsonObject = new JSONObject();
        Gson gson = new Gson();
        try {

            JSONArray jsonArray = new JSONArray(gson.toJson(slotesArrayList));
            JSONArray jsonArray1 = new JSONArray();
            byte[] data;
            Log.i("contest_id", "==>" + contest_id);
            jsonObject.put("contest_id", contest_id);
            jsonObject.put("group_id", group_id);
            jsonObject.put("lock_style", lock_style);
            jsonObject.put("cols", "5");
            jsonObject.put("rows", raw);
            jsonObject.put("game_type", game_mode);
            jsonObject.put("ansRangeMin", ans_min);
            jsonObject.put("ansRangeMax", "4");
            jsonObject.put("no_of_items", "4");
            if (CBit.selectedImageArrayList.size() > 0) {
               // jsonObject.put("categoryId", CBit.selectedImageArrayList.get(0).getCategoryId());
                jsonObject.put("categoryId", "3");
                for (int i = 0; i < CBit.selectedImageArrayList.size(); i++) {
                    itemArray.add(CBit.selectedImageArrayList.get(i).getItemId());
                }
                JSONArray jsonArray2 = new JSONArray(gson.toJson(itemArray));

               // jsonObject.put("Items_value", jsonArray2.toString());
                jsonObject.put("Items_value", "[61,62,63]");

            } else {
                jsonObject.put("categoryId", "0");
                jsonObject.put("Items_value", "[]");
            }
            jsonObject.put("slots", jsonArray.toString());
            request = jsonObject.toString();
            Log.i("isLoadMore Request", "==>" + request);
            request = CBit.getCryptLib().encryptPlainTextWithRandomIV(request, getString(R.string.crypt_pass));
            data = request.getBytes("UTF-8");
            request = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Call<ResponseBody> call = APIClient.getInstance()
                .privateGroupJoinContest(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Intent intent = new Intent(context, JoinPrivateRoomActivity.class);
                intent.putExtra("contest_id", contest_id);
                startActivity(intent);
                finish();

                //  jTicktWaitingRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(String responseData) {

            }
        });


    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            inIt();
            handler.postDelayed(runnable, 500);
        }
    };

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    public void selectedSlots(int k) {
        if (k == 2) {
            slotes = new Slotes();
            slotes.setDisplay_Name("0 to 4");
            slotes.setStart_value("0");
            slotes.setEnd_value("4");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("5 to 9");
            slotes.setStart_value("5");
            slotes.setEnd_value("9");
            slotesArrayList.add(slotes);

        }
        if (k == 3) {
            slotes = new Slotes();
            slotes.setDisplay_Name("0 to 3");
            slotes.setStart_value("0");
            slotes.setEnd_value("3");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("4 to 6");
            slotes.setStart_value("4");
            slotes.setEnd_value("6");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("7 to 9");
            slotes.setStart_value("7");
            slotes.setEnd_value("9");
            slotesArrayList.add(slotes);

        }
        if (k == 5) {
            slotes = new Slotes();
            slotes.setDisplay_Name("0 to 1");
            slotes.setStart_value("0");
            slotes.setEnd_value("1");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("2 to 3");
            slotes.setStart_value("2");
            slotes.setEnd_value("3");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("4 to 5");
            slotes.setStart_value("4");
            slotes.setEnd_value("5");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("6 to 7");
            slotes.setStart_value("6");
            slotes.setEnd_value("7");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("8 to 9");
            slotes.setStart_value("8");
            slotes.setEnd_value("9");
            slotesArrayList.add(slotes);

        }
        if (k == 10) {
            slotes = new Slotes();
            slotes.setDisplay_Name("0");
            slotes.setStart_value("0");
            slotes.setEnd_value("0");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("1");
            slotes.setStart_value("1");
            slotes.setEnd_value("1");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("2");
            slotes.setStart_value("2");
            slotes.setEnd_value("2");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("3");
            slotes.setStart_value("3");
            slotes.setEnd_value("3");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("4");
            slotes.setStart_value("4");
            slotes.setEnd_value("4");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("5");
            slotes.setStart_value("5");
            slotes.setEnd_value("5");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("6");
            slotes.setStart_value("6");
            slotes.setEnd_value("6");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("7");
            slotes.setStart_value("7");
            slotes.setEnd_value("7");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("8");
            slotes.setStart_value("8");
            slotes.setEnd_value("8");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("9");
            slotes.setStart_value("9");
            slotes.setEnd_value("9");
            slotesArrayList.add(slotes);

        }
        if(k==11){
            slotes = new Slotes();
            slotes.setDisplay_Name("Australia");
            slotes.setStart_value("61");
            slotes.setEnd_value("61");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("India");
            slotes.setStart_value("62");
            slotes.setEnd_value("62");
            slotesArrayList.add(slotes);
            slotes = new Slotes();
            slotes.setDisplay_Name("New Zealand");
            slotes.setStart_value("63");
            slotes.setEnd_value("63");
            slotesArrayList.add(slotes);

        }

    }

    public class Slotes {
        public String Display_Name = "";
        public String start_value = "";
        public String end_value = "";

        public String getDisplay_Name() {
            return Display_Name;
        }

        public void setDisplay_Name(String display_Name) {
            Display_Name = display_Name;
        }

        public String getStart_value() {
            return start_value;
        }

        public void setStart_value(String start_value) {
            this.start_value = start_value;
        }

        public String getEnd_value() {
            return end_value;
        }

        public void setEnd_value(String end_value) {
            this.end_value = end_value;
        }
    }

    private void inIt() {
        for (int i = 0; i < 4; i++) {
            HashMap<String, Integer> temp = bricksColorModel.get(bricksColorModel.size() - 1);
            bricksColorModel.remove(bricksColorModel.size() - 1);
            bricksColorModel.add(0, temp);
        }
        Collections.shuffle(bricksItems);
        bricksAdapter.notifyDataSetChanged();
    }

    public static int rand(int min, int max) {
        if (min > max || (max - min + 1 > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("Invalid range");
        }

        return new Random().nextInt(max - min + 1) + min;
    }

    private void inItBricks(int totalItem, int min, int max) {

        HashMap<String, Integer> map = new HashMap<>();
        if (totalItem == 8) {
            while (bricksItems.size() < 8) {
                bricksItems.add(Math.abs(rand(min, max)));
            }
            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

        } else if (totalItem == 16) {

            while (bricksItems.size() < 16) {
                bricksItems.add(Math.abs(rand(min, max)));
            }

            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 8);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 9);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 10);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 11);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 12);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 13);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 14);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 15);
            bricksColorModel.add(map);
        } else {

            while (bricksItems.size() < 32) {
                bricksItems.add(Math.abs(rand(min, max)));
            }


            map.put("color", R.color.color_green);
            map.put("index", 0);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 1);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 2);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 3);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 4);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 5);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 6);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 7);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 8);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 9);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 10);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 11);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 12);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 13);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 14);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 15);
            bricksColorModel.add(map);

            map.put("color", R.color.color_green);
            map.put("index", 16);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 17);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 18);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 19);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 20);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 21);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 22);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 23);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 24);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 25);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 26);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 27);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_red);
            map.put("index", 28);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 29);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_blue);
            map.put("index", 30);
            bricksColorModel.add(map);

            map = new HashMap<>();
            map.put("color", R.color.color_green);
            map.put("index", 31);
            bricksColorModel.add(map);

        }


        Collections.shuffle(bricksItems);

    }

    private void getGroupList() {
        Call<ResponseBody> call = APIClient
                .getInstance().allUsersPrivateGroup(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
                Gson gson = new Gson();
                PrivateGroupResponse nm = gson.fromJson(responseData, PrivateGroupResponse.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    myGroupList.clear();
                    myGroupList.addAll(nm.getContent());
                }
                groupAdapter = new GroupAdapter(EditGameActivity.this, myGroupList);
                binding. spGroup.setAdapter(groupAdapter);

            }

            @Override
            public void failure(String responseData) {
                Log.i(TAG, "responseData :-> " + responseData);
            }
        });
    }

    class GroupAdapter extends BaseAdapter {
        List<PrivateGroupResponse.Content> stateList;
        LayoutInflater inflter;
        Context context;

        public GroupAdapter(Context context, List<PrivateGroupResponse.Content> stateLists) {
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
                names.setText("Select Group");
                names.setTextColor(Color.GRAY);
            } else {
                PrivateGroupResponse.Content categoryModel = stateList.get(position - 1);
                names.setText(categoryModel.getPrivate_group_name());
                names.setTextColor(Color.BLACK);
            }
            return convertView;
        }


    }

    private void openCatchObject() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_catch_the_object);

        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            lp.width = (int) (metrics.widthPixels * 0.90);
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
        ViewPager mViewPager = dialog.findViewById(R.id.vp_image_pick);
        mViewPager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        mViewPager.setAdapter(new ImageBrowserAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dialog.show();
    }

    public int[] imageArray = new int[]{R.drawable.mouce, R.drawable.scooter, R.drawable.scooter, R.drawable.star};

    private class ImageBrowserAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView view = new PhotoView(EditGameActivity.this);
           // view.enable();
            view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            try {
                // Picasso.with(context).load(categoryDetails.getCategoryImage()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.img_categories);
                RequestOptions ro = new RequestOptions();
                Glide.with(EditGameActivity.this)
                        .applyDefaultRequestOptions(ro)
                        .load(imageArray[position])
                        .transition(withCrossFade())
                        .into(view);
                container.addView(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageArray.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}