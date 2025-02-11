package ott.chillx.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ott.chillx.DetailsActivity;
import ott.chillx.R;
import ott.chillx.models.CommonModels;
import ott.chillx.utils.ItemAnimation;

public class CommonGridAdapter extends RecyclerView.Adapter<CommonGridAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;


    public CommonGridAdapter(Context context, List<CommonModels> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public CommonGridAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CommonGridAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_image_albums, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(CommonGridAdapter.OriginalViewHolder holder, final int position) {
        final CommonModels obj = items.get(position);

        holder.qualityTv.setText(obj.getQuality());
        holder.releaseDateTv.setText(obj.getReleaseDate());
        holder.name.setText(obj.getTitle());

        Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder).into(holder.image);


        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  if (PreferenceUtils.isMandatoryLogin(ctx)){
                    if (PreferenceUtils.isLoggedIn(ctx)){

                    }else {
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                }else {
                    goToDetailsActivity(obj);
                }*/
                goToDetailsActivity(obj);
            }
        });
        setAnimation(holder.itemView, position);


        // holder.cat_name.setText(obj.getTitle());
        //  holder.duration.setText(obj.getMovieDuration());
        //holder.cat_type.setText(obj.getVideoType());
        // holder.description.setText(obj.getDescription());

/*

        holder.option.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(holder.option.getContext(), v);

                popup.inflate(R.menu.product_option_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                // Intent intent1 = new Intent(SellerProductListActivity.this, AddProductImageActivity.class);
                                //sepearet 3 images upload
                              */
/*  Intent intent1 = new Intent(ctx, UplaodProductImageActivity.class);
                                intent1.putExtra("productId", model.getProductId());
                                //  intent1.putExtra("productId", productid);
                                startActivity(intent1);*//*

                                return true;


                            default:
                                return false;
                        }
                    }
                });



                   */
/* popup.setGravity(Gravity.RIGHT);

                    try {
                        Field mFieldPopup=popup.getClass().getDeclaredField("mPopup");
                        mFieldPopup.setAccessible(true);
                        MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popup);
                        mPopup.setForceShowIcon(true);
                    } catch (Exception e) {

                    }*//*


                popup.show();

            }
        });
*/




/*

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.isMandatoryLogin(ctx)) {
                    if (PreferenceUtils.isLoggedIn(ctx)) {
                        goToDetailsActivity(obj);
                    } else {
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                } else {
                    goToDetailsActivity(obj);
                }
            }
        });
*/

/*

        holder.play_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.isMandatoryLogin(ctx)) {
                    if (PreferenceUtils.isLoggedIn(ctx)) {
                        goToDetailsActivity(obj);
                    } else {
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                } else {
                    goToDetailsActivity(obj);
                }
            }
        });
*/


    }


    private void goToDetailsActivity(CommonModels obj) {
        Intent intent = new Intent(ctx, DetailsActivity.class);
        intent.putExtra("vType", obj.getVideoType());
        intent.putExtra("id", obj.getId());
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();

    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // ProductItemLayoutBinding layoutBinding;

        public ImageView image, option;
        public TextView name, cat_name, qualityTv, releaseDateTv, duration, cat_type;
        public MaterialRippleLayout lyt_parent;

        public View view;
        PopupMenu popup;
        public CardView cardView;
        ImageView play_logo;
        TextView description;

        public OriginalViewHolder(View v) {
            super(v);
            view = v;
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            play_logo = v.findViewById(R.id.play_logo);
            cat_type = v.findViewById(R.id.cat_type);
            cat_name = v.findViewById(R.id.cat_name);
            duration = v.findViewById(R.id.duratipn);
            description = v.findViewById(R.id.description);
            option = v.findViewById(R.id.menu);

            lyt_parent = v.findViewById(R.id.lyt_parent);
            qualityTv = v.findViewById(R.id.quality_tv);
            releaseDateTv = v.findViewById(R.id.release_date_tv);
           /* cardView = v.findViewById(R.id.top_layout);
            cardView = v.findViewById(R.id.top_layout);*/
        }

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

}