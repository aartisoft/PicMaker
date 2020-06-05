package com.yd.photoeditor.actions;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yd.photoeditor.R;
import com.yd.photoeditor.adapter.CustomMenuAdapter;
import com.yd.photoeditor.database.table.ItemPackageTable;
import com.yd.photoeditor.listener.OnBottomMenuItemClickListener;
import com.yd.photoeditor.listener.OnInstallStoreItemListener;
import com.yd.photoeditor.model.ItemInfo;
import com.yd.photoeditor.model.ItemPackageInfo;
import com.yd.photoeditor.ui.activity.ImageProcessingActivity;
import com.yd.photoeditor.utils.DialogUtils;
import com.yd.photoeditor.utils.TempDataContainer;
import com.yd.photoeditor.utils.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PackageAction extends BaseAction implements OnBottomMenuItemClickListener, OnInstallStoreItemListener {
    protected static final int DEFAULT_CROP_SELECTED_ITEM_INDEX = 3;
    protected View mBackButton;
    protected String mCurrentPackageFolder;
    protected long mCurrentPackageId = 0;
    protected int mCurrentPosition = 1;
    protected RecyclerView mRecycler;
    protected CustomMenuAdapter mMenuAdapter;
    protected List<ItemInfo> mMenuItems;
    private final String mPackageType;
    protected Map<Long, Integer> mSelectedItemIndexes;

    public void onStartDownloading(ItemPackageInfo itemPackageInfo) {
    }

    /* access modifiers changed from: protected */
    public abstract void selectNormalItem(int i);

    public PackageAction(ImageProcessingActivity imageProcessingActivity, String str) {
        super(imageProcessingActivity);
        createMenuList();
        this.mPackageType = str;
        String str2 = this.mPackageType;
        if (str2 != null && ItemPackageTable.CROP_TYPE.equalsIgnoreCase(str2)) {
            this.mCurrentPosition = 3;
        }
    }

    public static void setAbsoluteBackgroundPath(ItemPackageInfo itemPackageInfo) {
        itemPackageInfo.setShowingType(1);
        String str = Utils.CROP_FOLDER;
        if ("frame".equalsIgnoreCase(itemPackageInfo.getType())) {
            str = Utils.FRAME_FOLDER;
        } else if (ItemPackageTable.FILTER_TYPE.equalsIgnoreCase(itemPackageInfo.getType())) {
            str = Utils.FILTER_FOLDER;
        } else if (ItemPackageTable.CROP_TYPE.equalsIgnoreCase(itemPackageInfo.getType())) {
            str = Utils.CROP_FOLDER;
        } else if ("background".equalsIgnoreCase(itemPackageInfo.getType())) {
            str = Utils.BACKGROUND_FOLDER;
        } else if ("sticker".equalsIgnoreCase(itemPackageInfo.getType())) {
            str = Utils.STICKER_FOLDER;
        }
        itemPackageInfo.setThumbnail(str.concat("/").concat(itemPackageInfo.getFolder()).concat("/").concat(itemPackageInfo.getThumbnail()));
        if (itemPackageInfo.getSelectedThumbnail() != null && itemPackageInfo.getSelectedThumbnail().length() > 0) {
            itemPackageInfo.setSelectedThumbnail(str.concat("/").concat(itemPackageInfo.getFolder()).concat("/").concat(itemPackageInfo.getSelectedThumbnail()));
        }
    }

    public void onFinishInstalling(ItemPackageInfo itemPackageInfo, boolean z) {
        if (this.mPackageType != null && itemPackageInfo.getType().equalsIgnoreCase(this.mPackageType)) {
            boolean z2 = true;
            itemPackageInfo.setShowingType(1);
            String str = Utils.CROP_FOLDER;
            if ("frame".equalsIgnoreCase(this.mPackageType)) {
                str = Utils.FRAME_FOLDER;
            } else if (ItemPackageTable.FILTER_TYPE.equalsIgnoreCase(this.mPackageType)) {
                str = Utils.FILTER_FOLDER;
            } else if (ItemPackageTable.CROP_TYPE.equalsIgnoreCase(this.mPackageType)) {
                str = Utils.CROP_FOLDER;
            } else if ("background".equalsIgnoreCase(this.mPackageType)) {
                str = Utils.BACKGROUND_FOLDER;
            } else if ("sticker".equalsIgnoreCase(this.mPackageType)) {
                str = Utils.STICKER_FOLDER;
            }
            itemPackageInfo.setThumbnail(str.concat("/").concat(itemPackageInfo.getFolder()).concat("/").concat(itemPackageInfo.getThumbnail()));
            if (itemPackageInfo.getSelectedThumbnail() != null && itemPackageInfo.getSelectedThumbnail().length() > 0) {
                itemPackageInfo.setSelectedThumbnail(str.concat("/").concat(itemPackageInfo.getFolder()).concat("/").concat(itemPackageInfo.getSelectedThumbnail()));
            }
            Iterator<ItemInfo> it = this.mMenuItems.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z2 = false;
                    break;
                }
                ItemInfo next = it.next();
                if ((next instanceof ItemPackageInfo) && ((ItemPackageInfo) next).getIdString().equals(itemPackageInfo.getIdString())) {
                    next.setTitle(itemPackageInfo.getTitle());
                    break;
                }
            }
            if (!z2) {
                int size = this.mMenuItems.size();
                int i = size - 1;
                if (this.mCurrentPosition == i) {
                    this.mCurrentPosition = size;
                }
                this.mMenuItems.add(i, itemPackageInfo);
            }
            this.mMenuAdapter.notifyDataSetChanged();
        }
    }

    public void onEditorItemClick(int i, ItemInfo itemInfo) {
        Log.d("xxl", "mCurrentPosition " + mCurrentPosition + " i: " + i);
        if (mCurrentPosition != i && mMenuItems != null &&mMenuItems.size() > i) {
            mMenuItems.get(mCurrentPosition).setSelected(false);
            mMenuItems.get(i).setSelected(true);
            mMenuAdapter.notifyDataSetChanged();
            selectItem(i);
            onClicked();
        }
    }

    public void onDeleteButtonClick(int i, final ItemInfo itemInfo) {
        DialogUtils.showCoolConfirmDialog(this.mActivity, R.string.photo_editor_app_name, R.string.photo_editor_confirm_uninstall, new DialogUtils.ConfirmDialogOnClickListener() {
            public void onOKButtonOnClick() {
                PackageAction.this.mMenuItems.remove(itemInfo);
                PackageAction.this.mMenuAdapter.setShaking(false);
            }

            public void onCancelButtonOnClick() {
                PackageAction.this.mMenuAdapter.setShaking(false);
            }
        });
    }

    public void onMenuItemLongClick(int i, ItemInfo itemInfo) {
        this.mMenuAdapter.setShaking(true);
    }

    public void onInit() {
        super.onInit();
    }

    public void attach() {
        super.attach();

        TempDataContainer.getInstance().getOnInstallStoreItemListeners().add(this);
        if (mMenuItems != null && mMenuItems.get(mCurrentPosition).getShowingType() != 2) {
            selectItem(mCurrentPosition);
        }

        mActivity.getAdapter().setListener(this);
    }

    public void onDetach() {
        super.onDetach();
        TempDataContainer.getInstance().getOnInstallStoreItemListeners().remove(this);
        mActivity.getAdapter().setListener(null);
    }

    public void onActivityResume() {
        super.onActivityResume();
    }

    public void saveInstanceState(Bundle bundle) {
        super.saveInstanceState(bundle);
    }

    public void restoreInstanceState(Bundle bundle) {
        super.restoreInstanceState(bundle);
    }

    private void createMenuList() {
        this.mRecycler = mRootActionView.findViewById(R.id.bottomListView);
        if (this.mRecycler != null) {
            mMenuItems = new ArrayList();
            mMenuAdapter = new CustomMenuAdapter(this.mActivity, this.mMenuItems, true);
            mMenuAdapter.setListener(this);
            mRecycler.setHasFixedSize(false);
            mRecycler.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false));
            mRecycler.setAdapter(mMenuAdapter);
        }
        this.mBackButton = this.mRootActionView.findViewById(R.id.backButton);
        View view = this.mBackButton;
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    PackageAction.this.backNormalPackage();
                }
            });
        }
    }

    public void backNormalPackage() {

    }

    private void selectItem(int i) {
        selectNormalItem(i);
        mCurrentPosition = i;
    }
}
