package ott.spices.network;

import android.view.MotionEvent;

import ott.spices.databinding.ItemReelsBinding;
import ott.spices.models.ReelsModel;

public interface OnReelsVideoAdapterListner {

    void onItemClick(ItemReelsBinding reelsBinding, int pos, int type);

    void onOpenClick(ItemReelsBinding reelsBinding, int pos);

    void onShareClick(ItemReelsBinding reelsBinding, int pos);

    void onDoubleClick(ReelsModel model, MotionEvent event, ItemReelsBinding binding);

    void onClickLike(ItemReelsBinding reelsBinding, String likeStatus, int pos);

}
