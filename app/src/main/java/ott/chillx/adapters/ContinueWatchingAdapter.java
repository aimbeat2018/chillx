package ott.chillx.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import ott.chillx.database.continueWatching.ContinueWatchingModel;
import ott.chillx.DetailsActivity;
import ott.chillx.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ott.chillx.utils.Constants;

public class ContinueWatchingAdapter extends RecyclerView.Adapter<ContinueWatchingAdapter.ContinueWatchingViewHolder> {

    private Context context;
    private List<ContinueWatchingModel> list = new ArrayList<>();

    public ContinueWatchingAdapter(Context context, List<ContinueWatchingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContinueWatchingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_continue_watching, parent, false);
        return new ContinueWatchingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueWatchingViewHolder holder, int position) {
        final ContinueWatchingModel model = list.get(position);
        if (model != null){
            holder.title.setText(model.getName());
           // holder.type.setText(model.getType());
            holder.progressBar.setProgress((int) model.getProgress());
            if(!model.getImgUrl().equals("")) {
                Picasso.get().load(model.getImgUrl()).placeholder(R.drawable.poster_placeholder).into(holder.posterIV);
            }
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra(Constants.CONTENT_ID, model.getContentId());
                    intent.putExtra(Constants.CONTENT_TITLE, model.getName());
                    intent.putExtra(Constants.IMAGE_URL, model.getImgUrl());
                    intent.putExtra(Constants.STREAM_URL, model.getStreamUrl());
                    intent.putExtra(Constants.SERVER_TYPE, model.getvType());
                    intent.putExtra(Constants.CATEGORY_TYPE, model.getType());
                    intent.putExtra(Constants.POSITION, model.getPosition());
                    intent.putExtra(Constants.IS_FROM_CONTINUE_WATCHING, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ContinueWatchingViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView type;
        private ImageView posterIV;
        private ProgressBar progressBar;
        private MaterialRippleLayout lyt_parent;

        public ContinueWatchingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.name);
          // type = itemView.findViewById(R.id.type);
            title.setSelected(true);
            posterIV = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progressBar);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
