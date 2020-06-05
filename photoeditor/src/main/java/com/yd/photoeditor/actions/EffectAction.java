package com.yd.photoeditor.actions;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yd.photoeditor.R;
import com.yd.photoeditor.config.ALog;
import com.yd.photoeditor.config.Constants;
import com.yd.photoeditor.database.table.ItemPackageTable;
import com.yd.photoeditor.imageprocessing.ImageProcessor;
import com.yd.photoeditor.imageprocessing.filter.ImageFilter;
import com.yd.photoeditor.listener.ApplyFilterListener;
import com.yd.photoeditor.model.FilterInfo;
import com.yd.photoeditor.model.ItemInfo;
import com.yd.photoeditor.task.ApplyFilterTask;
import com.yd.photoeditor.ui.activity.ImageProcessingActivity;
import com.yd.photoeditor.utils.PhotoUtils;

import java.util.ArrayList;

public class EffectAction extends PackageAction {
    private static final String TAG = EffectAction.class.getSimpleName();

    public String getActionName() {
        return "EffectAction";
    }

    public EffectAction(ImageProcessingActivity imageProcessingActivity) {
        super(imageProcessingActivity, ItemPackageTable.FILTER_TYPE);
    }

    public View inflateMenuView() {
        this.mRootActionView = LayoutInflater.from(mActivity).inflate(R.layout.photo_editor_action_effect, null);
        mCurrentPosition = 1;
        return this.mRootActionView;
    }

    public void attach() {
        mActivity.attachBottomRecycler(0);
        mMenuItems = mActivity.mEffectInfos;
        super.attach();
        mActivity.getNormalImageView().setVisibility(View.GONE);
        mActivity.getImageProcessingView().setVisibility(View.VISIBLE);
        mActivity.attachMaskView(null);
    }

    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
        ALog.d(TAG, "restoreInstanceState");
    }

    public void onActivityResume() {
        super.onActivityResume();
        ALog.d(TAG, "onActivityResume");
        mActivity.attachMaskView(null);
    }

    public void apply(final boolean done) {
        if (isAttached()) {
            new ApplyFilterTask(mActivity, new ApplyFilterListener() {
                public void onFinishFiltering() {
                    ((ItemInfo) mMenuItems.get(mCurrentPosition)).setSelected(false);
                    mCurrentPosition = 0;
                    mCurrentPackageId = 0;
                    mCurrentPackageFolder = null;
                    mActivity.applyFilter(new ImageFilter());
                    ((ItemInfo) mMenuItems.get(0)).setSelected(true);
                    mMenuAdapter.notifyDataSetChanged();
                    if (done) {
                        done();
                    }
                }

                public Bitmap applyFilter() {
                    try {
//                        mActivity.getImageProcessingView().setDrawingCacheEnabled(true);
//                        mActivity.getImageProcessingView().buildDrawingCache();
//                        Bitmap bitmap = mActivity.getImageProcessingView().getDrawingCache();

                        //return mActivity.getImageProcessingView().getImageProcessor().getCurrentBitmap();

                        Bitmap bitmap2 = ImageProcessor.getFiltratedBitmap(mActivity.getImage(), (((FilterInfo) mMenuItems.get(mCurrentPosition))).getImageFilter());
                        int width = bitmap2.getWidth();
                        int height = bitmap2.getHeight();
                        int i = (height > 4000 || width > 4000) ? 16 : (height > 3000 || width > 3000) ? 12 : (height > 2000 || width > 2000) ? 8 : (height > 1000 || width > 1000) ? 6 : 4;
                        Matrix matrix = new Matrix();
                        matrix.postScale(((float) (width / i)) / ((float) width), ((float) (height / i)) / ((float) height));
                        return Bitmap.createBitmap(bitmap2, 0, 0, width, height, matrix, false);
                    } catch (Exception e) {
                        Log.e("xxl" , "sasas");
                        return null;
                    }
                }
            }).execute();
        }
    }


    public void selectNormalItem(int i) {
        this.mActivity.applyFilter(((FilterInfo) this.mMenuItems.get(i)).getImageFilter());
    }

    /* access modifiers changed from: protected */
    public ArrayList<ItemInfo> loadNormalItems(int[] datas) {
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        ArrayList<FilterInfo> itemInfos = new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {
            int id = datas[i];
            FilterInfo info = new FilterInfo();
            info.setThumbnail(PhotoUtils.DRAWABLE_PREFIX + id);
            info.setSelected(false);
            info.setShowingType(0);
            itemInfos.add(info);
            info.setCmd(Constants.EFFECTS_CMDS[i]);
        }
        arrayList.addAll(itemInfos);
        return arrayList;
    }
}
