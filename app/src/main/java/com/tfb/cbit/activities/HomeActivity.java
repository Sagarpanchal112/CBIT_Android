package com.tfb.cbit.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.OnDownloadListener;
import com.google.gson.Gson;
import com.judemanutd.autostarter.AutoStartPermissionHelper;
import com.tfb.cbit.CBit;
import com.tfb.cbit.R;
import com.tfb.cbit.api.APIClient;
import com.tfb.cbit.api.ApiCallback;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityHomeBinding;
import com.tfb.cbit.event.SocketConnectionEvent;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.event.UpdateProfileEvent;
import com.tfb.cbit.event.UpdateVersionEvent;
import com.tfb.cbit.fragments.AutoRenewFragment;
import com.tfb.cbit.fragments.DashBoardFragment;
import com.tfb.cbit.fragments.EasyJoinFragment;
import com.tfb.cbit.fragments.JTicketAutomationFragment;
import com.tfb.cbit.fragments.JTicketWaitingRoomFragment;
import com.tfb.cbit.fragments.JoinByCodeFragment;
import com.tfb.cbit.fragments.LiveHistoryTabFragment;
import com.tfb.cbit.fragments.MyContestPkgFragment;
import com.tfb.cbit.fragments.MyJTicketFragment;
import com.tfb.cbit.fragments.PackegesFragment;
import com.tfb.cbit.fragments.RedeemJTicketFragment;
import com.tfb.cbit.fragments.SettingsFragment;
import com.tfb.cbit.fragments.WalletFragment;
import com.tfb.cbit.models.CommonRes;
import com.tfb.cbit.models.PopupDataModel;
import com.tfb.cbit.models.UserJoinDateTimeModel;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.permission.PeermissionActivity;
import com.tfb.cbit.services.TimerService;
import com.tfb.cbit.utility.CustomDialog;
import com.tfb.cbit.utility.LogHelper;
import com.tfb.cbit.utility.PrintLog;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;
//
//import com.thin.downloadmanager.DefaultRetryPolicy;
//import com.thin.downloadmanager.DownloadRequest;
//import com.thin.downloadmanager.DownloadStatusListenerV1;
//import com.thin.downloadmanager.RetryPolicy;
//import com.thin.downloadmanager.ThinDownloadManager;

import net.mftd313.updatelibrary.UpdateLibrary;
import net.mftd313.updatelibrary.listeners.UpdateDownloadStartedListener;
import net.mftd313.updatelibrary.listeners.UpdateInstallStartedListener;
import net.mftd313.updatelibrary.listeners.UpdateReadyToDownloadListener;
import net.mftd313.updatelibrary.listeners.UpdateReadyToInstallListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.ResponseBody;
import retrofit2.Call;

import com.downloader.PRDownloader;

import com.downloader.PRDownloaderConfig;

public class HomeActivity extends BaseAppCompactActivity {
    private Context context;
    private PopupWindow popupWindow;
    private SessionUtil sessionUtil;
    private static final String TAG = HomeActivity.class.getSimpleName();
    CustomDialog customDialog;
    private boolean isHome = false;
    private static final String VIDEO_SAMPLE =
            "https://www.cbitoriginal.com/howtoplayvideo.MP4";
    private int mCurrentPosition = 0;
    // public static ThinDownloadManager downloadManager;
    public static int totalDownloads = 0, completedDownload = 0;
    /* public MyDownloadDownloadStatusListenerV1
             myDownloadStatusListener = new MyDownloadDownloadStatusListenerV1();
     public RetryPolicy retryPolicy;*/
    public static LinkedHashMap<String, String> mapImages = new LinkedHashMap<>();
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 4;
    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;
    public String apdValue = "", efmValue = "", emValue = "", bapValue = "", dayOfJoin = "";


