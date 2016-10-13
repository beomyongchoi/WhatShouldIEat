package xyz.beomyong.whatshouldieat.common;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.oooobang.library.OBFragmentActivity;
import com.oooobang.library.OBLog;
import com.tsengvn.typekit.TypekitContextWrapper;

import xyz.beomyong.whatshouldieat.ui.MainActivity;

/**
 * Created by BeomyongChoi on 10/11/16
 */
public class BaseFragmentActivity extends OBFragmentActivity {

    /**
     * Toast.
     */
    protected Toast mToast = null;

    /**
     * Snackbar.
     */
    protected Snackbar mSnackbar = null;

    /**
     * Progress Dialog Show Count.
     */
    private int mProgressDialogShowCount = 0;

    /**
     * Progress Dialog를 보여주기 위한 Intent Filter, Broadcast Receiver.
     */
    private String mProgressDialogAction = null;

    /**
     * Progress Dialog.
     */
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
		 * 액티비티를 초기화 한다.
		 */

        //==================================================
        OBLog.v(OBLog.TAG, "==========> " + getClass().getSimpleName() + " onCreate <");
        //==================================================

        //Finish BroadcastReceiver 등록.
        registerFinishedReceiver();
        registerSubFinishedReceiver();

        //Show Dialog BroadcastReceiver 등록.
        registerShowDialogReceiver();

        //Gradient 사용 부분에 계단 현상이 일어나 부드럽게 처리하기 위함.
        getWindow().setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Screen On/Off BroadcastReceiver 등록.
        registerScreenOnReceiver();
        registerScreenOffReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void startActivity (Intent intent) {
        super.startActivity(intent);
    }
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//
//		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//	}

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Screen On/Off BroadcastReceiver 해지.
        unregisterScreenOnReceiver();
        unregisterScreenOffReceiver();
    }

    @Override
    protected void onDestroy() {
//        // 네트워크 리소스 해제.
//        if (DataHandler.getInstance() != null) {
//            DataHandler.getInstance().cancel();
//        }

        //Dialog Destroy.
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

        //Finish BroadcastReceiver 해제.
        unregisterFinishedReceiver();
        unregisterSubFinishedReceiver();

        //Show Dialog BroadcastReceiver 해제. 
        unregisterShowDialogReceiver();

        //Destroy.
        super.onDestroy();
    }

    /**
     * 종료 & 애니메이션.
     * @param enterAnimation
     * @param exitAnimation
     */
    protected void finish(int enterAnimation, int exitAnimation) {
        super.finish();
        overridePendingTransition(enterAnimation, exitAnimation);
    }

    /**
     * Screen On BroadcastReceiver 생성.
     */
    private BroadcastReceiver mScreenOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    /**
     * Screen On BroadcastReceiver 등록.
     */
    private void registerScreenOnReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenOnReceiver, filter);
    }

    /**
     * Screen On BroadcastReceiver 해제. 
     */
    private void unregisterScreenOnReceiver() {
        unregisterReceiver(mScreenOnReceiver);
    }

    /**
     * Screen Off BroadcastReceiver 생성.
     */
    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    };

    /**
     * Screen Off BroadcastReceiver 등록.
     */
    private void registerScreenOffReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenOffReceiver, filter);
    }

    /**
     * Screen Off BroadcastReceiver 해제. 
     */
    private void unregisterScreenOffReceiver() {
        unregisterReceiver(mScreenOffReceiver);
    }

    /**
     * BroadcastReceiver 생성.
     */
    private BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BaseFragmentActivity.this.finish();
        }
    };

    /**
     * BroadcastReceiver 등록.
     */
    private void registerFinishedReceiver() {
        IntentFilter intentFilter = new IntentFilter(BYConstants.ACTION_FINISH);
        registerReceiver(finishedReceiver, intentFilter);
    }

    /**
     * BroadcastReceiver 해제.
     */
    private void unregisterFinishedReceiver() {
        unregisterReceiver(finishedReceiver);
    }

    /**
     * BroadcastReceiver 생성.
     */
    private BroadcastReceiver subFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!MainActivity.class.isInstance(this)) {
                BaseFragmentActivity.this.finish();
            }
        }
    };

    /**
     * BroadcastReceiver 등록.
     */
    private void registerSubFinishedReceiver() {
        IntentFilter intentFilter = new IntentFilter(BYConstants.ACTION_SUB_FINISH);
        registerReceiver(subFinishedReceiver, intentFilter);
    }

    /**
     * BroadcastReceiver 해제.
     */
    private void unregisterSubFinishedReceiver() {
        unregisterReceiver(subFinishedReceiver);
    }

    /**
     * ProgressDialog BroadcastReceiver 생성.
     */
    private BroadcastReceiver mProgressDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isActivation = intent.getBooleanExtra(
                    BYConstants.INTENT_DATA, false);

            if (isActivation) {
                boolean isTextResource = intent.getBooleanExtra(
                        BYConstants.INTENT_DATA2, false);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
                if (isTextResource) {
                    String message = intent.getStringExtra(
                            BYConstants.INTENT_DATA3);
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage(message);
                    mProgressDialog.show();
                } else {
                    int resourceId = intent.getIntExtra(
                            BYConstants.INTENT_DATA3, 0);
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setCancelable(false);
                    mProgressDialog.setMessage(getString(resourceId));
                    mProgressDialog.show();
                }
            } else if (mProgressDialogShowCount <= 0 &&
                    mProgressDialog != null &&
                    mProgressDialog.isShowing()) {
                mProgressDialogShowCount = 0;
                mProgressDialog.dismiss();
            }
        }
    };

    /**
     * 커스텀 다이얼 로그의 BroadcastReceiver 등록.
     */
    private void registerShowDialogReceiver() {
        mProgressDialogAction = getClass().getName() + ".action.CUSTOM_DIALOG_SHOW";
        IntentFilter filter = new IntentFilter(mProgressDialogAction);
        registerReceiver(mProgressDialogReceiver, filter);
    }

    /**
     * 기본 다이얼 로그의 BroadcastReceiver 해제.
     */
    private void unregisterShowDialogReceiver() {
        unregisterReceiver(mProgressDialogReceiver);
    }

    /**
     * ProgressDialog set message.
     * @param resourceId
     */
    protected void setProgressDialogMessage(int resourceId) {
        if (isCustomProgressDialogShow()) {
            mProgressDialog.setMessage(getString(resourceId));
        }
    }

    /**
     * ProgressDialog set message.
     * @param text
     */
    protected void setProgressDialogMessage(String text) {
        if (isCustomProgressDialogShow()) {
            mProgressDialog.setMessage(text);
        }
    }

    /**
     *
     */
    protected boolean isCustomProgressDialogShow() {
        return mProgressDialogShowCount > 0 ? true : false;
    }

    /**
     * Custom Progress Dialog Show.
     * @param resourceId
     */
    protected void showCustomProgressDialog(int resourceId) {
        mProgressDialogShowCount++;
        Intent showProgressDialogIntent = new Intent(mProgressDialogAction);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA,
                true);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA2,
                false);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA3,
                resourceId);
        sendBroadcast(showProgressDialogIntent);
    }

    /**
     * Custom Progress Dialog Show.
     * @param text
     */
    protected void showCustomProgressDialog(String text) {
        mProgressDialogShowCount++;
        Intent showProgressDialogIntent = new Intent(mProgressDialogAction);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA,
                true);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA2,
                true);
        showProgressDialogIntent.putExtra(BYConstants.INTENT_DATA3,
                text);
        sendBroadcast(showProgressDialogIntent);
    }

    /**
     * Custom Progress Dialog Dissmiss.
     */
    protected void dismissCustomProgressDialog() {
        mProgressDialogShowCount--;
        Intent dismissProgressDialogIntent = new Intent(mProgressDialogAction);
        dismissProgressDialogIntent.putExtra(BYConstants.INTENT_DATA,
                false);
        sendBroadcast(dismissProgressDialogIntent);
    }

    /**
     * 이전.
     */
    protected void goBack() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /**
     * 메인.
     */
    protected void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

