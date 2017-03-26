package ng.com.calabaryellowpages.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ng.com.calabaryellowpages.Model.Review;
import ng.com.calabaryellowpages.R;

/**
 * Created by SMILECS on 3/11/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{
    ArrayList<Review> model;

    public ReviewAdapter(ArrayList<Review> model){
        this.model = model;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title, comment;
        RatingBar ratingBar;
        public ViewHolder(View itemView) {
            super(itemView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar2);
            title = (TextView) itemView.findViewById(R.id.textView7);
            comment = (TextView) itemView.findViewById(R.id.textView8);
        }
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review data = model.get(position);
        holder.comment.setText(data.getComment());
        holder.ratingBar.setRating(data.getScore());
        holder.title.setText(data.getName());
    }

    @Override
    public int getItemCount() {
        return model.size();
    }
}