    public TimerService timerService;
    public boolean serviceBound;
    private ProgressDialog progressDialog;
    ProgressDialog mProgressDialog;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.header.toolbar);
        context = this;
        sessionUtil = new SessionUtil(context);
        //  retryPolicy = new DefaultRetryPolicy();
        //  downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE);
      /*  if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }*/
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        mProgressDialog = new ProgressDialog(HomeActivity.this);
        mProgressDialog.setMessage("Downloading Game Assets");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);


        if (!CBit.ImageDownload.equals("")) {
            openPopup(CBit.ImageDownload);
        }
        CBit.getSocketUtils().connect();
        getSpinningMachineitemByDate();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        inIt();
        if (!sessionUtil.getAutoStart()) {
            sessionUtil.setAutoStart(true);
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this);
        }
        getUserJTicket();
        getUserInfo();
        getPopUpNotification();
        binding.tvVersion.setText("Version :" + Utils.getVersionName(context));
        binding.tvHelpCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PackageManager packageManager = context.getPackageManager();
                String number = "918591497179";
                try {
                    Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + "" + number + "?body=" + ""));
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.sideMenu.btnAPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApdPopup();
            }
        });

        binding.sideMenu.btnEFM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEFMPopup();
            }
        });
        binding.sideMenu.btnEM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEMPopup();
            }
        });
        binding.sideMenu.btnBAP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBAPPopup();
            }
        });
        binding.tvEasyJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.header.ivInfo.setVisibility(View.GONE);
                binding.header.linWallet.setVisibility(View.GONE);
                isHome = false;
                binding.drawer.closeDrawer(GravityCompat.START);
                if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearSubMenu.setVisibility(View.GONE);
                    binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearJSubMenu.setVisibility(View.GONE);
                    binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearAboutSubMenu.setVisibility(View.GONE);
                    binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (!binding.tvShareApp.isSelected()) {
                    menuSelectUnSelect(11);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.header.toolbarTitle.setText("Easy Join");
                            // ivSorting.setVisibility(View.GONE);
                            // ivFilter.setVisibility(View.GONE);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameContent, EasyJoinFragment.newInstance())
                                    .commit();
                        }
                    }, 200);
                }
            }  /* Intent intent = new Intent(context, EasyJoinActivity.class);
                startActivity(intent);*/

        });
        binding.tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserReportsActivity.class);
                startActivity(intent);

            }
        });
        binding.tvAutoRenew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.header.ivInfo.setVisibility(View.GONE);
                binding.header.linWallet.setVisibility(View.GONE);
                isHome = false;
                binding.drawer.closeDrawer(GravityCompat.START);
                if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearSubMenu.setVisibility(View.GONE);
                    binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearJSubMenu.setVisibility(View.GONE);
                    binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                    binding.linearAboutSubMenu.setVisibility(View.GONE);
                    binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
                }
                if (!binding.tvChangePass.isSelected()) {
                    menuSelectUnSelect(8);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.header.toolbarTitle.setText(getString(R.string.auto_renew));
                            // ivSorting.setVisibility(View.GONE);
                            // ivFilter.setVisibility(View.GONE);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameContent, AutoRenewFragment.newInstance())
                                    .commit();
                        }
                    }, 200);
                }
            }
        });
        Intent i = new Intent(this, TimerService.class);
        startService(i);
        bindService(i, mConnection, 0);

        binding.header.ivMenu.setOnClickListener(view -> {
            binding.drawer.openDrawer(GravityCompat.START);
        });
        binding.header.ivInfo.setOnClickListener(view -> {
            openInfoPopup();
        });
        binding.header.ivSorting.setOnClickListener(view -> {
            sortingPopup();
        });
        binding.header.ivFilter.setOnClickListener(view -> {
            openFilterDialog();
        });
        binding.sideMenu.ivProfilePic.setOnClickListener(view -> {
            ivProfilePicClick();
        });
        binding.tvHostContest.setOnClickListener(view -> {
            tvHostContestClick();
        });
        binding.tvJoinGroup.setOnClickListener(view -> {
            tvJoinGroupClick();
        });
        binding.tvMyGroup.setOnClickListener(view -> {
            tvMyGroupClick();
        });
        binding.tvCreateGroup.setOnClickListener(view -> {
            tvCreateGroupClick();
        });
        binding.tvRedeemJtckt.setOnClickListener(view -> {
            tvRedeemJtcktClick();
        });
        binding.tvMyJtckt.setOnClickListener(view -> {
            tvMyJtcktClick();
        });
        binding.tvJWaitingRoom.setOnClickListener(view -> {
            tvJWaitingRoomClick();
        });
        binding.tvJAutomation.setOnClickListener(view -> {
            tvJAutomation();
        });
        binding.tvJTC.setOnClickListener(view -> {
            tvJTCClick();
        });
        binding.tvHoroscope.setOnClickListener(view -> {
            tvHoroscopeClick();
        });
        binding.tvJIds.setOnClickListener(view -> {
            tvJIdsClick();
        });
        binding.tvPermission.setOnClickListener(view -> {
            tvPermissionClick();
        });
        binding.tvPackages.setOnClickListener(view -> {
            tvPackagesClick();
        });
        binding.tvMyContestPkg.setOnClickListener(view -> {
            tvMyContestPkgClick();
        });
        binding.tvJoinByCode.setOnClickListener(view -> {
            tvJoinByCodeClick();
        });
        binding.tvAboutUsSub.setOnClickListener(view -> {
            tvAboutUsSubClick();
        });
        binding.tvTermsCond.setOnClickListener(view -> {
            tvTermsCondClick();
        });
        binding.tvPrivacyPolicy.setOnClickListener(view -> {
            tvPrivacyPolicyClick();
        });
        binding.tvlegality.setOnClickListener(view -> {
            tvlegalityClick();
        });
        binding.btnLogout.setOnClickListener(view -> {
            btnLogoutClick();
        });
        onMenuClick();

    }

    protected void btnLogoutClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logOut();
            }
        }, 300);

    }

    protected void tvlegalityClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.legality));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.legality_link));
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvJIdsClick() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, IdsActivity.class);
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvPermissionClick() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, PeermissionActivity.class);
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvPackagesClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.packages));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, PackegesFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvMyContestPkgClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.mycontestandpkg));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, MyContestPkgFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvJoinByCodeClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.joinbycode));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, JoinByCodeFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvAboutUsSubClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.aboutus));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.about_link));
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvTermsCondClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.termscond));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.terms_link));
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvPrivacyPolicyClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, AboutUsActivity.class);
                intent.putExtra(AboutUsActivity.NAME, getString(R.string.privacypolicy));
                intent.putExtra(AboutUsActivity.LINK, getString(R.string.privacy_link));
                startActivity(intent);
            }
        }, 300);
    }

    protected void ivProfilePicClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, ProfileActivity.class));
            }
        }, 200);

    }

    protected void tvHostContestClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, HostGameActivity.class));
            }
        }, 300);
    }

    protected void tvJoinGroupClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, MyJoinGroupActivity.class));
            }
        }, 300);
    }

    protected void tvMyGroupClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        startActivity(new Intent(context, MyPrivateGroupActivity.class));

    }

    protected void tvCreateGroupClick() {
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, CreateGroupActivity.class));
            }
        }, 300);
    }

    protected void tvRedeemJtcktClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.Redeemjtckt));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, RedeemJTicketFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvMyJtcktClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, MyJTicketFragment.class);
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvJWaitingRoomClick() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.WaitingRoom));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, JTicketWaitingRoomFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvJAutomation() {
        binding.header.ivInfo.setVisibility(View.GONE);
        binding.header.linWallet.setVisibility(View.GONE);
        isHome = false;
        binding.drawer.closeDrawer(GravityCompat.START);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.header.toolbarTitle.setText(getString(R.string.Automation));
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameContent, JTicketAutomationFragment.newInstance())
                        .commit();
            }
        }, 300);
    }

    protected void tvJTCClick() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, TermsAndConditionActivity.class);
                intent.putExtra(TermsAndConditionActivity.NAME, getString(R.string.tc));
                intent.putExtra(TermsAndConditionActivity.LINK, getString(R.string.termscondition));
                startActivity(intent);
            }
        }, 300);
    }

    protected void tvHoroscopeClick() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context, LuckyGameActivity.class);
                startActivity(intent);
            }
        }, 300);
    }

    private void getPopUpNotification() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("plateform", "Android");
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
        Call<ResponseBody> call = APIClient.getInstance().getPopUpNotification(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("PopUp sucess :", responseData);
                Gson gson = new Gson();
                PopupDataModel nm = gson.fromJson(responseData, PopupDataModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    if (nm.getContent().getUserData() == 0) {
                        for (int i = 0; i < nm.getContent().getPopUpData().size(); i++) {
                            openNotiocationPopup(nm.getContent().getPopUpData().get(i));
                        }
                    }

                }

            }

            @Override
            public void failure(String responseData) {

                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void openNotiocationPopup(PopupDataModel.PopUpData popUpData) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dailog_popup_notification);

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
        TextView tv_content = dialog.findViewById(R.id.tv_content);
        tv_content.setText(popUpData.getContent());
        Button btnOk = dialog.findViewById(R.id.btnOk);

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        if (popUpData.getIs_cancel() == 1) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setPopUpNotification();
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                File file = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/files/cbitoriginal.apk");
                LogHelper.e("TAG", "Path : " + file.getAbsolutePath());
                if (file.exists()) {
                    file.delete();
                    LogHelper.e("TAG", " IF Path : Delete");
                }
                if (CBit.getSocketUtils().getmSocket().connected()) {
                    CBit.getSocketUtils().disConnect();
                }
                sessionUtil.setAutoStart(false);
                String url = popUpData.getLink();
                Log.i("Url", "==>" + url);
                //  DownloadApk downloadApk = new DownloadApk(context);
                //  downloadApk.startDownloadingApk(url);
                UpdateLibrary.with(HomeActivity.this)
                        .setDownloadingNotificationTitle(getString(R.string.app_name))
                        .setDownloadingNotificationText(getString(R.string.downloading_new_version))
                        .setDownloadedNotificationTitle(getString(R.string.app_name))
                        .setDownloadedNotificationText(getString(R.string.download_completed))
                        .setDownloadedNotificationSmallIconResource(R.mipmap.app_green_icon)
                        .setDownloadedNotificationLargeIconResource(R.mipmap.app_green_icon)

                        .setUpdateReadyToDownloadListener(new UpdateReadyToDownloadListener() {
                            @Override
                            public void onReadyToDownload(final Context context, Uri uri) {
                                progressDialog.hide();

                                UpdateLibrary.getUpdateManager().download(context);

                            }
                        })

                        .setUpdateDownloadStartedListener(new UpdateDownloadStartedListener() {
                            @Override
                            public void onDownloadStarted(Context context, Uri uri) {
                                progressDialog.setMessage(getString(R.string.downloading_new_version));
                                progressDialog.show();
                            }
                        })
                        .setUpdateReadyToInstallListener(new UpdateReadyToInstallListener() {
                            @Override
                            public void onReadyToInstall(final Context context, Uri uri) {
                                progressDialog.hide();
                                UpdateLibrary.getUpdateManager().install(context);

                            }
                        })
                        .setUpdateInstallStartedListener(new UpdateInstallStartedListener() {
                            @Override
                            public void onInstallStarted(Context context, Uri uri) {
                                progressDialog.setMessage(getString(R.string.installing_new_version));
                                //progressDialog.show();
                            }
                        })
                        .init(Uri.parse(url));
            }
        });
        dialog.show();
    }

    private void openPopup(String imageData) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.overlay_loading);

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
        ImageView img_close = dialog.findViewById(R.id.img_close);
        ImageView imageDialog = dialog.findViewById(R.id.imageDialog);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.loading_logo)
                .error(R.drawable.loading_logo)
                .dontAnimate()
                .dontTransform();
        Glide.with(context).load(imageData).apply(options).into(imageDialog);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CBit.ImageDownload = "";
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setPopUpNotification() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("status", "1");
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
        Call<ResponseBody> call = APIClient.getInstance().setPopUpNotification(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);


            }

            @Override
            public void failure(String responseData) {

                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.v("TAG", "Service bound");
            TimerService.RunServiceBinder binder = (TimerService.RunServiceBinder) service;
            timerService = binder.getService();
            serviceBound = true;
            getUserJoinDateTime();
            if (timerService.isTimerRunning()) {

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (Log.isLoggable("TAG", Log.VERBOSE)) {
                Log.v("TAG", "Service disconnect");
            }
            serviceBound = false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStop() {
        super.onStop();

        if (serviceBound) {
            // If a timer is active, foreground the service, otherwise kill the service
            if (timerService.isTimerRunning()) {
                timerService.foreground();

            } else {
                stopService(new Intent(this, TimerService.class));
            }
            // Unbind the service
            unbindService(mConnection);
            serviceBound = false;
        }
    }

    private void getUserJoinDateTime() {
        Call<ResponseBody> call = APIClient.getInstance().getUserJoinDateTime(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {

                LogHelper.d(TAG, "success: " + responseData);
                Gson gson = new Gson();
                UserJoinDateTimeModel nm = gson.fromJson(responseData, UserJoinDateTimeModel.class);
                if (nm.getStatusCode() == Utils.StandardStatusCodes.SUCCESS) {
                    String contestDate = "", servertime = "";
                    for (int i = 0; i < nm.getContent().getContest().size(); i++) {
                        // if (i == 0) {
                        contestDate = nm.getContent().getContest().get(i).getStartDate();
                        UpcomingContestModel upcomingContestModel = new UpcomingContestModel(contestDate, servertime, 1, nm.getContent().getContest().get(i).getId(), nm.getContent().getContest().get(i).getName(), nm.getContent().getContest().get(i).getGame_type());
                        DatabaseHandler databaseHandler = new DatabaseHandler(context);
                        upcomingContestModel.setId(databaseHandler.addContest(upcomingContestModel));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Calendar calendar = Calendar.getInstance();
                        try {
                            calendar.setTime(sdf.parse(upcomingContestModel.getContestDateTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        startAlert(upcomingContestModel);
                        //   }
                    }
                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    public void startAlert(UpcomingContestModel ucm) {
        long mill = 0;
        mill = Utils.convertMillSecondsReminder(ucm.getContestDateTime());
        Log.i("Alarm", "==>startTimer alarm");
        Log.i("Alarm", "==>" + ucm.getContestID() + "," + ucm.getContestName() + "," + ucm.getContestType());

        long currentTime = System.currentTimeMillis();
        if (mill > currentTime)
            timerService.startTimer(this, ucm.getContestID(), ucm.getContestName(), ucm.getContestType(), mill);
    }

    public String request = "";

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void openApdPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_apd);

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
        TextView tvApd = dialog.findViewById(R.id.tvApd);
        TextView tv_detail = dialog.findViewById(R.id.tv_detail);
        if (dayOfJoin.equals("2") || dayOfJoin.equals("3")) {
            tv_detail.setText("Your APD cycle refreshes on " + dayOfJoin + "nd of every month .");

        } else {
            tv_detail.setText("Your APD cycle refreshes on " + dayOfJoin + "th of every month .");

        }
        tvApd.setText(apdValue);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openEFMPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_efm);

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
        TextView tvEfm = dialog.findViewById(R.id.tvEfm);
        tvEfm.setText(efmValue);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openEMPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_em);

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
        TextView tvEm = dialog.findViewById(R.id.tvEm);
        tvEm.setText(emValue);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openBAPPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_bap);

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
        TextView tvBap = dialog.findViewById(R.id.tvBap);
        TextView tv_detail = dialog.findViewById(R.id.tv_detail);
        tvBap.setText("" + bapValue+" Points");

        tv_detail.setText("You can apply J tickets worth " + bapValue + " points until midnight . Increase your APD to extend the BAP limit .");

        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getUserJTicket() {
        Call<ResponseBody> call = APIClient.getInstance().getAllJticketDatas(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);

                Gson gson = new Gson();
                try {
                    JSONObject jObj = new JSONObject(responseData);
                    if (jObj.getInt("statusCode") == Utils.StandardStatusCodes.SUCCESS) {
                        JSONObject jcontent = jObj.getJSONObject("content");
                     /*   try {
                            btnAPD.setText(numDifferentiation(Float.parseFloat(new DecimalFormat("##.##").format(jcontent.getString("ADP")))) + "");
                            btnBAP.setText("CC  " + numDifferentiation(Float.parseFloat(new DecimalFormat("##.##").format(jcontent.getString("BAP")))) + "");
                            btnEFM.setText(numDifferentiation(Float.parseFloat(new DecimalFormat("##.##").format(jcontent.getString("TotalEntry")))) + "");
                            btnEM.setText(numDifferentiation(Float.parseFloat(new DecimalFormat("##.##").format(jcontent.getString("TotalEarning")))) + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*/

                        Glide.with(context).load(jcontent.getString("referral_image")).apply(Utils.getUserAvatarReques()).into(binding.sideMenu.ivProfilePic);

                        apdValue = Utils.getCurrencyFormat(jcontent.getString("ADP"));
                        bapValue = jcontent.getString("BAP");
                        dayOfJoin = String.valueOf(jcontent.getInt("DayOfJoin"));
                        efmValue = Utils.getCurrencyFormat(jcontent.getDouble("TotalEntry") + "");
                        emValue = Utils.getCurrencyFormat(jcontent.getDouble("TotalEarning") + "");
                        binding.sideMenu.btnAPD.setText(Utils.getCurrencyFormat(jcontent.getString("ADP")));
                        binding.sideMenu.btnBAP.setText(jcontent.getString("BAP")+" Points");
                        binding.sideMenu.btnEFM.setText(Utils.getCurrencyFormat(jcontent.getDouble("TotalEntry") + ""));
                        binding.sideMenu.btnEM.setText(Utils.getCurrencyFormat(jcontent.getDouble("TotalEarning") + ""));

                    } else {
                        binding.sideMenu.btnAPD.setTag("0.0");
                        binding.sideMenu.btnBAP.setTag("0.0");
                        binding.sideMenu.btnEFM.setTag("0.0");
                        binding.sideMenu.btnEM.setTag("0.0");
                    }
                } catch (JSONException e) {
                    binding.sideMenu.btnAPD.setTag("0.0");
                    binding.sideMenu.btnBAP.setTag("0.0");
                    binding.sideMenu.btnEFM.setTag("0.0");
                    binding.sideMenu.btnEM.setTag("0.0");
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String responseData) {
                binding.sideMenu.btnAPD.setTag("0.0");
                binding.sideMenu.btnBAP.setTag("0.0");
                binding.sideMenu.btnEFM.setTag("0.0");
                binding.sideMenu.btnEM.setTag("0.0");
                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    private void getUserInfo() {
        JSONObject jsonObject = new JSONObject();
        byte[] data;
        try {
            jsonObject.put("plateform", "Android");
            jsonObject.put("version", Utils.getVersionName(context));
            jsonObject.put("device", android.os.Build.MODEL);
            jsonObject.put("device_version", Build.VERSION.RELEASE);
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
        Call<ResponseBody> call = APIClient.getInstance().getUserInfo(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                //  longLog("sucess :",responseData);
                Log.i("sucess :", responseData);


            }

            @Override
            public void failure(String responseData) {

                Log.d(TAG, "success: " + responseData);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionUtil.isLogin()) {
           /* if (!CBit.getSocketUtils().getmSocket().connected()) {
                customDialog = new CustomDialog();
                customDialog.displayProgress(context, getString(R.string.loading));
            }*/
            try {
                double amount = Double.parseDouble(sessionUtil.getAmount());
                double wAmount = Double.parseDouble(sessionUtil.getWAmount());
                binding.header.tvWallet.setText(Utils.getCurrencyFormat(String.valueOf((amount + wAmount))));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(context, LoginSignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void inIt() {
        binding.sideMenu.tvUsername.setText(sessionUtil.getUserName());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("screen", "").equals(getString(R.string.wallet))) {
                binding.header.toolbarTitle.setText(getString(R.string.wallet));
                binding.tvWallet.setSelected(true);
                binding.header.ivInfo.setVisibility(View.GONE);
                binding.header.linWallet.setVisibility(View.GONE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frameContent, WalletFragment.newInstance())
                        .commit();
                isHome = false;
            } else {
                binding.header.toolbarTitle.setText(getString(R.string.dashboard));
                binding.tvDashboard.setSelected(true);
                binding.header.ivInfo.setVisibility(View.VISIBLE);
                binding.header.linWallet.setVisibility(View.VISIBLE);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frameContent, DashBoardFragment.newInstance())
                        .commit();
                isHome = true;
            }
        } else {
            binding.header.ivInfo.setVisibility(View.VISIBLE);
            binding.header.linWallet.setVisibility(View.VISIBLE);
            binding.header.toolbarTitle.setText(getString(R.string.dashboard));
            binding.tvDashboard.setSelected(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameContent, DashBoardFragment.newInstance())
                    .commit();
            isHome = true;
        }

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate the custom layout/view
        View customView = layoutInflater.inflate(R.layout.dialog_sorting, null);
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

    }


    private void openInfoPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_info);

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
        final VideoView mVideoView;
        final ProgressBar prog_buffering;
        mVideoView = dialog.findViewById(R.id.videoview);
        prog_buffering = dialog.findViewById(R.id.prog_buffering);
        prog_buffering.setVisibility(VideoView.VISIBLE);
        Uri videoUri = getMedia(VIDEO_SAMPLE);
        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        // Hide buffering message.
                        prog_buffering.setVisibility(VideoView.INVISIBLE);

                        // Restore saved position, if available.
                        if (mCurrentPosition > 0) {
                            mVideoView.seekTo(mCurrentPosition);
                        } else {
                            // Skipping to 1 shows the first frame of the video.
                            mVideoView.seekTo(1);
                        }

                        // Start playing!
                        mVideoView.start();
                    }
                });
        mVideoView.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        // Return the video position to the start.
                        mVideoView.seekTo(0);
                        mediaPlayer.stop();
                    }
                });


        dialog.show();
    }

    private Uri getMedia(String mediaName) {
        if (URLUtil.isValidUrl(mediaName)) {
            // Media name is an external URL.
            return Uri.parse(mediaName);
        } else {
            // you can also put a video file in raw package and get file from there as shown below
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START);
        } else if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else if (!isHome) {
            isHome = true;
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            binding.header.ivInfo.setVisibility(View.VISIBLE);
            binding.header.linWallet.setVisibility(View.VISIBLE);
            menuSelectUnSelect(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.header.toolbarTitle.setText(getString(R.string.dashboard));
                    // ivSorting.setVisibility(View.VISIBLE);
                    // ivFilter.setVisibility(View.VISIBLE);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameContent, DashBoardFragment.newInstance())
                            .commit();
                }
            }, 100);
        } else {
            super.onBackPressed();
        }
    }


    protected void onMenuClick() {
        binding.tvDashboard.setOnClickListener(view -> {
            binding.header.ivInfo.setVisibility(View.VISIBLE);
            binding.header.linWallet.setVisibility(View.VISIBLE);
            isHome = true;
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (!binding.tvDashboard.isSelected()) {
                menuSelectUnSelect(0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.header.toolbarTitle.setText(getString(R.string.dashboard));
                        // ivSorting.setVisibility(View.VISIBLE);
                        // ivFilter.setVisibility(View.VISIBLE);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameContent, DashBoardFragment.newInstance())
                                .commit();
                    }
                }, 200);
            }
        });

        binding.tvOrganize.setOnClickListener(view -> {
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            if (binding.linearSubMenu.getVisibility() == View.GONE) {
                menuSelectUnSelect(1);
                Utils.expand(binding.linearSubMenu);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_colorprimary_down, 0);
            } else {
                menuSelectUnSelect(-1);
                Utils.collapse(binding.linearSubMenu);
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
        });
        binding.tvJTckt.setOnClickListener(view -> {
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.GONE) {
                menuSelectUnSelect(2);
                Utils.expand(binding.linearJSubMenu);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_colorprimary_down, 0);
            } else {
                menuSelectUnSelect(-1);
                Utils.collapse(binding.linearJSubMenu);
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
        });
        binding.tvAboutUs.setOnClickListener(view -> {
            //isHome = false;
            // drawer.closeDrawer(GravityCompat.START);
                /*if(!binding.tvAboutUs.isSelected()) {
                    menuSelectUnSelect(7);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toolbar_title.setText(getString(R.string.aboutus));
                            // ivSorting.setVisibility(View.GONE);
                            // ivFilter.setVisibility(View.GONE);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameContent, AboutFragment.newInstance())
                                    .commit();
                        }
                    }, 200);
                }*/
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.GONE) {
                menuSelectUnSelect(7);
                Utils.expand(binding.linearAboutSubMenu);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_colorprimary_down, 0);
            } else {
                menuSelectUnSelect(-1);
                Utils.collapse(binding.linearAboutSubMenu);
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
        });

        binding.tvWallet.setOnClickListener(view -> {
            binding.header.ivInfo.setVisibility(View.GONE);
            binding.header.linWallet.setVisibility(View.GONE);
            isHome = false;
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (!binding.tvWallet.isSelected()) {
                menuSelectUnSelect(3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.header.toolbarTitle.setText(getString(R.string.wallet));
                        // ivSorting.setVisibility(View.GONE);
                        // ivFilter.setVisibility(View.GONE);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameContent, WalletFragment.newInstance())
                                .commit();
                    }
                }, 200);
            }
        });

        binding.tvNotification.setOnClickListener(view -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (!binding.tvNotification.isSelected()) {
                //   menuSelectUnSelect(4);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(context, NotificationActivity.class));
                    }
                }, 200);
            }
        });

        binding.tvHistory.setOnClickListener(view -> {
            binding.header.ivInfo.setVisibility(View.GONE);
            binding.header.linWallet.setVisibility(View.GONE);
            isHome = false;
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (!binding.tvHistory.isSelected()) {
                menuSelectUnSelect(5);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // ivSorting.setVisibility(View.VISIBLE);
                        // ivFilter.setVisibility(View.VISIBLE);
                        Intent i = new Intent(HomeActivity.this, LiveHistoryTabFragment.class);
                        i.putExtra("game_type", "rdb");
                        startActivity(i);
                    }
                }, 200);
            }

        });
        binding.tvHowToPlay.setOnClickListener(view -> {
            isHome = false;
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            if (!binding.tvHowToPlay.isSelected()) {
//                    menuSelectUnSelect(6);
                new Handler().postDelayed(() -> startActivity(new Intent(context, TutorialActivity.class)), 200);

                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toolbar_title.setText(getString(R.string.howtoplay1));
                           // ivSorting.setVisibility(View.GONE);
                           // ivFilter.setVisibility(View.GONE);
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frameContent, HowtoPlayFragment.newInstance())
                                    .commit();
                        }
                    },200);*/
            }

        });
        binding.tvChangePass.setOnClickListener(view -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            if (!binding.tvChangePass.isSelected()) {
                //    menuSelectUnSelect(8);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(context, ChangePasswordActivity.class));
                    }
                }, 200);
            }

        });
        binding.tvSettings.setOnClickListener(view -> {
            binding.header.ivInfo.setVisibility(View.GONE);
            binding.header.linWallet.setVisibility(View.GONE);
            isHome = false;
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (!binding.tvSettings.isSelected()) {
                menuSelectUnSelect(9);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.header.toolbarTitle.setText(getString(R.string.settings));
                        // ivSorting.setVisibility(View.GONE);
                        // ivFilter.setVisibility(View.GONE);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameContent, SettingsFragment.newInstance())
                                .commit();
                    }
                }, 200);
            }

        });
        binding.tvFaq.setOnClickListener(view -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }

            if (!binding.tvFaq.isSelected()) {
                //  menuSelectUnSelect(10);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // startActivity(new Intent(context, FAQActivity.class));
                        Intent intent = new Intent(context, FAQActivity.class);
                        intent.putExtra(FAQActivity.NAME, getString(R.string.faqs));
                        intent.putExtra(FAQActivity.LINK, getString(R.string.faqsLink));
                        startActivity(intent);
                    }
                }, 200);
            }

        });
        binding.tvShareApp.setOnClickListener(view -> {
            binding.drawer.closeDrawer(GravityCompat.START);
            if (binding.linearSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearSubMenu.setVisibility(View.GONE);
                binding.tvOrganize.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearJSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearJSubMenu.setVisibility(View.GONE);
                binding.tvJTckt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }
            if (binding.linearAboutSubMenu.getVisibility() == View.VISIBLE) {
                binding.linearAboutSubMenu.setVisibility(View.GONE);
                binding.tvAboutUs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_white_right_arrow, 0);
            }


            if (!binding.tvShareApp.isSelected()) {
                //   menuSelectUnSelect(11);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Intent intent = new Intent(context, ReferralActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                           /* Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            //String shareMessage= "\nLet me recommend you this application\n\n";
                            String shareMessage = "Hey! I'm inviting you to CBit.\nUse my referral code "+"'"+sessionUtil.getReferralcode()+"' "+"to register and get Benefits.\n Download Now!\n";
//                            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                            shareMessage = shareMessage + "http://cbitoriginal.com/cbit.apk" + "&coupon=" + sessionUtil.getReferralcode();

                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                            startActivity(Intent.createChooser(shareIntent, "choose one"));*/
                        } catch (Exception e) {
                            //e.toString();
                        }
                    }
                }, 200);
            }

        });


    }

    @Subscribe()
    public void onSocketConnectionEvent(final SocketConnectionEvent socketConnectionEvent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintLog.e(TAG, socketConnectionEvent.getMessage());
                if (CBit.getSocketUtils().getmSocket().connected()) {
                    if (customDialog != null)
                        customDialog.dismissProgress(context);
                    CBit.getSocketUtils().loginEmit(sessionUtil.getId());
                } else if (socketConnectionEvent.getMessage().equals("disconnected")) {
                   /* if(Utils.isNetworkAvailable(context)){
                        Utils.showToast(context,"Network Availalble");
                    }else{
                        Utils.showToast(context,"No Network Availalble");
                    }*/
                } else if (socketConnectionEvent.getMessage().equals("Error connecting")) {
                    if (customDialog == null) {
                        customDialog = new CustomDialog();
                    }
                    if (!customDialog.progressDialog.isShowing())
                        customDialog.displayProgress(context, getString(R.string.connecting));
                } else if (socketConnectionEvent.getMessage().equals("On Reconnecting")) {
                    if (customDialog == null) {
                        customDialog = new CustomDialog();
                    }
                    if (!customDialog.progressDialog.isShowing())
                        customDialog.displayProgress(context, getString(R.string.connecting));
                }/*else{
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    Intent intent = new Intent(context,LoginSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }*/
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateProfileEvent(UpdateProfileEvent updateProfileEvent) {
        Glide.with(context).load(sessionUtil.getPhoto()).apply(Utils.getUserAvatarRequestOptionHome()).into(binding.sideMenu.ivProfilePic);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent unAuthorizedEvent) {
        Utils.showToast(context, unAuthorizedEvent.getMessage());
        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(context, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateVersionEvent(UpdateVersionEvent vesrsion) {

        DatabaseHandler databaseHandler = new DatabaseHandler(context);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(context, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        CustomDialog customDialog = new CustomDialog();
        customDialog.showDialogOneButtonUpdate(HomeActivity.this, "Update Alert...!", vesrsion.getMessage(),
                "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Uri uri = Uri.parse("https://cbitoriginal.com/cbitoriginal.apk");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        // Utils.shareJoinCode(context, "");
                    }
                });
    }


    private void logoutAPI() {
        Call<ResponseBody> call = APIClient.getInstance().logout(sessionUtil.getToken(), sessionUtil.getId());
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                Gson gson = new Gson();
                CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                if (commonRes.getStatus() == Utils.StandardStatusCodes.SUCCESS) {
                    if (CBit.getSocketUtils().getmSocket().connected()) {
                        CBit.getSocketUtils().disConnect();
                    }
                    DatabaseHandler databaseHandler = new DatabaseHandler(context);
                    databaseHandler.deleteTable();
                    String fcmToken = sessionUtil.getFcmtoken();
                    sessionUtil.logOut();
                    sessionUtil.setFCMToken(fcmToken);
                    Intent intent = new Intent(context, LoginSignUpActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.showToast(context, commonRes.getMessage());
                }
            }

            @Override
            public void failure(String responseData) {

            }
        });
    }

    private void logOut() {
        CustomDialog customDialog = new CustomDialog();
        customDialog.showDialogTwoButton(context, getString(R.string.logout), "Are You Sure to want Logout?",
                getString(R.string.logout), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logoutAPI();
                    }
                }, null);
    }

    private void menuSelectUnSelect(int pos) {
//        for (int i = 0; i < menuViews.size(); i++) {
//            if (i == pos) {
//                menuViews.get(i).setSelected(true);
//            } else {
//                menuViews.get(i).setSelected(false);
//            }
//        }
    }

    private void sortingPopup() {
        if (popupWindow != null) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                //   popupWindow.showAtLocation(binding.frameContent, Gravity.BOTTOM, 0, 0);
            }
        }


    }

    private void openFilterDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_filter);

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

        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        TextView tvApply = dialog.findViewById(R.id.tvApply);

        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        if (CBit.getSocketUtils().getmSocket().connected()) {
            CBit.getSocketUtils().disConnect();
        }
        super.onDestroy();
    }

    public void getSpinningMachineitemByDate() {
        JSONObject jsonObject = new JSONObject();
        try {
            byte[] data;
            jsonObject.put("dates", sessionUtil.getLastUpdatedDate());
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

        Call<ResponseBody> call = APIClient.getInstance().getSpinningMachineitemByDate(sessionUtil.getToken(), sessionUtil.getId(), request);
        NewApiCall newApiCall = new NewApiCall();
        newApiCall.makeApiCall(context, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                LogHelper.d(TAG, "success: " + responseData);
                try {

                    JSONObject jObj = new JSONObject(responseData);

                    if (jObj.getInt("statusCode") == 200) {
                        mapImages.clear();
                        JSONArray jImages = jObj.getJSONObject("content").getJSONArray("contest");
                        if (jImages != null && jImages.length() > 0) {
                            String SDCardPath = new File(String.valueOf(getFilesDir())).getAbsolutePath() + "/";
                            String rootPath = "";
                            File f = new File(SDCardPath);
                            if (!f.exists()) {
                                if (f.mkdirs()) {
                                    LogHelper.d("Created  ::: ", f.getAbsolutePath() + "");
                                } else {
                                    LogHelper.d("Oh Fuck not::: ", f.getAbsolutePath() + "");
                                }
                            }
                            for (int i = 0; i < jImages.length(); i++) {
                                rootPath = jImages.getJSONObject(i).getString("image");
                                LogHelper.d("TAG rootPath ::::>", rootPath);
                                String url = rootPath;
                                String name = rootPath.substring(rootPath.lastIndexOf("/"), rootPath.length());
                                String localUrl = SDCardPath + name;
                                LogHelper.d("TAG IMAGE PATH ::::>", localUrl);
                                File lf = new File(localUrl);
                                if (lf.exists()) {
                                    //deleteFolder(lf);
                                    lf.delete();

                                }
                                LogHelper.d("Total Download ::: ", totalDownloads + "");
                                totalDownloads++;
                                LogHelper.d("TAG My LOG :: ", "Total: " + totalDownloads + "  , Complete : " + completedDownload);
                                mapImages.put(url, localUrl);
                            }
                            if (mapImages.size() > 0)
                                manageImagesDownload(mapImages);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(String responseData) {
                PrintLog.e("TAG", "Failure");
            }
        });
    }

    private void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public void manageImagesDownload(LinkedHashMap<String, String> mapImages) {
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgress(0);
        mProgressDialog.setMax(100);

        int totalDownloads = mapImages.size();
        AtomicInteger completedDownload = new AtomicInteger(0);

        for (Map.Entry<String, String> entry : mapImages.entrySet()) {
            String url = entry.getKey().replace(" ", "%20");
            String path = entry.getValue();

            PRDownloader.download(url, new File(path).getParent(), new File(path).getName())
                    .build()
                    .setOnStartOrResumeListener(() -> Log.d("Download", "Started: " + url))
                    .setOnPauseListener(() -> Log.d("Download", "Paused: " + url))
                    .setOnCancelListener(() -> Log.d("Download", "Cancelled: " + url))
                    .setOnProgressListener(progress -> {
                        int percent = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        mProgressDialog.setProgress(percent);
                    })
                    .start(new OnDownloadListener() {
                        @Override
                        public void onDownloadComplete() {
                            int done = completedDownload.incrementAndGet();
                            Log.i("Download", "Completed: " + url);
                            if (done == totalDownloads) {
                                sessionUtil.setLastUpdatedDate(Utils.getTodayDate());
                                mProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onError(com.downloader.Error error) {
                            completedDownload.incrementAndGet();
                            Log.e("Download", "Failed: " + url + " -> " + error);
                            sessionUtil.setLastUpdatedDate("");
                            if (completedDownload.get() == totalDownloads) {
                                mProgressDialog.dismiss();
                            }
                        }


                    });
        }
    }

   /* public void manageImagesDownload(LinkedHashMap<String, String> map) {
        Set<String> keys = map.keySet();
        int i = 0;
        for (String url : keys) {
            LogHelper.d("Tag", "manage Image Download==>" + url);
            LogHelper.d("Tag", "manage Image Download Path ==>" + map.get(url));

            final DownloadRequest downloadRequest1 = new DownloadRequest(Uri.parse(url.replace(" ", "%20")))
                    .setDestinationURI(Uri.parse(map.get(url))).setPriority(DownloadRequest.Priority.HIGH)
                    .setRetryPolicy(retryPolicy)
                    .setDownloadContext(url)
                    .setDeleteDestinationFileOnFailure(false)
                    .setStatusListener(myDownloadStatusListener);
            downloadManager.add(downloadRequest1);
            mProgressDialog.show();
        }
    }*/

//    class MyDownloadDownloadStatusListenerV1 implements DownloadStatusListenerV1 {
//
//
//        int tempCompleteDownload = 0;
//
//        @Override
//        public void onDownloadComplete(DownloadRequest request) {
//            if (totalDownloads == completedDownload) {
//                sessionUtil.setLastUpdatedDate(Utils.getTodayDate());
//                mProgressDialog.dismiss();
//            }
//
//            Log.i("Download Complete  :: ", " Total Download :-> " + totalDownloads + "  , Complete :-> " + completedDownload + " ,   Progregress :-> ");
//        }
//
//        @Override
//        public void onDownloadFailed(DownloadRequest request, int errorCode, String errorMessage) {
//
//            //Log.i("Total Download ::: ", totalDownloads + "");
//            completedDownload++;
//            sessionUtil.setLastUpdatedDate("");
//            mProgressDialog.dismiss();
//            LogHelper.d("Download Failed  :: ", " Total Download :-> " + totalDownloads + "  , Complete :-> " + completedDownload + " ,   Progregress :-> ");
//        }
//
//        @Override
//        public void onProgress(DownloadRequest request, long totalBytes, long downloadedBytes, int progress) {
//            int id = request.getDownloadId();
//            if (totalBytes == downloadedBytes) {
//                int per = (completedDownload * 100) / totalDownloads;
//                mProgressDialog.setCancelable(false);
//                mProgressDialog.setIndeterminate(false);
//                mProgressDialog.setMax(100);
//                mProgressDialog.setProgress(per);
//                completedDownload++;
//                LogHelper.d("Total Download ::: ", totalDownloads + "");
//            }
//        }
//    }

    public Dialog dialog;

}