//    /**
//     * Show Snackbar.
//     * @param resourceId
//     * @param duration
//     */
//    public void showSnackbar(int resourceId, int duration) {
//        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
//        if (coordinatorLayout != null) {
//            if (mSnackbar != null) {
//                mSnackbar.dismiss();
//            }
//            mSnackbar = Snackbar.make(
//                    coordinatorLayout,
//                    resourceId,
//                    duration);
//            mSnackbar.show();
//        } else {
//            showToast(resourceId, duration);
//        }
//    }
//
//    /**
//     * Show Snackbar.
//     *
//     * @param message
//     * @param duration
//     */
//    public void showSnackbar(String message, int duration) {
//        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_coordinator);
//        if (coordinatorLayout != null) {
//            if (mSnackbar != null) {
//                mSnackbar.dismiss();
//            }
//            mSnackbar = Snackbar.make(
//                    coordinatorLayout,
//                    message,
//                    duration);
//            mSnackbar.show();
//        } else {
//            showToast(message, duration);
//        }
//    }

    /**
     * Show Toast.
     * @param resourceId
     * @param duration
     */
    public void showToast(int resourceId, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                resourceId,
                duration);
        mToast.show();
    }

    /**
     * Show Toast.
     * @param message
     * @param duration
     */
    public void showToast(String message, int duration) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this,
                message,
                duration);
        mToast.show();
    }

    /**
     * Cancel Snackbar/Toast.
     */
    public void cancelSnackbarToast() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }
        if (mToast != null) {
            mToast.cancel();
        }
    }
    
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }

}
